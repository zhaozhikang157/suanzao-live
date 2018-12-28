package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/11/9.
 */
public class CourseDataUse {

    private long id ;
    private long courseId;
    private long liveDataUse;
    private long reviewUse;
    private Date updateTime;

    private Date startUseTime;

    public Date getStartUseTime() {
        return startUseTime;
    }

    public void setStartUseTime(Date startUseTime) {
        this.startUseTime = startUseTime;
    }

    public Date getEndUseTime() {
        return endUseTime;
    }

    public void setEndUseTime(Date endUseTime) {
        this.endUseTime = endUseTime;
    }

    private Date endUseTime;

    public long getId() {
        return id;
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

    public long getLiveDataUse() {
        return liveDataUse;
    }

    public void setLiveDataUse(long liveDataUse) {
        this.liveDataUse = liveDataUse;
    }

    public long getReviewUse() {
        return reviewUse;
    }

    public void setReviewUse(long reviewUse) {
        this.reviewUse = reviewUse;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
