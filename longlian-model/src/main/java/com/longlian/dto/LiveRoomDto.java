package com.longlian.dto;

import com.longlian.model.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pangchao on 2017/2/15.
 */
public class LiveRoomDto extends LiveRoom {
    private static Logger log = LoggerFactory.getLogger(LiveRoomDto.class);
    private String beginTime;//查询开始时间
    private String endTime;//查询开始时间
    private String appName;//用户名称
    private String type;//状态
    private String mobile;//用户手机号
    private String idCardFront;//身份证正面
    private String idCardRear;//身份证反面
    private String userAvatar;//身份证反面
    private String sortType; //主播间排序类别

    private String dataCounts;
    private String reviewCounts;
    private String reduceDataCounts;


    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        LiveRoomDto.log = log;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardRear() {
        return idCardRear;
    }

    public void setIdCardRear(String idCardRear) {
        this.idCardRear = idCardRear;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getDataCounts() {
        return dataCounts;
    }

    public void setDataCounts(String dataCounts) {
        this.dataCounts = dataCounts;
    }

    public String getReviewCounts() {
        return reviewCounts;
    }

    public void setReviewCounts(String reviewCounts) {
        this.reviewCounts = reviewCounts;
    }

    public String getReduceDataCounts() {
        return reduceDataCounts;
    }

    public void setReduceDataCounts(String reduceDataCounts) {
        this.reduceDataCounts = reduceDataCounts;
    }
}