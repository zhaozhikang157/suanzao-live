package com.longlian.model.course;

import java.io.Serializable;
import java.util.Date;

/**
 * 指定课程邀请卡
 */
public class CoursePrivateCard implements Serializable {
    private Long id;
    private Long userId;//用户id
    private Long courseId;//课程id
    private Date addTime;//添加时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
