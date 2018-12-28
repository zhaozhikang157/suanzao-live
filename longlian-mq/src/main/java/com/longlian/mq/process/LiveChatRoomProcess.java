package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.LiveChatRoomService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.LiveChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/12/20.
 *
 *
 */
@Service
public class LiveChatRoomProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(LiveChatRoomProcess.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    LiveChatRoomService liveChatRoomService;

    private int threadCount = 10;

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getMap4Json(msg);
            Long liveRoomId = Long.valueOf(map.get("liveRoomId").toString());
            String appId = map.get("appId").toString();
            String isUse = map.get("isUse").toString();
            String chatId = map.get("chatRoomId").toString();
            //代表创建直播间时预先申请
            if("0".equals(isUse)){
                creatrChatRoomId(appId,liveRoomId);             //创建直播间的时候发送mq
            }else{
                //使用过了,需要从新申请聊天室ID                      创建课程的时候发送的mq
                liveChatRoomService.update(Long.valueOf(chatId));
                creatrChatRoomId(appId, liveRoomId);
            }
        }
    }

    public void creatrChatRoomId(String appId , Long liveRoomId){
        Integer chatRoomId = yunxinChatRoomUtil.createRoom(appId, liveRoomId + "", 0l);
        if (chatRoomId != null && chatRoomId > 0) {
            LiveChatRoom liveChatRoom = new LiveChatRoom();
            liveChatRoom.setChatRoomId(Long.valueOf(chatRoomId));
            liveChatRoom.setCreateTime(new Date());
            liveChatRoom.setIsUse("0");
            liveChatRoom.setLiveRoomId(liveRoomId);
            liveChatRoomService.insert(liveChatRoom);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.live_chat_room_create);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
