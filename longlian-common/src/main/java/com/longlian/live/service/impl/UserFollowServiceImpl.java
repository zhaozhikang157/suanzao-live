package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.UserFollowMapper;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.CourseType;
import com.longlian.model.UserFollow;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.rmi.MarshalledObject;
import java.text.DateFormat;
import java.util.*;


/**
 * Created by pangchao on 2017/2/12.
 */
@Service("userFollowService")
public class UserFollowServiceImpl implements UserFollowService {
    private static Logger log = LoggerFactory.getLogger(UserFollowServiceImpl.class);
    @Autowired
    UserFollowMapper userFollowMapper;
    @Autowired
    RedisUtil redisUtil;
     @Autowired
    WechatOfficialService wechatOfficialService;
    
    @Override
    public ActResultDto getCountUserFollow(Long roomId, Integer offSet,Date createTime) {
        ActResultDto ac = new ActResultDto();
        Map map = new HashMap();
        List<Map> addUserFollowList = null;
        if (offSet == null || offSet==0) {
            addUserFollowList = userFollowMapper.getUserFollowListByApp(roomId);//新增关注
            if (addUserFollowList.size() > 0) {
                for (Map m : addUserFollowList) {
                    redisUtil.lpush(RedisKey.ll_userfollow_readed_sync_key, m.get("id").toString());//去设置关注已阅
                }
            }
        }
        String time = "";
        if (createTime!=null){
            time = DateUtil.format(createTime,"yyyy-MM-dd HH:mm");
            addUserFollowList =   userFollowMapper.getUserFollowList(roomId, time);
        }
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offSet);
        List<Map> historyUserFollowList = userFollowMapper.getUserFollowListByAppPage(roomId, dg,time);//历史关注分页
        map.put("addUserFollowList", addUserFollowList);
        map.put("addUserFollowListNum",addUserFollowList==null?0:addUserFollowList.size());
        map.put("historyUserFollowListNum",dg.getTotalCount());
        ac.setData(map);
        if(historyUserFollowList.size()==0){
            ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            ac.setCode(ReturnMessageType.NO_DATA.getCode());
            return ac;
        }
        ac.setExt(historyUserFollowList);
        return ac;
    }

    /**
     * 取直播间关注人数
     *
     * @param liveRoomId
     * @return
     */
    @Override
    public long getCountByRoomId(Long liveRoomId) {
        String praiseKey = RedisKey.ll_user_follow_key + liveRoomId;
        if (!redisUtil.exists(praiseKey)) {
            //加载关注数据到内存
            getUserFollow(liveRoomId);
        }

        return redisUtil.zcard(praiseKey);
        //改成redis加载
        //return userFollowMapper.getCountByRoomId(liveRoomId);
    }

    @Override
    public List<Map> followLiveRoom(long appId, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setCurrentPage(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> result = userFollowMapper.followLiveRoomPage(appId, dg);

        int index = -1;
        for ( int i = 0 ;i < result.size() ;i++) {
            Map m = result.get(i);
           Long roomId =  (Long)m.get("id");
           Double dou = redisUtil.zscore(RedisKey.ll_user_follow_key + roomId, String.valueOf(appId));
           //看在里面没有
           if (dou == null) {
               index = i;
           }
           long count = this.getCountByRoomId(roomId);
           m.put("count",count);
        }
        if (index > -1 ) {
            result.remove(index);
        }
        return result;
    }

    /**
     * 获取我关注得直播间 new
     * 获取该用户所有的已关注得和关注过的人
     */
    public List<Map> followLiveRoomNew(long appId, Integer pageNum, Integer pageSize){
        DataGridPage dg = new DataGridPage();
        dg.setCurrentPage(pageNum != null ? pageNum : 1);
        dg.setPageSize(pageSize != null ? pageSize : 20);
        List<Map> ls = userFollowMapper.followLiveRoomNewPage(appId,dg);
        return ls;
    }

    /**
     * 点赞
     *
     * @param id
     * @param liveRoomId
     * @return
     */
    @Override
    public ActResultDto follow(long id, Long liveRoomId,String  thirdOpenId,String  thirdWechatAppId) {
        ActResultDto resultDto = new ActResultDto();
        String praiseKey = RedisKey.ll_user_follow_key + liveRoomId;
        if (!redisUtil.exists(praiseKey)) {
            getUserFollow(liveRoomId);
        }

        Long appId = userFollowMapper.getAppIdByLiveRoomId(liveRoomId);
        if (appId != null) {
            if (appId == id) {
                resultDto.setCode(ReturnMessageType.LIVE_ROOM_CANNOT_FOLLOW_OWNER.getCode());
                resultDto.setMessage(ReturnMessageType.LIVE_ROOM_CANNOT_FOLLOW_OWNER.getMessage());
                return resultDto;
            }
            //Double score = redisUtil.zscore(praiseKey, String.valueOf(id));
            long result =  redisUtil.zadd(praiseKey, String.valueOf(id), new Date().getTime());
            //添加成功
            if (result == 1) {
                UserFollow userFollow = new UserFollow();
                userFollow.setStatus(0);
                userFollow.setAppId(id);
                userFollow.setCreateTime(new Date());
                userFollow.setRoomId(liveRoomId);
                userFollow.setThirdOpenId(thirdOpenId);
                userFollow.setThirdWechatAppId(thirdWechatAppId);

                redisUtil.lpush(RedisKey.ll_user_follow_wait2db, JsonUtil.toJson(userFollow));
                //微信客服消息
                wechatOfficialService.getSendWechatCustomerMessageByFollow(userFollow); 
                resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                return resultDto;
            }
        }
      
        
        resultDto.setCode(ReturnMessageType.LIVE_ROOM_ALREADY_FOLLOW.getCode());
        resultDto.setMessage(ReturnMessageType.LIVE_ROOM_ALREADY_FOLLOW.getMessage());
        return resultDto;
    }
    /**
     * 点赞
     *
     */

    public ActResultDto follow(long id, Long liveRoomId) {
        return  follow(id,liveRoomId,"","");
    }

    /**
     * 取消点赞
     *
     * @param id
     * @param liveRoomId
     * @return
     */
    @Override
    public ActResultDto cancelfollow(long id, Long liveRoomId) {
        ActResultDto resultDto = new ActResultDto();
        redisUtil.zrem(RedisKey.ll_user_follow_key + liveRoomId, String.valueOf(id));
        UserFollow userFollow = new UserFollow();
        userFollow.setStatus(1);
        userFollow.setAppId(id);
        userFollow.setCreateTime(new Date());
        userFollow.setRoomId(liveRoomId);
        redisUtil.lpush(RedisKey.ll_user_follow_wait2db, JsonUtil.toJson(userFollow));
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 从数据库中获取点赞数据放在到redis中
     */
    public void getUserFollow(Long liveRoomId) {
        if(liveRoomId!=null){
            String key = RedisKey.ll_user_follow_key + liveRoomId;
            List<UserFollow> list = userFollowMapper.selectUserFollowByRoomId(liveRoomId);
            if (list.size() > 0) {
                for (UserFollow userFollow : list) {
                    redisUtil.zadd(key, String.valueOf(userFollow.getAppId()), userFollow.getCreateTime().getTime());
                }
            }
        }
    }


    /**
     * 判断人员是否关注过没有
     *
     * @param liveRoomId
     * @param appId
     * @return
     */
    public boolean isFollowRoom(Long liveRoomId, long appId) {
        String praiseKey = RedisKey.ll_user_follow_key + liveRoomId;
        if (!redisUtil.exists(praiseKey)) {
            getUserFollow(liveRoomId);
        }

        Double score = redisUtil.zscore(praiseKey, String.valueOf(appId));
        if (score != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据room查出所有的关注人
     *
     * @param liveRoomId
     * @return
     */
    @Override
    public Set<String> getFollowUser(Long liveRoomId) {
        String praiseKey = RedisKey.ll_user_follow_key + liveRoomId;
        if (!redisUtil.exists(praiseKey)) {
            getUserFollow(liveRoomId);
        }
        return redisUtil.zrange(praiseKey, 0, -1);
    }

    /**
     * 获取用户openid by 直播间ID
     *
     * @param liveRoomId
     * @return
     */
    @Override
    public List<Map> getOpenIdByLiveRoomId(long liveRoomId) {
        return userFollowMapper.getOpenIdByLiveRoomId(liveRoomId);
    }

}
