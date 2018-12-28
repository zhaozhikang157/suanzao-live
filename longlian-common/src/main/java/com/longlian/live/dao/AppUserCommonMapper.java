package com.longlian.live.dao;

import com.longlian.model.AppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/13.
 */
@Mapper
public interface AppUserCommonMapper {

    //获取所有用户部分信息（openid和unionid）
    List<Map> getAllUserPartInfo();
    int updateYunXinToken(@Param("id") long id, @Param("token") String token);

    AppUser selectByPrimaryKey(Long id);

    List<Map> getAllVirtualUserIds();

    AppUser queryByMobile(@Param("mobile") String mobile);

    int updateAppUserById(@Param("id") long id, @Param("map") Map map);

    Map getUserInfo(long id);

    int updateUserType(long id);

    AppUser getAppIdByOpenid(String id);

    Integer getProportion( @Param("id") Long id);
}
