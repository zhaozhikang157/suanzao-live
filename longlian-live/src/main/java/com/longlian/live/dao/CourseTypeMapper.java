package com.longlian.live.dao;

import com.longlian.model.CourseType;

import java.util.List;
import java.util.Map;

public interface CourseTypeMapper {

    int insert(CourseType courseType);

    int update(CourseType courseType);

    List<CourseType> getCourseType();


    /**
     *  WHA 获取所有可用课程类型
     * @return
     */
    List<Map<String,String>> getCourseTypes();

}
