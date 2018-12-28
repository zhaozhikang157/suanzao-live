package com.longlian.live.dao;

import com.longlian.model.CourseBaseNum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface CourseBaseNumMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseBaseNum record);

    int insertSelective(CourseBaseNum record);

    CourseBaseNum selectByPrimaryKey(Long id);

    CourseBaseNum selectByCourse(@Param("courseId") Long courseId  ,@Param("type")  String type);

    int updateByPrimaryKeySelective(CourseBaseNum record);

    int updateByPrimaryKey(CourseBaseNum record);
}