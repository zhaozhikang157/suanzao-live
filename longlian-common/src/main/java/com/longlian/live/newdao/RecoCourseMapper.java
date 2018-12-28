package com.longlian.live.newdao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.RecoCourse;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@org.apache.ibatis.annotations.Mapper
public interface RecoCourseMapper extends Mapper<RecoCourse> {
    List<Map> getRecoCourseListPage(@Param(value = "page") DatagridRequestModel page, @Param(value="map") Map map);
    List<Map> getRecoCourses();
    RecoCourse getRecoCoursesById(Long id);
    void  updateRecoCoursesById(RecoCourse recoCourse);
    int isExistRecoCourse(Long courseId);
    void deleteById(@Param(value = "id")Long id);
}