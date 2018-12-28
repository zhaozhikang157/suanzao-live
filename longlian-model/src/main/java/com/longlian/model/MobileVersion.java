package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pangchao on 2017/1/22.
 */
public class MobileVersion implements Serializable {
    private static Logger log = LoggerFactory.getLogger(MobileVersion.class);
    private static final long serialVersionUID = 1L;
    private long id;
    private String versionType;//版本类型   "0":ios应用 ,"1":android应用,
    private String name;//名称
    private String versionNum;//版本号
    private String status;//状态   "0":下线   "1":上线
    private String downloadAddress;//下载地址
    private Date onlineTime;//上线时间
    private Date offlineTime;//下线时间
    private String userName;//发布版本人员
    private String isFoceUpdate;//1-强制升级 0-非强制升级
    private String versionBrief;//版本概要

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        MobileVersion.log = log;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownloadAddress() {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsFoceUpdate() {
        return isFoceUpdate;
    }

    public void setIsFoceUpdate(String isFoceUpdate) {
        this.isFoceUpdate = isFoceUpdate;
    }

    public String getVersionBrief() {
        return versionBrief;
    }

    public void setVersionBrief(String versionBrief) {
        this.versionBrief = versionBrief;
    }
}