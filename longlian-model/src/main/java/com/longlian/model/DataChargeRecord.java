package com.longlian.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "data_charge_record")
public class DataChargeRecord {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 原有总流量
     */
    @Column(name = "TOTAL_AMOUNT")
    private Long totalAmount;

    /**
     * 剩余流量
     */
    @Column(name = "BAL_AMOUNT")
    private Long balAmount;

    /**
     * 失效周期0-永久 30-天 1-天
     */
    @Column(name = "INVALID_DATE")
    private Integer invalidDate;

    /**
     * 已消耗多少流量
     */
    @Column(name = "USED_AMOUNT")
    private Long usedAmount;

    /**
     * -1-未支付 0-支付成功 1-已生效 2-已失效
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 购买日期
     */
    @Column(name = "ORDER_TIME")
    private Date orderTime;

    /**
     * 生效日期
     */
    @Column(name = "CAN_USE_TIME")
    private Date canUseTime;

    /**
     * 订单ID
     */
    @Column(name = "ORDER_ID")
    private Long orderId;

    /**
     * 直播间ID
     */
    @Column(name = "ROOM_ID")
    private Long roomId;

    /**
     * 充值人员
     */
    @Column(name = "USER_ID")
    private Long userId;

    /**
     * 消耗初始流量
     */
    @Column(name = "USE_ORIGIN_AMOUNT")
    private Long useOriginAmount;

    /**
     * 失效周期单位0-天 1-月 2-年
     */
    @Column(name = "INVALID_DATE_UNIT")
    private String invalidDateUnit;

    /**
     * 是否是平台赠送 0-否 1-是
     */
    @Column(name = "IS_PLATFORM_GIFT")
    private String isPlatformGift;

    /**
     * 使用档次
     */
    @Column(name = "LEVEL_ID")
    private Long levelId;

    /**
     * 失效日期
     */
    @Column(name = "INVALID_REAL_DATE")
    private Date invalidRealDate;

    public Date getInvalidRealDate() {
        return invalidRealDate;
    }

    public void setInvalidRealDate(Date invalidRealDate) {
        this.invalidRealDate = invalidRealDate;
    }

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取原有总流量
     *
     * @return TOTAL_AMOUNT - 原有总流量
     */
    public Long getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置原有总流量
     *
     * @param totalAmount 原有总流量
     */
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取剩余流量
     *
     * @return BAL_AMOUNT - 剩余流量
     */
    public Long getBalAmount() {
        return balAmount;
    }

    /**
     * 设置剩余流量
     *
     * @param balAmount 剩余流量
     */
    public void setBalAmount(Long balAmount) {
        this.balAmount = balAmount;
    }

    /**
     * 获取失效周期0-永久 30-天 1-天
     *
     * @return INVALID_DATE - 失效周期0-永久 30-天 1-天
     */
    public Integer getInvalidDate() {
        return invalidDate;
    }

    /**
     * 设置失效周期0-永久 30-天 1-天
     *
     * @param invalidDate 失效周期0-永久 30-天 1-天
     */
    public void setInvalidDate(Integer invalidDate) {
        this.invalidDate = invalidDate;
    }

    /**
     * 获取已消耗多少流量
     *
     * @return USED_AMOUNT - 已消耗多少流量
     */
    public Long getUsedAmount() {
        return usedAmount;
    }

    /**
     * 设置已消耗多少流量
     *
     * @param usedAmount 已消耗多少流量
     */
    public void setUsedAmount(Long usedAmount) {
        this.usedAmount = usedAmount;
    }

    /**
     * 获取-1-未支付 0-支付成功 1-已生效 2-已失效
     *
     * @return STATUS - -1-未支付 0-支付成功 1-已生效 2-已失效
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置-1-未支付 0-支付成功 1-已生效 2-已失效
     *
     * @param status -1-未支付 0-支付成功 1-已生效 2-已失效
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取购买日期
     *
     * @return ORDER_TIME - 购买日期
     */
    public Date getOrderTime() {
        return orderTime;
    }

    /**
     * 设置购买日期
     *
     * @param orderTime 购买日期
     */
    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * 获取生效日期
     *
     * @return CAN_USE_TIME - 生效日期
     */
    public Date getCanUseTime() {
        return canUseTime;
    }

    /**
     * 设置生效日期
     *
     * @param canUseTime 生效日期
     */
    public void setCanUseTime(Date canUseTime) {
        this.canUseTime = canUseTime;
    }

    /**
     * 获取订单ID
     *
     * @return ORDER_ID - 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取直播间ID
     *
     * @return ROOM_ID - 直播间ID
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * 设置直播间ID
     *
     * @param roomId 直播间ID
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 获取充值人员
     *
     * @return USER_ID - 充值人员
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置充值人员
     *
     * @param userId 充值人员
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取消耗初始流量
     *
     * @return USE_ORIGIN_AMOUNT - 消耗初始流量
     */
    public Long getUseOriginAmount() {
        return useOriginAmount;
    }

    /**
     * 设置消耗初始流量
     *
     * @param useOriginAmount 消耗初始流量
     */
    public void setUseOriginAmount(Long useOriginAmount) {
        this.useOriginAmount = useOriginAmount;
    }

    /**
     * 获取失效周期单位0-天 1-月 2-年
     *
     * @return INVALID_DATE_UNIT - 失效周期单位0-天 1-月 2-年
     */
    public String getInvalidDateUnit() {
        return invalidDateUnit;
    }

    /**
     * 设置失效周期单位0-天 1-月 2-年
     *
     * @param invalidDateUnit 失效周期单位0-天 1-月 2-年
     */
    public void setInvalidDateUnit(String invalidDateUnit) {
        this.invalidDateUnit = invalidDateUnit;
    }

    /**
     * 获取是否是平台赠送 0-否 1-是
     *
     * @return IS_PLATFORM_GIFT - 是否是平台赠送 0-否 1-是
     */
    public String getIsPlatformGift() {
        return isPlatformGift;
    }

    /**
     * 设置是否是平台赠送 0-否 1-是
     *
     * @param isPlatformGift 是否是平台赠送 0-否 1-是
     */
    public void setIsPlatformGift(String isPlatformGift) {
        this.isPlatformGift = isPlatformGift;
    }

    /**
     * 获取使用档次
     *
     * @return LEVEL_ID - 使用档次
     */
    public Long getLevelId() {
        return levelId;
    }

    /**
     * 设置使用档次
     *
     * @param levelId 使用档次
     */
    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}