package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.UserCountService;
import com.longlian.type.CountType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by lh on 2016/5/5.
 * 状态统计
 */
@Controller
@RequestMapping(value = "/stat")
public class StatController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserCountService userCountService;

    /**
     * 主页面
     *
     * @return
     */
    @RequestMapping
    public ModelAndView stat() {
        ModelAndView view = new ModelAndView("/func/stat/index");
        return view;
    }

    @RequestMapping(value = "/getCountList")
    @ResponseBody
    public DatagridResponseModel getCountList(DatagridRequestModel requestModel,@RequestParam Map map,@RequestParam(value = "order", required = false)String orderType) throws Exception {
        ActResult result=new ActResult();
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(userCountService.getCountListForPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getCount() throws Exception {

        String dateStr = getDateStr();
        BigDecimal dayNewUserCount =redisUtil.exists(RedisKey.ll_new_user_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_user_key + dateStr))):new BigDecimal(0); //新增用户数
        BigDecimal dayNewTeacherCount =redisUtil.exists(RedisKey.ll_new_teacher_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_teacher_key + dateStr))):new BigDecimal(0); //新增老师数
        BigDecimal dayActiveCount =redisUtil.exists(RedisKey.ll_active_user_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_active_user_key + dateStr))):new BigDecimal(0); //活跃用户数
        BigDecimal dayPayCount =redisUtil.exists(RedisKey.ll_pay_user_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_pay_user_key + dateStr))):new BigDecimal(0); //付费用户数
        BigDecimal dayPayPmtCount =  redisUtil.exists(RedisKey.ll_pay_amt_count_key + dateStr)?getCount(String.valueOf(redisUtil.get(RedisKey.ll_pay_amt_count_key + dateStr))).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP):new BigDecimal("0.00");//付费总金额
        BigDecimal dayPVCount =redisUtil.exists(RedisKey.ll_pv_count_key + dateStr)?getCount(String.valueOf(redisUtil.get(RedisKey.ll_pv_count_key + dateStr))):new BigDecimal(0); //pv
        BigDecimal dayNewCourseCounts =redisUtil.exists(RedisKey.ll_new_course_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_course_key + dateStr))):new BigDecimal(0); //新增课程数
        BigDecimal dayAllCoursePayCounts =redisUtil.exists(RedisKey.ll_new_pay_course_key + dateStr)?getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_pay_course_key + dateStr))):new BigDecimal(0); //新增付费课程数

        //当天课程付费率
        Set<String> newCourses= redisUtil.smembers(RedisKey.ll_new_course_key + dateStr);
        Set<String> newPayCourses= redisUtil.smembers(RedisKey.ll_new_pay_course_key + dateStr);
        BigDecimal daynewCoursePayCount =new BigDecimal(0); //新增付费(新增的)课程数
        if(null!=newCourses && newCourses.size()>0 && null !=newPayCourses && newPayCourses.size()>0)
        {
            daynewCoursePayCount  = new BigDecimal(this.sameSet(newCourses,newPayCourses).size());
        }
        //当天用户付费率
        Set<String> newUsers= redisUtil.smembers(RedisKey.ll_new_user_key + dateStr);
        Set<String> UserPays= redisUtil.smembers(RedisKey.ll_pay_user_key + dateStr);
        BigDecimal  daynewUserPayCounts=new BigDecimal(0); //新增付费(新增的)用户数
        if(null!=newUsers && newUsers.size()>0 && null !=UserPays && UserPays.size()>0) {
            daynewUserPayCounts = new BigDecimal(this.sameSet(newUsers, UserPays).size());
        }
        //当天用户留存率(昨天的用户)
        BigDecimal dayretentionRet =new BigDecimal(0); //新增付费(新增的)课程数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, -1);
        String dateStr2 = DateFormatUtils.format(calendar, "yyyyMMdd");
        BigDecimal  yesNewUserCounts= getCount(String.valueOf(redisUtil.scard(RedisKey.ll_new_user_key + dateStr2)));
        Set<String> yesNewUsers= redisUtil.smembers(RedisKey.ll_new_user_key + dateStr2);

        //当天用户留存率(今天活跃用户)
        Set<String> todayActiveusers= redisUtil.smembers(RedisKey.ll_active_user_key + dateStr);
        if (null != yesNewUsers && yesNewUsers.size() > 0 && null != todayActiveusers && todayActiveusers.size()>0 && yesNewUserCounts.intValue()>0) {
            dayretentionRet= new BigDecimal(this.sameSet(todayActiveusers, yesNewUsers).size()).divide(yesNewUserCounts, 4, RoundingMode.HALF_UP);
        }
        Map<String,String> dayMap = new HashMap<>();
        dayMap.put("dayNewUserCount",dayNewUserCount+"");
        dayMap.put("dayNewTeacherCount",dayNewTeacherCount+"");
        dayMap.put("dayPayCount",dayPayCount+"");
        dayMap.put("dayActiveCount",dayActiveCount+"");
        dayMap.put("dayPVCount",dayPVCount+"");
        dayMap.put("dayNewCourseCounts",dayNewCourseCounts+"");
        dayMap.put("daynewCoursePayCount",daynewCoursePayCount+"");


        dayMap.put("dayAllCoursePayCounts",dayAllCoursePayCounts+"");

        dayMap.put("daynewUserPayCounts",daynewUserPayCounts+"");
        dayMap.put("dayPayPmtCount",dayPayPmtCount+"");
        dayMap.put("dayretentionRetPercent",toPercent(dayretentionRet));
        dayMap.put("dayPayRet",getPayRet(dayNewUserCount,daynewUserPayCounts ));
        dayMap.put("coursePayRet",getPayRet(dayNewCourseCounts,daynewCoursePayCount ));

