package com.longlian.live.controller;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.huaxin.util.weixin.encryption.SHA1;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.ShareConst;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.JoinCourseRecordType;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.ThirdPayDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by syl on 2017/2/8.
 */
@Controller
@RequestMapping(value = "/")
public class H5Controller {

    private static Logger logger = LoggerFactory.getLogger(H5Controller.class);

    @Autowired
    AppUserService appUserService;
    @Autowired
    BankService bankService;
    @Autowired
    WithdrawalsService withdrawalsService;
    @Autowired
    VisitCourseRecordService visitCourseRecordService;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    UserDistributionService userDistributionService;
    @Autowired
    CourseService courseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WeiXinService weiXinService;
    @Autowired
    InviCodeService inviCodeService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    InviCodeItemService itemService;
    @Autowired
    LiveController liveController;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    UserFollowWechatOfficialService userFollowWechatOfficialService;
    @Autowired
    AppUserService userService;

    @Value("${wechat.token}")
    private String token;
    /**
     * 首页处理
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping()
    public ModelAndView home( HttpServletRequest request, HttpServletResponse response) throws Exception {
        return index( request,  response);
    }
    @Value("${index_title}")
    private String INDEX_TITLE;
    @RequestMapping(value = "weixin")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/index");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        result.addObject("indexTitle",INDEX_TITLE);
        return result;
    }


    @RequestMapping(value = "weixin/toLogin")
    public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/loginIn");
        String toUrl = Utility.null2Empty(request.getParameter("toUrl"));
        result.addObject("toUrl" , toUrl);
        return result;
    }

    /**
     * 微信页面登录
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "weixin/login")
    @ResponseBody
    public ActResultDto login(HttpServletRequest request, HttpServletResponse response, AppUser appUser) throws Exception {
        String md5 = MD5PassEncrypt.getMD5Str(appUser.getPassword());
        appUser.setPassword(md5);
        ActResultDto result = appUserService.loginIn(appUser, "WEIXIN");
        AppUserIdentity identity = (AppUserIdentity) result.getData();
        if (identity != null) {
            response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        }
        return result;
    }

    /**
     * 处理微信登录
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public AppUserIdentity handlerWeixinLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //AppUserIdentity identity = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);//获取用户信息
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (identity == null) {
            logger.info("handlerWeixinLogin  identity--->" + identity);
            WeixinAppUser weixinAppUser = (WeixinAppUser) request.getAttribute(CecurityConst.ll_live_weixin_app_user);
            if (weixinAppUser != null) {
                String nickname = Utility.getCheckNum(weixinAppUser.getNickname());
                weixinAppUser.setNickname(nickname);
                long invitationAppId = Utility.parseLong(request.getParameter("invitationAppId"));
                //System.out.println("接受---" + invitationAppId);
                ActResultDto resultDto = appUserService.weixinLogin(weixinAppUser, invitationAppId , "weixin");
                identity = (AppUserIdentity) resultDto.getData();
                if (identity != null) {
                    //处理cookie
                    response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
                }
            }
        }
        return identity;
    }

    /**
     * 获取微信getJSAPITicket 签名
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "weixin/getJSAPITicketSignature")
    @ResponseBody
    public ActResultDto getJSAPITicketSignature(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        String nonce = UUIDGenerator.generate();
        long timestamp = new Date().getTime() / 1000;
        String signature = weixinUtil.getJSApiSignature(nonce, timestamp + "", url);
        String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        Map map = new HashMap();
        map.put("appId", appid);
        map.put("timestamp", timestamp + "");
        map.put("nonceStr", nonce);
        map.put("signature", signature);
        resultDto.setData(map);
        return resultDto;
    }
    /**
     * 服务号接入身份校验
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("weixin/signatureIdentityCheck")
    public void signatureIdentityCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String signature = request.getParameter("signature");  // 微信加密签名
        String timestamp = request.getParameter("timestamp");       // 时间戳
        String nonce = request.getParameter("nonce");  // 随机数
        String echostr = Utility.getString(request.getParameter("echostr"));   // 随机字符串
        //加密
        String mytoken = SHA1.getSHA11(token, timestamp, nonce);
        //校验签名
        System.out.println(signature + ":" + timestamp + ":" + nonce + ":" + echostr + "-------mytoken------>" + mytoken + "------->" + signature);
        if (mytoken != null && mytoken != "" && mytoken.equals(signature)) {
            System.out.println("签名校验通过。");
            response.getWriter().println(echostr); //如果检验成功输出echostr，微信服务器接收到此输出，才会确认检验完成。
        } else {
            System.out.println("签名校验失败。");
        }
    }
    /**
     * 适用于微信
     * 分享
     *
     * @param invitationCode 邀请码
     * @param photoUrl       图片地址
     * @return
     */
    @RequestMapping(value = "share", method = RequestMethod.GET)
    @ResponseBody
    public ActResultDto share(HttpServletRequest request, String invitationCode,String isSeries  ,Long seriesid ,
                              String systemType, String roomIdOrCouseId, String photoUrl, String channel) {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取当前登录用户
        ActResultDto actResultDto = new ActResultDto();
        HashMap map = new HashMap();
        map.put("title", ShareConst.share_title);
        map.put("content", ShareConst.share_content);
        if(StringUtils.isEmpty(roomIdOrCouseId)) roomIdOrCouseId = "0";
        //课邀请卡，或者链接分享时
        if("COURSE_FIXED".equals(systemType)){systemType = "COURSE";}/*健身的课程专用*/
        if ("COURSE_INVI_CARD".equals(systemType) ||
                "COURSE".equals(systemType)) {
            Map course = courseService.getShareCourseTitle(Long.valueOf(roomIdOrCouseId));
            if (course != null) {
                isSeries = String.valueOf(course.get("isSeries"));
                BigDecimal amount = new BigDecimal(0);
                if(course.get("chartAmount") != null){
                    amount = new BigDecimal(String.valueOf(course.get("chartAmount")));
                }
                if("1".equals(isSeries) && amount.compareTo(new BigDecimal(0))>0){   //系列课
                    roomIdOrCouseId = String.valueOf(course.get("id"));
                    seriesid = 0l;
                }
                map.put("photoUrl", course.get("coverssAddress") + "?x-oss-process=style/share");//分享图片压缩
                map.put("title", String.valueOf(course.get("title")));
                /*map.put("content", String.valueOf(course.get("remark")));
                if(StringUtils.isEmpty(isSeries)){
                    map.put("content", ShareConst.defult_share_content);
                }*/
                map.put("content", ShareConst.defult_share_content);
                LiveRoom liveRoom = liveRoomService.findById(Long.valueOf(course.get("roomId").toString()));
                if(liveRoom!=null && liveRoom.getRemark()!= null && !"".equals(liveRoom.getRemark()) ){
                    map.put("content", liveRoom.getRemark());
                }
            }
        }else if("CREATR_LIVE_ROOM".equals(systemType)){    //邀请创建直播间分享
            LiveRoom liveRoom = liveRoomService.findById(Long.valueOf(roomIdOrCouseId));
            if(liveRoom!=null){
                map.put("photoUrl",liveRoom.getCoverssAddress());
            }
            map.put("title", ShareConst.share_title);
            map.put("content", ShareConst.share_content);
        }else if("LIVE_ROOM".equals(systemType)){
            map.put("title", ShareConst.share_title);
            map.put("content", ShareConst.share_content);
            LiveRoom liveRoom = liveRoomService.findById(Long.valueOf(roomIdOrCouseId));
            if(liveRoom!=null){
                map.put("photoUrl", liveRoom.getCoverssAddress());
                map.put("content", liveRoom.getRemark());
                map.put("title", liveRoom.getName());
            }
        }
        String shareAddress = "";
        String  invitationAppId  = "";//邀请人ID
        if (token != null) {
            invitationAppId  = token.getId() + "";
        } else {
            invitationAppId  = invitationCode;
        }
        shareAddress = SystemUtil.getShareAddress(invitationAppId , systemType,isSeries,seriesid,
                roomIdOrCouseId, photoUrl,channel);
        System.out.println("shareAddress-->" + shareAddress);
        map.put("shareAddress", shareAddress);
        map.put("inviAppId", invitationAppId);
        actResultDto.setData(map);
        return actResultDto;
    }


