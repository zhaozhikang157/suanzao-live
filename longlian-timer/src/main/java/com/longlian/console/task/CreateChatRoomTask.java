package com.longlian.console.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.CourseService;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/12.
 */
//@Component("createChatRoomTask")
public class CreateChatRoomTask {

    private static Logger log = LoggerFactory.getLogger(CreateChatRoomTask.class);

    //@Autowired
    //RedisUtil redisUtil;
    //@Autowired
    //CourseService courseService;
    //@Autowired
    //CourseBaseService courseBaseService;


    /***
     * 每分钟5触发
     */
   // @Scheduled(cron = "0 0/5 * * * ?")
    public void doJob() throws Exception {
      /* List<Course> list =  courseService.getNoChatRoomId();

       for (Course course : list) {
           //courseBaseService.setChatroomId(course , true);
       }*/
    }


}
