package com.longlian.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "live_connect_request")
public class LiveConnectRequest {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 申请人
     */
    @Column(name = "APPLY_USER")
    private Long applyUser;

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
     * 申请时间
     */
    @Column(name = "APPLY_TIME")
    private Date applyTime;

    /**
     * 0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 失败原因
     */
    @Column(name = "FAIL_MSG")
    private String failMsg;

    /**
     * 成功时间
     */
    @Column(name = "SUCCESS_TIME")
    private Date successTime;

    /**
     * 课程ID
     */
    @Column(name = "COURSE_ID")
    private Long courseId;

    /**
     * 关闭时间
     */
    @Column(name = "CLOSE_TIME")
    private Date closeTime;

    /**
     * 同意状态 0-邀请中 1- 同意 -1不同意
     */
    @Column(name = "AGREE_STATUS")
    private String agreeStatus;

    /**
     * 连麦失效时间(秒)
     */
    @Column(name = "LOSE_TIME")
    private Long loseTime;

    /**
     * 学生状态 0-不在线 1-在线
     */
    @Column(name = "STUDENT_STATUS")
    private String studentStatus;

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
     * 获取申请时间
     *
     * @return APPLY_TIME - 申请时间
     */
    public Date getApplyTime() {
        return applyTime;
    }

    /**
     * 设置申请时间
     *
     * @param applyTime 申请时间
     */
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    /**
     * 获取0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
     *
     * @return STATUS - 0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
     *
     * @param status 0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取失败原因
     *
     * @return FAIL_MSG - 失败原因
     */
    public String getFailMsg() {
        return failMsg;
    }

    /**
     * 设置失败原因
     *
     * @param failMsg 失败原因
     */
    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    /**
     * 获取成功时间
     *
     * @return SUCCESS_TIME - 成功时间
     */
    public Date getSuccessTime() {
        return successTime;
    }

    /**
     * 设置成功时间
     *
     * @param successTime 成功时间
     */
    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
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
     * 获取关闭时间
     *
     * @return CLOSE_TIME - 关闭时间
     */
    public Date getCloseTime() {
        return closeTime;
    }

    /**
     * 设置关闭时间
     *
     * @param closeTime 关闭时间
     */
    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * 获取同意状态 0-邀请中 1- 同意 -1不同意
     *
     * @return AGREE_STATUS - 同意状态 0-邀请中 1- 同意 -1不同意
     */
    public String getAgreeStatus() {
        return agreeStatus;
    }

    /**
     * 设置同意状态 0-邀请中 1- 同意 -1不同意
     *
     * @param agreeStatus 同意状态 0-邀请中 1- 同意 -1不同意
     */
    public void setAgreeStatus(String agreeStatus) {
        this.agreeStatus = agreeStatus;
    }

    /**
     * 获取连麦失效时间(秒)
     *
     * @return LOSE_TIME - 连麦失效时间(秒)
     */
    public Long getLoseTime() {
        return loseTime;
    }

    /**
     * 设置连麦失效时间(秒)
     *
     * @param loseTime 连麦失效时间(秒)
     */
    public void setLoseTime(Long loseTime) {
        this.loseTime = loseTime;
    }

    /**
     * 获取学生状态 0-不在线 1-在线
     *
     * @return STUDENT_STATUS - 学生状态 0-不在线 1-在线
     */
    public String getStudentStatus() {
        return studentStatus;
    }

    /**
     * 设置学生状态 0-不在线 1-在线
     *
     * @param studentStatus 学生状态 0-不在线 1-在线
     */
    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }
}