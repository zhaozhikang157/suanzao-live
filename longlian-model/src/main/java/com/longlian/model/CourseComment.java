package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:course_comment
 */
public class CourseComment implements Serializable {

    private Long id;
    private Long courseId;//课程_主键ID
    private Long appId;//用户ID
    private String content;//内容
    private String status;// 0-正常 1-删除
    private Date createTime;//创建时间
    private long  seriesCourseId;  //系列课ID  


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getSeriesCourseId() {
        return seriesCourseId;
    }

    public void setSeriesCourseId(long seriesCourseId) {
        this.seriesCourseId = seriesCourseId;
    }
}