package com.longlian.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "room_func")
public class RoomFunc {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ROOM_ID
     */
    @Column(name = "ROOM_ID")
    private Long roomId;

    /**
     * 功能代码
     */
    @Column(name = "FUNC_CODE")
    private String funcCode;

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
     * 获取ROOM_ID
     *
     * @return ROOM_ID - ROOM_ID
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * 设置ROOM_ID
     *
     * @param roomId ROOM_ID
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 获取功能代码
     *
     * @return FUNC_CODE - 功能代码
     */
    public String getFuncCode() {
        return funcCode;
    }

    /**
     * 设置功能代码
     *
     * @param funcCode 功能代码
     */
    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
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