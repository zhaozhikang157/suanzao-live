package com.huaxin.util.weixin.ParamesAPI;

/**
 * 参数API类
 *
 * @author syl
 * @date 2016.10.13
 */
public class ParamesUriAPI {

	//授权并返回redirect_uri
	public static String oauth2_authorize ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID"+
			"&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


	//根据code获取用户获取access_token
	public static String GET_APP = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	public static String getUnionidAndOpenid4XcX= "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
	//根据access_token 获取用户信息
	public static String GET_APP_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	//根据access_token 获取用户信息 包含UN是否关注
	public static String GET_APP_USER_INFO_CONTAIN_IS_FOLLOW = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	// 获取access_token的接口地址（GET）
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	// 获取JSAPI access_token的接口地址
	public final static String jsapi_access_token_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

	//上传临时图片素材
	public final static String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	//上传临时图片素材
	public final static String upload_https = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";


	//上传图文素材 ？？？
	public final static String  upload_news = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";

	//获取临时素材
	public final static String  get_temporary_media = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";

	//获取语音高清临时素材
	public final static String  get_temporary_height_media = "https://api.weixin.qq.com/cgi-bin/media/get/jssdk?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";


	//发送客服消息
	public final static String  custom_send = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";

	//发送客服回话状态 ,暂用不上
	public final static String  get_custom_session_status = "https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=ACCESS_TOKEN&openid=OPENID";

	//拉取用户列表
	public  final static String get_user_list= "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";



	//获取第三方平台component_access_token
	public  final  static  String third_component_access_token  = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

	//获取预授权码pre_auth_code  ,预授权码用于公众号授权时的第三方平台方安全验证。
	public  final  static  String third_component_pre_auth_code = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=COMPONENT_ACCESS_TOKEN";

	//使用授权码换取公众号的接口调用凭据和授权信息
	public  final  static  String third_component_api_query_auth = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=COMPONENT_ACCESS_TOKEN";

	//获取（刷新）授权公众号的接口调用凭据（令牌）
	public  final  static  String third_component_api_authorizer_token = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=COMPONENT_ACCESS_TOKEN";

	//第三方平台授权地址
	public  final  static  String third_component_componentloginpage= "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=COMPONENT_APPID"
			+"&pre_auth_code=PRE_AUTH_CODE&redirect_uri=REDIRECT_URI"  ;

	//获取授权方的公众号帐号基本信息
	public  final  static  String third_component_api_get_authorizer_info= "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=COMPONENT_ACCESS_TOKEN";

	//代公众号发起网页授权
	public  final static  String  third_component_replace_page_authorize = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE&component_appid=COMPONENT_APP_ID#wechat_redirect";
	//代公众号发起网页授权 通过code换取access_token
	public static String  third_component_get_user_access_token = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APP_ID&component_access_token=COMPONENT_ACCESS_TOKEN";

	//从行业模板库选择模板到帐号后台，获得模板ID的过程可在MP中完成。为方便第三方开发者，提供通过接口调用的方式来获取模板ID
	public  final static  String third_component_api_add_template= "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
	//发送模板消息
	public  final static  String third_component_send_template = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";

	// 群发消息
	public final  static String  send_all= "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";

	//根据OpenID列表群发
	public  final  static String send_by_openids = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
	//接口发送消息给指定用户,预览接口
	public final  static  String send_preview_by_Openid = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";

	//  创建带参数的公账号二维码
	public final  static  String get_para_qrcode_create_ticket = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	public final  static  String get_para_qrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";

	//第三方公众平台  网页登录
	public final  static  String third_component_web_login =  "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

}
