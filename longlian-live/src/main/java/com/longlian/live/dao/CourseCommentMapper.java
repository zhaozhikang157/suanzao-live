package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.CourseComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CourseCommentMapper {

    /*最近评论*/
    List<Map> getLastListByCourseId(long courseId);

    int insert(CourseComment courseComment);

    long getCourseCommentSum(long courseId);
    long getSeriesCourseCommentSum(@Param("seriesId") Long seriesId);
    
    List<Map> getCommentListByCourseIdPage(@Param("courseId") Long courseId, @Param("page") DataGridPage dg);

    long getCoursePeopleSum(long courseId);
    List<Map> getCommentListByServiesCourseIdPage(@Param("seriesId") Long seriesId, @Param("page") DataGridPage dg);
}
