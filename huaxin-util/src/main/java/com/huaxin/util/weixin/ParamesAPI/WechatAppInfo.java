package com.huaxin.util.weixin.ParamesAPI;

/**
 * Created by Administrator on 2017/2/10.
 */
public class WechatAppInfo {

    private String nickName;//nick_name;//昵称
    private String headImg;//授权方头像
    private String serviveTypeInfo;//service_type_info	授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
    private String verifyTypeInfo;//verify_type_info	授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
    private String userName;//user_name	授权方公众号的原始ID
    private String principalName;//principal_name	公众号的主体名称
    private String alias;//	授权方公众号所设置的微信号，可能为空
    private String qrcodeUrl;//qrcode_url	 二维码图片的URL，开发者最好自行也进行保存
    private String funcInfo;//公众号授权给开发者的权限集列表，ID为1到15时分别代表：

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getServiveTypeInfo() {
        return serviveTypeInfo;
    }

    public void setServiveTypeInfo(String serviveTypeInfo) {
        this.serviveTypeInfo = serviveTypeInfo;
    }

    public String getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(String verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(String funcInfo) {
        this.funcInfo = funcInfo;
    }
}
