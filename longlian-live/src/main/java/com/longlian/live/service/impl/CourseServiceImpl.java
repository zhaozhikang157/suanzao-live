package com.longlian.live.service.impl;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.*;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.newdao.DataChargeRecordMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.*;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.*;
import com.longlian.model.course.CourseCard;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by pangchao on 2017/2/12.
 */
@Service("courseService")
public class CourseServiceImpl implements CourseService {
    private static Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
    @Autowired
    private RoomFuncService roomFuncService;
    @Autowired
    EndLiveService endLiveService;
    @Autowired
    CourseWareMapper courseWareMapper;
    @Autowired
    JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseCommentService courseCommentService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    AccountTrackService accountTrackService;
    @Autowired
    VisitCourseRecordMapper visitCourseRecordMapper;
    @Autowired
    CourseImgMapper courseImgMapper;
    @Autowired
    CourseCommentMapper courseCommentMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisLock redisLock;
    @Autowired
    VisitCourseRecordService visitCourseRecordService;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    StudyRecordService studyRecordService;
    @Value("${website}")
    private String website;
    @Autowired
    LiveService liveService;
    @Autowired
    PicUtil picUtil;
    @Autowired
    CourseManagerService courseManagerService;
    @Autowired
    GagService gagService;
    @Autowired
    AppUserService userService;
    @Autowired
    AdvertisingDisplayService advertisingDisplayService;
    @Autowired
    RecoCourseServive recoCourseServive;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    DataChargeRecordMapper dataChargeRecordMapper;
    @Autowired
    private CoursePrivateCardMapper coursePrivateCardMapper;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private CourseTypeService courseTypeService;
    @Autowired
    private CourseRelayService courseRelayService;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    @Autowired
    private CourseRelayMapper courseRelayMapper;
    @Autowired
    AppMsgService appMsgService;
    //@Value("${aliyun.oss.kjbucketName}")
    //private String kjbucketName;
    private static final long limit = 10;//首页直播预告、正在直播默认显示5条

    /**
     * 根据老师appId获取所创建的直播间信息
     *
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public ActResultDto noLiveInfoPage(Integer offset, Integer pageSize, HttpServletRequest request) {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        long appId = token.getId();
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = courseMapper.noLiveInfoPage(appId, dg);
        if (list != null && list.size() > 0) {
            for (Map map : list) {
                long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
                String poorTime = DateUtil.getDatePoor2(new Date(), DateUtil.format(map.get("startTime").toString()));
                map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
                map.put("shareTile", map.get("title"));
                map.put("shareContent", map.get("remark"));
                map.put("poorTime", poorTime);
                map.put("joinCount", joinCount);
                map.put("shareAddress", "");
            }
            resultDto.setData(list);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return resultDto;
    }

    /**
     * 获取所有的该老师的直播课件信息(未直播和已直播的)
     *
     * @param alreadyLearnOffset
     * @param pageSize
     * @param appId
     * @return
     */
    @Override
    public ActResultDto getAllLiveInfoPage(Integer noLearnOffset, Integer pageSize, long appId, Integer alreadyLearnOffset) {
        ActResultDto resultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        dg.setOffset(0);
        dg.setPageSize(100);
        List<Map> list = courseMapper.noLiveInfoPage(appId, dg);
        if (list != null && list.size() > 0) {
            for (Map map : list) {
                long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
                String poorTime = DateUtil.getDatePoor2(new Date(), DateUtil.format(map.get("startTime").toString()));
                map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
                map.put("poorTime", poorTime);
                map.put("joinCount", joinCount);
                Date st = DateUtil.format(map.get("startTime").toString());
                if(st.compareTo(new Date()) > 0){
                    map.put("liveTimeStatus",0);
                }else{
                    map.put("liveTimeStatus",1);
                }
            }
            resultDto.setData(list);
        }
        List<Map> mapList = getAlreadyLivePage(alreadyLearnOffset, pageSize, appId);
        if (mapList != null && mapList.size() > 0) {
            resultDto.setExt(mapList);
        }
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return resultDto;
    }

    @Override
    public List getCourseList(long liveRoomId, Integer pageNum, Integer pageSize, boolean isHaveRecord, String status, String v) {
        DataGridPage dg = new DataGridPage();
        dg.setCurrentPage(pageNum != null ? pageNum : 1);
        dg.setPageSize(pageSize!=null?pageSize:10);
        List<Map> list = null;
        //status 0:已结束 1：已下架 2：直播中
        if(v != null && Integer.parseInt(v.replace(".", "")) > 163){ //兼容1.6.3的版本
            list = courseMapper.getCourseLIstV164Page(dg,liveRoomId,status);
        }else{
            list = courseMapper.getCourseListPage(dg, liveRoomId,status);
        }
        if (list != null && list.size() > 0) {
            for (Map m : list) {
                //转化时间
                String createTime = m.get("createTime").toString();
                createTime = DateUtil.transFormationStringDate(createTime);
               /* //获取分销比例
                String divideScale = systemParaRedisUtil.getCourseDivideScaleByValue(m.get("divideScale").toString());
                m.put("divideScale", divideScale);*/
                m.put("createTime", createTime);
            }
        }
        return list;
    }

    @Override
    public List<CourseImg> getCourseImgList(long courseId) {
        String redisVal = redisUtil.get(RedisKey.course_img + courseId);
        if (StringUtils.isEmpty(redisVal)) {
            List<CourseImg> list = null;
            if(courseId >= SystemCofigConst.RELAY_COURSE_ID_SIZE){
                list = courseImgMapper.getRelayCourseImgList(courseId);
            }else{
                list = courseImgMapper.getCourseImgList(courseId);
            }
            if (list != null && list.size() > 0) {
                redisUtil.set(RedisKey.course_img + courseId, JsonUtil.toJson(list));
                redisUtil.expire(RedisKey.course_img + courseId, 5 * 24 * 60 * 60);
            }
            return list;
        } else {
            List<CourseImg> list = JsonUtil.getList(redisVal, CourseImg.class);
            return list;
        }
    }


