package com.huaxin.util.weixin.oauth2;

import com.huaxin.util.Utility;
import com.huaxin.util.weixin.ParamesAPI.*;
import com.huaxin.util.weixin.type.WechatTemplateMessage;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2017/1/22.
 */
public class ThirdOAuth2 {

    /**
     * 获取第三方 component_access_token
     * @param appid   第三方平台APPID
     * @param secret 第三方平台appsecret
     * @param component_verify_ticket 微信服务器每隔10分钟会向第三方的消息接收地址推送一次component_verify_ticke
     * @return
     * @throws Exception
     */
    public static AccessToken  getThirdComponentAccessToken( String appid  , String secret , String component_verify_ticket) {
        AccessToken token = new AccessToken();
        String CODE_TO_USERINFO_NEW = ParamesUriAPI.third_component_access_token;
        String data =  "{\"component_appid\":\"" + appid + "\","+
                          "\"component_appsecret\":\"" +  secret + "\","+
                          "\"component_verify_ticket\":\"" +  component_verify_ticket + "\""+
                        "}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(CODE_TO_USERINFO_NEW, "POST", data);
        if (null != jsonobject) {
            System.out.println(jsonobject+"-----------------------------");
            if(jsonobject.containsKey("component_access_token")){
                String component_access_token = jsonobject.getString("component_access_token");//网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
                int expires_in = jsonobject.getInt("expires_in");//access_token接口调用凭证超时时间，单位（秒）
                token.setToken(component_access_token);
                token.setExpiresIn(expires_in);
                return token;
            } else {
                System.out.println("错误码：" + jsonobject);
            }
        } else {
            System.out.println("获取授权失败了，自己找原因。。。");
        }
        return null;
    }


    /**
     * 根据code获取用户信息
     * @param code
     *            通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     *            appid 服务号appid
     *            componentAppid 第三方 appid
     *            componentToken 第三方 token
     * */
    public static WeixinApp getUser( String code , String appid , String componentAppid , String componentToken ) {
        String CODE_TO_USERINFO_NEW = ParamesUriAPI.third_component_get_user_access_token.replace("APPID", appid).replace("CODE", code)
                .replace("COMPONENT_APP_ID", componentAppid).replace("COMPONENT_ACCESS_TOKEN", componentToken);
        JSONObject jsonobject = HttpRequestUtil.httpRequest(CODE_TO_USERINFO_NEW, "GET", null);
        WeixinApp weixinApp = null;
        if (null != jsonobject) {
            if(jsonobject.containsKey("access_token")){
                System.out.println(jsonobject+"-----------------------------");
                String access_token = jsonobject.getString("access_token");//网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
                String refresh_token = jsonobject.getString("refresh_token");//access_token接口调用凭证超时时间，单位（秒）
                String openid = jsonobject.getString("openid");//用户刷新access_token
                String scope =  jsonobject.getString("scope");//用户授权的作用域，使用逗号（,）分隔
                System.out.println("获取信息成功，openid:" + openid);
                weixinApp = new WeixinApp();
                weixinApp.setOpenid(openid);
                weixinApp.setAccess_token(access_token);
                weixinApp.setRefresh_token(refresh_token);
                weixinApp.setScope(scope);
            } else {
                int errorrcode = jsonobject.getInt("errcode");
                String errmsg = jsonobject.getString("errmsg");
                System.out.println("错误码：" + errorrcode + ",错误信息：" + errmsg);
            }
        } else {
            System.out.println("获取授权失败了，自己找原因。。。");
        }
        return weixinApp;
    }


