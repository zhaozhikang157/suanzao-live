package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:user_follow_wechat_official
 */
public class UserFollowWechatOfficial {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 用户Id
     *
     * field:appId  column:APP_ID
     * default:0
     * 
     */
    private Long appId;

    /**
     * 状态0-关注 1-取消关注
     *
     * field:status  column:STATUS
     * default:0
     * 
     */
    private Integer status;

    /**
     * 公众号用户关注的openid（老师的公众号）
     *
     * field:openid  column:OPENID
     * 
     */
    private String openid;

    /**
     * 公众号appid
     *
     * field:wechatAppid  column:WECHAT_APPID
     * 
     */
    private String wechatAppid;

    /**
     * 创建时间
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getWechatAppid() {
        return wechatAppid;
    }

    public void setWechatAppid(String wechatAppid) {
        this.wechatAppid = wechatAppid == null ? null : wechatAppid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}