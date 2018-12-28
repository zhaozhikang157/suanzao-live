package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.ProxyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface ProxyUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProxyUser record);

    int insertSelective(ProxyUser record);

    ProxyUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProxyUser record);

    int updateByPrimaryKey(ProxyUser record);
    
    List<Map> getProxyUserListPage(@Param("page") DatagridRequestModel datagridRequestModel, @Param("status") String status);
    
    int updateProxyUser(@Param("map") Map map);

    ProxyUser getProxyUserByAppId(@Param("appId") Long appId);
    
    Map getProxyUserDetail(@Param("id") Long id);
}