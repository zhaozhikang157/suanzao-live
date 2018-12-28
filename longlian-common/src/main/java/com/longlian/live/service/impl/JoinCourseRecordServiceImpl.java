package com.longlian.live.service.impl;

import com.alibaba.fastjson.JSON;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.dao.JoinCourseRecordMapper;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.service.UserRewardRecordService;
import com.longlian.model.Course;
import com.longlian.model.CourseBaseNum;
import com.longlian.model.JoinCourseRecord;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.JoinCourseRecordType;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.ThirdPayDto;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lh on 2017-02-14.
 */
@Service("joinCourseRecordService")
public class JoinCourseRecordServiceImpl implements JoinCourseRecordService {
    private static Logger log = LoggerFactory.getLogger(JoinCourseRecordServiceImpl.class);
    @Autowired
    private JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CourseBaseNumMapper courseBaseNumMapper;
    @Autowired
    private RedisLock redisLock;

    @Autowired
    private UserRewardRecordService userRewardRecordService;

    @Autowired
    UserFollowService userFollowService;

    /**
     * 支付中和支付成功的
     * @param userId
     * @param course
     * @return
     */
    @Override
    public boolean isJoinCourse(Long userId ,Course course) {
        Long courseId = course.getId();
        //是系列课的单节课
        if (course.getSeriesCourseId() != 0) {
            courseId = course.getSeriesCourseId();
        }

        loadJoinCourseUser(courseId);
        Double score = redisUtil.zscore( RedisKey.ll_join_course_user_key + courseId , String.valueOf(userId));
        //报名中，是否报名0也算，解决报名过程中有可能支付还没回调 问题
        if (score != null) {
            return true;
        }
        return false;
    }


    /**
     *
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public String  getJoinCourseStatus(Long userId , Long courseId) {
        String status = "";
        JoinCourseRecord joinCourseRecord = getByAppIdAndCourseIdByRedis(userId, courseId ,false);
        if(joinCourseRecord != null ){
            status = joinCourseRecord.getSignUpStatus();
        }
        //String status = joinCourseRecordMapper.getJoinStatus(userId, courseId);
        return status;
    }

    /**
     * 根据用户ID和课程ID获取报名记录
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public JoinCourseRecord getByAppIdAndCourseId(long userId, long courseId) {
        return joinCourseRecordMapper.getByAppIdAndCourseId(userId ,courseId);
    }

    /**
     * 根据用户ID和课程ID获取报名记录
     * @param userId
     * @param isLock 是否加锁  购买的是否需要加所
     * @param courseId
     * @return
     */
    @Override
    public JoinCourseRecord getByAppIdAndCourseIdByRedis(long userId, long courseId , boolean isLock) {
        JoinCourseRecord joinCourseRecord = null;
        boolean isExists = redisUtil.exists(RedisKey.ll_live_join_course_record_pre + courseId);//是否存在
        String signUpStatusStr = "";
        if(!isExists){
            //重新从数据取一次，将数据库加入到redis
            List<Map> list = joinCourseRecordMapper.getListCourseId(courseId);
            Map<String , String> mapStr = new HashMap<String , String>();
            for(Map map : list){
                String id = map.get("id").toString();
                String appId = map.get("appId").toString();
                String signUpStatus = map.get("signUpStatus").toString();
                if(appId.equals(userId + "")){
                    signUpStatusStr = signUpStatus;
                }
                mapStr.put(appId , signUpStatus);
            }
            if(!mapStr.isEmpty()){
                redisUtil.hmset(RedisKey.ll_live_join_course_record_pre + courseId , mapStr) ;
                redisUtil.expire(RedisKey.ll_live_join_course_record_pre + courseId, RedisKey.ll_live_join_course_record_use_time);
            }
        }else{
            signUpStatusStr = redisUtil.hget(RedisKey.ll_live_join_course_record_pre + courseId, userId + "");
        }
        if(!Utility.isNullorEmpty(signUpStatusStr)){
            joinCourseRecord = new JoinCourseRecord();
            if( "-1".equals(signUpStatusStr) && isLock){
                //先插入，考虑锁
                for (int i=0 ; i<8 ; i++){
                    try{
                        Thread.sleep(100);
                    }catch (Exception e){}
                    String signupStatus = redisUtil.hget(RedisKey.ll_live_join_course_record_pre + courseId,userId + "");
                    if(!Utility.isNullorEmpty(signupStatus) &&
                            !"-1".equals(signupStatus)   ){
                        signUpStatusStr = signupStatus;
                        break;
                    }
                }
                if( "-1".equals(signUpStatusStr)){
                    //并发的时候，可能没插进去，重新查数据库
                    JoinCourseRecord temp = getByAppIdAndCourseId(userId, courseId);
                    if(temp != null){
                        signUpStatusStr =   temp.getSignUpStatus();
                        redisUtil.hset(RedisKey.ll_live_join_course_record_pre + courseId, userId + "", signUpStatusStr);
                    }
                }
            }
            joinCourseRecord.setAppId(userId);
            joinCourseRecord.setCourseId(courseId);
            joinCourseRecord.setSignUpStatus(signUpStatusStr);
        }else{
            if(isLock) redisUtil.hsetnx(RedisKey.ll_live_join_course_record_pre + courseId ,userId + "", "-1");
        }
        return  joinCourseRecord;
    }