    /**
     * 获取第三方web端授权登录 获取用户
     * @param code
     * @param appid
     * @param appSecret
     * @return
     */
    public static WeixinApp getThirdComponentWebUser( String code , String appid , String appSecret ) {
        String CODE_TO_USERINFO_NEW = ParamesUriAPI.third_component_web_login.replace("APPID", appid).replace("CODE", code).replace("SECRET", appSecret);
        JSONObject jsonobject = HttpRequestUtil.httpRequest(CODE_TO_USERINFO_NEW, "GET", null);
        WeixinApp weixinApp = null;
        if (null != jsonobject) {
            if(jsonobject.containsKey("access_token")){
                System.out.println(jsonobject+"-----------------------------");
                String access_token = jsonobject.getString("access_token");//网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
                String refresh_token = jsonobject.getString("refresh_token");//access_token接口调用凭证超时时间，单位（秒）
                String openid = jsonobject.getString("openid");//用户刷新access_token
                String scope =  jsonobject.getString("scope");//用户授权的作用域，使用逗号（,）分隔
                String unionid =  jsonobject.getString("unionid");//用户授权的作用域，使用逗号（,）分隔
                System.out.println("获取信息成功，openid:" + openid);
                weixinApp = new WeixinApp();
                weixinApp.setOpenid(openid);
                weixinApp.setAccess_token(access_token);
                weixinApp.setRefresh_token(refresh_token);
                weixinApp.setScope(scope);
                weixinApp.setUnionid(unionid);
            } else {
                int errorrcode = jsonobject.getInt("errcode");
                String errmsg = jsonobject.getString("errmsg");
                System.out.println("错误码：" + errorrcode + ",错误信息：" + errmsg);
            }
        } else {
            System.out.println("获取第三方授权登录失败了，自己找原因。。。");
        }
        return weixinApp;
    }

    /**
     * 根据code获取用户信息
     * 			  调用接口凭证
     * @param code
     *            通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     *             componentAppid 第三方 appid
     *            componentToken 第三方 token
     * */
    public static WeixinAppUser getUserAppUser( String code, String appid   , String componentAppid , String componentToken ) {
        WeixinApp weixinApp =  getUser(code, appid, componentAppid, componentToken);
        WeixinAppUser weixinAppUser = OAuth2Core.getUserAppUser(weixinApp);
        return weixinAppUser;
    }

