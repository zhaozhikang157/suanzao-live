package com.longlian.type;

import java.math.BigDecimal;

/**
 * Created by syl on 2017/02/18.
 */
public class ThirdPayDto {
    private  String payType;//枚举类 PayType
    private String deviceNo;//手机设备号
    private long  couponId;//优惠券ID
    private String password;//交易密码
    private BigDecimal amount ;//订单金额，适用于所有订单
    private String orderType;//订单类型 0-课程购买 2- 充值
    private long payTypeId;//ios、安卓、微信支付类型表（ios_pay_type）ID 或者打赏类型ID
    private String iosPayType;//安卓、微信微信支付类型 0/空 - ios_pay_type表充值类型  1-购买课程类型
    private String iosTransactionId; // ios 支付凭证，验证支付结果使用

    private Long invitationAppId;//邀请人ID
    private Integer count;//数量
    /**
     * 以下为IOS内购支付 特定需要的字段 start
     */
    private String iosOrderId;//ios支付订单ID
    private BigDecimal iosAmount;//ios支付金额
    private String iosCommodityId;//Ios支付商品ID
    private String iosCommodityName;//Ios支付商品名称
    /**
     * 以上为IOS内购支付 特定需要的字段 end
     */
    private long courseId;//课程ID，购买课程
    private String isBuy;//是否购买 1-是  0-点击购买课程按钮

    private String ext;//扩展字段，目前邀请码支付启作用
    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getIosOrderId() {
        return iosOrderId;
    }

    public void setIosOrderId(String iosOrderId) {
        this.iosOrderId = iosOrderId;
    }

    public BigDecimal getIosAmount() {
        return iosAmount;
    }

    public void setIosAmount(BigDecimal iosAmount) {
        this.iosAmount = iosAmount;
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

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(long payTypeId) {
        this.payTypeId = payTypeId;
    }

    public String getIosPayType() {
        return iosPayType;
    }

    public void setIosPayType(String iosPayType) {
        this.iosPayType = iosPayType;
    }

    public String getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(String isBuy) {
        this.isBuy = isBuy;
    }

    public Long getInvitationAppId() {
        return invitationAppId;
    }

    public void setInvitationAppId(Long invitationAppId) {
        this.invitationAppId = invitationAppId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

	public String getIosTransactionId() {
		return iosTransactionId;
	}

	public void setIosTransactionId(String iosTransactionId) {
		this.iosTransactionId = iosTransactionId;
	}
}
