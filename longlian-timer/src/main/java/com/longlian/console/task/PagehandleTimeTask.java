package com.longlian.console.task;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.UserCountMapper;
import com.longlian.console.util.CountUtil;
import com.longlian.live.util.ElasticsearchClient;
import com.longlian.model.UserCount;
import com.longlian.type.CountType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 页面平均执行时间统计
 * Created by liuhan on 2017-07-15.
 */
@Component("pagehandleTimeTask")
public class PagehandleTimeTask extends AbstractShardingTask{
    private static Logger log = LoggerFactory.getLogger(PageCountTask.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Autowired
    UserCountMapper userCountMapper;

    private final static int pageSize = 10;

    @Override
    public String getTaskName() {
        return "页面平均执行时间统计";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("页面平均执行时间统计异常："+e.getMessage());
        }
    }

    /***
     * 每天晚上1点触发
     */
    //@Scheduled(cron = "0 0 2 * * ?")
    public void doJob() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String date = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        SearchRequestBuilder searchRequestBuilder =  elasticsearchClient.getInstance().prepareSearch(elasticsearchClient.getIndex());
        searchRequestBuilder.setTypes("urlvisit");
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequestBuilder.addSort("visitTime", SortOrder.ASC);

        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("visitDate", date))
                .must(QueryBuilders.matchPhraseQuery("isPage" , "1")) ;
        searchRequestBuilder.setQuery(boolQueryBuilder);


        Map pageMap = redisUtil.hmgetAll(RedisKey.ll_page_url);
        Map<Long , Long> pageCount = new HashMap();
        Map<Long , Long>  pageHandlerTime = new HashMap();

        List<Map> result = new ArrayList<>();
        SearchHit[] hits = null;
        searchRequestBuilder.setSize(pageSize);
        int index = 0 ;
        do {
            searchRequestBuilder.setFrom(index);
            searchRequestBuilder.setSize(pageSize);

            SearchResponse response = searchRequestBuilder.execute()
                    .actionGet();
            SearchHits searchHits = response.getHits();
            hits = searchHits.getHits();

            for (int i = 0; i < hits.length; i++) {
                Map temp = getMap(hits[i]);

                String url = (String)temp.get("url");
                if (url == null) {
                    url = (String)temp.get("uri");
                }

                Long pageId = Long.parseLong((String)pageMap.get(url));

                //访问次数
                CountUtil.mapAdd(pageCount , pageId , 1l);

                Integer handlerTime = (Integer)temp.get("handleTime");
                //访问执行时间加上
                CountUtil.mapAdd(pageHandlerTime , pageId , handlerTime.longValue());
                result.add(temp);
            }
            index += pageSize;
            //如果返回的条数等于pagesize则说明，可能还有一页
        } while(hits != null && hits.length == pageSize);

        //页面人均停留
        Map<Long , Long>  pageTimeAvg = new HashMap();
        for (Long pageId : pageHandlerTime.keySet()) {
            Long time = pageHandlerTime.get(pageId);
            Long pageUser = pageCount.get(pageId);
            Long avg = CountUtil.avg(time , pageUser).longValue();
            pageTimeAvg.put(pageId , avg);
        }

        saveMapToDB(pageTimeAvg, CountType.PAGE_HANDLE_TIME_AVG.getType() ,   date);
    }


    private void saveMapToDB(Map<Long , Long> map , String type , String date) {
        Set<Long> keys = map.keySet();
        for (Long pageId : keys) {
            if (map.get(pageId) == null) {
                continue;
            }
            UserCount uc = new UserCount(date, "", Integer.valueOf(type) ,new BigDecimal(map.get(pageId)) , pageId);
            userCountMapper.insert(uc);
        }
    }

    public Map getMap(SearchHit hit) {
        String json = hit.getSourceAsString();
        Map temp = JsonUtil.getObject(json ,  HashMap.class);
        return temp;
    }

}
