package com.longlian.mq.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by admin on 2017/8/1.
 */
public interface CourseManagerRealMapper {

    void insertManagerReal(@Param("userId")String userId , @Param("courseId")long courseId);

    void delManagerRealById(@Param("courseId") Long courseId , @Param("userId")Long userId);

    Set<String> findAllManagerRealByCourseId(Long courseId);


}