    /**
     * 是否可以插入，考虑并发，锁1秒中
     * @param userId
     * @return
     * @throws Exception
     */
/*    @Override
    public boolean isUseAddData(long userId, long courseId) throws Exception{
        boolean isUseAdd = true;
        //先插入，考虑锁
        long addCount = redisUtil.hsetnx(RedisKey.ll_live_join_course_record_pre + courseId ,userId + "", "-1");
        if(addCount <= 0){
            for (int i=0 ; i<10 ; i++){
                Thread.sleep(100);
                String signupStatus = redisUtil.hget(RedisKey.ll_live_join_course_record_pre + courseId,userId + "");
                if(!Utility.isNullorEmpty(signupStatus) &&
                        !"-1".equals(signupStatus)   ){
                    break;
                }
            }
            //并发的时候，可能没插进去，重新插下数据库
        *//*    JoinCourseRecord joinCourseRecord = getByAppIdAndCourseIdByRedis(userId, courseId);
            if(joinCourseRecord != null){
                return  false;
            }*//*
        }
        return  isUseAdd;
    }*/

    /**
     * 是否是第一次报名
     * @param userId
     * @return
     */
    @Override
    public boolean getFirstJoinByAppId(long userId) {
        //查询此人报名数量
        long count = joinCourseRecordMapper.getFirstJoinByAppId(userId);
        if(count > 0) return  false;
        return true;
    }

    /**
     * 创建，
     * @param joinCourseRecord
     */
    @Override
    public void insert(JoinCourseRecord joinCourseRecord) {
        joinCourseRecord.setCreateTime(new Date());
        joinCourseRecord.setStatus(0);
        if(Utility.isNullorEmpty(joinCourseRecord.getSignUpStatus())){
            joinCourseRecord.setSignUpStatus("0");
        }
        if(Utility.isNullorEmpty(joinCourseRecord.getJoinType())){
            joinCourseRecord.setJoinType("0");
        }
        joinCourseRecordMapper.insert(joinCourseRecord);
    }
    @Override
    public long getCourseRecordCount(long id) {
        return joinCourseRecordMapper.getCourseRecordCount(id);
    }

    /**
     * 获取支付成功记录 by 课程ID
     * @param courseId
     * @param payStatus 支付状态  0-所有的 1-支付成功
     * @return
     */
    @Override
    public int getPaySuccessRecordCount(long courseId , String payStatus) {
        return joinCourseRecordMapper.getPaySuccessRecordCount(courseId, payStatus);
    }

    /**
     * 获取首次支付成功记录  by 直播间
     * @param roomId
     * @param payStatus 支付状态  0-所有的 1-支付成功
     * @return
     */
    @Override
    public int getFirstPaySuccessRecordCount(long roomId , String payStatus) {
        return joinCourseRecordMapper.getFirstPaySuccessRecordCount(roomId, payStatus);
    }

