package com.longlian.dto;

import com.longlian.model.SystemLog;

import java.util.Date;

/**
 * Created by pangchao on 2017/1/22.
 */
public class SystemLogDto extends SystemLog{
    private Date createTimeBegin;  //查询条件（开始时间）
    private Date createTimeEnd; //查询条件（结束时间）
    private String logTypeStr;          //日志类型显示
    private String tableTypeStr;            //表类型显示

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

    public String getLogTypeStr() {
        return logTypeStr;
    }

    public void setLogTypeStr(String logTypeStr) {
        this.logTypeStr = logTypeStr;
    }

    public String getTableTypeStr() {
        return tableTypeStr;
    }

    public void setTableTypeStr(String tableTypeStr) {
        this.tableTypeStr = tableTypeStr;
    }
}
