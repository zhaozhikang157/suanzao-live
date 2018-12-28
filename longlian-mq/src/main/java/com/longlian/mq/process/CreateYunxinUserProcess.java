package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lh on 2016/10/20.
 */
@Service
public class CreateYunxinUserProcess extends LongLianProcess {

    @Autowired
    private AppUserCommonService appUserCommonService;
    @Autowired
    private RedisUtil redisUtil;

    public  int threadCount=10;


    private Logger logg = LoggerFactory.getLogger(CreateYunxinUserProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getObject(msg , HashMap.class);
            String id = (String)map.get("id");
            String name = (String)map.get("name");
            String photo =  (String)map.get("photo");
            appUserCommonService.createYunxinUser(Long.parseLong(id), name , photo);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_create_yunxin_user);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
