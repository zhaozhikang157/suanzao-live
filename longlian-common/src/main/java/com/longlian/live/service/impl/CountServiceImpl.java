package com.longlian.live.service.impl;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CountService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service("countService")
public class CountServiceImpl implements CountService {
    private static Logger log = LoggerFactory.getLogger(CountServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;
    //3天
    int seconds = 3 * 60 * 60 * 24;
    @Override
    public void newUserCount( Long userId) {
        String key = RedisKey.ll_new_user_key + getDateStr();
        add( key ,  userId);
    }

    /**
     * 记数
     * @param key
     */
    private void count(String key) {
        //自增string get()
        redisUtil.incr(key);
        redisUtil.expire(key, seconds);
    }

    /**
     * 记数
     * @param key
     */
    private void sum(String key , BigDecimal amt) {
        //转成long型
        BigDecimal tmp = amt.multiply(new BigDecimal(100));
        //自增string
        redisUtil.incrBy(key, tmp.longValue());
        redisUtil.expire(key, seconds);
    }

    /**
     * 记录到列表里面
     * @param key
     * @param userId
     */
    private void add(String key , Long userId) {
        //会去重set
        redisUtil.sadd(key, String.valueOf(userId));
        redisUtil.expire(key, seconds);
    }

    @Override
    public void newTeacherCount(Long userId) {
        String key = RedisKey.ll_new_teacher_key + getDateStr();
        add(key, userId);
    }
    private String getDateStr() {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return dateStr;
    }

    /**
     * 活跃用户记录，以及pv记数
     * @param userId
     */
    @Override
    public void activeUserCount(Long userId) {
        String dateStr = getDateStr();
        String key = RedisKey.ll_active_user_key + dateStr;
        //活跃用户
        add(  key ,   userId);
        //增加PV
        count(RedisKey.ll_pv_count_key + dateStr);
    }

    @Override
    public void payUserCount(Long userId, BigDecimal amt) {
        String dateStr = getDateStr();
        add(RedisKey.ll_pay_user_key + dateStr ,   userId);
        sum(RedisKey.ll_pay_amt_count_key + dateStr, amt);
    }

    /**
     * 新增课程数
     * @param courseId
     */
    @Override
    public void newCourseCount(Long courseId) {
        String key = RedisKey.ll_new_course_key + getDateStr();
        add(key, courseId);
    }

    /**
     * 新增付费课程数
     * @param courseId
     */
    @Override
    public void newPayCourseCount(Long courseId) {
        String key = RedisKey.ll_new_pay_course_key + getDateStr();
        add(key ,   courseId);
    }

    /**
     * 新增平台开课数
     * @param courseId
     */
    @Override
    public void newPlatformCourseCount(Long courseId) {
        String key = RedisKey.ll_new_platform_course_key + getDateStr();
        add(key ,   courseId);
    }


}
