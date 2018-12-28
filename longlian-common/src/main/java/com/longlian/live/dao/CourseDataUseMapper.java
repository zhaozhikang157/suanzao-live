package com.longlian.live.dao;

import com.longlian.model.CourseDataUse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/11/9.
 */
@Mapper
public interface CourseDataUseMapper {

    void insert(CourseDataUse courseDataUse);

    Set<String> findAllCourseId();

    void  updateUseSize(CourseDataUse courseDataUse);
}
