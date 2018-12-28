package com.longlian.live.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.dao.ProxyUserMapper;
import com.longlian.live.service.ProxyUserService;
import com.longlian.model.ProxyUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/4.
 */
@Component("ProxyUserService")
public class ProxyUserServiceImpl implements ProxyUserService {

    @Autowired
    ProxyUserMapper proxyUserMapper;
    
   public  List<Map> getProxyUserListPage(DatagridRequestModel requestModel,String status){
      return proxyUserMapper.getProxyUserListPage(requestModel,status);
   }
    public  int updateProxyUser(@Param("map") Map map){
        return proxyUserMapper.updateProxyUser(map);
    }
    public int insertSelective(ProxyUser record){
        return proxyUserMapper.insertSelective(record);
    }
    public int deleteByPrimaryKey(Long id){
        return proxyUserMapper.deleteByPrimaryKey(id);
    }
    public  ProxyUser getProxyUserByAppId( Long appId){
        return proxyUserMapper.getProxyUserByAppId(appId);
    }

    public Map getProxyUserDetail(Long id){
        return proxyUserMapper.getProxyUserDetail(id);
    }
}
