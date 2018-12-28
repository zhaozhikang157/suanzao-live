package com.longlian.live.util.weixin;

import com.alibaba.fastjson.JSON;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.*;
import com.huaxin.util.weixin.oauth2.OAuth2Core;
import com.huaxin.util.weixin.oauth2.ThirdOAuth2;
import com.huaxin.util.weixin.oauth2.WechatSendTempleMsgDataRunner;
import com.huaxin.util.weixin.type.WechatMedieType;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.huaxin.util.weixin.type.WechatTemplateMessage;
import com.longlian.live.service.UserFollowWechatOfficialService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.UserFollowWechatOfficial;
import com.longlian.type.LogType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求数据通用类
 *
 * @author syl
 * @date 2016.10.29
 */
@Component
public class WeixinUtil {

	private static Logger log = LoggerFactory.getLogger(WeixinUtil.class);
	@Autowired
	RedisUtil redisUtil;
	ExecutorService threadPool  = Executors.newFixedThreadPool(50);

	@Autowired
	WechatOfficialService wechatOfficiaService;

	@Autowired
	UserFollowWechatOfficialService userFollowWechatOfficialService;

	/**
	 * 获取授权网页地址
	 * @param uri
	 * @return
	 */
	public  String getRedirectUri(String uri) {
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String result = uri;
		try {
			result = URLEncoder.encode(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String requestUrl = ParamesUriAPI.oauth2_authorize.replace("APPID", appid)
				.replace("REDIRECT_URI", result);
		return requestUrl;
	}

	/**
	 * 第三方网页代授权 获取授权网页地址
	 * @param uri
	 * @return
	 */
	public  String getThirdReplacePageRedirectUri(String uri  ) {
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String wechat_third_appid = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		String result = uri;
		try {
			result = URLEncoder.encode(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String requestUrl = ParamesUriAPI.third_component_replace_page_authorize.replace("APPID",  appid)
				.replace("REDIRECT_URI", result).replace("COMPONENT_APP_ID", wechat_third_appid);
		return requestUrl;
	}



	/**
	 * 解析消息xml 转换为 对象
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static WechatMessageInfo getWechatMessageInfo4XML(String xml) throws Exception{
		WechatMessageInfo wechatMessageInfo = null;
		if(!Utility.isNullorEmpty(xml)){
			wechatMessageInfo = new WechatMessageInfo();
			Document doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			String msgType = rootElt.elementText("MsgType");
			String toUserName = rootElt.elementText("ToUserName");
			String fromUserName = rootElt.elementText("FromUserName") ;
			String createTime = rootElt.elementText("CreateTime");
			String event = rootElt.elementText("Event");
			String eventKey = rootElt.elementText("EventKey") ;
			String ticket = rootElt.elementText("Ticket") ;
			String latitude = rootElt.elementText("Latitude");
			String longitude = rootElt.elementText("Longitude");
			String precision = rootElt.elementText("Precision") ;
			String content = rootElt.elementText("Content") ;
			String MsgId = rootElt.elementText("MsgId") ;
			wechatMessageInfo.setCreateTime(createTime);
			wechatMessageInfo.setMsgType(msgType);
			wechatMessageInfo.setToUser(toUserName);
			wechatMessageInfo.setFormUserName(fromUserName);
			wechatMessageInfo.setEvent(event);
			wechatMessageInfo.setEventKey(eventKey);
			wechatMessageInfo.setTicket(ticket);
			wechatMessageInfo.setLatitude(latitude);
			wechatMessageInfo.setLongitude(longitude);
			wechatMessageInfo.setPrecision(precision);
			wechatMessageInfo.setMsgId(MsgId);
			wechatMessageInfo.setContent(content);
		}
		return  wechatMessageInfo;
	}

	/**
	 * url 转换UTF-8
	 * @param str
	 * @return
	 */
	public static String URLEncoder(String str) {
		String result = str;
		try {
			result = URLEncoder.encode(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据内容类型判断文件扩展名
	 *
	 * @param contentType
	 *            内容类型
	 * @return
	 */
	public static String getFileEndWitsh(String contentType) {
		String fileEndWitsh = "";
		if ("image/jpeg".equals(contentType))
			fileEndWitsh = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileEndWitsh = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileEndWitsh = ".amr";
		else if ("video/mp4".equals(contentType))
			fileEndWitsh = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileEndWitsh = ".mp4";
		return fileEndWitsh;
	}


	/**
	 * 获取accessToken  ,存入redis
	 * */
	public  AccessToken getAccessToken(){
		AccessToken accessToken  = new AccessToken();;
		//从公众号直接获取
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		//从第三方授权token
		String token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(appid);
		accessToken.setToken(token);
		return accessToken;
	}

	/**
	 * JSAPITicket  ,存入redis
	 * */
	public  AccessToken getJSAPITicket(){
		AccessToken jsapiTicket = null;
		if(redisUtil.exists(RedisKey.ll_live_weixin_jsapi_ticket)){
			jsapiTicket = new AccessToken();
			String ticket = redisUtil.get(RedisKey.ll_live_weixin_jsapi_ticket);
			jsapiTicket.setToken(ticket);
		}else{
			AccessToken accessToken = getAccessToken();//先获取accessToken
			if(accessToken != null){
				jsapiTicket = OAuth2Core.getJSAPITicket(accessToken.getToken());
				if (jsapiTicket != null ){
					redisUtil.setex(RedisKey.ll_live_weixin_jsapi_ticket , RedisKey.ll_live_access_token_use_time, jsapiTicket.getToken());
				}
			}
		}
		return jsapiTicket;
	}

	/**
	 * 获取 JS api 签名
	 * @return
	 * @throws IOException
	 */
	public  String getJSApiSignature(String nonce , String  timestamp ,String url) throws IOException {
		AccessToken jsTicket = getJSAPITicket();
		String  signature = OAuth2Core.getJSApiSignature(jsTicket.getToken(), timestamp, nonce, url);
		return  signature;
	}

	/**
	 * 根据code获取用户信息
	 * @param code
	 * @return
	 */
	public static WeixinApp getUser( String code) {
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String appsecret = CustomizedPropertyConfigurer.getContextProperty("wechat.appsecret");
		WeixinApp weixinApp = OAuth2Core.getUser(code, appid, appsecret);
		return weixinApp;
	}

	/**
	 * 根据code获取用户信息
	 * 	调用接口凭证
	 * @param code
	 * 通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * */
	public    WeixinAppUser getUserAppUser(String code) {
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String appsecret = CustomizedPropertyConfigurer.getContextProperty("wechat.appsecret");
		WeixinAppUser weixinAppUser = OAuth2Core.getUserAppUser(code, appid, appsecret);
		return weixinAppUser;
	}

	/**
	 * 根据code获取用户信息
	 * 	调用接口凭证
	 * @param code
	 * 通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * */
	public   WeixinAppUser getUserAppUserByThird(String code) {
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String wechat_third = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		AccessToken accessToken = getThirdAccessToken();
		WeixinAppUser weixinAppUser = ThirdOAuth2.getUserAppUser(code, appid, wechat_third, accessToken.getToken());
		return weixinAppUser;
	}



	/**
	 * 根据code获取用户信息 包含是否关注
	 * 	调用接口凭证
	 * @param code
	 * 通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * */
	public    WeixinAppUser getUserAppUserContainIsFollow(String code) {
		String accessToken = getAccessToken().getToken();
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String appsecret = CustomizedPropertyConfigurer.getContextProperty("wechat.appsecret");
		WeixinAppUser weixinAppUser =  getUserAppUserContainIsFollowId(code, appid, appsecret, accessToken);
		return weixinAppUser;
	}


	/**
	 * 根据code获取用户信息 包含是否关注
	 * 	调用接口凭证
	 * @param code
	 * 通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * */
	public    WeixinAppUser getUserAppUserXCX(String code ,String encryptedData , String iv) {
		String appid = "wxba2bd42d9e580089";
		String appsecret = "012b765702fbad81a71dc81a30cc7fb2";
		 WeixinApp weixinApp =  OAuth2Core.getUnionidAndOpenid4XcX(appid, appsecret, code);
		JSONObject jsonobject = OAuth2Core.getXCXUserInfo(encryptedData, weixinApp.getAccess_token(), iv);
		WeixinAppUser weixinAppUser = new WeixinAppUser();
		weixinAppUser.setOpenid(weixinApp.getOpenid());//用户唯一标示
		weixinAppUser.setUnionid(weixinApp.getUnionid());
		weixinAppUser.setNickname(jsonobject.getString("nickName"));//用户昵称
		weixinAppUser.setSex(jsonobject.getString("gender"));//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
		weixinAppUser.setProvince(jsonobject.getString("province"));//用户个人资料填写的省份
		weixinAppUser.setCity(jsonobject.getString("city"));//普通用户个人资料填写的城市
		weixinAppUser.setCountry(jsonobject.getString("country"));//	国家，如中国为CN
		weixinAppUser.setHeadimgurl(jsonobject.getString("avatarUrl"));//用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
		return weixinAppUser;
	}

	/**
	 * 根据code获取用户信息
	 * 			  调用接口凭证
	 * @param code
	 *            通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * */
	public  WeixinAppUser getUserAppUserContainIsFollowId( String code, String appid  , String secret , String accessToken) {
		WeixinAppUser weixinAppUser = null;
		WeixinApp weixinApp =  OAuth2Core.getUser(code, appid, secret);
		if(weixinApp != null ){
			String GET_APP_USER_INFO_NEW = ParamesUriAPI.GET_APP_USER_INFO_CONTAIN_IS_FOLLOW.replace("ACCESS_TOKEN",accessToken).replace("OPENID", weixinApp.getOpenid());
			JSONObject jsonobject = HttpRequestUtil.httpRequest(GET_APP_USER_INFO_NEW, "GET", null);
			log.info("getUserAppUserContainIsFollowId code:{}, appid:{}, secret:{}" , code , appid , secret);	log.info("code:{}, appid:{}, secret:{}" , code , appid , secret);
			if (null != jsonobject) {
				if(jsonobject.containsKey("errcode") ){
					log.error("getUserAppUserContainIsFollowId1:错误：{}", jsonobject);
					Integer errcode = (Integer) jsonobject.get("errcode");
					//access过期
					if (errcode == 40001) {
						//再取一次
						//WeixinUtil weixinUtil = SpringContextUtil.getBeanByName("weixinUtil");
						accessToken = getAccessToken().getToken();
						GET_APP_USER_INFO_NEW = ParamesUriAPI.GET_APP_USER_INFO_CONTAIN_IS_FOLLOW.replace("ACCESS_TOKEN",accessToken).replace("OPENID", weixinApp.getOpenid());
						jsonobject = HttpRequestUtil.httpRequest(GET_APP_USER_INFO_NEW, "GET", null);
						if (errcode == 40001) {
							if(jsonobject.containsKey("errcode") ) {
								log.error("getUserAppUserContainIsFollowId2:错误：{}", jsonobject);
								errcode = (Integer) jsonobject.get("errcode");
							}
						}
					}
				}

				weixinAppUser = new WeixinAppUser();

				weixinAppUser.setSubscribe(jsonobject.getString("subscribe"));//用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
				if("0".equals(weixinAppUser.getSubscribe())){//如果没有关注，则从网页授权获取
					weixinAppUser = OAuth2Core.getUserAppUser( weixinApp );
					weixinAppUser.setSubscribe("0");
					weixinAppUser.setIsFollowLlWechat("0");
					return weixinAppUser;
				}
				weixinAppUser.setIsFollowLlWechat("1");
				weixinAppUser.setAccess_token(weixinApp.getAccess_token());
				weixinAppUser.setOpenid(weixinApp.getOpenid());//用户唯一标示
				weixinAppUser.setNickname(jsonobject.getString("nickname"));//用户昵称
				weixinAppUser.setSex(jsonobject.getString("sex"));//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
				weixinAppUser.setProvince(jsonobject.getString("province"));//用户个人资料填写的省份
				weixinAppUser.setCity(jsonobject.getString("city"));//普通用户个人资料填写的城市
				weixinAppUser.setCountry(jsonobject.getString("country"));//	国家，如中国为CN
				weixinAppUser.setHeadimgurl(jsonobject.getString("headimgurl"));//用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
				String unionid = "";
				if(jsonobject.containsKey("unionid")){
					unionid = jsonobject.getString("unionid");
				}

				weixinAppUser.setUnionid(unionid);//只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
			} else {
				System.out.println("获取用户授权失败了，自己找原因。。。");
			}
		}
		return weixinAppUser;
	}

	/**
	 * 获取第三方平台token  ,存入redis
	 * */
	public  AccessToken getThirdAccessToken(){
		AccessToken accessToken = null;
		if(redisUtil.exists(RedisKey.ll_live_third_component_access_token)){
			accessToken = new AccessToken();
			String accessTokenStr = redisUtil.get(RedisKey.ll_live_third_component_access_token);
			accessToken.setToken(accessTokenStr);
		}else{
			String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
			String COMPONENT_APPSECRET = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appsecret");
			//从redis缓存取component_verify_ticket
			String ticket = redisUtil.get(RedisKey.ll_live_component_verify_ticket);
			if(!Utility.isNullorEmpty(ticket)){
				accessToken = ThirdOAuth2.getThirdComponentAccessToken(COMPONENT_APPID, COMPONENT_APPSECRET,ticket);
				if(accessToken != null){
					redisUtil.setex(RedisKey.ll_live_third_component_access_token , RedisKey.ll_live_access_token_use_time, accessToken.getToken());
				}
			}
		}
		return accessToken;
	}

	/**
	 *  获取第三方平台 PreAuthCode
	 * @return
	 */
	public String getThirdComponentPreAuthCode(){
		String  preAuthCode = "";
		String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		AccessToken  token = getThirdAccessToken();
		if(token != null){
			preAuthCode = ThirdOAuth2.getThirdComponentPreAuthCode(COMPONENT_APPID, token.getToken());
		}
		return preAuthCode;
	}
	/**
	 * 使用授权码换取公众号的接口调用凭据和授权信息
	 * @param authorization_code 授权code,会在授权成功时返回给第三方平台，详见第三方平台授权流程说明
	 * @return
	 */
	public  WechatAuthorizationToken getThirdComponentAuthorizerAccessToken(String  authorization_code){
		WechatAuthorizationToken wechatAuthorizationToken = null;
		String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		AccessToken  token = getThirdAccessToken();
		if(token != null){
			wechatAuthorizationToken = ThirdOAuth2.getThirdComponentAuthorizerAccessToken(COMPONENT_APPID, token.getToken(), authorization_code);
		}
		return  wechatAuthorizationToken;
	}

	/**
	 *  获取（刷新）授权公众号的接口调用凭据（令牌）
	 * @param authorizer_appid authorizer_appid
	 * @param authorizer_refresh_token : 授权方的刷新令牌，刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
	 * @return
	 * @throws Exception
	 */
	public  WechatAuthorizationToken  getThirdComponentAuthorizerAccessTokenByRefreshToken( String  authorizer_appid , String authorizer_refresh_token) {
		String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		AccessToken accessToken = getThirdAccessToken();
		WechatAuthorizationToken wechatAuthorizationToken = null;
		if(accessToken != null){
			wechatAuthorizationToken = ThirdOAuth2.getThirdComponentAuthorizerAccessTokenByRefreshToken(COMPONENT_APPID, accessToken.getToken(), authorizer_appid, authorizer_refresh_token);
		}
		return  wechatAuthorizationToken;
	}


	/**
	 * 获取授权方的公众号帐号基本信息
	 * @param authorizer_appid 授权方公众号
	 * @return
	 */
	public WechatAppInfo getThirdComponentAuthorizerInfo(  String  authorizer_appid ){
		WechatAppInfo wechatAppInfo = null;
		String COMPONENT_APPID = CustomizedPropertyConfigurer.getContextProperty("wechat_third.appid");
		AccessToken  token = getThirdAccessToken();
		if(token != null){
			wechatAppInfo = ThirdOAuth2.getThirdComponentAuthorizerInfo(COMPONENT_APPID, token.getToken(), authorizer_appid);
		}
		return  wechatAppInfo;
	}


	/**
	 * 获取直播间的公众号二维码 --直播间
	 * @param roomId 房间Id    //最大为4294967295 32位二进制  前两位为分类详细见WechatQRCodeType
	 * @return
	 */
	public String getParaLiveQrcode( long roomId ){
		return  getParaQrcode( WechatQRCodeType.room.getValue() , roomId , 0 ,0 ,0  );
	}
	/**
	 * 获取直播间的公众号二维码 ---开课  //最大为4294967295 32位二进制  前两位为分类详细见WechatQRCodeType
	 * 目前用于CourseController类中的getQrAddress方法
	 * @param type  类型  房间、课程 详见 WechatQRCodeType
	 * @oaram courseId 课程ID
	 * @param roomId 房间Id
	 * @param appId 用户Id 分享人
	 * @param channelId 渠道Id
	 * @return
	 */
	public String getParaQrcode(String type ,long roomId  , long courseId ,long appId , long channelId){
		String qrcodeUrl = "";
		String sceneId = "";
		String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String logDesc = "type-->" + type + "_roomId-->" + roomId  + "_courseId-->" + courseId;
		//判断是否存在从第三方授权token
		String ext = "0";//后面做扩展
		String appid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, roomId + "");
		if(WechatQRCodeType.third_wechat_or_course_param.getValue().equals(type) ||
				WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue().equals(type) ){//就是针对课程的或者第三方
			//if(WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue().equals(type));
			sceneId = getCourseWechatQRCodeSceneId(type, courseId, roomId, appId, channelId , ext);
		}else{
			sceneId = getWechatQRCodeSceneId(type, roomId);
		}
		if(!Utility.isNullorEmpty(appid) && !ll_appid.equals(appid)){//如果不是龙链默认的公众号
			ll_appid = appid;
		}
		String accessTokenStr = wechatOfficiaService.updateAndGetAuthorizeAccessToken(ll_appid);
		JSONObject jsonObject = null;
		if(WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue().equals(type)){//需要设置有效性时间
			jsonObject = ThirdOAuth2.getParaQrcodeCreateTicket(accessTokenStr, sceneId , WechatConst.ll_live_default_third_wechat_qrcode_use_time);
		}else{
			jsonObject = ThirdOAuth2.getParaQrcodeCreateTicket(accessTokenStr, sceneId);
		}

		String ticket = "";
		if (null != jsonObject) {
			if (jsonObject.containsKey("ticket")) {
				ticket = jsonObject.getString("ticket");//获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
				String url = jsonObject.getString("url");//二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
				String expire_seconds = jsonObject.getString("expire_seconds");//该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。
			}
		}
		if(!Utility.isNullorEmpty(ticket)){
			qrcodeUrl = ThirdOAuth2.getParaQrcode(ticket);
		}else{
			SystemLogUtil.saveSystemLog(LogType.get_invitation_card_fail.getType() ,"0" ,appId , "" ,"{公众号appid:" + ll_appid  + ",sceneId：" + sceneId
					+ "," + logDesc + "}", "获取图片Ticket出错" + "-----------------------------" + jsonObject);
		}
		return  qrcodeUrl;
	}

	/**
	 * 模板发送消息
	 * @param authorizer_appid
	 * @param openid
	 * @param template_id
	 * @param url
	 * @param first
	 * @param keynote1
	 * @param keynote2
	 * @param keynote3
	 * @return
	 */
/*	public    JSONObject sendTemplateMessageById(String authorizer_appid ,String openid,String  template_id , String url ,String first , String keynote1 , String keynote2  ,String keynote3){
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		return  ThirdOAuth2.sendTemplateMessageByOpenid(access_token, openid, template_id, url, first, keynote1, keynote2, keynote3);
	}*/

	/**
	 * 模板 模板发送消息
	 * @param authorizer_appid
	 * @param openid
	 */
	public JSONObject sendTemplateMessageById(String authorizer_appid ,String openid,WechatTemplateMessage wechatTemplateMessage){
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		//让线程去执行
		WechatSendTempleMsgDataRunner wechatSendTempleMsgDataRunner = new WechatSendTempleMsgDataRunner(access_token, openid,wechatTemplateMessage);
		threadPool.execute(wechatSendTempleMsgDataRunner);
		//return  ThirdOAuth2.sendTemplateMessageByOpenid(access_token, openid, wechatTemplateMessage);
		return  null;
	}


	/**
	 * 获取根据公众号所有用户openid
	 * @param authorizer_appid
	 * @return
	 */
	public   JSONArray getAllUserListByAppid(String authorizer_appid){
		JSONArray jsonArrayPara = new JSONArray();
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		OAuth2Core.getAllUserListByAppid(access_token, "", jsonArrayPara);
		return  jsonArrayPara;
	}

	/**
	 * 预览发送消息 byId
	 * @param authorizer_appid
	 * @param openid
	 * @param media_id
	 * @return
	 */
	public    JSONObject sendPreviewMessageByOpenId(  String authorizer_appid    ,String openid , String  media_id){
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		return OAuth2Core.sendPreviewMessageByOpenId(access_token, openid, media_id);
	}

	/**
	 * 客服发送图文消息
	 * @param authorizer_appid
	 * @param openid
	 * @param title  标题
	 * @param description 描述
	 * @param url  点击链接
	 * @param picurl  图片地址
	 * @return
	 */
	public  JSONObject  sendCustomMessageByOpenId(  String authorizer_appid    ,String openid ,String title , String description, String url , String picurl) {
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		return OAuth2Core.sendCustomMessageByOpenId(access_token, openid, title, description, url, picurl);
	}

	/**
	 * 客服发送文本消息
	 * @param authorizer_appid
	 * @param openid
	 * @param content
	 * @return
	 */
	public  JSONObject  sendCustomTextMessageByOpenId(   String authorizer_appid    ,String openid ,String content){
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		return OAuth2Core.sendCustomTextMessageByOpenId(access_token, openid, content);
	}

	/**
	 * 客服发送图片
	 * @param authorizer_appid
	 * @param openid
	 * @param addresss
	 * @return
	 */
	public  JSONObject  sendCustomMediaMessageByOpenId(String authorizer_appid    ,String openid ,String addresss){
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(authorizer_appid);
		//上传临时图片
		JSONObject jsonobject = OAuth2Core.upload(access_token, WechatMedieType.image.getValue(), addresss);
		if(jsonobject.containsKey("type")) {
			String typeI = jsonobject.getString("type");//授权方appid
			String media_id = jsonobject.getString("media_id");//
			Long created_at = jsonobject.getLong("created_at");//
			return OAuth2Core.sendCustomMediaMessageByOpenId(access_token , openid , WechatMedieType.image.getValue(), media_id);
		}
		return null;
	}
	/**
	 * 客服发送文本消息全网发布
	 * @param authorizerToken
	 * @param openid
	 * @param content
	 * @return
	 */
	public  JSONObject  sendAllNetCustomTextMessageByOpenId(   String authorizerToken    ,String openid ,String content){
		return OAuth2Core.sendCustomTextMessageByOpenId(authorizerToken, openid, content);
	}

	/**
	 * 群发客服消息
	 * @param authorizer_appid
	 */
	public void sendCustomMessageAll(String authorizer_appid   ,String title , String description, String url , String picurl) {
		JSONArray jsonArray = getAllUserListByAppid( authorizer_appid);
		for (int i = 0 ,l=jsonArray.size() ; i<l ; i++){
			String openid = (String)jsonArray.get(i);
			JSONObject  jsonObject = sendCustomMessageByOpenId(authorizer_appid ,openid ,title , description ,url,picurl);
		}
	}
	/**
	 * 根据 公众号  发送模板消息，所有的关注的
	 * @param authorizer_appid
	 */
	public void sendTemplateMassageAll(String authorizer_appid   ,WechatTemplateMessage wechatTemplateMessage) {
		JSONArray jsonArray = getAllUserListByAppid(authorizer_appid);
		for (int i = 0 ,l=jsonArray.size() ; i< l ; i++){
			String openid = (String)jsonArray.get(i);
			sendTemplateMessageById( authorizer_appid , openid,wechatTemplateMessage);
		}
	}
	/**
	 * 发送模板消息，所有的关注的 . 根据 公众号appId  查出 user_follow_wechat_official 中 包含集合中 用户appID 不为0 的用户
	 * @param authorizer_appid
	 */
	public void sendTemplateMassageToFollowWechat(String authorizer_appid ,WechatTemplateMessage wechatTemplateMessage) {
			List<UserFollowWechatOfficial> list =userFollowWechatOfficialService.getUserListByFollowId(authorizer_appid);
			if(list.size()>0){
				for(UserFollowWechatOfficial userFollow : list){
					if(userFollow.getAppId()>0){
						System.out.print("第三方公众号wechatId为-----"+ authorizer_appid+",发送给用户appId----" + userFollow.getAppId());
						sendTemplateMessageById( authorizer_appid ,userFollow.getOpenid(),wechatTemplateMessage);
					}
				}
			}
	}

	/**
	 * 获取带参数的二维码 scenId
	 * @param type  类型  房间、课程 详见WechatQRCodeType
	 * @param id 房间Id或者 课程ID
	 * @return
	 */
	public static  String getWechatQRCodeSceneId(String type ,long id){
		//*if(id >= 100000000 )//超过或者等于1亿,后面考虑
		String sceneId = type + Utility.getAutoDigitComplement(id + "" , 8);
		return  sceneId;
	}

	/**
	 * 为4294967295 32位二进制  前两位为39，那么最大值为94967295 前两位为分类详细见WechatQRCodeType
	 * 存入redis，有效时间和和设置二维码的时间一致 采用散列liveId + courseId + appId + channelId 是否考虑存储数据库
	 * 获取带参数的二维码 scenId
	 * @param type  类型  房间、课程 详见WechatQRCodeType
	 * @param courseId 课程ID
	 * @param roomId 房间Id
	 * @param appId 用户ID,分享人
	 * @param channelId 渠道Id
	 * @param ext 扩展字段 0-默认
	 * @return
	 */
	public  String getCourseWechatQRCodeSceneId(String type ,long courseId  , long roomId , long appId ,long channelId , String ext){
		String sceneId = "";//sceneId采用redis自动增长，如果超过94967295 ，从1开始
		if( WechatQRCodeType.third_wechat_or_course_param.getValue().equals(type)||
				WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue().equals(type)){
			long nextId = redisUtil.incr(RedisKey.ll_live_third_auth_wechat_qrcode_next_num);
			if(nextId > WechatConst.ll_live_third_auth_wechat_qrcode_max_num){
				//考虑同步块状锁，以后考虑分布式
				synchronized(this) {
					nextId = redisUtil.incr(RedisKey.ll_live_third_auth_wechat_qrcode_next_num);//在加一次
					nextId = redisUtil.decrBy(RedisKey.ll_live_third_auth_wechat_qrcode_next_num , WechatConst.ll_live_third_auth_wechat_qrcode_max_num + 1 );//自动
				}
			}
			sceneId =type + Utility.getAutoDigitComplement(nextId + "" , 8);
			//System.out.println("sceneId-->" + sceneId);
			//存入redis
			Map map = new HashMap();
			map.put("liveId" , roomId + "");//直播间Id
			map.put("appId" , appId + "");//分享人Id或者生成人Id
			map.put("courseId" , courseId + "");//课程Id
			map.put("channelId" , channelId + "");//渠道Id
			map.put("ext" , ext);//扩展字段
			redisUtil.hmset(RedisKey.ll_live_third_auth_wechat_qrcode_pre + sceneId, map, WechatConst.ll_live_default_wechat_qrcode_use_time);
		}
		return  sceneId;
	}
	/**
	 * 第三方web端授权登录
	 * @param code
	 * @return
	 */
	public  WeixinApp getThirdComponentWebUser( String code  ){
		String appid = CustomizedPropertyConfigurer.getContextProperty("wechat.web.appid");
		String appSecret = CustomizedPropertyConfigurer.getContextProperty("wechat.web.appSecret");
		WeixinApp weixinApp = ThirdOAuth2.getThirdComponentWebUser(code , appid , appSecret  );
		return weixinApp;
	}

	/**
	 * 获取临时素材
	 * @param mediaid
	 * @return
	 */
	public  byte[] getTemporaryMediaByMediaid(String mediaid ){
		byte[] bytes = null;
		try {
			Map map = this.getTemporaryMediaMapByMediaid(  mediaid );
			if (map == null) return null;
			Object obj = map.get("object");
			if(  obj  instanceof JSONObject){
				log.error("获取临时素材失败,mediaid: " + mediaid + " {}", obj.toString());
			}else {
				bytes = (byte[] ) obj;
				System.out.println(bytes.length);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return bytes ;
	}


	/**
	 * 获取临时素材
	 * @param mediaid
	 * @return
	 */
	public  Map getTemporaryMediaMapByMediaid(String mediaid ){
		String  appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(appid);
		Map result = new HashMap();
		try {
			result =  OAuth2Core.getTemporaryMediaMapByMediaid(access_token, mediaid);
			return result;
		}catch (Exception e){
			e.printStackTrace();
		}
		return result ;
	}

	/**
	 * 获取高清语音临时素材
	 * @param mediaid
	 * @return
	 */
	public  Map getTemporaryMediaHeightMapByMediaid(String mediaid ){
		String  appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String access_token = wechatOfficiaService.updateAndGetAuthorizeAccessToken(appid);
		Map result = new HashMap();
		try {
			result =  OAuth2Core.getTemporaryHeightMediaMapByMediaid(access_token, mediaid);
			return result;
		}catch (Exception e){
			e.printStackTrace();
		}
		return result ;
	}

	public static void main(String[] args) {



		WeixinUtil weixinUtil = new WeixinUtil();
		String appid = "wx4a1e117b08e36e0a";
		String access_token = "74gi8DGg6kNgQIIXM_zN0VGr_NOHRmPOJwxiEoppYVOvK5J3vacNizJxCeF_1_Y_It2v3gjfzV9Mrp_FKc_GPFIjue5RJtruGbihzAj2ZzPB7UdTOZhbkuNp--u8qdXwTPCbADDJFS";
		String openid = "oFL9AvwmmzmFSpNPKWZVXjpQjx3o";
		String template_id = "u4CeZn8ksuYxtwrwJQU8-paL5tkeE8zKv_Ztz492y7Q";
		String url = "http://api.longlianwang.com/weixin/";
		String first = "测试标题";
		String keynote1 = "2017-10-25 10:00";
		String keynote2 = "关于是是 是";
		String keynote3 = "你好的 ";
		String re_acc_token = "refreshtoken@@@drmCj8mEoQY2Qp-eLhdXYJl7lb6NnhloN8XXJeaJYpQ";


		String COMPONENT_APPID ="wx3ea560592eab45d8";
		String COMPONENT_APPSECRET = "518d5ccf47a8f9a9f25e00f43f10d241";
		AccessToken accessToken = null;
		String ticket = "ticket@@@nEeiuoU0xaMyhVRU76LKl29Ul8wdiWFOk0b42Hv9Y5h5nGCRX3KCmHfozw_UEHuM6n8zP5ET2B8aSj4dSqZSHg";
		if(!Utility.isNullorEmpty(ticket)){
			accessToken = new AccessToken();
			//accessToken = ThirdOAuth2.getThirdComponentAccessToken(COMPONENT_APPID, COMPONENT_APPSECRET,ticket);
		}
		String a = "7DuXebG0K5bj1kMXORX5ucbWF5qGsujRbEDoGHdVV_Q1-YRx_u7sRMj9DrMIcAagHWhI5odnUOIkfOs0Uzl8NPtoqg6xcp3PyuSp24_3u5RWtBmSwtq5B_Fe8xd6-U1fIVWcAMDOBC";
		accessToken.setToken(a);
		//WechatAuthorizationToken wechatAuthorizationToken = ThirdOAuth2.getThirdComponentAuthorizerAccessTokenByRefreshToken("wx3ea560592eab45d8" , accessToken.getToken(),"wx4a1e117b08e36e0a",re_acc_token);

		Object obj = OAuth2Core.getTemporaryMediaByMediaid(access_token, "WV8f78EiQwRyl0ciAm5vk46-hdgijYWtAL1a1zOUcxckuw4MvxGFwfB12eqP3GKf");
//		Object inputStream = weixinUtil.getTemporaryMediaByMediaid("W0tYaSt1Mb6c1f3J9S3lFj-QPSl3RRsBFq9R3LCEUD8kyIONOg4zU3e4HfB_HfTY");
		if(  obj  instanceof JSONObject){
//			log.error("获取临时素材失败,mediaid: " + mediaid + " {}", obj.toString());
		}else {
			byte[] bytes = (byte[] ) obj;

			FileImageOutputStream imageOutput = null;
			try {
				imageOutput = new FileImageOutputStream(new File("D:\\000.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				imageOutput.write(bytes, 0, bytes.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				imageOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



		//获取临时素材
		/*Object inputStream = weixinUtil.getTemporaryMediaByMediaid("TGQODCdAcPtT-9iJu1JrdGfedW_37A_fUIDGYM1ev4nrSEIv0nPDfCdxstEHjjALIVhR0b4ucYJv3AQJmGW3FcGl66A7p9zSaZw95JbHs2pOyg9aj2_XLq4VOoQ94yXsPTAeAKDXBI",
				"jbMdH6VZBOCVWW9Hr9ZdLo968_a9OR4Sibr0ROxVxZ3j381_R8U5TsDuGCfe4B8k");*/

		//System.out.println(inputStream);
		//JSONObject jsonObject = OAuth2Core.getCustomSessionStatus(accessToken.getToken(), openid);
		//System.out.println(jsonObject);
		/*JSONObject jsonObject  = OAuth2Core.sendCustomMessageByOpenId(accessToken.getToken(), openid, "客服测试", "你好，欢迎关注", "http://api.longlianwang.com/weixin", "https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=3975159606,587457225&fm=85&s=E8B82AD74AE27488402847650300B07A");
		if(jsonObject.containsKey("errcode")){
			String errcode = jsonObject.getString("errcode");//授权方appid
			String errmsg = jsonObject.getString("errmsg");//
			String msg_id = null;
			if(jsonObject.containsKey("msg_id")){
				msg_id = jsonObject.getString("msg_id");//
			}
			if(!"0".equals(errcode)){
				//发送模板消息
				WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
				wechatTemplateMessage.setFirst("您好，您预约的课程即将开始");
				wechatTemplateMessage.setTempalteId("LE0x2brR1ZiBfRhYeapeRFE4ym_RYumKHHCP9bqAKqk");
				wechatTemplateMessage.setUrl(url);
				wechatTemplateMessage.setKeyword1("关于两性教育");
				wechatTemplateMessage.setKeyword2("2017-02-15 15:22");
				wechatTemplateMessage.setKeyword3("舒友林");
				//wechatTemplateMessage.setKeyword4("haoyyewe@sina.com");
				wechatTemplateMessage.setRemark("请您通过Skype及时完成课程，如有问题，请及时联系客服。");
				ThirdOAuth2.sendTemplateMessageByOpenid(a, openid, wechatTemplateMessage);
			}
			System.out.println(errcode + "-->" +errmsg + "--->" + msg_id );
		} */

		//weixinUtil.sendCustomMessageByOpenId(appid, openid , "");
		/*WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
		wechatTemplateMessage.setFirst("您好，您预约的课程即将开始<img src=\\\"https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=3975159606,587457225&fm=85&s=E8B82AD74AE27488402847650300B07A\\\" > </img> ");
		wechatTemplateMessage.setTempalteId("LE0x2brR1ZiBfRhYeapeRFE4ym_RYusmKHHCP9bqAKqk");
		wechatTemplateMessage.setUrl(url);
		wechatTemplateMessage.setKeyword1("关于两性教育");
		wechatTemplateMessage.setKeyword2("2017-02-15 15:22");
		wechatTemplateMessage.setKeyword3("舒友林");
		//wechatTemplateMessage.setKeyword4("haoyyewe@sina.com");
		wechatTemplateMessage.setRemark("请您通过Skype及时完成课程，如有问题，请及时联系客服。");
		ThirdOAuth2.sendTemplateMessageByOpenid(a, openid, wechatTemplateMessage);
*/

		//预览单发media_id
		//String media_id = "";
/*		OAuth2Core.sendCustomTextMessageByOpenId(accessToken.getToken(), openid,"1)欢迎酸枣的新同学！\n" +
				"2)酸枣在线，是一个直播学习平台。聚集各行业优质老师，分享行业知识和个人经验。老师 1 分钟开通自己的直播间，实现知识变现；学生即刻在线学习，丰富碎片时间。\n" +
				"3)我要开课：<a href='api.longlianwang.com/weixin/createSingleCourse'>1分钟开课点这里</a>\\n" +
				"4)我要听课：<a href='api.longlianwang.com/weixin'>精彩课程点这里</a>");*/
		/*JSONObject jsonobject = OAuth2Core.upload(accessToken.getToken(), WechatMedieType.image.getValue(), "http://file.llkeji.com/upload/2017/06/f039c454885d4d1abe0eabec1603c032.jpg");
		if(jsonobject.containsKey("type")) {
			String typeI = jsonobject.getString("type");//授权方appid
			String media_id = jsonobject.getString("media_id");//
			Long created_at = jsonobject.getLong("created_at");//
			OAuth2Core.sendCustomMediaMessageByOpenId(accessToken.getToken(), openid, WechatMedieType.image.getValue(), media_id);

		}*/


		//发送模板消息
		/*JSONArray jsonArrayPara = new JSONArray();
		String access_token2= "FDsIleaB9ohKTJFfA37rB6pUo1e7UUuR0wiSUvAWSNVVRUfHrFvd-FtD8v0F5scKMOo0ROvzv9BII69d098s0s3zHw6I3rywqIkgeAuoBd9a3__13QreOU2Bxz_XhT4RKPChAJDAFS";
		OAuth2Core.getAllUserListByAppid(access_token2, "", jsonArrayPara);
		System.out.println(jsonArrayPara.size() + "-->" + jsonArrayPara);
		WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
		wechatTemplateMessage.setTempalteId("YMS-mDkkeGzE3Zmt4dqKNmycf1hWsnLExY7hi8_dQno");
		wechatTemplateMessage.setUrl("http://api.longlianwang.com");
		wechatTemplateMessage.setFirst("舒友林在测试哈！");
		wechatTemplateMessage.setKeyword1("syl");
		wechatTemplateMessage.setKeyword2("2017-03-04");
		wechatTemplateMessage.setKeyword3("suanzao");
		wechatTemplateMessage.setKeyword4("");
		ExecutorService threadPool  = Executors.newFixedThreadPool(20);
		for (int i=0 ; i<5; i++){
			for (int j=0 ; j<jsonArrayPara.size(); j++){
				WechatSendTempleMsgDataRunner wechatSendTempleMsgDataRunner = new WechatSendTempleMsgDataRunner(access_token2, jsonArrayPara.get(j).toString(),wechatTemplateMessage);
				threadPool.execute(wechatSendTempleMsgDataRunner);
			}
		}
		*/

		//String redirect_uri = URLEncoder.encode("http://wechat.llkeji.com/wechat/loginBackcall");
		//System.out.println(redirect_uri);
		/*String redirect_uri = URLEncoder.encode("http://www.longlianwang.com");
		String URL= "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + ParamesAPI.appId +
				"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		System.out.println(URL);*/

		/*String s = "15010098997";
		System.out.println(s.substring(s.length() - 6));

		String test = "code=1&test2=4&test5=4";
		String paraArray[] = test.split("&");
		String param = "";
		for (int i = 0 ; i<paraArray.length ; i++){
			String singleParam = paraArray[i];
			String []singleArray = singleParam.split("=");
			if(singleArray.length == 2 && !"code".equals(singleArray[0])){
				if(Utility.isNullorEmpty(param)){
					param =  singleArray[0] +"=" + singleArray[1];
				}else{
					param = param + "&" + singleArray[0] +"=" + singleArray[1];
				}
			}
		}
		System.out.println(param);*/
	}
}
