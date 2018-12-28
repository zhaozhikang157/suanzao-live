package com.longlian.console.timer;

import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.RewardRecordService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 课程结束
 */
@Component("newTeachCourseRewardTimer")
public class NewTeachCourseRewardTimer extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(NewTeachCourseRewardTimer.class);

    @Autowired
    AccountService accountService;

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    CourseService  courseService;
    @Autowired
    RewardRecordService rewardRecordService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    /**
     * 需要计算(虚数)的课程ID，以都好分割
     * 励的时候是否需要计算(虚数)
     */
    private String needBaseNum;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 课程结束";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            //job();
        } catch (Exception e) {
            log.error("循环处理 课程结束异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    public void setNeedBaseNum(String needBaseNum) {
        this.needBaseNum = needBaseNum;
    }

    /*public void job() throws  Exception {
        String startTime = systemParaRedisUtil.getNewTeachCourseRewardRuleStartTime();  //奖励-开始时间
        String endTime = systemParaRedisUtil.getNewTeachCourseRewardRuleEndime();       //奖励-结束时间
        List<Long> notReward = rewardRecordService.findAlreadyCourseId();   //老师授课奖励记录中的课程ID
        List<Course> courseList = courseService.findDuringThisPeriodCourse(startTime, endTime, notReward);//找到这个时间段没有奖励记录的课程
        List<Course> newCourse = new ArrayList<Course>();
        Map<Long,Long> mapCourse = new HashMap<Long,Long>(); // appId -> courseId
        Map<Long,Long> mapCount = new HashMap<Long,Long>();  // courseId -> payMenCount
        for(Course course : courseList) {
            String teach_course_reward_men_count_isfree = systemParaRedisUtil.getTeachCourseRewardMenCountIsFree();
            int payMenCount = joinCourseRecordService.getPaySuccessRecordCount(course.getId(), teach_course_reward_men_count_isfree);
            mapCount.put(course.getId(), Long.valueOf(payMenCount));
            Long courseId = mapCourse.get(course.getAppId()); //根据老师的appId,获取mapCourse中是否存在couseId
            if (courseId == null) { //如果不存在,则存入map中
                mapCourse.put(course.getAppId(), course.getId());
            } else { //如果存在,则用courseId取出对应的支付人数
                Long mapPayMenCount = mapCount.get(courseId);
                if (mapPayMenCount < payMenCount) {   //如果map中的支付人数小于当前课程的支付人数,则替换掉之前的courseId ,反之.不动
                    mapCourse.put(course.getAppId(), course.getId());
                }
            }
        }
        //获取符合条件的courseId获取course信息
        Set<Long> mapCourseKey = mapCourse.keySet();
        for(Long appIdKey : mapCourseKey){   //获取所有的mapCourse中的appId
            Long courseId = mapCourse.get(appIdKey);
            for(Course course : courseList){
                if(courseId == course.getId()){
                    newCourse.add(course);
                    break;
                }
            }
        }
        if(newCourse!=null && newCourse.size()>0){
            Set<Long> courseKey = mapCount.keySet();    //所有的课程Id
            for(Course course : newCourse){
                for(Long courseId : courseKey){
                    if(courseId == course.getId()){
                        accountService.newTeachCourseReward(course, Utility.findById(needBaseNum, String.valueOf(course.getId())), mapCount.get(courseId));
                        break;
                    }
                }
            }
        }
    }*/
}
