package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:proxy_teacher
 */
public class ProxyTeacher {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 代理人ID
     *
     * field:proxyAppId  column:PROXY_APP_ID
     * 
     */
    private Long proxyAppId;

    /**
     * 被代理人ID
     *
     * field:teacherId  column:TEACHER_ID
     * 
     */
    private Long teacherId;

    /**
     * 创建时间
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

    /**
     * 状态 0-正常 1-撤销代理
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

    public Long getProxyAppId() {
        return proxyAppId;
    }

    public void setProxyAppId(Long proxyAppId) {
        this.proxyAppId = proxyAppId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
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