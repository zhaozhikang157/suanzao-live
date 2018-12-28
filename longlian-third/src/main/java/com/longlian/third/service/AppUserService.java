package com.longlian.third.service;

import com.longlian.model.AppUser;

import java.util.Map;


/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserService {
    AppUser getById(long id);
    Long getAppIdByOpenid(String openid);
    Map queryUserLiveRoomByUnitionId(String appId);
}
