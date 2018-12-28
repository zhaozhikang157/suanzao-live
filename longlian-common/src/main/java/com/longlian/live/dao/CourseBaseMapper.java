package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.CourseRelayDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Mapper
public interface CourseBaseMapper {
    int setLiveRoomInfo(Course course);

    int setRelayLiveRoomInfo(CourseRelayDto course);

    void updateIsInviteCode(@Param("id") Long courseId);

    void updateAddress(@Param("id") Long courseId, @Param("convertAddress") String convertAddress);

    void updateStatus(@Param("id") Long id, @Param("status")  String status);

    void updateStatusToRelay(@Param("id") Long id, @Param("status")  String status);

    Course getCourse(Long courseId);
    Course getRelayCourse(Long courseId);

    List<Course> getCourses(String startTime);

    void  closeSeries(long courseId);

    /**
     *  录播审核操作列表
     * @param requestModel
     * @param map
     * @return
     */
    List<Map> getAuditListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);


    /**
     *  录播审核未通过列表
     * @param requestModel
     * @param map
     * @return
     */
    List<Map> getAuditListNoPassPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);


    void updateCreateTime(@Param("courseId")  Long courseId,@Param("startTime") Date now);


    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param isSeriesCourse
     * @param
     * @return
     */
    List<Map> findCourseWeeklySelection( @Param("isSeriesCourse") String isSeriesCourse,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    Course selectCourseMsgByChatRoomId(long chatRoomid);

    List<Course> findChildCourse(long seriesId);

    void updateIsConnection(@Param("courseId")long courseId , @Param("isConnection")String isConnection);

    void updateVideoAddr(Course course);

    List<Course> getCourseBySeriesId(@Param("seriesCourseId") Long courseId);

    void update(Course course);

    /**
     * 获取直播流配置
     * @return
     */
    List getVideoConverts();

    /**
     * 获取系列单节课
     */
    List<CourseRelayDto> getCourseRelayBySeriesId(Long id);
}
