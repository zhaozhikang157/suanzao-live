package com.longlian.console.timer;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.CourseService;

import com.longlian.console.util.SystemConst;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 * 开课十分钟之前,发送通知
 */
@Component("coursePreRemindTimer")
public class CoursePreRemindTimer extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(CoursePreRemindTimer.class);

    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    AppUserService appUserService;

    /**
     * 任务名称
     * @return
     */
    @Autowired
    public String getTaskName() {
        return "开课十分钟之前,发送通知";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            job();
        } catch (Exception e) {
            log.error("开课十分钟之前,发送通知 异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    public void job() throws  Exception {
        List<Course> list = courseService.getPreRemindByHourList();
        if(list.size()>0){
            for(Course course : list){
                long appId = course.getAppId();
                //AppUser appUser= appUserService.getAppUserById(appId);
                //System.out.print("appId================================================= " +appId);
                if(!redisUtil.exists(RedisKey.ll_live_pre_teacher_course_remind+course.getId())){
                    AppUser appUser= appUserService.getAppUserById(appId);
                    wechatOfficialService.getFollowUserSendWechatTemplateMessageByCoursePreRemind(course,appUser, SystemConst.initDepartmentOfOperation());
                    redisUtil.setex(RedisKey.ll_live_pre_teacher_course_remind + course.getId(),
                            RedisKey.ll_live_pre_teacher_course_valid_time, appId + "");
                }
            }
        }
    }

}
