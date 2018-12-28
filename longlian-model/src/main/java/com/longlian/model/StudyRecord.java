package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:study_record
 */
public class StudyRecord {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 课程ID
     *
     * field:courseId  column:COURSE_ID
     * 
     */
    private Long courseId;

    /**
     * APP_ID
     *
     * field:appId  column:APP_ID
     * default:0
     * 
     */
    private Long appId;

    /**
     * 创建时间
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

    /**
     * 是否是回看 0-不是 1-是
     *
     * field:review  column:REVIEW
     * default:0
     * 
     */
    private String review;

    private Long seriesCourseId;

    public boolean isVirtualUser() {
        return isVirtualUser;
    }

    public void setVirtualUser(boolean virtualUser) {
        isVirtualUser = virtualUser;
    }

    private boolean isVirtualUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review == null ? null : review.trim();
    }

    public Long getSeriesCourseId() {
        return seriesCourseId;
    }

    public void setSeriesCourseId(Long seriesCourseId) {
        this.seriesCourseId = seriesCourseId;
    }
}