    @Override
    public List<Map> getCourseRecordList(long id) {
        return joinCourseRecordMapper.getCourseRecordList(id);
    }

    @Override
    public List<Map> getMyBuyCourseList(long appId, Integer pageNum, Integer pageSize , boolean isHaveRecord) {
        DataGridPage dg = new DataGridPage();
        if (pageNum!=null) dg.setCurrentPage(pageNum);
        if (pageSize!=null) dg.setPageSize(pageSize);
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }
        List<Map> list = joinCourseRecordMapper.getMyBuyCourseListPage(appId, dg, isRecorded);
        if (list.size()>0){
            for (Map map:list){
                if ( map.get("startTime") == null) {
                    map.put("startTime", "");
                    continue;
                }
                map.put("startTime", DateUtil.format((Date) map.get("startTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        return list;
    }

    @Override
    public List<Map> getMyBuyCourseListV2(long appId, Integer pageNum, Integer pageSize, boolean isHaveRecord) {
        DataGridPage dg = new DataGridPage();
        if (pageNum!=null) dg.setCurrentPage(pageNum);
        if (pageSize!=null) dg.setPageSize(pageSize);
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }
        List<Map> list = joinCourseRecordMapper.getMyBuyCourseListV2Page(appId, dg, isRecorded);
        if (list.size()>0){
            for (Map map:list){
                Date endTime = (Date) map.get("endTime");
                Date startTime = (Date) map.get("startTime");
                if ( map.get("startTime") == null) {
                    map.put("startTime", "");
                    continue;
                }
                map.put("startTime", DateUtil.format((Date) map.get("startTime"), "yyyy-MM-dd HH:mm"));
                String liveTimeStatus = getLiveTimeStatus(startTime, endTime);
                map.put("liveTimeStatus", liveTimeStatus);
                if(map.get("noUpdatedCount") == null || Integer.valueOf(map.get("noUpdatedCount").toString())<0){
                    map.put("noUpdatedCount",0);
                }
            }
        }
        return list;
    }

    @Override
    public List<Map> getMyBuyCourseListV3(long appId, Integer pageNum, Integer pageSize, boolean isHaveRecord,String clientType) {
        // pageNum,pageNum  默认 带上参数 不复制  值默认转为0
        DataGridPage dg = new DataGridPage();
        if (pageNum!=null && pageNum!=0) dg.setCurrentPage(pageNum);
        if (pageSize!=null && pageSize!=0) dg.setPageSize(pageSize);
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }
        List<Map> list =new ArrayList<Map>();
        if(!StringUtils.isNotBlank(clientType)){
            //H5
            list=joinCourseRecordMapper.getMyBuyCourseListH5Page(appId, dg, isRecorded);
        }else{
            //app
            list=joinCourseRecordMapper.getMyBuyCourseListV3Page(appId, dg, isRecorded);
        }

        if (list.size()>0){
          //  Set<String> dateSet=new HashSet<>();
            //先获取当前list数据中存在的年月信息 分类---->map中；
            Map<String,List<Map>> dateMap=new LinkedHashMap<>();
            for (Map map:list){
                if(map.get("createTime")==null){   //判断 createTime为 null 则不会执行下面语句
                    continue;
                }
                Date buyCourseTime=(Date) map.get("createTime");
                String formatStr=DateUtil.format(buyCourseTime,"yyyy年MM月");
                if(dateMap.get(formatStr)!=null){
                    continue;
                }
                dateMap.put(formatStr,new ArrayList<Map>());
            }
            //遍历list中数据逐个判断list类型
            for (Map map:list){
                if ( map.get("startTime") == null) {   //判断 startTime null  则不会执行下面语句
                    map.put("startTime", "");
                    continue;
                }
                Object endTimeObj = map.get("endTime");
                Date startTime = (Date) map.get("startTime");
                Object object = map.get("startTime") ;
                if(object != null){
                    map.put("startTime",DateUtil.transFormationStringDate(object.toString()));
                }
                object = map.get("endTime");
                if(object != null){
                    map.put("endTime",  DateUtil.transFormationStringDate(object.toString()));
                }
                String liveTimeStatus = getLiveTimeStatus(startTime, (endTimeObj==null)?null:(Date)endTimeObj);
                map.put("liveTimeStatus", liveTimeStatus);
                if(map.get("noUpdatedCount") == null || Integer.valueOf(map.get("noUpdatedCount").toString())<0){
                    map.put("noUpdatedCount",0);
                }
                Date buyCourseTime=(Date) map.get("createTime");  //用户购买课程时间
                String  buyCourseTimeFormat= DateUtil.format(buyCourseTime, "yyyy年MM月");//格式化时间 分类
                Set dateKeys = dateMap.keySet();
                //逐个判断list中集合数据是属于哪个年月类的
                for (Object dateFormat:dateKeys){
                    if (dateFormat!=null && dateFormat.toString().equals(buyCourseTimeFormat)){
                        List<Map> dateList = dateMap.get(dateFormat);
                        map.put("createTime",  DateUtil.format(buyCourseTime, "yyyy-MM-dd HH:mm"));
                        dateList.add(map);
                        break;
                    }
                }
            }
            //map 集合转换list集合 返回给客户端
            List<Map> dataList=new ArrayList<>();
            for(String dateKey:dateMap.keySet()){
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("dateStr",dateKey);
                dataMap.put("dataList",dateMap.get(dateKey));
                dataList.add(dataMap);
            }
            return dataList;
        }
        return null;
    }
    /**
     * 通用视频时间状态 0-预告 1-直播中 2-已结束
     *
     * @param startTime
     * @param endTime
     * @return
     */
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

    /**
     * 更新报名状态
     * @param id
     * @param signUpStatus
     * @return
     */
    @Override
    public int updateSignUpStatus(long id, String signUpStatus  , long  invitationAppId) {
        return joinCourseRecordMapper.updateSignUpStatusById(id, signUpStatus, invitationAppId);
    }

    /**
     * 更新报名状态 by 课程I的和appid
     * @param courseId
     * @param appId
     * @param signUpStatus
     * @param invitationAppId
     * @return
     */
    @Override
    public int updateSignUpStatusByCourseIdAndAppId(long courseId , long appId, String signUpStatus  , long  invitationAppId) {
        return joinCourseRecordMapper.updateSignUpStatusByCourseIdAndAppId(courseId, appId, signUpStatus, invitationAppId);
    }



    /**
     * 获取课程Id  根据Id
     * @param id
     * @return
     */
    @Override
    public long getCourseIdById(long id) {
         Long courseId = joinCourseRecordMapper.getCourseIdById(id);
        if(courseId != null) return  courseId;
        return 0;
    }



    /**
     * 加载对应的加入课程的人员到redis
     * @param courseId
     */
    public void loadJoinCourseUser(Long courseId){
        String key = RedisKey.ll_join_course_user_key + courseId;
        if (redisUtil.exists(key)){
            return ;
        }

        String lockKey = RedisKey.ll_join_lock_pre + courseId;
        if (redisLock.lock(lockKey , 200 * 1000, 6)) {
            try {
                key = RedisKey.ll_join_course_user_key + courseId;
                if (redisUtil.exists(key)){
                    return ;
                }
                List<Map> res = joinCourseRecordMapper.getJoinCourseUser(courseId);
                //放入zset里面，方便以后做分页列表,数据统计
                if (res.size() > 0) {
                    //long firstUserTime = 0;
                    for (int i = 0 ;i < res.size() ;i++) {
                        //设置第一个加入用户的时间
                        Map m = res.get(i);
//                        if (i == 0) {
//                            firstUserTime =((Date)m.get("CREATE_TIME")).getTime();
//                            redisUtil.setex(RedisKey.ll_join_course_first_user , 24 * 60 * 60 * 5 , String.valueOf(firstUserTime));
//                        }
                        Long date = ((Date)m.get("CREATE_TIME")).getTime();
                        //是虚拟用户,-5年时间，，让它们排在实际用户后面 ,
                        if ("1".equals( m.get("IS_VIRTUAL_USER"))) {
                            date = date -   5l * 365 * 24 * 60 * 60 * 1000;
                        }

                        redisUtil.zadd(key ,String.valueOf(m.get("APP_ID")) , date );
                        Map m2 = new HashMap();
                        m2.put("id", m.get("APP_ID"));
                        m2.put("name", m.get("NAME"));
                        m2.put("photo", m.get("PHOTO"));
                        redisUtil.hset(RedisKey.ll_user_info ,  String.valueOf(m.get("APP_ID")) , JsonUtil.toJson(m2));
                    }
                    //处理分值，将打赏的值融入列表中，便于提取,用队头的分值－打赏的金额，打赏的越多，就能越排在前面
                    List<Map> userRewards = userRewardRecordService.loadUserReward2Redis(courseId, true);
                    if (userRewards != null) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.AM_PM , 0);
//                        calendar.set(calendar.get(Calendar.YEAR) , 0, 1 , 0, 0 , 0  );
//                        calendar.set(Calendar.MILLISECOND , 0);

                        for (Map m : userRewards) {
                            String userId = (String)m.get("userId");
                            //加基础100年时间，*100去掉小数点, 让它们排在实际用户前面
                            BigDecimal amout = ((BigDecimal) m.get("amout"));
                            amout = amout.add(new BigDecimal(String.valueOf(100l * 365 * 24 * 60 * 60 * 1000))).multiply(new BigDecimal(100l));
                            //增加5年时间，让它们排在实际用户前面
                            redisUtil.zadd(key ,String.valueOf(userId) ,   amout.doubleValue() );
                        }
                    }

                    //只保存5天时间
                    redisUtil.expire(key , 24 * 60 * 60 * 5);
                }

                //课程参加学习基数,后台设置的
                CourseBaseNum base = courseBaseNumMapper.selectByCourse(courseId , "0");

                long baseCount = 0l;
                if (base != null) {
                    baseCount = base.getCount();
                }

                redisUtil.hset(RedisKey.ll_course_base_join_num, String.valueOf(courseId) , String.valueOf(baseCount));
            } finally {
                redisLock.unlock(lockKey);
            }
        }
    }

