package com.longlian.model.system;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by liuhan on 2018-06-04.
 */
public class SystemAdmin implements Serializable{
    private Long id;
    /** 管理员id */
    private Long adminId;
    /** 管理员名称 */
    private String adminName;
    /** 创建时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
