package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:course_ware
 */
public class CourseWare implements Serializable {
   
    private long id;
    private long courseId;//课程ID
    private String address;//课件文件地址
    private String status;//状态 0-正常 1-删除
    private Date createTime;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
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
}