//        redisUtil.del(RedisKey.ll_console_count_key);

        Map<String,String> map = null;
        if (redisUtil.exists(RedisKey.ll_console_count_key+dateStr)) {
            Map<String,String> mapv= redisUtil.hmgetAll(RedisKey.ll_console_count_key+dateStr2);
            map= redisUtil.hmgetAll(RedisKey.ll_console_count_key+dateStr);
            //redisUtil.del(RedisKey.ll_console_count_key+dateStr);
        }else {
            map = new HashMap<String,String>();
            Date now = new Date();
            Date Yestday = DateUtil.getYestday(DateUtil.format(new Date(), "yyyyMMdd"));
            Date[] weekLimit = DateUtil.getWeekLimit(now);   //取得指定日期的当周的起始时间
            Date[] monthLimit = DateUtil.getMonthLimit(now); //取得指定日期的当月起始时间
            Date[] yearLimit = DateUtil.getYearLimit(now);   //取得指定日期的当年起始时间

            //用户
            Long yestNewUserCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.NEW_USER.getType()));
            Long weekNewUserCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.NEW_USER.getType()));
            Long monthNewUserCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.NEW_USER.getType()));
            Long yearNewUserCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.NEW_USER.getType()));
            //新增老师
            Long yestNewTeacherCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.NEW_TEACHER.getType()));
            Long weekNewTeacherCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.NEW_TEACHER.getType()));
            Long monthNewTeacherCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.NEW_TEACHER.getType()));
            Long yearNewTeacherCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.NEW_TEACHER.getType()));
            //pv
            Long yestPVCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.START_USER.getType()));
            Long weekPVCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.START_USER.getType()));
            Long monthPVCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.START_USER.getType()));
            Long yearPVCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.START_USER.getType()));
            //活跃用户
            Long yestActiveCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.ACTIVE_USER.getType()));
            Long weekActiveCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.ACTIVE_USER.getType()));
            Long monthActiveCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.ACTIVE_USER.getType()));
            Long yearActiveCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.ACTIVE_USER.getType()));

            //新增付费人数
            Long yestPayCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.NEW_USER_PAY.getType()));
            Long weekPayCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.NEW_USER_PAY.getType()));
            Long monthPayCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.NEW_USER_PAY.getType() ));
            Long yearPayCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.NEW_USER_PAY.getType()));
            //付费总金额
            BigDecimal yestPayAmtCount = userCountService.getUserCountDouble(Yestday, Yestday, Integer.valueOf(CountType.USER_PAY_AMT.getType()));
            BigDecimal weekPayAmt = userCountService.getUserCountDouble(weekLimit[0], now, Integer.valueOf(CountType.USER_PAY_AMT.getType()));
            BigDecimal monthPayAmt = userCountService.getUserCountDouble(monthLimit[0], now, Integer.valueOf(CountType.USER_PAY_AMT.getType()));
            BigDecimal yearPayAmt = userCountService.getUserCountDouble(yearLimit[0], now, Integer.valueOf(CountType.USER_PAY_AMT.getType()));

            //新增单节课
            Long yestNewCourseCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.NEW_COURSE.getType()));
            Long weekNewCourseCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.NEW_COURSE.getType()));
            Long monthNewCourseCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.NEW_COURSE.getType()));
            Long yearNewCourseCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.NEW_COURSE.getType()));

            //新增付费单节课(新增加的)
            Long yestCoursePayCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.NEW_COURSE_PAY.getType()));
            Long weekCoursePayCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.NEW_COURSE_PAY.getType()));
            Long monthCoursePayCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.NEW_COURSE_PAY.getType()));
            Long yearCoursePayCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.NEW_COURSE_PAY.getType()));

            //新增付费单节课(所有付费的)
            Long yestAllCoursePayCount = userCountService.getCountUser(Yestday, Yestday, Integer.valueOf(CountType.ALL_COURSE_PAY.getType()));
            Long weekAllCoursePayCount = userCountService.getCountUser(weekLimit[0], now, Integer.valueOf(CountType.ALL_COURSE_PAY.getType()));
            Long monthAllCoursePayCount = userCountService.getCountUser(monthLimit[0], now, Integer.valueOf(CountType.ALL_COURSE_PAY.getType()));
            Long yearAllCoursePayCount = userCountService.getCountUser(yearLimit[0], now, Integer.valueOf(CountType.ALL_COURSE_PAY.getType()));



             //次留存率
            int weekDays = DateUtil.getIntervalOfDays(weekLimit[0] ,  now);
            int monthDays = DateUtil.getIntervalOfDays(monthLimit[0] ,  now);
            int yearDays = DateUtil.getIntervalOfDays(yearLimit[0] ,  now);


            BigDecimal yestRetentionRet = userCountService.getUserCountDouble(Yestday, Yestday, Integer.valueOf(CountType.DAY_RETENTION.getType()));
            BigDecimal weekRetentionRet = userCountService.getUserCountDouble(weekLimit[0], now, Integer.valueOf(CountType.DAY_RETENTION.getType()));
            if (weekDays != 0){
                weekRetentionRet = weekRetentionRet.divide(new BigDecimal(weekDays), 4, RoundingMode.HALF_UP);
            }
            BigDecimal monthRetentionRet = userCountService.getUserCountDouble(monthLimit[0], now, Integer.valueOf(CountType.DAY_RETENTION.getType()));
            if(monthDays!=0){  //当遇到 每个月的1号时  monthdays=0; 默认为第一天值  无需计算   monthdays>0 可计算
                monthRetentionRet = monthRetentionRet.divide(new BigDecimal(monthDays), 4, RoundingMode.HALF_UP);
            }
            BigDecimal yearRetentionRet = userCountService.getUserCountDouble(yearLimit[0], now, Integer.valueOf(CountType.DAY_RETENTION.getType()));
           if(yearDays!=0){
               yearRetentionRet= yearRetentionRet.divide(new BigDecimal(yearDays), 4, RoundingMode.HALF_UP);
           }
            map.put("yestNewUserCount", yestNewUserCount+"");
            map.put("weekNewUserCount", weekNewUserCount+"");
            map.put("monthNewUserCount", monthNewUserCount+"");
            map.put("yearNewUserCount", yearNewUserCount+"");
            map.put("yestNewTeacherCount", yestNewTeacherCount+"");
            map.put("weekNewTeacherCount", weekNewTeacherCount+"");
            map.put("monthNewTeacherCount", monthNewTeacherCount+"");
            map.put("yearNewTeacherCount", yearNewTeacherCount+"");
            map.put("yestPVCount", yestPVCount+"");
            map.put("weekPVCount", weekPVCount+"");
            map.put("monthPVCount", monthPVCount+"");
            map.put("yearPVCount", yearPVCount+"");
            map.put("yestActiveCount", yestActiveCount+"");
            map.put("weekActiveCount", weekActiveCount+"");
            map.put("monthActiveCount", monthActiveCount+"");
            map.put("yearActiveCount", yearActiveCount+"");
            map.put("yestPayCount", yestPayCount+"");
            map.put("weekPayCount", weekPayCount+"");
            map.put("monthPayCount", monthPayCount+"");
            map.put("yearPayCount", yearPayCount+"");

            //新增加用户付费率
            map.put("yestPayRet" , this.getPayRet(yestNewUserCount , yestPayCount));

            map.put("yestNewCourseCount", yestNewCourseCount+"");
            map.put("weekNewCourseCount", weekNewCourseCount+"");
            map.put("monthNewCourseCount", monthNewCourseCount+"");
            map.put("yearNewCourseCount", yearNewCourseCount+"");
            map.put("yestAllCoursePayCount", yestAllCoursePayCount+"");

            map.put("weekAllCoursePayCount", weekAllCoursePayCount+"");
            map.put("monthAllCoursePayCount", monthAllCoursePayCount+"");
            map.put("yearAllCoursePayCount", yearAllCoursePayCount+"");
            //新增付费单节课(新增加的)
            map.put("weekCoursePayCount", weekCoursePayCount+"");
            map.put("monthCoursePayCount", monthCoursePayCount+"");
            map.put("yearCoursePayCount", yearCoursePayCount+"");


            map.put("yestPayAmtCount", yestPayAmtCount+"");
            map.put("weekPayAmtCount", weekPayAmt+"");
            map.put("monthPayAmtCount", monthPayAmt+"");
            map.put("yearPayAmtCount", yearPayAmt+"");
            //次留存
            map.put("yestRetentionRetPercent", toPercent(yestRetentionRet));
            map.put("weekRetentionRetPercent", toPercent(weekRetentionRet));
            map.put("monthRetentionRetPercent", toPercent(monthRetentionRet));
            map.put("yearRetentionRetPercent", toPercent(yearRetentionRet));

            //新增课程付费率
            map.put("yestNewCoursePayRet", getPayRet(yestNewCourseCount, yestCoursePayCount));

            redisUtil.hmset(RedisKey.ll_console_count_key+dateStr, map, RedisKey.ll_console_count_valid_time);
        }
        //用户
        long weekNewUserCount = Long.valueOf(map.get("weekNewUserCount").toString())+dayNewUserCount.longValue();
        long monthNewUserCount = Long.valueOf(map.get("monthNewUserCount").toString())+dayNewUserCount.longValue();
        long yearNewUserCount = Long.valueOf(map.get("yearNewUserCount").toString())+dayNewUserCount.longValue();
        map.put("weekNewUserCount", String.valueOf(weekNewUserCount));
        map.put("monthNewUserCount",String.valueOf(monthNewUserCount));
        map.put("yearNewUserCount",String.valueOf(yearNewUserCount));
        //新增老师
        map.put("weekNewTeacherCount",String.valueOf(Long.valueOf(map.get("weekNewTeacherCount").toString())+dayNewTeacherCount.longValue()));
        map.put("monthNewTeacherCount", String.valueOf(Long.valueOf(map.get("monthNewTeacherCount").toString())+dayNewTeacherCount.longValue()));
        map.put("yearNewTeacherCount",String.valueOf(Long.valueOf(map.get("yearNewTeacherCount").toString())+dayNewTeacherCount.longValue()));
        //pv
        map.put("weekPVCount",String.valueOf(Long.valueOf(map.get("weekPVCount").toString())+dayPVCount.longValue()));
        map.put("monthPVCount",String.valueOf(Long.valueOf(map.get("monthPVCount").toString())+dayPVCount.longValue()));
        map.put("yearPVCount",String.valueOf(Long.valueOf(map.get("yearPVCount").toString())+dayPVCount.longValue()));
        //活跃用户
        map.put("weekActiveCount",String.valueOf(Long.valueOf(map.get("weekActiveCount").toString())+dayActiveCount.longValue()));
        map.put("monthActiveCount",String.valueOf(Long.valueOf(map.get("monthActiveCount").toString())+dayActiveCount.longValue()));
        map.put("yearActiveCount", String.valueOf(Long.valueOf(map.get("yearActiveCount").toString())+dayActiveCount.longValue()));

        //新增付费人数
        long weekPayCount = Long.valueOf(map.get("weekPayCount").toString())+daynewUserPayCounts.longValue();
        long monthPayCount = Long.valueOf(map.get("monthPayCount").toString())+daynewUserPayCounts.longValue();
        long yearPayCount = Long.valueOf(map.get("yearPayCount").toString())+daynewUserPayCounts.longValue();
        map.put("weekPayCount", String.valueOf(weekPayCount));
        map.put("monthPayCount",String.valueOf(monthPayCount));
        map.put("yearPayCount",String.valueOf(yearPayCount));
        //付费总金额
        map.put("weekPayAmt",getAddStringAndBigDecimal(map.get("weekPayAmtCount").toString() ,dayPayPmtCount));
        map.put("monthPayAmt",getAddStringAndBigDecimal(map.get("monthPayAmtCount").toString() ,dayPayPmtCount));
        map.put("yearPayAmt",getAddStringAndBigDecimal(map.get("yearPayAmtCount").toString() ,dayPayPmtCount));

        //新增单节课
        long weekNewCourseCount = Long.valueOf(map.get("weekNewCourseCount").toString())+dayNewCourseCounts.longValue();
        long monthNewCourseCount = Long.valueOf(map.get("monthNewCourseCount").toString())+dayNewCourseCounts.longValue();
        long yearNewCourseCount = Long.valueOf(map.get("yearNewCourseCount").toString())+dayNewCourseCounts.longValue();
        map.put("weekNewCourseCount",String.valueOf(weekNewCourseCount));
        map.put("monthNewCourseCount",String.valueOf(monthNewCourseCount));
        map.put("yearNewCourseCount",String.valueOf(yearNewCourseCount));

        //新增付费单节课
        map.put("weekAllCoursePayCount",String.valueOf(Long.valueOf(map.get("weekAllCoursePayCount").toString())+dayAllCoursePayCounts.longValue()));
        map.put("monthAllCoursePayCount",String.valueOf(Long.valueOf(map.get("monthAllCoursePayCount").toString())+dayAllCoursePayCounts.longValue()));
        map.put("yearAllCoursePayCount",String.valueOf(Long.valueOf(map.get("yearAllCoursePayCount").toString())+dayAllCoursePayCounts.longValue()));


        //新增加用户付费率
        map.put("weekPayRet" , this.getPayRet( weekNewUserCount , weekPayCount));
        map.put("monthPayRet" , this.getPayRet(monthNewUserCount ,monthPayCount));
        map.put("yearPayRet" , this.getPayRet(yearNewUserCount ,  yearPayCount));

        //新增课程付费率
        long weekCoursePayCount = Long.valueOf(map.get("weekCoursePayCount").toString())+dayNewCourseCounts.longValue();
        long monthCoursePayCount = Long.valueOf(map.get("monthCoursePayCount").toString())+dayNewCourseCounts.longValue();
        long yearCoursePayCount = Long.valueOf(map.get("yearCoursePayCount").toString())+dayNewCourseCounts.longValue();

        map.put("weekNewCoursePayRet", getPayRet(weekNewCourseCount , weekCoursePayCount ));
        map.put("monthNewCoursePayRet", getPayRet(monthNewCourseCount , monthCoursePayCount ));
        map.put("yearNewCoursePayRet", getPayRet(yearNewCourseCount, yearCoursePayCount ));



        Map totalMap = new HashMap<>();
           totalMap.put("dayMap",dayMap);
           totalMap.put("oldMap",map);
            return ActResult.success(totalMap);
        }

        private String getPayRet(BigDecimal counts , BigDecimal payCount ) {
            BigDecimal dayPayRet = new BigDecimal(0);
            if (payCount.compareTo(dayPayRet) > 0) {
                dayPayRet = payCount.divide(counts , 4 , RoundingMode.HALF_UP);
            }
            return toPercent(dayPayRet);
        }
        private String getPayRet(Long counts , Long payCount ) {
            return getPayRet(new BigDecimal(counts) , new BigDecimal(payCount) );
        }

        private String getAddStringAndBigDecimal(String old , BigDecimal nowDay) {
             BigDecimal oldBig = new BigDecimal(old);
             oldBig = oldBig.add(nowDay);
             return oldBig.toString();
        }
    /**
     *
     * @return
     */
    private String toPercent(BigDecimal bd) {

        if (bd.compareTo(new BigDecimal(0)) == 0) {
            return "0.00%";
        }

       BigDecimal result =  bd.multiply(new BigDecimal(100));
        //四舍五入
          result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
       return result.toString() + "%";
    }

    public BigDecimal getCount(String val) {
        BigDecimal result = null ;
        BigDecimal bd=new BigDecimal(val);
        if (val != null) {
            result =bd;
        }
        return result;
    }
    private String getDateStr() {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return dateStr;
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
    @RequestMapping("/getcountMap")
    @ResponseBody
    public ActResult getCountMap(String dayTime,String beginTime,String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult= userCountService.getLiveChannelUsingCount(dayTime,beginTime,endTime);
        return actResult;
    }
}
