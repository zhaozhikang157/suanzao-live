package com.longlian.dto;

import com.longlian.model.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pangchao on 2017/3/1.
 */
public class OrdersDto extends Orders {
    private Date createTimeBegin;   //查询条件（开始时间）
    private Date createTimeEnd;         //查询条件（结束时间）

    private String appName;         //申请人
    private String bankCaidOpenName;        //开户人

    private BigDecimal balance = new BigDecimal(0);   //钱余额

    private String auditorName;     //审核人姓名
    private String appMobile;           //用户手机号码
    private String capitalAmount;           //收费金额大写

    public String getCapitalAmount() {
        return capitalAmount;
    }

    public void setCapitalAmount(String capitalAmount) {
        this.capitalAmount = capitalAmount;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBankCaidOpenName() {
        return bankCaidOpenName;
    }

    public void setBankCaidOpenName(String bankCaidOpenName) {
        this.bankCaidOpenName = bankCaidOpenName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getAppMobile() {
        return appMobile;
    }

    public void setAppMobile(String appMobile) {
        this.appMobile = appMobile;
    }

}