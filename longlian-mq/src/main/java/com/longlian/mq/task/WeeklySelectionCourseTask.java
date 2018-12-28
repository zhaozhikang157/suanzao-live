package com.longlian.mq.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by longlian007 on 2017/12/16.
 * 首页每周精选课程缓存
 */
@Component("WeeklySelectionCourseTask")
public class WeeklySelectionCourseTask {
    private static Logger log = LoggerFactory.getLogger(WeeklySelectionCourseTask.class);//创建日志对象
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;
    @Scheduled(cron = "0 0 1 * * ?")
    public void doWeeklySelection()throws Exception{
        redisUtil.del(RedisKey.course_live_weekly_selection_id);//每次执行清空缓存
        ActResultDto r = courseBaseService.findCourseWeeklySelection();
        if(r!=null&&r.getData()!=null){
            List<Map> courseWeeklySelection = (List<Map>)r.getData();
            if(courseWeeklySelection!=null&&courseWeeklySelection.size()==4){
                for(Map map :courseWeeklySelection){
                    if(map.get("id")!=null){
                        redisUtil.sadd(RedisKey.course_live_weekly_selection_id,map.get("id").toString());
                    }
                }
            }
        }
    }
}
