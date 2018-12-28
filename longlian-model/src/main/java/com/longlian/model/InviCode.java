package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/8/29.
 */
public class InviCode {

    private Long id;            //id
    private Long appId;         //用户id
    private Long courseId;      //课程id
    private int codeCount;      //邀请码总数
    private int balanceCount;   //剩余数
    private Date createTime;    //创建时间
    private String remark;      //备注
    private String codeUnion;   //邀请码唯一标示
    private Date startTime;    //有效期:开始时间
    private Date endTime;      //有效期:截止时间

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public int getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(int codeCount) {
        this.codeCount = codeCount;
    }

    public int getBalanceCount() {
        return balanceCount;
    }

    public void setBalanceCount(int balanceCount) {
        this.balanceCount = balanceCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCodeUnion() {
        return codeUnion;
    }

    public void setCodeUnion(String codeUnion) {
        this.codeUnion = codeUnion;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
