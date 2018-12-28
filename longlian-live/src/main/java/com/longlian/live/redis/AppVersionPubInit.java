package com.longlian.live.redis;

import com.huaxin.util.redis.RedisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lh on 2016/3/28.
 * 订阅消息 
 */
@Component 
public class AppVersionPubInit implements   Runnable,InitializingBean {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppVersionPubSub appVersionPubSub ;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this).start();;
    }

    @Override
    public void run() {
        redisUtil.subscribe(appVersionPubSub, new String[] { "msg.app" });
    }
}
