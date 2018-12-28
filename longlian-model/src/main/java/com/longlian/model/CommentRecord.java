package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pangchao on 2017/1/23.
 */
public class CommentRecord implements Serializable {
    private static Logger log = LoggerFactory.getLogger(CommentRecord.class);
    private long id;
    private long commentId;   //反馈表ID
    private long handUserId;        //处理人（员工ID)
    private String remarks;         //处理内容
    private Date createTime;       //处理时间
    private String handStatus;     //状态 1-处理中 2-已处理
    private long  courseId;     //课程ID

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

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getHandUserId() {
        return handUserId;
    }

    public void setHandUserId(long handUserId) {
        this.handUserId = handUserId;
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

    public String getHandStatus() {
        return handStatus;
    }

    public void setHandStatus(String handStatus) {
        this.handStatus = handStatus;
    }
}