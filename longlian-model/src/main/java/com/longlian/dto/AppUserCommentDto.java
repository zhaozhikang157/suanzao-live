package com.longlian.dto;

import com.longlian.model.AppUserComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by pangchao on 2017/1/23.
 */
public class AppUserCommentDto extends AppUserComment {
    private static Logger log = LoggerFactory.getLogger(AppUserCommentDto.class);
    private Date beginTime; //查询条件（开始时间）
    private Date endnTime;  //查询条件（结束时间）
    private String handRemarks;   //处理内容
    private long handUserId;   //处理人Id
    private String appName;//用户名称
    private String liveTopic;//课程主题
    private String courseRemark;//课程备注

    public String getCourseRemark() {
        return courseRemark;
    }

    public void setCourseRemark(String courseRemark) {
        this.courseRemark = courseRemark;
    }

    public String getLiveTopic() {
        return liveTopic;
    }

    public void setLiveTopic(String liveTopic) {
        this.liveTopic = liveTopic;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getHandUserId() {
        return handUserId;
    }

    public void setHandUserId(long handUserId) {
        this.handUserId = handUserId;
    }

    public Date getEndnTime() {
        return endnTime;
    }

    public void setEndnTime(Date endnTime) {
        this.endnTime = endnTime;
    }

    public String getHandRemarks() {
        return handRemarks;
    }

    public void setHandRemarks(String handRemarks) {
        this.handRemarks = handRemarks;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }
}