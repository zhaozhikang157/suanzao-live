package com.longlian.live.service.impl;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.UserDistributionMapper;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.UserDistributionService;
import com.longlian.model.UserDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22.
 */
@Service("userDistributionService")
public class UserDistributionServiceImpl implements UserDistributionService {
    @Autowired
    UserDistributionMapper userDistributionMapper;

    @Autowired
    CourseService courseService;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 新增分销人记录 课程
     * @param courseId 课程ID
     * @param appId 系统当前登录ID
     * @param invitationAppId  邀请人ID
     */
    @Override
    public void addUserDistributionByCourseId(long courseId, long appId, long invitationAppId) {
        if(appId != invitationAppId ){//邀请人不能是自己
            long roomId = courseService.getRoomIdById(courseId);      //查询房间
            //判断是否此用户使用已经被发展了
            if(!isExistByRoomId(roomId, appId)) insert(roomId,  appId ,  invitationAppId);
        }
    }

    /**
     * 新增分销人记录  直播间
     * @param roomId 房间Id
     * @param appId 系统当前登录ID
     * @param invitationAppId 邀请人ID
     */
    @Override
    public void addUserDistributionByRoomId(long roomId, long appId, long invitationAppId) {
        if(appId != invitationAppId ){//邀请人不能是自己
            //判断是否此用户使用已经被发展了
            if(!isExistByRoomId(roomId, appId)){
                insert(roomId,  appId ,  invitationAppId);
            }
        }

    }

    /**
     * 新增
     * @param roomId
     * @param appId
     * @param invitationAppId
     */
    public  void insert( long roomId, long appId , long invitationAppId){
        if( appId > 0  && invitationAppId> 0 && roomId > 0){
            UserDistribution userDistribution  = new  UserDistribution();
            userDistribution.setAppId(appId);
            userDistribution.setRoomId(roomId);
            userDistribution.setCreateTime(new Date());
            userDistribution.setInvitationUserId(invitationAppId);
            userDistributionMapper.insert(userDistribution);
            //存入缓存
            redisUtil.hset(RedisKey.ll_live_user_distribution_record, userDistribution.getRoomId() + "_" + userDistribution.getAppId(), userDistribution.getInvitationUserId() + "");
        }
    }
    /**
     * 判断是否存在 根据直播间Id
     * @param roomId
     * @param appId
     * @return
     */
    public  boolean isExistByRoomId( long roomId, long appId){
        if(!Utility.isNullorEmpty( getInvitationAppId( roomId,  appId))) return  true;
        return false;
    }

    /**
     * 获取分销分ID
     * @param roomId
     * @param appId
     * @return
     */
    @Override
    public  String getInvitationAppId(long roomId, long appId){
        if(!redisUtil.exists(RedisKey.ll_live_user_distribution_record)){
            getAllListSaveRedis();
        }
        String invitationAppId  = redisUtil.hget(RedisKey.ll_live_user_distribution_record, roomId + "_" + appId);
        return  invitationAppId;
    }

    /**
     * 获取所有数据保存到redis中
     */
    public void getAllListSaveRedis(){
        List<UserDistribution> list = userDistributionMapper.getAllList();
        for (UserDistribution temp : list ){
            redisUtil.hset(RedisKey.ll_live_user_distribution_record, temp.getRoomId() + "_" + temp.getAppId(), temp.getInvitationUserId() + "");
        }
    }

}
