package com.longlian.mq.task;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.ChatRoomMsgMapper;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.UserFollowService;
import com.longlian.model.Course;
import com.longlian.model.CourseWare;
import com.longlian.mq.dao.CourseWareMapper;
import com.longlian.mq.dao.VisitCourseRecordStatMapper;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/11.
 * 课程排序
 */
@Component("CourseSortTask")
public class CourseSortTask {
    private static Logger log = LoggerFactory.getLogger(CourseSortTask.class);

    @Autowired
    CourseService courseService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseWareMapper courseWareMapper;
    @Autowired
    ChatRoomMsgMapper chatRoomMsgMapper;
    @Autowired
    VisitCourseRecordStatMapper visitCourseRecordStatMapper;


    /**
     * 想学人数: 0~100 101~200 201~400 401~600 601~1000 1001~1500 1501~2000 2001~5000 5000+
     *实际购买: 0~10 11~50 51~100 101~200 200+
     *课程时长: 1~30min  31min~60min  61min~90min  90min+
     *课程简介: 0 1~3  4~6  7+
     *互动消息: 0 1~10  11~30  30+
     *转发量: 0 1~10 11~20 21~30 30+
     *关注老师人数: 0 1~50  51~100  101~500  501~1000  1000+
     * 老师建课数: 0 1~3  4~10  11~20 21~30 30+
     *是否是系列课:  否0  是3
     * 是否是收费课: 否0  是4
     *开课时间: 7 7~30 30~60
     *
     */

    private final long a = 0;
    private final long b = 1;
    private final long c = 3;
    private final long d = 5;
    private final long e = 10;
    private final long f = 100;
    private final long g = 1000;
    private final long h = 3000;
    private final long i = 5000;
    private final long j = 10000;
    private final long k = 100000;
    private final long l = 30;
    private final long m = 60;
    private final long n = 90;


    private final int pageSize = 300;

    //@Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 2 * * ?")
    public void doJob() throws Exception {
        log.info("课程排序任务start");
        int size = 0;
        int offset = 0;
        do {
            List<Course> list = courseService.findAllCourse(offset,pageSize);
            size = list.size();
            offset  = offset + size ;
            for (Course course : list) {
                try {
                if(!StringUtils.isEmpty(course.getLiveTopic())){
                    String topic = course.getLiveTopic().toLowerCase();
                    if((course.getLiveTopic()).contains("测试") || topic.contains("test")){
                        continue;
                    }
                }
                float order = 0.00f;
                //查询该直播间所有买课的学生数
                if (StringUtils.isEmpty(redisUtil.get(RedisKey.ll_room_study + course.getRoomId()))) {
                    long studyAllCount = joinCourseRecordService.getCountByRoomId(course.getRoomId());
                    redisUtil.set(RedisKey.ll_room_study + course.getRoomId(), String.valueOf(studyAllCount));
                }
                //统计直播间的课程数
                redisUtil.incr(RedisKey.ll_room_course_num + course.getRoomId());
                //课程时长--
                if (course.getEndTime() != null) {
                    long min = DateUtil.getDatePoor3( course.getStartTime(),course.getEndTime());
                    order = order + courseDurationCount(min);
                }
                //系列课--
                if (course.getSeriesCourseId() > 0 && course.getIsSeriesCourse().equals("1")) {
                    Boolean boo = true;
                    order = order + isSeriesCount(boo);
                }
                //是否收费课--
                 BigDecimal  chargeAmt=(null==course.getChargeAmt()?new BigDecimal(0):course.getChargeAmt());
                if (chargeAmt.compareTo(new BigDecimal(0.00)) == 1) {
                    Boolean boo = true;
                    order = order + isFreeCount(boo);
                }
                //开课时间--
                if (course.getStartTime() != null) {
                    int min = DateUtil.getIntervalOfDays(new Date(), course.getStartTime());
                    order = order + courseStartCount(min);
                }
                //转发量--
                Map courseSourceMap = visitCourseRecordStatMapper.findCourseSource(course.getId());
                if(null != courseSourceMap && courseSourceMap.containsKey("invitationCard")){
                    BigDecimal bigDecimal = (BigDecimal) courseSourceMap.get("invitationCard");
                    if(null != bigDecimal){
                        order = order + amountForwardingCount(bigDecimal.intValue());
                    }
                }
                //消息互动--
                int msgCount = chatRoomMsgMapper.getMsgCount(course.getId());
                order = order + interactiveMessageCount(msgCount);
                //想学人数--
                Long visitCount = course.getVisitCount() == null ? 0l: course.getVisitCount();
                order = order + visitCount(visitCount);
                //学习人数--
                long studyCount = course.getStudyCount() == null ? 0l: course.getStudyCount();
                order = order + studyCount(studyCount);
                //关注数--
                long followCount = userFollowService.getCountByRoomId(course.getRoomId());
                order = order + concernTeacherCount(followCount);
                //学生
                long studyAllCount = Long.parseLong(redisUtil.get(RedisKey.ll_room_study + course.getRoomId()));
                //课程数--
                long courseNum = Long.parseLong(redisUtil.get(RedisKey.ll_room_course_num + course.getRoomId()));
                order = order + teacherBuidingCount(courseNum);
                //课程简介--
                List<CourseWare> list1 = courseWareMapper.getCourseWare(course.getId());
                order = order + courseIntroductionCount(list1.size());
                //评分
                BigDecimal dd = new BigDecimal(order).setScale(0,BigDecimal.ROUND_HALF_UP);
                courseService.updateCourseSort(course.getId(),dd);
              }catch (Exception e){
                log.error("课程排序任务异常"+ JsonUtil.toJson(course),e);
            }
            }
        }while (size == pageSize);
        redisUtil.del(RedisKey.ll_room_study + "*"); //直播间的学生数
        redisUtil.del(RedisKey.ll_room_course_num + "*"); //直播间的课程数
        redisUtil.del(RedisKey.ll_set_series_course ); //所有的系列课
        log.info("课程排序任务end");
    }

