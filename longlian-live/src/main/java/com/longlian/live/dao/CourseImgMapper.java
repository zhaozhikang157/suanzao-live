package com.longlian.live.dao;

import com.longlian.model.CourseImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/21.
 */
public interface CourseImgMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseImg record);

    CourseImg selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseImg record);

    int updateByPrimaryKey(CourseImg record);

    void insertList(@Param("list")List list,@Param("courseId")long courseId);

    List<CourseImg> getCourseImgList(long courseId);

    int deleteByCourseId(long courseId);
    
    void insertImgList(@Param("list")List list,@Param("courseId")long courseId);

    List<CourseImg> getRelayCourseImgList(long courseId);
}
