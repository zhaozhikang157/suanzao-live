package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.CdnVisit;
import com.longlian.mq.service.CdnVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lh on 2016/10/20.
 * CDN消息处理
 */
@Service
public class CdnVisitProcess extends LongLianProcess {

    @Autowired
    private CdnVisitService cdnVisitService;
    @Autowired
    private RedisUtil redisUtil;

    public  int threadCount=10;

    private Logger logg = LoggerFactory.getLogger(CdnVisitProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            CdnVisit visit = JsonUtil.getObject(msg, CdnVisit.class);
            cdnVisitService.saveCdnVisit(visit);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_cdn_visit_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
