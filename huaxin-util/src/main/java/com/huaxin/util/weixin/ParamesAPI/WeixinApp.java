package com.huaxin.util.weixin.ParamesAPI;

/**
 * Created by Administrator on 2016/10/14.
 */
public class WeixinApp  {
    private String access_token ;//网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
    private String refresh_token ;///access_token接口调用凭证超时时间，单位（秒）
    private String openid ;//用户刷新access_token
    private String scope ;//用户授权的作用域，使用逗号（,）分隔
    private String unionid;//	只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