    public List<Map> getAlreadyLivePage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = courseMapper.getAlreadyLivePage(appId, dg);
        for (Map map : list) {
            map.put("poorTime", "");
            map.put("endTime",
                    map.get("endTime").toString().substring(0, map.get("endTime").toString().length() - 2));
        }
        return list;
    }

    public Course getCourse(Long courseId) {
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
            return courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
        }
        return courseBaseService.getCourse(courseId);
    }

    @Override
    public Course getRelayCourse(long courseId) {
        return courseBaseService.getRelayCourse(courseId);
    }

    @Override
    public void updateIsOpenById(int i, long id) {
        courseMapper.updateIsOpenById(i, id);
    }

    @Override
    public Course getCourseFromRedis(Long courseId) {
        return courseBaseService.getCourseFromRedis(courseId);
    }

    public List<CourseWare> getCourseWare(Long courseId) {
        String key = RedisKey.ll_course_ware + courseId;

        if (redisUtil.exists(key)) {
            return getCourseWareList(key);
        }


        String lockKey = RedisKey.ll_course_ware_lock_pre + courseId;
        boolean flag = redisLock.lock(lockKey, 200 * 1000, 6);
        //获取锁失败，
        if (!flag) {
            log.info("获取锁{}失败，请稍等!", lockKey);
            GlobalExceptionHandler.sendEmail("获取锁" + lockKey + "失败，请等侍!", "注意");
            return courseWareMapper.getCourseWare(courseId);
        }
        try {
            if (redisUtil.exists(key)) {
                return getCourseWareList(key);
            }
            List<CourseWare> list = courseWareMapper.getCourseWare(courseId);
            redisUtil.setex(key, 60 * 60 * 24 * 3, JsonUtil.toJson(list));

            return list;
        } finally {
            redisLock.unlock(lockKey);
        }
    }

    private List<CourseWare> getCourseWareList(String key) {
        String value = redisUtil.get(key);
        redisUtil.expire(key, 60 * 60 * 24 * 3);

        List<CourseWare> list = JsonUtil.getList(value, CourseWare.class);
        return list;
    }


    /**
     * 获取首页预告直播
     *
     * @return
     */
    @Override
    public List<Map> getPrevueLive4Home(Integer PrevueOffset) {
        DataGridPage dg = new DataGridPage();
        if (PrevueOffset != null) dg.setOffset(PrevueOffset);
        List<Map> temp = courseMapper.getPrevueLive4HomePage(dg);
        return prevueLive4Home(temp);
    }

    /**
     * 获取首页预告直播
     *
     * @return
     */
    @Override
    public List<Map> getPrevueLive4HomeV2(Integer PrevueOffset, boolean isHaveRecord) {
        DataGridPage dg = new DataGridPage();
        if (PrevueOffset != null) dg.setOffset(PrevueOffset);

        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        List<Map> temp = courseMapper.getPrevueLive4HomeV2Page(dg, isRecorded);
        return prevueLive4Home(temp);
    }

    public List<Map> prevueLive4Home(List<Map> temp) {
        List<Map> list = new ArrayList<Map>();
        String date_fmt_str = "yyyy-MM-dd";
        String date_time_fmt_str = "HH:mm";
        String date_time_fmt2_str = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_fmt_str);
        SimpleDateFormat date_time_fmt = new SimpleDateFormat(date_time_fmt_str);
        SimpleDateFormat date_time_fmt3 = new SimpleDateFormat(date_time_fmt2_str);
        for (Map map : temp) {
            Timestamp startTime = (Timestamp) map.get("startTime");
            map.put("chargeAmt", map.get("chargeAmt").toString());
            Date d = new Date(startTime.getTime());
            String dateStr = Utility.getDateTimeStr(d, simpleDateFormat);
            String currDate = Utility.getCurDateTimeStr(date_fmt_str);
            Date tomorrow = DateUtil.getDayAfter(new Date(), 1);
            String tomorrowStr = Utility.getDateTimeStr(tomorrow, simpleDateFormat);
            String startTimeStr = "";
            if (currDate.equals(dateStr)) {
                startTimeStr = "今天" + Utility.getDateTimeStr(d, date_time_fmt);
            } else if (tomorrowStr.equals(dateStr)) {
                startTimeStr = "明天" + Utility.getDateTimeStr(d, date_time_fmt);
            } else {
                startTimeStr = Utility.getDateTimeStr(d, date_time_fmt3);
            }
            map.put("startTimeStr", startTimeStr);
            // map.put("joinCount", joinCourseRecordService.getCountByCourseId((Long) map.get("id")));
            list.add(map);
        }
        return list;
    }


    /**
     * 获取首页直播中的-分页
     *
     * @return
     */
    @Override
    public List<Map> getLiveing4Home(Integer liveingOffset) {
        DataGridPage dg = new DataGridPage();
        if (liveingOffset != null) dg.setOffset(liveingOffset);
        return courseMapper.getLiveing4HomePage(dg);
    }

    @Override
    public List<Map> getLiveing4HomeV2(Integer liveingOffset, boolean isHaveRecord) {
        DataGridPage dg = new DataGridPage();
        if (liveingOffset != null) dg.setOffset(liveingOffset);
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        return courseMapper.getLiveing4HomeV2Page(dg, isRecorded);
    }

    @Override
    public List<Map> getCommend4HomePageV2(DataGridPage page, boolean isHaveRecord) {
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }
        List<Map> list = courseMapper.getCommend4HomeV2Page(page, isRecorded);
        return list;
    }

    /**
     * 获取首页精选，推荐的(分页)
     *
     * @return
     */
    @Override
    public List<Map> getCommend4HomePage(DataGridPage page) {
        List<Map> list = courseMapper.getCommend4HomePage(page);
        return list;
    }

    /**
     * 取得所有的课程（导入云信时创建聊天室）
     *
     * @return
     */
    public List<Course> getAllCourse() {
        return courseMapper.getAllCourse();
    }

    /**
     * 设置取得的聊天室ID到课程
     *
     * @param course
     */
    public void updateCourseInfo(Course course) {
        courseMapper.setLiveRoomInfo(course);
    }

    /**
     * 课程统计详情
     *
     * @param courseId
     */
    @Override
    public Map getCourseDetails(Long courseId) {
        Map map = new HashMap();
        Course course = courseMapper.getCourse(courseId);
        map.put("course", course);
        //付费人数
        long payNum = joinCourseRecordService.getCountByCourseId(courseId);
        map.put("payNum", payNum);
        //学习人数
        long studyMum = courseMapper.getStudyNum(courseId);
        if(course == null){
            return null;
        }
        if ("1".equals(course.getIsSeriesCourse())) {
            studyMum = courseMapper.getSeriesStudyNum(courseId);
        }
        map.put("studyMum", studyMum);
        //评论人数
        map.put("commentCount", courseCommentMapper.getCoursePeopleSum(courseId));
        //评论条数
        long courseCommentNum = courseCommentMapper.getCourseCommentSum(courseId);
        map.put("courseCommentNum", courseCommentNum);
        //渠道数据
        Map courseSourceMap = visitCourseRecordMapper.findCourseSource(courseId);
        map.put("courseSourceMap", courseSourceMap);
        return map;
    }

    /**
     * 进入原始课程详情页  并记录访问记录   不是转播课
     *
     * @param id
     * @param appid
     * @return
     */
    @Override
    public ActResultDto getCourseInfo(long id, long appid, boolean isWechatClient, String v,String type) {
        ActResultDto resultDto = new ActResultDto();
        Map data = new HashMap();
        Map map = null;
        String isJoinStatus = "";
        long s = System.currentTimeMillis();
        //获取系列课单节课课程信息
        map = courseMapper.getCourseInfoDetails(id);
        //判断:如果该课程已下架,或者删除,则直接返回
        if (map == null) {
            resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
            return resultDto;
        }
        if ("1".equals(map.get("status").toString()) || "1".equals(map.get("isDelete").toString())) {
            resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
        }
        Long seriesid = (Long) map.get("seriesCourseId");
        boolean isSeries = false;
        if (seriesid != null && seriesid > 0) {
            isSeries = true;
        }
        if (appid != 0) {
            isJoinStatus = joinCourseRecordService.getJoinCourseStatus(appid, isSeries ? seriesid : id);  //查询是否报名
        }
        long e = System.currentTimeMillis();
        //System.out.println(e - s);
        if (map == null) {
            resultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            return resultDto;
        }
        map.put("chargeAmt", isSeries ? map.get("seriesAmt").toString() : map.get("chargeAmt").toString());
        BigDecimal chargeAmt = BigDecimal.valueOf(Double.parseDouble(map.get("chargeAmt").toString()));
        String percent = "0" ;
        if(map.get("distribution")!= null && ((String)map.get("distribution")).length() > 0){
            percent = (String) map.get("distribution");
        }
        if(chargeAmt.compareTo(new BigDecimal(0)) > 0){
            map.put("isPay",1);
            BigDecimal sd = chargeAmt.multiply(BigDecimal.valueOf(Double.parseDouble(percent!=null&&percent.trim().length()>0?percent:"0"))).divide(new BigDecimal(100));
            DecimalFormat df = new DecimalFormat("###0.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            String sdStr = df.format(sd);
            map.put("stuDisAmount",new BigDecimal(sdStr));
        }else{
            map.put("isPay", 0);
            map.put("stuDisAmount",0);
        }
        //判断课程是否是当前用户的
        if (Long.parseLong(map.get("appId").toString()) == appid) {
            data.put("myLiveRoom", 1);
        } else {
            data.put("myLiveRoom", 0);
            //增加访问记录
            if (!isWechatClient) {//不是微信端就执行
                visitCourseRecordService.insertRecord(appid, id, 0l, null, seriesid, "0");
            }
        }
        long roomId = Long.parseLong(map.get("roomId").toString());//聊天室id
        data.put("isFollow", userFollowService.isFollowRoom(roomId, appid));//判断直播间是否关注过
        data.put("followNum", userFollowService.getCountByRoomId(roomId));//直播间关注数
        Boolean b = wechatOfficialService.isWechatOfficial(roomId);
        if(b){
            //判断是不是有成语流量,如果没有则充值,
            BigDecimal banlanceAmount =  new BigDecimal(dataChargeRecordMapper.getBanlanceByUserId(appid));
            Long reduceDataCount = dataChargeRecordMapper.getReduceDataCountbByAppId(appid);
            if(reduceDataCount == null){reduceDataCount = 0l;}
            if(banlanceAmount.subtract(new BigDecimal(reduceDataCount)).compareTo(new BigDecimal(0)) >=0){
                data.put("isWechatOfficial","0"); //不需要充值流量
            }else{
                data.put("isWechatOfficial","1"); //流量不足
            }
        }else{
            data.put("isWechatOfficial","0"); //不需要充值流量
        }
        long e2 = System.currentTimeMillis();
        //System.out.println(e2 - e);

        if (seriesid == null || seriesid == 0l) {
            map.put("studyCount", joinCourseRecordService.getCountByCourseId(id));// 学习参加人数
        } else {
            map.put("studyCount", studyRecordService.getStudyRecordCount(id));// 学习参加人数
        }
        long e3 = System.currentTimeMillis();
        //System.out.println(e3 - e2);

        List<CourseWare> courseWaresList = this.getCourseWare(id);
        //courseWareMapper.getCourseWare(id);
        data.put("courseWaresList", courseWaresList);
        Date startTime = (Date) map.get("startTime");
        if (startTime != null) {
            map.put("startTime", DateUtil.format(startTime, "yyyy-MM-dd HH:mm"));
            if (startTime.compareTo(new Date()) > 0) {
                map.put("proTimeH5", (startTime.getTime() - (new Date().getTime())));
            } else {
                map.put("proTimeH5", 0);
            }
        }

        long e4 = System.currentTimeMillis();
        //System.out.println(e4 - e3);

        Date endTime = (Date) map.get("endTime");
        String liveTimeStatus = "2";
        if (startTime != null) {
            liveTimeStatus = getLiveTimeStatus(startTime, endTime);
        }
        map.put("liveTimeStatus", liveTimeStatus);
        /*//分销比例
        String divideScaleDesc = systemParaRedisUtil.getCourseDivideScaleByValue(map.get("divideScale").toString());
        if (Utility.isNullorEmpty(divideScaleDesc)) divideScaleDesc = "0";
        map.put("divideScaleDesc", divideScaleDesc);*/
        data.put("course", map);
        //老师课程数量
        long teachAppId = (Long) map.get("appId");

        long e5 = System.currentTimeMillis();
        //System.out.println(e5 - e4);

        int teachCourseCount = this.getCourseCountByAppId(teachAppId,"");
        if("0".equals(type)){
            int relayCount = courseRelayMapper.getRelayCountByOriAppId(teachAppId);
            teachCourseCount += relayCount;
        }
        long e6 = System.currentTimeMillis();
        //System.out.println(e6 - e5);

        data.put("teachCourseCount", teachCourseCount);
        //老师所有课程参加学习人数:普通单节课是报名人数，如果是系列课
        long studyAllCount = joinCourseRecordService.getCountByRoomId(roomId);
        long e7 = System.currentTimeMillis();
        //System.out.println(e7 - e6);
        data.put("studyAllCount", studyAllCount);
        long commentCount = 0l;
        //已经去掉;
        //courseCommentService.getCommentSumByCourseId(id);
        data.put("commentCount", commentCount);
        //系列课数量(新加)
        int seriesCourseCount = this.getSerisCourseCount(roomId);
        long e8 = System.currentTimeMillis();
        //System.out.println(e8 - e7);
        data.put("seriesCourseCount", seriesCourseCount);
        data.put("isJoin", isJoinStatus);//是否购买
        int isSuperAdmin = userService.findSystemAdminByUserId(appid);
        if(isSuperAdmin > 0){
            data.put("isJoin", "1");//是否购买
        }

        //获取简介图片列表
        List<CourseImg> courseImgList = this.getCourseImgList(id);
        long e9 = System.currentTimeMillis();
        //System.out.println(e9 - e8);
        List<CourseImg> newCourseImgList = new ArrayList<CourseImg>();
        data.put("courseImgList", courseImgList);
        log.info("courseImgList :" + JsonUtil.toJsonString(courseImgList));
        if (!isWechatClient) {
            if (Utility.isNullorEmpty(v)) {
                v = "0";
            }
            v = v.replace(".", "");
            if (Integer.valueOf(v) <= 140) {
                for (CourseImg courseImg : courseImgList) {

                    newCourseImgList.add(courseImg);
                }
                data.put("courseImgList", newCourseImgList);
            }
        }
        if (data.get("courseImgList") != null) {
            List<CourseImg> newCourseImg = new ArrayList<CourseImg>();
            if (isWechatClient && teachAppId == appid) {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        courseImg.setAddress(courseImg.getLocalIds());
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    newCourseImg.add(courseImg);
                }
            } else {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && courseImg.getAddress().startsWith("http")) {
                        newCourseImg.add(courseImg);
                    } else {
                        if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        } else {
                            newCourseImg.add(courseImg);
                        }
                    }
                }
            }
            data.put("courseImgList", newCourseImg);
            log.info("newCourseImgList :" + JsonUtil.toJsonString(newCourseImg));
        }
        String managers = courseManagerService.findAllManagerRealByCourseId(id);
        long e10 = System.currentTimeMillis();
        //System.out.println(e10 - e9);

        data.put("managerId", managers);
        data.put("isManager", Utility.findById(managers, String.valueOf(appid)) ? "1" : "0");
        data.put("gagUserId", gagService.findUserIdByCourseId(id));

        //是不是老师，不是录播课程\课程没结束
        if (Long.parseLong(map.get("appId").toString()) == appid
                && "0".equals(map.get("isRecorded")) && endTime == null) {
            LiveChannel liveChannel = qiNiuliveChannelService.getDefaultLiveChannel();
            String originUrl = qiNiuliveChannelService.getPushAddress((String) map.get("pushAddress"), liveChannel  , String.valueOf(id));
            data.put("pushAddress", originUrl);
        } else {
            data.put("pushAddress", "");
        }
        //wha - 微信加入录播课字段 录播课未到时间 不让学生观看
        if(map!=null&&map.get("isRecorded")!=null){
            data.put("isRecorded",map.get("isRecorded").toString());
        }
        CourseCard card = coursePrivateCardMapper.findCardUrlByCourseId(id);
        if(card != null){
            data.put("modelUrl", card.getModelUrl());
            data.put("cardUrl", card.getCardUrl());
        }
        resultDto.setData(data);
        return resultDto;
    }


    /**
     * 进入课程详情页  并记录访问记录  转播课
     *
     * @param id
     * @param appid
     * @return
     */
    @Override
    public ActResultDto getRelayCourseInfo(long id, long appid, boolean isWechatClient, String v) {
        long realCourseId=id;
        ActResultDto resultDto = new ActResultDto();
        Map data = new HashMap();
        Map map = null;
        String isJoinStatus = "";
        long s = System.currentTimeMillis();
        //获取系列课单节课课程信息
        map = courseMapper.getRelayCourseInfo(id);
        long relayAppid=(long)map.get("relayAppId");
        //判断:如果该课程已下架,或者删除,则直接返回
        if (map == null) {
            resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
            return resultDto;
        }
        if ("1".equals(map.get("status").toString()) || "1".equals(map.get("isDelete").toString())) {
            resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
        }
        Long seriesid = (Long) map.get("seriesCourseId");
        boolean isSeries = false;
        if (seriesid != null && seriesid > 0) {
            isSeries = true;
        }
        String relayId = map.get("id").toString();
        if(relayId!=null && relayId.length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto=courseRelayService.queryById((Long) map.get("id"));
            id=courseRelayDto.getOriCourseId();
        }else{
            id=Long.valueOf((String)map.get("id"));
        }
        if (appid != 0) {
            isJoinStatus = joinCourseRecordService.getJoinCourseStatus(appid, isSeries ? seriesid : Long.parseLong(relayId));  //查询是否报名
        }
        long e = System.currentTimeMillis();
        //System.out.println(e - s);
        if (map == null) {
            resultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            return resultDto;
        }
        map.put("chargeAmt", isSeries ? map.get("seriesAmt").toString() : map.get("chargeAmt").toString());
        BigDecimal chargeAmt = BigDecimal.valueOf(Double.parseDouble(map.get("chargeAmt").toString()));
        String percent = "0" ;
        if(map.get("distribution")!= null && ((String)map.get("distribution")).length() > 0){
            percent = (String) map.get("distribution");
        }
        if(chargeAmt.compareTo(new BigDecimal(0)) > 0){
            map.put("isPay",1);
            map.put("stuDisAmount",chargeAmt.multiply(BigDecimal.valueOf(Double.parseDouble(percent!=null&&percent.trim().length()>0?percent:"0"))).divide(new BigDecimal(100)));
        }else{
            map.put("isPay", 0);
            map.put("stuDisAmount",0);
        }
        //判断课程是否是当前用户的
        if (Long.parseLong(map.get("appId").toString()) == appid) {
            data.put("myLiveRoom", 1);
        } else {
            data.put("myLiveRoom", 0);
            //增加访问记录
            if (!isWechatClient) {//不是微信端就执行
                //课程id修改为实际ID（转播课ID就是转播课ID）
                visitCourseRecordService.insertRecord(appid, realCourseId, 0l, null, seriesid, "0");
            }
        }
        long roomId = (Long)map.get("roomId");//聊天室id
        data.put("isFollow", userFollowService.isFollowRoom(roomId, appid));//判断直播间是否关注过
        data.put("followNum", userFollowService.getCountByRoomId(roomId));//直播间关注数
        Boolean b = wechatOfficialService.isWechatOfficial(roomId);
        if(b){
            //判断是不是有成语流量,如果没有则充值,
            BigDecimal banlanceAmount =  new BigDecimal(dataChargeRecordMapper.getBanlanceByUserId(appid));
            Long reduceDataCount = dataChargeRecordMapper.getReduceDataCountbByAppId(appid);
            if(reduceDataCount == null){reduceDataCount = 0l;}
            if(banlanceAmount.subtract(new BigDecimal(reduceDataCount)).compareTo(new BigDecimal(0)) >=0){
                data.put("isWechatOfficial","0"); //不需要充值流量
            }else{
                data.put("isWechatOfficial","1"); //流量不足
            }
        }else{
            data.put("isWechatOfficial","0"); //不需要充值流量
        }
        long e2 = System.currentTimeMillis();
        //System.out.println(e2 - e);

        if (seriesid == null || seriesid == 0l) {
            map.put("studyCount", joinCourseRecordService.getCountByCourseId((relayId != null && relayId.length() >= SystemCofigConst.RELAY_COURSE_ID_LENTH) ? Long.parseLong(relayId) : id));// 学习参加人数
        } else {
            map.put("studyCount", studyRecordService.getStudyRecordCount((relayId != null && relayId.length() >= SystemCofigConst.RELAY_COURSE_ID_LENTH) ? Long.parseLong(relayId) : id));// 学习参加人数
        }
        long e3 = System.currentTimeMillis();
        //System.out.println(e3 - e2);

        List<CourseWare> courseWaresList = this.getCourseWare(id);
        //courseWareMapper.getCourseWare(id);
        data.put("courseWaresList", courseWaresList);
        Date startTime = (Date) map.get("startTime");
        if (startTime != null) {
            map.put("startTime", DateUtil.format(startTime, "yyyy-MM-dd HH:mm"));
            if (startTime.compareTo(new Date()) > 0) {
                map.put("proTimeH5", (startTime.getTime() - (new Date().getTime())));
            } else {
                map.put("proTimeH5", 0);
            }
        }

        long e4 = System.currentTimeMillis();
        //System.out.println(e4 - e3);

        Date endTime = (Date) map.get("endTime");
        String liveTimeStatus = "2";
        if (startTime != null) {
            liveTimeStatus = getLiveTimeStatus(startTime, endTime);
        }
        map.put("liveTimeStatus", liveTimeStatus);
        /*//分销比例
        String divideScaleDesc = systemParaRedisUtil.getCourseDivideScaleByValue(map.get("divideScale").toString());
        if (Utility.isNullorEmpty(divideScaleDesc)) divideScaleDesc = "0";
        map.put("divideScaleDesc", divideScaleDesc);*/
        data.put("course", map);
        //老师课程数量
        long teachAppId = (Long) map.get("appId");

        long e5 = System.currentTimeMillis();
        //System.out.println(e5 - e4);

        int teachCourseCount = this.getCourseCountByAppId(teachAppId,"");
        long e6 = System.currentTimeMillis();
        //System.out.println(e6 - e5);

        data.put("teachCourseCount", teachCourseCount);
        //老师所有课程参加学习人数:普通单节课是报名人数，如果是系列课
        long studyAllCount = joinCourseRecordService.getCountByRoomId(roomId);
        long e7 = System.currentTimeMillis();
        //System.out.println(e7 - e6);
        data.put("studyAllCount", studyAllCount);
        long commentCount = 0l;
        //已经去掉;
        //courseCommentService.getCommentSumByCourseId(id);
        data.put("commentCount", commentCount);
        //系列课数量(新加)
        int seriesCourseCount = this.getSerisCourseCount(roomId);
        long e8 = System.currentTimeMillis();
        //System.out.println(e8 - e7);
        data.put("seriesCourseCount", seriesCourseCount);
        data.put("isJoin", isJoinStatus);//是否购买
        int isSuperAdmin = userService.findSystemAdminByUserId(appid);
        if(isSuperAdmin > 0){
            data.put("isJoin", "1");//是否购买
        }

        //获取简介图片列表
        List<CourseImg> courseImgList = this.getCourseImgList(id);
        long e9 = System.currentTimeMillis();
        //System.out.println(e9 - e8);
        List<CourseImg> newCourseImgList = new ArrayList<CourseImg>();
        data.put("courseImgList", courseImgList);
        log.info("courseImgList :" + JsonUtil.toJsonString(courseImgList));
        if (!isWechatClient) {
            if (Utility.isNullorEmpty(v)) {
                v = "0";
            }
            v = v.replace(".", "");
            if (Integer.valueOf(v) <= 140) {
                for (CourseImg courseImg : courseImgList) {

                    newCourseImgList.add(courseImg);
                }
                data.put("courseImgList", newCourseImgList);
            }
        }
        if (data.get("courseImgList") != null) {
            List<CourseImg> newCourseImg = new ArrayList<CourseImg>();
            if (isWechatClient && teachAppId == appid) {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        courseImg.setAddress(courseImg.getLocalIds());
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    newCourseImg.add(courseImg);
                }
            } else {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && courseImg.getAddress().startsWith("http")) {
                        newCourseImg.add(courseImg);
                    } else {
                        if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        } else {
                            newCourseImg.add(courseImg);
                        }
                    }
                }
            }
            data.put("courseImgList", newCourseImg);
            log.info("newCourseImgList :" + JsonUtil.toJsonString(newCourseImg));
        }
        String managers = courseManagerService.findAllManagerRealByCourseId(id);
        long e10 = System.currentTimeMillis();
        //System.out.println(e10 - e9);

        data.put("managerId", managers);
        data.put("isManager", Utility.findById(managers, String.valueOf(appid)) ? "1" : "0");
        data.put("gagUserId", gagService.findUserIdByCourseId(id));

        //是不是老师，不是录播课程\课程没结束
        if (Long.parseLong(map.get("appId").toString()) == appid
                && "0".equals(map.get("isRecorded")) && endTime == null) {
            LiveChannel liveChannel = qiNiuliveChannelService.getDefaultLiveChannel();
            String originUrl = qiNiuliveChannelService.getPushAddress((String) map.get("pushAddress"), liveChannel  , String.valueOf(id));
            data.put("pushAddress", originUrl);
        } else {
            data.put("pushAddress", "");
        }
        //wha - 微信加入录播课字段 录播课未到时间 不让学生观看
        if(map!=null&&map.get("isRecorded")!=null){
            data.put("isRecorded",map.get("isRecorded").toString());
        }
        CourseCard card = coursePrivateCardMapper.findCardUrlByCourseId(id);
        if(card != null){
            data.put("modelUrl", card.getModelUrl());
            data.put("cardUrl", card.getCardUrl());
        }
        resultDto.setData(data);
        return resultDto;
    }

    @Override
    public ActResultDto getSeriesCourseInfo(long seriesId, long appid, boolean isWechatClient, String v) {
        ActResultDto resultDto = new ActResultDto();
        Map data = new HashMap();
        //获取系列课程信息
        Map map = courseMapper.getCourseInfoDetails(seriesId);
        if (map == null) {
            resultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            return resultDto;
        }
        if ("1".equals(map.get("status").toString()) || "1".equals(map.get("isDelete").toString())) {
            resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
        }
        map.put("chargeAmt", map.get("chargeAmt").toString());
        String percent = "0";
        if(map.get("distribution")!= null && ((String)map.get("distribution")).length() > 0){
            percent = (String) map.get("distribution");
        }else{
            percent = (String) map.get("divideScale");
        }
        BigDecimal chargeAmt = BigDecimal.valueOf(Double.parseDouble(map.get("chargeAmt").toString()));
        if(chargeAmt.compareTo(new BigDecimal(0)) > 0){
            map.put("isPay",1);
            map.put("stuDisAmount",chargeAmt.multiply(new BigDecimal(Double.parseDouble(percent!=null?percent:"0"))).divide(new BigDecimal(100)));
        }else{
            map.put("isPay",0);
            map.put("stuDisAmount",0);
        }
        //判断课程是否是当前用户的
        if (Long.parseLong(map.get("appId").toString()) == appid) {
            data.put("myLiveRoom", 1);
        } else {
            data.put("myLiveRoom", 0);
            if (!isWechatClient) {//不是微信端就执行
                visitCourseRecordService.insertRecord(appid, seriesId, 0l, null);
            }
        }
        //获取简介图片列表
        List<CourseImg> courseImgList = this.getCourseImgList(seriesId);
        List<CourseImg> newCourseImgList = new ArrayList<CourseImg>();
        data.put("courseImgList", courseImgList);
        if (!isWechatClient) {
            if (Utility.isNullorEmpty(v)) {
                v = "0";
            }
            v = v.replace(".", "");
            if (Integer.valueOf(v) <= 140) {
                for (CourseImg courseImg : courseImgList) {

                    newCourseImgList.add(courseImg);
                }
                data.put("courseImgList", newCourseImgList);
            }
        }
        Long teachAppId = Long.parseLong(map.get("appId").toString());
        if (data.get("courseImgList") != null) {
            List<CourseImg> newCourseImg = new ArrayList<CourseImg>();
            if (isWechatClient && teachAppId == appid) {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        courseImg.setAddress(courseImg.getLocalIds());
                    }
                    newCourseImg.add(courseImg);
                }
            } else {
                for (CourseImg courseImg : courseImgList) {
                    if (!Utility.isNullorEmpty(courseImg.getAddress())) {
                        courseImg.setShowAddress(courseImg.getAddress() + "?x-oss-process=style/mst800");
                    }
                    if (!Utility.isNullorEmpty(courseImg.getAddress()) && courseImg.getAddress().startsWith("http")) {
                        newCourseImg.add(courseImg);
                    } else {
                        if (!Utility.isNullorEmpty(courseImg.getAddress()) && !courseImg.getAddress().startsWith("http")) {
                        } else {
                            newCourseImg.add(courseImg);
                        }
                    }
                }
            }
            data.put("courseImgList", newCourseImg);
        }

