package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WechatAppInfo;
import com.huaxin.util.weixin.ParamesAPI.WechatAuthorizationToken;
import com.huaxin.util.weixin.ParamesAPI.WechatConst;
import com.huaxin.util.weixin.type.WechatTemplateMessage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.dao.WechatOfficialMapper;
import com.longlian.live.newdao.WechatOfficialRoomMapper;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.*;
import com.longlian.token.WechatOfficialIdentity;
import com.longlian.type.ReturnMessageType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 第三方用户服务
 * Created by liuhan on 2017-03-20.
 */
@Service("wechatOfficialService")
public class WechatOfficialServiceImpl implements WechatOfficialService {
    private static Logger log = LoggerFactory.getLogger(WechatOfficialServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WechatOfficialMapper wechatOfficialMapper;

    @Autowired
    WeixinUtil weixinUtil;

    @Autowired
    UserFollowService userFollowService;

    @Autowired
    WechatOfficialRoomMapper wechatOfficialRoomMapper;

    @Autowired
    SsoUtil ssoUtil;

    @Override
    public WechatOfficial addOrUpdate(WechatAuthorizationToken wechatAuthorizationToken, WechatAppInfo wechatAppInfo) {
        WechatOfficial wechatOfficial = wechatOfficialMapper.selectByAppid(wechatAuthorizationToken.getAppid());
        boolean isAdd = false;
        if (wechatOfficial == null) {
            wechatOfficial = new WechatOfficial();
            isAdd = true;
        }
        wechatOfficial.setAccessToken(wechatAuthorizationToken.getToken());
        wechatOfficial.setRefreshToken(wechatAuthorizationToken.getRefreshToken());

        wechatOfficial.setUserName(wechatAppInfo.getUserName());
        wechatOfficial.setNickName(wechatAppInfo.getNickName());
        wechatOfficial.setHeadImg(wechatAppInfo.getHeadImg());
        wechatOfficial.setQrcodeUrl(wechatAppInfo.getQrcodeUrl());
        wechatOfficial.setPrincipalName(wechatAppInfo.getPrincipalName());
        wechatOfficial.setVerifyTypeInfo(wechatAppInfo.getVerifyTypeInfo());
        if (isAdd) {
            wechatOfficial.setAppid(wechatAuthorizationToken.getAppid());
            wechatOfficial.setStatus("0");
            wechatOfficial.setCreateTime(new Date());
            wechatOfficial.setServiceType(wechatAppInfo.getServiveTypeInfo());
            wechatOfficialMapper.add(wechatOfficial);
        } else {
            wechatOfficial.setStatus("0");
            wechatOfficialMapper.updateWechat(wechatOfficial);
        }
        //将授权的公众号token存入redis中，并设置有效时间
        redisUtil.setex(RedisKey.ll_live_appid_access_token_pre + wechatOfficial.getAppid(), RedisKey.ll_live_access_token_use_time, wechatOfficial.getAccessToken());
        return wechatOfficial;
    }

    /**
     * 取消授权
     *
     * @param appid
     */
    @Override
    public void unauthorized(String appid) {
        int count = wechatOfficialMapper.unauthorized(appid);
        if (count > 0) {
            //查询liveId ,清空
            long liveId = wechatOfficialMapper.getLiveIdByAppid(appid);
            if (liveId > 0) redisUtil.hdel(RedisKey.ll_live_appid_use_authorizer_room_info, liveId + "");
        }
    }

    @Override
    public void update(WechatOfficial wechatOfficial) {

    }

    /**
     * 登录
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public ActResultDto loginIn(WechatOfficial WechatOfficial) throws Exception {
        ActResultDto result = new ActResultDto();
        WechatOfficial muser = wechatOfficialMapper.selectByAppid(WechatOfficial.getAppid());
        if (muser == null) {
            result.setMessage("用户登录异常！");
            result.setCode(ReturnMessageType.CODE_LOGIN_FALSE.getCode());
            return result;
        }
        WechatOfficialIdentity identity = new WechatOfficialIdentity();
        BeanUtils.copyProperties(muser, identity);
        String token = createToken(muser.getId());
        identity.setToken(token);
        Map<String, String> map = ObjectUtil.objectToStringMap(identity);
        //放到redis
        redisUtil.hmset(RedisKey.ll_live_third_login_prefix + muser.getId(), map, RedisKey.ll_live_app_user_login_valid_time);
        result.setData(identity);
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return result;
    }

    public String createToken(long userid) {
        return CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX + Jwts.builder().setSubject(userid + "").claim(
                "roles", userid).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256,
                "secretkey").compact();
    }

    /**
     * 成功登录
     *
     * @param user
     * @return
     * @throws Exception
     */
    public WechatOfficialIdentity loginSuccess(WechatOfficialIdentity user) throws Exception {
        //微信客户端
        user.setToken(SystemUtil.createToken(user.getId(), ""));
        Map<String, String> m = ObjectUtil.objectToStringMap(user);
        redisUtil.hmset(RedisKey.ll_live_third_login_prefix + user.getId(), m, RedisKey.ll_live_app_user_login_valid_time);
        return user;
    }


    /**
     * 根据授权服务号的appi 获取token 如果过期，则根据refreshToken重新获取token，并更新
     *
     * @param appid
     * @return
     */
    @Override
    public String updateAndGetAuthorizeAccessToken(String appid) {
        String token = redisUtil.get(RedisKey.ll_live_appid_access_token_pre + appid);
        if (Utility.isNullorEmpty(token)) {
            //先先从数据库查找refreshAccessToken
            token = updateAuthorizeAccessToken(appid);
        }
        return token;
    }

    /**
     * 更新
     *
     * @param appid
     * @return
     */
    public String updateAuthorizeAccessToken(String appid) {
        String token = null;
        WechatOfficial wechatOfficial = wechatOfficialMapper.selectByAppid(appid);
        if (wechatOfficial != null) {
            WechatAuthorizationToken wechatAuthorizationToken = weixinUtil.getThirdComponentAuthorizerAccessTokenByRefreshToken(appid, wechatOfficial.getRefreshToken());
            if (wechatAuthorizationToken != null) {
                token = wechatAuthorizationToken.getToken();
                //更新数据库
                wechatOfficial.setAccessToken(token);
                wechatOfficial.setRefreshToken(wechatAuthorizationToken.getRefreshToken());
                wechatOfficialMapper.updateTokenByAppid(wechatOfficial);
                //重新设置redis  将授权的公众号token存入redis中，并设置有效时间
                redisUtil.setex(RedisKey.ll_live_appid_access_token_pre + wechatOfficial.getAppid(), RedisKey.ll_live_access_token_use_time, wechatOfficial.getAccessToken());
            }
        } else {
            log.error("微信号appid-->" + appid + "  没有token记录，请重新授权");
        }
        return token;
    }


    /**
     * 根据公众id获取对象
     *
     * @param appid
     * @return
     */
    @Override
    public WechatOfficial getByAppid(String appid) {
        return wechatOfficialMapper.selectByAppid(appid);
    }

    @Override
    public List<WechatOfficial> selectAllOfficial() {
        return wechatOfficialMapper.selectAllOfficial();
    }

    /**
     * 根据授权状态 获取  带直播间
     *
     * @return
     */
    @Override
    public List<WechatOfficial> selectUseList() {
        return wechatOfficialMapper.selectUseList();
    }

    /**
     * 根据授权状 有效
     *
     * @return
     */
    @Override
    public List<WechatOfficial> selectUseTokenList() {
        return wechatOfficialMapper.selectUseTokenList();
    }

    @Override
    public void updateWechat(WechatOfficial wechatOfficial) {
        wechatOfficialMapper.updateWechat(wechatOfficial);
    }

    @Override
    public WechatOfficial selectById(long id) {
        return wechatOfficialMapper.selectById(id);

    }

    /**
     * 查询绑定直播间
     *
     * @param id
     * @return
     */
    @Override
    public Map selectBindListById(long id) {
        return wechatOfficialMapper.selectBindListById(id);
    }


    /**
     * 创建课程发送微信模板消息和内部消息
     *
     * @param course
     */
    public void getSendWechatTemplateMessageSaveRedis(CourseDto course) {
        redisUtil.lpush(RedisKey.ll_live_create_course_send_wechat_messsage, JsonUtil.toJsonString(course));
    }

    public void getSendWechatCustomerMessageByFollow(UserFollow UserFollow) {
        redisUtil.lpush(RedisKey.ll_live_follow_Liveroom_send_wechat_messsage, JsonUtil.toJsonString(UserFollow));
    }

    @Override
    public Map selectLiveRoomById(long roomId) {
        return wechatOfficialMapper.selectLiveRoomById(roomId);
    }

    /**
     * 创建课程 发送微信模板消息
     *
     * @param course
     */
    @Override
    public void getFollowUserSendWechatTemplateMessageByCourse(CourseDto course) {
        //先判断老师第三方公众号是否授权
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        //获取token
        String appid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, course.getRoomId() + "");
        String website = CustomizedPropertyConfigurer.getContextProperty("website");
        WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
        String first = "您关注的直播间【" + course.getAppUserName() + "】有新的课程啦！";
        wechatTemplateMessage.setFirst(first);
        wechatTemplateMessage.setUrl(website + LocalOauth2Url.courseInfo + "?id=" + course.getId());
        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
        wechatTemplateMessage.setKeyword2(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
        wechatTemplateMessage.setKeyword3("酸枣在线");
        Map woMap = wechatOfficialMapper.selectLiveRoomById(course.getRoomId());
        if (!Utility.isNullorEmpty(woMap.get("messageFlag")) && "1".equals(woMap.get("messageFlag"))) {
            if (!Utility.isNullorEmpty(appid) && !ll_appid.equals(appid)) {//如果不是龙链默认的公众号
                WechatOfficial wechatOfficial = wechatOfficialMapper.selectByAppid(appid);
                WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomMapper.findByRoomId(course.getRoomId());
                //先给老师第三方公众号全部发送消息模板
                if (wechatOfficial != null && wechatOfficialRoom != null &&
                        "2".equals(wechatOfficial.getServiceType()) && //必须是服务号
                        !Utility.isNullorEmpty(wechatOfficial.getReserveReminderId()) //模板消息不为空
                        && "1".equals(wechatOfficialRoom.getAuditStatus())) {
                           if (("C9k4wEh7GewxjGk0QTe9kYBmxj7nzBbGX0eW84CsjAg").equals(wechatOfficial.getReserveReminderId())
                            || ("1dDvDpcC-j1xNYccfVbTPbhr-dE5YbcIRuzRlAOiZOg").equals(wechatOfficial.getReserveReminderId())
                            || "sFCBOdIXPXULPGM7AJZBqzNuJX5H6Ni6KoBaevDD6-o".equals(wechatOfficial.getReserveReminderId())
                            || "60eGUzuyAYJBn9vPSdIjVXW-3qb7YTq_6hK6HyEvlnY".equals(wechatOfficial.getReserveReminderId())
                            ) {
                        first = course.getRemark();
                        wechatTemplateMessage.setFirst(first);
                        wechatTemplateMessage.setTempalteId(wechatOfficial.getReserveReminderId());
                        wechatTemplateMessage.setUrl(website + LocalOauth2Url.courseInfo + "?id=" + course.getId());
                        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
                        wechatTemplateMessage.setKeyword2(course.getAppUserName());
                        wechatTemplateMessage.setKeyword3(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
                        wechatTemplateMessage.setRemark("直播开始前会向您推送开课提醒哦,请注意查看！");
                        weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                    } else {
                        wechatTemplateMessage.setTempalteId(wechatOfficial.getReserveReminderId());
                        wechatTemplateMessage.setKeyword4(wechatOfficialRoom.getMobile());
                        wechatTemplateMessage.setRemark("直播开始前会向您推送开课提醒哦,请注意查看！");
                        weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                    }
                }

            } else {
                //获取关注主播间的粉丝
                List<Map> list = userFollowService.getOpenIdByLiveRoomId(course.getRoomId());//且判断是否真的关注过龙链公众号 IS_FOLLOW_LL_WECHAT = '1'
                for (Map map : list) {
                    if (Utility.isNullorEmpty(map.get("openid"))) continue;//等于空，跳出,可能是虚拟用户
                    String userName = map.get("name").toString();
                    String openid = map.get("openid").toString();
                    if (!Utility.isNullorEmpty(map.get("isFollowLlWechat"))) {
                        String isFollowLlWechat = map.get("isFollowLlWechat").toString();//是否关注过龙链公众号 0- 没有  1-关注过
                    }
                    if (!Utility.isNullorEmpty(map.get("thirdOpenid"))) {
                        String thirdOpenid = map.get("thirdOpenid").toString();//第三方公众号的openid
                    }
                    String templateId = CustomizedPropertyConfigurer.getContextProperty("teach_create_course_template_id");//LE0x2brR1ZiBfRhYeapeRFE4ym_RYumKHHCP9bqAKqk
                    //System.out.println("templateId------->" + templateId);
                    wechatTemplateMessage.setTempalteId(templateId);
                    wechatTemplateMessage.setKeyword4("4001-169-269");
                    wechatTemplateMessage.setRemark("直播开始前会向您推送开课提醒哦,请注意查看！");

                    String IS_SERIES_COURSE = "";
                    if ("1".equals(course.getIsSeriesCourse())) {
                        IS_SERIES_COURSE = "&isSeries=1";
                    }
                    String title = "" + course.getLiveTopic();
                    String time = Utility.getDateTimeStr(course.getStartTime(), "MM月dd日 HH:mm");
                    String description = "您关注的直播间" + course.getAppUserName() + "即将开课,欢迎加入哦！\n\n" + "课程名称:" + course.getLiveTopic() + "\n开课时间:" + time + "\n\n直播开始前会向您推送开课提醒哦,请注意查看！";
                    String picurl = course.getCoverssAddress();
                    JSONObject jsonObject = weixinUtil.sendCustomMessageByOpenId(ll_appid, openid, "课程开课提醒", description,
                            website + LocalOauth2Url.courseInfo + "?id=" + course.getId() + IS_SERIES_COURSE + "&channel=" + 0 + "&invitationAppId=0", picurl);
                    String errcode = "";
                    if (jsonObject.containsKey("errcode")) {
                        errcode = jsonObject.getString("errcode");//授权方appid
                    }
                    if (!"0".equals(errcode)) {
                        weixinUtil.sendTemplateMessageById(ll_appid, openid, wechatTemplateMessage);
                    }
                }
            }
        }
    }

    /**
     * 转播课程 发送微信模板消息
     *
     * @param course
     */
    @Override
    public void getFollowUserSendWechatTemplateMessageByRelayCourse(CourseDto course) {
        String pageSize="20";
        String offset="0";
        String sort="0";
        String sc="1";
        String courseName=course.getLiveTopic();
        String picurl = course.getCoverssAddress();
        String website = CustomizedPropertyConfigurer.getContextProperty("website");
        //先判断老师第三方公众号是否授权
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        //获取token
        String appid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, course.getRoomId() + "");
        WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
        String first = "您关注的【" + course.getAppUserName() + "】老师已成功转播一节课，快去看看吧！";
        wechatTemplateMessage.setFirst(first);
        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
        wechatTemplateMessage.setKeyword2(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
        Map woMap = wechatOfficialMapper.selectLiveRoomById(course.getRoomId());
        if (!Utility.isNullorEmpty(woMap.get("messageFlag")) && "1".equals(woMap.get("messageFlag"))) {

            if (!Utility.isNullorEmpty(appid) && !ll_appid.equals(appid)) {//如果不是龙链默认的公众号
                WechatOfficial wechatOfficial = wechatOfficialMapper.selectByAppid(appid);
                WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomMapper.findByRoomId(course.getRoomId());
                //先给老师第三方公众号全部发送消息模板
                if (wechatOfficial != null && wechatOfficialRoom != null &&
                        "2".equals(wechatOfficial.getServiceType()) && //必须是服务号
                        !Utility.isNullorEmpty(wechatOfficial.getReserveReminderId()) //模板消息不为空
                        && "1".equals(wechatOfficialRoom.getAuditStatus())) {

                    String url=website + LocalOauth2Url.relaymarket +
                            "?pageSize=" + pageSize + "&offset=" + offset + "&sort=" + sort + "&sc=" + sc + "&courseName=" + courseName;
                    if(course.getRelayCourseType()==1){
                        url=website+LocalOauth2Url.courseInfo+"?id="+course.getId();
                        wechatTemplateMessage.setUrl(url);
                    }
//                    wechatTemplateMessage.setRemark("快点击我去转播市场看看吧！");
                    wechatTemplateMessage.setKeyword3("酸枣在线");//
                    wechatTemplateMessage.setKeyword4("4001-169-269");//
                    if (("C9k4wEh7GewxjGk0QTe9kYBmxj7nzBbGX0eW84CsjAg").equals(wechatOfficial.getReserveReminderId())
                            || ("1dDvDpcC-j1xNYccfVbTPbhr-dE5YbcIRuzRlAOiZOg").equals(wechatOfficial.getReserveReminderId())
                            || "sFCBOdIXPXULPGM7AJZBqzNuJX5H6Ni6KoBaevDD6-o".equals(wechatOfficial.getReserveReminderId())
                            || "60eGUzuyAYJBn9vPSdIjVXW-3qb7YTq_6hK6HyEvlnY".equals(wechatOfficial.getReserveReminderId())
                            ) {
//                        first = course.getRemark();
//                        wechatTemplateMessage.setFirst(first);
                        wechatTemplateMessage.setTempalteId(wechatOfficial.getReserveReminderId());
//                        wechatTemplateMessage.setUrl(website + LocalOauth2Url.courseInfo + "?id=" + course.getId());
//                        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
//                        wechatTemplateMessage.setKeyword2(course.getAppUserName());
//                        wechatTemplateMessage.setKeyword3(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
//                        wechatTemplateMessage.setRemark("直播开始前会向您推送开课提醒哦,请注意查看！");
                        weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                    } else {
                        wechatTemplateMessage.setTempalteId(wechatOfficial.getReserveReminderId());
//                        wechatTemplateMessage.setKeyword4(wechatOfficialRoom.getMobile());
//                        wechatTemplateMessage.setRemark("直播开始前会向您推送开课提醒哦,请注意查看！");
                        weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                    }
                }

            }else{
                //获取关注主播间的粉丝
                List<Map> list = userFollowService.getOpenIdByLiveRoomId(course.getRoomId());//且判断是否真的关注过龙链公众号 IS_FOLLOW_LL_WECHAT = '1'
                for (Map map : list) {
                    if (Utility.isNullorEmpty(map.get("openid"))) continue;//等于空，跳出,可能是虚拟用户
                    String openid = map.get("openid").toString();

                    String templateId = CustomizedPropertyConfigurer.getContextProperty("teach_create_relay_course_pre_template_id");//LE0x2brR1ZiBfRhYeapeRFE4ym_RYumKHHCP9bqAKqk
                    wechatTemplateMessage.setTempalteId(templateId);
                    wechatTemplateMessage.setRemark("快点击我去转播市场看看吧！");
                    String description = "您关注的" + course.getAppUserName() + "老师已成功设置一节转播课，快去看看吧！";
                    String url=website + LocalOauth2Url.relaymarket +
                            "?pageSize=" + pageSize + "&offset=" + offset + "&sort=" + sort + "&sc=" + sc + "&courseName=" + courseName;
                    if(course.getRelayCourseType()==1){
                        description = "您关注的" + course.getAppUserName() + "老师已成功转播一节课，快去看看吧！";
                        url=website+LocalOauth2Url.courseInfo+"?id="+course.getId();
                    }
                    wechatTemplateMessage.setUrl(url);
                    JSONObject jsonObject = weixinUtil.sendCustomMessageByOpenId(ll_appid, openid, "转播课程提醒", description,
                            url, picurl);
                    String errcode = "";
                    if (jsonObject.containsKey("errcode")) {
                        errcode = jsonObject.getString("errcode");//授权方appid
                    }
                    if (!"0".equals(errcode)) {
                        weixinUtil.sendTemplateMessageById(ll_appid, openid, wechatTemplateMessage);
                    }
            }



            }
        }
    }


    /**
     * 创建课程 发送微信模板消息  提前提醒
     *
     * @param course
     * @param teach  老师
     * @param set    运营部用户Ids
     */
    @Override
    public void getFollowUserSendWechatTemplateMessageByCoursePreRemind(Course course, AppUser teach, Set<String> set) {
        //先判断老师第三方公众号是否授权
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        //获取token
        String appid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, course.getRoomId() + "");
        String website = CustomizedPropertyConfigurer.getContextProperty("live_website");
        WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
        String first = "您关注的直播间【" + teach.getName() + "】即将开课,欢迎加入哦！";
        wechatTemplateMessage.setFirst(first);
        wechatTemplateMessage.setUrl(website + LocalOauth2Url.courseInfo + "?id=" + course.getId());
        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
        wechatTemplateMessage.setKeyword2(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
        Map woMap = wechatOfficialMapper.selectLiveRoomById(course.getRoomId());//获取直播间消息开关是否关闭 0-关 1-开
        if (!Utility.isNullorEmpty(woMap.get("messageFlag")) && "1".equals(woMap.get("messageFlag"))) {
            if (!Utility.isNullorEmpty(appid) && !ll_appid.equals(appid)) {//如果不是龙链默认的公众号
                log.info("直播间已授权01"+course.getRoomId());
                WechatOfficial wechatOfficial = wechatOfficialMapper.selectByAppid(appid);
                WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomMapper.findByRoomId(course.getRoomId());
                if (wechatOfficial != null && wechatOfficialRoom != null && "2".equals(wechatOfficial.getServiceType()) && "1".equals(wechatOfficialRoom.getAuditStatus())) { //必须是服务号
                    if (("tPG9RutugxSgFVCvxBaEUWSQu6hTg4Ckm-6RduVLW9w").equals(wechatOfficial.getReservePreReminderId())
                            || ("5Bluc_HBKB0_HQLdi5_z8fMmQXyqrxjLvepR5qxY2E0").equals(wechatOfficial.getReservePreReminderId())
                            || "x5SUT28aQ7_IciXYA1WN-2PehmUiJ4epHj8XomQIAZM".equals(wechatOfficial.getReservePreReminderId())
                            || "WLuPS1S-WAply50ggKKiSfMLG-ViF70Xgy3mf2IvmW8".equals(wechatOfficial.getReservePreReminderId())
                            ) {
                        log.info("直播间已授权02"+course.getRoomId());
                        first = "您报名的直播【" + course.getLiveTopic() + "】马上就要开始啦，赶紧去观看吧！";
                        wechatTemplateMessage.setFirst(first);
                        wechatTemplateMessage.setTempalteId(wechatOfficial.getReservePreReminderId());
                        wechatTemplateMessage.setUrl(website + LocalOauth2Url.courseInfo + "?id=" + course.getId());
                        wechatTemplateMessage.setKeyword1(course.getLiveTopic());
                        wechatTemplateMessage.setKeyword2(Utility.getDateTimeStr(course.getStartTime(), "yyyy-MM-dd HH:mm"));
                        wechatTemplateMessage.setRemark("");
                        weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                    } else {
                        if (!Utility.isNullorEmpty(wechatOfficial.getReservePreReminderId())) {
                            log.info("直播间已授权03"+course.getRoomId());
                            first = "您报名的直播课程【" + course.getLiveTopic() + "】马上就要开始啦,赶紧去听课吧！";
                            wechatTemplateMessage.setFirst(first);
                            wechatTemplateMessage.setKeyword3("");
                            wechatTemplateMessage.setKeyword4("");
                            wechatTemplateMessage.setTempalteId(wechatOfficial.getReservePreReminderId());
                            wechatTemplateMessage.setRemark("");
                            weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                        } else {
                            if (!Utility.isNullorEmpty(wechatOfficial.getReserveReminderId())) {
                                log.info("直播间已授权04"+course.getRoomId());
                                wechatTemplateMessage.setTempalteId(wechatOfficial.getReserveReminderId());
                                wechatTemplateMessage.setKeyword3("酸枣在线");
                                wechatTemplateMessage.setKeyword4(wechatOfficialRoom.getMobile());
                                wechatTemplateMessage.setRemark("");
                                weixinUtil.sendTemplateMassageToFollowWechat(appid, wechatTemplateMessage);
                            }
                        }
                    }
                }
            } else {
                log.info("直播间未授权01"+course.getRoomId());
                //获取关注主播间的粉丝
                List<Map> list = userFollowService.getOpenIdByLiveRoomId(course.getRoomId());//且判断是否真的关注过龙链公众号 IS_FOLLOW_LL_WECHAT = '1'
//            String templateId = CustomizedPropertyConfigurer.getContextProperty("teach_create_course_template_id");//LE0x2brR1ZiBfRhYeapeRFE4ym_RYumKHHCP9bqAKqk
                String templateId = CustomizedPropertyConfigurer.getContextProperty("teach_create_course_pre_template_id");
                wechatTemplateMessage.setTempalteId(templateId);
                for (Map map : list) {
                    if (Utility.isNullorEmpty(map.get("openid"))) continue;//等于空，跳出,可能是虚拟用户
                    String userName = map.get("name").toString();
                    String openid = map.get("openid").toString();
                    if (set != null && !Utility.isNullorEmpty(openid)) {
                        set.remove(openid);//删除已经发过的人员
                    }
                    if (!Utility.isNullorEmpty(map.get("isFollowLlWechat"))) {
                        String isFollowLlWechat = map.get("isFollowLlWechat").toString();//是否关注过龙链公众号 0- 没有  1-关注过
                    }
                    if (!Utility.isNullorEmpty(map.get("thirdOpenid"))) {
                        String thirdOpenid = map.get("thirdOpenid").toString();//第三方公众号的openid
                    }
                    //wechatTemplateMessage.setKeyword4("haoyyewe@sina.com");
                    wechatTemplateMessage.setRemark("");
                    String IS_SERIES_COURSE = "";
                    if ("1".equals(course.getIsSeriesCourse())) {
                        IS_SERIES_COURSE = "&isSeries=1";
                    }
                    String title = "" + course.getLiveTopic();
                    String time = Utility.getDateTimeStr(course.getStartTime(), "MM月dd日 HH:mm");
                    String description = "您报名的直播课程" + title + "马上就要开始啦,赶紧去听课吧！\n\n课程名称:" + course.getLiveTopic() + "\n开课时间:" + time;
                    String picurl = course.getCoverssAddress();
                    JSONObject jsonObject = weixinUtil.sendCustomMessageByOpenId(ll_appid, openid, "上课提醒", description,
                            website + LocalOauth2Url.courseInfo + "?id=" + course.getId() + IS_SERIES_COURSE + "&channel=" + 0 + "&invitationAppId=0", picurl);
                    String errcode = "";
                    if (jsonObject.containsKey("errcode")) {
                        errcode = jsonObject.getString("errcode");//授权方appid
                    }
                    if (!"0".equals(errcode)) {
                        weixinUtil.sendTemplateMessageById(ll_appid, openid, wechatTemplateMessage);
                    }
                }
                //发送运营部门人员
                for (String opendid : set) {
                    wechatTemplateMessage.setRemark("");
                    weixinUtil.sendTemplateMessageById(ll_appid, opendid, wechatTemplateMessage);
                }
            }
        }
    }

    public void updateForWechatOfficial(WechatOfficial wechatOfficial) {
        wechatOfficialMapper.updateForWechatOfficial(wechatOfficial);
    }

    /**
     * 分享直播间和课程邀请卡发送微信消息
     *
     * @param shareRecord
     */
    @Override
    public void sendRoomOrCourseCardWachatMessage(ShareRecordDto shareRecord) {
        //先判断老师第三方公众号是否授权
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        if (!Utility.isNullorEmpty(shareRecord.getImgUrl()) &&
                !Utility.isNullorEmpty(shareRecord.getAppId()) &&
                !Utility.isNullorEmpty(shareRecord.getOpenid()) &&
                ("LIVE_ROOM_INVI_CARD".equals(shareRecord.getSystemType()) || "COURSE_INVI_CARD".equals(shareRecord.getSystemType()))
                ) {
            weixinUtil.sendCustomMediaMessageByOpenId(ll_appid, shareRecord.getOpenid(), shareRecord.getImgUrl());
        }
    }

    /**
     * 处理第三方集成过来的公众号token失效
     */
    @Override
    public void handleThirdWechatTokenLoseUse() {
        //先查询所有的有效公众号,且绑定过的直播间
        List<WechatOfficial> list = selectUseTokenList();
        for (WechatOfficial wechatOfficial : list) {
            Long loseUseTime = redisUtil.ttl(RedisKey.ll_live_appid_access_token_pre + wechatOfficial.getAppid());
            log.debug("appid = " + wechatOfficial.getAppid() + "loseUseTime = " + loseUseTime);
            if (!(loseUseTime != null && loseUseTime > WechatConst.ll_live_third_auth_wechat_access_token_min_use_time)) {
                //重新设置token
                updateAuthorizeAccessToken(wechatOfficial.getAppid());
            }
        }
    }

    @Override
    public List<Map> getWechatOfficialRoomListPage(DatagridRequestModel datagridRequestModel, Map map) {

        return wechatOfficialMapper.getWechatOfficialRoomListPage(datagridRequestModel, map);

    }

    @Override
    public void updateForWechatOfficialById(WechatOfficial wechatOfficial) {
        wechatOfficialMapper.updateForWechatOfficialById(wechatOfficial);
    }

    @Override
    public void updateAudit(WechatOfficial wechatOfficial) {
        wechatOfficialMapper.updateAudit(wechatOfficial);
    }

    @Override
    public void updateManager(WechatOfficial wechatOfficial) {
        wechatOfficialMapper.updateManager(wechatOfficial);
    }

    @Override
    public Boolean isWechatOfficial(Long liveRoomId) {
        if (liveRoomId == null) {
            return false;
        }

        //特殊处理，也能看到
        if (liveRoomId == 75L || liveRoomId == 613L || liveRoomId == 859 || liveRoomId == 105l) {
            return true;
        }
        WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomMapper.findByRoomId(liveRoomId);
        //是公众号
        if (wechatOfficialRoom != null) {
            Date time = wechatOfficialRoom.getFreeDate();
            if (time == null || time.compareTo(new Date()) < 0) {
                return true; //过期,需要充流量
            }
            return false;     //正常,不需要充流量
        } else {
            return false;     //没有对接第三方
        }
    }

    @Override
    public List<WechatOfficial> getAllWechat() {
        return wechatOfficialMapper.getAllWechat();
    }

    @Override
    public void updateFreeDate(long id) {
        wechatOfficialMapper.updateFreeDate(id);
    }

    /**
     * 生成直公众号二维码存入redis
     * @param qrCodeType
     * @param roomId
     * @param courseId
     * @param appId
     * @param channelId
     */
    @Override
    public  String getWechatOfficialQrCode(String qrCodeType, Long roomId, Long courseId, Long appId, Long channelId){
        String qrCode = "";
        String url ="";
        if (StringUtils.isEmpty(url)) {
            qrCode = weixinUtil.getParaQrcode(qrCodeType, roomId, courseId, appId, 0l);
            try {
                BufferedImage  bufferedImage = ImageIO.read(new URL(qrCode));
                ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();
                String path = UUIDGenerator.generate() + "_qrcode.png";
                ImageIO.write(bufferedImage, "jpg", outputStream);
                byte[] bytes = outputStream.toByteArray();
                url = ssoUtil.putObject(path, bytes);
            } catch (Exception e) {
                String tip = appId + ":" + roomId;
                log.error("二维码生成问题：{}" , tip , e);
                String errorKey = UUIDGenerator.generate();
                MobileGlobalExceptionHandler.sendMsg("二维码生成问题:" + tip , errorKey);
//                log.error("二维码缓存问题：{}" , tip , e);
//                String errorKey = UUIDGenerator.generate();
//                log.error("errorkey:{}" , errorKey);
//                MobileGlobalExceptionHandler.sendMsg("二维码缓存问题:" + tip , errorKey);
            }
        }
        return url;
    }

    /**
     * 获取解绑公众号解绑短信内容
     * @return
     */
    @Override
    public String getContentByWechat() {
        return wechatOfficialMapper.getContentByWechat();
    }

    @Override
    public WechatOfficial selectByAppid(String appId) {
        return wechatOfficialMapper.selectByAppid(appId);
    }

}
