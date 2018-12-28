package com.longlian.third.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.*;
import com.huaxin.util.weixin.encryption.AesException;
import com.huaxin.util.weixin.encryption.WXBizMsgCrypt;
import com.huaxin.util.weixin.type.WechatEventType;
import com.huaxin.util.weixin.type.WechatMessageType;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.WechatOfficialRoomService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.WechatOfficial;
import com.longlian.model.WechatOfficialRoom;
import com.longlian.third.service.AppUserService;
import com.longlian.third.service.WechatOfficiaService;
import com.longlian.token.WechatOfficialIdentity;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Administrator on 2017/2/9.
 */
@Controller
@RequestMapping(value = "/")
public class WechatController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    WeixinUtil weixinUtil;

    @Autowired
    WechatOfficialService wechatOfficialServices;

    @Autowired
    WechatOfficiaService wechatOfficiaService;

    @Autowired
    AppUserService appUserService;

    @Autowired
    WechatOfficialRoomService wechatOfficialRoomService;


    /**
     * 授权事件接收
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "wechat/receive")
    public void acceptAuthorizeEvent(HttpServletRequest request, HttpServletResponse response) throws IOException, AesException, DocumentException, Exception {
//       LogUtil.info(微信第三方平台---------微信推送Ticket消息10分钟一次-----------+ DataUtils.getDataString(DataUtils.yyyymmddhhmmss));
        processAuthorizeEvent(request);
        output(response, "success"); // 输出响应的内容。
    }

    /**
     * 一键授权功能
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "wechat/goAuthor")
    public void goAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException, AesException, DocumentException {
        String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
        String callbackUrl = CustomizedPropertyConfigurer.getContextProperty("wechat_third.callback.url");
        String website = CustomizedPropertyConfigurer.getContextProperty("website");
        String preAuthCode = weixinUtil.getThirdComponentPreAuthCode();//预授权码
        System.out.println(COMPONENT_APPID + ":" + callbackUrl + ":" + website + ":" + preAuthCode);
        String url = ParamesUriAPI.third_component_componentloginpage.replace("COMPONENT_APPID", COMPONENT_APPID)
                .replace("PRE_AUTH_CODE", preAuthCode).replace("REDIRECT_URI", website + callbackUrl);
        response.sendRedirect(url);
    }

    @RequestMapping(value = "wechat/index")
    public ModelAndView toAuthor() {
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/index");
        return modelAndView;
    }

    /**
     * 微信登录授权
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "wechat/loginBackcall")
    public ModelAndView loginBackcall(HttpServletRequest request, HttpServletResponse response, String code, String state) throws IOException, AesException, DocumentException, Exception {
        System.out.println("loginBackcall_code --------->" + code);
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/informationFilling");
        WeixinApp weixinApp = weixinUtil.getThirdComponentWebUser(code);
        //根据unionid ，找到用户和直播间ID和名称
        Map map = appUserService.queryUserLiveRoomByUnitionId(weixinApp.getUnionid());
        if (null == map) {
            modelAndView = new ModelAndView("/func/thirdWechat/home2");
        } else {
            modelAndView.addObject("liveId", map.get("liveId"));
            modelAndView.addObject("liveName", map.get("liveName"));
            modelAndView.addObject("liveRoomNo", map.get("liveRoomNo"));
        }
        return modelAndView;
    }


    @RequestMapping(value = "wechat/loginBackcall_second")
    public ModelAndView loginBackcall_second(HttpServletRequest request, HttpServletResponse response, String code, String state) throws IOException, AesException, DocumentException, Exception {
        System.out.println("loginBackcall_second_code --------->" + code);
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/toBindLiveRoom");
        WeixinApp weixinApp = weixinUtil.getThirdComponentWebUser(code);
        //根据unionid ，找到用户和直播间ID和名称
        Map map = appUserService.queryUserLiveRoomByUnitionId(weixinApp.getUnionid());
        if (null == map) {
            modelAndView = new ModelAndView("/func/thirdWechat/home2");
        } else {
            modelAndView.addObject("liveId", map.get("liveId"));
            modelAndView.addObject("liveName", map.get("liveName"));
            modelAndView.addObject("liveRoomNo", map.get("liveRoomNo"));
        }
        return modelAndView;
    }


    /*首页*/
    @RequestMapping(value = "wechat/home")
    public ModelAndView home() {

        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/home");
        return modelAndView;
    }

    @RequestMapping(value = "wechat/home2")
     public ModelAndView home2() {
         ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/home");
          return modelAndView;
    }

    @RequestMapping(value = "wechat/error")
    public ModelAndView error() {
                ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/error");
               return modelAndView;
           }
    /*公众号登记*/
    @RequestMapping(value = "wechat/informationFilling")
    public ModelAndView informationFilling() {
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/informationFilling");
        return modelAndView;
    }

    /*欢迎页面*/
    @RequestMapping(value = "wechat/welcome")
    public ModelAndView welcome() {
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/welcome");
        return modelAndView;
    }

    /*解绑定列表*/
    @RequestMapping(value = "wechat/bindinglive")
    public ModelAndView bindinglive() {
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/bindinglive");
        return modelAndView;
    }

    /**
     * 公众平台授权后返回地址
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "wechat/authorCallback")
    public ModelAndView authorCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String auth_code = request.getParameter("auth_code");
        String expires_in = request.getParameter("expires_in");
        //获取授权公众号token和APPID
        WechatAuthorizationToken wechatAuthorizationToken = weixinUtil.getThirdComponentAuthorizerAccessToken(auth_code);
        //获取授权公众号基本信息
        WechatAppInfo wechatAppInfo = weixinUtil.getThirdComponentAuthorizerInfo(wechatAuthorizationToken.getAppid());
        WechatOfficial WechatOfficial = wechatOfficialServices.addOrUpdate(wechatAuthorizationToken, wechatAppInfo);
        ModelAndView view = new ModelAndView("/func/thirdWechat/informationFilling");
        // if(!"2".equals(WechatOfficial.getServiceType()) && !"0".equals(WechatOfficial.getVerifyTypeInfo()))
        if (!"2".equals(WechatOfficial.getServiceType()) || ("-1").equals(WechatOfficial.getVerifyTypeInfo())) {
            view = new ModelAndView("/func/thirdWechat/noServiceNo");
            return view;
        }
        //登录
        ActResultDto result = wechatOfficialServices.loginIn(WechatOfficial);
        boolean flag = wechatOfficialRoomService.isBindedRoom(WechatOfficial.getAppid(), null);
        if (flag) {
            redisUtil.hset(RedisKey.ll_live_appid_use_authorizer_room_info, WechatOfficial.getLiveId() + "", WechatOfficial.getAppid() + "");
            view = new ModelAndView("/func/thirdWechat/home");
        }
        view.addObject("contactWechat", WechatOfficial.getContactWechat());

        System.out.print("*********WechatOfficial.getContactWechat()***********" + WechatOfficial.getContactWechat());
        //view.addObject("WechatOfficial", WechatOfficial);
        System.out.println("微信服务号auth_code-----------》" + auth_code);
        WechatOfficialIdentity identity = (WechatOfficialIdentity) result.getData();
        response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        return view;
    }

    /**
     * 查询
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "wechat/selectWechatOfficialRoomList.user")
    @ResponseBody
    public DatagridResponseModel selectOfficial(DatagridRequestModel requestModel, HttpServletRequest request) throws Exception {
        DatagridResponseModel model = new DatagridResponseModel();
        WechatOfficialIdentity token = (WechatOfficialIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> roomList = wechatOfficialRoomService.getBindRoomListByWechatById(requestModel, token.getAppid());
        model.setRows(roomList);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 绑定页面
     *
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/toBindLiveRoom")
    public ModelAndView toBindLiveRoom(HttpServletRequest request, Long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/thirdWechat/toBindLiveRoom");
        view.addObject("id", id);
        return view;
    }

    /**
     * 绑定直播间
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/insertWechatOfficialRoom.user", method = RequestMethod.POST)
    public ModelAndView insertWechatOfficialRoom(HttpServletRequest request, WechatOfficial wechatOfficial) throws Exception {
        ModelAndView view = new ModelAndView("redirect:/wechat/home");
        WechatOfficialIdentity token = (WechatOfficialIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        WechatOfficialRoom wechatOfficialRoom = new WechatOfficialRoom();
        wechatOfficialRoom.setWechatId(token.getAppid());
        wechatOfficialRoom.setBindTime(new Date());
        wechatOfficialRoom.setAuditStatus("0");
        wechatOfficialRoom.setLiveId(wechatOfficial.getLiveId());
        wechatOfficialRoom.setLiveName(wechatOfficial.getLiveName());
        wechatOfficialRoom.setContactMobile(wechatOfficial.getContactMobile());
        wechatOfficialRoom.setContactWechat(wechatOfficial.getContactWechat());
        wechatOfficialRoomService.addWechatOfficialRooom(wechatOfficialRoom);
        if (!Utility.isNullorEmpty(wechatOfficial.getReserveReminderId())) {
            WechatOfficial newWechatOfficial = new WechatOfficial();
            BeanUtils.copyProperties(wechatOfficial, newWechatOfficial);
            newWechatOfficial.setId(token.getId());
            wechatOfficialServices.updateForWechatOfficial(newWechatOfficial);
        }

        redisUtil.hset(RedisKey.ll_live_appid_use_authorizer_room_info, wechatOfficialRoom.getLiveId() + "", wechatOfficialRoom.getWechatId() + "");
        return view;
    }

    /**
     * 判断直播间是否绑定过公众号
     * @param request
     * @param liveId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/isBindedRoom.user")
    @ResponseBody
    public ActResult isBindedRoom(HttpServletRequest request, Long liveId) throws Exception {
        ActResult ac = new ActResult();
        WechatOfficialIdentity token = (WechatOfficialIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        boolean flag = wechatOfficialRoomService.isBindedRoom(null, liveId);
        ac.setData(flag);
        return ac;
    }

    /**
     * 解绑直播间
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/unBindLiveRoom.user")
    @ResponseBody
    public ActResult unBindLiveRoom(HttpServletRequest request, Long id) throws Exception {
        WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomService.findById(id);
        if (!Utility.isNullorEmpty(wechatOfficialRoom)) {
            wechatOfficialRoomService.deleteBindRoom(id);
        }
        redisUtil.hdel(RedisKey.ll_live_appid_use_authorizer_room_info, wechatOfficialRoom.getLiveId() + "");
        return ActResult.success();
    }

    /**
     * 编辑页面
      * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/editMessageTemplet")
    public ModelAndView editMessageTemplet(HttpServletRequest request, Long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/thirdWechat/edit");
        view.addObject("id", id);
        return view;
    }

    /**
     * 获取公众号信息
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/getWechatOfficial.user")
    @ResponseBody
    public ActResult getWechatOfficial(HttpServletRequest request) throws Exception {
        ActResult ac = new ActResult();
        WechatOfficialIdentity w = (WechatOfficialIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        WechatOfficial wechatOfficial =wechatOfficialServices.selectById(w.getId());
        ac.setData(wechatOfficial);
        return ac;
    }

    /**
     * 修改消息模板
     * @param request
     * @param official
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "wechat/updateMessageTemplet.user", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doUpdate(HttpServletRequest request, @RequestBody WechatOfficial official) throws Exception {
        ActResult ac = new ActResult();
        WechatOfficialIdentity token = (WechatOfficialIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        official.setId(token.getId());
        wechatOfficialServices.updateForWechatOfficialById(official);
        return ac;
    }

    /**
     * 授权成功回调页面
     *
     * @param request
     * @param response
     * @param appId
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     * @throws Exception
     */
    @RequestMapping(value = "{APPID}/wechat/callback")
    public void acceptMessageAndEvent(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "APPID") String appId) throws IOException, AesException, DocumentException, Exception {
        String msgSignature = request.getParameter("msg_signature");
        Map map = request.getParameterMap();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key;
            key = (String) it.next();
            //  System.out.println( key + "----->" +  map.get(key).toString());
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader in = request.getReader();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        String xml = sb.toString();
        Document doc = DocumentHelper.parseText(xml);
        Element rootElt = doc.getRootElement();
        String toUserName = rootElt.elementText("ToUserName");
        //微信全网测试账号
        //LogUtil.info(全网发布接入检测消息反馈开始---------------APPID=+ APPID +------------------------toUserName=+toUserName);
        try {
            checkWeixinAllNetworkCheck(request, response, xml, appId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 处理授权事件的推送
     *
     * @param request
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    public void processAuthorizeEvent(HttpServletRequest request) throws IOException, DocumentException, AesException, Exception {
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");
        String msgSignature = request.getParameter("msg_signature");
        System.out.println("nonce:" + nonce + "_timestamp:" + timestamp + "_signature:" + signature + "_msgSignature:" + msgSignature);
        //    return;// 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
        String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
        String COMPONENT_TOKEN = CustomizedPropertyConfigurer.getContextProperty("wechat_third.token");
        String COMPONENT_ENCODINGAESKEY = CustomizedPropertyConfigurer.getContextProperty("wechat_third.encodingAESKey");
        boolean isValid = checkSignature(COMPONENT_TOKEN, signature, timestamp, nonce);
        if (isValid) {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = request.getReader();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            String xml = sb.toString();
            System.out.println("xml-->" + xml);
            WXBizMsgCrypt pc = new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
            xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);
            System.out.println("xmlnew-->" + xml);
            processAuthorizationEvent(xml);
        }
    }

    /**
     * 处理授权成功、更新、取消通知
     * 保存Ticket
     *
     * @param xml
     */
    void processAuthorizationEvent(String xml) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            String AppId = rootElt.elementText("AppId");//公众号appid
            String InfoType = rootElt.elementText("InfoType");
            String AuthorizerAppid = rootElt.elementText("AuthorizerAppid"); //授权方的公众号

            //InfoType 信息类型  updateauthorized : 授权更新通知  unauthorized : 取消授权通知 authorized:授权成功通知 ,component_verify_ticket:第三方平台获取token票据
            if ("component_verify_ticket".equals(InfoType)) {
                String ticket = rootElt.elementText("ComponentVerifyTicket");
                System.out.println("ticket---------->" + ticket);
                if (!Utility.isNullorEmpty(ticket)) {
                    redisUtil.set(RedisKey.ll_live_component_verify_ticket, ticket);
                }
            } else if ("unauthorized".equals(InfoType)) {
                wechatOfficialServices.unauthorized(AuthorizerAppid);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取授权的Appid
     *
     * @param xml
     * @return
     */
    String getAuthorizerAppidFromXml(String xml) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            String toUserName = rootElt.elementText("ToUserName");
            return toUserName;
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析微信事件推送
     *
     * @param request
     * @param response
     * @param xml
     * @throws DocumentException
     * @throws IOException
     * @throws AesException
     * @throws Exception
     */
    public void checkWeixinAllNetworkCheck(HttpServletRequest request, HttpServletResponse response, String xml, String appId) throws DocumentException, IOException, AesException, Exception {
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
        String COMPONENT_TOKEN = CustomizedPropertyConfigurer.getContextProperty("wechat_third.token");
        String COMPONENT_ENCODINGAESKEY = CustomizedPropertyConfigurer.getContextProperty("wechat_third.encodingAESKey");
        WXBizMsgCrypt pc = new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
        xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);
        WechatMessageInfo wechatMessageInfo = weixinUtil.getWechatMessageInfo4XML(xml);
        //全网发布原始账号
        String all_network_released = "gh_3c884a361561";
        if (wechatMessageInfo == null) return;
        if (WechatMessageType.event.getValue().equals(wechatMessageInfo.getMsgType())) {
            String event = wechatMessageInfo.getEvent();
            System.out.println("event=========》》" + event);
            wechatMessageInfo.setAppId(appId);
            if (WechatEventType.subscribe.getValue().equals(event)
                    || WechatEventType.SCAN.getValue().equals(event)) {
                //关注和再次关注
                wechatOfficiaService.followWechatSendCustomMessageByOpenId(wechatMessageInfo);
            } else if (WechatEventType.SCAN.getValue().equals(event)) {
                wechatOfficiaService.followWechatSendCustomMessageByOpenId(wechatMessageInfo);
            } else if (WechatEventType.unsubscribe.getValue().equals(event)) {
                wechatOfficiaService.cannelFollowWechat(wechatMessageInfo);
            }
            // System.out.println("---全网发布接入检测--step.3-----------事件消息--------");
            if(all_network_released.equals(wechatMessageInfo.getToUser())){
                replyEventMessage(request,response,event,wechatMessageInfo.getToUser(),wechatMessageInfo.getFormUserName());
            }
        } else if (WechatMessageType.text_.getValue().equals(wechatMessageInfo.getMsgType())
                || WechatMessageType.image.getValue().equals(wechatMessageInfo.getMsgType())
                || WechatMessageType.voice.getValue().equals(wechatMessageInfo.getMsgType())
                || WechatMessageType.shortvideo.getValue().equals(wechatMessageInfo.getMsgType())
                || WechatMessageType.location.getValue().equals(wechatMessageInfo.getMsgType())
                || WechatMessageType.link.getValue().equals(wechatMessageInfo.getMsgType())
                ) {
            if(all_network_released.equals(wechatMessageInfo.getToUser())){
                // System.out.println("---全网发布接入检测--step.3-----------文本消息--------");
                String content = wechatMessageInfo.getContent();
                processTextMessage(request, response, content, wechatMessageInfo.getToUser(),wechatMessageInfo.getFormUserName());
            }else{
                replyCusomerServiceMessage(request, response, wechatMessageInfo.getToUser(), wechatMessageInfo.getFormUserName());
            }
        }
    }

    /**
     * 回复事件消息
     *
     * @param request
     * @param response
     * @param event
     * @param toUserName
     * @param fromUserName
     * @throws DocumentException
     * @throws IOException
     */
    public void replyEventMessage(HttpServletRequest request, HttpServletResponse response, String event, String toUserName, String fromUserName) throws DocumentException, IOException {
        String content = event + "from_callback";
//        LogUtil.info(---全网发布接入检测------step.4-------事件回复消息  content=+content +    toUserName=+toUserName+   fromUserName=+fromUserName);
        replyTextMessage(request, response, content, toUserName, fromUserName);
    }


    public void processTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName) throws IOException, DocumentException {
        if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)) {
            String returnContent = content + "_callback";
            replyTextMessage(request, response, returnContent, toUserName, fromUserName);
        } else if (StringUtils.startsWithIgnoreCase(content, "QUERY_AUTH_CODE")) {
            output(response, "success");
            //接下来客服API再回复一次消息
            replyApiTextMessage(request, response, content.split(":")[1], fromUserName);
        }
    }

    public void replyApiTextMessage(HttpServletRequest request, HttpServletResponse response, String auth_code, String fromUserName) throws DocumentException, IOException {
        String authorization_code = auth_code;
        // 得到微信授权成功的消息后，应该立刻进行处理！！相关信息只会在首次授权的时候推送过来
        System.out.println("------step.1----使用客服消息接口回复粉丝----逻辑开始-------------------------");
        try {
            String msg = auth_code + "_from_api";
            WechatAuthorizationToken wechatAuthorizationToken = weixinUtil.getThirdComponentAuthorizerAccessToken(auth_code);
            System.out.println(msg + ":------------------msg");
            weixinUtil.sendAllNetCustomTextMessageByOpenId(wechatAuthorizationToken.getToken(), fromUserName, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证是否过期
     *
     * @param accessTokenExpires
     * @return
     */
    boolean isExpired(long accessTokenExpires) {
        return false;
    }

    /**
     * 回复微信服务器文本消息
     *
     * @param request
     * @param response
     * @param content
     * @param toUserName
     * @param fromUserName
     * @throws DocumentException
     * @throws IOException
     */
    public void replyTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName) throws DocumentException, IOException {
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>");
        sb.append("<CreateTime> " + createTime + "</CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA[" + content + "]]></Content>");
        sb.append("</xml>");
        String replyMsg = sb.toString();
        String nonce = UUIDGenerator.generate();
        String returnvaleue = "";
        System.out.println("----发送消息--xml：" + sb.toString());
        try {
            String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
            String COMPONENT_TOKEN = CustomizedPropertyConfigurer.getContextProperty("wechat_third.token");
            String COMPONENT_ENCODINGAESKEY = CustomizedPropertyConfigurer.getContextProperty("wechat_third.encodingAESKey");
            WXBizMsgCrypt pc = new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
            returnvaleue = pc.encryptMsg(replyMsg, createTime.toString(), "nonce");
            System.out.println("------------------加密后的返回内容 returnvaleue：" + returnvaleue);
        } catch (AesException e) {
            e.printStackTrace();
        }
        output(response, returnvaleue);
    }


    /**
     * 消息转发到客服
     *
     * @param request
     * @param response
     * @param toUserName
     * @param fromUserName
     * @throws DocumentException
     * @throws IOException
     */
    public void replyCusomerServiceMessage(HttpServletRequest request, HttpServletResponse response, String toUserName, String fromUserName) throws DocumentException, IOException {
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>");
        sb.append("<CreateTime>" + createTime + "</CreateTime>");
        sb.append("<MsgType><![CDATA[transfer_customer_service]]></MsgType>");
        sb.append("</xml>");
        String replyMsg = sb.toString();
        String nonce = UUIDGenerator.generate();
        String returnvaleue = "";
        System.out.println("----发送消息--xml：" + sb.toString());
        try {
            String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
            String COMPONENT_TOKEN = CustomizedPropertyConfigurer.getContextProperty("wechat_third.token");
            String COMPONENT_ENCODINGAESKEY = CustomizedPropertyConfigurer.getContextProperty("wechat_third.encodingAESKey");
            WXBizMsgCrypt pc = new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
            returnvaleue = pc.encryptMsg(replyMsg, createTime.toString(), "nonce");
            System.out.println("------------------加密后的返回内容 returnvaleue：" + returnvaleue);
        } catch (AesException e) {
            e.printStackTrace();
        }
        output(response, returnvaleue);
    }


    public static void main(String[] args) {
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
        String replyMsg = "LOCATIONfrom_callback";
        String COMPONENT_APPID = "wx3ea560592eab45d8";//CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
        String COMPONENT_TOKEN = "test";
        String COMPONENT_ENCODINGAESKEY = "df4frrrrrr666666666666666666666666666666666";
        String returnvaleue = "";
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
            returnvaleue = pc.encryptMsg(replyMsg, createTime.toString(), "easemob");
            System.out.println(returnvaleue);
        } catch (AesException e) {
            e.printStackTrace();
        }
    }

    /**
     * 工具类：回复微信服务器文本消息
     *
     * @param response
     * @param returnvaleue
     */
    public void output(HttpServletResponse response, String returnvaleue) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
//          System.out.println(****************returnvaleue***************=+returnvaleue);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否加密
     *
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        // System.out.println(###token:+token+;signature:+signature+;timestamp:+timestamp+nonce:+nonce);
        boolean flag = false;
        if (signature != null && !signature.equals("") && timestamp != null && !timestamp.equals("") && nonce != null && !nonce.equals("")) {
            String sha1 = "";
            String[] ss = new String[]{token, timestamp, nonce};
            Arrays.sort(ss);
            for (String s : ss) {
                sha1 += s;
            }
            sha1 = AddSHA1.SHA1(sha1);
            if (sha1.equals(signature)) {
                flag = true;
            }
        }
        return flag;
    }

}

class AddSHA1 {
    public static String SHA1(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");     //选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(inStr.getBytes());       //返回的是byet[]，要转化为String存储比较方便
            outStr = bytetoString(digest);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }

    public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";
        for (int i = 0; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + 0 + tempStr;
            } else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }
}


