package com.longlian.chatmsg.config;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.chatmsg.service.ChatRoomMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/1/12.
 */
@Component
public class ChatRoomMsgInit implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(ChatRoomMsgInit.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //chat_room_msg_id不存在,则去数据库查一下最大的ID
        if (!redisUtil.exists(RedisKey.chat_room_msg_max_id)) {
            long msgId  =chatRoomMsgService.findMaxMsgId();
            redisUtil.set(RedisKey.chat_room_msg_max_id , msgId + "");
        }

    }
}
