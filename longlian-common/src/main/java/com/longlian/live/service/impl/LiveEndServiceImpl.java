package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.SpringContextUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.EndLiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by admin on 2017/11/23.
 */
public class LiveEndServiceImpl extends TimerTask {
    private static Logger log = LoggerFactory.getLogger(LiveEndServiceImpl.class);

    private long courseId;
    private long stamp;
    private EndLiveService endLiveService;
    private RedisUtil redisUtil;
    private String type;

    public LiveEndServiceImpl(long courseId, long stamp,
                              EndLiveService endLiveService,
                              RedisUtil redisUtil,
                              String type){
        this.courseId = courseId;
        this.stamp = stamp;
        this.endLiveService = endLiveService;
        this.redisUtil = redisUtil;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            if("1".equals(type)){ //直播中
                String redisValue = redisUtil.get(RedisKey.ll_course_live_notify_cation + courseId); //获取Timer的时间
                if(String.valueOf(stamp).equals(redisValue)){
                    String action = redisUtil.get(RedisKey.ll_live_notify_cation + courseId);//获取直播流
                    if("0".equals(action)){
//                        endLiveService.noLiveStreamEndLive(courseId) ;
                        //发送到mq,处理course中的isConnection中的字段 : 0-没有 1-有
                        sendMq(courseId);
                    }
                }
            }
        } catch (Exception e) {
            log.info("结束直播错误日志: "+e);
        }
    }

    //发送到mq,处理course中的isConnection中的字段 : 0-没有 1-有
    public void sendMq(Long courseId){
        Map map = new HashMap();
        map.put("courseId",courseId + "");
        map.put("isConnection","0");
        System.out.println("timer, 直播流状态:" + 0 + " 课程ID:" + courseId);
        redisUtil.lpush(RedisKey.course_is_connection , JsonUtil.toJson(map));
    }
}
