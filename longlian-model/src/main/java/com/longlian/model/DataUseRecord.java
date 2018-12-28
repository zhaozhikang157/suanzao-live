package com.longlian.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "data_use_record")
public class DataUseRecord {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COURSE_ID")
    private Long courseId;

    @Column(name = "ROOM_ID")
    private Long roomId;

    /**
     * 直播流量使用量
     */
    @Column(name = "LIVE_DATA_USE")
    private Long liveDataUse;

    /**
     * 回看和录播流量使用量
     */
    @Column(name = "REVIEW_DATA_USE")
    private Long reviewDataUse;

    /**
     * 流量总的使用量
     */
    @Column(name = "DATA_USE")
    private Long dataUse;

    /**
     * 使用时间
     */
    @Column(name = "USE_DATE")
    private Date useDate;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

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
     * @return COURSE_ID
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * @param courseId
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * @return ROOM_ID
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * @param roomId
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 获取直播流量使用量
     *
     * @return LIVE_DATA_USE - 直播流量使用量
     */
    public Long getLiveDataUse() {
        return liveDataUse;
    }

    /**
     * 设置直播流量使用量
     *
     * @param liveDataUse 直播流量使用量
     */
    public void setLiveDataUse(Long liveDataUse) {
        this.liveDataUse = liveDataUse;
    }

    /**
     * 获取回看和录播流量使用量
     *
     * @return REVIEW_DATA_USE - 回看和录播流量使用量
     */
    public Long getReviewDataUse() {
        return reviewDataUse;
    }

    /**
     * 设置回看和录播流量使用量
     *
     * @param reviewDataUse 回看和录播流量使用量
     */
    public void setReviewDataUse(Long reviewDataUse) {
        this.reviewDataUse = reviewDataUse;
    }

    /**
     * 获取流量总的使用量
     *
     * @return DATA_USE - 流量总的使用量
     */
    public Long getDataUse() {
        return dataUse;
    }

    /**
     * 设置流量总的使用量
     *
     * @param dataUse 流量总的使用量
     */
    public void setDataUse(Long dataUse) {
        this.dataUse = dataUse;
    }

    /**
     * 获取使用时间
     *
     * @return USE_DATE - 使用时间
     */
    public Date getUseDate() {
        return useDate;
    }

    /**
     * 设置使用时间
     *
     * @param useDate 使用时间
     */
    public void setUseDate(Date useDate) {
        this.useDate = useDate;
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
}