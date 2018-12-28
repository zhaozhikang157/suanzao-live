package com.longlian.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "live_connect")
public class LiveConnect {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 老师
     */
    @Column(name = "TEACHER")
    private Long teacher;

    /**
     * 学生
     */
    @Column(name = "STUDENT")
    private Long student;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 连麦请求ID
     */
    @Column(name = "REQ_ID")
    private Long reqId;

    /**
     * 课程ID
     */
    @Column(name = "COURSE_ID")
    private Long courseId;

    /**
     * 申请人
     */
    @Column(name = "APPLY_USER")
    private Long applyUser;

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取老师
     *
     * @return TEACHER - 老师
     */
    public Long getTeacher() {
        return teacher;
    }

    /**
     * 设置老师
     *
     * @param teacher 老师
     */
    public void setTeacher(Long teacher) {
        this.teacher = teacher;
    }

    /**
     * 获取学生
     *
     * @return STUDENT - 学生
     */
    public Long getStudent() {
        return student;
    }

    /**
     * 设置学生
     *
     * @param student 学生
     */
    public void setStudent(Long student) {
        this.student = student;
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_TIME - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取连麦请求ID
     *
     * @return REQ_ID - 连麦请求ID
     */
    public Long getReqId() {
        return reqId;
    }

    /**
     * 设置连麦请求ID
     *
     * @param reqId 连麦请求ID
     */
    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    /**
     * 获取课程ID
     *
     * @return COURSE_ID - 课程ID
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * 设置课程ID
     *
     * @param courseId 课程ID
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * 获取申请人
     *
     * @return APPLY_USER - 申请人
     */
    public Long getApplyUser() {
        return applyUser;
    }

    /**
     * 设置申请人
     *
     * @param applyUser 申请人
     */
    public void setApplyUser(Long applyUser) {
        this.applyUser = applyUser;
    }
}