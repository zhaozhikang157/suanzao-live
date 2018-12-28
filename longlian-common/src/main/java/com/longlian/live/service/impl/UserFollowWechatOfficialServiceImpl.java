package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.dto.CourseRelayDto;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.dao.AppUserCommonMapper;
import com.longlian.live.dao.UserFollowWechatOfficialMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.model.UserFollowWechatOfficial;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/4.
 */
@Service("UserFollowWechatOfficialService")
public class UserFollowWechatOfficialServiceImpl implements UserFollowWechatOfficialService {
    private static Logger log = LoggerFactory.getLogger(UserFollowWechatOfficialServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    UserFollowWechatOfficialMapper userFollowWechatOfficialMapper;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    AppUserCommonMapper appUserCommonMapper;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    WechatOfficialService wechatOfficialService;

    public UserFollowWechatOfficial selectFollowThirdOfficial(Long appId, String wechatAppid, String openId) {
        return userFollowWechatOfficialMapper.selectFollowThirdOfficial(appId, wechatAppid, openId);
    }

    public void insert(UserFollowWechatOfficial record) {
        userFollowWechatOfficialMapper.insert(record);
    }

    public void updateFollowStatus(Integer status, Long id) {

        userFollowWechatOfficialMapper.updateFollowStatus(status, id);
    }

    @Override
    public Map getUserFollowInfoByCourse(Long courseId, Long appId, Long roomId, Long teacher, String liveWay) {
        Map result = new HashMap();
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        String wechatAppid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, roomId + "");
        String qrCodeUrl= "";
        UserFollowWechatOfficial UserFollow = null;
        String qrCodeType = "";
        boolean isThird = false;
        if (!Utility.isNullorEmpty(wechatAppid) && !ll_appid.equals(wechatAppid) && teacher != appId) {
            UserFollow = this.selectFollowThirdOfficial(appId, wechatAppid, null);
//            redisUtil.del(RedisKey.ll_user_follow_wechat_cache + appId + "_" + wechatAppid);
            if (UserFollow == null) {
                String userFollowStr = redisUtil.get(RedisKey.ll_user_follow_wechat_cache + appId + "_" + wechatAppid);
                if (!StringUtils.isEmpty(userFollowStr)) {
                    UserFollow = JsonUtil.getObject(userFollowStr, UserFollowWechatOfficial.class);
                }
            }
            qrCodeType = WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue();
            qrCodeUrl= wechatOfficialService.getWechatOfficialQrCode(qrCodeType, roomId, courseId, appId, 0l);
            isThird = true;
        } else {
            UserFollow = this.selectFollowThirdOfficial(appId, ll_appid, null);
            qrCodeType = WechatQRCodeType.third_wechat_or_course_param.getValue();
            qrCodeUrl= wechatOfficialService.getWechatOfficialQrCode(qrCodeType, roomId, courseId, appId, 0l);
        }
        //满足语音和第三方
        if (Utility.isNullorEmpty(UserFollow) || (UserFollow.getStatus() != null && 1 == UserFollow.getStatus())) {
            result.put("isFollowThirdOfficial", "1");
        } else {
            result.put("isFollowThirdOfficial", "0");
        }
        result.put("qrCode", qrCodeUrl);
        result.put("isThird", isThird);

        return result;
    }
    @Override
    public UserFollowWechatOfficial findUserFollowById(Long id) {
        return userFollowWechatOfficialMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateAppIdByFollowId(Long appId, Long id) {
        userFollowWechatOfficialMapper.updateAppIdByFollowId(appId, id);
    }

    @Override
    public List<UserFollowWechatOfficial> getUserListByFollowId(String wechatAppid) {
        return userFollowWechatOfficialMapper.getUserListByFollowId(wechatAppid);
    }

    @Override
    public void sendCustomMessage4CourseByOpenid(WechatMessageInfo wechatMessageInfo, String website, long roomId, long courseId, long appId, long channelId, long followId) {
//        String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
        Course course;
        //处理转播课问题
        if(courseId > 0 && String.valueOf(courseId).length() >= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            course = courseBaseService.getRelayCourse(courseId);
        }else{
            course = courseBaseService.getCourse(courseId);
        }

        System.out.println("---->" + courseId + "");
        if (!Utility.isNullorEmpty(course)) {
            //查找直播间
            String title = "" + course.getLiveTopic();
            String time = Utility.getDateTimeStr(course.getStartTime(), "MM月dd日 HH:mm");
            String description = "直播时间" + time + "，记得准时来参加哦！";//course.getRemark();直播时间05月08日 19:00 ，记得准时来参加哦！
            String picurl = course.getCoverssAddress();
            //如果是龙链公众号的直播间 自动关注直播间
            if (appId <= 0) {
                AppUser appuser = appUserCommonMapper.getAppIdByOpenid(wechatMessageInfo.getFormUserName());    //先找到用户 by openId
                if (appuser != null && appuser.getId() > 0) appId = appuser.getId();
            }
            if (appId > 0) {
                roomId = course.getRoomId();
//                userFollowService.follow(appId, roomId);
            }
            String IS_SERIES_COURSE = "";
            if ("1".equals(course.getIsSeriesCourse())) {
                IS_SERIES_COURSE = "&isSeries=1";
            }
            String url = website + LocalOauth2Url.courseInfo + "?id=" + courseId + IS_SERIES_COURSE + "&channel=" + channelId + "&invitationAppId=" + appId + "&followId=" + followId;

            System.out.print("msg1 url===============================================>>" + url);
            //首次关注采用公众号自动发送
        /*    if(WechatEventType.subscribe.getValue().equals(wechatMessageInfo.getEvent())){
                wechatFirstFollowSendConent(wechatMessageInfo );
            }*/
            if (0 != channelId && "2".equals(course.getPushType())) {
                //发送图片
                weixinUtil.sendCustomMediaMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), course.getPushContent());
            } else if (0 != channelId && "1".equals(course.getPushType())) {
                //发送文字消息
                weixinUtil.sendCustomTextMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), course.getPushContent());
            } else {
                //发送客户消息 默认的
                weixinUtil.sendCustomMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), title, description, url, picurl);
            }
        }
    }

    @Override
    public void sendCustomMessage4LiveByOpenid(WechatMessageInfo wechatMessageInfo, String website, long roomId, long followId) {
//        String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
        LiveRoom liveRoom = liveRoomService.findById(roomId);
        if (!Utility.isNullorEmpty(liveRoom)) {
            //查找直播间
            String title = "亲，我们开通粉丝专属直播间啦，快进来看看吧~";
            String description = "直播间:" + liveRoom.getName() + "\r" + "我们将会以视频、语音形式在这个直播间进行在线直播，也会不定期邀请行业大咖与大家进行交流，大家实时跟老师互动、提问"
                    + "\r" + "所有的直播话题永久保存，不限次数回看！";
            String picurl = liveRoom.getCoverssAddress();
            //如果是龙链公众号的直播间 自动关注直播间
            AppUser appUser = appUserCommonMapper.getAppIdByOpenid(wechatMessageInfo.getFormUserName());    //先找到用户 by openId
            String isAutoFollow = "";
            if (appUser != null && appUser.getId() > 0) {
//                userFollowService.follow(appUser.getId(), roomId);
            } else {
                isAutoFollow = "&isAutoFollow=1&thirdWechatAppid=" + wechatMessageInfo.getAppId() + "&thirdOpenid=" + wechatMessageInfo.getFormUserName();
            }
            String url = website + LocalOauth2Url.liveRoom + "?id=" + roomId + isAutoFollow + "&followId=" + followId;
       /* if(WechatEventType.subscribe.getValue().equals(wechatMessageInfo.getEvent())){//首次关注，采用公众号
            wechatFirstFollowSendConent(wechatMessageInfo );
        }*/

            System.out.print("msg2 url===============================================>>" + url);
            //发送客户消息
            weixinUtil.sendCustomMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), title, description, url, picurl);
        }

    }
}
