package com.longlian.model;

import javax.persistence.*;

@Table(name = "func")
public class Func {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 功能代码
     */
    @Column(name = "FUNC_CODE")
    private String funcCode;

    /**
     * 功能说明
     */
    @Column(name = "FUNC_DISC")
    private String funcDisc;

    /**
     * 功能名
     */
    @Column(name = "FUNC_NAME")
    private String funcName;

    /**
     * 状态0-是未启用 1-是启用
     */
    @Column(name = "STATUS")
    private String status;

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
     * 获取功能说明
     *
     * @return FUNC_DISC - 功能说明
     */
    public String getFuncDisc() {
        return funcDisc;
    }

    /**
     * 设置功能说明
     *
     * @param funcDisc 功能说明
     */
    public void setFuncDisc(String funcDisc) {
        this.funcDisc = funcDisc;
    }

    /**
     * 获取功能名
     *
     * @return FUNC_NAME - 功能名
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * 设置功能名
     *
     * @param funcName 功能名
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /**
     * 获取状态0-是未启用 1-是启用
     *
     * @return STATUS - 状态0-是未启用 1-是启用
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态0-是未启用 1-是启用
     *
     * @param status 状态0-是未启用 1-是启用
     */
    public void setStatus(String status) {
        this.status = status;
    }
}