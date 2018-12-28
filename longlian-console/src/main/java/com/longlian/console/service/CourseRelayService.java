package com.longlian.console.service;

import com.longlian.dto.CourseRelayDto;

/**
 * Created by Administrator on 2018/8/17.
 */
public interface CourseRelayService {
    /**
     * 修改转播课上架或者下架
     * @param id
     */
    void updateRelayCourseStatus(long id,int status);

    /**
     * 根据id查询转播信息
     * @param id
     * @return
     */
    CourseRelayDto selectById(long id);

    /**
     * 查询原课信息和转播人信息
     * @param appId
     * @param oriCourseId
     * @return
     */
    CourseRelayDto queryByAppidAndOriCourseId(long appId, long oriCourseId);

    /**
     * 将转播过这节课的状态修改
     * @param id
     * @param status
     */
    void updateStatusByOriCourseId(long id, int status);

    /**
     * 更改转播课删除状态
     * @param id
     * @param i
     */
    void updateDeleteStatus(long id, int i);

    /**
     * 根据原课id删除转播课
     * @param id
     */
    void updateDeleteStatusByOriCourseId(long id,int status);
}
