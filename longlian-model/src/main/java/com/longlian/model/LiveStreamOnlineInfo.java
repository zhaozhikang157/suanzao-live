package com.longlian.model;

/**
 * Created by longlian007 on 2018/2/7.
 * 推流返回数据模型
 */
public class LiveStreamOnlineInfo {
    private String domainName;//流所属加速域名
    private String appName;//流所属应用名称
    private String streamName;//流名称
    private String publishTime;//开始推流时刻 UTC 时间
    private String publishUrl;//推流完整 URL 地址
    private String userNum;//在线人数

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishUrl() {
        return publishUrl;
    }

    public void setPublishUrl(String publishUrl) {
        this.publishUrl = publishUrl;
    }
}
