package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.SendMsgService;
import com.longlian.model.AppMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liuhan on 2017-02-25.
 */
@Service("sendMsgService")
public class SendMsgServiceImpl implements SendMsgService {
    private static Logger log = LoggerFactory.getLogger(SendMsgServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;
    @Override
    public void sendMsg(AppMsg appMsg) {
        redisUtil.lpush(RedisKey.ll_app_msg_wait2db , JsonUtil.toJsonString(appMsg));
    }

    @Override
    public void sendMsg(long appId, Integer type, long table, String content, String cAct) {
        sendMsg( appId,"" , type,  table,  content,  cAct);
    }

    @Override
    public void sendMsg(long appId,String openid ,Integer type, long table, String content, String cAct) {
        AppMsg appMsg = new AppMsg();
        appMsg.setAppId(appId);
        appMsg.setcAct(cAct);
        appMsg.setContent(content);
        appMsg.setCreateTime(new Date());
        appMsg.setStatus(0);
        appMsg.setTableId(table);
        appMsg.setType(type);
        appMsg.setOpenid(openid);
        this.sendMsg(appMsg);
    }
}

