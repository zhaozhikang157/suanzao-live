package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.util.yunXinAnti.TextQueryByTaskIds;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 聊天历史消息处理方法
 *
 * @author liuhan
 */
@Service
public class ChatRoomMsgProcess extends LongLianProcess {
    @Autowired
    private RedisUtil redisUtil;
    @Value("${chatRoomMsg.threadCount:10}")
    private int threadCount = 10;
    @Autowired
    private ChatRoomMsgService chatRoomMsgService;
    @Autowired
    private CourseService courseService;
    /**
     * 产品密钥ID，产品标识
     */
//    private final static String SECRETID = "ce8a2e16e04243d5ca7ae34d6dd50e0e";
    /**
     * 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
     */
//    private final static String SECRETKEY = "bf3d5b6956f07c0ca01d5453347966fc";
    /**
     * 业务ID，易盾根据产品业务特点分配
     */
//    private final static String BUSINESSID = "a4e93d02624d257b821621e9280903f2";
    /**
     * 易盾反垃圾云服务文本在线检测接口地址
     */
//    private final static String API_URL = "https://api.aq.163.com/v3/text/check";


    private Logger log = LoggerFactory.getLogger(ChatRoomMsgProcess.class);

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String json) throws Exception {
            Map map = (Map) JsonUtil.getObject(json, HashMap.class);
            ChatRoomMsg chatRoomMsg = new ChatRoomMsg().mapToValue(map);
            chatRoomMsgService.insertChatRoomMsg(chatRoomMsg);
        }
    }


    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_chat_room_msg_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    public static void main(String[] args) {
        String basePath = "d:/fdafdasf/ddafds.jar";
        basePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
        System.out.println(basePath);
    }
}