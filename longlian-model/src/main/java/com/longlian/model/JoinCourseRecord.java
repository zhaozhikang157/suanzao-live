package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:join_course_record
 */
public class JoinCourseRecord implements Serializable {
    private long id;
    private long courseId;//课程ID
    private long appId;
    private Integer status;//状态0-正常 1-删除
    private Date createTime;//创建时间
    private long roomId;//直播间ID
    private String signUpStatus;//状态0-报名中 1-成功 2-失败
    private String fromType;//来源方式0-默认 1-邀请卡 2-分销链接
    private long invitationUserId;//如果是我自己，则填我自己的ID
    private String invitationCode;//如果是我自己，则填我自己的CODE
    private String isFree;//IS_FREE              varchar(5) default '0' comment '是否免费0-不是 1-免费',
    private String isFirst;//IS_FIRST             varchar(5) default '0' comment '是否是第一次开课0-是 1-不是',
    private String joinType ;// '0-正常 1-场控人员2-管理员 3-邀请吗购买，详见JoinCourseRecordType';
    private Integer courseType;

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
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

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getSignUpStatus() {
        return signUpStatus;
    }

    public void setSignUpStatus(String signUpStatus) {
        this.signUpStatus = signUpStatus == null ? null : signUpStatus.trim();
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType == null ? null : fromType.trim();
    }

    public long getInvitationUserId() {
        return invitationUserId;
    }

    public void setInvitationUserId(long invitationUserId) {
        this.invitationUserId = invitationUserId;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode == null ? null : invitationCode.trim();
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }
}