    /**
     * 获取第三方平台 PreAuthCode
     * @param appid  appid   第三方平台APPID
     * @param component_access_token
     * @return
     * @throws Exception
     */
    public static String  getThirdComponentPreAuthCode( String appid , String  component_access_token){
        String new_url = ParamesUriAPI.third_component_pre_auth_code.replace("COMPONENT_ACCESS_TOKEN",component_access_token);
        String data =  "{\"component_appid\":\"" + appid + "\"}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        if (null != jsonobject) {
            System.out.println(jsonobject+"-----------------------------获取获取第三方平台 PreAuthCode");
            if(jsonobject.containsKey("pre_auth_code")){
                String pre_auth_code = jsonobject.getString("pre_auth_code");//网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
                String expires_in = jsonobject.getString("expires_in");//access_token接口调用凭证超时时间，单位（秒）
                 return pre_auth_code;
            } else {
                System.out.println("错误码：" + jsonobject);
            }
        } else {
            System.out.println("获取获取第三方平台 PreAuthCode失败了，自己找原因。。。");
        }
        return null;
    }


    /**
     *  使用授权码换取公众号的接口调用凭据和授权信息
     * @param appid  appid   第三方平台APPID
     * @param component_access_token 第三方平台token
     * @param authorization_code : 授权code,会在授权成功时返回给第三方平台，详见第三方平台授权流程说明
     * @return
     * @throws Exception
     */
    public static WechatAuthorizationToken getThirdComponentAuthorizerAccessToken( String appid , String component_access_token  , String  authorization_code){
        WechatAuthorizationToken wechatAuthorizationToken = null;
        String new_url = ParamesUriAPI.third_component_api_query_auth.replace("COMPONENT_ACCESS_TOKEN",component_access_token);
        String data =  "{\"component_appid\":\"" + appid + "\","+
                "\"authorization_code\":\"" + authorization_code + "\""+
                "}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        if (null != jsonobject) {
            System.out.println(jsonobject+"-----------------------------使用授权码换取公众");
            if(jsonobject.containsKey("authorization_info")){
                JSONObject authorization_infoObject = jsonobject.getJSONObject("authorization_info");
                String pre_auth_code = authorization_infoObject.getString("authorizer_appid");//授权方appid
                String authorizer_access_token = authorization_infoObject.getString("authorizer_access_token");//授权方接口调用凭据（在授权的公众号具备API权限时，才有此返回值），也简称为令牌
                String authorizer_refresh_token = authorization_infoObject.getString("authorizer_refresh_token");//接口调用凭据刷新令牌（在授权的公众号具备API权限时，才有此返回值），刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。 一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
                String func_info = authorization_infoObject.getString("func_info");//权限集合
                int expires_in = authorization_infoObject.getInt("expires_in");//access_token接口调用凭证超时时间，单位（秒）
                //拼接授权地址
                System.out.println(pre_auth_code + "-->" + authorizer_access_token);
                wechatAuthorizationToken = new WechatAuthorizationToken();
                wechatAuthorizationToken.setExpiresIn(expires_in);
                wechatAuthorizationToken.setToken(authorizer_access_token);
                wechatAuthorizationToken.setRefreshToken(authorizer_refresh_token);
                wechatAuthorizationToken.setAppid(pre_auth_code);
            } else {
                System.out.println("错误码：" + jsonobject);
            }
        } else {
            System.out.println("使用授权码换取公众号的接口调用凭据和授权信息失败了，自己找原因。。。");
        }
        return wechatAuthorizationToken;
    }


    /**
     *  获取（刷新）授权公众号的接口调用凭据（令牌）
     * @param appid  appid   第三方平台APPID
     * @param component_access_token 第三方平台token
     * @param authorizer_appid authorizer_appid
     * @param authorizer_refresh_token : 授权方的刷新令牌，刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
     * @return
     * @throws Exception
     */
    public static WechatAuthorizationToken  getThirdComponentAuthorizerAccessTokenByRefreshToken( String appid , String component_access_token  , String  authorizer_appid , String authorizer_refresh_token) {
        WechatAuthorizationToken wechatAuthorizationToken = null;
        String new_url = ParamesUriAPI.third_component_api_authorizer_token.replace("COMPONENT_ACCESS_TOKEN",component_access_token);
        String data =  "{\"component_appid\":\"" + appid + "\","+
                "\"authorizer_appid\":\"" + authorizer_appid + "\","+
                "\"authorizer_refresh_token\":\"" + authorizer_refresh_token + "\""+
                "}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        if (null != jsonobject) {
            if(jsonobject.containsKey("authorizer_access_token")){
                String authorizer_access_token = jsonobject.getString("authorizer_access_token");//授权方appid
                String authorizer_refresh_token_new = jsonobject.getString("authorizer_refresh_token");//接口调用凭据刷新令牌（在授权的公众号具备API权限时，才有此返回值），刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。 一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
                int expires_in = jsonobject.getInt("expires_in");//access_token接口调用凭证超时时间，单位（秒）S
                wechatAuthorizationToken = new WechatAuthorizationToken();
                wechatAuthorizationToken.setExpiresIn(expires_in);
                wechatAuthorizationToken.setToken(authorizer_access_token);
                wechatAuthorizationToken.setRefreshToken(authorizer_refresh_token);
                wechatAuthorizationToken.setAppid(authorizer_appid);
            } else {
                System.out.println("获取（刷新）授权公众号错误码:component_appid:" + appid + ",authorizer_appid:" + authorizer_appid + ";" + jsonobject);
            }
        } else {
            System.out.println("获取（刷新）授权公众号的接口调用凭据（失败了，自己找原因。。。");
        }
        return wechatAuthorizationToken;
    }


    /**
     * 获取授权方的公众号帐号基本信息
     * @param appid 第三平台微信号
     * @param component_access_token 第三平台token
     * @param authorizer_appid 授权方公众号
     * @return
     */
    public static WechatAppInfo getThirdComponentAuthorizerInfo( String appid , String component_access_token  , String  authorizer_appid ){
        WechatAppInfo wechatAppInfo = null;
        String new_url = ParamesUriAPI.third_component_api_get_authorizer_info.replace("COMPONENT_ACCESS_TOKEN",component_access_token);
        String data =  "{\"component_appid\":\"" + appid + "\","+
                "\"authorizer_appid\":\"" + authorizer_appid + "\""+
                "}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        if (null != jsonobject) {
            System.out.println(jsonobject+"-----------------------------获取授权方");
            if(jsonobject.containsKey("authorizer_info")){
                JSONObject authorizer_info = jsonobject.getJSONObject("authorizer_info");
                String nick_name = authorizer_info.getString("nick_name");//昵称
                String head_img = "";
                if(jsonobject.containsKey("head_img")){
                    head_img = authorizer_info.getString("head_img");//授权方头像
                }
                JSONObject service_type_info = authorizer_info.getJSONObject("service_type_info");//	授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
                String service_type = service_type_info.getString("id");//授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号

                JSONObject verify_type_info = authorizer_info.getJSONObject("verify_type_info");//	授权方认证类型，
                String verify_type = service_type_info.getString("id");//-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证


                String user_name = authorizer_info.getString("user_name");//授权方公众号的原始ID
                String principal_name = authorizer_info.getString("principal_name");//公众号的主体名称
                String alias = authorizer_info.getString("alias");//授权方公众号所设置的微信号，可能为空
                String qrcode_url = authorizer_info.getString("qrcode_url");//开发者最好自行也进行保存
                wechatAppInfo = new WechatAppInfo();
                wechatAppInfo.setNickName(nick_name);
                wechatAppInfo.setHeadImg(head_img);
                wechatAppInfo.setAlias(alias);
                wechatAppInfo.setServiveTypeInfo(service_type);
                wechatAppInfo.setVerifyTypeInfo(verify_type);
                wechatAppInfo.setPrincipalName(principal_name);
                wechatAppInfo.setUserName(user_name);
                wechatAppInfo.setQrcodeUrl(qrcode_url);
            } else {
                System.out.println("错误码：" + jsonobject);
            }
        } else {
            System.out.println("获取授权方的公众号帐号基本信息失败了，自己找原因。。。");
        }
        return wechatAppInfo;
    }


    /**
     * 生成带参数的二维码
     * @param caccess_token
     * @param scene_id 参数ID
     * @return
     */
    public static JSONObject  getParaQrcodeCreateTicket( String caccess_token , String scene_id ){
       return   getParaQrcodeCreateTicket(  caccess_token ,  scene_id , WechatConst.ll_live_default_wechat_qrcode_use_time);
    }
    public static  JSONObject  getParaQrcodeCreateTicket( String caccess_token , String scene_id ,int useTime ){
        if(Utility.isNullorEmpty(caccess_token)) return  null;
        String new_url = ParamesUriAPI.get_para_qrcode_create_ticket.replace("ACCESS_TOKEN",caccess_token);
        String data = "{\"expire_seconds\":" + useTime + ", \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "+ scene_id +"}}}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        return  jsonobject;
    }

    /**
     * 获取待参数的二维码
     * @param ticket
     * @return
     */
    public static String getParaQrcode( String ticket){
        String new_url = ParamesUriAPI.get_para_qrcode.replace("TICKET", ticket);
        return  new_url;
    }


    /**
     * 模板发送
     * @param access_token
     * @param openid
     * @param template_id
     * @param url
     * @param first
     * @param keynote1
     * @param keynote2
     * @param keynote3
     * @return
     */
