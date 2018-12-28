package com.longlian.live.dao;

import com.longlian.model.LiveChatRoom;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by admin on 2017/12/20.
 */
@Mapper
public interface LiveChatRoomMapper {

    void insert(LiveChatRoom liveChatRoom);

    void update(Long chatRoomId);

    LiveChatRoom findByLiveRoomId(Long liveRoomId);
}
