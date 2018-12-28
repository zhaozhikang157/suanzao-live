package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/22.
 */
public class UserDistribution implements Serializable {
    private long id;// ID                   bigint not null auto_increment,
    private long appId;//APP_ID               bigint default 0 comment '用户Id',
    private long roomId;//ROOM_ID              bigint default 0 comment '直播间ID',
    private long invitationUserId;//INVITION_USER_ID     bigint default 0 comment '分销人',
    private Date createTime;// CREATE_TIME          datetime default '0' comment '创建时间',

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

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getInvitationUserId() {
        return invitationUserId;
    }

    public void setInvitationUserId(long invitationUserId) {
        this.invitationUserId = invitationUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
