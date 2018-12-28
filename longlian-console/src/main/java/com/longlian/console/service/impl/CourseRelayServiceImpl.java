package com.longlian.console.service.impl;

import com.longlian.console.dao.CourseRelayMapper;
import com.longlian.console.service.CourseRelayService;
import com.longlian.dto.CourseRelayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/8/17.
 */
@Service
public class CourseRelayServiceImpl implements CourseRelayService {

    @Autowired
    private CourseRelayMapper courseRelayMapper;

    /**
     * 修改转播课上架或者下架
     * @param id
     */
    @Override
    public void updateRelayCourseStatus(long id,int status) {
        courseRelayMapper.updateRelayCourseStatus(id, status);
    }

    /**
     * 根据id查询转播信息
     * @param id
     * @return
     */
    @Override
    public CourseRelayDto selectById(long id) {
        return courseRelayMapper.selectById(id);
    }

    /**
     * 查询原课信息和转播人信息
     * @param appId
     * @param oriCourseId
     * @return
     */
    @Override
    public CourseRelayDto queryByAppidAndOriCourseId(long appId, long oriCourseId) {
        return courseRelayMapper.queryByAppidAndOriCourseId(appId,oriCourseId);
    }

    /**
     * 将转播过这节课的状态修改
     * @param id
     * @param status
     */
    @Override
    public void updateStatusByOriCourseId(long id, int status) {
        courseRelayMapper.updateStatusByOriCourseId(id, status);
    }

    /**
     * 更改转播课删除状态
     * @param id
     * @param status
     */
    @Override
    public void updateDeleteStatus(long id, int status) {
        courseRelayMapper.updateDeleteStatus(id, status);
    }

    /**
     * 根据原课id删除转播课
     * @param id
     */
    @Override
    public void updateDeleteStatusByOriCourseId(long id,int status) {
        courseRelayMapper.updateDeleteStatusByOriCourseId(id,status);
    }
}
