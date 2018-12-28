package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:push_msg
 */
public class PushMsg implements Serializable {

    private long id;
    private String title;//标题
    private Integer sendObj;//发送对象:0-所有人 1-指定对象 2-android 3-ios
    private String sendAppointObj;//发送指定对象,手机号以;分割
    private String content;//消息内容
    private Integer status;//1-未发送 0-已发送
    private Date sendTime;//发送时间
    private Date createTime;//新建时间
    private long createUserId;//创建人
    private String pushUrl;//推送网址
    private String pushType;//推送类型 1：为H5
    private String pushUser;//推送人
    private Date sendTimeEnd;//发送时间,搜索截止时间
    private String sendTimeFormat;//时间格式化

    public String getSendTimeFormat() {
        return sendTimeFormat;
    }

    public void setSendTimeFormat(String sendTimeFormat) {
        this.sendTimeFormat = sendTimeFormat;
    }

    public Date getSendTimeEnd() {
        return sendTimeEnd;
    }

    public void setSendTimeEnd(Date sendTimeEnd) {
        this.sendTimeEnd = sendTimeEnd;
    }

    public String getPushUser() {
        return pushUser;
    }

    public void setPushUser(String pushUser) {
        this.pushUser = pushUser;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getSendObj() {
        return sendObj;
    }

    public void setSendObj(Integer sendObj) {
        this.sendObj = sendObj;
    }

    public String getSendAppointObj() {
        return sendAppointObj;
    }

    public void setSendAppointObj(String sendAppointObj) {
        this.sendAppointObj = sendAppointObj == null ? null : sendAppointObj.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }
}