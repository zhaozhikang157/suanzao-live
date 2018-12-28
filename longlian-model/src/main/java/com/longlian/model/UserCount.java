package com.longlian.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2017/3/7.
 */
public class UserCount implements Serializable {
    public UserCount()
    {


    }
    private long id;// ID                   bigint not null auto_increment,
    private String countTime;//COUNT_TIME           统计时间
    private String machineType;//MACHINE_TYPE      终端类型
    private Integer type;//TYPE                      统计类型
    private BigDecimal count;// COUNT                  统计数
    private Long objectId; //OBJECT_ID               统计对象ID

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    private String objectValue;//OBJECT_VALUE           统计对象

    public long getId() {
        return id;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getCountTime() {
        return countTime;
    }

    public void setCountTime(String countTime) {
        this.countTime = countTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public UserCount( String countTime, String machineType, Integer type, BigDecimal count) {
        this.countTime = countTime;
        this.machineType = machineType;
        this.type = type;
        this.count = count;
    }
    public UserCount( String countTime, String machineType, Integer type, BigDecimal count ,Long objectId) {
        this.countTime = countTime;
        this.machineType = machineType;
        this.type = type;
        this.count = count;
        this.objectId = objectId;
    }
    public UserCount( String countTime, String machineType, Integer type, BigDecimal count ,String objectValue) {
        this.countTime = countTime;
        this.machineType = machineType;
        this.type = type;
        this.count = count;
        this.objectValue = objectValue;
    }
}
