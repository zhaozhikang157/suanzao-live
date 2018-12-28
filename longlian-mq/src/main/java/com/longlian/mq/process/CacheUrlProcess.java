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
 * 预热图片和语音
 */
@Service
public class CacheUrlProcess extends LongLianProcess{
    private static Logger log = LoggerFactory.getLogger(CacheUrlProcess.class);

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
            pushCacheService.pushCacheUrl(msg);
        }
    }


    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.push_object_cache_chat_room_msg);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
