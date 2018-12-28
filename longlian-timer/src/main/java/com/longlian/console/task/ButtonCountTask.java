package com.longlian.console.task;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.UserCountMapper;
import com.longlian.live.util.ElasticsearchClient;
import com.longlian.model.UserCount;
import com.longlian.type.Button;
import com.longlian.type.CountType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;

/**
 * 各个按钮点击次数统计
 * Created by admin on 2017/5/12.
 */
@Component
public class ButtonCountTask extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(ButtonCountTask.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Autowired
    UserCountMapper userCountMapper;
    @Override
    public String getTaskName() {
        return "各个按钮点击次数统计";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("各个按钮点击次数统计任务异常："+e.getMessage());
        }
    }

    /***
     * 每天晚上1点触发
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    public void doJob() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String date = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        //构建两种，分组方法
        SearchRequestBuilder searchButtonRequestBuilder =  getSearchRequestBuilder("button");
        SearchRequestBuilder searchButtonRefererRequestBuilder =  getSearchRequestBuilder("referer");

        List<Button> buttons = Button.getList();
        for (Button button : buttons) {
            //根据button是否有子button采用不同的构建方法
            SearchRequestBuilder searchRequestBuilder = searchButtonRequestBuilder;
            if (button.isHasChild()) {
                searchRequestBuilder = searchButtonRefererRequestBuilder;
            }

            BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("createTime", date))
                    .must(QueryBuilders.matchPhraseQuery("button" , button.getType()));
            searchRequestBuilder.setQuery(boolQueryBuilder);

            SearchResponse response = searchRequestBuilder.execute()
                    .actionGet();
            Map<String, Aggregation> aggMap = response.getAggregations().asMap();
            StringTerms buttonTerms = (StringTerms) aggMap.get("buttonAgg");
            Iterator<Bucket> bucketIterator = buttonTerms.getBuckets().iterator();
            while(bucketIterator.hasNext())  {
                Bucket buttonBucket = bucketIterator.next();
                UserCount uc = new UserCount(date, "", Integer.valueOf(CountType.BUTTON_CLICK_COUNT.getType()) ,new BigDecimal(buttonBucket.getDocCount()),buttonBucket.getKeyAsString() );
                userCountMapper.insert(uc);

                StringTerms typeTerms = (StringTerms) buttonBucket.getAggregations().asMap().get("typeAgg");
                Iterator<Bucket> typeBucketIt = typeTerms.getBuckets().iterator();
                while(typeBucketIt.hasNext())  {
                    Bucket typeBucket = typeBucketIt.next();
                    UserCount ucChild = new UserCount(date, typeBucket.getKeyAsString(), Integer.valueOf(CountType.BUTTON_CLICK_COUNT.getType()) ,new BigDecimal(typeBucket.getDocCount()),buttonBucket.getKeyAsString() );
                    userCountMapper.insert(ucChild);
                }
            }
        }
    }

    public SearchRequestBuilder getSearchRequestBuilder(String type) {
        SearchRequestBuilder searchButtonRequestBuilder =  elasticsearchClient.getInstance().prepareSearch(elasticsearchClient.getIndex());
        searchButtonRequestBuilder.setTypes("buttonCount");
        searchButtonRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("buttonAgg").field(type);
        AggregationBuilder subAgg = AggregationBuilders.terms("typeAgg").field("clientType");
        searchButtonRequestBuilder.addAggregation(aggregationBuilder.subAggregation(subAgg));
        return searchButtonRequestBuilder;
    }

    public Map getMap(SearchHit hit) {
        String json = hit.getSourceAsString();
        Map temp = JsonUtil.getObject(json ,  HashMap.class);
        return temp;
    }

    /**
     * 向m中增加多少
     * @param m
     * @param pageId
     * @param addCount
     */
    public void mapAdd(Map<Long , Long> m , Long pageId , Long addCount) {
        if ( m.containsKey(pageId)){
            m.put(pageId , m.get(pageId) + addCount);
        } else {
            m.put(pageId , addCount);
        }
    }


    private String getDateHmStr() {
        String dateStr = DateFormatUtils.format(new Date(), "HH:mm");
        return dateStr;
    }
    private String getDateStr() {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return dateStr;
    }
}
