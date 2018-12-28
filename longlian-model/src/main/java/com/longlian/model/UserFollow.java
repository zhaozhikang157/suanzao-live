package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:user_follow
 */
public class UserFollow implements Serializable {
    private long id;
    private long appId;//用户ID
    private long roomId;//直播间ID
    private Date createTime;//创建时间
    private Integer status;// 状态0-关注 1-取消关注
    private  String isReaderd;//是否看过0-未看 1-已看
    private  String thirdOpenId;//第三方公众号用户关注的openid（老师的公众号）
    private  String thirdWechatAppId;//第三方公众号appid（老师的公众号）

    public String getThirdWechatAppId() {
        return thirdWechatAppId;
    }

    public void setThirdWechatAppId(String thirdWechatAppId) {
        this.thirdWechatAppId = thirdWechatAppId;
    }

    public String getThirdOpenId() {
        return thirdOpenId;
    }

    public void setThirdOpenId(String thirdOpenId) {
        this.thirdOpenId = thirdOpenId;
    }

    public String getIsReaderd() {
        return isReaderd;
    }

    public void setIsReaderd(String isReaderd) {
        this.isReaderd = isReaderd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}