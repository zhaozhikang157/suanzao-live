package com.longlian.third.dao;

import com.longlian.model.AppUser;

import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserMapper {
    AppUser selectByPrimaryKey(long id);

    Long getAppIdByOpenid(String openid);

    Map queryUserLiveRoomByUnitionId(String unitionId);
}
