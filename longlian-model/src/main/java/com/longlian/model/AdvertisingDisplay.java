package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/23.
 */
public class AdvertisingDisplay implements Serializable {
    private static Logger log = LoggerFactory.getLogger(AdvertisingDisplay.class);
    private long id;
    private String name;                //名称
    private String advertType;          //广告类型 0:普通  1:其他
    private String status;              //状态 0:禁用 1:启用
    private Integer sortOrder;          //排序
    private String openUrl;             //打开地址
    private String picAddress;          //图片地址
    private Date createTime;            //创建时间
    private String remarks;             //备注
    private String systemType;          //系统类型
    private String type;                //类型 0-首页 1-讲师轮播图
    private Long courseId;              //课程id
    private String courseInfo;          //课程信息
    private String isSeriesCourse;//是否系列课 1-是系列课0-是单节课

    public String getIsSeriesCourse() {
        return isSeriesCourse;
    }

    public void setIsSeriesCourse(String isSeriesCourse) {
        this.isSeriesCourse = isSeriesCourse;
    }

    public String getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        AdvertisingDisplay.log = log;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdvertType() {
        return advertType;
    }

    public void setAdvertType(String advertType) {
        this.advertType = advertType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getOpenUrl() {
        return openUrl;
    }

    public void setOpenUrl(String openUrl) {
        this.openUrl = openUrl;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}