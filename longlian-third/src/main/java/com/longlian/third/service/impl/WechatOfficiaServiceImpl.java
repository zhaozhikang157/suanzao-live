package com.longlian.third.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.huaxin.util.weixin.type.WechatEventType;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.live.dao.WechatOfficialMapper;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.service.UserFollowWechatOfficialService;
import com.longlian.live.service.WechatOfficialRoomService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.model.UserFollowWechatOfficial;
import com.longlian.third.service.AppUserService;
import com.longlian.third.service.CourseService;
import com.longlian.third.service.WechatOfficiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 第三方用户服务
 * Created by liuhan on 2017-03-20.
 */
@Service("wechatOfficiaService")
public class WechatOfficiaServiceImpl implements WechatOfficiaService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WechatOfficialMapper wechatOfficialMapper;

    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    AppUserService appUserService;

    @Autowired
    UserFollowService userFollowService;


    @Autowired
    WeixinUtil weixinUtil;

    @Autowired
    CourseService courseService;

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    @Autowired
    UserFollowWechatOfficialService userFollowWechatOfficialService;
    @Autowired
    WechatOfficialRoomService wechatOfficialRoomService;

    /**
     * 关注公众号 并发送客户消息
     *
     * @param wechatMessageInfo
     */
    @Override
    public void followWechatSendCustomMessageByOpenId(WechatMessageInfo wechatMessageInfo) throws Exception {
        //判断是否传入直播间
        if (!Utility.isNullorEmpty(wechatMessageInfo.getEventKey())) {
            UserFollowWechatOfficial userFollow = null;
            Long followId = 0l;
            long roomId = 0;
            long courseId = 0;
            String scenId = "";
            //最大为4294967295 32位二进制-1  前两位为分类详细见WechatQRCodeType
            if (Utility.isValidLong(wechatMessageInfo.getEventKey())) {
                scenId = wechatMessageInfo.getEventKey();
            } else {
                if (wechatMessageInfo.getEventKey().split("_").length != 2) return;
                scenId = wechatMessageInfo.getEventKey().split("_")[1]; //二次关注的
                if (!Utility.isValidLong(scenId)) return;
            }
            if (scenId.length() == 10) {
                if (scenId.startsWith(WechatQRCodeType.room.getValue())) {//主播间
                    roomId = Utility.parseLong(scenId.substring(WechatQRCodeType.room.getValue().length()));
//                    sendCustomMessage4LiveByOpenid(wechatMessageInfo ,roomId);
                    this.sendCustomerRommMessage(wechatMessageInfo, roomId);
                } else if (scenId.startsWith(WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue())) {//课程
                    Map map = redisUtil.hmgetAll(RedisKey.ll_live_third_auth_wechat_qrcode_pre + scenId);
                    if (map != null) {
                        roomId = Utility.parseLong(map.get("liveId").toString());
                        long channelId = Utility.parseLong(map.get("channelId").toString());//渠道Id
                        long appId = Utility.parseLong(map.get("appId").toString());
                        courseId = Utility.parseLong(map.get("courseId").toString());
                        System.out.println("course--wechat--》" + map);
                        String wechatAppid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, roomId + "");


                        UserFollowWechatOfficial UserFollowWechatOfficial = new UserFollowWechatOfficial();
                        UserFollowWechatOfficial.setWechatAppid(wechatMessageInfo.getAppId());
                        UserFollowWechatOfficial.setCreateTime(new Date());
                        UserFollowWechatOfficial.setOpenid(wechatMessageInfo.getFormUserName());
                        UserFollowWechatOfficial.setStatus(0);
                        UserFollowWechatOfficial.setAppId(appId);

                        map.put("UserFollowWechatOfficial", UserFollowWechatOfficial);
                        map.put("wechatMessageInfo", wechatMessageInfo);

                        String str = JsonUtil.toJson(map);
                        //采用双写
                        redisUtil.lpush(RedisKey.ll_user_follow_wechat_wait2db, str);
                        redisUtil.setex(RedisKey.ll_user_follow_wechat_cache + appId + "_" + wechatMessageInfo.getAppId(), 60 * 30, str);


                    }
                } else if (scenId.startsWith(WechatQRCodeType.third_wechat_or_course_param.getValue())) {//课程或者第三方公众号
                    //从redis中去取sceneId
                    Map map = redisUtil.hmgetAll(RedisKey.ll_live_third_auth_wechat_qrcode_pre + scenId);
                    courseId = Utility.parseLong(scenId.substring(WechatQRCodeType.third_wechat_or_course_param.getValue().length()));
                    if (map != null) {
                        //courseId = Utility.parseLong(map.get("courseId").toString());
                        //roomId = Utility.parseLong(map.get("liveId").toString());
                        //long channelId = Utility.parseLong(map.get("channelId").toString());//渠道Id
                        long appId = Utility.parseLong(map.get("appId").toString());
                        System.out.println("course--wechat 222--》" + map);

                        UserFollowWechatOfficial UserFollowWechatOfficial = new UserFollowWechatOfficial();
                        UserFollowWechatOfficial.setWechatAppid(wechatMessageInfo.getAppId());
                        UserFollowWechatOfficial.setCreateTime(new Date());
                        UserFollowWechatOfficial.setOpenid(wechatMessageInfo.getFormUserName());
                        UserFollowWechatOfficial.setStatus(0);
                        UserFollowWechatOfficial.setAppId(appId);

                        map.put("UserFollowWechatOfficial", UserFollowWechatOfficial);
                        map.put("wechatMessageInfo", wechatMessageInfo);

                        String str = JsonUtil.toJson(map);
                        //采用双写
                        redisUtil.lpush(RedisKey.ll_user_follow_wechat_wait2db, str);
                        redisUtil.setex(RedisKey.ll_user_follow_wechat_cache + appId + "_" + wechatMessageInfo.getAppId(), 60 * 30, str);

                    }
                }
            } else {
                //roomId = Utility.parseLong(scenId);//以前没有分类的，只有直播间，可以暂不用管了，时间已经已过去
                // sendCustomMessage4LiveByOpenid(wechatMessageInfo ,  roomId);
            }
        } else {
      /*      String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
            //搜索关注
            UserFollowWechatOfficial userFollow = userFollowWechatOfficialService.selectFollowThirdOfficial(null, wechatMessageInfo.getAppId(),wechatMessageInfo.getFormUserName());
            Long followId = 0l;
            if(Utility.isNullorEmpty(userFollow)) {
                UserFollowWechatOfficial UserFollowWechatOfficial = new UserFollowWechatOfficial();
                UserFollowWechatOfficial.setWechatAppid(wechatMessageInfo.getAppId());
                UserFollowWechatOfficial.setCreateTime(new Date());
                UserFollowWechatOfficial.setOpenid(wechatMessageInfo.getFormUserName());
                UserFollowWechatOfficial.setStatus(0);
                UserFollowWechatOfficial.setAppId(0l);
                userFollowWechatOfficialService.insert(UserFollowWechatOfficial);
                followId = UserFollowWechatOfficial.getId();
            }else{
                  userFollowWechatOfficialService.updateFollowStatus(0, userFollow.getId());
                followId = userFollow.getId();
            }
            List<Map>  list = wechatOfficialRoomService.getBindRoomListByWechatById(wechatMessageInfo.getAppId());
            if(!Utility.isNullorEmpty(list) && list.size()>0){
                Long roomId =  Long.parseLong(list.get(0).get("liveId").toString());
                userFollowWechatOfficialService.sendCustomMessage4LiveByOpenid(wechatMessageInfo,website, roomId,followId);
            }*/
            this.sendCustomerRommMessage(wechatMessageInfo, 0l);
        }
    }

    public void sendCustomerRommMessage(WechatMessageInfo wechatMessageInfo, Long roomId) {
        String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
        //搜索关注
        UserFollowWechatOfficial userFollow = userFollowWechatOfficialService.selectFollowThirdOfficial(null, wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName());
        Long followId = 0l;
        if (Utility.isNullorEmpty(userFollow)) {
            UserFollowWechatOfficial UserFollowWechatOfficial = new UserFollowWechatOfficial();
            UserFollowWechatOfficial.setWechatAppid(wechatMessageInfo.getAppId());
            UserFollowWechatOfficial.setCreateTime(new Date());
            UserFollowWechatOfficial.setOpenid(wechatMessageInfo.getFormUserName());
            UserFollowWechatOfficial.setStatus(0);
            UserFollowWechatOfficial.setAppId(0l);
            userFollowWechatOfficialService.insert(UserFollowWechatOfficial);
            followId = UserFollowWechatOfficial.getId();
        } else {
            userFollowWechatOfficialService.updateFollowStatus(0, userFollow.getId());
            followId = userFollow.getId();
        }
        if (roomId > 0) {
            userFollowWechatOfficialService.sendCustomMessage4LiveByOpenid(wechatMessageInfo, website, roomId, followId);
        } else {
            List<Map> list = wechatOfficialRoomService.getBindRoomListByWechatById(wechatMessageInfo.getAppId());
            if (!Utility.isNullorEmpty(list) && list.size() > 0) {
                roomId = Long.parseLong(list.get(0).get("liveId").toString());
                userFollowWechatOfficialService.sendCustomMessage4LiveByOpenid(wechatMessageInfo, website, roomId, followId);
            }
        }
    }

    /**
     * @param wechatMessageInfo
     * @throws Exception
     */
    @Override
    public void cannelFollowWechat(WechatMessageInfo wechatMessageInfo) throws Exception {
        UserFollowWechatOfficial UserFollow = userFollowWechatOfficialService.selectFollowThirdOfficial(null, wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName());
        if (!Utility.isNullorEmpty(UserFollow)) {
            if (0 == UserFollow.getStatus()) {
                userFollowWechatOfficialService.updateFollowStatus(1, UserFollow.getId());
            }
        }

    }

    /**
     * 直播间发送客服消息
     *
     * @param wechatMessageInfo 微信消息对象
     * @param roomId            直播间Id
     */
    public void sendCustomMessage4LiveByOpenid(WechatMessageInfo wechatMessageInfo, long roomId) {
        String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
        LiveRoom liveRoom = liveRoomService.findById(roomId);
        if (!Utility.isNullorEmpty(liveRoom)) {
            //查找直播间
            String title = "亲，我们开通粉丝专属直播间啦，快进来看看吧~";
            String description = "直播间:" + liveRoom.getName() + "\r" + "我们将会以视频、语音形式在这个直播间进行在线直播，也会不定期邀请行业大咖与大家进行交流，大家实时跟老师互动、提问"
                    + "\r" + "所有的直播话题永久保存，不限次数回看！";
            String picurl = liveRoom.getCoverssAddress();
            //如果是龙链公众号的直播间 自动关注直播间
            Long appUserId = appUserService.getAppIdByOpenid(wechatMessageInfo.getFormUserName());    //先找到用户 by openId
            String isAutoFollow = "";
            if (appUserId != null && appUserId > 0) {
//                userFollowService.follow(appUserId, roomId);
            } else {
                isAutoFollow = "&isAutoFollow=1&thirdWechatAppid=" + wechatMessageInfo.getAppId() + "&thirdOpenid=" + wechatMessageInfo.getFormUserName();
            }
            String url = website + LocalOauth2Url.liveRoom + "?id=" + roomId + isAutoFollow;
       /* if(WechatEventType.subscribe.getValue().equals(wechatMessageInfo.getEvent())){//首次关注，采用公众号
            wechatFirstFollowSendConent(wechatMessageInfo );
        }*/
            //发送客户消息
            weixinUtil.sendCustomMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), title, description, url, picurl);
        }
    }

    /**
     * 直播课发送客服消息
     *
     * @param wechatMessageInfo 微信消息对象
     * @param roomId            直播间Id
     * @param courseId          课程Id
     * @param appId             用户Id
     * @param channelId         渠道Id
     */
    public void sendCustomMessage4CourseByOpenid(WechatMessageInfo wechatMessageInfo, long roomId, long courseId, long appId, long channelId, Long followId) {
        String website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
        Course course = courseService.getById(courseId);
        System.out.println("---->" + courseId + "");
        if (!Utility.isNullorEmpty(course)) {
            //查找直播间
            String title = "" + course.getLiveTopic();
            String time = Utility.getDateTimeStr(course.getStartTime(), "MM月dd日 HH:mm");
            String description = "直播时间" + time + "，记得准时来参加哦！";//course.getRemark();直播时间05月08日 19:00 ，记得准时来参加哦！
            String picurl = course.getCoverssAddress();
            //如果是龙链公众号的直播间 自动关注直播间
            if (appId <= 0) {
                Long appIdL = appUserService.getAppIdByOpenid(wechatMessageInfo.getFormUserName());    //先找到用户 by openId
                if (appIdL != null && appIdL > 0) appId = appIdL;
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

    /**
     * 首次发送微信客服消息
     *
     * @param wechatMessageInfo
     */
    public void wechatFirstFollowSendConent(WechatMessageInfo wechatMessageInfo) {
        String wechatFirstFollowSendConent = systemParaRedisUtil.getWechatFirstFollowSendConent();
        if (!Utility.isNullorEmpty(wechatFirstFollowSendConent)) {
            weixinUtil.sendCustomTextMessageByOpenId(wechatMessageInfo.getAppId(), wechatMessageInfo.getFormUserName(), wechatFirstFollowSendConent);
        }
    }
}
