package com.longlian.mq.service;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import com.longlian.model.CourseImg;
import com.longlian.model.CourseWare;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pangchao on 2017/2/12.
 */
public interface CourseService {

    public Course getCourse(Long courseId);

    public Course getRelayCourse(Long courseId);

    public Course getCourseFromRedis(Long courseId);



    void updateVisitCount( long courseId ,long count);

    void updateRelayVisitCount( long courseId ,long count);

    public List<Course> getCourseBySeriesId(Long courseId);

    List<Course> findSeriesCourseBySeriesId(long id);

    /**
     * 更新系列课

     */
    public void updateCourse(Course course);

    void updateCourseVerticalImage(Long courseId, String url);

    List<Course> getAllNoEndCourseByTeahcerId(long teacherId);

    void updateClassImg(String url , long imgId);


    Course getCourseByVideoAddress(String md5);

    Set<Course> getStartCourse();

    Set<Course> getAllStartCourse();


    List<Course> findAllCourse(int pageSize , int offset);

    Boolean isHaveWare(long courseId);

    void updateCourseSort(long courseId , BigDecimal sort);

    List<CourseImg> getCourseImgList(long courseId);

}
