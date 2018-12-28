package com.longlian.live.service.impl;

import com.longlian.live.dao.ProxyTeacherMapper;
import com.longlian.live.service.ProxyTeacherService;
import com.longlian.model.ProxyTeacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by admin on 2017/5/4.
 */
@Component("ProxyTeacherService")
public class ProxyTeacherServiceImpl implements ProxyTeacherService {

    @Autowired
    ProxyTeacherMapper proxyTeacherMapper;
    
     public int insertSelective(ProxyTeacher record){
        return proxyTeacherMapper.insertSelective(record);
    }

    public  ProxyTeacher getProxyTeacherByAppId(Long userId, Long appId){
        return proxyTeacherMapper.getProxyTeacherByAppId(userId, appId);
    }
    public  int updateProxyTeacher(Map map){
        return proxyTeacherMapper.updateProxyTeacher(map);
    }

    public ProxyTeacher selectByPrimaryKey(Long id){
        return proxyTeacherMapper.selectByPrimaryKey(id);
    }

    public Map getProxyAppIdByTeacherId(Long teacherId){
        return proxyTeacherMapper.getProxyAppIdByTeacherId(teacherId);
    }
}
