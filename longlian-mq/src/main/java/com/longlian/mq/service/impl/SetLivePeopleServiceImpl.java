package com.longlian.mq.service.impl;

import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.VirtualUserService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by admin on 2017/12/1.
 * <p>
 * 给直播室添加虚拟用户人数
 */
public class SetLivePeopleServiceImpl implements Runnable {
    private static Logger log = LoggerFactory.getLogger(SetLivePeopleServiceImpl.class);

    private Course course;
    private AppUserCommonService appUserCommonService;
    private JoinCourseRecordService joinCourseRecordService;
    private VirtualUserService virtualUserService;
    private CourseService courseService;
    private long vrCount;

    public SetLivePeopleServiceImpl(Course course,
                                    AppUserCommonService appUserCommonService,
                                    JoinCourseRecordService joinCourseRecordService,
                                    VirtualUserService virtualUserService,
                                    CourseService courseService,long vrCount) {
        this.course = course;
        this.appUserCommonService = appUserCommonService;
        this.joinCourseRecordService = joinCourseRecordService;
        this.virtualUserService = virtualUserService;
        this.courseService = courseService;
        this.vrCount = vrCount;
    }


    @Override
    public void run() {
        long count = (long)( (Math.random() + 2) * 500 ); // 1000 - 1500
        if(count > vrCount){    //如果大于总的虚拟人数
            return;
        }
        do {
            try {
                Boolean bo = getVrUse(course);
                if (bo) {
                    int max=30;
                    int min=5;
                    Random random = new Random();
                    int s = random.nextInt(max)%(max-min+1) + min;
                    //int number =  (int) ( (1 + Math.random()) * 2); // 1 -- 4
                    Thread.sleep(s * 1000); //睡眠10-30秒
                    count--;
                } else {
                    //结束线程
                    return;
                }
            } catch (InterruptedException e) {
                log.info("线程执行添加虚拟用户信息失败:" + e);
                return;
            }
        } while (count > 0);
        //结束线程
        return;
    }

    /**
     * 去虚拟用户信息,然后放入课程中
     *
     * @param course
     */
    public Boolean getVrUse(Course course) {
        Boolean b = true;
        //随机取1个虚拟用户放入课程中
        String[] ids = appUserCommonService.getRandomCountUsers(1, 1);
        if(ids.length < 1){
            return false;
        }
        //判断该用户是否已经在该课程中
        Boolean bo = joinCourseRecordService.isJoinCourse(Long.valueOf(ids[0]), course);
        if (!bo) {
            //添加到课程中
            try {
                Course c = courseService.getCourseFromRedis(course.getId());
                if (c.getEndTime() != null) { //该课程已经结束
                    return false;
                }
                if ("1".equals(c.getIsDelete())) { //该课程已被下线
                    return false;
                }
                virtualUserService.addVirtualUser(course, ids);
            } catch (Exception e) {
                log.info("课程=" + course.getId() + "存放虚拟用户信息失败 : " + e);
                return false;
            }
        } else {
            //该虚拟用户已经存在,则重新执行该方法
            getVrUse(course);
        }
        return true;
    }

    public static void main(String[] args) {
        for (int i = 0 ;i < 20;i++) {
            int max=30;
            int min=10;
            Random random = new Random();
            int s = random.nextInt(max)%(max-min+1) + min;
            System.out.println(s);
        }

    }

}