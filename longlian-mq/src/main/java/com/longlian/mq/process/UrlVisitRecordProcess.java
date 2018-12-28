package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.PageUrlService;
import com.longlian.live.util.ElasticsearchClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by liuhan on 2017-07-03.
 */
@Service("urlVisitRecordProcess")
public class UrlVisitRecordProcess  extends LongLianProcess {


    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PageUrlService pageUrlService;

    public  int threadCount=10;

    private Logger logg = LoggerFactory.getLogger(UrlVisitRecordProcess.class);

    @Autowired
    private ElasticsearchClient client;
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map urlVisit = JsonUtil.getObject(msg, Map.class);

            String url = (String)urlVisit.get("url");
            //处理带/的问题
            if (!StringUtils.isEmpty(url) && url.endsWith("/")) {
                url = url.substring(0 , url.length() - 2);
                urlVisit.put("url" , url);
            }

            //如果是页面访问，将页面访问放入数据库中
            if ("1".equals(urlVisit.get("isPage"))) {
                pageUrlService.savePageUrl((String)urlVisit.get("url"));
            }

            Long time = (Long)urlVisit.get("visitTime");
            String date = DateFormatUtils.format(new Date(time) , "yyyy-MM-dd");
            urlVisit.put("visitDate" ,date);

            //增加人员
            redisUtil.sadd(RedisKey.ll_page_visit_user + date , String.valueOf(urlVisit.get("userId")));
            redisUtil.expire(RedisKey.ll_page_visit_user + date , 60 * 60 * 24 * 3);

            if (client.getInstance() != null) {
                IndexResponse response = client.getInstance().prepareIndex(client.getIndex(), "urlvisit")
                        .setSource(JsonUtil.toJson(urlVisit))
                        .get();
            }
        }
    }

    @Override
    public void addThread() {
        UrlVisitRecordProcess.GetData t1 = new UrlVisitRecordProcess.GetData(this , redisUtil , RedisKey.ll_url_visit_record_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
