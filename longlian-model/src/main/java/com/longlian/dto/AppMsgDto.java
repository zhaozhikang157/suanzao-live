package com.longlian.dto;


import com.longlian.model.AppMsg;

/**
 * Created by U on 2016/8/25.
 */
public class AppMsgDto extends AppMsg {
    private String createTimeStr;    //创建时间字符串
    private String readTimeStr;      //读取时间字符串

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getReadTimeStr() {
        return readTimeStr;
    }

    public void setReadTimeStr(String readTimeStr) {
        this.readTimeStr = readTimeStr;
    }
}
