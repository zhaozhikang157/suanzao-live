package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by liuhan on 2018-05-30.
 */
public class RenBenUser implements Serializable {
    private Long id;
    private String userName;
    private String phone;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
