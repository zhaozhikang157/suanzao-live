package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * tablename:course
 */
public class Course implements Serializable {
    private long id;
    private long roomId;
    private long appId;
    private String liveTopic;//直播主题
    private String remark;//课程介绍
    private String liveWay;// 直播方式0-视频直播 1-语音直播
    private String liveType;//直播类型0-公开直播 1-加密直播
    private String livePwd;//加密直播密码
    private String divideScale;//分销比例0-5:5 1-6:4 默认为空，不分销
    private String coverssAddress;//封面地址
    private BigDecimal chargeAmt;//价格
    private Date startTime;//开始时间
    private Date updateTime;//更新时间
    private String status;//状态0-上线 1-下线
    private Date createTime;//创建时间
    private Integer orderSort;//排序号
    private long courseType;//课程分类
    private Integer pushPraiseCount;//点赞次数
    private Integer visitCount;//访问人数
    private Integer joinCount;// 参加报名人数（付费）
    private Integer studyCount;//学习人数(真实来学过的人)<参加报名人数
    private Integer commentCount;//评论人数
    private Date endTime;//实际结束时间
    private String liveAddress;//直播地址
    private String inviteTmp;//邀请卡模板地址
    private String  isSeriesCourse ; //是否系列课 1-是系列课0-是单节课
    private long  seriesCourseId;  //系列课ID
    private String adAddress;//广告地址
    private String isConnection;//是否连接直播流
    private String customDistribution;//自定义分销比例
    private int retryTime;//数据库无对应字段
    private String isHide;  //是否隐藏0-显示 1-隐藏
    private int isRelay;//是否允许转播 1：是  0：否
    private BigDecimal relayCharge;//'转播金额
    private float relayScale;//转播课购买收益比例
    private int isOpened;//是否开启过转播  1：是  0：否
    private String isRelayHide;  //是否隐藏0-显示 1-隐藏
    private Date setRelayTime;  //设置转播时间

    public int getIsRelay() {
        return isRelay;
    }

    public void setIsRelay(int isRelay) {
        this.isRelay = isRelay;
    }

    public String getIsRelayHide() {
		return isRelayHide;
	}

	public void setIsRelayHide(String isRelayHide) {
		this.isRelayHide = isRelayHide;
	}

	public BigDecimal getRelayCharge() {
        return relayCharge;
    }

    public void setRelayCharge(BigDecimal relayCharge) {
        this.relayCharge = relayCharge;
    }

    public float getRelayScale() {
        return relayScale;
    }

