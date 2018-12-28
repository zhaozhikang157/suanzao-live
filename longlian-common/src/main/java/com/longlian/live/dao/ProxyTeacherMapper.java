package com.longlian.live.dao;

import com.longlian.model.ProxyTeacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
@Mapper
public interface ProxyTeacherMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProxyTeacher record);

    int insertSelective(ProxyTeacher record);

    ProxyTeacher selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProxyTeacher record);
    int updateByPrimaryKey(ProxyTeacher record);
    int updateProxyTeacher(@Param("map") Map map);
    ProxyTeacher getProxyTeacherByAppId(@Param("userId") Long userId, @Param("appId") Long appId);
    Map getProxyAppIdByTeacherId(@Param("teacherId") Long teacherId);


}