package com.longlian.mq.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/1.
 */
public interface CourseManagerMapper {

    List<Map> findAllManagerByTeacherId(long teacherId);
}
