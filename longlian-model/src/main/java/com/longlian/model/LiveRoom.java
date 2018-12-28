package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * tablename:live_room
 */
public class LiveRoom implements Serializable {

    private long id;
    private long appId;//APP_ID
    private String name;//名称
    private Integer status;// 状态0-申请中 1-通过 2-审核失败
    private Date createTime;//创建时间
    private String remark;//备注
    private String inviteTmp;//邀请卡地址
    private String bgAddress;//背景图片地址
    private String coverssAddress;//封面图片
    private Date authTime;//审核时间
    private Date updateTime;//修改时间
    private long authUserId;//审核人ID
    private String authUserName;//审核人姓名
    private String authRemark;//审核备注
    private String weixinNum;//微信号
    private String inviteAddr;
    private String liveRoomNo;
    private String  messageFlag;
    private long dataCount;
    private long dayDataCount;
    private long reviewCount;
    private long dayReviewCount;
    private long reduceDataCount;
    private String roomStatus;
    private String disableRemark;
    private int autoCloseTime;//课程用户没关闭时，自动关闭时间
    private int isShow;//是否显示直播间课程

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public long getReduceDataCount() {
        return reduceDataCount;
    }

    public void setReduceDataCount(long reduceDataCount) {
        this.reduceDataCount = reduceDataCount;
    }

    public long getDataCount() {
        return dataCount;
    }

    public void setDataCount(long dataCount) {
        this.dataCount = dataCount;
    }

    public long getDayDataCount() {
        return dayDataCount;
    }

    public void setDayDataCount(long dayDataCount) {
        this.dayDataCount = dayDataCount;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getDayReviewCount() {
        return dayReviewCount;
    }

    public void setDayReviewCount(long dayReviewCount) {
        this.dayReviewCount = dayReviewCount;
    }

    public String getLiveRoomNo() {
        return liveRoomNo;
    }

    public void setLiveRoomNo(String liveRoomNo) {
        this.liveRoomNo = liveRoomNo;
    }

    public String getInviteAddr() {
        return inviteAddr;
    }

    public void setInviteAddr(String inviteAddr) {
        this.inviteAddr = inviteAddr;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getInviteTmp() {
        return inviteTmp;
    }

    public void setInviteTmp(String inviteTmp) {
        this.inviteTmp = inviteTmp;
    }

    public String getBgAddress() {
        return bgAddress;
    }

    public void setBgAddress(String bgAddress) {
        this.bgAddress = bgAddress == null ? null : bgAddress.trim();
    }

    public String getCoverssAddress() {
        return coverssAddress;
    }

    public void setCoverssAddress(String coverssAddress) {
        this.coverssAddress = coverssAddress == null ? null : coverssAddress.trim();
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(long authUserId) {
        this.authUserId = authUserId;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName == null ? null : authUserName.trim();
    }

    public String getAuthRemark() {
        return authRemark;
    }

    public void setAuthRemark(String authRemark) {
        this.authRemark = authRemark == null ? null : authRemark.trim();
    }

    public String getWeixinNum() {
        return weixinNum;
    }

    public void setWeixinNum(String weixinNum) {
        this.weixinNum = weixinNum == null ? null : weixinNum.trim();
    }

    public String getMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(String messageFlag) {
        this.messageFlag = messageFlag;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public String getDisableRemark() {
        return disableRemark;
    }

    public void setDisableRemark(String disableRemark) {
        this.disableRemark = disableRemark;
    }

    public int getAutoCloseTime() {
        return autoCloseTime;
    }

    public void setAutoCloseTime(int autoCloseTime) {
        this.autoCloseTime = autoCloseTime;
    }
}