package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  用户打赏记录
 * tablename:reward_record
 */
public class UserRewardRecord implements Serializable {
    private long id;
    private long appId;//用户ID,打赏的人学生
    private String type;// 类型 0 -学币 其他-待定
    private BigDecimal singleAmount;//单个金额
    private Integer rewardCount;//人数
    private BigDecimal amount;//总金额 = 单个金额 * 打赏个数
    private Date createTime;//创建时间
    private long courseId;//课程ID
    private long rewardRewardId;//被打赏人，老师
    private String remark;//备注

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(BigDecimal singleAmount) {
        this.singleAmount = singleAmount;
    }

    public Integer getRewardCount() {
        return rewardCount;
    }

    public void setRewardCount(Integer rewardCount) {
        this.rewardCount = rewardCount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getRewardRewardId() {
        return rewardRewardId;
    }

    public void setRewardRewardId(long rewardRewardId) {
        this.rewardRewardId = rewardRewardId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
