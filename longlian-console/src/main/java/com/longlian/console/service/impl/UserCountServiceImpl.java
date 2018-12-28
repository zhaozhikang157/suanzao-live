package com.longlian.console.service.impl;

import com.google.common.collect.Maps;
import com.huaxin.util.*;
import com.huaxin.util.Token.User;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.*;
import com.longlian.console.service.UserCountService;
import com.longlian.console.util.SystemUtil;
import com.longlian.dto.CourseDto;
import com.longlian.live.dao.PageUrlMapper;
import com.longlian.live.dao.VisitCourseRecordMapper;
import com.longlian.live.util.ElasticsearchClient;
import com.longlian.model.Account;
import com.longlian.model.PageUrl;
import com.longlian.model.UserCount;
import com.longlian.type.Button;
import com.longlian.type.ButtonRefererType;
import com.longlian.type.CountType;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by admin on 2017/3/7.
 */
@Component("userCountService")
public class UserCountServiceImpl implements UserCountService {


    @Autowired
    UserCountMapper UserCountMapper;
    @Autowired
    PageUrlMapper pageUrlMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    VisitCourseRecordMapper visitCourseRecordMapper;
    @Autowired
    VisitCourseRecordStatMapper visitCourseRecordStatMapper;
    @Autowired
    ElasticsearchClient elasticsearchClient;
    @Autowired
    CourseButtonStatictisMapper courseButtonStatictisMapper;
    @Autowired
    CourseDetailStatictisMapper courseDetailStatictisMapper;
    @Value("${course.visit.url}")
    public String courseUrlArray;

    @Override
    public List<Map> getCountListForPage(DatagridRequestModel page,Map map) {

        List<Map> countListForPage = UserCountMapper.getCountListForPage(page, map);
        for (Map mapss:countListForPage){
            mapss.put("clRate",toPercent(new BigDecimal(Double.parseDouble(mapss.get("clRate")+""))));
            mapss.put("auplRate",toPercent(new BigDecimal(Double.parseDouble(mapss.get("auplRate")+""))));
        }
        return countListForPage;
    }
    private String toPercent(BigDecimal bd) {

        if (bd.compareTo(new BigDecimal(0)) == 0) {
            return "0.00%";
        }

        BigDecimal result =  bd.multiply(new BigDecimal(100));
        //四舍五入
        result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        return result.toString() + "%";
    }
    @Override
    @Transactional(readOnly = true)
    public List<Map> getUserCountPage(DataGridPage page, String machineType) {

        List<Map> userCoutLists = UserCountMapper.getUserCountPage(page, machineType);
        return userCoutLists;
    }