    /**
     * 个人中心-直播间后台
     */
    @RequestMapping(value = "weixin/liveBackstage.user")
    public ModelAndView liveBackstage(Long id) throws Exception {
//        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/liveBackstage");
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/new_liveBackstage");
        result.addObject("id", id);//直播间id
        return result;
    }

    /**
     * 个人中心-数据统计(单节课列表)
     */
    @RequestMapping(value = "weixin/dataTotal.user")
    public ModelAndView dataTotal(Long id) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/dataTotal");
        result.addObject("id", id);//聊天室id
        return result;
    }

    /**
     * 个人中心-直播间全部关注人
     */
    @RequestMapping(value = "weixin/userFollow")
    public ModelAndView userFollow(Long id) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/userFollow");
        result.addObject("id", id);//直播间ID
        return result;
    }

    /**
     * 第三方公众账号入口
     * @param request
     * @param response
     * @param liveRoomNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "weixin/oauth2")
    public ModelAndView liveRoom(HttpServletRequest request, HttpServletResponse response ,String liveRoomNo ) throws Exception {
        if(!Utility.isNullorEmpty(liveRoomNo)){//可能从微信公众号过来的
            Long id = liveRoomService.getLiveRoomId(liveRoomNo);
            if(id != null && id >= 1) return  liveRoom( request,  response, id , "");
        }
        return new ModelAndView("/func/weixin/personalCenter/liveRoomnotexist");
    }

    /**
     * 个人中心-老师的直播间
     * @param request
     * @param response
     * @param id  直播间ID
     * @param sourseId 来源类型 不为空-分销（1-详情页我要开课 2-直播间邀请好友） 空-正常跳转
     * @return
     * @throws Exception`
     */
    @RequestMapping(value = "weixin/liveRoom")
    public ModelAndView liveRoom(HttpServletRequest request, HttpServletResponse response, Long id,
                                 String sourseId  ) throws Exception {
        String url = "/func/weixin/personalCenter/new_liveRoom";
        ModelAndView result = new ModelAndView(url);
        result.addObject("tId", 0);
        AppUserIdentity identity = handlerWeixinLogin(request, response);

        logger.info("liveRoom  identity--->" + identity);
        LiveRoom liveRoom = null;
        if(identity != null){
            liveRoom = liveRoomService.findByAppId(identity.getId());
            if(liveRoom != null){
                result.addObject("tId", liveRoom.getAppId());
            }
        }
        boolean isCourseFollow =false;
        if (identity != null) {
            isCourseFollow= userFollowService.isFollowRoom(id, identity.getId());//判断直播间是否关注过
        }

        result.addObject("isFollow", isCourseFollow);
        String followId = request.getParameter("followId");
        if (!Utility.isNullorEmpty(identity) && !Utility.isNullorEmpty(followId) && Long.parseLong(followId) > 0) {
            UserFollowWechatOfficial userFollow = userFollowWechatOfficialService.findUserFollowById(Long.parseLong(followId));
            if (!Utility.isNullorEmpty(userFollow)) {
                if (0 == userFollow.getAppId() || Utility.isNullorEmpty(userFollow.getAppId())) {
                    userFollowWechatOfficialService.updateAppIdByFollowId(identity.getId(), Long.parseLong(followId));
                }
            }
        }
        if (identity != null) {
            //处理是否需要自动关注
            String isAutoFollow = request.getParameter("isAutoFollow") == null ? "" : request.getParameter("isAutoFollow");//
            logger.info("处理是否需要自动关注 isAutoFollow:"+isAutoFollow+"  id:"+id);
            if ("1".equals(isAutoFollow) && id != null && id > 0) {//第三方授权的公众号
                logger.info("开始处理是否需要自动关注");
                String thirdWechatAppid = request.getParameter("thirdWechatAppid") == null ? "" : request.getParameter("thirdWechatAppid");
                String thirdOpenid = request.getParameter("thirdOpenid") == null ? "" : request.getParameter("thirdOpenid");
//                userFollowService.follow(identity.getId(), id  ,thirdOpenid , thirdWechatAppid );
                logger.info("结束处理是否需要自动关注");
            }
            if (!Utility.isNullorEmpty(sourseId)) {
                if (liveRoom != null) {
                    result.addObject("isTeacher", liveRoom.getAppId() == identity.getId() ? "1" : "0");//是否是老师自己
                }

                if (liveRoom == null) {
                    return createLiveRoom(0l);
                } else if ("2".equals(liveRoom.getStatus())) {
                    return createLiveRoom(liveRoom.getId());
                } else if ("0".equals(liveRoom.getStatus())) {
                    return personalCenter("" , request , response);
                } else {
                    id = liveRoom.getId();
                }

            }
        }
        result.addObject("id", id);//直播间id
        return result;
    }

    /**
     * 个人中心-课程统计详情
     */
    @RequestMapping(value = "weixin/courseStatistics.user")
    public ModelAndView courseStatistics(Long id) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/courseStatistics");
        result.addObject("id", id);//课程id
        return result;
    }

    /**
     * 个人中心首页
     */
    @RequestMapping(value = "weixin/personalCenter")
    public ModelAndView personalCenter(String fromWeixinMsg , HttpServletRequest request , HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/new_index");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        result.addObject("fromWeixinMsg",fromWeixinMsg);
        return result;
    }

    /**
     * 个人中心-单节课收益
     */
    @RequestMapping(value = "weixin/singleCourseIncome")
    public ModelAndView singleCourseIncome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/singleCourseIncome");
        return result;
    }


    /**
     * 个人中心-创建直播间
     */
    @RequestMapping(value = "weixin/createLiveRoom")
    public ModelAndView createLiveRoom(Long id) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/createLiveRoom");
        result.addObject("id", id);
        return result;
    }

    /**
     * 个人中心-创建单节课
     */
    @RequestMapping(value = "weixin/createSingleCourse")
    public ModelAndView createSingleCourse(Long id ,HttpServletRequest request , HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/createSingleCourse");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        if(identity!=null){
            if(id == null || id == 0){
                //查找老师是否存在直播间
                LiveRoom liveRoom =  liveRoomService.findByAppId(identity.getId());
                if(liveRoom == null ){
                    return  createLiveRoom( id);
                }else{
                    id = liveRoom.getId();
                }
            }
        }
        result.addObject("roomId", id);//直播间id
        return result;
    }

    /**
     * 个人中心-创建单节课
     */
    @RequestMapping(value = "weixin/createSingleCourse.user")
    public ModelAndView createSingleCourseUser(Long id ,HttpServletRequest request , HttpServletResponse response) throws Exception {
        return  createSingleCourse(  id ,  request ,   response) ;
    }

    /**
     * 个人中心-直播间设置
     */
    @RequestMapping(value = "weixin/liveRoomSet")
    public ModelAndView liveRoomSet(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
        ModelAndView result = new ModelAndView("/func/weixin/personalCenter/liveRoomSet");
        result.addObject("id", id);//直播间id
        return result;
    }

    /**
     * 我的邀请卡
     *
     * @param roomId 分享roomId 直播间ID
     * @return
     */
    @RequestMapping(value = "weixin/inviCard")
    public ModelAndView myInviCard(Long roomId, Long courseId,
                                   Long appId , String type) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/inviCard/index");
        if (roomId == null) {
            roomId = 0l;
        }
        if (courseId == null) {
            courseId = 0l;
        }
        if (appId == null) {
            appId = 0l;
        }
        view.addObject("type",type);
        view.addObject("roomId", roomId);
        view.addObject("courseId", courseId);
        view.addObject("appId", appId);
        return view;
    }

    /**
     * 我的邀请卡---分享的邀请卡
     *
     * @param shareInviCardUrl 分享邀请卡的地址
     * @param invitationAppId  分享appId
     * @return
     * @fromType 分享类型 1-邀请卡 空-直播间
     */
    @RequestMapping(value = "weixin/shareInviCard")
    public ModelAndView myInviCard(String shareInviCardUrl, String invitationAppId, Long roomIdOrCourseId,
                                   HttpServletRequest request, HttpServletResponse response, String fromType) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/inviCard/share");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        if (identity != null) {
            long invitationAppIdL = Utility.parseLong(invitationAppId);
            //处理用户分销
            if (StringUtils.isEmpty(fromType)) {
                // userDistributionService.addUserDistributionByRoomId(roomIdOrCourseId, identity.getId(), invitationAppIdL);
            }
        }
        view.addObject("shareInviCardUrl", shareInviCardUrl);
        return view;
    }


    /**
     * 课程详情
     *
     * @param id              课程ID(如果是系列课则是系列课ID)
     * @param invitationAppId 邀请人ID
     * @param isSeries 是否系列课1-是， null或者0不是
     * @param channel 渠道
     * @param isMustShare 是否强制分享
     * @return
     * @fromType 分享类型 1-邀请卡 空-直播间
     *
     */
    @RequestMapping(value = "weixin/courseInfo")
    public ModelAndView courseInfo(HttpServletRequest request, HttpServletResponse response,
                                   long id, String invitationAppId, String fromType, String isSeries , Long channel , String isMustShare ) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/course/new_courseInfo");

        AppUserIdentity identity = handlerWeixinLogin(request, response);
        if(identity==null||identity.getToken()==null){
            view = new ModelAndView("/func/weixin/loginIn");
            return view;
        }
        String followId = request.getParameter("followId");
        if (!Utility.isNullorEmpty(identity) && !Utility.isNullorEmpty(followId) && Long.parseLong(followId) > 0) {
            UserFollowWechatOfficial userFollow = userFollowWechatOfficialService.findUserFollowById(Long.parseLong(followId));
            if (!Utility.isNullorEmpty(userFollow)) {
                if (0 == userFollow.getAppId() || Utility.isNullorEmpty(userFollow.getAppId())) {
                    userFollowWechatOfficialService.updateAppIdByFollowId(identity.getId(), Long.parseLong(followId));
                }
            }
        }


        long idL = 0;
        Course course =new Course();
        if(id!=0 && String.valueOf(id).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelay = courseRelayService.queryById(id);
            if (courseRelay == null) {
                return notFound(null);
            }
            idL=courseRelay.getOriCourseId();
            course=courseService.getRelayCourse(id);
        }else{
            idL = Long.valueOf(id);
            course = courseService.getCourseFromRedis(idL);
        }
        if (course == null) {
            return notFound(null);
        }
        view.addObject("course" , course);
        Long  seriesid = course.getSeriesCourseId();
        if ("1".equals(isSeries) || "1".equals(course.getIsSeriesCourse())) {
            view = new ModelAndView("/func/weixin/course/seriesofLessons");
        }
        if (seriesid == null) {
            seriesid = 0l;
        }
        if (seriesid != null){
            request.setAttribute("seriesid", seriesid);
            //如果是系列课下面的单节课，所属系列课已经下架，则提示下架
            if (seriesid > 0 ) {
                Course seriesCourse = courseService.getCourseFromRedis(seriesid);
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(seriesCourse.getIsDelete())) {
                    return notFound(course.getLiveTopic());
                }
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(seriesCourse.getStatus())) {
                    return courseDown(course.getLiveTopic());
                }
            }
        }


        //如果已经删除，则跳到提示已删除页面
        if ("1".equals(course.getIsDelete())) {
            return notFound(course.getLiveTopic());
        }

        //如果已经下架，则跳到提示已下架页面
        if ("1".equals(course.getStatus()) ) {
            String isFromMyBuy = request.getParameter("isFrom");
            //如果不是从我购买的课里进入的，如果已下架，则提示下架
            //if (!"1".equals(isFromMyBuy))
            return courseDown(course.getLiveTopic());
        }
        //如果该课程是免费的,则分享的时候点击该链接,直接跳转到聊天室 且不是系列课的时
        if(course.getChargeAmt() == null){
            course.setChargeAmt(new BigDecimal(0));
        }
        BigDecimal courseAmount = course.getChargeAmt();
        if (identity != null) {
            if (course.getAppId() == identity.getId()) {//自己进自己的直播间 跳转
                if ("1".equals(isSeries)) {

                    if(id!=0 && String.valueOf(id).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                        return teacherSeries(request,response,id);
                    }else{
                        return teacherSeries(request, response,idL);
                    }
                } else {
                    if(id!=0 && String.valueOf(id).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                        return teacherRelayCourse(id,seriesid);
                    }else{
                        return teachercourse(idL,seriesid);
                    }

                }
            }
            int isSuperAdmin = userService.findSystemAdminByUserId(identity.getId());
            if(isSuperAdmin <= 0){  //超级管理员不算
                //有渠道标志
                if (channel != null && channel != 0) {
                    ChannelVisitRecord visitRecord = new ChannelVisitRecord();
                    visitRecord.setAppId(identity.getId());
                    visitRecord.setChannelId(channel);
                    visitRecord.setCourseId(idL);
                    visitRecord.setCreateTime(new Date());
                    //如果是新用户
                    if (identity.isNewUser) {
                        visitRecord.setIsNewUser("1");
                    } else {
                        visitRecord.setIsNewUser("0");
                    }
                    view.addObject("channel", channel);
                    redisUtil.lpush(RedisKey.ll_channel_visit_record_wait2db , JsonUtil.toJson(visitRecord));
                }
                String wechatShareType = request.getParameter("wechatShareType");
                //课程id修改为实际ID（转播课ID就是转播课ID）
                visitCourseRecordService.insertRecord(identity.getId(), id, Utility.parseLong(invitationAppId), fromType, seriesid , wechatShareType);
            }
        }
        if (!"1".equals(isMustShare)) {
            if(seriesid > 0){
                Course c = courseService.getCourseFromRedis(seriesid);
                courseAmount = c.getChargeAmt() == null ? new BigDecimal(0) : c.getChargeAmt();
            }
            if (courseAmount.compareTo(new BigDecimal(0))<=0
                    && !StringUtils.isEmpty(invitationAppId)
                    && identity != null  && "0".equals(course.getIsSeriesCourse())){
                request.setAttribute(CecurityConst.REQUEST_USER_ATTR,identity);
                return liveController.live(request , response,id,invitationAppId,"1",seriesid);
            }
        } else {
            view.addObject("isMustShare", isMustShare);//是否是强制分享
            if (identity != null) {
                Map result = userFollowWechatOfficialService.getUserFollowInfoByCourse( course.getId(),identity.getId() , course.getRoomId() , course.getAppId(),course.getLiveWay());
                //是否已经关注
                view.addObject("isFollowThirdOfficial", result.get("isFollowThirdOfficial"));
            }

        }

        //是系列课
        view.addObject("invitationAppId", invitationAppId);//邀请人
        request.setAttribute("courseId", id);

        view.addObject("seriesid", seriesid);
        return view;
    }

    /**
     * 分销达人排行榜
     */
    @RequestMapping(value = "weixin/toShare")
    public ModelAndView toShare(HttpServletRequest request, HttpServletResponse response, long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/share/toShare");
        view.addObject("id", id);//课程id
        return view;
    }


    /**
     * 系统消息
     */
    @RequestMapping(value = "weixin/systemMessage")
    public ModelAndView systemMessage() throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/systemMessage/Systemmessage");
        return view;
    }

    /**
     * 老师设置空间
     *
     * @return
     */
    @RequestMapping(value = "weixin/directBroadcast")
    public ModelAndView directBroadcast(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/live/directBroadcast");
        view.addObject("courseId", id);//课程Id
        return view;
    }

    /**
     * 课程的全部评价
     *
     * @return
     */
    @RequestMapping(value = "weixin/curriCulum")
    public ModelAndView curriCulum(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/course/curriCulum");
        view.addObject("courseId", id);
        return view;
    }

    /**
     * 我关注的直播间列表
     *
     * @return
     */
    @RequestMapping(value = "weixin/report")
    public ModelAndView report() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/new_report");
        return view;
    }

    /**
     * 我的钱包
     *
     * @return
     */
    @RequestMapping(value = "weixin/wallet")
    public ModelAndView wallet() {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/wallet");
        return view;
    }

    /**
     * 举报
     *
     * @return
     */
    @RequestMapping(value = "weixin/complaint")
    public ModelAndView complaint(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/complaint");
        view.addObject("id", id);
        return view;
    }

    /**
     * 评论
     *
     * @return
     */
    @RequestMapping(value = "weixin/submitComment")
    public ModelAndView submitComment(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/submitComment");
        Course course = courseService.getCourse(id);
        if(course.getSeriesCourseId()>0)
        {
            view.addObject("seriesid", course.getSeriesCourseId());
        }
        view.addObject("courseId", id);
        return view;
    }

    /**
     * 我的直播间收益
     *
     * @return
     */
    @RequestMapping(value = "weixin/myLiveIncome")
    public ModelAndView myLiveIncome(String mylivmoney) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/myLiveIncome");
        if (StringUtils.isEmpty(mylivmoney)) mylivmoney = "0";
        view.addObject("mylivmoney", mylivmoney);
        return view;
    }

    /**
     * 分销收益
     *
     * @return
     */
    @RequestMapping(value = "weixin/disIncome")
    public ModelAndView disIncome(String disIncome) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/disIncome");
        if (StringUtils.isEmpty(disIncome)) disIncome = "0";
        view.addObject("disIncome", disIncome);
        return view;
    }

    /**
     * 平台收益
     *
     * @return
     */
    @RequestMapping(value = "weixin/platIncome")
    public ModelAndView platIncome(String platIncome) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/platIncome");
        if (StringUtils.isEmpty(platIncome)) platIncome = "0";
        view.addObject("platIncome", platIncome);
        return view;
    }

    /**
     * 代理收益
     *
     * @return
     */
    @RequestMapping(value = "weixin/proxyIncome")
    public ModelAndView proxyIncome(String proxyIncome) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/proxyIncome");
        if (StringUtils.isEmpty(proxyIncome)) proxyIncome = "0";
        view.addObject("proxyIncome", proxyIncome);
        return view;
    }

    /**
     * 系统消息详情
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "weixin/detailsMess")
    public ModelAndView detailsMess(String type) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/systemMessage/detailsMess");
        view.addObject("msg", type);
        return view;
    }

    /**
     * 系统消息详情
     *
     * @param msg
     * @return
     */
    @RequestMapping(value = "weixin/messageDetails")
    public ModelAndView messageDetails(String msg) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/systemMessage/Messagedetails");
        view.addObject("msg", msg);
        return view;
    }

    /**
     * 已购买课程列表
     */
    @RequestMapping(value = "weixin/courseBuy")
    public ModelAndView courseBuy() throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/course/coursesBuy");
        return view;
    }

    /**
     * 全体成员
     *
     * @return
     */
    @RequestMapping(value = "weixin/member")
    public ModelAndView member(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/member");
        view.addObject("courseId", id);
        return view;
    }

    /**
     * 讲师进入课程介绍页展示
     *
     * @return
     */
    @RequestMapping(value = "weixin/teachercourse")
    public ModelAndView teachercourse(Long id,Long seriesid) {
        ModelAndView view = new ModelAndView("/func/weixin/course/new_teachercourse");
        if(id != null && id > 0){
            Course course = courseService.getCourseFromRedis(id);
            if(course!=null){
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(course.getStatus())) {
                    return courseDown(course.getLiveTopic());
                }
                if("1".equals(course.getIsDelete())){
                    return notFound(course.getLiveTopic());
                }
            }
        }
        if(seriesid != null && seriesid > 0){
            Course course = courseService.getCourseFromRedis(seriesid);
            if(course!=null){
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(course.getStatus())) {
                    return courseDown(course.getLiveTopic());
                }
                if("1".equals(course.getIsDelete())){
                    return notFound(course.getLiveTopic());
                }
            }
        }
        view.addObject("courseId", id);
        view.addObject("seriesid", seriesid);
        return view;
    }


    /**
     * 讲师进入课程介绍页展示   转播课
     *
     * @return
     */
    @RequestMapping(value = "weixin/teacherRelayCourse")
    public ModelAndView teacherRelayCourse(Long id,Long seriesid) {
        ModelAndView view = new ModelAndView("/func/weixin/course/new_teachercourse");
        if(id != null && id > 0){
            Course course = courseService.getRelayCourse(id);
            if(course!=null){
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(course.getStatus())) {
                    return courseDown(course.getLiveTopic());
                }
                if("1".equals(course.getIsDelete())){
                    return notFound(course.getLiveTopic());
                }
            }
        }
        if(seriesid != null && seriesid > 0){
            Course course = courseService.getCourseFromRedis(seriesid);
            if(course!=null){
                //如果已经删除，则跳到提示已删除页面
                if ("1".equals(course.getStatus())) {
                    return courseDown(course.getLiveTopic());
                }
                if("1".equals(course.getIsDelete())){
                    return notFound(course.getLiveTopic());
                }
            }
        }
        view.addObject("courseId", id);
        view.addObject("seriesid", seriesid);
        return view;
    }

    /**
     * 讲师介绍页编辑
     *
     * @return
     */
    @RequestMapping(value = "weixin/evaluation")
    public ModelAndView evaluation(Long id) {
        ModelAndView view = new ModelAndView("/func/weixin/course/evaluation");
        view.addObject("courseId", id);
        return view;
    }

    /**
     * 我要提现
     *
     * @return
     */
    @RequestMapping(value = "weixin/myYention")
    public ModelAndView myYention() {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/myYention");
        return view;
    }

    /**
     * 银行卡管理
     *  isProxy 0:没有1:是
     * @return
     */
    @RequestMapping(value = "weixin/bankCard_manage")
    public ModelAndView bankCard_manage(String type,String cash,String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/bankCard_manage");
        view.addObject("type", type==null?"":type);
        view.addObject("cash", cash==null?"":cash);
        view.addObject("isProxy", isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 新增银行卡
     *
     * @return
     */
    @RequestMapping(value = "weixin/add_bankCard")
    public ModelAndView addBankCard(Long bankId, String userName,String cash,String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/add_bankCard");
        view.addObject("url", "");
        view.addObject("id", 0);
        view.addObject("name", "请选择银行");
        if (bankId != null && bankId > 0) {
            Bank bank = bankService.getBankInfo(bankId);
            view.addObject("url", bank.getPicAddress());
            view.addObject("id", bank.getId());
            view.addObject("name", bank.getName());
        }
        if (StringUtils.isEmpty(userName)) {
            view.addObject("userName", "");
        } else {
            view.addObject("userName", userName);
        }
        view.addObject("cash", cash==null?"":cash);
        view.addObject("isProxy", isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 选择银行卡
     *
     * @return
     */
    @RequestMapping(value = "weixin/selectedBank")
    public ModelAndView selectedBank(String userName,String cash,String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/selectedBank");
        if (!StringUtils.isEmpty(userName)) {
            view.addObject("userName", userName);
        } else {
            view.addObject("userName", "");
        }
        view.addObject("cash",cash==null?"":cash);
        view.addObject("isProxy",isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 银行卡提现
     *  isProxy 0:没有代理 1:代理
     * @return
     */
    @RequestMapping(value = "weixin/carryCash.user")
    public ModelAndView carryCash(String bankCardId, HttpServletRequest request , String type , String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/carryCash");
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = withdrawalsService.findAccountBalance(token.getId(), bankCardId);
        if ("000000".equals(resultDto.getCode())) {
            view.addObject("data", resultDto.getData());
        }
        view.addObject("appId", token.getId());
        view.addObject("bankCardId", bankCardId==null?'0':bankCardId);
        view.addObject("type", type==null?"":type);
        view.addObject("isProxy", isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 银行卡明细
     *
     * @return
     */
    @RequestMapping(value = "weixin/detailsMoneyPage")
    public ModelAndView detailsMoneyPage() {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/detailsMoneyPage");
        return view;
    }

    /**
     * 银行卡详情
     *
     * @return
     */
    @RequestMapping(value = "weixin/bankCard_details")
    public ModelAndView bankCard_details(String bankCardId,String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/bankCard_details");
        view.addObject("bankCardId", bankCardId==null?'0':bankCardId);
        view.addObject("isProxy", isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 设置密码界面
     *
     * @return
     */
    @RequestMapping(value = "weixin/toSetPwd")
    public ModelAndView toSetPwd(String type,String courseId,String isProxy) {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/dealPwdPage");
        view.addObject("type",type);
        view.addObject("courseId",courseId);
        view.addObject("isProxy",isProxy==null?"":isProxy);
        return view;
    }

    /**
     * 重置密码界面
     *
     * @return
     */
    @RequestMapping(value = "weixin/toResetPwd")
    public String toResetPwd() {
        return "/func/weixin/myBank/reset_trade_pwd";
    }

    /**
     * 学币明细
     *
     * @return
     */
    @RequestMapping(value = "weixin/toDetailsMoneyList")
    public ModelAndView toDetailsMoneyList() {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/detailsMoneyPage");
        return view;
    }

    /**
     * 钱包明细
     *
     * @return
     */
    @RequestMapping(value = "weixin/toMoneyList")
    public ModelAndView toMoneyList() {
        ModelAndView view = new ModelAndView("/func/weixin/myBank/toMoneyPage");
        return view;
    }

    /**
     * 关于我们
     *
     * @return
     */
    @Value("${version_no}")
    private String VERSION_NO;
    @RequestMapping(value = "weixin/aboutUs")
    public ModelAndView aboutUs() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/aboutUs");
        view.addObject("versionNo",VERSION_NO);
        return view;
    }

    /**
     * 福利
     *
     * @return
     */
    @RequestMapping(value = "weixin/welfareIndex")
    public ModelAndView welfareIndex(String type) {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/welfareIndex");
        view.addObject("type", type);
        return view;
    }

    /**
     * 规则
     *
     * @return
     */
    @RequestMapping(value = "weixin/rules")
    public ModelAndView rules() {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/rules");
        return view;
    }

    /**
     * 招募
     *
     * @return
     */
    @RequestMapping(value = "weixin/recruit")
    public ModelAndView recruit() {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/recruit");
        return view;
    }

    /**
     * 粉丝
     *
     * @return
     */
    @RequestMapping(value = "weixin/fans")
    public ModelAndView fans() {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/fans");
        return view;
    }

    /**
     * 直播间奖励
     *
     * @return
     */
    @RequestMapping(value = "weixin/livereward")
    public ModelAndView livereward() {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/livereward");
        return view;
    }

    /**
     * 更多课程 (正在直播、预告)
     *
     * @return
     */
    @RequestMapping(value = "weixin/moreClass")
    public ModelAndView moreClass(Long liveStatus, Long courseTypeId) {
        ModelAndView view = new ModelAndView("/func/weixin/moreClass");
        view.addObject("liveStatus", liveStatus);//0：正在直播 1：直播·预告
        view.addObject("courseTypeId", courseTypeId);//课程类型ID
        return view;
    }

    /**
     * 课程分类导航--更多课程
     *
     * @return
     */
    @RequestMapping(value = "weixin/navIgation")
    public ModelAndView navIgation(Long id, String name) {
        ModelAndView view = new ModelAndView("/func/weixin/navIgation");
        view.addObject("courseTypeId", id);
        view.addObject("courseTypeName", name);
        return view;
    }

    /**
     * 发送验证码页面
     * 如果isporxy有值,则为提现页面转过来.需设置好交易密码之后在跳转到提现页面
     *
     * @return
     */
    @RequestMapping(value = "weixin/forgetTradePwd.user")
    public ModelAndView forgetTradePwd(HttpServletRequest request,String isProxy) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView view = new ModelAndView("/func/weixin/myBank/send_code");
        view.addObject("mobile", token.getMobile());
        view.addObject("isProxy", isProxy == null ?"":isProxy);
        return view;
    }
    /**
     *新个人中心
     *
     * @return
     */
    @RequestMapping(value = "weixin/new_index")
    public ModelAndView new_index(String fromWeixinMsg ) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/new_index");
        view.addObject("fromWeixinMsg",fromWeixinMsg);
        return view;
    }
    /**
     *充值
     *
     * @return
     */
    @RequestMapping(value = "weixin/recharge")
    public ModelAndView recharge() {
        ModelAndView view = new ModelAndView("/func/weixin/learncoin/recharge");
        return view;
    }
    /**
     *学币账户
     *
     * @return
     */
    @RequestMapping(value = "weixin/new_learncoinAccount")
    public ModelAndView learncoinAccount(HttpServletRequest request, HttpServletResponse response , Long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/learncoin/new_learncoinAccount");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        view.addObject("id",id);
        return view;
    }
    /**
     *课程收益详情
     *
     * @return
     */
    @RequestMapping(value = "weixin/courseRevenuedetails")
    public ModelAndView courseRevenuedetails(Long courseId) {
        ModelAndView view = new ModelAndView("/func/weixin/course/courseRevenuedetails");
        view.addObject("courseId",courseId);
        return view;
    }
    /* *
      *意见反馈
      *
      * @return
      */
    @RequestMapping(value = "weixin/feedBack")
    public ModelAndView feedBack() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/feedBack");
        return view;
    }
    /**
     *新设置页面
     *
     * @return
     */
    @RequestMapping(value = "weixin/setUp")
    public ModelAndView setUp(String mobile) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/setUp");
        view.addObject("mobile",mobile);
        return view;
    }
    /**
     *新课程创建完成
     *
     * @return
     */
    @RequestMapping(value = "weixin/createGood")
    public ModelAndView createGood(Long courseId , Long seriesCourseId) {
        ModelAndView view = new ModelAndView("/func/weixin/course/createGood");
        view.addObject("courseId",courseId);
        view.addObject("seriesCourseId",seriesCourseId);
        return view;
    }
    /**
     *我的打赏收益
     *
     * @return
     */
    @RequestMapping(value = "weixin/reward")
    public ModelAndView reward() {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/myreward");
        return view;
    }
    /*
    *打赏收益详情
    *
    * @return
    */
    @RequestMapping(value = "weixin/rewardFordetails")
    public ModelAndView rewardFordetails(Long courseId) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/rewardFordetails");
        view.addObject("courseId",courseId);
        return view;
    }

    @RequestMapping(value = "weixin/editSeriesCourse")
    public ModelAndView editSeriesCourse(Long seriesid) {
        ModelAndView view = new ModelAndView("/func/weixin/course/editSeriesCourse");
        view.addObject("seriesid",seriesid);
        return view;
    }

    /*
   *
   *
   *创建系列课页面
   * @return
   */
    @RequestMapping(value = "weixin/createSeriesCourse")
    public ModelAndView createSeriesCourse(Long roomId) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/createSeriesCourse");
        view.addObject("roomId",roomId);
        return view;
    }

    /*
      *
      *创建单节课页面
      * @return
      */
    @RequestMapping(value = "weixin/createSerieSingleCourse")
    public ModelAndView createSerieSingleCourse(Long seriesid,Long roomId) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/createSeriesSingleCourse");
        view.addObject("seriesid", seriesid);
        view.addObject("roomId",roomId);
        return view;
    }
    /*
     *
     *流量充值
     * @return
     */
    @RequestMapping(value = "weixin/flowRecharge")
    public ModelAndView flowRecharge() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/flowRecharge");
        return view;
    }
    /*
    *
    *流量充值记录
    * @return
    */
    @RequestMapping(value = "weixin/rechargeRecord")
    public ModelAndView rechargeRecord() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/rechargeRecord");
        return view;
    }
    /*
    *
    *流量消费记录
    * @return
    */
    @RequestMapping(value = "weixin/consumeRecord")
    public ModelAndView consumeRecord() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/consumeRecord");
        return view;
    }


    /*
         *
         *编辑系列课单节课单节课页面
         * @return
         */
    @RequestMapping(value = "weixin/editSeriesSingleCourse")
    public ModelAndView editSeriesSingleCourse(Long courseId ,Long seriesid ) {
        ModelAndView view = new ModelAndView("/func/weixin/course/editSeriesSingleCourse");
        view.addObject("courseId", courseId);
        view.addObject("seriesid",seriesid);
        return view;
    }


    /*
    *老师系列课详情
    *
    * @return
    */
    @RequestMapping(value = "weixin/teacherSeries")
    public ModelAndView teacherSeries(HttpServletRequest request, HttpServletResponse response,Long seriesid) {
        ModelAndView view = new ModelAndView("/func/weixin/course/teacherSeries");
        //用户已经登录
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息
        weiXinService.handlerUserNavivationRecord(request, token, LocalOauth2Url.teacherSeries);
        view.addObject("seriesid",seriesid);
        return view;
    }


    /*
    *老师系列课详情
    *
    * @return
    */
    @RequestMapping(value = "weixin/teacherSeriescourse")
    public ModelAndView teacherSeriescourse(Long id,Long seriesid) {
        ModelAndView view = new ModelAndView("/func/weixin/course/new_teachercourse");
        view.addObject("courseId", id);
        view.addObject("seriesid",seriesid);
        return view;
    }

    /*
    *老师系列课单节课程详情
    *
    * @return
    */
    @RequestMapping(value = "weixin/teacherSeriesdetails")
    public ModelAndView  teacherSeriesdetails() {
        ModelAndView view = new ModelAndView("/func/weixin/course/teacherSeriesdetails");
        return view;
    }

    /*
    *贡献排行榜
    *
    * @return
    */
    @RequestMapping(value = "weixin/contributionList")
    public ModelAndView  contributionList(Long courseId ) {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/contributionList");
        if(courseId == null) courseId = 0l;
        view.addObject("courseId" , courseId);
        return view;
    }
    /*
        *课程已删除
        *
        * @return
        */
    @RequestMapping(value = "weixin/notFound")
    public ModelAndView  notFound(String courseName) {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/notFound");
        view.addObject("courseName",courseName == null ? "未找到":courseName);
        return view;
    }

    /*
      *课程已删除
      *
      * @return
      */
    @RequestMapping(value = "weixin/courseDown")
    public ModelAndView  courseDown(String courseName) {
        ModelAndView view = new ModelAndView("/func/weixin/welfare/courseDown");
        view.addObject("courseName",courseName == null ? "未找到":courseName);
        return view;
    }
    /*
    *更换绑定
    *
    * @return
    */
    @RequestMapping(value = "weixin/changeCell")
    public ModelAndView  changeCell(String mobile) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/changeCell");
        view.addObject("oldMobile",mobile);
        return view;
    }

    /**
     * 健人直播活动页
     */
    @RequestMapping(value = "weixin/course/fitnessLive")
    public ModelAndView fitnessLive(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        ModelAndView view = new ModelAndView("/func/weixin/course/fitnessLive");
        if (identity != null) {
            visitCourseRecordService.insertRecord(identity.getId(), 1333l, 0l, "", 0l , "0");
        }
        return view;
    }

    /**
     * 第三方用户专题页面   待定
     * @param request
     * @param response
     * @param thirdType  第三方用户类型  详见 ThirdSpecialType
     * @param courseId   课程Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "weixin/thirdSpecial")
    public ModelAndView thirdSpecial(HttpServletRequest request, HttpServletResponse response ,
                                     String thirdType , Long  courseId) throws Exception {
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        ModelAndView view = new ModelAndView("");
        return view;
    }
    /*
        *管理员列表
        *
        * @return
        */
    @RequestMapping(value = "weixin/managers")
    public ModelAndView  managers() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/managers");
        return view;
    }
    /*
       *管理员添加
       *
       * @return
       */
    @RequestMapping(value = "weixin/administrator")
    public ModelAndView  administrator() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/administrator");
        return view;
    }
    /*
    * 酸枣管理后台登录
    * */
    @RequestMapping(value = "weixin/pcindex")
    public ModelAndView  pcindex() {
        ModelAndView view = new ModelAndView("/func/pc/index");
        return view;
    }
    /*
    * 编辑单节课
    * */
    @RequestMapping(value = "weixin/singleEdit")
    public ModelAndView  singleEdit() {
        ModelAndView view = new ModelAndView("/func/pc/singleEdit");
        return view;
    }

    /*
    * 课程邀请码页面，并进行抢码
    * */
    @RequestMapping(value = "weixin/inviCode")
    public ModelAndView  inviCode(HttpServletRequest request , HttpServletResponse response , Long inviCodeId  ) throws Exception {
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        ModelAndView view = new ModelAndView("/func/weixin/inviCard/InvitationCode");
        Map map = new HashMap();
        map.put("isGrab","1");
        map.put("itemId",0);
        Long courseId = 0l;
        String isSeriesCourse = "0";
        if(inviCodeId != null && inviCodeId != 0){
            map = inviCodeService.getInfo(inviCodeId);
            if(map != null){
                String isGrab = "0";
                courseId = (Long)map.get("courseId");
                Course course = courseService.getCourse(courseId);
                Map m = inviCodeService.getInviCode(courseId, identity, inviCodeId,course);
                if(m.get("isGrab")!=null){
                    isGrab = m.get("isGrab").toString();
                }
                if(m.get("isSeriesCourse")!=null){
                    isSeriesCourse = m.get("isSeriesCourse").toString();
                }
                if(m.get("isTeacherSelf")!=null){
                    return courseInfo(request,response,(long)map.get("courseId"),"0","",isSeriesCourse,0l , "");
                }
                map.put("itemId",m.get("itemId"));
                map.put("id" , inviCodeId);
                map.put("isGrab" , isGrab);//是否抢到 0-正常 1-未抢到
                if(map.get("startTime") != null){
                    map.put("startTime" , Utility.getDateTimeStr((Date)map.get("startTime") , "yyyy-MM-dd HH:mm"));
                }else{
                    map.put("startTime" , "0");//系列课没有时间
                }
                String start = " ";
                String end = " ";
                if(map.get("inveCodeStartTime")!=null && map.get("inveCodeEndTime")!=null){
                    start = Utility.getDateTimeStr((Date) map.get("inveCodeStartTime"), "yyyy-MM-dd HH:mm:ss");
                    end = Utility.getDateTimeStr((Date) map.get("inveCodeEndTime"), "yyyy-MM-dd HH:mm:ss");
                    map.put("time","请在" + start + "至" + end + "之内使用");
                }else if(map.get("inveCodeStartTime")!=null && map.get("inveCodeEndTime")==null){
                    start = Utility.getDateTimeStr((Date) map.get("inveCodeStartTime"), "yyyy-MM-dd HH:mm:ss");
                    map.put("time" ,"请在" + start + "后使用");
                }else if(map.get("inveCodeStartTime")==null && map.get("inveCodeEndTime")!=null){
                    end = Utility.getDateTimeStr((Date) map.get("inveCodeEndTime"), "yyyy-MM-dd HH:mm:ss");
                    map.put("time" ,"请在" + end + "前使用");
                }else{
                    map.put("time" , "永久有效");
                }
                if(identity!=null){
                    redisUtil.set(RedisKey.ll_invi_code_use_time + inviCodeId + identity.getId(), start + "," + end);
                }
                map.put("courseId",courseId);
                map.put("isSeriesCourse",isSeriesCourse);//1-是系列课0-是单节课
            }
        }
        if(map.get("isGrab").toString().equals("1")){
            return new ModelAndView("/func/weixin/inviCard/EndCode");
        }
        map.put("courseId" , courseId);
        map.put("inviCodeId",inviCodeId);
        view.addObject("inviCodeInfo" , map);
        return view;
    }
    /*
       *免费课程
       *
       * @return
       */
    @RequestMapping(value = "weixin/courseFree")
    public ModelAndView  courseFree() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/courseFree");
        return view;
    }
    /*
   *
   *每周精选
   * @return
   */
    @RequestMapping(value = "weixin/weekSelection")
    public ModelAndView weekSelection() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/weekSelection");
        return view;
    }
    /*
     *
     *我的课程
     * @return
     */

        @RequestMapping(value = "weixin/mycourse")
        public ModelAndView mycourse(HttpServletRequest request ,HttpServletResponse response )  throws Exception{
            AppUserIdentity token = handlerWeixinLogin(request, response);
            ModelAndView view = new ModelAndView("/func/weixin/personalCenter/mycourse");
            if(!Utility.isNullorEmpty(token)) {
                view.addObject("userType", token.getUserType());
            }
            return view;
         }
    /*
   *重置密码
   *
   * @return
   */
    @RequestMapping(value = "weixin/resetPassword")
    public ModelAndView  resetPassword(Optional<String> mobile) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/resetPassword");
        if(mobile.isPresent()){
            view.addObject("mobile",mobile.get());
        }
        return view;
    }
    /*
*设置新密码
*
* @return
*/
    @RequestMapping(value = "weixin/newpasswordsettings")
    public ModelAndView  reseatNewpasssword(Optional<String> mobile) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/newpasswordsettings");
        if(mobile.isPresent()){
            view.addObject("mobile",mobile.get());
        }
        return view;
    }
    /*
*输入验证码
*
* @return
*/
    @RequestMapping(value = "weixin/verificationCode")
    public ModelAndView  verificationCode(Optional<String> mobile) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/verificationCode");
        if(mobile.isPresent()){
            view.addObject("mobile",mobile.get());
        }
        return view;
    }
    /*
    *兑换学币
    *
    * @return
    */
    @RequestMapping(value = "weixin/exchangeCurrency.user")
    public ModelAndView  exchangeCurrency(HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/exchangeCurrency");
        if(token!=null){
            view.addObject("userId",token.getId());
        }
        return view;
    }
    /*
        *兑换详情
        *
        * @return
        */
    @RequestMapping(value = "weixin/detailsFor")
    public ModelAndView  detailsFor() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/detailsFor");
        return view;
    }
    /*
   *免费课程
   *
   * @return
   */
    @RequestMapping(value = "weixin/rankingList")
    public ModelAndView  rankingList() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/rankingList");
        return view;
    }
    /**
     * 抢到邀请码
     * @param request
     * @param inviCodeId
     * @return
     */
    @RequestMapping("/weixin/useCode")
    @ResponseBody
    public ActResultDto useCode(HttpServletRequest request ,HttpServletResponse response ,
                                String inviCodeId , String courseId , String itemId) throws Exception {
        AppUserIdentity token = handlerWeixinLogin(request, response);
        ActResultDto resultDto = new ActResultDto();
        if(token == null){
            resultDto.setCode(ReturnMessageType.ERROR_403.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_403.getMessage());
            return resultDto;
        }
        InviCodeItem item = itemService.getItemInfo(Long.parseLong(itemId));
        if(item==null){
            resultDto.setCode(ReturnMessageType.GET_INVI_CODE_INFO_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_INVI_CODE_INFO_ERROR.getMessage());
            return resultDto;
        }
        if(item.getUseAppId() < 1){     //第一次抢需要更新抢到的数量和使用人
            itemService.updateUseAppId(Long.valueOf(token.getId()), Long.valueOf(itemId), null, Long.parseLong(inviCodeId));
        }
        String isUse = inviCodeService.isUseTime(inviCodeId , token.getId());
        if(isUse.equals("0")){  //能使用
            ThirdPayDto thirdPayDto = new ThirdPayDto();
            thirdPayDto.setCourseId(Long.parseLong(courseId));
            Course course = courseService.getCourse(thirdPayDto.getCourseId());
            thirdPayDto.setAmount(course.getChargeAmt());
            thirdPayDto.setInvitationAppId(0l);
            joinCourseRecordService.handlerJoinCourseRecord(token, thirdPayDto, resultDto, JoinCourseRecordType.invi_code.getValue(), course.getRoomId());
            itemService.updateUseAppIdTwo(Long.valueOf(token.getId()), Long.valueOf(itemId), new Date(), Long.parseLong(inviCodeId));
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            redisUtil.del(RedisKey.ll_invi_code_use_time + inviCodeId);
        }else if(isUse.equals("1")){    //未到使用时间
            resultDto.setCode(ReturnMessageType.INVI_CODE_NOT_USE.getCode());
            resultDto.setMessage(ReturnMessageType.INVI_CODE_NOT_USE.getMessage());
        }else if(isUse.equals("2")){    //过期
            resultDto.setCode(ReturnMessageType.NOT_IN_USE_TIME.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_IN_USE_TIME.getMessage());
        }else{                          //第二次点击
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }
        return resultDto;
    }
    /*
      *直播列表
      *
      * @return
      */
    @RequestMapping(value = "weixin/liveitmesinfo")
    public ModelAndView  liveItmesInfo() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/liveItmesInfo");
        return view;
    }
    /*
      *预告
      *
      * @return
      */
    @RequestMapping(value = "weixin/traileritmesinfo")
    public ModelAndView  trailerItmesInfo() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/trailerItmesInfo");
        return view;
    }
    /*
      *搜索
      *
      * @return
      */
    @RequestMapping(value = "weixin/search")
    public ModelAndView  searchInfo() {
        ModelAndView view = new ModelAndView("/func/weixin/search/index");
        return view;
    }
    /*
      *跳转
      *
      * @return
      */
    @RequestMapping(value = "weixin/shareindex")
    public ModelAndView  shareindex() {
        ModelAndView view = new ModelAndView("/func/weixin/shareIndex");
        return view;
    }
