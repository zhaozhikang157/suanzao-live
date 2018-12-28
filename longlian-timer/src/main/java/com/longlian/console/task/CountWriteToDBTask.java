package com.longlian.console.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.UserCountMapper;
import com.longlian.live.service.CountService;
import com.longlian.model.UserCount;
import com.longlian.type.CountType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 计数写入db
 * Created by lihan on 2016/6/29.
 */
@Component("countWriteToDBTask")
public class CountWriteToDBTask extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(CountWriteToDBTask.class);
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CountService countService;

    @Autowired
    UserCountMapper userCountMapper;

    @Override
    public String getTaskName() {
        return "计数写入db";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("计数写入db异常："+e.getMessage());
        }
    }

    /***
     * 每天晚上1点触发
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    public void doJob() throws Exception{
        try {
            writeNewCourseCount();       //新增课程数
            writeAllCoursePayCount();    //新增付费课程数
            writeNewCoursePayCount();    //新增付费(新增的)课程数
            //writeAllPlatformCourseCounts();//新增平台开课数

            writeNewUserCount();         //新增用户数
            writeNewTeacherCount();      //新增老师数
            writePayUserCount();         //付费总用户数
            writePayAmtCount();          //付费总金额
            writeNewUserPayCount();      //新增付费(新增的)用户数
            writeActiveUserCount();      //活跃用户
            writePvCount();              //pv
            writeNewUserPayRet();        //新增用户付费率
            writeNewCoursePayRet();      //新增课程付费率
            writeDayRetentionRet();      //次留存率*/
            writeChannelUsingCount();   //正在使用直播流
        }catch (Exception e)
        {
            e.printStackTrace();

        }

    }

    /**
     * 新增课程数
     */
    public BigDecimal writeNewCourseCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, -1);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        BigDecimal newCourseCount = getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_course_key + dateStr)));
        this.saveCountResult(new UserCount(dateStrSave, "", Integer.valueOf(CountType.NEW_COURSE.getType()), newCourseCount
        ));
        return newCourseCount;
    }
    /**
     * 新增付费课程数
     */
    public void writeAllCoursePayCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.ALL_COURSE_PAY.getType()),
                getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_pay_course_key + dateStr)))
        ));
    }

    /**
     * 新增平台开课数
     */
    public void writeAllPlatformCourseCounts() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.ALL_PLATFORM_COURSE_COUNTS.getType()),
                getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_platform_course_key + dateStr)))
        ));
    }

    /**
     * 新增付费(新增的)课程数
     */
    public BigDecimal writeNewCoursePayCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");

        Set<String> newCourses= redisUtil.smembers(RedisKey.ll_new_course_key + dateStr);
        Set<String> newPayCourses= redisUtil.smembers(RedisKey.ll_new_pay_course_key + dateStr);
        BigDecimal newCoursePayCount = new BigDecimal(0);
        if(null!=newCourses && newCourses.size()>0 && null !=newPayCourses && newPayCourses.size()>0){
            newCoursePayCount  = new BigDecimal(this.sameSet(newCourses,newPayCourses).size());
            this.saveCountResult(new UserCount(dateStrSave,"", Integer.valueOf(CountType.NEW_COURSE_PAY.getType()),newCoursePayCount));
        }else
        {
            this.saveCountResult(new UserCount(dateStrSave,"", Integer.valueOf(CountType.NEW_COURSE_PAY.getType()),new BigDecimal(0)));
        }
        return newCoursePayCount;
    }

    /**
     * 新增用户数
     */
    public BigDecimal writeNewUserCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        BigDecimal newUsers=  getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_user_key + dateStr)));
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.NEW_USER.getType()),newUsers));

       return newUsers;

    }
    /**
     * 新增老师数
     */
    public void writeNewTeacherCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.NEW_TEACHER.getType()),
                getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_teacher_key + dateStr)))
        ));
    }
    /**
      *付费总用户数
    */
    public void writePayUserCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.USER_PAY.getType()),
                getCount(String.valueOf(redisUtil.scard(RedisKey.ll_pay_user_key + dateStr)))
        ));
    }

    /**
     * 付费总金额
     */
    public void writePayAmtCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.USER_PAY_AMT.getType()),redisUtil.exists(RedisKey.ll_pay_amt_count_key + dateStr)==true?getCount(String.valueOf(redisUtil.get(RedisKey.ll_pay_amt_count_key + dateStr))).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP):new BigDecimal("0.00")

        ));
    }

    /**
     * 新增付费(新增的)用户数
     */
    public BigDecimal writeNewUserPayCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");

        Set<String> newUsers= redisUtil.smembers(RedisKey.ll_new_user_key + dateStr);
        Set<String> UserPays= redisUtil.smembers(RedisKey.ll_pay_user_key + dateStr);
        BigDecimal  newUserPayCounts=new BigDecimal(0);
        if(null!=newUsers && newUsers.size()>0 && null !=UserPays && UserPays.size()>0){
            newUserPayCounts = new BigDecimal(this.sameSet(newUsers,UserPays).size());
            this.saveCountResult(new UserCount(dateStrSave,"", Integer.valueOf(CountType.NEW_USER_PAY.getType()),newUserPayCounts));
        }else
        {
            this.saveCountResult(new UserCount(dateStrSave,"", Integer.valueOf(CountType.NEW_USER_PAY.getType()),new BigDecimal(0))
            );
        }
        return newUserPayCounts;
    }

    /**
     *活跃用户
     */
    public void writeActiveUserCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        BigDecimal activeUsers=  getCount(String.valueOf(redisUtil.scard(RedisKey.ll_active_user_key + dateStr)));
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.ACTIVE_USER.getType()),activeUsers
        ));
    }
    /**
     *PV
     */
    public void writePvCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.START_USER.getType()),
                getCount(String.valueOf(redisUtil.get(RedisKey.ll_pv_count_key + dateStr)))
        ));
    }
    /**
     * 新增用户付费率
     */
    public void writeNewUserPayRet() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
    //      String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
    String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        System.out.print("this.writeNewUserPayCount()=======================  "+ this.writeNewUserPayCount());
        System.out.print("this.writeNewUserCount()=======================  " + this.writeNewUserCount());
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.NEW_USER_PAY_RET.getType()),
            this.writeNewUserCount().intValue()!=0?this.writeNewUserPayCount().divide(this.writeNewUserCount(), 4, RoundingMode.HALF_UP):new BigDecimal(0.00)));
}

    /**
     * 新增课程付费率
     */
    public void writeNewCoursePayRet() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
