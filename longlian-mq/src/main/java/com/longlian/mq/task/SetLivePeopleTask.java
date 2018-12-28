package com.longlian.mq.task;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.VirtualUserService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import com.longlian.mq.service.impl.SetLivePeopleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by admin on 2017/12/4.
 */
@Component("SetLivePeopleTask")
public class SetLivePeopleTask {
    private static Logger log = LoggerFactory.getLogger(SetLivePeopleTask.class);

    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    VirtualUserService virtualUserService;
    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void doJob() throws Exception {
        Set<Course> ss = courseService.getAllStartCourse();
        //取所有的虚拟用户信息 -- 放入内存中
        appUserCommonService.loadVirtualUser2Redis();
        long vrCount = redisUtil.llen(RedisKey.ll_all_virtual_userid);
        if (vrCount < 1) {
            return;
        }
        for (Course course : ss) {
            //语音课排除
            if("1".equals(course.getLiveWay())) {
                continue;
            }

            BigDecimal bd = course.getChargeAmt();

            if ("1".equals(course.getLiveWay())) {
                continue;
            }

            //系列课的价格来判断
            if (course.getSeriesCourseId() > 0) {
                Course seriesCourse = courseService.getCourse(course.getSeriesCourseId());
                bd = seriesCourse.getChargeAmt();
            }
            //价格为空，且等于零才添加虚拟用户
            if (bd == null || bd.compareTo(new BigDecimal(0)) == 0) {
                Boolean bo = redisUtil.sismember(RedisKey.ll_set_course_vr_user, String.valueOf(course.getId()));
                if (!bo) {
                    //启线程,开始为课程添加人数
                    redisUtil.sadd(RedisKey.ll_set_course_vr_user, String.valueOf(course.getId()));
                    SetLivePeopleServiceImpl impl = new SetLivePeopleServiceImpl(course, appUserCommonService,
                            joinCourseRecordService, virtualUserService, courseService, vrCount);
                    Thread thread = new Thread(impl);
                    thread.start();
                }
            }
        }
        redisUtil.expire(RedisKey.ll_set_course_vr_user, 60 * 60 * 24 * 2);
    }

}
