package com.longlian.live.redis;

import com.longlian.live.controller.VersionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by lh on 2016/3/28.
 * 消息处理类
 */
@Component("appVersionPubSub")
public class AppVersionPubSub extends JedisPubSub  {

    private static Logger log = LoggerFactory.getLogger(AppVersionPubSub.class);

    // 取得订阅的消息后的处理  
    public void onMessage(String channel, String message) {
      //后台有更新时，将标志修改为已 
        log.info("取得订阅的消息:" + message);
        setParam(  message);
    }  
    
    public void setParam(String message) {
        if ("android_version_change".equals(message)) {
            VersionController.android_must_update = null;
            VersionController.android_max_version = null;
        } else if ("ios_version_change".equals(message)) {
            VersionController.ios_must_update = null;
            VersionController.ios_max_version = null;
        }
    }
  
    // 初始化订阅时候的处理  
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("初始化订阅时候的处理  ");
    }  
  
    // 取消订阅时候的处理  
    public void onUnsubscribe(String channel, int subscribedChannels) {  
    }  
  
    // 初始化按表达式的方式订阅时候的处理  
    public void onPSubscribe(String pattern, int subscribedChannels) {  
    }  
  
    // 取消按表达式的方式订阅时候的处理  
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        log.info("取消按表达式的方式订阅时候的处理   ");
    }  
  
    // 取得按表达式的方式订阅的消息后的处理  
    public void onPMessage(String pattern, String channel, String message) {
        log.info("取得按表达式的方式订阅的消息:" + message);
        setParam(  message);
    }
}
