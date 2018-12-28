package com.longlian.console.task;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.CourseDetailsStaticsMapper;
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
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 课程详情 访问统计 停留时长
 * Created by admin on 2017/5/12.
 */
@Component("coursePageCountTask")
public class CoursePageCountTask extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(CoursePageCountTask.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    private  static final String livePageUrl = "/weixin/live/index.user";

  /*  private static final String[] courseUrlArray={
            "/course/getCourseInfo","/course/findCourseInfoById","/course/getCourseInfo.user",
            "/teacher/findCourseInfoById","/weixin/courseInfo", "weixin/teacherSeriesdetails"
    };*/
    @Value("${course.visit.url}")
    public String courseUrlArray;

    @Value("${live.visit.url}")
    public String liveUrlArray;
    @Autowired
    CourseDetailsStaticsMapper CourseDetailsStaticsMapper;

    private final static int pageSize = 500;

    @Override
    public String getTaskName() {
        return "课程详情用户行为访问统计2";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("课程详情用户行为访问统计2"+e.getMessage());
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

      //  Set<String> pCount=new HashSet<>();
      //  Map pageMap = redisUtil.hmgetAll(RedisKey.ll_page_url); //访问所有页面
        List<Map> dbMaps=new ArrayList<>();
        List<Map> liveMaps=new ArrayList<>();
      //  Map map=new TreeMap<>();
        for (String user : users) {
            if ("0".equals(user) || StringUtil.isEmpty(user)) {
                continue;
            }
            List<Map> result = this.findByUser(  searchRequestBuilder ,   user ,   date);  //统计当天页面 访问人数
            log.info("课程详情页面------------------------"+result.size()+"-------------------------------");
            for (int i = 0 ; i < result.size() ;i++) {   //遍历用户从直播间取出数据集合  统计课程详情页面
                Map temp = result.get(i);
                String url = (String)temp.get("url");  //获取访问页面URL
                if (url == null) {
                    url = (String)temp.get("uri");
                }
                Long visitTime = (Long)temp.get("visitTime");
                String[] cUrlArray = courseUrlArray.split(",");
                for(String curl:cUrlArray){
                    if(curl.equals(url)){  //判断该页面是否是进入课程详情页面；
                    /**----------直播页面统计开始---------*/
                        if (i == result.size() - 1) {
                            //页面直接退出；
                        }else{
                        Map tempNext = result.get(i + 1);   //获取下一个页面数据
                        Long nextPageVisitTime = (Long)tempNext.get("visitTime");  //下一个页面访问时间
                        Long timeDiff = nextPageVisitTime - visitTime;//时间差  访问前一个页面的停留时间
                        allTime += timeDiff;
                        boolean flag=false;
                        if(dbMaps!=null && dbMaps.size()>0){
                            for (Map dbMap:dbMaps){
                                if(dbMap!=null && dbMap.get("courseId")!=null && dbMap.get("courseId").equals(temp.get("courseId"))){
                                    dbMap.put("courseId",temp.get("courseId"));
                                    Object time = dbMap.get("totalTime");
                                    dbMap.put("totalStayTime",Long.parseLong((time!=null)?time+"":"0")+timeDiff);
                                    dbMap.put("visitCount",Long.parseLong(dbMap.get("visitCount")+"")+1);
                                    dbMap.put("visitDate", date);
                                    Set set= (Set) dbMap.get("pCount");
                                    set.add(user);
                                    dbMap.put("pCount",set);
                                    if( SystemUtil.clientType[0].equals(temp.get("clientType")+"")){//ios+1
                                        dbMap.put("iosCountc", Long.parseLong(dbMap.get("iosCountc") + "") + 1);

                                        Set iospSet= (Set) dbMap.get("iosCountp");
                                        iospSet.add(temp.get("userId") + "");
                                        dbMap.put("iosCountp", iospSet);
                                        dbMap.put("iosStayTime",Long.parseLong(dbMap.get("iosStayTime")+"")+timeDiff);
                                    }else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {//android +1
                                        dbMap.put("androidCountc",Long.parseLong(dbMap.get("androidCountc") + "") + 1);
                                        Set androidpSet= (Set) dbMap.get("androidCountp");
                                        androidpSet.add(temp.get("userId") + "");
                                        dbMap.put("androidCountp", androidpSet);
                                        dbMap.put("androidStayTime",Long.parseLong(dbMap.get("androidStayTime")+"")+timeDiff);
                                    }else /*if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/ {//weixin +1
                                        dbMap.put("weixinCountc", Long.parseLong(dbMap.get("weixinCountc") + "") + 1);
                                        Set weixinpSet= (Set) dbMap.get("weixinCountp");
                                        weixinpSet.add(temp.get("userId") + "");
                                        dbMap.put("weixinCountp", weixinpSet);
                                        dbMap.put("weixinStayTime",Long.parseLong(dbMap.get("weixinStayTime")+"")+timeDiff);
                                    }
                                    flag=true;
                                    break;
                                }
                            }
                        }
                        if(!flag){  //在dbMaps中没有找到数据
                            Map mapv=new TreeMap<>();
                            mapv.put("courseId",temp.get("courseId"));
                            mapv.put("totalStayTime",timeDiff);
                            mapv.put("visitCount",1);
                            mapv.put("visitDate",date);
                            Set count =new HashSet<>();
                            count.add(user);
                            mapv.put("pCount", count);

                            mapv.put("iosCountc", 0);
                            mapv.put("iosStayTime",0);
                            mapv.put("androidCountc",0);
                            mapv.put("androidStayTime",0);
                            mapv.put("weixinCountc",0);
                            mapv.put("weixinStayTime",0);
                            mapv.put("iosCountp", new HashSet<>());
                            mapv.put("androidCountp", new HashSet<>());
                            mapv.put("weixinCountp",  new HashSet<>());
                            if (SystemUtil.clientType[0].equals(temp.get("clientType") + "")) {//ios+1
                                Set counti = new HashSet<>();
                                counti.add(user);
                                mapv.put("iosCountp", counti);
                                mapv.put("iosCountc",1);
                                mapv.put("iosStayTime",timeDiff);
                            } else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {  //android +1
                                Set counta = new HashSet<>();
                                counta.add(user);
                                mapv.put("androidCountp", counta);
                                mapv.put("androidCountc",1);
                                mapv.put("androidStayTime",timeDiff);
                            } else/* if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/{   //weixin +1
                                Set countw = new HashSet<>();
                                countw.add(user);
                                mapv.put("weixinCountp", countw);
                                mapv.put("weixinCountc",1);
                                mapv.put("weixinStayTime",timeDiff);
                            }
                            dbMaps.add(mapv);

                        }
                        break;
                    }
                }
                }
            }
        }
        for (Map dmap: dbMaps){
           Set set= (Set) dmap.get("pCount");
            dmap.put("pCount",set.size());
            Set weixinCountp= (Set) dmap.get("weixinCountp");
            Set androidCountp= (Set) dmap.get("androidCountp");
            Set iosCountp= (Set) dmap.get("iosCountp");
            dmap.put("weixinCountp",(weixinCountp!=null && weixinCountp.size()>0)?weixinCountp.size():0);
            dmap.put("androidCountp",(androidCountp!=null && androidCountp.size()>0)?androidCountp.size():0);
            dmap.put("iosCountp",(iosCountp!=null && iosCountp.size()>0)?iosCountp.size():0);
            dmap.put("type",0);
            CourseDetailsStaticsMapper.insert(dmap);
        }
     /*************************直播间详情统计*****start*********************************/
        for (String user : users) {
            if ("0".equals(user) || StringUtil.isEmpty(user)) {
                continue;
            }
            List<Map> result = this.findByUser(searchRequestBuilder ,   user ,   date);  //统计当天页面 访问人数
            for (int i = 0 ; i < result.size() ;i++) {   //遍历用户从直播间取出数据集合  统计课程详情页面
                Map temp = result.get(i);
                String url = (String)temp.get("url");  //获取访问页面URL
                if (url == null) {
                    url = (String)temp.get("uri");
                }
                Long visitTime = (Long)temp.get("visitTime");
                String[] cUrlArray = liveUrlArray.split(",");
                for(String curl:cUrlArray){
                    if(curl.equals(url)){  //判断该页面是否是进入课程详情页面；
                        /**----------直播页面统计开始-----------------*/
                        if (i == result.size() - 1) {
                            //页面直接退出；
                        }else{
                            Map tempNext = result.get(i + 1);   //获取下一个页面数据
                            Long nextPageVisitTime = (Long)tempNext.get("visitTime");  //下一个页面访问时间
                            Long timeDiff = nextPageVisitTime - visitTime;//时间差  访问前一个页面的停留时间
                            allTime += timeDiff;
                            boolean flag=false;
                            if(liveMaps!=null && liveMaps.size()>0){
                                for (Map dbMap:liveMaps){
                                    if(dbMap!=null && dbMap.get("courseId")!=null && dbMap.get("courseId").equals(temp.get("courseId"))){
                                        dbMap.put("courseId",temp.get("courseId"));
                                        Object time = dbMap.get("totalTime");
                                        dbMap.put("totalStayTime",Long.parseLong((time!=null)?time+"":"0")+timeDiff);
                                        dbMap.put("visitCount",Long.parseLong(dbMap.get("visitCount")+"")+1);
                                        dbMap.put("visitDate", date);
                                        Set set= (Set) dbMap.get("pCount");
                                        set.add(user);
                                        dbMap.put("pCount", set);

                                        if( SystemUtil.clientType[0].equals(temp.get("clientType")+"")){//ios+1
                                            dbMap.put("iosCountc", Long.parseLong(dbMap.get("iosCountc") + "") + 1);
                                            Set iospSet= (Set) dbMap.get("iosCountp");
                                            iospSet.add(temp.get("userId") + "");
                                            dbMap.put("iosCountp", iospSet);
                                            dbMap.put("iosStayTime",Long.parseLong(dbMap.get("iosStayTime")+"")+timeDiff);
                                        }else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {//android +1
                                            dbMap.put("androidCountc",Long.parseLong(dbMap.get("androidCountc") + "") + 1);
                                            Set androidpSet= (Set) dbMap.get("androidCountp");
                                            androidpSet.add(temp.get("userId") + "");
                                            dbMap.put("androidCountp", androidpSet);
                                            dbMap.put("androidStayTime",Long.parseLong(dbMap.get("androidStayTime")+"")+timeDiff);
                                        }else /*if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/ {//weixin +1
                                            dbMap.put("weixinCountc", Long.parseLong(dbMap.get("weixinCountc") + "") + 1);
                                            Set weixinpSet= (Set) dbMap.get("weixinCountp");
                                            weixinpSet.add(temp.get("userId") + "");
                                            dbMap.put("weixinCountp", weixinpSet);
                                            dbMap.put("weixinStayTime",Long.parseLong(dbMap.get("weixinStayTime")+"")+timeDiff);
                                        }
                                        flag=true;
                                        break;
                                    }
                                }
                            }
                            if(!flag) {  //在dbMaps中没有找到数据
                                Map mapv = new TreeMap<>();
                                mapv.put("courseId", temp.get("courseId"));
                                mapv.put("totalStayTime", timeDiff);
                                mapv.put("visitCount", 1);
                                mapv.put("visitDate", date);
                                Set count = new HashSet<>();
                                count.add(user);
                                mapv.put("pCount", count);
                                /** -------------start  避免异常初始化 部分参数  --------------*/
                                mapv.put("iosCountc", 0);
                                mapv.put("iosStayTime",0);
                                mapv.put("androidCountc",0);
                                mapv.put("androidStayTime",0);
                                mapv.put("weixinCountc",0);
                                mapv.put("weixinStayTime",0);
                                mapv.put("iosCountp", new HashSet<>());
                                mapv.put("androidCountp", new HashSet<>());
                                mapv.put("weixinCountp",  new HashSet<>());
                                if (SystemUtil.clientType[0].equals(temp.get("clientType") + "")) {//ios+1
                                    Set counti = new HashSet<>();
                                    counti.add(user);
                                    mapv.put("iosCountp", counti);
                                    mapv.put("iosCountc",1);
                                    mapv.put("iosStayTime",timeDiff);
                                } else if (SystemUtil.clientType[1].equals(temp.get("clientType") + "")) {  //android +1
                                    Set counta = new HashSet<>();
                                    counta.add(user);
                                    mapv.put("androidCountp", counta);
                                    mapv.put("androidCountc",1);
                                    mapv.put("androidStayTime",timeDiff);
                                } else/* if (SystemUtil.clientType[2].equals(temp.get("clientType") + ""))*/{   //weixin +1
                                    Set countw = new HashSet<>();
                                    countw.add(user);
                                    mapv.put("weixinCountp", countw);
                                    mapv.put("weixinCountc",1);
                                    mapv.put("weixinStayTime",timeDiff);
                              }
                                mapv.put("type",1);   //type 0:课程详情页面统计   1:直播间详情页面统计
                                liveMaps.add(mapv);
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (Map livemap: liveMaps){
            Set set= (Set) livemap.get("pCount");
            livemap.put("pCount",set.size());

            Set weixinCountp= (Set) livemap.get("weixinCountp");
            Set androidCountp= (Set) livemap.get("androidCountp");
            Set iosCountp= (Set) livemap.get("iosCountp");
            livemap.put("weixinCountp",(weixinCountp!=null && weixinCountp.size()>0)?weixinCountp.size():0);
            livemap.put("androidCountp",(androidCountp!=null && androidCountp.size()>0)?androidCountp.size():0);
            livemap.put("iosCountp",(iosCountp!=null && iosCountp.size()>0)?iosCountp.size():0);
            livemap.put("type",1);
            CourseDetailsStaticsMapper.insert(livemap);
        }

    }


    private List<Map> findByUser( SearchRequestBuilder searchRequestBuilder , String userId , String date) {
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("visitDate", date))
                .must(QueryBuilders.termQuery("userId", Long.parseLong(userId)));
                //.must(QueryBuilders.matchPhraseQuery("isPage" , "1"))

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
