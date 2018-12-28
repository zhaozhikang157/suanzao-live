package com.huaxin.util.weixin.ParamesAPI;

/**
 * Created by Administrator on 2016/10/14.
 */
public class WeixinAppUser extends  WeixinApp {
    private String nickname;//	用户昵称
    private String sex;//	用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private String province;//	用户个人资料填写的省份
    private String city;//	普通用户个人资料填写的城市
    private String country;//	国家，如中国为CN
    private String headimgurl;//	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
    private String privilege;//	用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）

    private String subscribe;//用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    private String isFollowLlWechat;//是否关注龙链公众号 IS_FOLL0W_LL_WECHAT

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }


    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getIsFollowLlWechat() {
        return isFollowLlWechat;
    }

    public void setIsFollowLlWechat(String isFollowLlWechat) {
        this.isFollowLlWechat = isFollowLlWechat;
    }
}
