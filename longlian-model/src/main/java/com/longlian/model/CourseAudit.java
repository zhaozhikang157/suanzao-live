package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:course_audit
 */
public class CourseAudit implements Serializable {
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
     * 审核状态 0-审核中 1-审核通过 -1审核不通过
     *
     * field:status  column:STATUS
     * default:0
     * 
     */
    private String status;

    /**
     * 审核人
     *
     * field:auditUserId  column:AUDIT_USER_ID
     * 
     */
    private Long auditUserId;

    /**
     * 审核人名称
     *
     * field:auditUserName  column:AUDIT_USER_NAME
     * 
     */
    private String auditUserName;

    /**
     * 审核时间
     *
     * field:auditTime  column:AUDIT_TIME
     * default:CURRENT_TIMESTAMP
     * 
     */
    private Date auditTime;

    /**
     *
     * field:createTime  column:CREATE_TIME
     * default:CURRENT_TIMESTAMP
     * 
     */
    private Date createTime;

    /**
     * 备注
     *
     * field:remark  column:REMARK
     * 
     */
    private String remark;

    private String isGarbage;

    public String getIsGarbage() {
        return isGarbage;
    }

    public void setIsGarbage(String isGarbage) {
        this.isGarbage = isGarbage;
    }

    public String getGarbageTip() {
        return garbageTip;
    }

    public void setGarbageTip(String garbageTip) {
        this.garbageTip = garbageTip;
    }

    private String garbageTip;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName == null ? null : auditUserName.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}