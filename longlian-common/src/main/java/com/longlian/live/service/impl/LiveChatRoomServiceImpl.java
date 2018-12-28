package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.LiveChatRoomMapper;
import com.longlian.live.service.LiveChatRoomService;
import com.longlian.model.LiveChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/12/20.
 */
@Service("liveChatRoomService")
public class LiveChatRoomServiceImpl implements LiveChatRoomService {
    private static Logger log = LoggerFactory.getLogger(LiveChatRoomServiceImpl.class);

    @Autowired
    LiveChatRoomMapper liveChatRoomMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public void insert(LiveChatRoom liveChatRoom) {
        liveChatRoomMapper.insert(liveChatRoom);
        redisUtil.set(RedisKey.live_chat_room + liveChatRoom.getLiveRoomId(), liveChatRoom.getChatRoomId() + "");
        redisUtil.expire(RedisKey.live_chat_room + liveChatRoom.getLiveRoomId(), 3 * 24 * 60 * 60);
    }

    @Override
    public void update(Long chatRoomId) {
        liveChatRoomMapper.update(chatRoomId);
    }

    @Override
    public LiveChatRoom findByLiveRoomId(long roomId) {
        try {
            return liveChatRoomMapper.findByLiveRoomId(roomId);
        }catch (Exception e){
            log.error("查询聊天室ID roomId: {}"+ roomId);
            return null;
        }
    }
}
