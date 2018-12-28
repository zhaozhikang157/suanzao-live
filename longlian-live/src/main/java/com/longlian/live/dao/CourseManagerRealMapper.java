package com.longlian.live.dao;

import com.longlian.model.CourseManagerReal;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/7/11.
 */
public interface CourseManagerRealMapper {

    void insertManagerReal(@Param("userId")Long userId,@Param("courseId")Long courseId);

    Set<String> findAllManagerRealByCourseId(Long courseId);

    void delManagerRealById(@Param("courseId") Long courseId , @Param("userId")Long userId);

    int findManagerRealById(@Param("userId")Long userId , @Param("courseId")Long courseId);

}
