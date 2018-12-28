package com.longlian.live.task;

import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseRelayService;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.OrdersService;
import com.longlian.model.Course;
import com.longlian.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 转播系列课下的单节课
 * 初始化类
 */
@Component
@Scope("prototype")
public class UpdateRelayCourse implements Callable{

    @Autowired
    CourseService courseService;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    OrdersService ordersService;

    private long courseId;  //系列但节课ID
    private long appId;     //转播人ID
    private long roomId;    //直播间id

    public UpdateRelayCourse(){}

    public UpdateRelayCourse(long appId, long courseId, long roomId){
        this.appId = appId;
        this.courseId = courseId;
        this.roomId = roomId;
    }

    @Override
    public Object call() {
        try {
            CourseRelayDto searchRelay = new CourseRelayDto();      //构建查询条件bean
            searchRelay.setOriCourseId(courseId);
            List<CourseRelayDto> list = courseRelayService.getRelayCourse(searchRelay);//获取课
            if(list != null && list.size() > 0){
                for(CourseRelayDto courseRelayDto : list){
                    if(courseRelayDto != null){
                        courseService.setRelayCourseDown(courseRelayDto);
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
