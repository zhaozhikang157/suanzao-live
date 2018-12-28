package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.MsgCancelMapper;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.MsgCancelService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.MsgCancel;
import com.longlian.type.MsgType;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/12/5.
 *
 * 聊天室消息撤回
 */
@Service
public class MsgCancleProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(MsgCancleProcess.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    MsgCancelService msgCancelService;

    public int threadCount = 10;

    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            MsgCancel msgCancel= JsonUtil.getObject(msg, MsgCancel.class);
            chatRoomMsgService.updateMsgGarbage(msgCancel.getMsgClientId());
            msgCancelService.insertMsg(msgCancel);
            Map map = new HashMap();
            map.put("type", YunxinCustomMsgType.CHAT_MSG_CANCEL.getType());
            Map val = new HashMap();
            val.put("msgidClient",msgCancel.getMsgClientId());
            map.put("data", val);
            log.info(JsonUtil.toJson(map));
            yunxinChatRoomUtil.sendMsg(msgCancel.getChatRoomId()+"", msgCancel.getOptId()+"",
                    "100", JsonUtil.toJson(map));
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_msg_cancel);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }
}
