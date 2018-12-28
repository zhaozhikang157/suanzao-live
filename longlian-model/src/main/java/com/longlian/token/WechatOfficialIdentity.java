package com.longlian.token;

import com.longlian.model.WechatOfficial;

/**
 * Created by liuhan on 2017-03-20.
 */
public class WechatOfficialIdentity extends WechatOfficial {
    private String token ;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
