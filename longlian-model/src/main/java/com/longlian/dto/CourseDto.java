package com.longlian.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.longlian.model.Course;
import com.longlian.model.Video;
import com.longlian.type.CustomJsonDateDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/12.
 */
public class CourseDto extends Course {
    private String coursePhoto;//图片课件
    private String coursePhotos;//图片课件base64
    private String isRelayCourse;// 是否是转播课

    public String getCoursePhotos() {
        return coursePhotos;
    }

    public String getIsRelayCourse() {
		return isRelayCourse;
	}

	public void setIsRelayCourse(String isRelayCourse) {
		this.isRelayCourse = isRelayCourse;
	}

	public void setCoursePhotos(String coursePhotos) {
        this.coursePhotos = coursePhotos;
    }

    private long appId;//appUser id
    private String appUserName;//老师或机构名称
    private String courseTypeName;//课程类型名称s
    private String createTimes;//课程创建时间
    private String liveStatus;//直播状态
    private Map distributionMap;//课程分销金额和人数
    private String courseImg;//课程简介图片
    private String courseContent;//课程简介图片

    private long buyCount;
    private BigDecimal totalCourseAmount;
    private Long videoId;

    private Long duration;

    private String modelUrl;//模板url
    private String cardUrl;//邀请卡url

    private String liveRoomName;
    private Long realBuyCount;
    private BigDecimal totalAmount;//课总收益
    private BigDecimal totalRelayCourseAmount;//转播课总收益

    private int relayCourseType;//推送公众号类型  0：设置转播课   1：转播课程

    public Long getRealBuyCount() {
        return realBuyCount;
    }

    public void setRealBuyCount(Long realBuyCount) {
        this.realBuyCount = realBuyCount;
    }

    public String getLiveRoomName() {
        return liveRoomName;
    }

    public void setLiveRoomName(String liveRoomName) {
        this.liveRoomName = liveRoomName;
    }

    public Long getVideoId() {
        return videoId;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getCardUrl() {
        return cardUrl;
    }

    public void setCardUrl(String cardUrl) {
        this.cardUrl = cardUrl;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    private Date startTime;//开始时间

    public String getCourseImg() {
        return courseImg;
    }

    public void setCourseImg(String courseImg) {
        this.courseImg = courseImg;
    }

    public Map getDistributionMap() {
        return distributionMap;
    }

    public void setDistributionMap(Map distributionMap) {
        this.distributionMap = distributionMap;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes(String createTimes) {
        this.createTimes = createTimes;
    }

    public String getCoursePhoto() {
        return coursePhoto;
    }

    public void setCoursePhoto(String coursePhoto) {
        this.coursePhoto = coursePhoto;
    }

    @Override
    public long getAppId() {
        return appId;
    }

    @Override
    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public int getRelayCourseType() {
        return relayCourseType;
    }

    public void setRelayCourseType(int relayCourseType) {
        this.relayCourseType = relayCourseType;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
    }

    public long getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(long buyCount) {
        this.buyCount = buyCount;
    }

    public BigDecimal getTotalCourseAmount() {
        return totalCourseAmount;
    }

    public void setTotalCourseAmount(BigDecimal totalCourseAmount) {
        this.totalCourseAmount = totalCourseAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalRelayCourseAmount() {
        return totalRelayCourseAmount;
    }

    public void setTotalRelayCourseAmount(BigDecimal totalRelayCourseAmount) {
        this.totalRelayCourseAmount = totalRelayCourseAmount;
    }
}
