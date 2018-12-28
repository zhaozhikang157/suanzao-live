package com.longlian.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "wechat_official_room")
public class WechatOfficialRoom{

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 微信公众号
     */
    @Column(name = "WECHAT_ID")
    private String wechatId;

    /**
     * 直播间ID
     */
    @Column(name = "LIVE_ID")
    private Long liveId;

    /**
     * 直播间名称
     */
    @Column(name = "LIVE_NAME")
    private String liveName;

    /**
     * -1审核未通过 0-待审核 1-审核通过
     */
    @Column(name = "AUDIT_STATUS")
    private String auditStatus;

    /**
     * 审核时间
     */
    @Column(name = "AUDIT_TIME")
    private Date auditTime;

    /**
     * 审核人ID
     */
    @Column(name = "AUDIT_USER_ID")
    private Long auditUserId;

    /**
     * 审核人姓名
     */
    @Column(name = "AUDIT_USER_NAME")
    private String auditUserName;

    /**
     * 审核备注
     */
    @Column(name = "AUDIT_REMARK")
    private String auditRemark;

    /**
     * 销售负责人
     */
    @Column(name = "MANAGER")
    private Long manager;

    /**
     * 免费有效期
     */
    @Column(name = "FREE_DATE")
    private Date freeDate;

    /**
     * 绑定时间
     */
    @Column(name = "BIND_TIME")
    private Date bindTime;


    @Column(name = "MOBILE")
    private String mobile;


    @Column(name = "CONTACT_MOBILE")
    private String contactMobile;

    @Column(name = "CONTACT_WECHAT")
    private String contactWechat;
    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取微信公众号
     *
     * @return WECHAT_ID - 微信公众号
     */
    public String getWechatId() {
        return wechatId;
    }

    /**
     * 设置微信公众号
     *
     * @param wechatId 微信公众号
     */
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    /**
     * 获取直播间ID
     *
     * @return LIVE_ID - 直播间ID
     */
    public Long getLiveId() {
        return liveId;
    }

    /**
     * 设置直播间ID
     *
     * @param liveId 直播间ID
     */
    public void setLiveId(Long liveId) {
        this.liveId = liveId;
    }

    /**
     * 获取直播间名称
     *
     * @return LIVE_NAME - 直播间名称
     */
    public String getLiveName() {
        return liveName;
    }

    /**
     * 设置直播间名称
     *
     * @param liveName 直播间名称
     */
    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }

    /**
     * 获取-1审核未通过 0-待审核 1-审核通过
     *
     * @return AUDIT_STATUS - -1审核未通过 0-待审核 1-审核通过
     */
    public String getAuditStatus() {
        return auditStatus;
    }

    /**
     * 设置-1审核未通过 0-待审核 1-审核通过
     *
     * @param auditStatus -1审核未通过 0-待审核 1-审核通过
     */
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    /**
     * 获取审核时间
     *
     * @return AUDIT_TIME - 审核时间
     */
    public Date getAuditTime() {
        return auditTime;
    }

    /**
     * 设置审核时间
     *
     * @param auditTime 审核时间
     */
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }


    /**
     * 获取销售负责人
     *
     * @return MANAGER - 销售负责人
     */
    public Long getManager() {
        return manager;
    }

    /**
     * 设置销售负责人
     *
     * @param manager 销售负责人
     */
    public void setManager(Long manager) {
        this.manager = manager;
    }

    /**

     /**
     * 设置绑定时间
     *
     * @param bindTime 绑定时间
     */
    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Date getFreeDate() {
        return freeDate;
    }

    public void setFreeDate(Date freeDate) {
        this.freeDate = freeDate;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public String getMobile() {
        return mobile!=null?mobile:"4001-169-269";
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactWechat() {
        return contactWechat;
    }

    public void setContactWechat(String contactWechat) {
        this.contactWechat = contactWechat;
    }
}