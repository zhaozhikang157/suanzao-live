package com.longlian.mq.service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/1.
 */
public interface CourseManagerService {

    List<Map> findManagereByTeacherId(long teacherId);
}
