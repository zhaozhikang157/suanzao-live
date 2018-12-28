package com.longlian.third.service.impl;

import com.longlian.model.Course;
import com.longlian.third.dao.CourseMapper;
import com.longlian.third.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/3/24.
 */
@Service("courseService")
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseMapper courseMapper;
    @Override
    public Course getById(long id) {
        return courseMapper.getById(id);
    }
}
