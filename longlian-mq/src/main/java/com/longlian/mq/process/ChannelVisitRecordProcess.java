package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.ChannelVisitRecordService;
import com.longlian.model.ChannelVisitRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lh on 2016/10/20.
 * 渠道访问记录
 */
@Service
public class ChannelVisitRecordProcess extends LongLianProcess {

    @Autowired
    private ChannelVisitRecordService channelVisitRecordService;
    @Autowired
    private RedisUtil redisUtil;

    public  int threadCount = 10;

    private Logger logg = LoggerFactory.getLogger(ChannelVisitRecordProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            ChannelVisitRecord channelVisitRecord= JsonUtil.getObject(msg, ChannelVisitRecord.class);
            channelVisitRecordService.saveChannelVisitRecord(channelVisitRecord);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_channel_visit_record_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
