package com.longlian.mq.service;

import java.util.Set;

/**
 * Created by admin on 2017/8/1.
 */
public interface CourseManagerRealService {

    void insertManagerReal(String userId , long courseId);

    void delManagerRealById(Long courseId , Long userId);

    Set<String> findAllManagerRealByCourseId(Long courseId);
}
