package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:study_record_detail
 */
public class StudyRecordDetail {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 学习开始时间
     *
     * field:studyStartTime  column:STUDY_START_TIME
     * 
     */
    private Date studyStartTime;

    /**
     * 学习结束时间
     *
     * field:studyEndTime  column:STUDY_END_TIME
     * 
     */
    private Date studyEndTime;

    /**
     * 开始学习时间点（课程时间）
     *
     * field:studyStartPoint  column:STUDY_START_POINT
     * 
     */
    private Long studyStartPoint;

    /**
     * 结束学习时间点（课程时间）
     *
     * field:studyEndPoint  column:STUDY_END_POINT
     * 
     */
    private Long studyEndPoint;

    /**
     * 结束学习时间点是否是终结点（下次会从0开始学习）0-不是 1-是
     *
     * field:isEnd  column:IS_END
     * default:0
     * 
     */
    private String isEnd;

    /**
     * 学习记录ID
     *
     * field:recordId  column:RECORD_ID
     * 
     */
    private Long recordId;
    /**
     * 课程Id
     *
     * field:courseId  column:COURSE_ID
     *
     */
    private Long courseId;

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

    /**

     * APP_ID
     *
     * field:appId  column:APP_ID
     *
     */
    private Long appId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStudyStartTime() {
        return studyStartTime;
    }

    public void setStudyStartTime(Date studyStartTime) {
        this.studyStartTime = studyStartTime;
    }

    public Date getStudyEndTime() {
        return studyEndTime;
    }

    public void setStudyEndTime(Date studyEndTime) {
        this.studyEndTime = studyEndTime;
    }

    public Long getStudyStartPoint() {
        return studyStartPoint;
    }

    public void setStudyStartPoint(Long studyStartPoint) {
        this.studyStartPoint = studyStartPoint;
    }

    public Long getStudyEndPoint() {
        return studyEndPoint;
    }

    public void setStudyEndPoint(Long studyEndPoint) {
        this.studyEndPoint = studyEndPoint;
    }

    public String getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(String isEnd) {
        this.isEnd = isEnd == null ? null : isEnd.trim();
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}