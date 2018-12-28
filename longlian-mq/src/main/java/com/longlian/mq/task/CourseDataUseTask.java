
package com.longlian.mq.task;

import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseDataUseService;
import com.longlian.live.service.DataUseRecordService;
import com.longlian.live.service.DataUseService;
import com.longlian.model.Course;
import com.longlian.model.CourseDataUse;
import com.longlian.model.DataUseRecord;
import com.longlian.mq.util.LogDealUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by admin on 2017/11/9.
 */
@Component("CourseDataUseTask")
public class CourseDataUseTask {
    private static Logger log = LoggerFactory.getLogger(CourseDataUseTask.class);

    @Autowired
    CourseDataUseService courseDataUseService;
    @Autowired
    DataUseRecordService dataUseRecordService;

    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    RedisUtil redisUtil;

    @Scheduled(cron = "0 0 1 * * ?")
    public void doJob() throws Exception {
        Date date = LogDealUtil.getBeferDayDate();
        String dateStr = DateUtil.format(date, "yyyy-MM-dd");
        Set<String> list = courseDataUseService.findAllCourseId();
        String cdnLog = RedisKey.ll_cdn_log_course_id + dateStr;
        courseDataUseService.createOrUpdate(cdnLog, list, "1"  , dateStr);
        String ossLog = RedisKey.ll_oss_log_course_id + dateStr;
        courseDataUseService.createOrUpdate(ossLog, list, "2", dateStr);

        Map<String, String> map = redisUtil.hmgetAll(cdnLog);
        Map<String, String> ossMap = redisUtil.hmgetAll(ossLog);

        Map<String, DataUseRecord> res = new HashMap<>();
        Set<String> set = map.keySet();
        for (String str : set) {
            String size = map.get(str);
            try {
                res.put(str , this.getNew(str , size , "" , date));
            } catch (Exception ex) {
                log.error("转化成DataUseRecord对象出错:" ,ex );
            }
        }

         set = ossMap.keySet();
        for (String str : set) {
            String size = ossMap.get(str);
            try {

                DataUseRecord ur = res.get(str);
                if (ur == null) {
                    res.put(str , this.getNew(str , "" , size , date));
                } else {
                    ur.setReviewDataUse(Long.parseLong(size));
                }
            } catch (Exception ex) {
                log.error("转化成DataUseRecord对象出错:" ,ex );
            }
        }

        for (DataUseRecord dr : res.values()) {
            if (dr.getReviewDataUse() == null) dr.setReviewDataUse(0L);
            if (dr.getLiveDataUse() == null) dr.setLiveDataUse(0L);
            dr.setDataUse(dr.getLiveDataUse() + dr.getReviewDataUse());
            try {
                dataUseRecordService.save(dr);
            } catch (Exception ex) {
                log.error("保存DataUseRecord对象出错:" ,ex );
            }

        }
    }

    public DataUseRecord getNew(String courseId , String cdnSize , String ossSize , Date date) {
        DataUseRecord dr = new DataUseRecord();
        dr.setCourseId(Long.parseLong(courseId));
        dr.setCreateTime(new Date());
        Course course = courseBaseService.getCourseFromRedis(Long.parseLong(courseId));
        dr.setRoomId(course.getRoomId());
        if (!StringUtils.isEmpty(cdnSize)) {
            dr.setLiveDataUse(Long.parseLong(cdnSize));
        } else if (!StringUtils.isEmpty(ossSize)) {
            dr.setReviewDataUse(Long.parseLong(ossSize));
        }
        dr.setUseDate(date);
        return dr;
    }
}
