package com.longlian.console.dao;

import com.longlian.model.CourseImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/3/21.
 */
public interface CourseImgMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseImg record);

    CourseImg selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseImg record);

    int updateByPrimaryKey(CourseImg record);

    void insertList(@Param("list") List list, @Param("courseId") long courseId);

    List<CourseImg> getCourseImgList(long courseId);

    int deleteByCourseId(long courseId);

}
