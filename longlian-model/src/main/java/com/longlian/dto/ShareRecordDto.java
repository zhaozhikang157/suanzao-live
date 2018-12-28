package com.longlian.dto;

import com.longlian.model.ShareRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/6/23.
 */
public class ShareRecordDto extends ShareRecord {
    private String wechatShareType;
    private String imgUrl;
    private String systemType;
    private String openid;//微信openid

    public String getWechatShareType() {
        return wechatShareType;
    }

    public void setWechatShareType(String wechatShareType) {
        this.wechatShareType = wechatShareType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
