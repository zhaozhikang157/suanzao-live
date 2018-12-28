package com.longlian.mq.redis;

import com.huaxin.util.redis.RedisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lh on 2016/3/28.
 * 订阅消息 
 */
@Component("isRunPubInit")
public class ISRunPubInit implements   Runnable,InitializingBean {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ISRunPubSub isRunPubSub ;

    @Override
    public void afterPropertiesSet() throws Exception {
      ///new Thread(this).run();
    }
    @Override
    public void run() {

        redisUtil.subscribe(isRunPubSub, new String[] { "msg.mq.isAllClose" });
    }
}
