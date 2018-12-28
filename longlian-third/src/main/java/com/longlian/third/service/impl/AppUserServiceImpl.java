package com.longlian.third.service.impl;

import com.longlian.model.AppUser;
import com.longlian.third.dao.AppUserMapper;
import com.longlian.third.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */
@Service("appUserService")
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    AppUserMapper appUserMapper;
    @Override
    public AppUser getById(long id) {
        return appUserMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据 weixin  openid 获取 用户ID
     * @param openid
     * @return
     */
    @Override
    public Long getAppIdByOpenid(String openid) {
        return appUserMapper.getAppIdByOpenid(openid);
    }


    @Override
    public  Map queryUserLiveRoomByUnitionId(String appId){
        return  appUserMapper.queryUserLiveRoomByUnitionId(appId);
    }
}
