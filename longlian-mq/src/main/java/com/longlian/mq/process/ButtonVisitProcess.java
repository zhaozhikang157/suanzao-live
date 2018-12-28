package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ButtonCount;
import com.longlian.live.util.ElasticsearchClient;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by lh on 2016/10/20.
 */
@Service
public class ButtonVisitProcess extends LongLianProcess {

    @Autowired
    private RedisUtil redisUtil;
    public  int threadCount=10;
    @Autowired
    private ElasticsearchClient client;

    private Logger logg = LoggerFactory.getLogger(ButtonVisitProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            ButtonCount buttonCount= JsonUtil.getObject(msg, ButtonCount.class);
            if (client.getInstance() != null) {
                IndexResponse response = client.getInstance().prepareIndex(client.getIndex(), "buttonCount")
                        .setSource(msg)
                        .get();
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_button_visit_record_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
       super.afterPropertiesSet();
    }
}
