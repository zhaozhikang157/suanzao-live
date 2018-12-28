package com.longlian.console.task;

import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.AppUserService;
import com.longlian.live.service.AppUserCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 云信Token后台创建（补漏）
 * Created by admin on 2017/5/12.
 */
@Component("createYunxinTokenTask")
public class CreateYunxinTokenTask extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(CreateYunxinTokenTask.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppUserService appUserService;

    @Autowired
    AppUserCommonService appUserCommonService;

    @Override
    public String getTaskName() {
        return "云信Token后台创建（补漏）";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("云信Token后台创建（补漏）异常："+e.getMessage());
        }
    }

    /***
     * 每分钟10触发
     */
    //@Scheduled(cron = "0 0/10 * * * ?")
    public void doJob() throws Exception {
       List<Map> list =  appUserService.findNoYunxinTokenUser();
       for (Map map : list) {
           Long id = (Long)map.get("id");
           appUserCommonService.createYunxinUser(id ,(String)map.get("name") , (String)map.get("photo") );
       }
    }

}