    /**
     * 取得课程参加人数
     * @param courseId
     * @return
     */
    @Override
    public long getCountByCourseId(Long courseId) {
        loadJoinCourseUser(courseId);
        //课程参加学习基数,后台设置的
        String value = redisUtil.hget(RedisKey.ll_course_base_join_num, String.valueOf(courseId));
        if (StringUtils.isEmpty(value)) {
            value = "0";
        }
        return redisUtil.zcard(RedisKey.ll_join_course_user_key + courseId) + Long.parseLong(value);
    }

    /**
     * 将加入课程的人员添加 到redis列表里面
     * @param userId
     * @param createTime
     */
    @Override
    public void addJoinUser2Redis(Long courseId , Long userId , Date createTime){
        this.addJoinUser2Redis(courseId , userId , createTime , false);
    }

    @Override
    public void addJoinUser2Redis(Long courseId, Long userId, Date createTime, boolean isVirtualUser) {
        loadJoinCourseUser(  courseId);
        String key = RedisKey.ll_join_course_user_key + courseId;

        Long date = createTime.getTime();
        //是虚拟用户,-5年时间，，让它们排在实际用户后面
        if (isVirtualUser) {
            date  =  date -   5l * 365 * 24 * 60 * 60 * 1000;
        }

        redisUtil.zadd(key, String.valueOf(userId), date );
        //redisUtil.expire(key , 24 * 60 * 60 * 5);//放到队列处理
        //加入课程计数处理,将最新的数据写入course表中的joinCount字段
        redisUtil.lpush(RedisKey.ll_join_count_wait2db, String.valueOf(courseId));
        Map sendMsg = new HashMap();
        sendMsg.put("courseId",String.valueOf( courseId));
        sendMsg.put("userId" , String.valueOf(userId));
        redisUtil.lpush(RedisKey.ll_course_join_user_deal ,JsonUtil.toJson(sendMsg));
    }

