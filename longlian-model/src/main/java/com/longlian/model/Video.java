package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:video
 */
public class Video {
    private long id;//ID
    private long courseId;//课程名称

    private String failReason;

    public long getId() {
        return id;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getVideoAddress() {
        return videoAddress;
    }

    public void setVideoAddress(String videoAddress) {
        this.videoAddress = videoAddress;
    }

    public String getImgAddress() {
        return imgAddress;
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    public int getConvertStatus() {
        return convertStatus;
    }

    public void setConvertStatus(int convertStatus) {
        this.convertStatus = convertStatus;
    }

    public String getConvertAddress() {
        return convertAddress;
    }

    public void setConvertAddress(String convertAddress) {
        this.convertAddress = convertAddress;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }


    private long createUserId;//上传人Id
    private Date createTime;//创建时间
    private long size;//视频大小

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private String videoAddress;//视频地址
    private String imgAddress;//封面地址

    private int  convertStatus;//转化状态0-未转化 -1-转化失败 1-转化成功 ,2-转化中
    private String convertAddress;

    private Long duration;//时长 豪秒

    private int width ;
    private int height;


}