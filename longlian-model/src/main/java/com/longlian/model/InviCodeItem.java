package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/8/29.
 */
public class InviCodeItem {
    private static Logger log = LoggerFactory.getLogger(InviCodeItem.class);

    private Long id;
    private Long inviCodeId;
    private String inviCode;
    private Date createTime;
    private Long useAppId;
    private Date useTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInviCodeId() {
        return inviCodeId;
    }

    public void setInviCodeId(Long inviCodeId) {
        this.inviCodeId = inviCodeId;
    }

    public String getInviCode() {
        return inviCode;
    }

    public void setInviCode(String inviCode) {
        this.inviCode = inviCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUseAppId() {
        return useAppId;
    }

    public void setUseAppId(Long useAppId) {
        this.useAppId = useAppId;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }
}
