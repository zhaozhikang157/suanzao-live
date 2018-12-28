package com.longlian.live.task;

import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseRelayService;
import com.longlian.live.service.OrdersService;
import com.longlian.model.Course;
import com.longlian.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * 转播系列课下的单节课
 * 初始化类
 */
@Component
@Scope("prototype")
public class CreateRelayCourse implements Callable{

    @Value("${website}")
    private String website;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    OrdersService ordersService;

    private long courseId;  //系列但节课ID
    private long appId;     //转播人ID
    private long roomId;    //直播间id

    public CreateRelayCourse(){}

    public CreateRelayCourse(long appId,long courseId,long roomId){
        this.appId = appId;
        this.courseId = courseId;
        this.roomId = roomId;
    }

    @Override
    public Object call() {
        try{
            Course course = courseBaseService.getCourse(courseId);  //原系列单节课
            CourseRelayDto searchRelay = new CourseRelayDto();      //构建查询条件bean
            if(course != null){
                searchRelay.setOriCourseId(course.getSeriesCourseId());
                searchRelay.setIsSeriesCourse("1");
                List<CourseRelayDto> list = courseRelayService.getRelayCourse(searchRelay);//获取转播系列课
                if(list != null && list.size() > 0){
                    for(CourseRelayDto courseRelayDto : list){
                        if(courseRelayDto != null){
                            CourseRelayDto singleCourse = new CourseRelayDto();
                            singleCourse.setOriAppId(courseRelayDto.getOriAppId());
                            singleCourse.setOriCourseId(courseId);
                            singleCourse.setAppId(courseRelayDto.getAppId());
                            singleCourse.setIsSeriesCourse("0");
                            singleCourse.setSeriesCourseId(courseRelayDto.getId());
                            singleCourse.setRoomId(courseRelayDto.getRoomId());
                            if("1".equals(course.getLiveWay())){
                                singleCourse.setChatRoomId(course.getChatRoomId());
                            }
                            //转播课程的状态用原来的
                            singleCourse.setStatus(course.getStatus());
                            singleCourse.setIsDelete(course.getIsDelete());
                            courseRelayService.createCourseRelay(singleCourse);
                            CourseRelayDto search = new CourseRelayDto();
                            search.setOriCourseId(course.getId());
                            search.setAppId(singleCourse.getAppId());
                            search.setOriAppId(course.getAppId());
                            List ls = courseRelayService.getRelayCourse(search);
                            if(ls != null && ls.size() > 0){
                                singleCourse = (CourseRelayDto) ls.get(0);
                                //添加转播云信token  语音课用原来的
                                if(!"1".equals(course.getLiveWay())){
                                    courseBaseService.setRelayChatroomId(singleCourse, false);
                                }
                                //发送消息
                                //发送消息和自动关注
                                String cAct = website + "/weixin/courseInfo?id="+singleCourse.getId();
                                Orders orders = new Orders();
                                orders.setAppId(singleCourse.getAppId());
                                ordersService.sendRelayMsg(orders, course, cAct);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }
}
