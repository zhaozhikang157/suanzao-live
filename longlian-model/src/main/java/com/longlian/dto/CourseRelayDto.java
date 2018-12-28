package com.longlian.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/11.
 */
public class CourseRelayDto extends CourseDto{
    private long oriCourseId;//原课程ID
    private long id;//课程ID
    private long appId;//用户ID(转播人)
    private long oriAppId;//原始老师ID
    private Date createTime;//转播时间
    private Date updateTime;//更新时间
//    private BigDecimal relayCharge;//转播价格
//    private String relayScale;//转播收益比例

    public long getOriCourseId() {
        return oriCourseId;
    }

    public void setOriCourseId(long oriCourseId) {
        this.oriCourseId = oriCourseId;
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

    public long getOriAppId() {
        return oriAppId;
    }

    public void setOriAppId(long oriAppId) {
        this.oriAppId = oriAppId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    //    public BigDecimal getRelayCharge() {
//        return relayCharge;
//    }
//
//    public void setRelayCharge(BigDecimal relayCharge) {
//        this.relayCharge = relayCharge;
//    }
//
//    public String getRelayScale() {
//        return relayScale;
//    }
//
//    public void setRelayScale(String relayScale) {
//        this.relayScale = relayScale;
//    }
}
