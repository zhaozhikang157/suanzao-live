package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/12/5.
 */
public class MsgCancel {
    private static Logger log = LoggerFactory.getLogger(MsgCancel.class);

    private Long id;                //ID
    private Long chatRoomId;        //聊天室ID
    private Long courseId;          //课程ID
    private String msgClientId;     //消息ID
    private Date createTime;        //创建时间
    private Long optId;             //撤回者ID

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getMsgClientId() {
        return msgClientId;
    }

    public void setMsgClientId(String msgClientId) {
        this.msgClientId = msgClientId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getOptId() {
        return optId;
    }

    public void setOptId(Long optId) {
        this.optId = optId;
    }
}
