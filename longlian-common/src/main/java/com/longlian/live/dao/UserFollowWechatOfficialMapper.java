package com.longlian.live.dao;

import com.longlian.model.UserFollowWechatOfficial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserFollowWechatOfficialMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserFollowWechatOfficial record);

    int insertSelective(UserFollowWechatOfficial record);

    UserFollowWechatOfficial selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFollowWechatOfficial record);

    int updateByPrimaryKey(UserFollowWechatOfficial record);
   
    UserFollowWechatOfficial selectFollowThirdOfficial( @Param("appId")Long appId,@Param("wechatAppid")String wechatAppid,@Param("openid")String openid);

    void updateFollowStatus(@Param("status")Integer status,@Param("id")Long id);
    void updateAppIdByFollowId(@Param("appId")Long  appId,@Param("id")Long id);
    List<UserFollowWechatOfficial> getUserListByFollowId(@Param("wechatAppid")String  wechatAppid);
}
