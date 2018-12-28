package com.longlian.model;

import java.io.Serializable;

/**
 *
 * tablename:live_channel 直播流管理
 */
public class LiveChannel implements Serializable{

    private long id;
    private String code;//通道编号
    private String pushAddr;//通道推送地址
    private String playAddr1;//通道直播地址rtmp
    private String playAddr2;//通道直播地址flv
    private String playAddr3;//通道直播地址m3u8

    private String mixPlayAddr1;//通道直播混流地址rtmp
    private String mixPlayAddr2;//通道直播混流地址flv

    public String getMixPlayAddr1() {
        return mixPlayAddr1;
    }

    public void setMixPlayAddr1(String mixPlayAddr1) {
        this.mixPlayAddr1 = mixPlayAddr1;
    }

    public String getMixPlayAddr2() {
        return mixPlayAddr2;
    }

    public void setMixPlayAddr2(String mixPlayAddr2) {
        this.mixPlayAddr2 = mixPlayAddr2;
    }

    public String getMixPlayAddr3() {
        return mixPlayAddr3;
    }

    public void setMixPlayAddr3(String mixPlayAddr3) {
        this.mixPlayAddr3 = mixPlayAddr3;
    }

    private String mixPlayAddr3;//通道直播混流地址m3u8

    private String domain;//加速通道
    private String courseId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPushAddr() {
        return pushAddr;
    }

    public void setPushAddr(String pushAddr) {
        this.pushAddr = pushAddr;
    }

    public String getPlayAddr1() {
        return playAddr1;
    }

    public void setPlayAddr1(String playAddr1) {
        this.playAddr1 = playAddr1;
    }

    public String getPlayAddr2() {
        return playAddr2;
    }

    public void setPlayAddr2(String playAddr2) {
        this.playAddr2 = playAddr2;
    }

    public String getPlayAddr3() {
        return playAddr3;
    }

    public void setPlayAddr3(String playAddr3) {
        this.playAddr3 = playAddr3;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}