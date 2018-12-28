package com.longlian.mq.process;

import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.SystemLog;
import com.longlian.model.UserFollow;
import com.longlian.mq.service.SystemLogService;
import com.longlian.mq.service.UserFollowService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pangchao on 2017/3/23.
 */
@Service("userFollowReadedProcess")
public class UserFollowReadedProcess  extends LongLianProcess {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserFollowService userFollowService;


    @Value("${syslog.threadCount:10}")
    private int threadCount = 10;


    private Logger logg = LoggerFactory.getLogger(UserFollowReadedProcess.class);


    private class GetData  extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            logg.info("关注记录的ID：" + msg);
            userFollowService.updataIsReaderd(Long.parseLong(msg));
        }


    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_userfollow_readed_sync_key);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}