    public void setRelayScale(float relayScale) {
        this.relayScale = relayScale;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    private BigDecimal typeSort;   //分类排序  改变某一分类下的排序 不影响推荐下面排序

    public BigDecimal getTypeSort() {
        return typeSort;
    }

    public void setTypeSort(BigDecimal typeSort) {
        this.typeSort = typeSort;
    }

    public String getCustomDistribution() {
        return customDistribution;
    }

    public void setCustomDistribution(String customDistribution) {
        this.customDistribution = customDistribution;
    }

    public String getIsConnection() {
        return isConnection;
    }

    public void setIsConnection(String isConnection) {
        this.isConnection = isConnection;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public String getAdAddress() {
        return adAddress;
    }

    public void setAdAddress(String adAddress) {
        this.adAddress = adAddress;
    }

    public String getVerticalCoverssAddress() {
        return verticalCoverssAddress;
    }

    public void setVerticalCoverssAddress(String verticalCoverssAddress) {
        this.verticalCoverssAddress = verticalCoverssAddress;
    }

    private Integer autoCloseTime;//自动关闭时间
    private String  verticalCoverssAddress;//竖屏直播的背景图

    public Integer getEndedCount() {
        return endedCount;
    }

    public void setEndedCount(Integer endedCount) {
        this.endedCount = endedCount;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    private Integer  endedCount;  //已完成多少节课
    private String isDelete;  //是否已删除0-未删除 1-已删除



    public Integer getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(Integer updatedCount) {
        this.updatedCount = updatedCount;
    }

    private Integer coursePlanCount;  //排课计划
    private Integer updatedCount;//已更新多少节课
    

    public String getInviteTmp() {
        return inviteTmp;
    }

    public void setInviteTmp(String inviteTmp) {
        this.inviteTmp = inviteTmp;
    }

    public String getTeacherDesc() {
        return teacherDesc;
    }

    public void setTeacherDesc(String teacherDesc) {
        this.teacherDesc = teacherDesc;
    }

    private String pushAddress;//推流地址
    private String videoAddress;//视频地址
    private Integer recoTime;//10-180秒，默认60秒
    private Long chatRoomId;//云信聊天室ROOMid
    private String hlsLiveAddress;//m3u8直播地址
    private String isShowWare;//是否显示课件0-不显示 1-显示
    private String targetUsers;//适宜人群
    private String  teacherDesc;    //老师介绍
    
    private String pushType;//推送方式0-默认 1-文字 2-图片

    public BigDecimal getSort() {
        return sort;
    }

    public void setSort(BigDecimal sort) {
        this.sort = sort;
    }

    public String getCanConnect() {
        return canConnect == null ? "0" : canConnect;
    }

    public void setCanConnect(String canConnect) {
        this.canConnect = canConnect;
    }

    private String pushContent; //推送内容
    private String colImgAddress;  //列表页面图片
    private String isVerticalScreen; //是否是竖屏0-横屏 1-竖屏
    private Long cleanScreenTime;   //清屏时间
    private String isRecorded;      //是否是录播课程0-不是录播 1-是录播 
    private Long recTime;           //录播时长秒
    private Long trySeeTime;        //试看时长
    private Long mustShareTime;           //观看强制分享时长
    private BigDecimal sort; //综合排序值
    private String canConnect; //是否允许学生连麦0-不允许 1-允许

    public String getIsInviteCode() {
        return isInviteCode;
    }

    public void setIsInviteCode(String isInviteCode) {
        this.isInviteCode = isInviteCode;
    }

    private String isInviteCode;   //是否支持邀请码0-不支持 1-支持


    private Long recoSort;     //人工评分

    public Long getCleanScreenTime() {
        return cleanScreenTime;
    }

    public void setCleanScreenTime(Long cleanScreenTime) {
        this.cleanScreenTime = cleanScreenTime;
    }

    public String getIsShowWare() {
        return isShowWare;
    }

    public void setIsShowWare(String isShowWare) {
        this.isShowWare = isShowWare;
    }

    public String getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(String targetUsers) {
        this.targetUsers = targetUsers;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getHlsLiveAddress() {
        return hlsLiveAddress;
    }

    public void setHlsLiveAddress(String hlsLiveAddress) {
        this.hlsLiveAddress = hlsLiveAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getLiveTopic() {
        return liveTopic;
    }

    public void setLiveTopic(String liveTopic) {
        this.liveTopic = liveTopic == null ? null : liveTopic.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getLiveWay() {
        return liveWay;
    }

    public void setLiveWay(String liveWay) {
        this.liveWay = liveWay == null ? null : liveWay.trim();
    }

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType == null ? null : liveType.trim();
    }

    public String getLivePwd() {
        return livePwd;
    }

    public void setLivePwd(String livePwd) {
        this.livePwd = livePwd == null ? null : livePwd.trim();
    }

    public String getDivideScale() {
        return divideScale;
    }

    public void setDivideScale(String divideScale) {
        this.divideScale = divideScale == null ? null : divideScale.trim();
    }

    public String getCoverssAddress() {
        return coverssAddress;
    }

    public void setCoverssAddress(String coverssAddress) {
        this.coverssAddress = coverssAddress == null ? null : coverssAddress.trim();
    }

    public BigDecimal getChargeAmt() {
        return chargeAmt;
    }

    public void setChargeAmt(BigDecimal chargeAmt) {
        this.chargeAmt = chargeAmt;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOrderSort() {
        return orderSort;
    }

    public void setOrderSort(Integer orderSort) {
        this.orderSort = orderSort;
    }

    public long getCourseType() {
        return courseType;
    }

    public void setCourseType(long courseType) {
        this.courseType = courseType;
    }

    public Integer getPushPraiseCount() {
        return pushPraiseCount;
    }

    public void setPushPraiseCount(Integer pushPraiseCount) {
        this.pushPraiseCount = pushPraiseCount;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public Integer getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(Integer joinCount) {
        this.joinCount = joinCount;
    }

    public Integer getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(Integer studyCount) {
        this.studyCount = studyCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLiveAddress() {
        return liveAddress;
    }

    public void setLiveAddress(String liveAddress) {
        this.liveAddress = liveAddress;
    }

    public String getPushAddress() {
        return pushAddress;
    }

    public void setPushAddress(String pushAddress) {
        this.pushAddress = pushAddress;
    }

    public String getVideoAddress() {
        return videoAddress;
    }

    public void setVideoAddress(String videoAddress) {
        this.videoAddress = videoAddress;
    }

    public Integer getRecoTime() {
        return recoTime;
    }

    public void setRecoTime(Integer recoTime) {
        this.recoTime = recoTime;
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

    public Integer getCoursePlanCount() {
        return coursePlanCount;
    }

    public void setCoursePlanCount(Integer coursePlanCount) {
        this.coursePlanCount = coursePlanCount;
    }

    public Integer getAutoCloseTime() {
        return autoCloseTime;
    }

    public void setAutoCloseTime(Integer autoCloseTime) {
        this.autoCloseTime = autoCloseTime;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getColImgAddress() {
        return colImgAddress;
    }

    public void setColImgAddress(String colImgAddress) {
        this.colImgAddress = colImgAddress;
    }

    public String getIsVerticalScreen() {
        return isVerticalScreen;
    }

    public void setIsVerticalScreen(String isVerticalScreen) {
        this.isVerticalScreen = isVerticalScreen;
    }

    public String getIsRecorded() {
        return isRecorded;
    }

    public void setIsRecorded(String isRecorded) {
        this.isRecorded = isRecorded;
    }

    public Long getRecTime() {
        return recTime;
    }

    public void setRecTime(Long recTime) {
        this.recTime = recTime;
    }

    public Long getTrySeeTime() {
        if (trySeeTime == null) {
            return 0l;
        }
        return trySeeTime;
    }

    public void setTrySeeTime(Long trySeeTime) {
        this.trySeeTime = trySeeTime;
    }

    public Long getMustShareTime() {
        return mustShareTime;
    }

    public void setMustShareTime(Long mustShareTime) {
        this.mustShareTime = mustShareTime;
    }

    public Long getRecoSort() {
        return recoSort;
    }

    public void setRecoSort(Long recoSort) {
        this.recoSort = recoSort;
    }

    public int getIsOpened() {
        return isOpened;
    }

    public void setIsOpened(int isOpened) {
        this.isOpened = isOpened;
    }

    public Date getSetRelayTime() {
        return setRelayTime;
    }

    public void setSetRelayTime(Date setRelayTime) {
        this.setRelayTime = setRelayTime;
    }
}