package com.longlian.console.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/7.
 */
public interface VisitCourseRecordStatMapper {

    Map findCourseSource(long courseId);
    List<Map> queryFriendCircleCountByFromType(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
    List<Map> queryFriendCountByFromType(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
    List<Map> queryCountByFromType(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
    List<Map> findCourseCountByCourseId(@Param("map") Map map);
}
