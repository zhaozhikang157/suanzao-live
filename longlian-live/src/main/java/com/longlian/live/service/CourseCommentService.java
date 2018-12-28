package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.CourseComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/17.
 */
public interface CourseCommentService {
    List<Map> getLastListByCourseId(long courseId);
    void insert(CourseComment courseComment )throws  Exception;
    List<Map> getCommentListByCourseId(Long courseId, Integer pageNum, Integer pageSize);
    long getCommentSumByCourseId(Long courseId);
    List<Map> getCommentListByServiesCourseIdPage(Long seriesId, Integer offset, Integer pageSize);

    long getSeriesCourseCommentSum( Long seriesId);
}
