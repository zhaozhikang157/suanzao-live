package com.longlian.live.service;

import com.longlian.model.ProxyTeacher;

import java.util.Map;

/**
 * Created by admin on 2017/5/4.
 */
public interface ProxyTeacherService {
    int insertSelective(ProxyTeacher record);
    ProxyTeacher getProxyTeacherByAppId(Long userId, Long appId);
    int updateProxyTeacher(Map map);
    ProxyTeacher selectByPrimaryKey(Long id);
    Map getProxyAppIdByTeacherId(Long teacherId);
}
