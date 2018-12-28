package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.model.Course;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-10-31.
 */
public interface UpdateAndCreateCourseService {

    ActResultDto createCourse(CourseDto course)  throws Exception;

    ActResultDto createSeriesCourse(CourseDto course)  throws Exception;

    void updateCourse(CourseDto course,HttpServletRequest request) throws Exception;

    void updateSeriesCourse(CourseDto course, HttpServletRequest request) throws Exception;

    void setClassImgWait2db(long courseId, List<Map<Long,String>> list);

    public void sendCourseMqMsg(Course course);
}
