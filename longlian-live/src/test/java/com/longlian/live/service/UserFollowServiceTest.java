package com.longlian.live.service;

import com.huaxin.util.JsonUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 导入数据
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class UserFollowServiceTest {
    private static Logger log = LoggerFactory.getLogger(UserFollowServiceTest.class);
    @Autowired
    UserFollowService userFollowService;


    @Test
    public void testFollowLiveRoom() throws Exception {
//        List<Map> map = userFollowService.followLiveRoom(78 ,1  ,10 );
//        for (Map m : map) {
//            System.out.print(JsonUtil.toJson(m));
//        }
    }

}
