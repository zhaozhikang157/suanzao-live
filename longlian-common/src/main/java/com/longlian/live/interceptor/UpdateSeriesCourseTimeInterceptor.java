package com.longlian.live.interceptor;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果是系列课里面的单节课，则发送更新系列课时间队列
 */
public class UpdateSeriesCourseTimeInterceptor implements MethodInterceptor{

    private static ThreadLocal<Long> globalInfo = new ThreadLocal<Long>();

    public static Long getSeriesId(){
        return globalInfo.get();
    }

    public static void setSeriesId(Long seriesId){
        globalInfo.set(seriesId);
    }

    public static void clear(){
        globalInfo.remove();
    }

    private static Logger logger = LoggerFactory.getLogger(UpdateSeriesCourseTimeInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object object = invocation.proceed();
//        if (invocation.getMethod().isAnnotationPresent(UpdateSeriesCourseTime.class)) {
             updateTime();
//        }
        return object;
    }

    public void updateTime() {
        Long seriesId = UpdateSeriesCourseTimeInterceptor.getSeriesId();
        if (seriesId != null && seriesId != 0) {
            Map m = new HashMap();
            m.put("seriesId" , seriesId);
            redisUtil.lpush(RedisKey.ll_series_course_update_time_wait2db , JsonUtil.toJson(m));
            UpdateSeriesCourseTimeInterceptor.clear();
        }
    }
}

