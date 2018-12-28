package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:share_channel
 */
public class ShareChannel {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * NAME
     *
     * field:name  column:NAME
     * 
     */
    private String name;

    /**
     * 渠道类型0-普通
     *
     * field:type  column:TYPE
     * 
     */
    private String type;

    /**
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

    /**
     * 状态0-正常1-删除
     *
     * field:status  column:STATUS
     * default:0
     * 
     */
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}