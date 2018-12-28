package com.longlian.live.service;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.ChatRoomMsg;
import com.longlian.type.MsgType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class GenerateServiceTest {
    private static Logger log = LoggerFactory.getLogger(GenerateServiceTest.class);
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;


    @Test
    public void testGenerate() throws Exception {
        //liveRoomService.updateBatchLiveRoomNo();
        //deal();
//        Map map = new HashMap();
//        map.put("NotificationType", MsgType.SYS_OTHER.getTypeStr());
//        JPushLonglian.sendPushNotificationByUserId(  "222","1", map);

    }
//    public void deal() {
//        int index = 0 ;
//        ChatRoomMsg chatRoomMsg = new ChatRoomMsg();
//        chatRoomMsg.setMsgType("CUSTOM");
//        List<ChatRoomMsg> list =  chatRoomMsgService.getAllHistoryMsgPage(index , chatRoomMsg);
//        do {
//            for (ChatRoomMsg msg : list) {
//                String attach = msg.getAttach();
//                Map map2 = (Map) JsonUtil.getObject(attach, HashMap.class);
//
//                if (map2 != null) {
//                    Integer type = 0 ;
//                    try {
//                        type = (Integer) map2.get("type");
//                    } catch (Exception ex) {
//                        String temp = (String) map2.get("type");
//                        if (temp != null) {
//                            type = Integer.parseInt(temp);
//                        }
//                    }
//                    if (type !=0 ) {
//                        msg.setCustomMsgType(String.valueOf(type));
//                        //chatRoomMsgService.updateCoustomType(msg);
//                    }
//                }
//            }
//            index += list.size();
//            //list =  chatRoomMsgService.getAllHistoryMsgPage(index , chatRoomMsg);
//        } while (!list.isEmpty());
//
//    }
}
