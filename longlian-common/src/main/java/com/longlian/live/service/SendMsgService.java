package com.longlian.live.service;

import com.longlian.model.AppMsg;

/**
 * Created by liuhan on 2017-02-25.
 */
public interface SendMsgService {
    public void sendMsg(AppMsg appMsg);

    public void sendMsg(long appId , Integer type , long table , String content , String cAct);
    public void sendMsg(long appId ,String openid, Integer type , long table , String content , String cAct);
}