    @Override
    public List<Map> getCourseMessageTask(DataGridPage page) {
        return joinCourseRecordMapper.getCourseMessageTaskPage(page);
    }

    /**
     * 加入课程人数写入课程数据库
     * @param courseId
     * @param count
     */
    @Override
    public void writeJoinCount2DB(long courseId, long count) {
        if(courseId>SystemCofigConst.RELAY_COURSE_ID_SIZE){
            joinCourseRecordMapper.updateCourseRelayJoinCount(courseId,count);
        }else{
            joinCourseRecordMapper.updateCourseJoinCount(courseId,count);
        }

    }

    /**
     * 处理 支付中的订单 更改为支付失败
     * @param date
     */
    @Override
    public List<Map>  getHandlerPayingCourse(Date date) {
        return  joinCourseRecordMapper.getHandlerPayingJoinCourse(date);
    }

    /**
     * 更新状态  --失败
     * @param id
     * @return
     */
    @Override
    public  int handlerPayingJoinCourse(long id){
        return  joinCourseRecordMapper.handlerPayingJoinCourse(id);
    }

    /**
     * 根据直播间ID 获取所有报名学习人数
     * @param roomId
     * @return
     */
    @Override
    public long getCountByRoomId(long roomId) {
        return joinCourseRecordMapper.getCountByLiveId(roomId);
    }