    public Boolean isWare(long courseId, Boolean bo) {
        if (bo) { //系列课
            List<Course> courseList = courseService.findSeriesCourseBySeriesId(courseId);
            for (Course course : courseList) {
                Boolean b = courseService.isHaveWare(course.getId());
                if (b) {
                    return b;
                } else {
                    continue;
                }
            }
            return false;
        } else {
            return courseService.isHaveWare(courseId);
        }
    }


    public int addNum(long count, long aa, long bb, long cc, long dd, long ff) {
        int order = 0;
        if (aa <= count && bb > count) {
            order = 1;
        } else if (bb <= count && cc > count) {
            order = 2;
        } else if (cc <= count && dd > count) {
            order = 3;
        } else if (dd <= count && ff > count) {
            order = 4;
        } else if (ff > 1 && ff <= count) {
            order = 5;
        }
        return order;
    }
    //想学人数评分 0~100 101~200 201~400 401~600 601~1000 1001~1500 1501~2000 2001~5000 5000+
    public float visitCount(long count){
        float order = 0.00f;
        if(count >= 0 && count <= 100){
            order = (float)(Math.round((1.00*count/100.00)*100))/100;
        }else if (count >= 101 && count <= 200){
            order = (float)(Math.round((2.00*count/200.00)*100))/100;
        }else if (count >= 201 && count <= 400){
            order = (float)(Math.round((3.00*count/400.00)*100))/100;
        }else if (count >= 401 && count <= 600){
            order = (float)(Math.round((4.00*count/600.00)*100))/100;
        }else if (count >= 601 && count <= 1000){
            order = (float)(Math.round((5.00*count/1000.00)*100))/100;
        }else if (count >= 1001 && count <= 1500){
            order = (float)(Math.round((6.00*count/1500.00)*100))/100;
        }else if (count >= 1501 && count <= 2000){
            order = (float)(Math.round((7.00*count/2000.00)*100))/100;
        }else if (count >= 2001 && count <= 5000){
            order = (float)(Math.round((8.00*count/5000.00)*100))/100;
        }else if (count > 5000){
            order = 9.00f;
        }
        return order;
    }
    //实际购买评分 0~10 11~50 51~100 101~200 200+
    public float studyCount(long count){
        float order = 0.00f;
        if(count >= 0 && count <= 10){
            order = (float)(Math.round((1.00*count/10.00)*100))/100;
        }else if (count >= 11 && count <= 50){
            order = (float)(Math.round((3.00*count/50.00)*100))/100;
        }else if (count >= 51 && count <= 100){
            order = (float)(Math.round((5.00*count/100.00)*100))/100;
        }else if (count >= 101 && count <= 200){
            order = (float)(Math.round((7.00*count/200.00)*100))/100;
        }else if (count > 200){
            order = 9.00f;
        }
        return order;
    }
    //课程时长评分 1~30min  31min~60min  61min~90min  90min+
    public float courseDurationCount(long count){
        float order = 0.00f;
        if(count >= 1 && count <= 30){
            order = (float)(Math.round((3.00*count/30.00)*100))/100;
        }else if (count >= 31 && count <= 60){
            order = (float)(Math.round((6.00*count/60.00)*100))/100;
        }else if (count >= 61 && count <= 90){
            order = (float)(Math.round((9.00*count/90.00)*100))/100;
        }else if (count > 90){
            order = 12.00f;
        }
        return order;
    }
    //课程简介评分 0 1~3  4~6  7+
    public float courseIntroductionCount(long count){
        float order = 0.00f;
        if(count <= 0){
            order =0.00f;
        }else if (count >= 1 && count <= 3){
            order = 3.00f;
        }else if (count >= 4 && count <= 6){
            order = 6.00f;
        }else if (count >= 7){
            order = 9.00f;
        }
        return order;
    }
    //互动消息评分: 0 1~10  11~30  30+
    public float interactiveMessageCount(long count){
        float order = 0.00f;
        if(count <= 0){
            order = 0.00f;
        }else if (count >= 1 && count <= 10){
            order = (float)(Math.round((3.00*count/10.00)*100))/100;
        }else if (count >= 11 && count <= 30){
            order = (float)(Math.round((6.00*count/30.00)*100))/100;
        }else if (count > 30){
            order = 9.00f;
        }
        return order;
    }
    //转发量评分 0 1~10 11~20 21~30 30+
    public float amountForwardingCount(long count){
        float order = 0.00f;
        if(count <= 0){
            order =0.00f;
        }else if (count >= 1 && count <= 10){
            order = (float)(Math.round((3.00*count/10.00)*100))/100;
        }else if (count >= 11 && count <= 20){
            order = (float)(Math.round((6.00*count/20.00)*100))/100;
        }else if (count >= 21 && count <= 30){
            order = (float)(Math.round((9.00*count/30.00)*100))/100;
        }else if (count > 30){
            order = 12.00f;
        }
        return order;
    }
    //关注老师人数评分 0 1~50  51~100  101~500  501~1000  1000+
    public float concernTeacherCount(long count){
        float order = 0;
        if(count <= 0){
            order =0;
        }else if(count >= 1 && count <= 50){
            order = (float)(Math.round((3.00*count/50.00)*100))/100;
        }else if (count >= 51 && count <= 100){
            order = (float)(Math.round((6.00*count/100.00)*100))/100;
        }else if (count >= 101 && count <= 500){
            order = (float)(Math.round((9.00*count/500.00)*100))/100;
        }else if (count >= 501 && count <= 1000){
            order = (float)(Math.round((12.00*count/1000.00)*100))/100;
        }else if (count > 1000){
            order = 15.00f;
        }
        return order;
    }
    //老师建课数评分 0 1~3  4~10  11~20 21~30 30+
    public float teacherBuidingCount(long count){
        float order = 0.00f;
        if(count <= 0){
            order =0.00f;
        }else if(count >= 1 && count <= 3){
            order = (float)(Math.round((3.00*count/3.00)*100))/100;
        }else if (count >= 4 && count <= 10){
            order = (float)(Math.round((6.00*count/10.00)*100))/100;
        }else if (count >= 11 && count <= 20){
            order = (float)(Math.round((9.00*count/20.00)*100))/100;
        }else if (count >= 21 && count <= 30){
            order = (float)(Math.round((12.00*count/30.00)*100))/100;
        }else if (count > 30){
            order = 15.00f;
        }
        return order;
    }
    //是否是系列课评分 否0  是3
    public float isSeriesCount(boolean flag){
        float order = 0.00f;
        if(flag){
            order = 3.00f;
        }
        return order;
    }
    //是否是收费课评分 否0  是4
    public float isFreeCount(boolean flag){
        float order = 0.00f;
        if(flag){
            order = 4.00f;
        }
        return order;
    }
    //开课时间评分 7 7~30 30~60
    public float courseStartCount(long count){
        float order = 0.00f;
        if(count <= 7 && count > 0){
            order = 3.00f;
        }else if (count > 7 && count <= 30){
            order = 2.00f;
        }else if (count > 30){
            order = 1.00f;
        }
        return order;
    }
}
