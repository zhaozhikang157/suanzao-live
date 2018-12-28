package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface CourseCommonMapper {

    Course getCourse(@Param("id") long id);


    void updateEndTime(@Param("id") Long courseId);

}
