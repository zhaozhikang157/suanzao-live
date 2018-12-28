package com.longlian.mq.process;

import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.PushCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by admin on 2018/1/30.
 *
 * 预热录播课地址
 */
@Service
public class CacheAddressProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(CacheAddressProcess.class);

    //每天预热上限
    private final long cacheCount = 2000;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    PushCacheService pushCacheService;

    public  int threadCount=10;

    private class GetData extends DataRunner {

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            pushCacheService.pushCacheAddress(msg);
        }
    }


    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.push_object_cache_course_address);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
