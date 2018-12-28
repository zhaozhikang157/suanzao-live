package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.model.Course;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 课程公共接口
 * Created by liuhan on 2017-03-06.
 */
public interface CourseBaseService {
    public String getConvert(Long appId);

    public void setLiveRoomInfo(Course course);

    public void setChatroomId(Course course, boolean originTask );

    public void setRelayChatrooId(CourseRelayDto course,long oriCourseId);

    public void setRelayChatroomId(CourseRelayDto course, boolean originTask );

    void updateIsInviteCode(Long courseId);

    public void updateVideoAddr(Course course);
    void updeteAddress(long courseId, String convertAddress);

    void updateStatus(long id, String s);

    void updateStatusToRelay(long id, String s);

    public Course getCourse(Long courseId);
    public Course getRelayCourse(Long courseId);

    public Course getCourseFromRedis(Long courseId);

    void closeSeries(long courseId);

    /**
     * 录播课自动审核（修改为通过状态）
     * @param id
     * @throws Exception
     */
    public void updatePassStatus(long id) throws Exception;

    List<Map> getAuditListPage(DatagridRequestModel requestModel, Map map);

    List<Map> getAuditListNoPassPage(DatagridRequestModel requestModel,Map map);

    void updateCreateTime(Long courseId, Date now);

    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param
     * @return
     */
    ActResultDto findCourseWeeklySelection();

    Course selectCourseMsgByChatRoomId(long chatRoomId);

    List<Course> findChildCourse(long seriesId);

    void updateIsConnection(long courseId,String isConnection);

    public List<Course> getCourses(Integer i);

    void deal(Long seriesId) throws Exception;

    public List getVideoConverts();
}
