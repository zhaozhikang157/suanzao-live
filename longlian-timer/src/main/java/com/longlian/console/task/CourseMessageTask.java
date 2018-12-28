package com.longlian.console.task;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.SendMsgService;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 课程开课程消息提醒
 * Created by pangchao on 2017/2/25.
 */
@Component("courseMessageTask")
public class CourseMessageTask extends AbstractShardingTask{
    private static Logger log = LoggerFactory.getLogger(CourseMessageTask.class);

    @Value("${website}")
    private String website;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;
    SendMsgService sendMsgService;
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    @Override
    public String getTaskName() {
        return "课程开课程消息提醒";
    }

    @Override
    public void doExecute() {
        try {
            sendCourseMassage();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("课程开课程消息提醒异常："+e.getMessage());
        }
    }

    //@Scheduled(cron = "0 0/5 * * * ?")
    public void sendCourseMassage() throws Exception {

        Date time1 = DateUtil.getDayHourAfter(new Date(), 24);
        Date time11 = DateUtil.getDayHourAfter(new Date(), 25);
        String hour = DateUtil.format(time1, "mm");
        Date time2 = DateUtil.getDayHourAfter(new Date(), 2);
        Date time21 = DateUtil.getDayHourAfter(new Date(), 3);
        fixedThreadPool.execute(new Runnable() {
            public void run() {
                try {
                    DataGridPage page = new DataGridPage();
                    int i = 1;
                    do {
                        List<CourseDto> teacherList = courseService.getCourseMessageTask(page);
                        if (teacherList.size() > 0) {
                            for (CourseDto c : teacherList) {
                                String cact = website + "/weixin/courseInfo?id="+c.getId();
                                Date startTime = c.getStartTime();
                                if (time1.getTime() < startTime.getTime() && time11.getTime() > startTime.getTime()) {
                                    if (!redisUtil.exists(RedisKey.ll_live_teacher_course_remind + "_24h_" + c.getId())) {
                                        redisUtil.setex(RedisKey.ll_live_teacher_course_remind + "_24h_" + c.getId(), 3600, "1");
                                        sendMsgService.sendMsg(c.getAppId(), MsgType.NEW_COURSE_TEACHER.getType(), c.getId(),
                                                MsgConst.replace(MsgConst.TEACHER_COURSE_COMMENT_REMIND, c.getLiveTopic(), "明天" + hour + "点"), cact);
                                    }
                                }
                                if (time21.getTime() > startTime.getTime() && time2.getTime() < startTime.getTime()) {
                                    if (!redisUtil.exists(RedisKey.ll_live_teacher_course_remind + "_2h_" + c.getId())) {
                                        redisUtil.setex(RedisKey.ll_live_teacher_course_remind + "_2h_" + c.getId(), 3600, "1");
                                        sendMsgService.sendMsg(c.getAppId(), MsgType.NEW_COURSE_TEACHER.getType(), c.getId(),
                                                MsgConst.replace(MsgConst.TEACHER_COURSE_COMMENT_REMIND, c.getLiveTopic(), "2小时后"), cact);
                                    }
                                }
                                if (startTime.getTime() - new Date().getTime() < 300000) {
                                    if (!redisUtil.exists(RedisKey.ll_live_teacher_course_remind + "_5m_" + c.getId())) {
                                        redisUtil.setex(RedisKey.ll_live_teacher_course_remind + "_5m_" + c.getId(), 3600, "1");
                                        sendMsgService.sendMsg(c.getAppId(), MsgType.NEW_COURSE_TEACHER.getType(), c.getId(),
                                                MsgConst.replace(MsgConst.TEACHER_COURSE_COMMENT_REMIND, c.getLiveTopic(), "1分钟后"), cact);
                                    }
                                }

                            }
                        }
                        i++;
                        page.setCurrentPage(i);
                    } while (i <= page.getTotalPage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fixedThreadPool.execute(new Runnable() {
            public void run() {
                try {
                    DataGridPage page = new DataGridPage();
                    int i = 1;
                    do {
                        List<Map> studentList = joinCourseRecordService.getCourseMessageTask(page);
                        if (studentList.size() > 0) {
                            for (Map map : studentList) {
                                String cact = website + "/weixin/liveRoom?id="+map.get("roomId");
                                Date startTime = (Date) map.get("startTime");
                                long appId = Long.parseLong(map.get("appId").toString());
                                long courseId = Long.parseLong(map.get("courseId").toString());
                                String liveTopic = map.get("liveTopic").toString();
                                String name = map.get("name").toString();
                                if (time21.getTime() > startTime.getTime() && time2.getTime() < startTime.getTime()) {
                                    if (!redisUtil.exists(RedisKey.ll_live_student_course_remind + "_2h_" + courseId)) {
                                        redisUtil.setex(RedisKey.ll_live_student_course_remind + "_2h_" + courseId, 3600, "1");
                                        sendMsgService.sendMsg(appId, MsgType.NEW_COURSE_STUDENT.getType(), courseId,
                                                MsgConst.replace(MsgConst.STUDENT_COURSE_COMMENT_REMIND,name, liveTopic, "2小时后"), cact);
                                    }
                                }
                                if (startTime.getTime() - new Date().getTime() < 300000) {
                                    if (!redisUtil.exists(RedisKey.ll_live_student_course_remind + "_5m_" + courseId)) {
                                        redisUtil.setex(RedisKey.ll_live_student_course_remind + "_5m_" + courseId, 3600, "1");
                                        sendMsgService.sendMsg(appId, MsgType.NEW_COURSE_STUDENT.getType(), courseId,
                                                MsgConst.replace(MsgConst.STUDENT_COURSE_COMMENT_REMIND, name,liveTopic, "1分钟后"), cact);
                                    }
                                }

                            }
                        }
                        i++;
                        page.setCurrentPage(i);
                    } while (i <= page.getTotalPage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
