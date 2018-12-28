package com.longlian.live.service;

import com.huaxin.util.JsonUtil;
import com.longlian.model.Avatar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SendMQServiceTest {
    private static Logger log = LoggerFactory.getLogger(SendMQServiceTest.class);

    @Resource(name="myAmqpTemplate")
    AmqpTemplate amqpTemplate;


    @Test
    public void testSend() throws Exception {
        for (long i = 0 ;i < 100 ;i++) {
            Avatar av = new Avatar();
            av.setPhoto("aaaa");
            av.setUserName("andafdas");
            av.setId(i);
            amqpTemplate.convertAndSend("hello" , JsonUtil.toJson(av));
        }
    }

}
