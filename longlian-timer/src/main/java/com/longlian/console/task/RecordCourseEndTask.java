package com.longlian.console.task;

import com.huaxin.util.DateUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 录播课结束任务
 * Created by liuhan on 2017-09-08.
 */

@Component
public class RecordCourseEndTask extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(ButtonCountTask.class);
    @Autowired
    CourseService courseService;

    @Autowired
    EndLiveService endLiveService;

    private Logger logg= LoggerFactory.getLogger(RecordCourseEndTask.class);
    @Override
    public String getTaskName() {
        return "录播课结束任务";
    }

    @Override
    public void doExecute() {
        try {
            sendCourseMassage();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("录播课结束任务任务异常："+e.getMessage());
        }
    }

    //@Scheduled(cron = "0 0/1 * * * ?")
    public void sendCourseMassage() throws Exception {
        List<CourseDto> list = courseService.getNotEndRecordCourseList();//可以从slave走，

        for (CourseDto course : list){
            if (!"1".equals(course.getIsRecorded())) {
                continue;
            }
            if (course.getStartTime() == null) {
                continue;
            }

            logg.info("对象：{}" , course);
            Long duration = (course.getDuration() == null ? 0 :  course.getDuration())  * 1000l;
            Long recTime =  (course.getRecTime() == null ? 0 :  course.getRecTime()) * 1000l;

            if (duration == 0 && recTime == 0) {
                continue;
            }
            //时长为零，或者设置时长小于视频时长
            if ( duration == 0 || (recTime > 0 && duration > recTime )) {
                duration = recTime;
            }
            Long start = course.getStartTime().getTime();
            Long now = System.currentTimeMillis();

            //大于实际结束时间:开始时间+30s+视频时长<now
            if ((start + (30 * 1000l))< (now - duration)) {
                endLiveService.endLive(course);
                SystemLogUtil.saveSystemLog(LogType.course_console_end_by_recordtime.getType(), "1" ,   course.getAppId() ,   String.valueOf(course.getAppId())   , String.valueOf(course.getId()) ,   "课程：" + course.getLiveTopic() +"已被后台自动关闭");
            }


        }
    }

    public static void main(String[] args) {
        //录播时长
        //录播时长
        long recTime = 1800 * 1000l;


        Long timeStep = 0l;
        Long duration = 345  * 1000l;
        //如果视频时长大于录播时长，则按录播时长，如果视频时长小于录播时长，则按视频时长
        if (duration == null || duration == 0 || recTime <= duration ) {
            timeStep = recTime;
        } else {
            timeStep = duration;
        }

        Long start = (DateUtil.parseDate("2017-09-14 17:31:22")).getTime();
        Long now = System.currentTimeMillis();

        //大于实际结束时间
        if ((start + (30 * 1000))< (now - timeStep)) {
            System.out.println("clean");
        }


    }
}