//        if(Utility.isNullorEmpty(map.get("remark")) && courseImgList.size()==0)
//        {
//            map.put("remark", Const.DEFAULT_SERIES_CONTENT);
//        }
        //获取直播课系列课总数
        map.put("seriesCount", courseMapper.getSeriesCourseCount(appid));
        map.put("courseCount",courseMapper.getCourseCount(appid));
        data.put("seriesCourse", map);
        //已开课的
        //int alreadyCourseCount = courseMapper.getAlreadyCourseCountByAppId(appid);
        data.put("alreadyCourseCount", map.get("updatedCount"));
        //未开课的
        int notCourseCount = 0;
        if (!Utility.isNullorEmpty(map.get("coursePlanCount"))) {
            notCourseCount = (Integer) map.get("coursePlanCount") - (Integer) map.get("endedCount");
        }
        data.put("notCourseCount", notCourseCount);

        long roomId = Long.parseLong(map.get("roomId").toString());//聊天室id
        data.put("isFollow", userFollowService.isFollowRoom(roomId, appid));//判断直播间是否关注过
        map.put("studyCount", joinCourseRecordService.getCountByCourseId(seriesId));// 学习参加人数


        //查询是否报名
        String isJoinStatus = "";
        if (appid != 0) {
            isJoinStatus = joinCourseRecordService.getJoinCourseStatus(appid, seriesId);
        }
        data.put("isJoin", isJoinStatus);
		//超级管理员默认为购买
        int isSuperAdmin = userService.findSystemAdminByUserId(appid);
        if(isSuperAdmin > 0){
            data.put("isJoin", "1");//是否购买
        }
        //老师课程数量
        //long teachAppId1 = (Long) map.get("appId");
        int teachCourseCount = courseMapper.getCourseCountByAppId(teachAppId);
        data.put("teachCourseCount", teachCourseCount);
        //老师所有课程参加学习人数
        long studyAllCount = joinCourseRecordService.getCountByRoomId(roomId);
        data.put("studyAllCount", studyAllCount);
        //系列课数量(新加)
        int seriesCourseCount = this.getSerisCourseCount(roomId);
        long e8 = System.currentTimeMillis();
        //System.out.println(e8 - e7);
        data.put("seriesCourseCount", seriesCourseCount);
        //long commentCount = courseCommentService.getSeriesCourseCommentSum(seriesId);
        data.put("commentCount", 0l);
        CourseCard card = coursePrivateCardMapper.findCardUrlByCourseId(seriesId);
        if(card != null){
            data.put("modelUrl", card.getModelUrl());
            data.put("cardUrl", card.getCardUrl());
        }

        resultDto.setData(data);

        return resultDto;
    }


    /**
     * 通用视频时间状态 0-预告 1-直播中 2-已结束
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public String getLiveStatus(Date startTime, Date endTime) {
        Date currDate = new Date();
        String liveTimeStatus = "0";//结束的
        if (endTime == null) {
            if (currDate.compareTo(startTime) < 0) {
                liveTimeStatus = "1";//预告
            } else {
                liveTimeStatus = "2";//直播中的
            }
        }
        return liveTimeStatus;
    }
    public String getLiveTimeStatus(Date startTime, Date endTime) {
        Date currDate = new Date();
        String liveTimeStatus = "2";//结束的
        if (endTime == null) {
            if (currDate.compareTo(startTime) < 0) {
                liveTimeStatus = "0";//预告
            } else {
                liveTimeStatus = "1";//直播中的
            }
        }
        return liveTimeStatus;
    }

    @Override
    public boolean getLivePwd(long id, String livePwd) {
        boolean b = false;
        Course c = courseMapper.getCourse(id);
        if (c != null) {
            if (MD5PassEncrypt.getMD5Str(livePwd).equals(c.getLivePwd())) {
                b = true;
            }
        }
        return b;
    }

    private String getBase642Images(String base64) {
        if (Utility.isNullorEmpty(base64)) {
            return null;
        }

        String[] base64s = base64.split("&&");
        StringBuffer sb = new StringBuffer("");
        for (String b : base64s) {
            String url = picUtil.base64ToUrl(b);
            if (!StringUtils.isEmpty(url)) {
                sb.append(url).append(";");
            }
        }
        return sb.toString();
    }


    @Override
    public ActResultDto getCourseWareById(Long id) {
        ActResultDto resultDto = new ActResultDto();
        if (id == null) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        List<Map> list = courseWareMapper.getCourseWarebyId(id);
        if (list != null && list.size() > 0) {
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(list);
            String classIndex = redisUtil.get(RedisKey.ll_course_class_index + id);
            if (StringUtils.isEmpty(classIndex)) {
                classIndex = "0";
            }
            resultDto.setExt(classIndex);
            return resultDto;
        } else {
            resultDto.setCode(ReturnMessageType.NO_COURSE_WARE.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE_WARE.getMessage());
            return resultDto;
        }
    }

    @Override
    public List<CourseDto> getCourseListByLiveRoom(long id) {
        List<CourseDto> list = courseMapper.getCourseListByLiveRoom(id);
        if (list.size() > 0) {
            for (CourseDto c : list) {
                String createTime = DateUtil.format(c.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                c.setCreateTimes(createTime);
                Date date = new Date();
                if (c.getEndTime() == null) {
                    if (c.getStartTime().getTime() < date.getTime()) {
                        c.setLiveStatus("1");//直播中
                    } else {
                        c.setLiveStatus("0");//待直播
                    }
                } else {
                    c.setLiveStatus("2");//回放
                }
            }
        }
        return list;
    }


    /**
     * 搜索课程
     *
     * @param name
     * @return
     */
    @Override
    public ActResultDto findCoursebyName(String name, Integer offset, Integer pageSize) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = courseMapper.findCourseByNamePage(name, dg);
        for (Map map : list) {
            if (map.get("startTime") != null) {
                map.put("courseStartTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
            } else {
                map.put("courseStartTime", "");
            }
            if (map.get("endTime") != null) {
                map.put("courseEndTime", map.get("endTime").toString().substring(0, map.get("endTime").toString().length() - 5));
            } else {
                map.put("courseEndTime", "");
            }
            map.put("chargeAmt", map.get("chargeAmt").toString());
        }
        if (list != null && list.size() > 0) {
            actResultDto.setData(list);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            return actResultDto;
        }
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        return actResultDto;
    }

    @Override
    public ActResultDto findCoursebyNameV2(String name, Integer offset, Integer pageSize, boolean isHaveRecord) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);


        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        List<Map> list = courseMapper.findCourseByNameV2Page(name, dg, isRecorded);
        for (Map map : list) {
            if (map.get("startTime") != null) {
                map.put("courseStartTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
            } else {
                map.put("courseStartTime", "");
            }
            if (map.get("endTime") != null) {
                map.put("courseEndTime", map.get("endTime").toString().substring(0, map.get("endTime").toString().length() - 5));
            } else {
                map.put("courseEndTime", "");
            }
            map.put("chargeAmt", map.get("chargeAmt").toString());
        }
        if (list != null && list.size() > 0) {
            actResultDto.setData(list);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            return actResultDto;
        }
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        return actResultDto;
    }

    /**
     * 录音设置获取录音时长
     *
     * @param courseId
     * @return
     */
    @Override
    public ActResultDto getRecTimeByCourseId(Long courseId) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId == 0) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        Course course = courseMapper.getCourse(courseId);
        if (course == null) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        Map map = new HashMap();
        map.put("whenLong", course.getRecoTime() == null ? 60 : course.getRecoTime());
        String sysParaReco = systemParaRedisUtil.getRecordingLength();
        try {
            map.put("minValue", sysParaReco.split(",")[0]);
            map.put("maxValue", sysParaReco.split(",")[1]);
        } catch (Exception e) {
            map.put("minValue", 10);
            map.put("maxValue", 180);
        }
        resultDto.setData(map);
        return resultDto;
    }

    @Override
    public ActResultDto setRecoTimeById(Long courseId, Integer recoTime) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId == 0) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        if (recoTime == null || recoTime == 0) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        try {
            courseMapper.updateRecoTime(recoTime, courseId);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } catch (Exception e) {
            resultDto.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
        }
        return resultDto;
    }

    /**
     * 获取课程课程直播间ID
     *
     * @param id
     * @return
     */
    @Override
    public long getRoomIdById(long id) {
        Long roomId = courseMapper.getRoomIdById(id);
        if (roomId != null) return roomId.longValue();
        return 0;
    }

    /**
     * 老师的设置空间
     *
     * @param courseId
     * @return map
     */
    @Override
    public Map getTeacherSetByCourseId(long courseId, long appId) {
        Map map = new HashMap();
        //课程详情
        Map courseMap = courseMapper.getCourseInfoDetails(courseId);
        map.put("courseMap", courseMap);
        long roomId = Long.parseLong(courseMap.get("roomId").toString());
        //老师的Id
        long tId = Long.parseLong(courseMap.get("appId").toString());
        //判断课程是否是当前用户的
        if (appId == tId) {
            map.put("myLiveRoom", 1);
        } else {
            map.put("myLiveRoom", 0);
        }
        //老师的课程数
        long teacherCourseCount = this.getCourseCountByAppId(tId,"");
        map.put("teacherCourseCount", teacherCourseCount);
        //判断是否关注该直播间
        if (userFollowService.isFollowRoom(roomId, appId)) {
            map.put("isFollow", 1);//已关注
        } else {
            map.put("isFollow", 0);//关注
        }
        //直播间关注数
        map.put("followNum", userFollowService.getCountByRoomId(roomId));
        //全体成员
        long seriesCourseId = Long.parseLong(courseMap.get("seriesCourseId").toString());
        if (seriesCourseId > 0) {
            courseId = seriesCourseId;
        }
        List<Map> memberList = joinCourseRecordMapper.getCourseRecordList(courseId);
        map.put("memberList", memberList);
        return map;
    }

    @Override
    public ActResultDto findCourseInfoById(Long courseId, long appId) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId != null && courseId > 0) {
            //课程信息:
            Map map = courseMapper.getCourseInfoById(courseId);
            map.put("studyCount", joinCourseRecordService.getCountByCourseId(courseId));
            map.put("liveType", map.get("liveWay"));
            Date courseStartTime = (Date) map.get("stratTime");
            map.put("stratTime", DateUtil.format(courseStartTime, "yyyy-MM-dd HH:mm"));
            int roomId = 0;
            if (map.get("roomId") != null) {
                roomId = Integer.parseInt(String.valueOf(map.get("roomId")));
            }
            //查询课程数量
            int courseCount = courseMapper.getCountByRoomId(roomId);
            map.put("courseCount", courseCount);
            //查询课件图片
            List<Map> coursePhoto = courseWareMapper.getCourseWarebyId(courseId);
            map.put("coursePhoto", coursePhoto);
            //评价,最近2两条记录
            List<Map> lastCommentList = courseCommentService.getLastListByCourseId(courseId);
            for (Map map1 : lastCommentList) {
                Date startTime = (Date) map1.get("createTime");
                if (startTime != null) {
                    map1.put("createTime", DateUtil.format(startTime, "yyyy-MM-dd HH:mm"));
                }
            }
            map.put("lastCommentList", lastCommentList);

            //是否关注
            long courseAppId = Long.parseLong(map.get("appId").toString());
            long courseRoomId = Long.parseLong(map.get("roomId").toString());
            map.put("isFollow", userFollowService.isFollowRoom(courseRoomId, appId));
            //直播间关注人数
            map.put("followNum", userFollowService.getCountByRoomId(courseRoomId));
            //是否是当前用户
            map.put("isCurrentUser", courseAppId == appId);
            map.put("roomId", courseRoomId);
            List<CourseImg> courseImgList = this.getCourseImgList(courseId);
            map.put("courseImgList", courseImgList);
            resultDto.setData(map);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            return resultDto;
        } else {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
    }

    @Override
    public ActResultDto getCoursePushAddr(Long courseId, Long appId, String machineModel) {
        Course course = this.getCourseFromRedis(courseId);
        if (course.getEndTime() != null) {
            return ActResultDto.fail(ReturnMessageType.LIVE_COURSE_IS_END.getCode());
        }

        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);

        //新加入分享地址,内容,图片
        Map map = new HashMap();
        map.put("shareContent", course.getRemark());
        map.put("shareTitle", course.getLiveTopic());

        //竖屏处理封面图片， 将封面图片改为高斯莫负
        if ("1".equals(course.getIsVerticalScreen()) && !StringUtils.isEmpty(course.getVerticalCoverssAddress())) {
            course.setCoverssAddress(course.getVerticalCoverssAddress());
        }

        map.put("coverssAddress", course.getCoverssAddress());
        map.put("shareAddress", website + "/weixin/courseInfo?id=" + course.getId() +
                "&invitationAppId=" + appId + "&fromType=1" + "&seriesid=" + course.getSeriesCourseId() + "&isSeries=" + course.getIsSeriesCourse());
        map.put("managerId", courseManagerService.findAllManagerRealByCourseId(course.getId()));
        map.put("gagUserId", gagService.findUserIdByCourseId(course.getId()));

        map.put("cleanScreenTime", course.getCleanScreenTime());
        map.put("serverTime", System.currentTimeMillis());
        map.put("isRecorded", course.getIsRecorded());
        Boolean bo = roomFuncService.isRoomFunc(course.getRoomId(), FuncCode.live_watermark.getValue());
        if (bo) {
            map.put("isWaterMark", 0); //添加水印
        } else {
            map.put("isWaterMark", 1); //不添加水印
        }
        boolean hasConnectAuth = roomFuncService.isRoomFunc(course.getRoomId() , FuncCode.live_connect.getValue());
        map.put("hasConnectAuth",hasConnectAuth ? "1" : "0");
        map.put("isCanConnect",course.getCanConnect());


        liveService.setLiveInfo(map, course);
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(liveChannel);
        resultDto.setExt(map);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }


    public ActResultDto getCourseLiveAddr(Long courseId) {
        Course course = this.getCourseFromRedis(courseId);
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        log.info("课程：{}的播放地址：{}", courseId, liveChannel.getPlayAddr3());
        return ActResultDto.success().setData(liveChannel);
    }

    @Override
    public List<Map> getMoreLiveCourse(Long liveStatus, Long courseTypeId, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setCurrentPage(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> list = null;
        if (courseTypeId != null && courseTypeId == 0) courseTypeId = null;
        if (liveStatus == 1) {
            list = courseMapper.getMoreLiveCoursePage(courseTypeId, dg);
        } else {
            list = courseMapper.getMoreLiveCourseNowPage(courseTypeId, dg);
        }
        String date_fmt_str = "yyyy-MM-dd";
        String date_time_fmt_str = "HH:mm";
        String date_time_fmt2_str = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_fmt_str);
        SimpleDateFormat date_time_fmt = new SimpleDateFormat(date_time_fmt_str);
        SimpleDateFormat date_time_fmt3 = new SimpleDateFormat(date_time_fmt2_str);
        if (list.size() > 0) {
            for (Map map : list) {
                String currDate = Utility.getCurDateTimeStr(date_fmt_str);
                Timestamp startTime = (Timestamp) map.get("startTime");
                Date d = new Date(startTime.getTime());
                String dateStr = Utility.getDateTimeStr(d, simpleDateFormat);
                Date tomorrow = DateUtil.getDayAfter(new Date(), 1);
                String tomorrowStr = Utility.getDateTimeStr(tomorrow, simpleDateFormat);
                String startTimeStr = "";
                if (currDate.equals(dateStr)) {
                    startTimeStr = "今天" + Utility.getDateTimeStr(d, date_time_fmt);
                } else if (tomorrowStr.equals(dateStr)) {
                    startTimeStr = "明天" + Utility.getDateTimeStr(d, date_time_fmt);
                } else {
                    startTimeStr = Utility.getDateTimeStr(d, date_time_fmt3);
                }
                map.put("startTime", startTimeStr);
//                map.put("joinCount", joinCourseRecordService.getCountByCourseId((Long) map.get("id")));
            }
        }
        return list;
    }

    @Override
    public Map getCourseIsFreeSign(Long courseId) {
        Map map = new HashMap();
        Course c = courseMapper.getCourse(courseId);
        if (c.getSeriesCourseId() > 0) {
            c = courseMapper.getCourse(c.getSeriesCourseId());
            courseId = c.getId();
        }
        long num = joinCourseRecordMapper.getPayCourseNum(courseId);
        if (c != null && c.getChargeAmt().compareTo(new BigDecimal(0)) > 0 && num == 0) {
            map.put("isFreeSign", 0);//付费课程未购买
        } else {
            map.put("isFreeSign", 1);//课程免费或购买
        }
        return map;
    }

    @Override
    @UpdateSeriesCourseTime
    public ActResultDto setCourseDown(long id, long appId) throws Exception {
        ActResultDto ac = new ActResultDto();
        
        //转播课下架
        if(id>=SystemCofigConst.RELAY_COURSE_ID_SIZE){
        	return this.courseRelayService.myRelayCourseDown(id);
        }
        
        Course c = courseMapper.getCourseByAppidAndId(id, appId);
        if (c != null && "0".equals(c.getStatus())) {
            //免费的课程下架
            if (c.getChargeAmt().compareTo(new BigDecimal(0)) == 0) {
            } else {
                //非免费的
                if (joinCourseRecordMapper.getPayCourseNum(id) > 0) {//有人买
                    ac.setCode(ReturnMessageType.COURSE_HAVE_PAY.getCode());
                    ac.setMessage(ReturnMessageType.COURSE_HAVE_PAY.getMessage());
                    return ac;
                }
            }
            
            //判断该课程是有人付费转播、是否有人付费购买转播课，满足其一，则不允许下架
            ac=relayCourseDown(id);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(ac.getCode())){
            	return ac;
            }
            
            setCourseClose(c);
            
            //如果是系列课里面的单节课，则发送更新系列课时间队列
            UpdateSeriesCourseTimeInterceptor.setSeriesId(c.getSeriesCourseId());
            
            Course course = courseBaseService.getCourseFromRedis(id);
        	if (course != null) {
        		appMsgService.updateMsgCourseStatus(id, "1" , course.getRoomId());
        	}
        	String courseKey = RedisKey.ll_course + id;
        	redisUtil.del(courseKey);
        	/*
        	 * 下架转播课的逻辑在上面的下架课程里面都有了
        	//下架成功，下架转播课
        	UpdateRelayCourse updateRelayCourse = SpringContextUtil.getBeanByType(UpdateRelayCourse.class);
        	updateRelayCourse.setCourseId(course.getId());
        	F.submit(updateRelayCourse);
        	*/
            
            return ac;
        }
        ac.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
        ac.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
        return ac;
    }

    public void setCourseClose(Course c) {
        courseMapper.setCourseDown(c.getId());
        if (Utility.isNullorEmpty(c.getEndTime())) {
            try {
                endLiveService.endLive(c.getId(), c.getAppId());
                SystemLogUtil.saveSystemLog(LogType.course_user_end.getType(), "0"
                        , c.getAppId(), String.valueOf(c.getAppId())
                        , String.valueOf(c.getId()), "课程：" + c.getLiveTopic() + "已被老师下架关闭");
                redisUtil.del(RedisKey.series_course_count + c.getRoomId());
                redisUtil.del(RedisKey.teacher_course_count + c.getAppId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
    }

    @Override
    public ActResultDto getAllCourses(long id) {
        ActResultDto resultDto = new ActResultDto();
        Map homeCourse = new HashMap();
        List<Map> doingAndNotCourse = courseMapper.findDoingAndNotCourse(id);
        for (Map map : doingAndNotCourse) {
            long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
            String poorTime = DateUtil.getDatePoor2(new Date(), DateUtil.format(map.get("startTime").toString()));
            map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
            map.put("shareTile", map.get("title"));
            map.put("shareContent", map.get("remark"));
            map.put("poorTime", poorTime);
            map.put("joinCount", joinCount);
            map.put("shareAddress", "");
        }
        homeCourse.put("livingCourse", doingAndNotCourse);
        log.info("未开播和已开播的课程");
        List<Map> alreadyCourse = courseMapper.findAlreadyCourse(id);
        for (Map map : alreadyCourse) {
            long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
            map.put("poorTime", "");
            map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
            map.put("shareTile", map.get("title"));
            map.put("shareContent", map.get("remark"));
            map.put("shareAddress", "");
            map.put("joinCount", joinCount);
        }
        homeCourse.put("endCourse", alreadyCourse);
        log.info("历史开播课程");
        resultDto.setData(homeCourse);
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return resultDto;
    }

    @Override
    public ActResultDto getMoreCoursePage(long appId, String type, Integer pageSize, Integer offset) {
        ActResultDto resultDto = new ActResultDto();
        DataGridPage gridPage = new DataGridPage();
        gridPage.setOffset(offset);
        gridPage.setPageSize(pageSize);
        List<Map> courseList = new ArrayList<Map>();
        if (StringUtils.isEmpty(type)) {  //查询已开播和正在开播的课程
            courseList = courseMapper.findDoingAndNotCoursePage(appId, gridPage);
        } else {                          //查询历史课程
            courseList = courseMapper.findAlreadyCoursePage(appId, gridPage);
        }
        for (Map map : courseList) {
            long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
            map.put("joinCount", joinCount);
            map.put("poorTime", "");
            if (StringUtils.isEmpty(type)) {  //查询已开播和正在开播的课程
                String poorTime = DateUtil.getDatePoor2(new Date(), DateUtil.format(map.get("startTime").toString()));
                map.put("poorTime", poorTime);
            }
            map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
            map.put("shareTile", map.get("title"));
            map.put("shareContent", map.get("remark"));
            map.put("shareAddress", "");
        }
        if (courseList != null && courseList.size() > 0) {
            resultDto.setData(courseList);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * app端获取课程数据
     *
     * @param courseId
     * @return
     */
    @Override
    public Map findCourseInfoById(long courseId) {
        Map map = courseMapper.findCourseInfoById(courseId);
        if (map == null) {
            return null;
        }
        map.put("startTime", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 2));
        long joinCount = joinCourseRecordService.getCountByCourseId(courseId);
        map.put("joinCount", joinCount);
        long userFollowCount = userFollowService.getCountByRoomId(Long.valueOf(map.get("roomId").toString()));
        map.put("userFollowCount", userFollowCount);
        int courseCount = courseMapper.getCountByRoomId(Long.valueOf(map.get("roomId").toString()));
        map.put("courseCount", courseCount);
        return map;
    }

    /**
     * 获取该直播间的课程数
     *
     * @param noLearnOffset
     * @param pageSize
     * @param roomId
     * @param alreadyLearnOffset
     * @return
     */
    @Override
    public ActResultDto findCourseByRoomId(Integer noLearnOffset, Integer pageSize, Long roomId, Integer alreadyLearnOffset, boolean isHaveRecord) {
        ActResultDto resultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        dg.setOffset(noLearnOffset);
        dg.setPageSize(pageSize);
        if (roomId == null || roomId == 0l) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }

        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        List<Map> list = courseMapper.findNoLeanCourseByRoomIdPage(roomId, dg, isRecorded);
        if (list != null && list.size() > 0) {
            for (Map map : list) {
                long joinCount = joinCourseRecordService.getCountByCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
                String poorTime = "";
                if (map.get("startTime") != null) {
                    poorTime = DateUtil.getDatePoor2(new Date(), DateUtil.format(map.get("startTime").toString()));
                    map.put("startTime", DateUtil.transFormationStringDate2(DateUtil.format((Date) map.get("startTime"))));
                    Date st = DateUtil.format(map.get("startTime").toString());
                    if(st!=null&&st.compareTo(new Date()) <= 0 ){
                        map.put("isLive",0); //正在直播
                    }else{
                        map.put("isLive",1);   //未开播
                    }
                    Date startTime = DateUtil.format(map.get("startTime").toString());
                    if(startTime!=null&&startTime.compareTo(new Date()) <= 0 ){
                        map.put("liveTimeStatus",1); //正在直播
                    }else{
                        map.put("liveTimeStatus",0);   //未开播
                    }

                } else {
                    map.put("startTime", "");
                }
                map.put("poorTime", poorTime);
                map.put("joinCount", joinCount);
                map.put("chargeAmt", map.get("chargeAmt").toString());
            }
            resultDto.setData(list);
        }
        List<Map> mapList = findAlreadyCourseLearnByRoomId(alreadyLearnOffset, pageSize, roomId, isRecorded);
        if (mapList != null && mapList.size() > 0) {
            resultDto.setExt(mapList);
        }
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        if ((list == null || list.size() == 0) && (mapList == null || mapList.size() == 0)) {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    public List<Map> findAlreadyCourseLearnByRoomId(Integer offset, Integer pageSize, long roomId, String isRecorded) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = courseMapper.findAlreadyCourseLearnByRoomIdPage(roomId, dg, isRecorded);
        for (Map map : list) {
            map.put("poorTime", "");
            map.put("endTime",
                    map.get("endTime").toString().substring(0, map.get("endTime").toString().length() - 2));
            if(map.get("startTime") != null){
                map.put("startTime",
                        DateUtil.transFormationStringDate2(DateUtil.format((Date) map.get("startTime"))));
            }
        }
        return list;
    }

    public List<Map> getMyCourseList(long seriesCourseId, long appid, Integer offset, Integer pageSize, boolean isHaveRecord , boolean isOldVersion) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        List<Map> list = courseMapper.getMyCourseListPage(seriesCourseId, dg, isRecorded);
        if (list.size() > 0) {
            for (Map map : list) {
                Object startTime = map.get("startTime");
                map.put("startTime", DateUtil.format((Date) startTime, "yyyy-MM-dd HH:mm"));
                Date endTime = (Date) map.get("endTime");
                if (endTime == null) {
                    map.put("endTime", "");
                } else {
                    map.put("endTime", DateUtil.format((Date) map.get("endTime"), "yyyy-MM-dd HH:mm"));
                }

                String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                //如果是老版本
                if (isOldVersion) {
                    liveTimeStatus = getLiveTimeStatus((Date) startTime, endTime);
                }

                map.put("liveTimeStatus", liveTimeStatus);
                String managerid = courseManagerService.findAllManagerRealByCourseId(Long.parseLong(map.get("id").toString()));
                map.put("managerId", managerid);
                map.put("isManager", Utility.findById(managerid, String.valueOf(appid)) ? "1" : "0");
                map.put("gagUserId", gagService.findUserIdByCourseId(Long.parseLong(map.get("id").toString())));
            }
        }
        return list;
    }

    /**
     * 转播课
     * @param seriesCourseId
     * @param appid
     * @param offset
     * @param pageSize
     * @param isHaveRecord
     * @param isOldVersion
     * @return
     */
    public List<Map> getMyRelayCourseList(long seriesCourseId, long appid, Integer offset, Integer pageSize, boolean isHaveRecord , boolean isOldVersion) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }

        List<Map> list = courseMapper.getMyRelayCourseListPage(seriesCourseId, dg, isRecorded);
        if (list.size() > 0) {
            for (Map map : list) {
                Object startTime = map.get("startTime");
                map.put("startTime", DateUtil.format((Date) startTime, "yyyy-MM-dd HH:mm"));
                Date endTime = (Date) map.get("endTime");
                if (endTime == null) {
                    map.put("endTime", "");
                } else {
                    map.put("endTime", DateUtil.format((Date) map.get("endTime"), "yyyy-MM-dd HH:mm"));
                }

                String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                //如果是老版本
                if (isOldVersion) {
                    liveTimeStatus = getLiveTimeStatus((Date) startTime, endTime);
                }

                map.put("liveTimeStatus", liveTimeStatus);
                String managerid = courseManagerService.findAllManagerRealByCourseId(Long.parseLong(map.get("id").toString()));
                map.put("managerId", managerid);
                map.put("isManager", Utility.findById(managerid, String.valueOf(appid)) ? "1" : "0");
                map.put("gagUserId", gagService.findUserIdByCourseId(Long.parseLong(map.get("id").toString())));
            }
        }
        return list;
    }


    /**
     * 系列课下架
     *
     * @param courseId
     * @return
     */
    @Override
    @UpdateSeriesCourseTime
    public ActResultDto closeSeries(Long courseId, Long appId) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId < 1) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        //转播课下架
        if(courseId>=SystemCofigConst.RELAY_COURSE_ID_SIZE){
            CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
            if(courseRelayDto==null){
                resultDto.setMessage(ReturnMessageType.NOT_COLSE_COURSE.getMessage());
                resultDto.setCode(ReturnMessageType.NOT_COLSE_COURSE.getCode());
                return resultDto;
            }
            Course c = courseBaseService.getCourse(courseRelayDto.getOriCourseId());
            if(c==null || c.getSeriesCourseId()>0){
                resultDto.setMessage(ReturnMessageType.NOT_COLSE_COURSE.getMessage());
                resultDto.setCode(ReturnMessageType.NOT_COLSE_COURSE.getCode());
                return resultDto;
            }
        	return this.courseRelayService.myRelayCourseDown(courseId);
        }

        Course course = courseMapper.getCourse(courseId);
        if (course != null) {
            if (course.getAppId() != appId) {
                resultDto.setMessage(ReturnMessageType.NOT_COLSE_COURSE.getMessage());
                resultDto.setCode(ReturnMessageType.NOT_COLSE_COURSE.getCode());
                return resultDto;
            }
            //如果是系列课里面的单节课，则发送更新系列课时间队列
            UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        }

        resultDto=relayCourseDown(courseId);
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())){
            return resultDto;
        }

        try {
            //免费的课程下架
            if (course.getChargeAmt().compareTo(new BigDecimal(0)) == 0) {
                setDown(course);
                //下架系列课下面的单节课
                setCourseDown(courseId);
                resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                return resultDto;
            } else {
                int num = joinCourseRecordMapper.getPayCourseNum(courseId);
                if (num < 1) {
                    setDown(course);
                    //下架系列课下面的单节课
                    setCourseDown(courseId);
                    resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                    resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                    return resultDto;
                } else {
                    resultDto.setMessage(ReturnMessageType.BUY_SERIES_COUNT.getMessage());
                    resultDto.setCode(ReturnMessageType.BUY_SERIES_COUNT.getCode());
                    return resultDto;
                }
            }
        } catch (Exception e) {
            resultDto.setMessage(ReturnMessageType.SERIES_OFF_THE_SHELF.getMessage());
            resultDto.setCode(ReturnMessageType.SERIES_OFF_THE_SHELF.getCode());
            return resultDto;
        }
    }


    private ActResultDto relayCourseDown(Long courseId){
        ActResultDto resultDto = new ActResultDto();
        //判断该课程是有人付费转播、是否有人付费购买转播课，满足其一，则不允许下架
        if(this.courseRelayService.hasRelayIncome(courseId) || this.courseRelayService.hasRelayCoursePayIncome(courseId)){
            resultDto.setCode(ReturnMessageType.COURSE_HAVE_PAY.getCode());
            resultDto.setMessage(ReturnMessageType.COURSE_HAVE_PAY.getMessage());
            return resultDto;
        }
        //删除转播课
        this.courseRelayService.deleteRelayCourseByOriCourse(courseId);
        return resultDto;
    }

    /**
     * 系列课下面的单节课下架
     * @param seriesId
     */
    public void setCourseDown(long seriesId){
        List<Course> list = courseMapper.getCourseBySeriesId(seriesId);
        for(Course course : list){
            courseMapper.closeSeries(course.getId());
            redisUtil.del(RedisKey.ll_course + course.getId());
        }
    }

    public void setDown(Course course) {
        courseMapper.closeSeries(course.getId());
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
        redisUtil.del(RedisKey.series_course_count + course.getRoomId());
        redisUtil.del(RedisKey.teacher_course_count + course.getAppId());
        //系列课里面的单节课，且没结束
        if (course.getSeriesCourseId() != 0 ) {
            try {
                endLiveService.endLive(course.getId(), course.getAppId());
                SystemLogUtil.saveSystemLog(LogType.course_user_end.getType(), "0"
                        , course.getAppId(), String.valueOf(course.getAppId())
                        , String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被老师下架关闭");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getSeriesSingleCourseBySeriesId(long id, long appid) {
        return courseMapper.getSeriesSingleCourseBySeriesId(id, appid);
    }

    @Override
    public ActResultDto limitedCoursePlanCount(Long seriesid, Long appid) throws Exception {
        ActResultDto resultDto = new ActResultDto();

        if (seriesid == null || seriesid == 0) {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        int seriesSingleCourseCount = courseMapper.getSeriesSingleCourseBySeriesId(seriesid, appid);
        int coursePlan = courseMapper.getCoursePlanById(seriesid, appid);
        if (seriesSingleCourseCount >= coursePlan) {
            resultDto.setMessage(ReturnMessageType.LIMIT_COURSE_PLAN_COUNT.getMessage());
            resultDto.setCode(ReturnMessageType.LIMIT_COURSE_PLAN_COUNT.getCode());
        }
        return resultDto;
    }

    @Override
    public Map getShareCourseTitle(long courseId) {
//        redisUtil.del(RedisKey.ll_course_share_title + courseId);
        String redisValue = redisUtil.get(RedisKey.ll_course_share_title + courseId);
        Map map = new HashMap();
        if (StringUtils.isEmpty(redisValue)) {
            Course course = courseMapper.getCourse(courseId);
            if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
                course=courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
            }
            if (course != null) {
                map.put("isSeries", course.getIsSeriesCourse());
                map.put("id", course.getId());
                String content = course.getRemark();
                if (StringUtils.isEmpty(content)) {
                    content = ShareConst.defult_share_content;
                }
                map.put("remark", content);
                map.put("title", course.getLiveTopic());
                map.put("coverssAddress", course.getCoverssAddress());
                map.put("chartAmount", course.getChargeAmt() == null ? new BigDecimal(0) : course.getChargeAmt());
                if (course.getSeriesCourseId() > 0) {
                    Course seriesCourse = courseMapper.getCourse(course.getSeriesCourseId());
                    if (seriesCourse != null) { //如果是系列课
                        if (seriesCourse.getChargeAmt() == null) seriesCourse.setChargeAmt(new BigDecimal(0));
                        if (seriesCourse.getChargeAmt().compareTo(new BigDecimal(0)) > 0) {
                            map.put("coverssAddress", seriesCourse.getCoverssAddress());
                            String seriesCourseCont = seriesCourse.getRemark();
                            if (StringUtils.isEmpty(seriesCourseCont)) {
                                seriesCourseCont = ShareConst.defult_share_content;
                            }
                            map.put("remark", seriesCourseCont);
                            map.put("title", seriesCourse.getLiveTopic());
                        }
                        map.put("id", seriesCourse.getId());
                        map.put("isSeries", seriesCourse.getIsSeriesCourse());
                        map.put("chartAmount", seriesCourse.getChargeAmt() == null ? new BigDecimal(0) : seriesCourse.getChargeAmt());
                    }
                }
                map.put("roomId", course.getRoomId());
                redisUtil.set(RedisKey.ll_course_share_title + courseId, JsonUtil.toJson(map));
                redisUtil.expire(RedisKey.ll_course_share_title + courseId, 60 * 10);
            }
        } else {
            map = JsonUtil.getObject(redisValue, Map.class);
        }
        return map;
    }


    @Override
    public ActResultDto clearScreenByChatRoomId(long courseId , Long userId) {
        Course course = getCourseFromRedis(courseId);
        long clearTime = new Date().getTime();
        courseMapper.clearScreenByChatRoomId(course.getChatRoomId(), clearTime);
        redisUtil.del(RedisKey.ll_course + courseId);
        Map msg2 = new HashMap();
        msg2.put("type", YunxinCustomMsgType.CLEAN_SCREEN_CMD.getType());
        Map val = new HashMap();
        val.put("value", clearTime);
        val.put("clearUserId" , userId);
        msg2.put("data", val);
        yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg2));
        redisUtil.lpush(RedisKey.ll_clear_chatmsg, String.valueOf(course.getChatRoomId()));
        return ActResultDto.success();
    }

    @Override
    public String getLastCourseType(Long seriesid) {
        Map map = courseMapper.getLastCourseType(seriesid);
        if (map != null) {
            String liveWay = (String) map.get("liveWay");
            String isVerticalScreen = (String) map.get("isVerticalScreen");
            String liveTimeStatus = getLiveStatus((Date) map.get("startTime"), (Date) map.get("endTime"));
            map.put("liveTimeStatus", liveTimeStatus);
            //语音直播
            if ("1".equals(liveWay)) {
                return "1";
            } else {
                if ("1".equals(isVerticalScreen)) {
                    return "3";
                } else {
                    return "2";
                }
            }
        }
        return "";
    }

    /**
     * 获取所有的单节课
     *
     * @param appId
     * @return
     */
    @Override
    public List<Map> getAllSingleClass(long appId) {
        return courseMapper.getAllSingleClass(appId);
    }

    /**
     * 获取所有的系列课
     *
     * @param appId
     * @return
     */
    @Override
    public List<Map> getAllseriesClass(long appId) {
        return courseMapper.getAllSeriesClass(appId);
    }


    @Override
    public List<Map> getPcCourseListPage(DataGridPage dataGridPage, Map map) {
        List<Map> list = courseMapper.getPcCourseListPage(dataGridPage, map);
        if (list.size() > 0) {
            for (Map m : list) {
                String courseStatus = m.get("courseStatus").toString();
                if (!Utility.isNullorEmpty(m.get("startTime"))) {
                    Date startTime = (Date) m.get("startTime");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    m.put("startTime", formatter.format(startTime));
                    if (StringUtils.isEmpty(m.get("endTime"))) {
                        if (startTime.getTime() > new Date().getTime()) {
                            m.put("time", "0");
                        } else {
                            m.put("time", "1");
                        }
                    } else {
                        m.put("time", "2");
                    }
                }
                if (courseStatus.equals("1")) {
                    m.put("time", "3");
                }
                int isJoinStatus = joinCourseRecordService.getPaySuccessRecordCount(Long.parseLong(m.get("id").toString()), "1");
                if (isJoinStatus > 0) {
                    isJoinStatus = 1;
                } else {
                    isJoinStatus = 0;
                }
                m.put("isJoinStatus", isJoinStatus);
            }
        }
        return list;
    }

    public List<CourseImg> getCoursImg(Long courseId) {
        return this.getCourseImgList(courseId);
    }

    public List<Map> getAllPcSeriesClass(long appId) {
        return courseMapper.getAllPcSeriesClass(appId);
    }

    @Override
    @UpdateSeriesCourseTime
    public void del(long id, long optId, String optName) throws Exception {
        courseMapper.del(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_del.getType(), "1", optId, optName, String.valueOf(id), "课程：" + course.getLiveTopic() + "已被删除");
    }

    @Override
    @UpdateSeriesCourseTime
    public void updateUp(long id, long optId, String optName) throws Exception {
        courseMapper.updateUp(id);
        Course course = courseMapper.getCourse(id);
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        SystemLogUtil.saveSystemLog(LogType.console_course_up.getType(), "1", optId, optName, String.valueOf(id), "课程：" + course.getLiveTopic() + "已被上架");
    }

    @Override
    public String getCourseStatusByCourseId(Long courseId) {
        String isEmpty = "";
        Course course = courseMapper.getCourse(courseId);
        if (!Utility.isNullorEmpty(course)) {
            if ("1".equals(course.getStatus())) {
                isEmpty = "1";
            } else if (!Utility.isNullorEmpty(course.getEndTime())) {
                isEmpty = "0";
            }
        }
        return isEmpty;
    }

    @Override
    public ActResultDto isSameCoureForLiving(Long appId) {
        ActResultDto actResultDto = new ActResultDto();
        Map map = new HashMap<>();
        String sameCourseId = "0";
        List<Course> courseList = courseMapper.getLivingForCourse(appId);
        if (courseList.size() > 0) { //是否有正在直播未结束的课 1:有
            sameCourseId = "1";
        }
        map.put("sameCourseId", sameCourseId);
        actResultDto.setData(map);
        return actResultDto;
    }

    @Override
    @UpdateSeriesCourseTime
    public void setCourseDown(Course course) {
        courseMapper.setCourseDown(course.getId());
        //下架原课时同时下架对应的转播课
        if(course.getSeriesCourseId() > 0){
            this.courseRelayMapper.setDownRelayCourseByOriCourse(course.getId());
        }else{
            this.courseRelayMapper.deleteRelayCourse(course.getId());
        }
        redisUtil.del(RedisKey.ll_course + course.getId());
        // 如果是系列课里面的单节课，则发送更新系列课时间队列
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        redisUtil.del(RedisKey.course_live_weekly_selection_id);
    }

    /**
     * 转播课程下架
     * @param course
     */
    @Override
    public void setRelayCourseDown(CourseRelayDto course) {
        if("1".equals(course.getIsSeriesCourse())){
            courseMapper.setRelaySeriesCourseDown(course.getId());
        }
        courseMapper.setRelayCourseDown(course.getId());
        redisUtil.del(RedisKey.ll_course + course.getId());
    }

    @Override
    public List<Course> getLivingForCourse(Long appId) {
        return courseMapper.getLivingForCourse(appId);
    }

    @Override
    public List<Course> getshortTimeCourse(long roomId) {
        return courseMapper.getshortTimeCourse(roomId);
    }

    @Override
    public void setShareTime(long mustShareTime, long courseId) {
        courseMapper.setShareTime(mustShareTime, courseId);
    }


    /**
     * WHA
     *
     * @param name     搜索使用字段 直播间名称
     * @param offset   分页参数
     * @param pageSize 分页参数
     * @return
     */
    @Override
    public ActResultDto findLiveRoomByName(String name, Integer offset, Integer pageSize) {
        ActResultDto actResultDto = new ActResultDto();//创建返回数据模型对象
        DataGridPage dg = new DataGridPage();//创建分页模型对象
        dg.setOffset(offset);//设置分页偏移量
        dg.setPageSize(pageSize);//设置分页每页数据量
        List<Map> liveRooms = courseMapper.findLiveRoomByNamePage(name, dg);
        if (liveRooms != null && liveRooms.size() > 0) {
            for (Map map : liveRooms) {
                Long id = (Long) map.get("id");
                map.put("followNum", userFollowService.getCountByRoomId(id));
            }
            actResultDto.setData(liveRooms);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            return actResultDto;
        }
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        return actResultDto;
    }

    /**
     * WHA
     *
     * @param liveTopic      搜索使用字段 直播主题
     * @param offset         分页参数
     * @param pageSize       分页参数
     * @param isSeriesCourse 1-是系列课0-是单节课
     * @return
     */
    @Override
    public ActResultDto findCourseByLiveTopic(String liveTopic, Integer offset, Integer pageSize, String isSeriesCourse) {
        ActResultDto actResultDto = new ActResultDto();//创建返回数据模型对象
        DataGridPage dg = new DataGridPage();//创建分页模型对象
        dg.setOffset(offset);//设置分页偏移量
        dg.setPageSize(pageSize);//设置分页每页数据量
        List<Map> courses = courseMapper.findCourseByLiveTopicPage(liveTopic, dg, isSeriesCourse,"0");
        if (courses != null && courses.size() > 0) {
            NumberUtil.setListMaps(courses,null,false);
            actResultDto.setData(courses);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            return actResultDto;
        }
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        return actResultDto;
    }

    /**
     * 首页 搜索默认
     * WHA
     *
     * @param condition 搜索使用字段 直播主题
     * @return
     */
    @Override
    public ActResultDto findCourseAndLiveRoomByCondition(String condition,String type) {
        ActResultDto actResultDto = new ActResultDto();//创建返回数据模型对象
        DataGridPage dg = new DataGridPage();//创建分页模型对象
        dg.setOffset(0);//设置分页偏移量
        dg.setPageSize(4);//设置分页每页数据量
        List<Map> liveRooms = courseMapper.findLiveRoomByNamePage(condition, dg);
        if (liveRooms != null && liveRooms.size() > 0) {
            for (Map map : liveRooms) {
                Long id = (Long) map.get("id");
                map.put("followNum", userFollowService.getCountByRoomId(id));
            }
        }
        List<Map> coursesX = courseMapper.findCourseByLiveTopicPage(condition, dg, "1",type);//系列课
        if (coursesX != null && coursesX.size() > 0) {
            NumberUtil.setListMaps(coursesX,null,false);
        }
        List<Map> coursesD = courseMapper.findCourseByLiveTopicPage(condition, dg, "0",type);//单节课
        if (coursesD != null && coursesD.size() > 0) {
            NumberUtil.setListMaps(coursesD,null,false);
        }
        Map map = new HashMap<>();
        map.put("liveRooms", liveRooms);//直播间
        map.put("coursesX", coursesX);//系列课
        map.put("coursesD", coursesD);//单节课
        actResultDto.setData(map);
        actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return actResultDto;
    }

    @Override
    public ActResultDto updateCanConnect(long courseId, String isCan) {
        redisUtil.del(RedisKey.ll_course + courseId); //删除缓存
        courseMapper.updateCanConnect(courseId, isCan);
        return ActResultDto.success();
    }

    /**
     * WHA
     * 单节课OR系列课 每周精选
     *
     * @param
     * @return
     */
    @Override
    public ActResultDto findCourseWeeklySelection() {
        ActResultDto actResultDto = new ActResultDto();//创建返回数据模型对象
        List<Map> weeklySelection = new ArrayList<Map>(4);
        List<Map> recoCourses = recoCourseServive.getRecoCourses();//首先获取手动推荐的热门资源
        Set<String> ids = redisUtil.smembers(RedisKey.course_live_weekly_selection_id);//先从缓存中获取数据
        if(recoCourses!=null&& recoCourses.size()>0){//如果存在手动设置资源
            if(recoCourses.size()==4){//当手动设置资源正好4个时
                NumberUtil.setListMaps(recoCourses,redisUtil,true);//设置 时间的 明 后 天
                actResultDto.setData(recoCourses);//保存数据资源
                actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                return actResultDto;
            }else if(recoCourses.size()<4){//当手动设置资源不足4个时
                int x = 4 - recoCourses.size();//计算缺少几个满足4个视频资源
                boolean b = false;
                if (ids != null && ids.size() > 0) {//当缓存中视频资源ID存在
                    for (String id : ids) {//遍历ID值
                        if (cn.jpush.api.utils.StringUtils.isNotEmpty(id)) {//为空判断
                            for(Map p : recoCourses){
                                if(p!=null&&p.get("id")!=null&&p.get("id").toString().equals(id)){//为空判断 并且判断手动设置资源ID与缓存数据冲突问题
                                    b = true;
                                }
                            }
                            if(b){
                                continue;//如果冲突 跳过本次循环
                            }
                            //不冲突 根据资源ID 查询资源
                            Map map = courseMapper.findCourseWeeklySelectionById(Long.parseLong(id));
                            //设置系列课和诞节课的显示位置顺序
                            if (map.get("isSeriesCourse") != null && "1".equals(map.get("isSeriesCourse").toString())) {
                                weeklySelection.add(0, map);
                            } else {
                                weeklySelection.add(map);
                            }
                        }
                    }
                    for(int i = 0 ;i<x;i++){
                        recoCourses.add(weeklySelection.get(i));
                    }
                    NumberUtil.setListMaps(recoCourses,redisUtil,true);
                    actResultDto.setData(recoCourses);
                    actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                    actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                    return actResultDto;
                }else{//当缓存中视频资源ID不存在
                    //从数据库中获取视频资源
                    ActResultDto courseWeeklySelection = courseBaseService.findCourseWeeklySelection();
                    if(courseWeeklySelection!=null&&courseWeeklySelection.getData()!=null){
                        List<Map> data =  (List<Map>)courseWeeklySelection.getData();
                        if(data.size()>0){
                            for (Map m :data) {
                                if(m!=null&&m.get("id")!=null){
                                    for(Map p : recoCourses){
                                        if(p!=null&&p.get("id")!=null&&p.get("id").toString().equals(m.get("id").toString())){
                                            b = true;
                                        }
                                    }
                                    if(b){
                                        continue;
                                    }
                                    weeklySelection.add(m);
                                }
                            }
                        }
                    }
                    for(int i = 0 ;i<x;i++){
                        recoCourses.add(weeklySelection.get(i));
                    }
                    NumberUtil.setListMaps(recoCourses,redisUtil,true);
                    actResultDto.setData(recoCourses);
                    actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                    actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                    return actResultDto;
                }
            }
        }

        if (ids != null && ids.size() > 0) {
            for (String id : ids) {
                if (cn.jpush.api.utils.StringUtils.isNotEmpty(id)) {
                    Map map = courseMapper.findCourseWeeklySelectionById(Long.parseLong(id));
                    if (map.get("isSeriesCourse") != null && "1".equals(map.get("isSeriesCourse").toString())) {
                        weeklySelection.add(0, map);
                    } else {
                        weeklySelection.add(map);
                    }
                }
            }
            NumberUtil.setListMaps(weeklySelection, redisUtil, true);
            if (weeklySelection != null && weeklySelection.size() == 4) {//确认数据数量完整
                actResultDto.setData(weeklySelection);
                actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            } else {
                actResultDto.setData(weeklySelection);
                actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
                actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            }
            return actResultDto;
        }
        return courseBaseService.findCourseWeeklySelection();
    }

    @Override
    public ActResultDto findCourseAllSelection(Integer offset, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        Map<String, Long> map = new HashMap<String, Long>();


        ActResultDto actResultDto = new ActResultDto();
        Set<String> ids = redisUtil.smembers(RedisKey.course_live_weekly_selection_id_cc);//先从缓存中获取数据
        if (ids != null && ids.size() > 0) {
            int i = 1;
            for (String id :ids) {
                if (cn.jpush.api.utils.StringUtils.isNotEmpty(id)) {
                    map.put("id" + i, Long.parseLong(id));
                    i++;
                }
            }
        }else{
            for(int i = 1;i<5;i++){
                map.put("id" + i,0L);
            }
        }
        List<Map> courseAllSelection = courseMapper.findCourseAllSelectionPage(dg, map);
        if (courseAllSelection != null && courseAllSelection.size() > 0) {
            NumberUtil.setListMaps(courseAllSelection,redisUtil,false);
            actResultDto.setData(courseAllSelection);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(courseAllSelection);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    /**
     * 首页精彩推荐 综合接口
     *
     * @return
     */
    @Override
    public ActResultDto wonderfulrecommendation() {
        ActResultDto actResultDto = new ActResultDto();
        Map map = new HashMap();
        List advertisingDisplays = advertisingDisplayService.getList("0");
        ActResultDto courseWeeklySelection = this.findCourseWeeklySelection();
        ActResultDto courseAllSelection = this.findCourseAllSelection(0, 4);

        map.put("advertisingList", advertisingDisplays);//广告 (redis缓存)
        map.put("courseWeeklySelection", courseWeeklySelection.getData());
        map.put("courseAllSelection", courseAllSelection.getData());
        actResultDto.setData(map);
        return actResultDto;
    }

    @Override
    public ActResultDto getMyHistoryCourseList(DataGridPage page, Long appId,String clientType) {
        ActResultDto result = new ActResultDto();
        List<Map> historyCourseList = courseMapper.getMyHistoryCourseListPage(page, appId,clientType);
        if (historyCourseList.size() > 0) {
            for (int i = 0; i < historyCourseList.size(); i++) {
                Map map = historyCourseList.get(i);
                String seriesId = map.get("seriesCourseId").toString();
                if (!Utility.isNullorEmpty(seriesId) && Long.parseLong(seriesId) > 0) {
                    Map seriesMap = courseMapper.getSeriesCourseInfoBySeriesId(Long.parseLong(seriesId));
                    Object startTime = seriesMap.get("startTime");
                    Date endTime = (Date) seriesMap.get("endTime");
                    if (seriesMap.get("startTime") != null) {
                        seriesMap.put("startTimeStr", DateUtil.transFormationStringDate(seriesMap.get("startTime").toString()));
                    } else {
                        seriesMap.put("startTimeStr", "");
                    }
                    String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                    if("1".equals(liveTimeStatus)){
                        liveTimeStatus = "0";
                    }
                    seriesMap.put("liveTimeStatus", liveTimeStatus);

                    if(seriesMap.get("studyCount") != null){
                        seriesMap.put("studyCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(seriesMap.get("studyCount").toString())));
                    }
                    historyCourseList.remove(map);
                    historyCourseList.add(i, seriesMap);
                } else {
                    Object startTime = map.get("startTime");
                    Date endTime = (Date) map.get("endTime");
                    if (map.get("startTime") != null) {
                        map.put("startTimeStr", DateUtil.transFormationStringDate(map.get("startTime").toString()));
                    } else {
                        map.put("startTimeStr", "");
                    }
                    String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                    map.put("liveTimeStatus", liveTimeStatus);
                    if(map.get("studyCount") != null){
                        map.put("studyCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("studyCount").toString())));
                    }
                }

            }
        } else {
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return result;
        }
        result.setData(historyCourseList);
        return result;
    }

    @Override
    public ActResultDto getMyBuyFreeCourseList(DataGridPage page, Long appId,String clientType) {
        ActResultDto result = new ActResultDto();
        List<Map> buyFreeCourseList = courseMapper.getMyBuyFreeCourseListPage(page, appId,clientType);
        if (buyFreeCourseList.size() > 0) {
            for (Map map : buyFreeCourseList) {
                Object startTime = map.get("startTime");
                Date endTime = (Date) map.get("endTime");
                if (map.get("startTime") != null) {
                    map.put("startTimeStr", DateUtil.transFormationStringDate(map.get("startTime").toString()));
                } else {
                    map.put("startTimeStr", "");
                }
                String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                map.put("liveTimeStatus", liveTimeStatus);
                if(map.get("studyCount") != null){
                    map.put("studyCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("studyCount").toString())));
                }
            }
        } else {
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return result;
        }
        result.setData(buyFreeCourseList);
        return result;
    }

    @Override
    public ActResultDto getMyLivedCourseList(DataGridPage dg, Long appId,String clientType) {
        ActResultDto actResultDto = new ActResultDto();
        Map cMap = new HashMap();
        List<Map> nolivedCourseList = courseMapper.getMynoLivedCourseList(appId,clientType);
        if (nolivedCourseList.size() > 0) {
            for (Map map : nolivedCourseList) {
                Object startTime = map.get("startTime");
                Date endTime = (Date) map.get("endTime");
                if (startTime != null) {
                    map.put("startTimeStr", DateUtil.transFormationStringDate(map.get("startTime").toString()));
                } else {
                    map.put("startTimeStr", "");
                }
                String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                map.put("liveTimeStatus", liveTimeStatus);

                if(map.get("studyCount") != null){
                    map.put("studyCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("studyCount").toString())));
                }
            }
        }
        List<Map> havelivedCourseList = courseMapper.getMyhaveLivedCourseListPage(dg, appId,clientType);
        if (havelivedCourseList.size() > 0) {
            for (Map map : havelivedCourseList) {
                Object startTime = map.get("startTime");
                Date endTime = (Date) map.get("endTime");
                if (endTime != null) {
                    map.put("startTimeStr", DateUtil.transFormationStringDate(map.get("endTime").toString()));
                } else {
                    map.put("startTimeStr", "");
                }
                String liveTimeStatus = getLiveStatus((Date) startTime, endTime);
                map.put("liveTimeStatus", liveTimeStatus);
                if(map.get("studyCount") != null){
                    map.put("studyCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("studyCount").toString())));
                }
            }
        }
        cMap.put("havelivedCourseList", havelivedCourseList);
        cMap.put("nolivedCourseList", nolivedCourseList);
        if (havelivedCourseList.size() == 0 && nolivedCourseList.size()==0) {
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return actResultDto;
        }
        actResultDto.setData(cMap);
        return actResultDto;
    }

    /**
     * 免费专区
     *
     * @param offset
     * @param pageSize
     * @param sort     合排序--0 人气排序--1 时间排序--2
     * @param sc       时间正序--0 时间倒序--1
     * @return
     */
    @Override
    public ActResultDto findFreeAdmissionCoursePage(Integer offset, Integer pageSize, String sort, String sc) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> freeAdmissionCoursePage = courseMapper.findFreeAdmissionCoursePage(dg, sort, sc);
        if (freeAdmissionCoursePage != null && freeAdmissionCoursePage.size() > 0) {
            NumberUtil.setListMaps(freeAdmissionCoursePage,null,false);
            actResultDto.setData(freeAdmissionCoursePage);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(freeAdmissionCoursePage);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    /**
     * 每周精选--更多（分页）
     *
     * @param timeStr  0-本周  1-上周 3-其他所有
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public ActResultDto findWeeklySelectionPage(Integer offset, Integer pageSize, String timeStr) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        String startTimes = null;
        String endTimes = null;
        String isTime = null;
        Date date = new Date();
        if ("0".equals(timeStr)) {//本周
            startTimes = DateUtil.format(DateUtil.getFirstDayOfWeek(date));
            endTimes = DateUtil.format(DateUtil.getLastDayOfWeek(date));
        } else if ("1".equals(timeStr)) {//上周
            startTimes = DateUtil.format(DateUtil.getFirstDayOfWeek(DateUtil.getYestday(DateUtil.getFirstDayOfWeek(date))));
            endTimes = DateUtil.format(DateUtil.getLastDayOfWeek(DateUtil.getYestday(DateUtil.getFirstDayOfWeek(date))));
        } else {//其他
            isTime = "true";
            startTimes = DateUtil.format(DateUtil.getFirstDayOfWeek(DateUtil.getYestday(DateUtil.getFirstDayOfWeek(date))));
            endTimes = DateUtil.format(DateUtil.getLastDayOfWeek(date));
        }

        List<Map> weeklySelectionPage = courseMapper.findWeeklySelectionPage(dg, startTimes, endTimes, isTime);
        if (weeklySelectionPage != null && weeklySelectionPage.size() > 0) {
            NumberUtil.setListMaps(weeklySelectionPage,null,false);
            actResultDto.setData(weeklySelectionPage);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(weeklySelectionPage);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    /**
     * 正在直播
     *
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public ActResultDto findBeingroadcastive(Integer offset, Integer pageSize) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> beingroadcastivePage = courseMapper.findBeingroadcastivePage(dg, DateUtil.format(new Date()));
        if (beingroadcastivePage != null && beingroadcastivePage.size() > 0) {
            NumberUtil.setListMaps(beingroadcastivePage,null,false);
            actResultDto.setData(beingroadcastivePage);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(beingroadcastivePage);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    @Override
    public ActResultDto findRankingListPage(Integer offset, Integer pageSize, String sort, String sc) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> freeAdmissionCoursePage = courseMapper.findRankingListPage(dg, sort, sc);
        if (freeAdmissionCoursePage != null && freeAdmissionCoursePage.size() > 0) {
            NumberUtil.setListMaps(freeAdmissionCoursePage,null,false);
            actResultDto.setData(freeAdmissionCoursePage);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(freeAdmissionCoursePage);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    /**
     * 未开播
     *
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public ActResultDto findCommend4HomeV4Page(Integer offset, Integer pageSize) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> beingroadcastivePage = courseMapper.getCommend4HomeV5Page(dg, DateUtil.format(new Date()));
        if (beingroadcastivePage != null && beingroadcastivePage.size() > 0) {
            NumberUtil.setListMaps(beingroadcastivePage,null,false);
            actResultDto.setData(beingroadcastivePage);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(beingroadcastivePage);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }

    @Override
    public int getSerisCourseCount(long roomId) {
        String redisVal = redisUtil.get(RedisKey.series_course_count + roomId);
        if (StringUtils.isEmpty(redisVal)) {
            int count = courseMapper.getSerisCourseCount(roomId);
            redisUtil.set(RedisKey.series_course_count + roomId, count + "");
            redisUtil.expire(RedisKey.series_course_count + roomId, 5 * 24 * 60 * 60);
            return count;
        } else {
            return Integer.parseInt(redisVal);
        }
    }

    @Override
    public int getCourseCountByAppId(long teacherId,String type) {
        String redisVal = redisUtil.get(RedisKey.teacher_course_count + teacherId);
        if (StringUtils.isEmpty(redisVal)) {
            int count = courseMapper.getCourseCountByAppId(teacherId);
            redisUtil.set(RedisKey.teacher_course_count + teacherId, count + "");
            redisUtil.expire(RedisKey.teacher_course_count + teacherId, 5 * 24 * 60 * 60);
            return count;
        } else {
            return Integer.parseInt(redisVal);
        }
    }

    @Override
    public Course selectCourseMsgByChatRoomId(long chatRoomId) {
        return courseMapper.selectCourseMsgByChatRoomId(chatRoomId);
    }

    /**
     * 检验课程id在定制邀请卡中是否存在
     *
     * @param courseId
     * @return
     */
    @Override
    public int findCourseIsExist(Long courseId) {
        return coursePrivateCardMapper.findCourseIsExist(courseId);
    }

    /**
     * 查询邀请卡
     * @param courseId
     * @return
     */
    public CourseCard findCardUrlByCourseId(Long courseId){
        return coursePrivateCardMapper.findCardUrlByCourseId(courseId);
    }
    /**
     * 插入邀请卡模板
     *
     * @param courseCard
     * @return
     */
    @Override
    public int insertCourseCard(CourseCard courseCard,HttpServletRequest request) {
        String modelUrl = courseCard.getModelUrl();
        Long courseId = courseCard.getCourseId();
        String cardUrl = courseCard.getCardUrl();
        if(StringUtils.isEmpty(cardUrl)){
            String url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
            courseCard.setCardUrl(url);
        }
        return coursePrivateCardMapper.insertCourseCard(courseCard);
    }

    /**
     * 更新邀请卡url
     *
     * @param courseCard
     * @return
     */
    @Override
    public int updateCourseCard(CourseCard courseCard,HttpServletRequest request) {
        String modelUrl = courseCard.getModelUrl();
        Long courseId = courseCard.getCourseId();
        String cardUrl = courseCard.getCardUrl();
        if(StringUtils.isEmpty(cardUrl)){
            String url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
            courseCard.setCardUrl(url);
        }
        return coursePrivateCardMapper.updateCourseCard(courseCard);
    }
    public String doPreviewCourseCard(CourseCard courseCard,HttpServletRequest request){
        String modelUrl = courseCard.getModelUrl();
        Long courseId = courseCard.getCourseId();
        CourseCard card = coursePrivateCardMapper.findCardUrlByCourseId(courseId);
        String url = "";
        try {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(modelUrl)) {
                modelUrl = URLDecoder.decode(modelUrl, "UTF-8");
            }
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(modelUrl) && card != null && modelUrl.equals(card.getModelUrl())){
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(card.getCardUrl())) {
                    url = card.getCardUrl();
                } else {
                    url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
                }
            } else {
               url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("根据模板生成邀请卡异常:"+e.getMessage());
        }
        return url;
    }

    /**
     * 首页精彩推荐 综合接口
     *
     * @return
     */
    @Override
    public ActResultDto wonderfulrecommendationV164() {
        Map<String,Object> dataMap=courseMapper.findBeingroadcastivePageSize(Long.valueOf(10));
        int pageSize=10;
        if(dataMap!=null && dataMap.get("content")!=null && !"".equals(dataMap.get("content"))){
            pageSize=Integer.parseInt((String)dataMap.get("content"));
        }

        ActResultDto actResultDto = new ActResultDto();
        Map map = new HashMap();
        List advertisingDisplays = advertisingDisplayService.getList("0");
        ActResultDto courseWeeklySelection = this.findCourseWeeklySelection();
        ActResultDto courseAllSelection = this.findCourseAllSelection(0, 20);
        ActResultDto beingroadcastive = this.findBeingroadcastive(0, pageSize);
        ActResultDto commend4HomeV4Page = findCommend4HomeV4Page(0, 4);
        map.put("advertisingList", advertisingDisplays);//广告 (redis缓存)
        map.put("courseWeeklySelection", courseWeeklySelection.getData());
        map.put("courseAllSelection", courseAllSelection.getData());
        map.put("beingroadcastive", beingroadcastive.getData());
        map.put("advance", commend4HomeV4Page.getData());
        map.put("courseTypes", courseTypeService.getCourseTypes());
        map.put("pageSize", pageSize);
        actResultDto.setData(map);
        return actResultDto;
    }


    /**
     * 分类
     *
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public ActResultDto getCoursesByType(Integer offset, Integer pageSize,String courseType) {
        ActResultDto actResultDto = new ActResultDto();
        DataGridPage dg = new DataGridPage();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> coursesByType = courseMapper.getCoursesByTypePage(dg, courseType);
        if (coursesByType != null && coursesByType.size() > 0) {
            NumberUtil.setListMaps(coursesByType,null,false);
            actResultDto.setData(coursesByType);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(coursesByType);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
 
    }

    /**
     * 删除邀请卡
     * @param courseCard
     * @param request
     * @return
     */
    @Override
    public int deleteCourseCard(CourseCard courseCard, HttpServletRequest request) {
        String modelUrl = courseCard.getModelUrl();
        Long courseId = courseCard.getCourseId();
        //String cardUrl = "";
        int result = 0;
        if(org.apache.commons.lang3.StringUtils.isBlank(modelUrl)){
            courseCard = coursePrivateCardMapper.findCardUrlByCourseId(courseId);
            if(courseCard != null) {
                modelUrl = courseCard.getModelUrl();
                //cardUrl = courseCard.getCardUrl();
            }
        } else {
            try {
                if(org.apache.commons.lang3.StringUtils.isNotBlank(modelUrl)) {
                    modelUrl = URLDecoder.decode(modelUrl, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("模板url解码异常:"+e.getMessage());
            }
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(modelUrl)){
            String hostUrl =modelUrl.substring(modelUrl.indexOf("/upload")+1);
            String preFix = hostUrl.substring(0, hostUrl.lastIndexOf("/")+1);
            String subFix = hostUrl.substring(hostUrl.lastIndexOf("/")+1);
            result = LonglianSsoUtil.deleteFileOfBucket(bucketName, preFix, subFix);
            if(result > 0){
                //courseCard.setModelUrl(null);
                result = coursePrivateCardMapper.deleteCourseCard(courseId);
            }
        }
        /*if(org.apache.commons.lang3.StringUtils.isNotBlank(cardUrl)){
            String subFix = cardUrl.substring(cardUrl.lastIndexOf("/")+1);
            result = LonglianSsoUtil.deleteFileOfBucket(kjbucketName,"",subFix);
            if(result > 0){
                courseCard.setCardUrl(null);
                result = coursePrivateCardMapper.updateCourseCard(courseCard);
            }
        }*/
        return result;
    }

    /**
     * 获取课程状态
     * @param id
     * @return
     */
    @Override
    public ActResultDto getCourseStatus(long id) {
        ActResultDto dto = new ActResultDto<Map>();
        String coursesJson =  redisUtil.get(RedisKey.ll_course + id);
        Map course = null;
        Map map = new HashMap<String,Object>();
        if(coursesJson != null && coursesJson.length() > 0){
            course = JsonUtil.getMap4Json(coursesJson);
            map.put("STATUS",course.get("status"));
            map.put("IS_DELETE",course.get("isDelete"));
            map.put("IS_RECORDED",course.get("isRecorded"));
            map.put("END_TIME",course.get("endTime"));
        }
        if(map == null || map.size() < 1){
            map = courseMapper.getCourseStatus(id);
        }
        if(map != null && map.size() > 0){
            Object object = map.get("END_TIME");
            if(object != null){
                String endTime = object.toString();
                if(endTime != null && endTime.length() > 0){
                    map.put("ISBACKSHOW",1);
                }else{
                    map.put("ISBACKSHOW",0);
                }
            }
            dto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            dto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            dto.setData(map);
            return dto;
        }else{
            dto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            dto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return dto;
        }
    }

    /**
     * 获取课程邀请卡提示信息 老师：多少分销 学生：多少分销金额
     * @param id
     * @param app_id
     * @return
     */
    @Override
    public ActResultDto getCourseCardAmt(long id,long app_id) {
        ActResultDto dto = new ActResultDto();
        Course course = courseMapper.getCourseDetial(id);
        if(String.valueOf(id).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto=courseRelayService.queryById(id);
            course=courseRelayService.queryByAppidAndOriCourseId(app_id,String.valueOf(courseRelayDto.getOriCourseId()));
        }
        String distribution = course.getCustomDistribution();
        String divideScale = course.getDivideScale();
        Map map = new HashMap<String,String>();
        if(course != null){
            //判断是老师还是学生
            if(course.getAppId() == app_id){ //老师
                map.put("type","teacher");
                if(distribution != null && distribution.length() > 0){
                    map.put("value", distribution);
                }else{
                    map.put("value", divideScale);
                }
            }else{ //学生
                map.put("type","student");
                BigDecimal amount = new BigDecimal(0);
                BigDecimal chargeAmt = course.getChargeAmt();
                if(distribution != null && distribution.length() > 0){
                    amount = chargeAmt.multiply(BigDecimal.valueOf(Double.parseDouble(distribution))).divide(new BigDecimal(100));
                }else{
                    amount = chargeAmt.multiply(BigDecimal.valueOf(Double.parseDouble(divideScale!=null&&divideScale.length()>0?divideScale:"0"))).divide(new BigDecimal(100));
                }
                DecimalFormat df = new DecimalFormat("###0.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                String amountStr = df.format(amount);
                map.put("value",new BigDecimal(amountStr));
            }
            dto.setData(map);
        }else{
            dto.setCode(ReturnMessageType.NO_DATA.getCode());
            dto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return dto;
    }


    /**
     * 获取所有可以转播的课程
     * @param sc  排序  正序--0 , 倒序--1
     * @param sort   综合排序  时间--1,价格--2,分成比例--3
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public List<Map> queryCanBroadcastCoursePage(String courseName,long appId,String sc,String sort, Integer offset, Integer pageSize){
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list=courseMapper.queryCanBroadcastCoursePage(courseName,appId,sc,sort,dg);
        for(Map map:list){
            map.put("createTime",DateUtil.transFormationStringDate2(DateUtil.format((Date) map.get("createTime"))));
            if (map.get("startTime") != null) {
                String startTime = map.get("startTime").toString();
                String s = DateUtil.transFormationStringDate(startTime);
                map.put("startTimeStr", s);
                String endTime = null;
                if (map.get("endTime") != null) {
                    endTime = map.get("endTime").toString();
                }
                map.put("statusStr", DateUtil.getStatusStr(map.get("startTime").toString(), endTime));
            }
        }
        return list;
    }

    /**
     * 修改课程转播信息
     * @param courseId
     * @param isRelay
     * @param relayCharge
     * @param relayScale
     * @return
     */
    @Override
    public ActResultDto updateCourseRelayInfo(String courseId, String isRelay, String relayCharge, String relayScale) {
        ActResultDto actResultDto = new ActResultDto();
        Course course=getCourse(Long.valueOf(courseId));
        if(course==null){
            actResultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            actResultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            return actResultDto;
        }
        if(Float.valueOf(relayScale)>100F || Float.valueOf(relayScale)<0F){
            actResultDto.setCode(ReturnMessageType.CHECK_RELAY_SCALE.getCode());
            actResultDto.setMessage(ReturnMessageType.CHECK_RELAY_SCALE.getMessage());
            return actResultDto;
        }
        if(course.getRelayCharge()==null){
            course.setRelayCharge(new BigDecimal(0));
        }
        if(1==course.getIsOpened() && "1".equals(isRelay)){
            if(new BigDecimal(relayCharge).compareTo(course.getRelayCharge())!=0 || Float.valueOf(relayScale)!=course.getRelayScale()){
                actResultDto.setCode(ReturnMessageType.CAN_NOTRELAY_COURSE_UPDATE.getCode());
                actResultDto.setMessage(ReturnMessageType.CAN_NOTRELAY_COURSE_UPDATE.getMessage());
                return actResultDto;
            }
        }
        Course newCourse=new Course();
        newCourse.setId(Long.valueOf(courseId));
        newCourse.setIsRelay(Integer.valueOf(isRelay));
        newCourse.setRelayCharge(new BigDecimal(Double.valueOf(relayCharge)));
        newCourse.setRelayScale(Float.valueOf(relayScale));
        //如果开关关闭，不记录数据
        if(1!=Integer.valueOf(isRelay)){
            newCourse.setRelayCharge(course.getRelayCharge());
            newCourse.setRelayScale(course.getRelayScale());
            course.setSetRelayTime(course.getSetRelayTime());
        }else{
            newCourse.setSetRelayTime(new Date());
        }
        //修改课程转播信息
        courseMapper.updateCourseRelayInfo(newCourse);
        if(1!=course.getIsOpened() && 1==Integer.valueOf(isRelay)){
            courseMapper.updateIsOpenById(1,course.getId());
        }
        if(course.getIsRelay()!=1 && "1".equals(isRelay)){
            //发送公众号消息
            CourseDto courseDto=new CourseDto();
            courseDto.setLiveTopic(course.getLiveTopic());
            courseDto.setId(course.getId());
            courseDto.setIsRelay(1);
            courseDto.setRoomId(course.getRoomId());
            courseDto.setRemark(course.getRemark());
            courseDto.setCoverssAddress(course.getCoverssAddress());
            courseDto.setLiveTopic(course.getLiveTopic());
            courseDto.setStartTime(course.getStartTime());
            AppUser teach = appUserService.getById(course.getAppId());
            courseDto.setAppUserName(teach.getName());
            wechatOfficialService.getSendWechatTemplateMessageSaveRedis(courseDto);
            //老师消息
            String cAct = website + "/weixin/relaymarket.user";
            sendMsgService.sendMsg(course.getAppId(), MsgType.SYS_OTHER_RELAY.getType(), course.getId() ,
                    MsgConst.replace(MsgConst.RELAY_COUSER_REMIND_FOLLOW_USER_CONTENT,course.getLiveTopic()) , cAct);
            //粉丝消息
            Set<String> follows=userFollowService.getFollowUser(course.getRoomId());
            AppUser user = appUserService.getById(course.getAppId());
            //cAct = website + "/weixin/liveRoom?id="+course.getAppId();
            for(String userId:follows){
                sendMsgService.sendMsg(Long.parseLong(userId), MsgType.SYS_OTHER_RELAY.getType(), course.getId()
                        , MsgConst.replace(MsgConst.RELAY_COURSE_SUCCESS, user.getName())
                        , cAct);
            }
        }
        String courseKey = RedisKey.ll_course + course.getId();
        redisUtil.del(courseKey);
        return actResultDto;
    }
}
