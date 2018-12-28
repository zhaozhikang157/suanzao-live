package com.longlian.live.service.impl;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.CourseCommonMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveChannel;
import com.longlian.type.LogType;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 结束直播接口
 * Created by liuhan on 2017-03-06.
 */
@Service("endLiveService")
public class EndLiveServiceImpl implements EndLiveService{
    private static Logger log = LoggerFactory.getLogger(EndLiveServiceImpl.class);
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    AccountService accountService;
    @Autowired
    CourseCommonMapper courseCommonMapper;
    @Autowired
    CourseBaseService courseBaseService;
    /**
     * 结束直播
     *
     * @param courseId
     */
    @Override
    public void endLive(Long courseId, Long userId) throws Exception {
        //获取课程信息
        Course course = courseCommonMapper.getCourse(courseId);//获取课程
        endLive(course);
    }


    /**
     * 结束直播  后台轮询
     * @param course  课程对象
     */
    @Override
    public void endLive(Course course) throws Exception {

        long courseId = course.getId();

        //更新结束时间
        courseCommonMapper.updateEndTime(courseId);
        redisUtil.setex(RedisKey.course_is_end + courseId , 20 , "1");



        //n收回直播通道资源
        Map msg = new HashMap();
        msg.put("courseId", String.valueOf(courseId));
        LiveChannel liveChannel = qiNiuliveChannelService.getDefaultLiveChannel();
        msg.put("domain", liveChannel.getDomain());
        redisUtil.lpush(RedisKey.ll_course_end_event_key, JsonUtil.toJsonString(msg));
        redisUtil.expire(RedisKey.ll_live_notify_cation + courseId, 20 * 60);           //判断直播流是否断开
        redisUtil.expire(RedisKey.ll_course_live_notify_cation + courseId, 20 * 60);   //直播流断开 创建了Timer
        redisUtil.srem(RedisKey.ll_set_course_vr_user, String.valueOf(courseId));   //移动给该课程添加虚拟用户

        //如果是系列课下的单节课,则直接结束
        if("0".equals(course.getIsSeriesCourse()) && course.getSeriesCourseId() > 0){
            courseBaseService.deal(course.getSeriesCourseId());
        }else{
            // 如果是系列课里面的单节课，则发送更新系列课时间队列
            UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        }
    }

    @Override
    public void noLiveStreamEndLive(Long courseId) throws Exception  {
        Course course = courseCommonMapper.getCourse(courseId);//获取课程
        this.endLive(course);
        SystemLogUtil.saveSystemLog(LogType.system_colse_course.getType(), "0" ,   course.getAppId() ,   String.valueOf(course.getAppId())   , String.valueOf(course.getId()) ,   "课程：" + course.getLiveTopic() +"老师未在规定时间内直播该课程,已被系统下架");
    }
}
