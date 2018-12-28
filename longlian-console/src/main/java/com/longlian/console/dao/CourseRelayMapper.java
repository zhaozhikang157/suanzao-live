package com.longlian.console.dao;

import com.longlian.dto.CourseRelayDto;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2018/8/17.
 */
public interface CourseRelayMapper {
    /**
     * 修改转播课上架或者下架
     * @param id
     */
    void updateRelayCourseStatus(@Param("id")long id,@Param("status")int status);

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
    CourseRelayDto queryByAppidAndOriCourseId(@Param("appId")long appId, @Param("oriCourseId")long oriCourseId);

    /**
     * 将转播过这节课的状态修改
     * @param id
     * @param status
     */
    void updateStatusByOriCourseId(@Param("id")long id, @Param("status")int status);

    /**
     * 更改转播课删除状态
     * @param id
     * @param status
     */
    void updateDeleteStatus(@Param("id")long id, @Param("status")int status);

    /**
     * 根据原课id删除转播课
     * @param id
     */
    void updateDeleteStatusByOriCourseId(@Param("id")long id,@Param("status")int status);
}
