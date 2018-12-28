package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by U on 2016-09-08.
 */
public class SystemLog implements Serializable {
    private long id;
    private String systemType;   //0-APP端 1- console后台
    private long userId;       //操作人员ID（APP或者USER）
    private String userName;           //操作人员的姓名或者手机号
    private String ipAddress;          //IP地址
    private String logType;        //日志类型  0-系统日志 详见LOG_TYPE枚举类
    private String  object;         //操作对象
    private String content;         //备注
    private Date logTime;          //操作的时间
    private String tableType;          //表类型   详见LOG_TABLE_TYPE枚举类
    private long tableId;          //表ID
    private String deviceNo;//手机设备号  DEVICE_NO

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }
}
