package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:course_record
 */
public class VisitCourseRecord implements Serializable {

    private long id;
    private long courseId;//课程ID
    private long appId;
    private Integer status;//状态0-正常 1-删除
    private Date createTime;//创建时间
    private String fromType;//来源方式0-默认 1-邀请卡 2-分销链接'
    private long invitationUserId;//如果是我自己，则填我自己的ID',
    private long seriesCourseId;//系列课ID
    private String fromShareType;//分享来源方式0-默认 1-朋友圈分享 2-好友分享

    public String getFromShareType() {
        return fromShareType;
    }

    public void setFromShareType(String fromShareType) {
        this.fromShareType = fromShareType;
    }

    public String getFromType() {
        return fromType;
    }

    public long getSeriesCourseId() {
        return seriesCourseId;
    }

    public void setSeriesCourseId(long seriesCourseId) {
        this.seriesCourseId = seriesCourseId;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public long getInvitationUserId() {
        return invitationUserId;
    }

    public void setInvitationUserId(long invitationUserId) {
        this.invitationUserId = invitationUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}