    /**
     * 用户报名
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @param joinType
     * @param roomId
     * @return
     * @throws Exception
     */
    @Override
    public JoinCourseRecord handlerJoinCourseRecord(AppUserIdentity appUserIdentity ,
                                                    ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId )throws Exception{

        JoinCourseRecord joinCourseRecord = getByAppIdAndCourseIdByRedis(appUserIdentity.getId(), thirdPayDto.getCourseId() , true);
        //课程查找直播间ID
        if(joinCourseRecord == null || "-1".equals(joinCourseRecord.getSignUpStatus())){
            if(!"4".equals(joinType)){
                joinCourseRecord = new JoinCourseRecord();
                joinCourseRecord.setAppId(appUserIdentity.getId());
                joinCourseRecord.setCourseId(thirdPayDto.getCourseId());
                joinCourseRecord.setRoomId(roomId);
                joinCourseRecord.setIsFree("0");
                //判断是否是第一次报名
                joinCourseRecord.setIsFirst("1");
                joinCourseRecord.setSignUpStatus("1");
                joinCourseRecord.setJoinType(joinType);
                joinCourseRecord.setInvitationUserId(thirdPayDto.getInvitationAppId());
                boolean isFirst = getFirstJoinByAppId(appUserIdentity.getId());
                if(isFirst)  joinCourseRecord.setIsFirst("0");
                if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) <=0 ){
                    joinCourseRecord.setIsFree("1");
                }
                //创建报名记录
                insert(joinCourseRecord);
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
            }
        }else {
            if("0".equals(joinCourseRecord.getSignUpStatus()) //提示已经支付中...
                    || "2".equals(joinCourseRecord.getSignUpStatus())){    //失败的
                //课程查找直播间ID
                updateSignUpStatusByCourseIdAndAppId(joinCourseRecord.getCourseId(), joinCourseRecord.getAppId(), "1", thirdPayDto.getInvitationAppId());//直接更新支付成功
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
            }else  if("1".equals(joinCourseRecord.getSignUpStatus())){
                //提示已经支付
                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                resultDto.setExt("return");
                return  joinCourseRecord;
            }
        }

        //场控人员和管理员记录数据
        if(JoinCourseRecordType.contrl_user.getValue().equals(joinType) ||
                JoinCourseRecordType.virtual_user.getValue().equals(joinType)||
                JoinCourseRecordType.invi_code.getValue().equals(joinType)){
            //将报名的人写入redis
            addJoinUser2Redis(joinCourseRecord.getCourseId(), joinCourseRecord.getAppId(), new Date() , JoinCourseRecordType.virtual_user.getValue().equals(joinType));
            //自动关注老师
            //userFollowService.follow(appUserIdentity.getId(), roomId);
        }
        if (JoinCourseRecordType.invi_code.getValue().equals(joinType)) {
            Map map = new HashMap();
            map.put("courseId" , String.valueOf(joinCourseRecord.getCourseId()));
            map.put("appId" , String.valueOf(joinCourseRecord.getAppId()));

            redisUtil.lpush(RedisKey.ll_series_course_join_user , JsonUtil.toJson(map));
        }

        return  joinCourseRecord;
    }
    /**
     * 用户报名购买转播
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @param joinType
     * @param roomId
     * @return
     * @author qym
     * @throws Exception
     */
    @Override
    public JoinCourseRecord handlerJoinCourseRecordRelay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto, ActResultDto resultDto, String joinType, long roomId) throws Exception {
        JoinCourseRecord joinCourseRecord = getByAppIdAndCourseIdByRedis(appUserIdentity.getId(), thirdPayDto.getCourseId() , true);
        //课程查找直播间ID
        if(joinCourseRecord == null || "-1".equals(joinCourseRecord.getSignUpStatus())){
            if(!"4".equals(joinType)){
                joinCourseRecord = new JoinCourseRecord();
                joinCourseRecord.setAppId(appUserIdentity.getId());
                joinCourseRecord.setCourseId(thirdPayDto.getCourseId());
                joinCourseRecord.setRoomId(roomId);
                joinCourseRecord.setIsFree("0");
                joinCourseRecord.setCourseType(1);
                //判断是否是第一次报名
                joinCourseRecord.setIsFirst("1");
                joinCourseRecord.setSignUpStatus("1");
                joinCourseRecord.setJoinType(joinType);
                joinCourseRecord.setInvitationUserId(thirdPayDto.getInvitationAppId());
                boolean isFirst = getFirstJoinByAppId(appUserIdentity.getId());
                if(isFirst)  joinCourseRecord.setIsFirst("0");
                if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) <=0 ){
                    joinCourseRecord.setIsFree("1");
                }
                //创建报名记录
                insert(joinCourseRecord);
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
            }
        }else {
            if("0".equals(joinCourseRecord.getSignUpStatus()) //提示已经支付中...
                    || "2".equals(joinCourseRecord.getSignUpStatus())){    //失败的
                //课程查找直播间ID
                updateSignUpStatusByCourseIdAndAppId(joinCourseRecord.getCourseId(), joinCourseRecord.getAppId(), "1", thirdPayDto.getInvitationAppId());//直接更新支付成功
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
            }else  if("1".equals(joinCourseRecord.getSignUpStatus())){
                //提示已经支付
                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                resultDto.setExt("return");
                return  joinCourseRecord;
            }
        }

        //场控人员和管理员记录数据
        if(JoinCourseRecordType.contrl_user.getValue().equals(joinType) ||
                JoinCourseRecordType.virtual_user.getValue().equals(joinType)||
                JoinCourseRecordType.invi_code.getValue().equals(joinType)){
            //将报名的人写入redis
            addJoinUser2Redis(joinCourseRecord.getCourseId(), joinCourseRecord.getAppId(), new Date() , JoinCourseRecordType.virtual_user.getValue().equals(joinType));
            //自动关注老师
            //userFollowService.follow(appUserIdentity.getId(), roomId);
        }
        if (JoinCourseRecordType.invi_code.getValue().equals(joinType)) {
            Map map = new HashMap();
            map.put("courseId" , String.valueOf(joinCourseRecord.getCourseId()));
            map.put("appId" , String.valueOf(joinCourseRecord.getAppId()));

            redisUtil.lpush(RedisKey.ll_series_course_join_user , JsonUtil.toJson(map));
        }

        return  joinCourseRecord;
    }

}