    @Override
    public void deleteById(long id) throws Exception {
        UserCountMapper.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public long getCountUser(Date start, Date end, int type)
    {
        String startt = DateUtil.format(start, "yyyy-MM-dd");
        String endd = DateUtil.format(end, "yyyy-MM-dd");
        return   UserCountMapper.getCountUser(startt, endd, type);
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getUserCountDouble(Date start, Date end, int type){
        String startt = DateUtil.format(start, "yyyy-MM-dd");
        String endd = DateUtil.format(end, "yyyy-MM-dd");
        return UserCountMapper.getUserCountDouble(startt, endd, type);
    }
    @Override
    public ActResult getLiveChannelUsingCount(String dayTime,String beginTime,String endTime) throws Exception{
        ActResult actResult=new ActResult();
        Map map = null;
        Map<String, String> redisMap =null;
        if(Utility.isNullorEmpty(dayTime) || dayTime.equals("0")) {
            redisMap = redisUtil.hmgetAll(RedisKey.ll_live_channel_using_counts + "_" + getDateStr());
            map = this.commonQueryChannelUsing(redisMap);
        }else {
            //(昨天)从数据库查询
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            if(!Utility.isNullorEmpty(beginTime) && !Utility.isNullorEmpty(endTime)){
                beginTime = dateStrSave+" "+beginTime;
                endTime=dateStrSave+" "+ endTime;
            }else
            {
                beginTime = dateStrSave+" "+"00:00";
                endTime=dateStrSave+" "+ "23:59";
            }
            List<UserCount> UserCountLists = UserCountMapper.getCountChannelusing(beginTime,endTime, Integer.valueOf(CountType.USING_LIVE_CHANNEL.getType()));
            redisMap = new HashMap<>();
            for (UserCount UserCount : UserCountLists) {
                redisMap.put(UserCount.getCountTime().substring(UserCount.getCountTime().indexOf(" "),UserCount.getCountTime().length()),UserCount.getCount().intValue()+"");
            }
            map = this.commonQueryChannelUsing(redisMap);
        }
        List<String> keys =(List)map.get("keys");
        List<String> values =(List)map.get("values");
        //条件查询
        if(!Utility.isNullorEmpty(beginTime) && !Utility.isNullorEmpty(endTime)){
            if(keys.contains(beginTime) && keys.contains(endTime)) {
                keys =  keys.subList(keys.indexOf(beginTime),keys.indexOf(endTime)+1);
                values.clear();
                for(String key:keys) {
                    values.add(redisMap.get(key));
                }
            }
        }


        map.put("keyList",keys);
        map.put("valueList",values);
        actResult.setData(map);
        return actResult;
    }

    /**
     * 查询正在使用直播里
     * @return
     */
    private   Map commonQueryChannelUsing(Map redisMap){
        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(redisMap.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String, String>   mapStr:infoIds){
            keys.add(mapStr.getKey());
            values.add(mapStr.getValue());
        }
        Map mapStr = new HashMap();
        mapStr.put("keys", keys);
        mapStr.put("values", values);
        return mapStr;
    }
    public  List<String> sortListDesc(List<String> list) throws ParseException{
        List<String> retStr=new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Map<Long,String> map = new TreeMap<Long, String>();
        for(int i=0;i<list.size();i++){
            String dateStr = list.get(i);
            map.put(sdf.parse(dateStr).getTime(), dateStr);
        }
        Collection<String> coll=map.values();
        retStr.addAll(coll);
        Collections.reverse(retStr);
        return retStr;
    }

    public Long getLongTime(Date Date)
    {
        System.out.println(Date.toString());   //java.util.Date的含义
        long lSysTime1 = Date.getTime() / 1000;   //得到秒数，Date类型的getTime()返回毫秒数
        return lSysTime1;
    }
    public  String longToDate(long currentTime, String formatType) throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        return sDateTime;
    }
    public  Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
    public  String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    private String getDateStr() throws Exception{
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return dateStr;
    }
    /**
     * 将短日期格式的String转化为Date类型
     *
     * @param
     * @return Date
     * @throws ParseException
     * @author MaChao
     */
    private String getDateHmStr(Date Date) {
        String dateStr = DateFormatUtils.format(Date, "HH:mm");
        return dateStr;
    }

    public static Date[] parseTimes(String beginTimeStr , String endTimeStr) {
        Date startTime = null;
        Date endTime = null;
        try {
            startTime = DateUtils.parseDate(beginTimeStr +" 00:00:00" , "yyyy-MM-dd HH:ss:mm");
            endTime = DateUtils.parseDate(endTimeStr +" 23:59:59" , "yyyy-MM-dd HH:ss:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date[]{startTime , endTime};
    }

    public static String[] getIntervalDateStr(String beginTime , String endTime) {
        Date[] dates = parseTimes(beginTime , endTime);
        List<String> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(dates[0]);

        Calendar e = Calendar.getInstance();
        e.setTime(dates[1]);

        do {
            //将时间转成date,并加入结果
            String date =  DateFormatUtils.format(c , "yyyy-MM-dd");
            list.add(date);
            //加一天
            c.add(Calendar.DAY_OF_MONTH, 1);
            //加一天，如果大于现在的endTime，说明超出，则跳出来
        } while(c.compareTo(e) < 1) ;


        return list.toArray(new String[]{});
    }

    /**
     * 停留时长
     * @param objectId
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public  ActResult getLengthOfStay(Long objectId,String beginTime,String endTime) throws Exception{
        ActResult result=new ActResult();
        Map map = new HashMap<>();
        if(objectId != null && objectId>0)  {
            //url
            Map urlMap = new HashMap<>();
            List<Map>  urlList =  UserCountMapper.getCountPageUrl(beginTime, endTime,objectId,CountType.PAGE_STAY_TIME.getType());
            for(Map map2 : urlList){
                urlMap.put(map2.get("countTime"),toMinLong((BigDecimal)map2.get("count")));
            }
            urlMap = this.commonQueryLengthOfStay(urlMap, beginTime , endTime);
            map.put("stayLengthMap",urlMap);
        }else  {
            //站点
            Map siteMap = new HashMap<>();
            List<Map> siteList=UserCountMapper.getCountSite(beginTime, endTime,CountType.PAGESITE_STAY_TIME.getType());
            for(Map map1 : siteList){
                siteMap.put(map1.get("countTime"),toMinLong((BigDecimal)map1.get("count")));
            }
            siteMap = this.commonQueryLengthOfStay(siteMap, beginTime , endTime);
            map.put("stayLengthMap",siteMap);
        }

        result.setData(map);
        return result;
    }

    /**
     * 转秒
     * @param value
     * @return
     */
    public long toMinLong(BigDecimal value) {
        return value.divide(new BigDecimal(1000L)).longValue();
    }

    /**
     * 退出率
     * @param objectId
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public  ActResult getExitRate(Long objectId,String beginTime,String endTime) throws Exception{
        Map map = new HashMap<>();
        ActResult result=new ActResult();

        List<Map>  exitRateList =  UserCountMapper.getCountPageUrl(beginTime, endTime, objectId, CountType.PAGE_EXIT_RATE.getType());
        Map exitRateMap = new HashMap<>();

        for(Map map1 : exitRateList){
            BigDecimal bigDecimal = new BigDecimal(map1.get("count").toString());
            exitRateMap.put(map1.get("countTime"),bigDecimal.multiply(new BigDecimal(100)));
        }
        exitRateMap = this.commonQueryLengthOfStay(exitRateMap, beginTime , endTime);
        map.put("exitRateMap",exitRateMap);
        result.setData(map);
        return result;
    }

    /**
     * 访问数
     * @param objectId
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public   ActResult getVisit(Long objectId,String beginTime,String endTime) throws Exception{
        Map map = new HashMap<>();
        ActResult result=new ActResult();

        if(objectId != null && objectId>0){
            //pageUrl
            List<Map>  pageVisitCountList =  UserCountMapper.getCountPageUrl(beginTime, endTime, objectId, CountType.PAGE_VISIT_COUNT.getType());  //页面综合访问数 pv
            List<Map>  pageVisitUserCountList =  UserCountMapper.getCountPageUrl(beginTime, endTime, objectId, CountType.PAGE_VISIT_USER_COUNT.getType());  //页面访问人数 uv
            Map pageVisitCountMap = new HashMap<>();
            if(pageVisitCountList.size()>0){
                for(Map map1 : pageVisitCountList){
                    pageVisitCountMap.put(map1.get("countTime"),((BigDecimal)map1.get("count")).longValue());
                }
            }

            pageVisitCountMap = this.commonQueryPv(pageVisitCountMap, beginTime , endTime);
            map.put("pvMap",pageVisitCountMap); //pv

            Map pageVisitUserCountMap = new HashMap<>();
            for(Map map1 : pageVisitUserCountList){
                pageVisitUserCountMap.put(map1.get("countTime"),((BigDecimal)map1.get("count")).longValue());
            }
            pageVisitUserCountMap =  this.commonQueryUv(pageVisitUserCountMap, beginTime , endTime);
            map.put("uvMap",pageVisitUserCountMap); //uv

        }else {
            //站点
            List<Map> siteVisitCountList=UserCountMapper.getCountSite(beginTime, endTime, CountType.PAGESITE_VISIT_COUNT.getType());   // 站点综合访问数 pv
            List<Map> siteVisitUserCountList=UserCountMapper.getCountSite(beginTime, endTime, CountType.PAGESITE_VISIT_USER_COUNT.getType());   // 站点访问数 uv
            Map siteVisitCountMap = new HashMap<>();
            for(Map map1 : siteVisitCountList){
                siteVisitCountMap.put(map1.get("countTime"),((BigDecimal)map1.get("count")).longValue());
            }
            siteVisitCountMap = this.commonQueryPv(siteVisitCountMap, beginTime , endTime);
            map.put("pvMap",siteVisitCountMap); //pv

            Map siteVisitUserCountMap = new HashMap<>();
            for(Map map1 : siteVisitUserCountList){
                siteVisitUserCountMap.put(map1.get("countTime"),((BigDecimal)map1.get("count")).longValue());
            }
            siteVisitUserCountMap = this.commonQueryUv(siteVisitUserCountMap, beginTime , endTime);
            map.put("uvMap",siteVisitUserCountMap); //uv
        }
        result.setData(map);
        return result;
    }

    @Override
    public ActResult getPageHandleTime(Long objectId, String beginTime, String endTime) throws Exception {

        Map map = new HashMap<>();
        ActResult result=new ActResult();

        List<Map>  pageHandleTimeCountList =  UserCountMapper.getCountPageUrl(beginTime, endTime, objectId, CountType.PAGE_HANDLE_TIME_AVG.getType());  //页面执持平均时长
        Map pageVisitCount = new HashMap<>();
        if(pageHandleTimeCountList.size()>0){
            for(Map map1 : pageHandleTimeCountList){
                pageVisitCount.put(map1.get("countTime"),((BigDecimal)map1.get("count")).longValue());
            }
        }
        pageVisitCount =  resultConvert(pageVisitCount , beginTime ,  endTime , "values" , "keys");
        result.setData(pageVisitCount);
        return result;
    }

    /**
     * 按钮点击次数
     * @param beginTime
     * @param endTime
     * @param objectValue
     * @return
     * @throws Exception
     */
    public ActResult getButtonClick(String beginTime,String endTime,String objectValue) throws Exception{
        ActResult result=new ActResult();
        //补全日期
        String[] intervalDates = getIntervalDateStr(beginTime ,endTime);

        List timeList = new ArrayList<>();

        Button parentButton = Button.getButtonByType(objectValue);
        List<ButtonRefererType> buttonRefererTypes = ButtonRefererType.getListByParent(objectValue);
        //便于后面填充没有数据的buttonRef
        Set<String> noDataButtonRef = new HashSet<>();
        //用于后面返回相应的buttonRef
        Map<String , String> buttonRefInfo = new HashMap<>();
        if (parentButton.isHasChild()) {
            for (ButtonRefererType bu : buttonRefererTypes) {
                noDataButtonRef.add(bu.getType());
                buttonRefInfo.put(bu.getType(), bu.getName());
            }
        }
        buttonRefInfo.put(parentButton.getType(), parentButton.getName() );

        List<Map>  buttonClickList   =  UserCountMapper.getButtonClick(beginTime, endTime, CountType.BUTTON_CLICK_COUNT.getType(), objectValue);

        Map<String , Long> parentMap = new HashMap<>();

        //归类
        Map<String , Map> sortOut = new HashMap();
        for(int i = 0;i < buttonClickList.size(); i++){
            Map countMap =  buttonClickList.get(i);
            String obvKey = (String)countMap.get("objectValue");
            Map<String , Long> valuesMap = sortOut.get(obvKey);
            if (valuesMap == null) {
                valuesMap = new HashMap();
                sortOut.put(obvKey , valuesMap);
            }
            Long value = ((BigDecimal)countMap.get("count")).longValue();
            String time = (String)countMap.get("countTime");
            valuesMap.put(time,value);
            //是否有下级,有的话需要算父级总合
            if (parentButton.isHasChild()) {
                Long temp = parentMap.get(time);
                //第一个
                if (temp == null) {
                    parentMap.put(time , value);
                } else {
                    //原来有了，加起来
                    parentMap.put(time , temp.longValue() + value.longValue());
                }
                //有的话，移出，便于后面填充没有的ButtonRef
                noDataButtonRef.remove(obvKey);
            }
        }

        //如果子级的情况 ，值 是被 算出来的，应该加到sortOut里面
        //如果是没有子级的情况 ，值 是被 查出来的，不应该加到sortOut（本来就有）里面，
        if (parentButton.isHasChild()) {
            sortOut.put(parentButton.getType()  , parentMap);
        }

        //填充没有的子ButtonRefererType
        for (String temp : noDataButtonRef) {
            sortOut.put(temp , new HashMap<String , Long>());
        }

        //组装结果
        Map map = new HashMap();
        List<Map> res = new ArrayList<>();
        for (String key : sortOut.keySet()) {
            Map temp = new HashMap();
            temp.put("objKey", key);
            temp.put("name" , buttonRefInfo.get(key));

            Map<String , Long> valuesMap = sortOut.get(key);
            List<Long> vals = new ArrayList<>();
            for (String date : intervalDates) {
                //填充没有的日期
                if (!valuesMap.containsKey(date)) {
                    vals.add(0L);
                } else {
                    //将数据写入数组
                    vals.add(valuesMap.get(date));
                }
            }
            temp.put("values", vals);
            res.add(temp);
        }
        map.put("keys" , intervalDates);
        map.put("datas" , res);
        result.setData(map);
        return  result;
    }

    @Override
    public List<PageUrl> getUrls() {
        return pageUrlMapper.selectAll();
    }

    @Override
    public ActResult getButtonClickDetail(String objectValue , String date) {
        String buttonName = "";
        //是否根据机器类型统计
        boolean isFindMachine = false;
        //是子按扭
        if (objectValue.length() > 3) {
            isFindMachine = true;
            ButtonRefererType bs = ButtonRefererType.getButtonRefererType(objectValue);
            buttonName = bs.getName();
        } else {
            Button parentButton = Button.getButtonByType(objectValue);
            if (!parentButton.isHasChild()) {
                isFindMachine = true;
            }
            buttonName = parentButton.getName();
        }
        List<Map> result = new ArrayList();
        if (isFindMachine) {
            List<UserCount> list =  UserCountMapper.getButtonClickStatMachine(date, CountType.BUTTON_CLICK_COUNT.getType(), objectValue);
            //处理三个端的和小于总合计的情况，应该有一个其它的记录
            //计算三个端的和
            BigDecimal sum = new BigDecimal(0);
            //本来的合计
            BigDecimal count = null;
            for (UserCount uc : list) {
                //如果机器类型为空说明是，本来的合计
                if (StringUtils.isEmpty(uc.getMachineType())) {
                    count = uc.getCount();
                    //将有机器类型的加入到结果集中
                } else {
                    Map map = new HashMap();
                    sum =  sum.add(uc.getCount());
                    map.put("name", uc.getMachineType());
                    map.put("value" , uc.getCount() != null ? uc.getCount().longValue() : 0);
                    result.add(map);
                }
            }
            //处理三个端的和小于总合计的情况，应该有一个其它的记录
            if (count != null && sum.compareTo(count) < 0 ) {
                Map map = new HashMap();
                map.put("name", "其它");
                map.put("value" , (count.subtract(sum)).longValue());
                result.add(map);
            }
        } else {
            List<UserCount> list =  UserCountMapper.getButtonClickStatChild(date, CountType.BUTTON_CLICK_COUNT.getType(), objectValue);
            for (UserCount uc : list) {
                Map map = new HashMap();
                ButtonRefererType bs = ButtonRefererType.getButtonRefererType(uc.getObjectValue());
                String name = uc.getObjectValue();
                if (bs != null) {
                    name = bs.getName();
                }
                map.put("name", name);
                map.put("value" , uc.getCount() != null ? uc.getCount().longValue() : 0);
                result.add(map);
            }
        }
        ActResult ac = new ActResult();
        ac.setData(result);
        ac.setMsg(buttonName);
        return ac;
    }


    private   Map commonQueryLengthOfStay(Map redisMap , String beginTime , String endTime){
        return resultConvert(  redisMap ,   beginTime ,   endTime ,   "keys" , "values");
    }

    /**
     * 将结果转化成前台相应的格式
     * @param redisMap
     * @param beginTime
     * @param endTime
     * @param kkey
     * @param valueKey
     * @return
     */
    private   Map resultConvert(Map redisMap , String beginTime , String endTime , String kkey , String valueKey){
        //补全日期
        String[] intervalDates = getIntervalDateStr(beginTime ,endTime);

        for (String date : intervalDates) {
            if (!redisMap.containsKey(date)) {
                redisMap.put(date , "0");
            }
        }

        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(redisMap.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String, String>   mapStr:infoIds){
            keys.add(mapStr.getKey());
            values.add(mapStr.getValue());
        }
        Map mapStr = new HashMap();
        mapStr.put(kkey, keys);
        mapStr.put(valueKey, values);
        return mapStr;
    }


    private   Map commonQueryPv(Map redisMap, String beginTime , String endTime){
        return resultConvert(  redisMap ,   beginTime ,   endTime ,   "pvKeys" , "pvValues");
    }
    private   Map commonQueryUv(Map redisMap, String beginTime , String endTime){
        return resultConvert(  redisMap ,   beginTime ,   endTime ,   "uvKeys" , "uvValues");
    }

    public Map getMap(SearchHit hit) {
        String json = hit.getSourceAsString();
        Map temp = JsonUtil.getObject(json, HashMap.class);
        return temp;
    }
    private final static int pageSize = 500;
    @Override
    public Map getDateRangeMap(Map requestMap) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        //String defaultStartDate = sdf.format(nowDate);    //格式化前一天
        try{
            if("0".equals(requestMap.get("dateRange"))){
                // String format = DateUtil.format(nowDate,"yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance(); //得到日历
                calendar.setTime(new Date());//把当前时间赋给日历
                calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                Date nowDate = calendar.getTime();//得到前一天的时间
                requestMap.put("beginTime",sdf.format(nowDate));
                // requestMap.put("endTime",sdf.parse(format+" 23:59:59").getTime());
            }else if("1".equals(requestMap.get("dateRange"))){  //每周
                Map calendarWeek = DateUtil.getCalendarWeek();
                requestMap.put("beginTime",calendarWeek.get("mondayDate")+" 00:00:00");
                requestMap.put("endTime",calendarWeek.get("sundayDate")+" 23:59:59");
            }else{  //每月
                Map calendarMonth = DateUtil.getCalendarMonth();
                requestMap.put("beginTime",calendarMonth.get("firstday")+" 00:00:00");
                requestMap.put("endTime",calendarMonth.get("lastday")+" 23:59:59");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Map responseMap = courseDetailStatictisMapper.getCourseDatasRange(requestMap);
        if(responseMap!=null && responseMap.get("totalStayTime")!=null){
            responseMap.put("totalStayTime",SystemUtil.secondsToHoursMin(Long.parseLong(responseMap.get("totalStayTime")+"")));
        }
        return responseMap;
    }

    @Override
    public Map getClickDateRangeMap(Map requestMap) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        //String defaultStartDate = sdf.format(nowDate);    //格式化前一天
        try{
            if("0".equals(requestMap.get("dateRange"))){  //每日
                // String format = DateUtil.format(nowDate,"yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance(); //得到日历
                calendar.setTime(new Date());//把当前时间赋给日历
                calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                Date nowDate = calendar.getTime();//得到前一天的时间
                requestMap.put("beginTime",sdf.format(nowDate));
                // requestMap.put("endTime",sdf.parse(format+" 23:59:59").getTime());
            }else if("1".equals(requestMap.get("dateRange"))){  //每周
                Map calendarWeek = DateUtil.getCalendarWeek();
                requestMap.put("beginTime",calendarWeek.get("mondayDate")+" 00:00:00");
                requestMap.put("endTime",calendarWeek.get("sundayDate")+" 23:59:59");
            }else{  //每月
                Map calendarMonth = DateUtil.getCalendarMonth();
                requestMap.put("beginTime",calendarMonth.get("firstday")+" 00:00:00");
                requestMap.put("endTime",calendarMonth.get("lastday")+" 23:59:59");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        requestMap.put("courseButton",Button.BUY_COURSE_BUTTON.getType());//购买课程按钮
        requestMap.put("playButton", Button.PLAY_CLICK_BUTTON.getType()); //播放按钮
        requestMap.put("outButton", Button.CDETAIL_OUT_BUTTON.getType());  //退出按钮
        List<Map> respMaps=courseButtonStatictisMapper.getCourseDatasRange(requestMap);

        //获取指定时间访问次数；
        Map responseMap = courseDetailStatictisMapper.getCourseDatasRange(requestMap);
        Object vCount = responseMap.get("vCount"); //页面访问次数
        Map respMap=new HashMap<>();
        String courseButtonCount= respMaps.get(0).get("vCount")+"";
        String playButtonCount= respMaps.get(1).get("vCount")+"";
        String outButtonCount= respMaps.get(2).get("vCount")+"";
        if(!"0".equals(vCount+"")){
            BigDecimal v=new BigDecimal(vCount+"");
            respMap.put("courseButtonCountStr",courseButtonCount+"("+(new BigDecimal(courseButtonCount).divide(v,2,BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal("100"))+"%)");  //购买课程 按钮点击量
            respMap.put("playButtonCountStr",playButtonCount+"("+new BigDecimal(playButtonCount).divide(v, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))+"%)");
            respMap.put("outButtonCountStr",outButtonCount+"("+new BigDecimal(outButtonCount).divide(v, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))+"%)");
        }else{
            respMap.put("courseButtonCountStr",courseButtonCount+"(0%)");  //购买课程 按钮点击量
            respMap.put("playButtonCountStr",playButtonCount+"(0%)");
            respMap.put("outButtonCountStr",outButtonCount+"(0%)");
        }

        return respMap;
    }

    /**
     * @Author:liuna
     * @param requestModel
     * @param map
     * @return
     */
    @Override
    public List<Map> getCourseDetailDataStatistics(DatagridRequestModel requestModel, Map map) {
        map.put("startIndex",requestModel.getOffset());map.put("pageSize",requestModel.getLimit());

        List<Map> maps = courseMapper.getDatasListPage(requestModel, map);
        try {
            requestModel.setTotal(Integer.parseInt(courseMapper.getListTotalCount(map).get("totalCount") + ""));
            for (Map mapv :maps){
                //平均时长
                //   if(mapv.get("totalStayTime")!=null) {
                //根据端口判断 来自类型
                if ("1".equals(map.get("portType") + "")) {   //ios
                    mapv.put("vCount", mapv.get("iosCountc"));
                    mapv.put("pCount", mapv.get("iosCountp"));
                    // if(mapv.get("iosStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("iosStayTime") == null ? "0" : mapv.get("iosStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("iosStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                } else if ("2".equals(map.get("portType") + "")) {//android
                    mapv.put("vCount", mapv.get("androidCountc"));
                    mapv.put("pCount", mapv.get("androidCountp"));
                    //  if(mapv.get("androidStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("androidStayTime") == null ? "0" : mapv.get("androidStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("androidStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                    //  }
                    // mapv.put("totalStayTimeStr",mapv.get("androidStayTime"));
                } else if ("3".equals(map.get("portType") + "")) {//weixin
                    mapv.put("vCount", mapv.get("weixinCountc"));
                    mapv.put("pCount", mapv.get("weixinCountp"));
                    //  if(mapv.get("weixinStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("weixinStayTime") == null ? "0" : mapv.get("weixinStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("weixinStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                    //  }
                }else{
                    BigDecimal tt = new BigDecimal(mapv.get("totalStayTime")==null?"0": mapv.get("totalStayTime")+"");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = tt.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("totalStayTime") + "")));
                    }else{
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                }
            }
            //   }
            return maps;

        } catch (Exception e) {
            e.printStackTrace();
        }
        /** -----------------------------end-------------------------  */
        return null;
    }

    public static  void main(String[]args){
        String str= "3731742";
    }
    @Override
    public List<Map> getCourseDetailClickDataStatistics(DatagridRequestModel requestModel, Map map) {
        map.put("startIndex",requestModel.getOffset());map.put("pageSize",requestModel.getLimit());
        map.put("buttonType",Button.BUY_COURSE_BUTTON.getType());
        List<Map> maps = courseMapper.getButtonDatasBListPg(requestModel, map);
        try {
            requestModel.setTotal(Integer.parseInt(courseMapper.getListTotalCount(map).get("totalCount") + ""));
            for (Map mapv: maps){
                mapv.put("portType",map.get("portType"));
                BigDecimal  visitClickCount=BigDecimal.ZERO;
                BigDecimal buyCourseClickRet=BigDecimal.ZERO;

                BigDecimal  visitpClickCount=BigDecimal.ZERO;
                BigDecimal buyCoursepClickRet=BigDecimal.ZERO;

                BigDecimal  visiteClickCount=BigDecimal.ZERO;
                BigDecimal buyCourseeClickRet=BigDecimal.ZERO;
                if(mapv.get("vCount")!=null){
                    BigDecimal vCount=new BigDecimal(mapv.get("vCount")+"");   //页面总访问次数
                    /** ----start  ----*/
                    if("1".equals(map.get("portType")+"")){   //ios
                        visitClickCount =new BigDecimal(mapv.get("iosCountc")==null?"0":mapv.get("iosCountc")+"");  //购买课程
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//课程点击率
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /** 播放按钮点击数率   end */
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** 退出按钮点击数率   end */
                    }else if("2".equals(map.get("portType")+"")){//android
                        visitClickCount=new BigDecimal(mapv.get("androidCountc")==null?"0":mapv.get("androidCountc")+"");
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("androidpCountc")==null?"0":mapv.get("androidpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /** ---------------- end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("androideCountc")==null?"0":mapv.get("androideCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** --------------- end -----------------*/
                    }else if("3".equals(map.get("portType")+"")){//weixin
                        visitClickCount=new BigDecimal(mapv.get("weixinCountc")==null?"0":mapv.get("weixinCountc")+"");
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("weixinpCountc")==null?"0":mapv.get("weixinpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /**-----------   end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("weixineCountc")==null?"0":mapv.get("weixineCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** -------------   end-------------- */
                    }else /*if(mapv.get("visitClickCount")!=null)*/{

                          visitClickCount=new BigDecimal(mapv.get("visitClickCount")==null?"0":mapv.get("visitClickCount")+"");
                         buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("visitpClickCount")==null?"0":mapv.get("visitpClickCount")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /**-----------   end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("visiteClickCount")==null?"0":mapv.get("visiteClickCount")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** -------------   end-------------- */
                    }
                }else{
                    /** ----start  ----*/
                    if("1".equals(map.get("portType")+"")){   //ios
                        visitClickCount =new BigDecimal(mapv.get("iosCountc")==null?"0":mapv.get("iosCountc")+"");  //购买课程
                        buyCourseClickRet=new BigDecimal("0.00");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=new BigDecimal("0.00");
                        /** 退出按钮点击数率   end */
                        visiteClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=new BigDecimal("0.00");

                    }else if("2".equals(map.get("portType")+"")){//android
                        visitClickCount=new BigDecimal(mapv.get("androidCountc")==null?"0":mapv.get("androidCountc")+"");
                        buyCourseClickRet = new BigDecimal("0.00");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("androidpCountc")==null?"0":mapv.get("androidpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=new BigDecimal("0.00");
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("androideCountc")==null?"0":mapv.get("androideCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=new BigDecimal("0.00");
                        /** --------------- end -----------------*/
                    }else if("3".equals(map.get("portType")+"")){//weixin
                        visitClickCount=new BigDecimal(mapv.get("weixinCountc")==null?"0":mapv.get("weixinCountc")+"");
                        buyCourseClickRet = new BigDecimal("0.00");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("weixinpCountc")==null?"0":mapv.get("weixinpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=new BigDecimal("0.00");
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("weixineCountc")==null?"0":mapv.get("weixineCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=new BigDecimal("0.00");
                        /** -------------   end-------------- */
                    }else /*if(mapv.get("visitClickCount")!=null)*/{

                        visitClickCount=new BigDecimal(mapv.get("visitClickCount")==null?"0":mapv.get("visitClickCount")+"");
                        buyCourseClickRet = new BigDecimal("0.00");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("visitpClickCount")==null?"0":mapv.get("visitpClickCount")+"");  //播放按钮点击次数
                        buyCoursepClickRet=new BigDecimal("0.00");
                        /**-----------   end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("visiteClickCount")==null?"0":mapv.get("visiteClickCount")+"");  //退出按钮点击次数
                        buyCourseeClickRet=new BigDecimal("0.00");
                        /** -------------   end-------------- */
                    }
                }
                mapv.put("buyCourseClickRet",buyCourseClickRet+"%");
                mapv.put("visitClickCount",visitClickCount);
                mapv.put("visitClickCount",visitClickCount);   //端口查询  指定类型  点击次数显示指定类型
                mapv.put("visitpClickCount",visitpClickCount);  //播放按钮点击次数
                mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                mapv.put("visiteClickCount",visiteClickCount);   //退出按钮点击次数
                mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");
            }
            return maps;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ExportExcelWhaUtil importButtonCountExcel(HttpServletRequest req, HttpServletResponse response,Map map) {
        List<Map> maps = courseMapper.getButtonDatasBList(map);   //无需分页 查询 所有数据
        try {
            for (Map mapv: maps){
                if("0".equals(mapv.get("seriesCourseId")+"")){ //单节课
                    if(!"0".equals(mapv.get("seriesCourseId"))){  //系列课下的单节课
                        mapv.put("courseTypeStr","系列课" + mapv.get("seriesCourseId") + "的单节课");
                    }else{  //单节课
                        mapv.put("courseTypeStr","单节课");
                    }
                }else{
                    mapv.put("courseTypeStr","系列课");
                }
                mapv.put("portType",map.get("portType"));
                BigDecimal  visitClickCount=BigDecimal.ZERO;
                BigDecimal buyCourseClickRet=BigDecimal.ZERO;

                BigDecimal  visitpClickCount=BigDecimal.ZERO;
                BigDecimal buyCoursepClickRet=BigDecimal.ZERO;

                BigDecimal  visiteClickCount=BigDecimal.ZERO;
                BigDecimal buyCourseeClickRet=BigDecimal.ZERO;
                if(mapv.get("vCount")!=null){
                    BigDecimal vCount=new BigDecimal(mapv.get("vCount")+"");   //页面总访问次数
                    if("1".equals(map.get("portType")+"")){   //ios
                        visitClickCount =new BigDecimal(mapv.get("iosCountc")==null?"0":mapv.get("iosCountc")+"");  //购买课程
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//课程点击率
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /** 播放按钮点击数率   end */
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("iospCountc")==null?"0":mapv.get("iospCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** 退出按钮点击数率   end */
                    }else if("2".equals(map.get("portType")+"")){//android
                        visitClickCount=new BigDecimal(mapv.get("androidCountc")==null?"0":mapv.get("androidCountc")+"");
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");
                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("androidpCountc")==null?"0":mapv.get("androidpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /** ---------------- end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("androideCountc")==null?"0":mapv.get("androideCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** --------------- end -----------------*/
                    }else if("3".equals(map.get("portType")+"")){//weixin
                        visitClickCount=new BigDecimal(mapv.get("weixinCountc")==null?"0":mapv.get("weixinCountc")+"");
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("weixinpCountc")==null?"0":mapv.get("weixinpCountc")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /**-----------   end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("weixineCountc")==null?"0":mapv.get("weixineCountc")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** -------------   end-------------- */
                    }else /*if(mapv.get("visitClickCount")!=null)*/{

                        visitClickCount=new BigDecimal(mapv.get("visitClickCount")==null?"0":mapv.get("visitClickCount")+"");
                        buyCourseClickRet = visitClickCount.divide(vCount, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        mapv.put("buyCourseClickRet",buyCourseClickRet+"%");

                        /** 播放按钮点击数率   start */
                        visitpClickCount=new BigDecimal(mapv.get("visitpClickCount")==null?"0":mapv.get("visitpClickCount")+"");  //播放按钮点击次数
                        buyCoursepClickRet=visitpClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCoursepClickRet",buyCoursepClickRet+"%");
                        /**-----------   end ---------------*/
                        /** 退出按钮点击数率   start */
                        visiteClickCount=new BigDecimal(mapv.get("visiteClickCount")==null?"0":mapv.get("visiteClickCount")+"");  //退出按钮点击次数
                        buyCourseeClickRet=visiteClickCount.divide(vCount,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));//计算播放按钮点击率 *100%
                        mapv.put("buyCourseeClickRet",buyCourseeClickRet+"%");  //退出按钮
                        /** -------------   end-------------- */
                    }
                }
                mapv.put("visitClickCountStr",visitClickCount+"("+buyCourseClickRet+"%)");   //端口查询  指定类型  点击次数显示指定类型
                mapv.put("visitpClickCountStr",visitpClickCount+"("+buyCoursepClickRet+"%)");  //播放按钮点击次数
                mapv.put("visiteClickCountStr",visiteClickCount+"("+buyCourseeClickRet+"%)");   //退出按钮点击次数
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "课程页面按钮点击数统计";
        listStr.add(top1);

        String top3 = "序号,课程ID,课程名称,课程类型,讲课老师,开课时间,访问次数," +
                "购买课程点击次数(点击率),观看点击次数(点击率),返回点击次数(点击率)";
        listStr.add(top3);
        List<ExcelTop> exceltitel = ExcelUtil.getExceltitel(listStr);
        String keys = "id,liveTopic,courseTypeStr,appUserName,startTime,vCount," +
                "visitClickCountStr,visitpClickCountStr,visiteClickCountStr";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "课程详情按钮统计", req, exceltitel);
        return excel;
    }

    @Override
    public ExportExcelWhaUtil importCoursePageCountExcel(HttpServletRequest req, HttpServletResponse response, Map map) {
        List<Map> maps = courseMapper.getDatasListPage(null, map);
        try {
            for (Map mapv :maps){
                //平均时长
                //   if(mapv.get("totalStayTime")!=null) {
                //根据端口判断 来自类型
                if ("1".equals(map.get("portType") + "")) {   //ios
                    mapv.put("vCount", mapv.get("iosCountc"));
                    mapv.put("pCount", mapv.get("iosCountp"));
                    // if(mapv.get("iosStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("iosStayTime") == null ? "0" : mapv.get("iosStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("iosStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                } else if ("2".equals(map.get("portType") + "")) {//android
                    mapv.put("vCount", mapv.get("androidCountc"));
                    mapv.put("pCount", mapv.get("androidCountp"));
                    //  if(mapv.get("androidStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("androidStayTime") == null ? "0" : ("androidStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("androidStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                    //  }
                    // mapv.put("totalStayTimeStr",mapv.get("androidStayTime"));
                } else if ("3".equals(map.get("portType") + "")) {//weixin
                    mapv.put("vCount", mapv.get("weixinCountc"));
                    mapv.put("pCount", mapv.get("weixinCountp"));
                    //  if(mapv.get("weixinStayTime")!=null){
                    BigDecimal ts = new BigDecimal(mapv.get("weixinStayTime") == null ? "0" : mapv.get("weixinStayTime") + "");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = ts.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("weixinStayTime") + "")));
                    } else {
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                    //  }
                }else{
                    BigDecimal tt = new BigDecimal(mapv.get("totalStayTime")==null?"0": mapv.get("totalStayTime")+"");
                    if (mapv.get("vCount") != null && !"0".equals(mapv.get("vCount")+"")) {
                        BigDecimal avgStayTime = tt.divide(new BigDecimal(mapv.get("vCount") + ""), 2);
                        String s = SystemUtil.secondsToHoursMin(avgStayTime.longValue());
                        mapv.put("avgStayTimeStr", s);
                        mapv.put("totalStayTimeStr", SystemUtil.secondsToHoursMin(Long.parseLong(mapv.get("totalStayTime") + "")));
                    }else{
                        mapv.put("avgStayTimeStr", 0);
                        mapv.put("totalStayTimeStr", 0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /** -----------------------------end-------------------------  */
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "课程详情页面时长统计";
        if("1".equals(map.get("type")+"")){
            top1="直播间详情页面时长统计";
        }
        listStr.add(top1);

        String top3 = "序号,课程ID,课程名称,课程类型,讲课老师,开课时间,停留时长," +
                "访问人数,访问次数,人均停留时长";
        listStr.add(top3);
        List<ExcelTop> exceltitel = ExcelUtil.getExceltitel(listStr);
        String keys = "id,liveTopic,courseTypeStr,appUserName,startTime,totalStayTimeStr," +
                "pCount,vCount,avgStayTimeStr";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        String sheetName="课程详情页面时长统计";
        if("1".equals(map.get("type")+"")){
            sheetName="直播间详情页面时长统计";
        }
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, sheetName, req, exceltitel);
        return excel;
    }

    @Override
    public ExportExcelWhaUtil importCountExcel(String beginTime, String endTime,HttpServletRequest req) {
        Map map=new HashMap<>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        List<Map> countListForPage = UserCountMapper.getCountList(map);
        for (Map mapss:countListForPage){
            mapss.put("clRate",toPercent(new BigDecimal(Double.parseDouble(mapss.get("clRate")+""))));
            mapss.put("auplRate",toPercent(new BigDecimal(Double.parseDouble(mapss.get("auplRate")+""))));
        }

        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "页面统计列表";
        listStr.add(top1);

        String top3 = "序号,日期,新增用户,新增老师,pv,活跃用户,新增付费人数,付费金额," +
                "付费率,次留存率";
        listStr.add(top3);
        List<ExcelTop> exceltitel = ExcelUtil.getExceltitel(listStr);
        String keys = "countTime,ucount,tcount,pvcount,account,apcount," +
                "tpcount,auplRate,clRate";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(countListForPage, keys);
        String sheetName="页面统计列表";

        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, sheetName, req, exceltitel);
        return excel;
    }
}
