package com.longlian.model;

import java.io.Serializable;

import java.util.Date;

/**
 *
 * tablename:course
 */
public class LiveStream implements Serializable {

    private long courseId;
    private String courseName;
    private Date startTime;//开始时间
    private Date endTime;//更新时间

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}