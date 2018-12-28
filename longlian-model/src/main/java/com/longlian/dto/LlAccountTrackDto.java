package com.longlian.dto;

import com.longlian.model.LlAccountTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2017/5/6.
 */
public class LlAccountTrackDto extends LlAccountTrack {
    private static Logger log = LoggerFactory.getLogger(LlAccountTrackDto.class);

    private String mobile;
    private String orderNo;         //订单编号
    private String tranNo;          //第三方回执单号
    private String bankType;        //充值类型
    private BigDecimal oAmount;     //订单金额
    private BigDecimal realAmount;  //实收金额
    private Date startTime;         //开始时间
    private Date endTime;           //结束时间
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTranNo() {
        return tranNo;
    }

    public void setTranNo(String tranNo) {
        this.tranNo = tranNo;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public BigDecimal getoAmount() {
        return oAmount;
    }

    public void setoAmount(BigDecimal oAmount) {
        this.oAmount = oAmount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }
}
