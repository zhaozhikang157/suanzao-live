package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.LiveConnectRequest;
import com.longlian.mq.service.LiveConnectRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lh on 2016/10/20.
 * APP消息处理
 */
@Service
public class WriteLiveConnectReqOnlStatusProcess extends LongLianProcess {

    @Autowired
    private LiveConnectRequestService liveConnectRequestService;
    @Autowired
    private RedisUtil redisUtil;
    public  int threadCount=10;

    private Logger logg = LoggerFactory.getLogger(WriteLiveConnectReqOnlStatusProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            LiveConnectRequest connectRequest = JsonUtil.getObject(msg, LiveConnectRequest.class);
            liveConnectRequestService.setLiveConnectStudenOnlineStatus(connectRequest);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.write_student_connect_online_status);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
