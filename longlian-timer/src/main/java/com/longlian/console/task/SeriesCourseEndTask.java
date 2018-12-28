package com.longlian.console.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.CourseService;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by admin on 2017/5/12.
 */
//@Component("seriesCourseEndTask")
public class SeriesCourseEndTask {

    private static Logger log = LoggerFactory.getLogger(SeriesCourseEndTask.class);
    @Autowired
    CourseService courseService;

    @Autowired
    RedisUtil redisUtil;
    /***
     * 每1小时触发
     */
    //@Scheduled(cron = "0 0 */1 * * ?")
    public void doJob() throws Exception {
        List<Course> list = courseService.getSeriesCourseNotEnd();//可以从slave走，
        for (Course course : list){
            List<Course> children = courseService.getCourseBySeries(course.getId());
            //有课，且有没结束的课
            if (children != null && children.size() > 0 ) {
                boolean isEnd = true;
                for (Course c : children) {
                    if (c.getEndTime() == null) {
                        isEnd = false;
                    }
                }
                //更新结束时间
                if (isEnd) {
                    courseService.updateEndTime(course.getId());
                    String courseKey = RedisKey.ll_course + course.getId();
                    redisUtil.del(courseKey);
                }
            }


        }
    }




}
