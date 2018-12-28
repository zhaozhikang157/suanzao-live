package com.longlian.mq.dao;

import com.longlian.model.CourseWare;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CourseWareMapper {

    int insert(CourseWare courseWare);

    int update(CourseWare courseWare);

    void insertList(@Param("list") List list, @Param("courseId") long courseId);

    List<CourseWare> getCourseWare(@Param("id") long id);

    int delete(long id);

    List<Map> getCourseWarebyId(@Param("id") long id);

    int deleteByCourseId(long courseId);

}