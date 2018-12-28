package com.longlian.console.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 系统配置操作mapper
 */
public interface SystemConfigMapper {
    /**
     * 根据类型查询系统参数
     * @param type
     * @return
     */
    Map<String,Object> findSystemConfigByType(@Param("type") Integer type);

    Map<String,Object> findSystemConfigById(@Param("id") Long id);
}