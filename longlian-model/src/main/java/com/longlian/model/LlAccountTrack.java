package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/13.
 */
public class LlAccountTrack implements Serializable {
    private long id;//ID                   bigint not null auto_increment,
    private long formAccountId;//FORM_ACCOUNT_ID      bigint comment '付钱appId',
    private long toAccountId;//TO_ACCOUNT_ID        bigint default 0 comment '得到方APPId',
    private String type;//TYPE                 varchar(5) default '0' comment '支出或者收入类型 0-收入 1-支出',
    private BigDecimal amount = new BigDecimal(0);// AMOUNT               decimal(20,2) default 0 comment '金额',
    private BigDecimal currBalance = new BigDecimal(0);//CURR_BALANCE         decimal(20,2) comment '当前账户余额',
    private Date createTime;//CREATE_TIME          datetime comment '创建时间',
    private long trackId;//TRACK_ID             bigint comment '关联ID',
    private long orderId;//ORDER_ID             bigint comment '关联订单ID',  RETURN_MONEY_LEVEL等于2-打赏记录ID ；等于0或者 1 关联订单ID，
    private int returnMoneyLevel;// 来源类型 0-充值 1-购买课程2打赏
    private int courseType;//课程类型：0：原课程  1：转播课

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFormAccountId() {
        return formAccountId;
    }

    public void setFormAccountId(long formAccountId) {
        this.formAccountId = formAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCurrBalance() {
        return currBalance;
    }

    public void setCurrBalance(BigDecimal currBalance) {
        this.currBalance = currBalance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getReturnMoneyLevel() {
        return returnMoneyLevel;
    }

    public void setReturnMoneyLevel(int returnMoneyLevel) {
        this.returnMoneyLevel = returnMoneyLevel;
    }

    public int getCourseType() {
        return courseType;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
    }
}
