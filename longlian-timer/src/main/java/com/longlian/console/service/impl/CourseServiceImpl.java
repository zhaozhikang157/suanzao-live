package com.longlian.console.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.*;
import com.longlian.console.service.CourseService;
import com.longlian.console.util.LiveRoomCourseOSSUtil;
import com.longlian.live.dao.AccountTrackMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.dto.CourseDto;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.dao.JoinCourseRecordMapper;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.*;
import com.longlian.type.LogType;
import jdk.nashorn.internal.ir.IdentNode;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
/**
 * Created by pangchao on 2017/2/15.
 */
@Service("course")
public class CourseServiceImpl implements CourseService {
    private static Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    VisitCourseRecordStatMapper visitCourseRecordStatMapper;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    CourseCommentMapper courseCommentMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    CourseWareMapper courseWareMapper;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    CourseBaseNumMapper courseBaseNumMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
     CourseImgMapper courseImgMapper;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    LiveRoomCourseOSSUtil liveRoomCourseOSSUtil;
    @Autowired
    AccountTrackMapper accountTrackMapper;


    @Override
    public ExportExcelWhaUtil platformStreamImportExcel(HttpServletRequest req, HttpServletResponse response, Map requestMap, Date startDate, Date endDate) {
        String startTime = DateUtil.format(startDate, "yyyy-MM-dd");
        String endTime = DateUtil.format(endDate, "yyyy-MM-dd");
        endDate = DateUtil.format(endTime + " 23:59:59");
        startDate = DateUtil.format(startDate + " 00:00:00");
        requestMap.put("startDate",startDate); requestMap.put("endDate", endDate);
        List<Map> maps = courseMapper.getCoursePlatformStreamList(requestMap);
        if(maps!=null && maps.size()>0){
            for (Map mapss:maps){
                Map requestMapv=new HashMap<>();
                requestMapv.put("orderId",mapss.get("orderId"));
                requestMapv.put("returnLevel",1);    //查询分销用户信息
                requestMapv.put("trackName",mapss.get("trackName"));
                Map trackUserInfo = appUserMapper.getTrackUserInfo(requestMapv);
                if(trackUserInfo!=null){
                    mapss.put("trackAppName",trackUserInfo.get("trackAppName"));
                    mapss.put("trackRealName",trackUserInfo.get("trackRealName"));
                    mapss.put("trackAmount",trackUserInfo.get("trackAmount"));
                }else{
                    if((requestMap.get("trackName")!=null&&!"".equals((requestMap.get("trackName")+"")))){
                        maps.remove(maps);
                    }
                }
                log.info("--------"+new Date()+"获取平台流水："+maps.size());
                if(maps.size()==0){
                    break;
                }
            }
        }
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "课程平台流水统计";
        listStr.add(top1);

        String top3 = "序号,用户ID,课程购买者,订单号,联系电话,老师收益金额,分销人,分销金额,课程价格,成功下单时间";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "appId,appName,orderNo,appMobile,toTAmount,trackAppName,trackAmount,chargeAmt,successTime";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "课程平台流水", req, exceltitel);
        return excel;
    }

    /**
        课程统计报表导出
     */
    @Override
    public ExportExcelWhaUtil importExcel(HttpServletRequest req, HttpServletResponse response, Map requestMap,Date startDate,Date endDate) {
        String startTime = DateUtil.format(startDate, "yyyy-MM-dd");
        String endTime = DateUtil.format(endDate, "yyyy-MM-dd");
        String endDateStr = endTime + " 23:59:59";
        String startDateStr = startTime + " 00:00:00";
        requestMap.put("startDate",startDateStr); requestMap.put("endDate",endDateStr);
        List<CourseDto> courseList = courseMapper.getCourseList(requestMap);
        List<Map> maps=new ArrayList<>();
        try {
            maps = convertListBean2ListMap(courseList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "课程统计";
        listStr.add(top1);

        String top3 = "序号,课程ID,开课时间,结束时间,课程名称,分销比例,讲课老师,课程状态,是否是系列课,课程类型,课程价格,报名人数,购买人数,课程总收益";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "id,startTime,endTime,liveTopic,divideScale,appUserName,status,isSeriesCourse,courseTypeName,chargeAmt,joinCount,buyCount,totalCourseAmount";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "课程统计", req, exceltitel);
        return excel;
    }


    public List<Map> convertListBean2ListMap(List<CourseDto> beanList) throws Exception {
        List<Map> mapList = new ArrayList<Map>();
        for(int i=0, n=beanList.size(); i<n; i++){
            CourseDto bean = beanList.get(i);
           // Object bean = beanList.get(i);

            if(bean.getCustomDistribution()!=null && !"".equals(bean.getCustomDistribution()) &&!"0".equals(bean.getCustomDistribution())){  //新字段存在分销，以新字段分销比例为准
                bean.setDivideScale(bean.getCustomDistribution()+":"+new BigDecimal(100).subtract(new BigDecimal(bean.getCustomDistribution())));
            }else if (!Utility.isNullorEmpty(bean.getDivideScale()) && (bean.getChargeAmt()!=null && bean.getChargeAmt().compareTo(BigDecimal.ZERO)!=0) ) {//判断分销比例是否为空
               bean.setDivideScale(systemParaRedisUtil.getCourseDivideScaleByValue(bean.getDivideScale()));
            }
            Map map = convertBean2Map(bean);
            mapList.add(map);
        }
        return mapList;
    }
    public static Map convertBean2Map(Object bean) throws Exception {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0, n = propertyDescriptors.length; i <n ; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    if ("startTime".equals(propertyName)) {
                        returnMap.put(propertyName, DateUtil.format((Date) result));
                    } else if ("endTime".equals(propertyName)) {
                        returnMap.put(propertyName, DateUtil.format((Date) result));
                    } else if ("status".equals(propertyName)) {
                        returnMap.put(propertyName, ("0".equals(result + "")) ? "上线" : "下线");
                    } else if ("isSeriesCourse".equals(propertyName)) {
                        returnMap.put(propertyName, ("1".equals(result + "")) ? "序列课" : "单节课");
                    }else{
                        returnMap.put(propertyName, result);
                    }


                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
    public List<ExcelTop> getExceltitel(List<String> strs) {
        //++++++++++++++++++++++++++++++++++++++++表头合并拼接++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        List<ExcelTop> ets = new ArrayList<ExcelTop>();//行集合
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();//每行第一个位置
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();//每行内的 每列集合
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }
        }
        return ets;
    }

    @Override
    public List<Map> getCoursePlatformStreamList(DatagridRequestModel requestModel,Map map) {
        List<Map> coursePlatformStreamListPage = courseMapper.getCoursePlatformStreamListPage(requestModel, map);
        if(coursePlatformStreamListPage!=null && coursePlatformStreamListPage.size()>0){
            for (Map maps:coursePlatformStreamListPage){
                Map requestMap=new HashMap<>();
                requestMap.put("orderId",maps.get("orderId"));
                requestMap.put("returnLevel",1);    //查询分销用户信息
                requestMap.put("trackName",map.get("trackName"));
                Map trackUserInfo = appUserMapper.getTrackUserInfo(requestMap);
                if(trackUserInfo!=null){
                    maps.put("trackAppName",trackUserInfo.get("trackAppName"));
                    maps.put("trackRealName",trackUserInfo.get("trackRealName"));
                    maps.put("trackAmount",trackUserInfo.get("trackAmount"));
                }else{
                    if((map.get("trackName")!=null&&!"".equals((map.get("trackName")+"")))){
                        coursePlatformStreamListPage.remove(maps);
                    }
                }
                log.info("--------"+new Date()+"获取平台流水："+coursePlatformStreamListPage.size());
                if(coursePlatformStreamListPage.size()==0){
                    break;
                }
            }
            return coursePlatformStreamListPage;
        }
        return new ArrayList<Map>();
    }

    @Override
    public Map getTeacherCourseProfit(Map map) {
        Map mapp = courseMapper.getTeacherCourseProfit(map);
        if(map==null){
            return new HashMap<>();
        }
        return mapp;
    }

    @Override
    public List<Map> getCourseOrdersList(DataGridPage requestModel,Map map) {
        List<Map> courseOrdersList = courseMapper.getCourseOrdersListPage(requestModel, map);
        return courseOrdersList;
    }
    @Override
    public List<CourseDto> getListPage(DatagridRequestModel requestModel, Map map) {
        /*if("1".equals(map.get("isSerier").toString())){ //系列课
            map.put("beginTime","");
            map.put("endTime","");
        }*/

        List<CourseDto> list = courseMapper.getListPage(requestModel, map);
        if (list.size() > 0) {
            for (CourseDto c : list) {
                if(c.getCustomDistribution()!=null && !"".equals(c.getCustomDistribution()) &&!"0".equals(c.getCustomDistribution())){  //新字段存在分销，以新字段分销比例为准
                    c.setDivideScale(c.getCustomDistribution()+":"+new BigDecimal(100).subtract(new BigDecimal(c.getCustomDistribution())));
                }else if (!Utility.isNullorEmpty(c.getDivideScale()) && (c.getChargeAmt()!=null && c.getChargeAmt().compareTo(BigDecimal.ZERO)!=0) ) {//判断分销比例是否为空
                    c.setDivideScale(systemParaRedisUtil.getCourseDivideScaleByValue(c.getDivideScale()));
                }
               // Map m = courseMapper.getDistributionInformation(c.getId());//获取课程分销金额和人数
               // c.setDistributionMap(m);
               /* Map mapCount = courseMapper.findTotalCountByCourseId(c.getId());
                c.setBuyCount(Long.parseLong(mapCount.get("buyCount") + ""));
                BigDecimal count = new BigDecimal((double) c.getBuyCount());
                BigDecimal totalAmount = c.getChargeAmt().multiply(count);   //课程总费用
                c.setTotalCourseAmount(totalAmount);*/
            }
        }
       /* if(map.get("sort")!=null){
            sort(list,map.get("sort")+"",map.get("orderType")+"");
        }*/
        return list;
    }

    private static void sort(List<CourseDto> data,String sort,String orderType) {
        Collections.sort(data, new Comparator<CourseDto>() {
            public int compare(CourseDto o1, CourseDto o2) {
                BigDecimal totalCourseAmount=new BigDecimal(0); BigDecimal totalCourseAmountv=new BigDecimal(0);
                if("totalCourseAmount".equals(sort)){
                     totalCourseAmount = o1.getTotalCourseAmount();
                     totalCourseAmountv = o2.getTotalCourseAmount();
                }
                if("asc".equals(orderType)){
                    // 升序
                    return totalCourseAmount.compareTo(totalCourseAmountv);
                }else{
                    // 降序
                     return totalCourseAmountv.compareTo(totalCourseAmount);
                }
            }
        });
    }
    @Override
    public CourseDto findById(long id) {
        return courseMapper.findById(id);
    }

    @Override
    public List<Map> getCourseCommentList(long id) {
        return courseCommentMapper.getCourseCommentList(id);
    }

    @Override
    @UpdateSeriesCourseTime
    public void updateDown(long id, long optId , String optName) throws Exception {
        courseMapper.updateDown(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_down.getType(), "1" ,optId , optName,String.valueOf(id) ,"课程：" + course.getLiveTopic() +"已被下架" );
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
    }
    @Override
    @UpdateSeriesCourseTime
    public void recoveryCourse(long id, long optId , String optName)  throws Exception{
        courseMapper.recoveryCourse(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        //恢复云信聊天室
        yunxinChatRoomUtil.toggleCloseRoom(String.valueOf(course.getAppId()), String.valueOf(course.getChatRoomId()), "true");
        SystemLogUtil.saveSystemLog(LogType.console_course_recovery.getType(), "1", optId, optName, String.valueOf(id), "课程：" + course.getLiveTopic() + "已被恢复");
    }
    @Override
    @UpdateSeriesCourseTime
    public void updateUp(long id, long optId , String optName) throws Exception {
        courseMapper.updateUp(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_up.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"已被上架");
    }

    @Override
    @UpdateSeriesCourseTime
    public void del(long id, long optId, String optName) throws Exception {
        courseMapper.del(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_del.getType(), "1", optId, optName, String.valueOf(id), "课程：" + course.getLiveTopic() + "已被删除");
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
    }

    @Override
    @UpdateSeriesCourseTime
    public void restore(long id, long optId, String optName) throws Exception {
        courseMapper.restore(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_restore.getType(), "1", optId, optName, String.valueOf(id), "课程：" + course.getLiveTopic() + "已被恢复");
    }

    /**
     * 定时轮询 24小时 没有结束的课程
     *
     * @return
     */
    @Override
    public List<Course> getNotEndListBy24Hour() {
        List<Course> list = courseMapper.getNotEndListBy24Hour();
        return list;
    }

    @Override
    public List<CourseDto> getCourseMessageTask(DataGridPage page) {
        return courseMapper.getCourseMessageTaskPage(page);
    }

    /**
     * 跟新课程结束时间
     *
     * @param courseId
     * @return
     */
    @Override
    public int updateEndTime(long courseId) {
        return courseMapper.updateEndTime(courseId);
    }

    public Course getCourse(Long courseId) {
        return courseMapper.getCourse(courseId);
    }

    @Override
    public Map getCourseDetails(long id) {
        Map map = new HashMap();
        //课程详情
        Course course = courseMapper.getCourse(id);
        if ("0".equals(course.getStatus())) {
            map.put("status", "上线");
        } else {
            map.put("status", "下线");
        }
        //课程老师详情
        AppUser appUser = appUserMapper.findById(course.getAppId());
        //课程来源
        Map courseSourceMap = visitCourseRecordStatMapper.findCourseSource(id);
        //课程购买人数
        map.put("payCourseNumMap", joinCourseRecordMapper.getPaySourceNum(id));
        //课程评价
        map.put("commentList", courseCommentMapper.getCourseCommentList(id));
        map.put("course", course);
        map.put("appUser", appUser);
        map.put("courseSourceMap", courseSourceMap);
        LiveChannel lc = qiNiuliveChannelService.getCourseLiveAddr(course);
        if (lc != null) {
            map.put("liveChannel", lc);
        }
        return map;
    }

    public List<CourseWare> getCourseWare(Long courseId) {
        return courseWareMapper.getCourseWare(courseId);
    }

    public List<CourseImg> getCoursImg(Long courseId){
        return courseImgMapper.getCourseImgList(courseId);
    }

    @Override
    public List<Map> getCourseMessageListPage(DataGridPage page, Long courseId, String liveTopic, String attach) {
        List<Map> listMap = courseMapper.getCourseMessagePage(page, courseId, liveTopic, attach);
        return listMap;
    }


    @Override
    public List<Course> getPreRemindByHourList() {
        Date date = DateUtil.getSecondAfter(new Date(), +12 * 60);
        String timeStr = Utility.getDateTimeStr(date, "yyyy-MM-dd HH:mm:ss");
        //System.out.print("timeStr ==================================== " + timeStr);
        List<Course> list = courseMapper.getPreRemindByHourList(timeStr);
        return list;
    }

    @Override
    public List<Course> findDuringThisPeriodCourse(String startTime, String endTime,List<Long> courseId) {
        return courseMapper.findDuringThisPeriodCourse(startTime,endTime,courseId);
    }


    public synchronized long addVisitCount(long id, long addCount , long optId , String optName) {
        Course course = courseMapper.getCourse(id);
        Integer old = course.getVisitCount();

        CourseBaseNum courseBaseNum = courseBaseNumMapper.selectByCourse(id , "1");
        if (courseBaseNum == null) {
            courseBaseNum = new CourseBaseNum();
            courseBaseNum.setCount(addCount);
            courseBaseNum.setCourseId(id);
            courseBaseNum.setType("1");
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(course.getRoomId());
            courseBaseNumMapper.insert(courseBaseNum);
        } else {
            courseBaseNum.setCount(courseBaseNum.getCount() + addCount);
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(course.getRoomId());
            courseBaseNumMapper.updateByPrimaryKey(courseBaseNum);
        }

        course.setVisitCount(old + (int)addCount  );
        courseMapper.setVisitCount(id , course.getVisitCount());

        //访问基数,后台设置的
        redisUtil.hset(RedisKey.ll_course_base_visit_num, String.valueOf(id), String.valueOf(courseBaseNum.getCount()));

        SystemLogUtil.saveSystemLog(LogType.console_course_visitcount.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"的访问人数由原来的："+old+"增加到：" + course.getVisitCount() );
        return course.getVisitCount();
    }


    @Override
    public synchronized long addJoinCount(long id, long addCount , long optId , String optName) {
        Course course = courseMapper.getCourse(id);
        Integer old = course.getJoinCount();
         CourseBaseNum courseBaseNum = courseBaseNumMapper.selectByCourse(id , "0");
        if (courseBaseNum == null) {
            courseBaseNum = new CourseBaseNum();
            courseBaseNum.setCount(addCount);
            courseBaseNum.setCourseId(id);
            courseBaseNum.setType("0");
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(course.getRoomId());
            courseBaseNumMapper.insert(courseBaseNum);
        } else {
            courseBaseNum.setCount(courseBaseNum.getCount() + addCount);
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(course.getRoomId());
            courseBaseNumMapper.updateByPrimaryKey(courseBaseNum);
        }

        course.setJoinCount(old + (int) addCount);
        courseMapper.setJoinCount(id, course.getJoinCount());

        //课程参加学习基数,后台设置的
        redisUtil.hset(RedisKey.ll_course_base_join_num, String.valueOf(id), String.valueOf(courseBaseNum.getCount()));

        SystemLogUtil.saveSystemLog(LogType.console_course_joincount.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"的参加人数由原来的："+old+"增加到：" + course.getJoinCount()   );
        return  course.getJoinCount() ;
    }


    @Override
    public synchronized long addRecoSort(long id, long addCount , long optId , String optName) {
        Course course = courseMapper.getCourse(id);
        Long old = course.getRecoSort();
        courseMapper.setRecoSort(id, addCount);
        SystemLogUtil.saveSystemLog(LogType.console_course_Sortcount.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"的人工评分由原来的："+old+"设置为：" + addCount   );
        return  addCount ;
    }


    @Override
    public BigDecimal addTypeSort(long id, BigDecimal addCount, long optId, String optName) {
        Course course = courseMapper.getCourse(id);
        Long old = course.getRecoSort();
        courseMapper.setTypeSort(id, addCount);
        SystemLogUtil.saveSystemLog(LogType.console_course_Sortcount.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"的分类评分由原来的："+old+"设置为：" + addCount   );
        return  addCount ;
    }

    @Override
    public  long setAutoCloseTime(long id, long autoCloseTime , long optId , String optName){

        Course course = courseMapper.getCourse(id);
        Integer old = course.getAutoCloseTime();
        courseMapper.setAutoCloseTime(id, (int) autoCloseTime);
        SystemLogUtil.saveSystemLog(LogType.console_course_autoclosetime.getType(), "1" ,optId , optName,String.valueOf(id) , "课程：" + course.getLiveTopic() +"的访问人数由原来的："+old+"修改为：" + autoCloseTime);
        return autoCloseTime;
        
    }

    @Override
    public  void doUpdate(CourseDto course) throws Exception{
            //简介图片
//        String imgs = course.getCourseImg();
//        courseImgMapper.deleteByCourseId(course.getId());
//        if (!StringUtils.isEmpty(imgs)) {
//            String[] ps = imgs.split("&&");
//            List list = Arrays.asList(ps);
//            courseImgMapper.insertList(list, course.getId());
//        }
        if (!StringUtils.isEmpty(course.getCoverssAddress())) {
            course.setVerticalCoverssAddress(course.getCoverssAddress());
        }
        courseMapper.update(course);
        if (!StringUtils.isEmpty(course.getCoverssAddress())) {
            Map map = new HashMap();
            map.put("courseId" , course.getId());
            map.put("image" , course.getVerticalCoverssAddress());
            redisUtil.lpush(RedisKey.ll_set_vertical_coverss_img , JsonUtil.toJson(map));
        }
        Map map2 = new HashMap();
        map2.put("id" , String.valueOf(course.getId()));
        map2.put("name", course.getLiveTopic());
        redisUtil.lpush(RedisKey.ll_update_course_name, JsonUtil.toJson(map2));
    }

    @Override
    public List<Course> getNoChatRoomId() {
        return courseMapper.getNoChatRoomId();
    }
    @Override
    public List<Map>  getAuditListPage(DatagridRequestModel requestModel,Map map){
        List<Map> list = courseMapper.getAuditListPage(requestModel, map);
        return list;
    }

    @Override
    public List<Course> getSeriesCourseNotEnd() {
        List<Course> list = courseMapper.getSeriesCourseNotEnd();
        return list;
    }

    @Override
    public List<Course> getCourseBySeries(long id) {
        List<Course> list = courseMapper.getCourseBySeries(id);
        return list;
    }

    @Override
    public List<Long> findStatus() {
        return courseMapper.findStatus();
    }

    @Override
    public List<Course> findAll() {
        return courseMapper.findAll();
    }

    @Override
    public List<Course> findAllCourse(long pageSize, long offset) {
        return courseMapper.findAllCourse(pageSize, offset);
    }

    @Override
    public void updateVideoAddress(long id, String videoAddress) {
        courseMapper.updateVideoAddress(id , videoAddress);
    }

    @Override
    public List<Map> getNeedDealCourse() {
        return courseMapper.getNeedDealCourse();
    }

    @Override
    public List<Course> findVideoAddress(int pageSize) {
        return courseMapper.findVideoAddress(pageSize);
    }

    @Override
    public List<CourseDto> getNotEndRecordCourseList() {
        List<CourseDto> list = courseMapper.getNotEndRecordCourseList();
        return list;
    }

    @Override
    public List<CourseDto> getListByRoomIdPage(DatagridRequestModel requestModel, Map map) {
        if("1".equals(map.get("isSerier").toString())){ //系列课
            map.put("beginTime","");
            map.put("endTime","");
        }
        List<CourseDto> list = courseMapper.getListByRoomIdPage(requestModel, map);
        if (list.size() > 0) {
            for (CourseDto c : list) {
                if (!Utility.isNullorEmpty(c.getDivideScale())) {//判断分销比例是否为空
                    c.setDivideScale(systemParaRedisUtil.getCourseDivideScaleByValue(c.getDivideScale()));
                }
                Map m = courseMapper.getDistributionInformation(c.getId());//获取课程分销金额和人数
                c.setDistributionMap(m);
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delOSS(Course course, long optId, String optName) throws Exception {
        List<Course> courses = new ArrayList<Course>();
        courseMapper.del(course.getId());
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_del.getType(), "1", optId, optName, String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被删除");
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
        courses.add(course);
        liveRoomCourseOSSUtil.delOSS(courses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delOSSAll( List<Course> courses, long optId, String optName) throws Exception {
        if (courses != null && courses.size() > 0) {
            courseMapper.dels(courses);
            for (Course course : courses) {
                if (course != null) {
                    // 如果是系列课里面的单节课，则发送更新系列课时间队列
                    UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
                    SystemLogUtil.saveSystemLog(LogType.console_course_del.getType(), "1", optId, optName, String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被删除");
                }
            }
            liveRoomCourseOSSUtil.delOSS(courses);
        }
    }

    @Override
    public Course getCourseFromRedis(Long courseId) {
        String key = RedisKey.ll_course + courseId;
        String courseStr = redisUtil.get(key);
        //没找到
        if (org.apache.commons.lang3.StringUtils.isEmpty(courseStr) || "null".equals(courseStr)) {
            Course course = this.getCourse(courseId);
            //缓存三天
            redisUtil.setex(key , 3 * 24 * 60 * 60 , JsonUtil.toJson(course));
            return course;
        } else {
            return JsonUtil.getObject(courseStr , Course.class);
        }
    }

    @Override
    public Set<Course> getAllStartCourse() {
        return courseMapper.getAllStartCourse();
    }

    @Override
    public Course getCourseByVideoAddress(String md5) {
        return courseMapper.getCourseByVideoAddress(md5);
    }

    @Override
    public void updateCourseSort(long courseId, BigDecimal sort) {
        courseMapper.updateCourseSort(courseId , sort);
    }

    @Override
    public List<Course> findSeriesCourseBySeriesId(long id) {
        return courseMapper.findSeriesCourseBySeriesId(id);
    }

    @Override
    public Boolean isHaveWare(long courseId) {
        String value = redisUtil.get(RedisKey.ll_course_ware + courseId);
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(value)){
            if(JsonUtil.getList(value, CourseWare.class).size() > 0){
                return true;
            }
        }
        List<CourseWare> list = courseWareMapper.getCourseWare(courseId);
        if(list.size()>0){
            redisUtil.setex(RedisKey.ll_course_ware, 60 * 60 * 24 * 3, JsonUtil.toJson(list));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<Course> getCourseByToday() {
        List<Course> list = courseMapper.getCourseByToday();
        return list;
    }

    /**
     * 下线课程操作
     *
     * @param courseId
     */
    @Override
    public int updateNoStreamClassDown(Long courseId) {
        return courseMapper.updateNoStreamClassDown(courseId);
    }

    /*获取今日的没有结束的语音课*/
    @Override
    public List getCourseByVoiceToday() {
        return courseMapper.getCourseByVoiceToday();
    }

    @Override
    public void updateHidden(long id,String isHide) {
        courseMapper.updateHidden(id,isHide);
    }

    @Override
    public List<Course> getSeriesCourseToday() {
        return courseMapper.getSeriesCourseToday();
    }

    /**
     * 查询当前时间之前的最后一节课
     * @param courseId
     * @return
     */
    @Override
    public Course getCourseByBefore(long courseId) {
        return courseMapper.getCourseByBefore(courseId);
    }

    /**
     * 获取当天的课程
     * @return
     */
    @Override
    public Map getCourseCountBySeriesInToday() {
        Map map = new HashMap<Long,Long>();
        List list = courseMapper.getCourseCountBySeriesInToday();
        if(list != null && list.size() > 0){
            for(int i=0; i<list.size(); i++){
                Map<String,Long> m = (Map) list.get(i);
                long key = 0;
                long value = 0;
                for(Map.Entry<String,Long> entry : m.entrySet()){
                    if("ID".equals(entry.getKey())){
                        key = entry.getValue();
                    }else{
                        value = entry.getValue();
                    }
                }
                map.put(key,value);
            }
        }
        return map;
    }

    @Override
    public Map<Long,Map> getCourseRelayCountBySeriesToday(Long[] obs) {
        Map ls = new HashMap<Long,Map<Long,Long>>();
        Map map = new HashMap<Long,Long>();
        List list = courseMapper.getCourseRelayCountBySeriesToday(obs);
        if(list != null && list.size() > 0){
            for(int i=0; i<list.size(); i++){
                Map<String,Long> m = (Map) list.get(i);
                long id = 0;
                long key = 0;
                long value = 0;
                for(Map.Entry<String,Long> entry : m.entrySet()){
                    if("ID".equals(entry.getKey())){
                        key = entry.getValue();
                    }else if("ORI_COURSE_ID".equals(entry.getKey())){
                        id = entry.getValue();
                    }else{
                        value = entry.getValue();
                    }
                }
                if(ls.containsKey(id)){
                    map.put(key,value);
                }else{
                    map.put(key,value);
                    ls.put(id,map);
                }
            }
        }
        return ls;
    }
}
