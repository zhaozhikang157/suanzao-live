package com.longlian.console.task;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.CourseButtonStatictisMapper;
import com.longlian.console.util.SystemUtil;
import com.longlian.live.util.ElasticsearchClient;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 课程按钮/返回按钮/播放按钮 访问统计 停留时长
 * Created by admin on 2017/5/12.
 */
@Component("courseButtonCountTask")
public class CourseButtonCountTask extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(CourseButtonCountTask.class);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ElasticsearchClient elasticsearchClient;
    @Value("${course.visit.url}")
    public String courseUrlArray;
    @Autowired
    CourseButtonStatictisMapper courseButtonStatictisMapper;
    private final static int pageSize = 500;

    @Override
    public String getTaskName() {
        return "课程按钮/返回按钮/播放按钮 访问统计 停留时长";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("课程按钮/返回按钮/播放按钮 访问统计 停留时长"+e.getMessage());
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
        searchRequestBuilder.setTypes("buttonCount");
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
      //  searchRequestBuilder.addSort("visitTime", SortOrder.ASC);
       // Set<String> users =  redisUtil.smembers(RedisKey.ll_page_visit_user + date);
        //综合数量pv
        int allCount = 0;
        //综合人数uv
       // int allUserCount = users.size();
        //停留总时间
        long allTime = 0l;

        Set<String> pCount=new HashSet<>();

        List<Map> dbMaps=new ArrayList<>();
        Map map=new TreeMap<>();
         List<Map> result = this.findByUser(searchRequestBuilder ,   null ,   date);  //统计当天页面 访问人数
         for (int i = 0 ; i < result.size() ;i++) {   //遍历用户从直播间取出数据集合
                Map temp = result.get(i);
                        boolean flag=false;
                        if(dbMaps!=null && dbMaps.size()>0){
                            for (Map dbMap:dbMaps){
                                //判断是否是相同课程 相同按钮的点击事件   buttonType:008 课程按钮点击 courseId
                                if(dbMap!=null && dbMap.get("courseId")!=null && dbMap.get("courseId").equals(temp.get("courseId"))&& (dbMap.get("buttonType")!=null &&dbMap.get("buttonType").equals(temp.get("button")))){
                                    dbMap.put("courseId",temp.get("courseId"));
                                    dbMap.put("visitCount",Long.parseLong(dbMap.get("visitCount")+"")+1);
                                    Set set= (Set) dbMap.get("pCount");
                                    set.add(temp.get("userId")+"");
                                    dbMap.put("pCount",set);
                                    if( SystemUtil.clientType[0].equals(temp.get("clientType")+"")){//ios+1
                                        dbMap.put("iosCountc", Long.parseLong(dbMap.get("iosCountc") + "") + 1);
                                        Set iospSet= (Set) dbMap.get("iosCountp");
                                        iospSet.add(temp.get("userId") + "");
                                        dbMap.put("iosCountp",iospSet);
                                    }else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {//android +1
                                        dbMap.put("androidCountc",Long.parseLong(dbMap.get("androidCountc") + "") + 1);
                                        Set androidpSet= (Set) dbMap.get("androidCountp");
                                        androidpSet.add(temp.get("userId") + "");
                                        dbMap.put("androidCountp",androidpSet);
                                    }else /*if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/ {//weixin +1
                                        dbMap.put("weixinCountc", Long.parseLong(dbMap.get("weixinCountc") + "") + 1);
                                        Set weixinpSet= (Set) dbMap.get("weixinCountp");
                                        weixinpSet.add(temp.get("userId") + "");
                                        dbMap.put("weixinCountp",weixinpSet);
                                    }
                                    flag=true;
                                    break;
                                }
                            }
                        }
                        if(!flag){  //在dbMaps中没有找到数据
                            Map mapv=new TreeMap<>();
                            mapv.put("courseId", temp.get("courseId"));
                            mapv.put("visitCount",1);
                            mapv.put("staticDate", date);
                            Set count =new HashSet<>();
                            count.add(temp.get("userId") + "");
                            mapv.put("pCount", count);
                            mapv.put("buttonType", temp.get("button"));
                            mapv.put("androidCountc",0); mapv.put("weixinCountc",0); mapv.put("iosCountc",0);
                            mapv.put("androidCountp",new HashSet<>()); mapv.put("weixinCountp",new HashSet<>()); mapv.put("iosCountp",new HashSet<>());
                            if( SystemUtil.clientType[0].equals(temp.get("clientType")+"")){//ios+1
                                mapv.put("iosCountc",1);
                                Set countios =new HashSet<>();
                                countios.add(temp.get("userId") + "");
                                mapv.put("iosCountp",countios);
                            }else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {//android +1
                                mapv.put("androidCountc",1);
                                Set countAndroid =new HashSet<>();
                                countAndroid.add(temp.get("userId") + "");
                                mapv.put("androidCountp",countAndroid);
                            }else /*if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/ {//weixin +1
                                mapv.put("weixinCountc",1);
                                Set countWeixin =new HashSet<>();
                                countWeixin.add(temp.get("userId") + "");
                                mapv.put("weixinCountp",countWeixin);
                            }
                            dbMaps.add(mapv);
                        }
        }
      //  for (Map dmap: dbMaps){
        for(int i=0;i<dbMaps.size();i++){
                Map dmap = dbMaps.get(i);
        //    }
            Set set= (Set) dmap.get("pCount");
            Set weixinCountp= (Set) dmap.get("weixinCountp");
            Set androidCountp= (Set) dmap.get("androidCountp");
            Set iosCountp= (Set) dmap.get("iosCountp");
            dmap.put("pCount",set.size());
            dmap.put("weixinCountp",(weixinCountp!=null && weixinCountp.size()>0)?weixinCountp.size():0);
            dmap.put("androidCountp",(androidCountp!=null && androidCountp.size()>0)?androidCountp.size():0);
            dmap.put("iosCountp",(iosCountp!=null && iosCountp.size()>0)?iosCountp.size():0);
            courseButtonStatictisMapper.insert(dmap);
        }
    }


    private List<Map> findByUser( SearchRequestBuilder searchRequestBuilder , String userId , String date) {
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("createTime", date));
               // .must(QueryBuilders.matchPhraseQuery("button.keyword","025"));
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



    public Map getMap(SearchHit hit) {
        String json = hit.getSourceAsString();
        Map temp = JsonUtil.getObject(json ,  HashMap.class);
        return temp;
    }

}
