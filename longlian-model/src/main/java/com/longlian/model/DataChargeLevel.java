package com.longlian.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "data_charge_level")
public class DataChargeLevel {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * GB为单位
     */
    @Column(name = "AMOUNT")
    private Long amount;

    /**
     * 原价
     */
    @Column(name = "ORIG_PRICE")
    private BigDecimal origPrice;

    /**
     * 优惠价
     */
    @Column(name = "PREF_PRICE")
    private BigDecimal prefPrice;

    /**
     * 0-永久 1-代表1天
     */
    @Column(name = "INVALID_DATE")
    private Integer invalidDate;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "SORT")
    private Integer sort;

    /**
     * 失效周期单位0-天 1-月 1-年
     */
    @Column(name = "INVALID_DATE_UNIT")
    private String invalidDateUnit;

    /**
     * 0-在客户端不显示 1-显示 2-删除
     */
    @Column(name = "STATUS")
    private String status;

    @Column(name = "UNIT_PRICE")
    private BigDecimal unitPrice;

    /**
     * 是否是零售0-不是 1-是
     */
    @Column(name = "IS_RETAIL")
    private String isRetail;

    /**
     * 是否是热门0-不是 1-是
     */
    @Column(name = "IS_HOT")
    private String isHot;
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
     * 获取GB为单位
     *
     * @return AMOUNT - GB为单位
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * 设置GB为单位
     *
     * @param amount GB为单位
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public BigDecimal getOrigPrice() {
        return origPrice;
    }

    public void setOrigPrice(BigDecimal origPrice) {
        this.origPrice = origPrice;
    }

    public BigDecimal getPrefPrice() {
        return prefPrice;
    }

    public void setPrefPrice(BigDecimal prefPrice) {
        this.prefPrice = prefPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 获取0-永久 1-代表1天
     *
     * @return INVALID_DATE - 0-永久 1-代表1天
     */
    public Integer getInvalidDate() {
        return invalidDate;
    }

    /**
     * 设置0-永久 1-代表1天
     *
     * @param invalidDate 0-永久 1-代表1天
     */
    public void setInvalidDate(Integer invalidDate) {
        this.invalidDate = invalidDate;
    }

    /**
     * @return CREATE_TIME
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return SORT
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param sort
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取失效周期单位0-天 1-月 1-年
     *
     * @return INVALID_DATE_UNIT - 失效周期单位0-天 1-月 1-年
     */
    public String getInvalidDateUnit() {
        return invalidDateUnit;
    }

    /**
     * 设置失效周期单位0-天 1-月 1-年
     *
     * @param invalidDateUnit 失效周期单位0-天 1-月 1-年
     */
    public void setInvalidDateUnit(String invalidDateUnit) {
        this.invalidDateUnit = invalidDateUnit;
    }

    /**
     * 获取0-在客户端不显示 1-显示 2-删除
     *
     * @return STATUS - 0-在客户端不显示 1-显示 2-删除
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置0-在客户端不显示 1-显示 2-删除
     *
     * @param status 0-在客户端不显示 1-显示 2-删除
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * 获取是否是零售0-不是 1-是
     *
     * @return IS_RETAIL - 是否是零售0-不是 1-是
     */
    public String getIsRetail() {
        return isRetail;
    }

    /**
     * 设置是否是零售0-不是 1-是
     *
     * @param isRetail 是否是零售0-不是 1-是
     */
    public void setIsRetail(String isRetail) {
        this.isRetail = isRetail;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }
}