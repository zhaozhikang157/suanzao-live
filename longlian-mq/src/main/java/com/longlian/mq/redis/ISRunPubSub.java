package com.longlian.mq.redis;

import com.longlian.mq.process.LongLianProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by lh on 2016/3/28.
 * 消息处理类
 */
@Component("isRunPubSub")
public class ISRunPubSub extends JedisPubSub  {

    private static Logger log = LoggerFactory.getLogger(ISRunPubSub.class);
    @Autowired
    private ISRunPubInit isRunPubInit;

    // 取得订阅的消息后的处理  
    public void onMessage(String channel, String message) {
      //后台有更新时，将标志修改为已 
        log.info("取得订阅的消息:" + message);
        setIsRun(  message);
    }  
    
    public void setIsRun(String message) {
        //close
        if ("1".equals(message)) {
            log.info("收到关闭MQ消息:{}" , message);
            LongLianProcess.setIsAllColse(true);
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
        setIsRun(  message);
    }
}
