package com.longlian.console.dao;

import com.longlian.model.CourseType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/2/14.
 */
public interface CourseTypeMapper {

    long update(CourseType courseType);
    long insert(CourseType courseType);
    List<CourseType> getList(@Param("courseType")CourseType courseType);
    CourseType findById(long id);
    int toOrder(List list) ;
    int deleteById(long id);
    List<CourseType> getCourseType();
}
