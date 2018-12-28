package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:course_base_num
 */
public class CourseBaseNum {
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
     * 基数类型：0-参加报名人数 1-访问人数
     *
     * field:type  column:TYPE
     * default:0
     * 
     */
    private String type;

    /**
     * 基数
     *
     * field:count  column:COUNT
     * 
     */
    private Long count;

    /**
     * 修改时间
     *
     * field:updateTime  column:UPDATE_TIME
     * 
     */
    private Date updateTime;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 直播间ID
     *
     * field:roomId  column:ROOM_ID
     *
     */

    private Long roomId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}