package com.longlian.console.timer;

import com.huaxin.util.DateUtil;
import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.LongLianRewardService;
import com.longlian.console.task.ButtonCountTask;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Course;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 课程结束
 */
@Component("courseEndTimer")
public class CourseEndTimer extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(CourseEndTimer.class);
    @Autowired
    CourseService courseService;

    @Autowired
    EndLiveService endLiveService;

    private String notNeedEnd;


    public void setNotNeedEnd(String notNeedEnd) {
        this.notNeedEnd = notNeedEnd;
    }

    public void job() throws  Exception {
        log.info("courseEndTimer直播>8h,课程结束定时结束状态==========start");
        List<Course> list = courseService.getNotEndListBy24Hour();//可以从slave走，
        //返钱
        for (Course course : list){
            if (Utility.findById(notNeedEnd, String.valueOf(course.getId()))) {
                continue;
        }

           Date startTime =  course.getStartTime();
           if (startTime != null) {
               long start = startTime.getTime();
               long now = new Date().getTime();
               //超过时间(自动关闭时间)
               if (course.getAutoCloseTime() == 0) {
                   continue;
               }
               //开始时间后>8h则自动结束课程
                if (start < (now - course.getAutoCloseTime() * 60 * 60 * 1000)) {
                    endLiveService.endLive(course);
                    log.info("courseEndTimer直播>8h,课程结束定时结束状态==========end");
                    SystemLogUtil.saveSystemLog(LogType.course_console_end.getType(), "1" ,   course.getAppId() ,   String.valueOf(course.getAppId())   , String.valueOf(course.getId()) ,   "课程：" + course.getLiveTopic() +"已被后台自动关闭");
                }
           }



        }
    }


    /**
     * 任务名称
     *
     * @return
     */
    @Override
    public String getTaskName() {
        return "定时结束课程";
    }

    /**
     * 具体执行的任务
     */
    @Override
    public void doExecute() {
        try{
            job();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时结束课程任务异常："+e.getMessage());
        }
    }
}
