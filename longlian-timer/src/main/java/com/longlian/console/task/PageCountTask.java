package com.longlian.console.task;

import com.github.pagehelper.StringUtil;
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
import java.math.RoundingMode;
import java.util.*;

/**
 * 页面访问统计
 * Created by admin on 2017/5/12.
 */
@Component("pageCountTask")
public class PageCountTask extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(PageCountTask.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    private  static final String livePageUrl = "/weixin/live/index.user";

    @Autowired
    UserCountMapper userCountMapper;

    private final static int pageSize = 10;

    @Override
    public String getTaskName() {
        return "页面访问统计";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("页面访问统计"+e.getMessage());
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
        SearchRequestBuilder searchRequestBuilder =  elasticsearchClient.getInstance().prepareSearch(elasticsearchClient.getIndex());
        searchRequestBuilder.setTypes("urlvisit");
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequestBuilder.addSort("visitTime", SortOrder.ASC);

        Set<String> users =  redisUtil.smembers(RedisKey.ll_page_visit_user + date);



        //综合数量pv
        int allCount = 0;
        //综合人数uv
        int allUserCount = users.size();
        //停留总时间
        long allTime = 0l;

        Map<Long , Long> pageCount = new HashMap();
        Map<Long , Long>  pageUserCount = new HashMap();
        Map<Long , Long>  pageTime = new HashMap();
        Map<Long , Long>  pageLeaveCount = new HashMap();
        Map<Long , Long>  pageUserLeaveCount = new HashMap();

        Map pageMap = redisUtil.hmgetAll(RedisKey.ll_page_url);

        for (String user : users) {
            if ("0".equals(user) || StringUtil.isEmpty(user)) {
                continue;
            }
            List<Map> result = this.findByUser(  searchRequestBuilder ,   user ,   date);

            Set<Long> hasPageUserCount = new HashSet<>();
            Set<Long> hasPageUserLeaveCount = new HashSet<>();

            for (int i = 0 ; i < result.size() ;i++) {
                Map temp = result.get(i);
                String url = (String)temp.get("url");
                if (url == null) {
                    url = (String)temp.get("uri");
                }
                Long visitTime = (Long)temp.get("visitTime");
                //一搬页面，10分钟
                Long intervalTime = 10 * 60 * 1000l;
                if (livePageUrl.equals(url)) {
                    //直播页面，30分钟
                    intervalTime = 30 * 60 * 1000l;
                }
                Long pageId = Long.parseLong((String)pageMap.get(url));


                //页面访问次数pv
                CountUtil.mapAdd(pageCount , pageId , 1l);
                             //页面访问人数uv, 这个页面是否记过一次了
                if (!hasPageUserCount.contains(pageId)) {
                    CountUtil.mapAdd(pageUserCount , pageId , 1l);
                    hasPageUserCount.add(pageId);
                }

                if (i == result.size() - 1) {
                    allCount++;
                    //页面退出次数pv
                    CountUtil.mapAdd(pageLeaveCount , pageId , 1l);
                    //页面退出人次uv
                    if (!hasPageUserLeaveCount.contains(pageId)) {
                        CountUtil.mapAdd(pageUserLeaveCount , pageId , 1l);
                        hasPageUserLeaveCount.add(pageId);
                    }
                } else {
                    Map tempNext = result.get(i + 1);
                    Long nextPageVisitTime = (Long)tempNext.get("visitTime");
                    Long timeDiff = nextPageVisitTime - visitTime;//时间差
                    if (timeDiff > intervalTime) {
                        allCount++;
                        //页面退出次数pv
                        CountUtil.mapAdd(pageLeaveCount , pageId , 1l);
                        //页面退出人次uv
                        if (!hasPageUserLeaveCount.contains(pageId)) {
                            CountUtil.mapAdd(pageUserLeaveCount , pageId , 1l);
                            hasPageUserLeaveCount.add(pageId);
                        }
                    } else {
                        CountUtil.mapAdd(pageTime , pageId , timeDiff);
                        allTime += timeDiff;
                    }
                }
            }
        }
       //页面人均停留
        Map<Long , Long>  pageTimeAvg = new HashMap();
        for (Long pageId : pageTime.keySet()) {
            Long time = pageTime.get(pageId);
            Long pageUser = pageUserCount.get(pageId);
            Long avg = CountUtil.avg(time , pageUser).longValue();
            pageTimeAvg.put(pageId , avg);
        }

        saveMapToDB(pageCount ,CountType.PAGE_VISIT_COUNT.getType() ,   date);
        saveMapToDB(pageUserCount ,CountType.PAGE_VISIT_USER_COUNT.getType() ,   date);
        saveMapToDB(pageTimeAvg ,CountType.PAGE_STAY_TIME.getType() ,   date);
        saveMapToDB(pageLeaveCount ,CountType.PAGE_EXIT_COUNT.getType() ,   date);
        saveMapToDB(pageUserLeaveCount ,CountType.PAGE_EXIT_USER_COUNT.getType() ,   date);

        saveExitRateMapToDB(pageCount , pageLeaveCount   , CountType.PAGE_EXIT_RATE.getType() ,   date);

        //如果UV>PV则按uv算
        if (allCount < allUserCount) {
            allCount = allUserCount;
        }

        UserCount uc = new UserCount(date, "", Integer.valueOf(CountType.PAGESITE_VISIT_COUNT.getType()) ,new BigDecimal(allCount) );
        userCountMapper.insert(uc);

        UserCount uc2 = new UserCount(date, "", Integer.valueOf(CountType.PAGESITE_VISIT_USER_COUNT.getType()) ,new BigDecimal(allUserCount) );
        userCountMapper.insert(uc2);

        UserCount uc3 = new UserCount(date, "", Integer.valueOf(CountType.PAGESITE_STAY_TIME.getType()) ,CountUtil.avg(allTime , (long)allUserCount));
        userCountMapper.insert(uc3);
    }


    private List<Map> findByUser( SearchRequestBuilder searchRequestBuilder , String userId , String date) {
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("visitDate", date))
                .must(QueryBuilders.matchPhraseQuery("isPage" , "1"))
                .must(QueryBuilders.matchPhraseQuery("userId"  , Long.parseLong(userId)));
        searchRequestBuilder.setQuery(boolQueryBuilder);

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
                result.add(temp);
            }
            index += pageSize;
            //如果返回的条数等于pagesize则说明，可能还有一页
        } while(hits != null && hits.length == pageSize);
        return result;
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

    private void saveExitRateMapToDB(Map<Long , Long> map ,Map<Long , Long> exitMap   , String type , String date) {
        Set<Long> keys = map.keySet();
        for (Long pageId : keys) {
            Long visitPage = map.get(pageId) == null ? 0l : map.get(pageId)  ;
            Long exitVisitPage =  exitMap.get(pageId) == null ? 0l : exitMap.get(pageId)  ;


            BigDecimal bd = new BigDecimal(0);
            if (visitPage != 0) {
                bd = new BigDecimal(exitVisitPage).divide(new BigDecimal(visitPage), 2, RoundingMode.HALF_UP);
            }

            UserCount uc = new UserCount(date, "", Integer.valueOf(type) ,bd , pageId);
            userCountMapper.insert(uc);
        }
    }

    public Map getMap(SearchHit hit) {
        String json = hit.getSourceAsString();
        Map temp = JsonUtil.getObject(json ,  HashMap.class);
        return temp;
    }

}
