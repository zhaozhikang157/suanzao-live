package com.longlian.live.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.weixin.ParamesAPI.WechatAppInfo;
import com.huaxin.util.weixin.ParamesAPI.WechatAuthorizationToken;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.LiveRoomDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.UserFollow;
import com.longlian.model.WechatOfficial;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuhan on 2017-03-20.
 */
public interface WechatOfficialService {
    ActResultDto loginIn(WechatOfficial user)  throws Exception ;

    String updateAndGetAuthorizeAccessToken(String appid);

    WechatOfficial addOrUpdate(WechatAuthorizationToken wechatAuthorizationToken ,  WechatAppInfo wechatAppInfo );

    void unauthorized(String appid);
    void update(WechatOfficial wechatOfficial);
    void updateWechat(WechatOfficial wechatOfficial);
    void updateForWechatOfficial(WechatOfficial wechatOfficial);
    WechatOfficial getByAppid(String appid);

    List<WechatOfficial> selectAllOfficial();
    List<WechatOfficial> selectUseList();
    List<WechatOfficial> selectUseTokenList();

    void getSendWechatTemplateMessageSaveRedis(CourseDto courseDto);
    void getFollowUserSendWechatTemplateMessageByCourse(CourseDto course );
    void getFollowUserSendWechatTemplateMessageByRelayCourse(CourseDto course );
    void getFollowUserSendWechatTemplateMessageByCoursePreRemind(Course course , AppUser teach , Set<String> set);

    void getSendWechatCustomerMessageByFollow(UserFollow userFollow);


    void sendRoomOrCourseCardWachatMessage(ShareRecordDto shareRecord);
    WechatOfficial selectById(long id);

    Map selectBindListById(long id);

    void handleThirdWechatTokenLoseUse();

    List<Map> getWechatOfficialRoomListPage(DatagridRequestModel datagridRequestModel,Map map);
    void updateForWechatOfficialById(WechatOfficial wechatOfficial);
    Map selectLiveRoomById(long roomId);
    void updateAudit(WechatOfficial WechatOfficial);
    void updateManager(WechatOfficial wechatOfficial);
    Boolean isWechatOfficial(Long liveRoomId);

    List<WechatOfficial> getAllWechat();

    void updateFreeDate(long id);

    String getWechatOfficialQrCode(String qrCodeType, Long roomId, Long courseId, Long appId, Long channelId);

    String getContentByWechat();

    WechatOfficial selectByAppid(String appId);
}
