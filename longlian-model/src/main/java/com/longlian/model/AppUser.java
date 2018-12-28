package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:app_user
 */
public class AppUser implements Serializable {

    private long id;
    private String openid;//微信openID
    private String mobile;//手机号
    private String name;//姓名
    private String status;//0-正常 1-禁用
    private int level;//用户级别ID
    private Date createTime;//创建时间
    private int loginCount;//登录次数
    private long invitationAppId;//邀请人APPID
    private String userPriv;//用户权限 0-默认 1-待定
    private String password;//密码
    private String photo;//图像
    private String city;//所属城市
    private String fromType;//用户来源 0-微信 1-待定
    private String idCard;//身份证号
    private String idCardFront;//身份证号正面
    private String idCardRear;//身份证号反面
    private String gender;//性别0-男 1-女（后面可有身份证号自动关联出生年月）
    private Date birthday;//出生日期（后面可有身份证号自动关联出生年月）
    private String yunxinToken;//云信token
    private String userType;//用户身份 0:学生 1:老师 2:机构
    private String unionid;//只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段;
    private String isFollowLlWechat;//是否关注龙链微信公众号0-没有 1-已关注 2-关注老师（第三方公众号）
    private String  realName;    //老师介绍
    private Date  teacherCreateTime;
    private String isVirtualUser;   //是否是虚拟用户0-不是 1-是
    private String blurPhoto;//模糊头像
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBlurPhoto() {
        return blurPhoto;
    }

    public void setBlurPhoto(String blurPhoto) {
        this.blurPhoto = blurPhoto;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public long getInvitationAppId() {
        return invitationAppId;
    }

    public void setInvitationAppId(long invitationAppId) {
        this.invitationAppId = invitationAppId;
    }

    public String getUserPriv() {
        return userPriv;
    }

    public void setUserPriv(String userPriv) {
        this.userPriv = userPriv;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardRear() {
        return idCardRear;
    }

    public void setIdCardRear(String idCardRear) {
        this.idCardRear = idCardRear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getYunxinToken() {
        return yunxinToken;
    }

    public void setYunxinToken(String yunxinToken) {
        this.yunxinToken = yunxinToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getIsFollowLlWechat() {
        return isFollowLlWechat;
    }

    public void setIsFollowLlWechat(String isFollowLlWechat) {
        this.isFollowLlWechat = isFollowLlWechat;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getTeacherCreateTime() {
        return teacherCreateTime;
    }

    public void setTeacherCreateTime(Date teacherCreateTime) {
        this.teacherCreateTime = teacherCreateTime;
    }

    public String getIsVirtualUser() {
        return isVirtualUser;
    }

    public void setIsVirtualUser(String isVirtualUser) {
        this.isVirtualUser = isVirtualUser;
    }
}
