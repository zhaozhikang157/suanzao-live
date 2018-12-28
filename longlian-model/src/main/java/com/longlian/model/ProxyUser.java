package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:proxy_user
 */
public class ProxyUser implements Serializable {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 代理人
     *
     * field:appId  column:APP_ID
     * 
     */
    private Long appId;

    /**
     * 状态 0-启用 1-撤销
     *
     * field:status  column:STATUS
     * default:0
     * 
     */
    private String status;

    /**
     * 创建时间
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}