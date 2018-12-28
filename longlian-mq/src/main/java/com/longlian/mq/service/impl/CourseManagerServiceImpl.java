package com.longlian.mq.service.impl;

import com.longlian.mq.dao.CourseManagerMapper;
import com.longlian.mq.service.CourseManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/1.
 */
@Service("courseManagerService")
public class CourseManagerServiceImpl implements CourseManagerService {
    private static Logger log = LoggerFactory.getLogger(CourseManagerServiceImpl.class);

    @Autowired
    CourseManagerMapper courseManagerMapper;

    @Override
    public List<Map> findManagereByTeacherId(long teacherId) {
        return courseManagerMapper.findAllManagerByTeacherId(teacherId);
    }
}
