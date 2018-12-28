package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:course_avatar_user
 */
public class CourseAvatarUser {
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
     * 用户ID
     *
     * field:userId  column:USER_ID
     * 
     */
    private Long userId;

    /**
     * 创建时间
     *
     * field:createTime  column:CREATE_TIME
     * 
     */
    private Date createTime;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}