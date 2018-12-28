package com.longlian.model;

import java.util.Date;

/**
 *
 * tablename:user_machine_info
 */
public class UserMachineInfo {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 用户ID
     *
     * field:userId  column:USER_ID
     * 
     */
    private Long userId;

    /**
     * 机器码
     *
     * field:machineCode  column:MACHINE_CODE
     * 
     */
    private String machineCode;

    /**
     * 机器类型androidios
     *
     * field:machineType  column:MACHINE_TYPE
     * 
     */
    private String machineType;

    /**
     * 登录时间
     *
     * field:loginTime  column:LOGIN_TIME
     * 
     */
    private Date loginTime;

    /**
     * 记住不升级版本
     *
     * field:connotUpdateVersion  column:CONNOT_UPDATE_VERSION
     * 
     */
    private String connotUpdateVersion;

    /**
     * 当前版本
     *
     * field:version  column:VERSION
     * 
     */
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode == null ? null : machineCode.trim();
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType == null ? null : machineType.trim();
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getConnotUpdateVersion() {
        return connotUpdateVersion;
    }

    public void setConnotUpdateVersion(String connotUpdateVersion) {
        this.connotUpdateVersion = connotUpdateVersion == null ? null : connotUpdateVersion.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }
}