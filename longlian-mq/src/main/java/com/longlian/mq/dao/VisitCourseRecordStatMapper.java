package com.longlian.mq.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/7.
 */
public interface VisitCourseRecordStatMapper {

    Map findCourseSource(long courseId);
}
