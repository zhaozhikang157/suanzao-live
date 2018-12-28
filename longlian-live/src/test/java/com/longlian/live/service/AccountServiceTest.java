package com.longlian.live.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class AccountServiceTest {
    private static Logger log = LoggerFactory.getLogger(AccountServiceTest.class);

    @Autowired
    AccountService accountService;
    @Autowired
    CourseService courseService;


    @Test
    public void testNewTeachCourseReward() throws Exception {
        //Course course = courseService.getCourse(893l);
        //accountService.newTeachCourseReward(course);
    }

}
