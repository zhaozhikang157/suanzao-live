package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * tablename:reward_record
 */
public class RewardRecord implements Serializable {
    private long id;
    private long appId;//用户ID
    private String type;// 类型 0 -老师授课奖励 1-推介老师奖励
    private BigDecimal singleAmount;//单个金额
    private Integer menCount;//人数
    private Integer llAddMenCount;//人数龙链添加人数（后台奖励）';
    private BigDecimal amount;//总金额
    private Date createTime;//创建时间
    private long courseId;//课程ID
    private long relationAppId;//奖励的关联人(暂对推介老师的奖励有关联人)
    private long followRewardId;// FOLLOW_REWARD_ID     bigint default 0 comment '粉丝关注奖励ID',
    private String remark;//备注
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public BigDecimal getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(BigDecimal singleAmount) {
        this.singleAmount = singleAmount;
    }

    public Integer getMenCount() {
        return menCount;
    }

    public void setMenCount(Integer menCount) {
        this.menCount = menCount;
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

    public long getRelationAppId() {
        return relationAppId;
    }

    public void setRelationAppId(long relationAppId) {
        this.relationAppId = relationAppId;
    }

    public long getFollowRewardId() {
        return followRewardId;
    }

    public void setFollowRewardId(long followRewardId) {
        this.followRewardId = followRewardId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLlAddMenCount() {
        return llAddMenCount;
    }

    public void setLlAddMenCount(Integer llAddMenCount) {
        this.llAddMenCount = llAddMenCount;
    }
}
