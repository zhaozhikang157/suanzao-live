package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/13.
 */
public class AccountTrack implements Serializable {
    private long id;//ID                   bigint not null auto_increment,
    private long formAccountId;//FORM_ACCOUNT_ID      bigint comment '付钱appId',
    private long toAccountId;//TO_ACCOUNT_ID        bigint default 0 comment '得到方APPId',
    private String type;//TYPE                 varchar(5) default '0' comment '支出或者收入类型 0-收入 1-支出',
    private BigDecimal amount = new BigDecimal(0);// AMOUNT               decimal(20,2) default 0 comment '金额',
 /*   private BigDecimal realAmount = new BigDecimal(0);//REAL_AMOUNT          decimal(20,2) default 0 comment '实际金额',
    private BigDecimal chargePercent = new BigDecimal(0);// CHARGE_PERCENT       decimal(20,4) comment '手续费率',
    private BigDecimal charge = new BigDecimal(0);//CHARGE               decimal(20,2) comment '手续费',*/
    private BigDecimal currBalance = new BigDecimal(0);//CURR_BALANCE         decimal(20,2) comment '当前账户余额',
    private Date createTime;//CREATE_TIME          datetime comment '创建时间',
    private long trackId;//TRACK_ID             bigint comment '关联ID',
    private long orderId;//ORDER_ID             bigint comment '关联订单ID',RETURN_MONEY_LEVEL等于7-打赏记录ID ；等于0、1 、5关联订单ID；等于2、3、4为龙链平台奖励记录
    private int returnMoneyLevel;// 来源类型 0-默认 1-分销奖励 2-推荐老师平台奖励 3-老师课程授课奖励 4-老师粉丝关注奖励 5-充值钱包枣币 6-老师提现给代理返钱 10-提现返钱 11-平台奖励  12-平台扣款 详见AccountFromType
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
