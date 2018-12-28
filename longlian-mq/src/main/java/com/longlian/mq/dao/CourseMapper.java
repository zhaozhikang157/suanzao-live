package com.longlian.mq.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseMapper {

    Course getCourse(@Param("id") long id);

    Course getRelayCourse(@Param("id") long id);

    public void addVisitCount(@Param("id") long id);

    List<Course> GetVideoAddressPage(@Param("page") DataGridPage page);

    List<Course> getCourseBySeriesId(@Param("seriesCourseId") Long courseId);


    void update(Course course);

    void updateVisitCount(@Param("id")  long courseId,@Param("visitCount")  long count);

    void updateCourseVerticalImage(@Param("id")  Long courseId, @Param("verticalCoverssAddress") String url);

    List<Course> getAllNoEndCourseByTeahcerId(long teacherId);

    List<Course> findSeriesCourseBySeriesId(long id);

    void updateClassImg(@Param("url")String url , @Param("courseId")long courseId);

    void delClassImg(long courseId);

    Course selectCourseMsgByChatRoomId(long chatRoomId);



    void updateCreateTime(@Param("courseId")  Long courseId,@Param("startTime") Date now);

    Course getCourseByVideoAddress(@Param("md5") String md5);

    Set<Course> getStartCourse();

    Set<Course> getAllStartCourse();

    void  closeSeries(long courseId);

    List<Course> findAllCourse(@Param("offset")int offset,@Param("pageSize")int pageSize);

    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param isSeriesCourse
     * @param
     * @return
     */
    List<Map> findCourseWeeklySelection( @Param("isSeriesCourse") String isSeriesCourse,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    void updateCourseSort(@Param("courseId")long courseId , @Param("sort")BigDecimal sort);

}
