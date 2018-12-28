package com.longlian.appmsg.config;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.appmsg.service.AppMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/1/12.
 */
@Component
public class AppMsgInit implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(AppMsgInit.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppMsgService appMsgService;


    @Override
    public void afterPropertiesSet() throws Exception {
        //app_msg_id不存在,则去数据库查一下最大的ID
        if (!redisUtil.exists(RedisKey.app_msg_max_id)) {
            long maxId = appMsgService.findAppMsgMaxId();
            redisUtil.set(RedisKey.app_msg_max_id, maxId + "");
        }
    }
}
