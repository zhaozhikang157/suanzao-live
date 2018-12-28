package com.longlian.mq.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.mq.redis.ISRunPubSub;
import com.longlian.mq.util.LogDealUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Created by liuhan on 2017-11-11.
 */
@Component
public class InitLogDeal implements  InitializingBean{
    @Autowired
    private CdnLogTask cdnLogTask;
    @Autowired
    private OssLogTask ossLogTask;

    @Autowired
    private CourseDataUseTask courseDataUseTask;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isRun = false;
        int min = -2;

        for (int i = -1 ;i > min ;i--) {
            LogDealUtil.dayBefore = i;
            String date = LogDealUtil.getBeferDay();

            if (isRun) {
                cdnLogTask.doJob();
                ossLogTask.doJob();
                courseDataUseTask.doJob();
            }
        }

    }
}