/*    public  static  JSONObject sendTemplateMessageById(String access_token ,String openid,String  template_id , String url ,String first , String keynote1 , String keynote2  ,String keynote3){
        String new_url = ParamesUriAPI.third_component_send_template.replace("ACCESS_TOKEN",access_token);
        String data  = "{" +
                 "\"touser\":\""  +openid + "\"," +
                "\"template_id\":\"" + template_id +"\"," +
                 "\"url\":\"" + url + "\"," +
                "\"data\":{"+
                    "\"first\": {"+
                         "\"value\":\"" + first + "\"," +
                         "\"color\":\"#173177\"" +
                             "}," +
                    "\"keyword1\":{"+
                        "\"value\":\"" + keynote1 + "\"," +
                         "\"color\":\"#173177\"" +
                         "}," +
                    "\"keyword2\":{"+
                         "\"value\":\"" + keynote2 + "\"," +
                         "\"color\":\"#173177\"" +
                         "}," +
                    "\"remark\":{" +
                        "\"value\":\"" + keynote3 + "\"," +
                        "\"color\":\"#173177\"" +
                     "}" +
                  "}" +
             "}";
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        System.out.println("消息发送模板jsonobject----->" + jsonobject );
        return jsonobject;
    }*/

    /**
     * 模板发送
     * @param access_token
     * @param openid
     * @param wechatTemplateMessage  发送消息的模板对象
     * @return
     */
    public  static  JSONObject sendTemplateMessageByOpenid(String access_token ,String openid,WechatTemplateMessage wechatTemplateMessage){
        String new_url = ParamesUriAPI.third_component_send_template.replace("ACCESS_TOKEN",access_token);
        String data  = "{" +
                "\"touser\":\""  +openid + "\"," +
                "\"template_id\":\"" + wechatTemplateMessage.getTempalteId() +"\"," +
                "\"url\":\"" + wechatTemplateMessage.getUrl() + "\"," +
                "\"data\":{"+
                "\"first\": {"+
                "\"value\":\"" +getFormatJsonString( wechatTemplateMessage.getFirst()) +"\\n"+ "\"," +
                "\"color\":\"" + wechatTemplateMessage.getFirstColor() + "\"" +
                "},"+
                "\"keyword1\":{"+
                "\"value\":\"" + getFormatJsonString(wechatTemplateMessage.getKeyword1()) + "\"," +
                "\"color\":\"" + wechatTemplateMessage.getKeyword1Color() + "\"" +
                "}," ;
                if(!Utility.isNullorEmpty(wechatTemplateMessage.getKeyword2())){
                    data = data +
                            "\"keyword2\":{"+
                            "\"value\":\"" + getFormatJsonString(wechatTemplateMessage.getKeyword2()) + "\"," +
                            "\"color\":\"" + wechatTemplateMessage.getKeyword2Color() + "\"" +
                            "},";
                }
                if(!Utility.isNullorEmpty(wechatTemplateMessage.getKeyword3())){
                    data = data +
                            "\"keyword3\":{"+
                            "\"value\":\"" + getFormatJsonString(wechatTemplateMessage.getKeyword3()) + "\"," +
                            "\"color\":\"" + wechatTemplateMessage.getKeyword3Color() + "\"" +
                            "},";
                }
                if(!Utility.isNullorEmpty(wechatTemplateMessage.getKeyword4())){
                    data = data +
                            "\"keyword4\":{"+
                            "\"value\":\"" + getFormatJsonString(wechatTemplateMessage.getKeyword4()) + "\"," +
                            "\"color\":\"" + wechatTemplateMessage.getKeyword4Color() + "\"" +
                            "}," ;
                }
                if(!Utility.isNullorEmpty(wechatTemplateMessage.getRemark())){
                    data = data +
                            "\"remark\":{" +
                            "\"value\":\"" +"\\n"+ getFormatJsonString(wechatTemplateMessage.getRemark()) + "\"," +
                            "\"color\":\"" + wechatTemplateMessage.getRemarkColor() + "\"" +
                            "}" +
                            "}" +
                            "}";
                }else{
                    data = data +
                            "\"remark\":{" +
                            "\"value\":\"" +   getFormatJsonString(wechatTemplateMessage.getRemark()) + "\"," +
                            "\"color\":\"" + wechatTemplateMessage.getRemarkColor() + "\"" +
                            "}" +
                            "}" +
                            "}";
                }
              
        JSONObject jsonobject = HttpRequestUtil.httpRequest(new_url, "POST", data);
        System.out.println("消息发送模板jsonobject----->" + jsonobject );
        return jsonobject;
    }

    /**
     * 转json字符串
     * @param str
     * @return
     */
    public static String getFormatJsonString(String str){
        if(str != null){
            str = str.replaceAll("\"", "”");
        }
        return  str;
    }

    public static void main(String[] args) {
        //String ticket = "gQFY8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyQTd1dndLa185MF8xek5EWk5vMWkAAgRx7bNYAwSAOgkA";
        //getParaQrcode(ticket);
        WechatTemplateMessage wechatTemplateMessage = new WechatTemplateMessage();
        wechatTemplateMessage.setFirst("您关注的直播间【bb】即将开课,欢迎加入哦！");
        wechatTemplateMessage.setKeyword1("bb");
        wechatTemplateMessage.setKeyword3("");
        wechatTemplateMessage.setKeyword4("");
        wechatTemplateMessage.setTempalteId("GSfT9l936z2nlpTt57HC21DRVx6qzlCQ4VxSwBTkJcA");
        wechatTemplateMessage.setRemark("");
        wechatTemplateMessage.setUrl("http://www.suanzao.com.cn");
        String token="9_QBpEA2sB-Kwhtst5WqzgAcaK296_8I-1qCKpGqtVSMPrUo9Zfj4TuCR_Xb-bOa6GGEuLVYS5949LWGsDpsfNjVPJzlm_RUBvoYPDd0VJWBuCz5Aj3ueBq1V2mVUZ5cg6EKXkH76eFiGvEithKMIiAJDNPL";
        //String openid="oPZNRwHiq3mq-yJXA5s6H4-mqnUM";//zzp
        String openid="oPZNRwAw3yyXiQFvxOnVihqt5n5k";
        sendTemplateMessageByOpenid(token,openid,wechatTemplateMessage);
    }
}
