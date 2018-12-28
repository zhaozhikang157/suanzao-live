package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pangchao on 2017/1/23.
 */
public class AppUserComment implements Serializable {
    private static Logger log = LoggerFactory.getLogger(AppUserComment.class);
    private long id;
    private long appId;            //手机用户ID
    private String mobile;              //手机号
    private String remarks;             //备注
    private Date createTime;               //创建时间
    private String status;           //状态 0-启用  1-关闭
    private String handStatus;      //处理结果 0-等待 1-处理中 2-已处理
    private long courseId;      //课程Id

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getHandStatus() {
        return handStatus;
    }

    public void setHandStatus(String handStatus) {
        this.handStatus = handStatus;
    }
}