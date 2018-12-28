package com.longlian.live.service;

import com.longlian.model.CourseDataUse;

import java.util.Set;

/**
 * Created by admin on 2017/11/9.
 */
public interface CourseDataUseService {

    void insert(CourseDataUse courseDataUse);

    Set<String> findAllCourseId();

    void updateUseSize(CourseDataUse courseDataUse);

    public void createOrUpdate(String redisKey, Set<String> list, String type , String date);
}
