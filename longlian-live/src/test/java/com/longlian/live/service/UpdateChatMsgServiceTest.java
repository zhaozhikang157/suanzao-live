package com.longlian.live.service;

import com.longlian.model.Course;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class UpdateChatMsgServiceTest {
    private static Logger log = LoggerFactory.getLogger(UpdateChatMsgServiceTest.class);

    @Autowired
    ChatRoomMsgService chatRoomMsgService;

    @Autowired
    CourseService courseService;


    @Test
    public void testGen() throws Exception {
       List<Course> list =   courseService.getAllCourse();

       for (Course c : list) {
           //chatRoomMsgService.updateCourseByChatRoomId(c.getChatRoomId() , c.getId() , c.getLiveTopic());
       }
    }

}
