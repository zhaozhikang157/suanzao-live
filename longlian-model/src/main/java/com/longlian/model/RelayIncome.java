package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/13.
 */
public class RelayIncome  implements Serializable {
    private long id;//主键
    private long oriAppId;//原老师（用户）ID
    private long oriCourseId;//原课程ID
    private long relCourseId;//转播课ID
    private long payAppId;//付费人ID（转播者或购买者）
    private BigDecimal charge;//金额（转播费用、购买费用）
    private BigDecimal relayScale;//转播比例
    private String type;//类型：1 转播 2 购买
    private Date createTime;//创建时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOriAppId() {
        return oriAppId;
    }

    public void setOriAppId(long oriAppId) {
        this.oriAppId = oriAppId;
    }

    public long getOriCourseId() {
        return oriCourseId;
    }

    public void setOriCourseId(long oriCourseId) {
        this.oriCourseId = oriCourseId;
    }

    public long getRelCourseId() {
        return relCourseId;
    }

    public void setRelCourseId(long relCourseId) {
        this.relCourseId = relCourseId;
    }

    public long getPayAppId() {
        return payAppId;
    }

    public void setPayAppId(long payAppId) {
        this.payAppId = payAppId;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getRelayScale() {
        return relayScale;
    }

    public void setRelayScale(BigDecimal relayScale) {
        this.relayScale = relayScale;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
