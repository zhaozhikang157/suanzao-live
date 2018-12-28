package com.longlian.live.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 导入数据
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class VisitRecordServiceTest {
    private static Logger log = LoggerFactory.getLogger(VisitRecordServiceTest.class);
    @Autowired
    VisitCourseRecordService visitCourseRecordService;


    @Test
    public void testVisit() throws Exception {
        for (int i = 0; i < 50; i++) {
            visitCourseRecordService.insertRecord(23l, 2810l, 0l, "0",  0l , "0" );
        }
    }

}
