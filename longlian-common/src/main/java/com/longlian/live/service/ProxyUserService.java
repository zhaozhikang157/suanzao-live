package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.ProxyUser;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/4.
 */
public interface ProxyUserService  {

    List<Map> getProxyUserListPage(DatagridRequestModel datagridRequestModel, String status);
    int updateProxyUser(Map map);
    int insertSelective(ProxyUser record);
    int deleteByPrimaryKey(Long id);
    ProxyUser getProxyUserByAppId(Long appId);

    Map getProxyUserDetail(Long id);
}
