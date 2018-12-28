package com.longlian.dto;

import com.longlian.model.Course;
import com.longlian.model.CourseAudit;

import java.util.Date;

/**
 * Created by liuhan on 2017-09-11.
 */
public class CourseAuditDto extends CourseAudit {
    private long size;//视频大小

    private String videoAddress;//视频地址
    private String imgAddress;//封面地址
    private String  isSeriesCourse ; //是否系列课 1-是系列课0-是单节课

    private Long seriesCourseId;

    public Long getSeriesCourseId() {
        return seriesCourseId;
    }

    public void setSeriesCourseId(Long seriesCourseId) {
        this.seriesCourseId = seriesCourseId;
    }

    private String isVerticalScreen; //是否是竖屏0-横屏 1-竖屏

    public String getIsSeriesCourse() {
        return isSeriesCourse;
    }

    public void setIsSeriesCourse(String isSeriesCourse) {
        this.isSeriesCourse = isSeriesCourse;
    }

    public String getIsVerticalScreen() {
        return isVerticalScreen;
    }

    public void setIsVerticalScreen(String isVerticalScreen) {
        this.isVerticalScreen = isVerticalScreen;
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

    private int  convertStatus;//转化状态0-未转化 -1-转化失败 1-转化成功 ,2-转化中
    private String convertAddress;

    private Long duration;//时长 豪秒

    private int width ;
    private int height;
}
