package com.longlian.console.service;

import com.longlian.model.CourseType;

import java.util.List;

/**
 * Created by pangchao on 2017/2/13.
 */
public interface CourseTypeService {
    List<CourseType> getList(CourseType courseType) ;

    CourseType findById(long id);

    long doSaveAndUpdate(CourseType courseType)throws Exception ;

    void toOrder(String ids) throws Exception ;

    void deleteById(long id) throws Exception ;

    List<CourseType> getCourseType4Redis();

    List<CourseType> buildCourseTypeTree(List<CourseType> newList, List<CourseType> oldList);
}
