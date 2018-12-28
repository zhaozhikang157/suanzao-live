package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  用户打赏类型
 * tablename:user_reward_record
 */
public class UserRewardType implements Serializable {
    private long id;
    private BigDecimal amount;//金额',
    private String status;//STATUS '0-启用 1-禁用',
    private Date createTime;// CREATE_TIME          datetime comment '创建时间',
    private String remark;//备注
    private String picAddress;  //打赏图片
    private String chatPicAddress; //聊天室打赏小图标

    public String getChatPicAddress() {
        return chatPicAddress;
    }

    public void setChatPicAddress(String chatPicAddress) {
        this.chatPicAddress = chatPicAddress;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
