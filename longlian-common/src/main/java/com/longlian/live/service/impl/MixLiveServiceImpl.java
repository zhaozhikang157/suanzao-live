package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.newdao.LiveConnectMapper;
import com.longlian.live.newdao.LiveConnectRequestMapper;
import com.longlian.live.service.*;
import com.longlian.model.Course;
import com.longlian.model.LiveConnect;
import com.longlian.model.LiveConnectRequest;
import com.longlian.token.AppUserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by liuhan on 2018-03-25.
 */
@Service("mixLiveService")
public class MixLiveServiceImpl implements MixLiveService {
    private static Logger log = LoggerFactory.getLogger(MixLiveServiceImpl.class);
    private org.slf4j.Logger logg= org.slf4j.LoggerFactory.getLogger(CourseBaseServiceImpl.class);
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JoinCourseRecordService joinCourseRecordService;
    @Autowired
    private StudyRecordService studyRecordService;
    @Autowired
    private LiveConnectMapper liveConnectMapper;
    @Autowired
    private LiveConnectRequestMapper liveConnectRequestMapper;
    @Autowired
    RedisLock redisLock;


    /**
     * 取第一页数据
     * @return
     */
    private static String[] getFirstPageUsers(List<String> list  ) {
        return list.get(0).split(",");
    }


    public Set<String> getAllUses(Long courseId){
        Course course = courseBaseService.getCourseFromRedis(courseId);
        String key = null;
        //系列课里面的单节课
        if (course.getSeriesCourseId() >0) {
            studyRecordService.loadCourseUser(courseId);
            key = RedisKey.ll_visit_course_user_key + courseId;
        } else {
            joinCourseRecordService.loadJoinCourseUser(courseId);
            key = RedisKey.ll_join_course_user_key + courseId;
        }
        Set<String> ids = redisUtil.zrevrange(key , 0 ,  - 1);
        //已经连接的人
        Set<String> canConnects = redisUtil.smembers(RedisKey.join_room_user + courseId);
        //需要移出的人
        List<String> mustRemove = new ArrayList<>();
        for (String id :ids) {
            if (!canConnects.contains(id)) {
                mustRemove.add(id);
            }
        }
        //移出
        for (String str : mustRemove) {
            ids.remove(str);
        }
        return ids;
    }

    private static List<String> getAllUsersArray( String[] arrayIds , Integer pageSize) {
        List<String> result = new ArrayList<>();
        int index = 0;
        for (int i = index ;i < arrayIds.length ;) {
            StringBuffer sb = new StringBuffer();
            for (int j = i ; j < i + pageSize  && j <  arrayIds.length  ;j++) {
                sb.append(arrayIds[j]).append(",");
            }
            if (sb.length() > 0 ) {
                sb.deleteCharAt(sb.length() - 1);
            }
            if (!StringUtils.isEmpty(sb.toString())) {
                result.add(sb.toString());
            }
            i += pageSize ;
        }

        return result;
    }

    @Override
    public ActResultDto getAllUsers(Long courseId, String ids, Integer pageSize, Long onConnectId) {
            String[] arrayIds = null;
            Map allData = new HashMap();
            List<Map> result = new ArrayList<>();
        if(onConnectId==null){onConnectId=0L;}
        if(courseId==null){courseId=0L;}
        Set<Long> idset  = liveConnectMapper.getEvenForWheatList(onConnectId, courseId);
        if(idset==null){idset = new HashSet<Long>();}
        log.info("申请中学生数量："+idset.size());
        if (StringUtils.isEmpty(ids)) {
                Set<String> alls = this.getAllUses(courseId);
                boolean isIn = false;
                //连麦的人
                String onConnectIdStr = String.valueOf(onConnectId);
                //没买过
                if (alls.contains(onConnectIdStr)) {
                    isIn = true;
                    alls.remove(onConnectIdStr);
                }

                String[] allIds = alls.toArray(new String[]{});
                String[] newIds = allIds;
                if (isIn) {
                    newIds = new String[allIds.length + 1];
                    //把连麦的人排到第一
                    newIds[0] = onConnectIdStr;
                    if (allIds.length > 0) {
                        for (int i = 0 ; i < allIds.length ;i++) {
                            newIds[i+1] = allIds[i];
                        }
                    }
                }

                List<String> list = getAllUsersArray(newIds  ,   pageSize);
                if (list != null && list.size() > 0 ) {
                    arrayIds = getFirstPageUsers(list);
                    list.remove(0);
                    allData.put("allUser", list);
                } else {
                    arrayIds = new String[0];
                    allData.put("allUser", new ArrayList<>());
                }
            } else {
                allData.put("allUser", new ArrayList<>());
                arrayIds = ids.split(",");
            }

            for (int i = 0 ;i < arrayIds.length ;i++) {
                String id = arrayIds[i];
                if (StringUtils.isEmpty(id)) {
                    continue;
                }
                String userInofStr = redisUtil.hget(RedisKey.ll_user_info, id);
                if (!StringUtils.isEmpty(userInofStr)) {
                    Map temp = JsonUtil.getObject(userInofStr , HashMap.class);
                    if (id.equals(String.valueOf(onConnectId))) {
                        temp.put("connecting" , "1");
                    }else if(idset.contains(Long.parseLong(id))){
                        temp.put("connecting" , "2");
                    }
                    else {
                        temp.put("connecting", "0");
                    }
                    result.add(temp);
                }
            }
            allData.put("users" , result);

            return ActResultDto.success().setData(allData);
    }

    @Override
    public void saveLiveConnect(LiveConnectRequest request) {
        //200毫秒轮讯一次 ， 2秒算超时
        boolean flag = redisLock.lock(RedisKey.ll_create_connectrequest_lock + request.getCourseId() + request.getTeacher() + request.getStudent() + request.getApplyUser(), 200 * 1000, 5);
        //获取锁失败，
        if (!flag) {
            logg.info("获取连麦请求锁{}失败，请稍等!", RedisKey.ll_create_connectrequest_lock + request.getCourseId() + request.getTeacher() + request.getStudent() + request.getApplyUser());
        }{
            try{
                liveConnectRequestMapper.insert(request);
                LiveConnect liveConnect =   liveConnectMapper.getLiveConnectByRequest(request);
                if (liveConnect == null) {
                    liveConnect = new LiveConnect();
                    liveConnect.setCourseId(request.getCourseId());
                    liveConnect.setApplyUser(request.getApplyUser());
                    liveConnect.setTeacher(request.getTeacher());
                    liveConnect.setStudent(request.getStudent());
                    liveConnect.setCreateTime(new Date());
                    liveConnect.setReqId(request.getId());
                    liveConnectMapper.insert(liveConnect);
                } else {
                    liveConnect.setReqId(request.getId());
                    liveConnectMapper.updateByPrimaryKey(liveConnect);
                }
            }catch (Exception e){
                log.error("数据保存出错：",e);
            }finally {
                redisLock.unlock(RedisKey.ll_create_connectrequest_lock + request.getCourseId() + request.getTeacher() + request.getStudent() + request.getApplyUser());
            }
        }
    }

    @Override
    public void closeConnectRequest(LiveConnectRequest liveConnectRequest) {
        liveConnectRequestMapper.closeLiveConnectRequest(liveConnectRequest);
    }
}
