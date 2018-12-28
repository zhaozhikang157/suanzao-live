package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;

import java.util.List;

/**
 * Created by Administrator on 2018/7/11.
 */
public interface CourseRelayService {
    /**
     * 创建转播
     * @param appId
     * @param oriCourseId
     */
    ActResultDto createRelayCourse(long appId, String oriCourseId, String relayCharge, String relayScale);

    /**
     * 通过appid和原课程id查询
     * @param appId
     * @param oriCourseId
     * @return
     */
    CourseRelayDto queryByAppidAndOriCourseId(long appId,String oriCourseId);

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

    List<CourseRelayDto> getRelayCourse(CourseRelayDto courseRelayDto) throws Exception;

    /**
     * 原课是否有人转播
     * @param courseId 原课ID
     * @return true 有人转播 false 没有人转播
     */
    boolean hasRelayIncome(long courseId);

    /**
     * 原课对应的转播课是否有人购买
     * @param courseId
     * @return
     */
    boolean hasRelayCoursePayIncome(long courseId);

    /**
     * 根据原课ID删除对应的转播课
     * @param id
     */
    void deleteRelayCourseByOriCourse(long id);

    /**
     * 删除转播课
     * @param id
     */
    void deleteRelayCourseById(Long id);

    /**
     * 查看是否有人买过此转播课，并且原课程不是免费课
     * @param courseId
     * @return
     */
    boolean hasRelayCoursePayIncomeById(Long courseId);
    
    /**
     * 我的转播课下架
     * @param relayCourseId 转播课ID
     * @return
     */
    ActResultDto myRelayCourseDown(Long relayCourseId);
    
    
}
