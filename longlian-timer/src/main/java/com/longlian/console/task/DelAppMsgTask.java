package com.longlian.console.task;

import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.live.service.AppMsgService;
import org.apache.tools.ant.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 删除10天之前的消息
 * Created by admin on 2017/5/12.
 */
@Component("delAppMsgTask")
public class DelAppMsgTask extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(DelAppMsgTask.class);

    @Autowired
    AppMsgService appMsgService;

    @Override
    public String getTaskName() {
        return "删除10天之前的消息";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除10天之前的消息异常："+e.getMessage());
        }
    }

    /***
     * 每天晚上1点半触发
     */
    //@Scheduled(cron = "0 30 1 * * ?")
    public void doJob() throws Exception {
        Calendar calendar = Calendar.getInstance();
        //删除10天前的
        calendar.add(Calendar.DAY_OF_MONTH,-10);
        String date = DateUtils.format(calendar.getTime() , "yyyy-MM-dd");
        System.out.println(date);
        //删除11这种类型的消息:关注人的开课通知提醒
        appMsgService.delAppMsgBefore(calendar.getTime() , 11);
    }

}
