package com.longlian.live.service;

import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.longlian.model.UserFollowWechatOfficial;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/4.
 */
public interface UserFollowWechatOfficialService {

    UserFollowWechatOfficial selectFollowThirdOfficial(Long appId,String wechatAppid,String openId);
    void insert(UserFollowWechatOfficial record);
    void updateFollowStatus(Integer status,Long id);

    Map getUserFollowInfoByCourse( Long courseId, Long appId ,Long roomId , Long teacher ,String liveWay);

    UserFollowWechatOfficial findUserFollowById(Long id);

    void updateAppIdByFollowId(Long  appId,Long id);

    List<UserFollowWechatOfficial> getUserListByFollowId(String  wechatAppid);

    void sendCustomMessage4CourseByOpenid(WechatMessageInfo wechatMessageInfo ,String website, long roomId , long courseId , long appId , long channelId, long followId);
    void sendCustomMessage4LiveByOpenid(WechatMessageInfo wechatMessageInfo ,String website, long roomId,long followId);
}
