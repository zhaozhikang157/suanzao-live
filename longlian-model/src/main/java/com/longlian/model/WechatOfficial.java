package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * tablename:wechat_official
 */
public class WechatOfficial implements Serializable {

    private long id;
    private String appid;//微信公众号
    private String serviceType;//授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务好
    private String verifyTypeInfo;//comment '-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证';
    private String accessToken;//微信公众号TOKEN
    private String refreshToken;//接口调用凭据刷新令牌
    private String userName;//授权方公众号的原始ID
    private String nickName;//授权方昵称
    private String headImg;//授权方头像
    private String principalName;//公众号的主体名称
    private String qrcodeUrl;//二维码图片的URL
    private long liveId;//直播间ID
    private String liveName;//直播间名称
    private String reserveReminderId;//预约提醒ID（微信）
    private String contactWechat;//联系微信号
    private Date createTime;//创建时间
    private String status;//状态 0-已授权 1-取消
    private String contactMobile; //联系人手机号
    private Date   bindTime ; // 绑定时间
    private String reservePreReminderId;
    private String auditStatus; //审核状态
    private Date   auditTime;  // 审核时间
    private long  auditUserId; //审核人ID
    private String auditUserName; //审核人姓名
    private String auditRemark; //审核备注
    private long  manager; //销售负责人
    private Date freeDate; //免费有效期
    private BigDecimal payAmount; //免费有效期


    public Map<String , String> toMap() {
        Map<String , String> res = new HashMap();
        res.put("id", this.getId()+"");
        res.put("appid", this.getAppid()== null ? "" :  this.getAppid() );
        res.put("nickName", this.getNickName()== null ? "" :  this.getNickName());
        res.put("liveId", this.getLiveId() + "");
        res.put("liveName", this.getLiveName()== null ? "" :  this.getLiveName());
        res.put("contactMobile", this.getContactMobile()== null ? "" :  this.getContactMobile());
        return res;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(String verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken == null ? null : refreshToken.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg == null ? null : headImg.trim();
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName == null ? null : principalName.trim();
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl == null ? null : qrcodeUrl.trim();
    }

    public long getLiveId() {
        return liveId;
    }

    public void setLiveId(long liveId) {
        this.liveId = liveId;
    }

    public String getLiveName() {
        return liveName;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }

    public String getReserveReminderId() {
        return reserveReminderId;
    }

    public void setReserveReminderId(String reserveReminderId) {
        this.reserveReminderId = reserveReminderId == null ? null : reserveReminderId.trim();
    }

    public String getContactWechat() {
        return contactWechat;
    }

    public void setContactWechat(String contactWechat) {
        this.contactWechat = contactWechat;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public String getReservePreReminderId() {
        return reservePreReminderId;
    }

    public void setReservePreReminderId(String reservePreReminderId) {
        this.reservePreReminderId = reservePreReminderId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public long getManager() {
        return manager;
    }

    public void setManager(long manager) {
        this.manager = manager;
    }

    public Date getFreeDate() {
        return freeDate;
    }

    public void setFreeDate(Date freeDate) {
        this.freeDate = freeDate;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
}
