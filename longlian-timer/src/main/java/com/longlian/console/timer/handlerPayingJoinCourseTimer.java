package com.longlian.console.timer;

import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.LongLianRewardService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/8.
 * 定时处理 支付中的订单  5分钟之前的
 */
@Component("handlerPayingJoinCourseTimer")
public class handlerPayingJoinCourseTimer extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(handlerPayingJoinCourseTimer.class);

    public static  long times = 10 * 60 *1000;
    @Autowired
    LongLianRewardService longLianRewardService;

    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "定时处理 支付中的订单 5分钟之前的";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            job();
        } catch (Exception e) {
            log.error("定时处理 支付中的订单 5分钟之前的"+e.getMessage());
            e.printStackTrace();
        }
    }

    public  void job() throws  Exception {
        Calendar calendar = Calendar.getInstance();
        long currTime = calendar.getTimeInMillis();
        currTime = currTime - times;
        calendar.setTimeInMillis(currTime);
        System.out.println(Utility.getDateTimeStr(calendar.getTime()));
       /* List<Map> list  = joinCourseRecordService.getHandlerPayingCourse(calendar.getTime());
        for (Map map : list){
            long id = (Long)map.get("id");
            longLianRewardService.handlerPayingJoinCourse(id);
        }*/
    }

}