//      String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        this.saveCountResult(new UserCount(dateStrSave,"",Integer.valueOf(CountType.NEW_COURSE_PAY_RET.getType()),
                this.writeNewCourseCount().intValue()!=0?this.writeNewCoursePayCount().divide(this.writeNewCourseCount(), 2, RoundingMode.HALF_UP):new BigDecimal(0.00)));

    }
    /**
     * 次留存率
     */
    public void writeDayRetentionRet() throws Exception{
        //前天新增用户
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        BigDecimal  yesNewUserCounts= getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_user_key + dateStr)));
        Set<String> yesNewUsers= redisUtil.smembers(RedisKey.ll_new_user_key + dateStr);

        //昨天活跃用户
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr1 = DateFormatUtils.format(calendar1, "yyyyMMdd");
        String dateStrSave1 = DateFormatUtils.format(calendar1, "yyyy-MM-dd");
        Set<String> todayActiveusers=redisUtil.smembers(RedisKey.ll_active_user_key + dateStr1);
        if(null!= yesNewUsers && yesNewUsers.size()>0 && null!= todayActiveusers && todayActiveusers.size()>0 && yesNewUserCounts.intValue()>0) {
            this.saveCountResult(new UserCount(dateStrSave1,"",Integer.valueOf(CountType.DAY_RETENTION.getType()),
                    new BigDecimal(this.sameSet(todayActiveusers, yesNewUsers).size()).divide(yesNewUserCounts, 4, RoundingMode.HALF_UP)
            ));
        }else{
            this.saveCountResult(new UserCount(dateStrSave1, "", Integer.valueOf(CountType.DAY_RETENTION.getType()),
                    new BigDecimal("0.00")));
        }
    }



    /**
     * 正在使用直播流统计
     */
    public void writeChannelUsingCount() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");
        String dateStrSave = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        Map<String, String> redisMap = redisUtil.hmgetAll(RedisKey.ll_live_channel_using_counts + "_" + dateStr);
        List<Map.Entry<String, String>> infoIds =  this.commonQueryChannelUsing(redisMap);
        for(Map.Entry<String, String>   mapStr:infoIds){
//            keys.add(mapStr.getKey());
//            values.add(mapStr.getValue());
            this.saveChannelUsingCountResult(new UserCount(dateStrSave + " " + mapStr.getKey(), "", Integer.valueOf(CountType.USING_LIVE_CHANNEL.getType()), new BigDecimal(mapStr.getValue())));
        }
    }
    /**
     * 查询正在使用直播里
     * @return
     */
    private   List<Map.Entry<String, String>> commonQueryChannelUsing(Map redisMap) {
        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(redisMap.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
     
        return infoIds;
    }

    public synchronized void saveCountResult(UserCount uc) {
           /* if (uc.getCount().equals(0)) {
                return ;
            }*/
        List ul = userCountMapper.selectByUserCount(uc);
        if (ul == null || ul.size() == 0) {
            userCountMapper.insert(uc);
        }
    }

    public synchronized void saveChannelUsingCountResult(UserCount uc) {
            userCountMapper.insert(uc);
    }

    public BigDecimal getCount(String val) {
        BigDecimal result = null ;
        BigDecimal bd=new BigDecimal(val);
        if (val != null) {
            result =bd;
        }
        return result;
    }
    /*
     * sameSet方法返回并集
     */
    public Set sameSet(Set<String> s1 , Set<String> s2){
        Set<String> sameSet = new HashSet<String>();
        /*
         * 利用ForEach循环和HashSet中的contains方法判断两个Set中元素是否相交
         * 相交则存入SameSet中
         */
        for(String s : s1){
            if(s2.contains(s))
                sameSet.add(s);
        }
        return sameSet;
    }

    public static void main(String[] args) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        String dateStr = DateFormatUtils.format(calendar, "yyyyMMdd");

        //昨天活跃用户
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr1 = DateFormatUtils.format(calendar1, "yyyyMMdd");
        String dateStrSave1 = DateFormatUtils.format(calendar1, "yyyy-MM-dd");
        System.out.println(dateStr);
        System.out.println(dateStr1);
        System.out.println(dateStrSave1);
    }

}
