package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/13.
 */
public class Orders implements Serializable{
    private long id;//ID                   bigint not null auto_increment,
    private long appId;// APP_ID               bigint comment '用户ID',
    private String tranNo;//TRAN_NO              varchar(50) comment '公共交易编号（银行返回）',
    private String orderNo;//ORDER_NO             varchar(50) comment '平台流水编号（平台）',
    private BigDecimal amount = new BigDecimal(0);// AMOUNT               decimal(20,2) default 0 comment '金额',
    private BigDecimal realAmount = new BigDecimal(0);//REAL_AMOUNT          decimal(20,2) default 0 comment '实际金额',
    private BigDecimal couponAount = new BigDecimal(0);//COUPON_AMOUNT        decimal(20,2) default 0 comment '优惠金额',
    private long couponId;//COUPON_ID            bigint default 0 comment '优惠券ID',
    private String bankType;//BANK_TYPE            varchar(5) default '10' comment 银行类型 07-学币 08-线下支付  09-钱包（枣币） 10-银联  11-支付宝 12-农行 13-微信14-H5微信 15-ios内购（查看PayType枚举类）
    private String orderType;//ORDER_TYPE           int default 0 comment '    订单交易类型 0-购买课程1- 钱包提现  2-充值钱包（枣币）3-充值学币 4-打赏学币 ，详见OrderType枚举类
    private String optStatus;// OPT_STATUS           varchar(5) default '0' comment '操作状态 0- 进行中 1 - 成功 2 - 失败',
    private Date createTime;//CREATE_TIME          datetime comment '创建时间',
    private Date successTime;//SUCCESS_TIME         datetime comment '成功时间',
    private String merId;//MER_ID               varchar(40) comment '商户号（平台）银联、支付宝、微信、钱包',
    private String name;//NAME                 varchar(40) comment '姓名',
    private String bankCardNo;//BANK_CARD_NO         varchar(40) comment '银行卡号',
    private String bankName;//BANK_NAME            varchar(40) comment '银行名称',
    private String mobile;// MOBILE               varchar(20) comment '手机号',
    private String remark;//REMARK               varchar(200) comment '备注',

    private long auditorId;//AUDITOR_ID           bigint default 0 comment '审核人ID',
    private int auditStatus;//AUDIT_STATUS         int default 0 comment '审核状态 0 -审核时间 1-通过 2-不通过',
    private Date auditTime;//AUDIT_TIME           datetime comment '审核时间',
    private String auditAgreed;                     //审核意见
    private String unionStat;//银联返回状态 UNION_STAT 银联返回状态 0-正常 2、3、4、5、7、8 -处理中  6、9 - 失败
    private long joinCourseId;//JOIN_COURSE_ID 报名课程记录表ID
    private String iosPayType;//购买学币类型 0-充值类型 1-购买课程类型
    private long iosPayTypeId;//0-充值类型  ->购买学币类型ID 1-购买课程类型->课程ID

    //平台费率和第三方支付费率
    private BigDecimal chargePercent = new BigDecimal(0);// CHARGE_PERCENT       decimal(20,4) comment '第三方手续费率',
    private BigDecimal charge = new BigDecimal(0);//CHARGE               decimal(20,2) comment '手续费',*/
    private BigDecimal llChargePercent = new BigDecimal(0);// CHARGE_PERCENT       decimal(20,4) comment '龙链手续费率',
    private BigDecimal llCharge = new BigDecimal(0);//CHARGE               decimal(20,2) comment '龙链手续费',*/

    private int courseType;//课程类型：0：原课程  1：转播课

    public int getCourseType() {
        return courseType;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
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

    public String getTranNo() {
        return tranNo;
    }

    public void setTranNo(String tranNo) {
        this.tranNo = tranNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public BigDecimal getCouponAount() {
        return couponAount;
    }

    public void setCouponAount(BigDecimal couponAount) {
        this.couponAount = couponAount;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(String optStatus) {
        this.optStatus = optStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(long auditorId) {
        this.auditorId = auditorId;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditAgreed() {
        return auditAgreed;
    }

    public void setAuditAgreed(String auditAgreed) {
        this.auditAgreed = auditAgreed;
    }

    public String getUnionStat() {
        return unionStat;
    }

    public void setUnionStat(String unionStat) {
        this.unionStat = unionStat;
    }

    public BigDecimal getChargePercent() {

        return chargePercent;
    }

    public void setChargePercent(BigDecimal chargePercent) {
        this.chargePercent = chargePercent;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getLlChargePercent() {
        return llChargePercent;
    }

    public void setLlChargePercent(BigDecimal llChargePercent) {
        this.llChargePercent = llChargePercent;
    }

    public BigDecimal getLlCharge() {
        return llCharge;
    }

    public void setLlCharge(BigDecimal llCharge) {
        this.llCharge = llCharge;
    }

    public long getJoinCourseId() {
        return joinCourseId;
    }

    public void setJoinCourseId(long joinCourseId) {
        this.joinCourseId = joinCourseId;
    }
    public String getIosPayType() {
        return iosPayType;
    }
    public void setIosPayType(String iosPayType) {
        this.iosPayType = iosPayType;
    }
    public long getIosPayTypeId() {
        return iosPayTypeId;
    }

    public void setIosPayTypeId(long iosPayTypeId) {
        this.iosPayTypeId = iosPayTypeId;
    }
}
