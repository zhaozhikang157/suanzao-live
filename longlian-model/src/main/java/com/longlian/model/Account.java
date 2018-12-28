package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wtc on 2016/8/1.
 */
public class Account implements Serializable {
    private Long accountId;
    private BigDecimal balance = new BigDecimal(0);//剩余金额
    private BigDecimal addTotalAmount = new BigDecimal(0);//充值总金额
    private BigDecimal delTotalAmount = new BigDecimal(0);//消费总金额
    private long formTrackId;
    private Date createTime;
    private String remark;
    private String tradePwd;//交易面TRADE_PWD
    private String status;//状态 0-正常 1-禁止提现 2-冻结

    public BigDecimal getAddTotalAmount() {
        return addTotalAmount;
    }

    public void setAddTotalAmount(BigDecimal addTotalAmount) {
        this.addTotalAmount = addTotalAmount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getDelTotalAmount() {
        return delTotalAmount;
    }

    public void setDelTotalAmount(BigDecimal delTotalAmount) {
        this.delTotalAmount = delTotalAmount;
    }

    public long getFormTrackId() {
        return formTrackId;
    }

    public void setFormTrackId(long formTrackId) {
        this.formTrackId = formTrackId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradePwd() {
        return tradePwd;
    }

    public void setTradePwd(String tradePwd) {
        this.tradePwd = tradePwd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
