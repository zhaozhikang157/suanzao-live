package com.longlian.dto;

import java.util.Date;

/**
 * Created by liuhan on 2017-06-21.
 */
public class ButtonCount {
    private String button;
    private String referer;//来源
    private Long courseId;//课程ID
    private Long roomId;//直播间ID
    private Long rewardId;//打赏ID
    private Long userId;
    private String clientType;//android/web/ios
    private String v;//版本
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getReferer() {
        return referer;

    }

    public void setReferer(String referer) {
        this.referer = referer;
    }



}
