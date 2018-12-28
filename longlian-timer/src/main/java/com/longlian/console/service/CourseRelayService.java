package com.longlian.console.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/11.
 */
public interface CourseRelayService {

    /**
     * 通过appid和原课程id查询
     * @param appId
     * @param oriCourseId
     * @return
     */
    CourseRelayDto queryByAppidAndOriCourseId(long appId, String oriCourseId);

    /**
     * 判断是否已经添加过转播
     * @param appId
     * @param oriCourseId
     * @return
     */
    Boolean isTransmitted(long appId, String oriCourseId);

    /**
     * 查询最近的一条转播记录
     * @param appId
     * @return
     */
    CourseRelayDto queryByCreateTime(long appId);

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    CourseRelayDto queryById(long id);

    /**
     * 获取转播课
     * @param courseId
     * @return
     */
    CourseRelayDto getCourseRelayFromRedis(Long courseId);

    List<CourseRelayDto> queryByPlayingCourse(long id);

    /**
     * 给转播人新增记录
     * @param course
     */
    void insertSeriesSingleCourse(CourseDto course);

    /**
     * 创建转播课
     * @param courseRelayDto
     * @return
     */
    ActResultDto createCourseRelay(CourseRelayDto courseRelayDto);

    List<CourseRelayDto> getRelayCourse(CourseRelayDto courseRelayDto);

    /**
     * 获取系列单节课IDs
     * @return
     */
    List<Long> getCourseIdsBySeries(long id);

    /**
     * 获取转播系列单节课IDs
     * @return
     */
    Map<Long,Long> getRelayCourseIds(long id);

}