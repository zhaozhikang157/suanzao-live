package com.longlian.live.service;

import com.longlian.model.LiveChatRoom;

import java.util.List;

/**
 * Created by admin on 2017/12/20.
 */
public interface LiveChatRoomService {

    void insert(LiveChatRoom liveChatRoom);

    void update(Long chatRoomId);

    LiveChatRoom findByLiveRoomId(long roomId);

}