/*
*
*   父亲节活动页statistics
* @return
*/
    @RequestMapping(value = "weixin/answer")
    public ModelAndView answer(HttpServletRequest request,HttpServletResponse response) throws Exception {
        ModelAndView view = new ModelAndView("/func/fathersDay/answer");
        WeixinAppUser weixinAppUser = (WeixinAppUser) request.getAttribute(CecurityConst.ll_live_weixin_app_user);
        System.out.println("get weixin infomation -------------------------");
        System.out.println("identity="+weixinAppUser);
        if(weixinAppUser != null){
            System.out.println(weixinAppUser.getHeadimgurl());
            System.out.println(weixinAppUser.getCity());
            System.out.println(weixinAppUser.getCountry());
            System.out.println(weixinAppUser.getNickname());
            System.out.println(weixinAppUser.getSex());
            System.out.println(weixinAppUser.getOpenid());
        }
        return view;
    }
/*
*
*   父亲节活动页
* @return
*/
    @RequestMapping(value = "weixin/statistics")
    public ModelAndView statistics() {
        ModelAndView view = new ModelAndView("/func/fathersDay/statistics");
        return view;
    }
    /*
*
*   消息大类
* @return
*/
    @RequestMapping(value = "weixin/messageType.user")
    public ModelAndView messageType(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/messageType");
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(token!=null){
            view.addObject("userId",token.getId());
        }
        return view;
    }
    /*
*
*   今日收益详情
* @return
*/
    @RequestMapping(value = "weixin/earningsDay.user")
    public ModelAndView earningsDay() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/earningsDay");
        return view;
    }
