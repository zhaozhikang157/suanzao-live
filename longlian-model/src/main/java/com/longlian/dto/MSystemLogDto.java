package com.longlian.dto;


import com.longlian.model.MSystemLog;

import java.util.Date;
import java.util.List;

/**
 * Created by pangchao on 2016/5/20.
 */
public class MSystemLogDto extends MSystemLog {
    private Date createTimeBegin;  //起始时间
    private Date createTimeEnd;  //截至时间
    private List mSysLogTypes;  //日志类型集合
    private String typeName;//日志类型名称

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List getmSysLogTypes() {
        return mSysLogTypes;
    }

    public void setmSysLogTypes(List mSysLogTypes) {
        this.mSysLogTypes = mSysLogTypes;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    private String types;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}
