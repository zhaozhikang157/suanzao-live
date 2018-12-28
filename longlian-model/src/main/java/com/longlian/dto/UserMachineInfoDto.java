package com.longlian.dto;

import com.longlian.model.UserMachineInfo;

import java.util.Date;

/**
 * Created by liuhan on 2017-06-13.
 */
public class UserMachineInfoDto extends UserMachineInfo {
    private Date createTimeBegin;   //查询条件（开始时间）
    private Date createTimeEnd;         //查询条件（结束时间）

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

}
