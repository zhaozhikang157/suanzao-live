package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2017/4/20.
 */
public class IosPayType {
    private static Logger log = LoggerFactory.getLogger(IosPayType.class);

    private Long id;
    private BigDecimal amount;            //金额（苹果）
    private BigDecimal llReallyAmount;     //龙链实收金额（苹果）
    private BigDecimal userReallyAmount; //用户实收金额
    private String iosCommodityId;      //Ios支付商品ID（苹果）
    private String iosCommodityName;    //Ios支付商品名称（苹果）
    private Date createTime;           //创建时间
    private String remark;              //备注
    private String status;              //状态 0-启用 1-不启用
    private String type;            //0:IOS 1:androd

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLlReallyAmount() {
        return llReallyAmount;
    }

    public void setLlReallyAmount(BigDecimal llReallyAmount) {
        this.llReallyAmount = llReallyAmount;
    }

    public BigDecimal getUserReallyAmount() {
        return userReallyAmount;
    }

    public void setUserReallyAmount(BigDecimal userReallyAmount) {
        this.userReallyAmount = userReallyAmount;
    }

    public String getIosCommodityId() {
        return iosCommodityId;
    }

    public void setIosCommodityId(String iosCommodityId) {
        this.iosCommodityId = iosCommodityId;
    }

    public String getIosCommodityName() {
        return iosCommodityName;
    }

    public void setIosCommodityName(String iosCommodityName) {
        this.iosCommodityName = iosCommodityName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
