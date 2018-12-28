package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CourseCommentMapper {
    List<Map> getCourseCommentList(long courseId);
    List<Map> getListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);
    int  deleteById(long id);

}