/*
*
*   转播收益
* @return
*/
    @RequestMapping(value = "weixin/myRevenue")
    public ModelAndView myRevenue() {
        ModelAndView view = new ModelAndView("/func/weixin/mywallet/myRevenue");
        return view;
    }
    /*
*
*   转播收益详情
* @return
*/
    @RequestMapping(value = "weixin/detailsReturn")
    public ModelAndView detailsReturn() {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/detailsReturn");
        return view;
    }

    /**
     * 游客登录
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "tourist/login")
    @ResponseBody
    public ActResultDto touristLogin(HttpServletRequest request, HttpServletResponse response, AppUser appUser) throws Exception {
        //AppUserIdentity appUserIdentity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        ActResultDto result = appUserService.touristLogin(appUser);
        AppUserIdentity identity = (AppUserIdentity) result.getData();
        if (identity != null) {
            response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        }
        return result;
    }
    /*
    *
    *   转播市场
    * @return
    */
    @RequestMapping(value = "weixin/relaymarket.user")
    public ModelAndView relaymarket(String courseName,HttpServletRequest request,HttpServletResponse response) throws Exception {
        ModelAndView view = new ModelAndView("/func/weixin/personalCenter/new_relaymarket");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        if(identity==null||identity.getToken()==null){
            view = new ModelAndView("/func/weixin/loginIn");
            return view;
        }
        view.addObject("courseName",courseName);
        return view;
    }
}
