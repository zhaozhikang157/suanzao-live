package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.course.CoursePrivateCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/8.
 */
public interface CoursePrivateCardMapper {

    List<Map> getListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);

    Map getCoursePrivateCardById(@Param("id") long id);

    Map getCourseByCourseId(@Param("courseId") long courseId);

    long addCoursePrivateCard(@Param("coursePrivateCard") CoursePrivateCard coursePrivateCard);
    int updateCoursePrivateCard(@Param("coursePrivateCard") CoursePrivateCard coursePrivateCard);
    void deleteById(@Param("id") long id);
}
