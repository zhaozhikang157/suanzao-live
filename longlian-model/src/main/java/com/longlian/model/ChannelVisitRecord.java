package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:channel_visit_record
 */
public class ChannelVisitRecord {
    /**
     * ID
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 渠道ID
     *
     * field:channelId  column:CHANNEL_ID
     * 
     */
    private Long channelId;

    /**
     * 课程ID
     *
     * field:courseId  column:COURSE_ID
     * 
     */
    private Long courseId;

    /**
     * 学员ID
     *
     * field:appId  column:APP_ID
     * 
     */
    private Long appId;

    /**
     * 是否新用户 0-访问 1-新用户
     *
     * field:isNewUser  column:IS_NEW_USER
     * 
     */
    private String isNewUser;

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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser == null ? null : isNewUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}