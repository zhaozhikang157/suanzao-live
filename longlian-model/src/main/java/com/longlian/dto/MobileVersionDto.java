package com.longlian.dto;

import com.longlian.model.MobileVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
public class MobileVersionDto extends MobileVersion{
    private static Logger log = LoggerFactory.getLogger(MobileVersionDto.class);

    private List versionTypes;//版本类型
    private List statuses; //版本状态
    private List isFoceUpdates; //是否强制升级
    private String type;//版本类型
    private Date time;//上下线时间

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        MobileVersionDto.log = log;
    }

    public List getVersionTypes() {
        return versionTypes;
    }

    public void setVersionTypes(List versionTypes) {
        this.versionTypes = versionTypes;
    }

    public List getStatuses() {
        return statuses;
    }

    public void setStatuses(List statuses) {
        this.statuses = statuses;
    }

    public List getIsFoceUpdates() {
        return isFoceUpdates;
    }

    public void setIsFoceUpdates(List isFoceUpdates) {
        this.isFoceUpdates = isFoceUpdates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}