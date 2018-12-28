package com.longlian.dto;

import java.util.Date;

/**
 * Created by Administrator on 2018/5/9.
 */
public class CoursePrivateCardDto {
    private Long id;
    private Long userId;//用户id
    private Long courseId;//课程id
    private Date addTime;//添加时间
    private String liveTopic;
    private String appUserName;

    public String getLiveTopic() {
        return liveTopic;
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public void setLiveTopic(String liveTopic) {

        this.liveTopic = liveTopic;
    }

    public Long getId() {
        return id;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public void setCourseId(Long courseId) {

        this.courseId = courseId;
    }

    public void setUserId(Long userId) {

        this.userId = userId;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Date getAddTime() {

        return addTime;
    }

    public Long getCourseId() {

        return courseId;
    }

    public Long getUserId() {

        return userId;
    }
}
