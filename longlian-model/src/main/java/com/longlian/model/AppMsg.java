package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:app_msg
 */
public class AppMsg  implements Serializable {

    private long id;
    private long appId;//接收人
    private String openid;//微信openid
    private Integer type;//消息类型 MsgType
    private long tableId;//表Id 0为系统消息 其他为消息类型的表的ID
    private String content;//内容
    private long roomId;//直播间id

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    private long status;//状态0：未读 1：已读 2：删除
    private Date createTime;//创建时间
    private Date readTime;//读取时间
    private String cAct;//点击动作
    private String courseStatus; //课程状态 0-正常 1-下架
    private long teacherId; //课程老师
    private String isSeriesCourse; //课程是否是系列课
    private long seriesCourseId; //课程所属系列课id
    private String pushUrl; //

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getIsSeriesCourse() {
        return isSeriesCourse;
    }

    public void setIsSeriesCourse(String isSeriesCourse) {
        this.isSeriesCourse = isSeriesCourse;
    }

    public long getSeriesCourseId() {
        return seriesCourseId;
    }

    public void setSeriesCourseId(long seriesCourseId) {
        this.seriesCourseId = seriesCourseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public String getcAct() {
        return cAct;
    }

    public void setcAct(String cAct) {
        this.cAct = cAct == null ? null : cAct.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
