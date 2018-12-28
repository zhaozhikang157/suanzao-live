package com.longlian.live.service;

import com.longlian.model.AppUser;
import com.longlian.model.Course;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by liuhan on 2017-05-22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CourseServiceTest {

    private static Logger log = LoggerFactory.getLogger(CourseServiceTest.class);

    @Autowired
    CourseService courseService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    AppUserService   appUserService;
    
    @Autowired
    WechatOfficialService wechatOfficialService;


//    /**
//     * 预放
//     * @throws Exception
//     */
//    @Test
//    public void testGetPrevueLive4Home() throws Exception {
//        List<Map> lists =  courseService.getPrevueLive4Home(0);
//        for (Map m : lists) {
//            log.info("{}", m );
//        }
//    }
//
//    /**
//     * 推建
//     * @throws Exception
//     */
//    @Test
//    public void testGetCommend4Home() throws Exception {
//        ActResultDto result = new ActResultDto();
//        DataGridPage page = new DataGridPage();
//        page.setOffset(0);
//        List<Map> lists =  courseService.getCommend4HomePage(page);
//        for (Map m : lists) {
//            log.info("{}", m );
//        }
//    }
//
//    /**
//     * 正在直播中的
//     * @throws Exception
//     */
//    @Test
//    public void testGetLiveing4Home() throws Exception {
//        List<Map> lists =  courseService.getLiveing4Home(0);
//        for (Map m : lists) {
//            log.info("{}", m );
//        }
//    }
//
//
//    /**
//     * search
//     * @throws Exception
//     */
//    @Test
//    public void testFindCoursebyName() throws Exception {
//        ActResultDto ard =  courseService.findCoursebyName("测试", 0,10);
//        for (Map m : (List<Map>)ard.getData()) {
//            log.info("{}", m );
//        }
//    }
//
//    /**
//     * 我购买的课
//     * @throws Exception
//     */
//    @Test
//    public void testGetMyBuyCourseList() throws Exception {
//        List<Map> lists  =  joinCourseRecordService.getMyBuyCourseList(23l, 1, 10);
//        for (Map m : lists) {
//            log.info("{}", m );
//        }
//    }
//
//    /**
//     * 取得课程列表说
//     * @throws Exception
//     */
    @Test
    public void testGetCourseListByAppId() throws Exception {
              Course Course = courseService.getCourse(6929l);
//        AppUser user= appUserService.getById(Course.getAppId());
//        wechatOfficialService.getFollowUserSendWechatTemplateMessageByCourse(Course);
        AppUser user= appUserService.getById(Course.getAppId());
       // wechatOfficialService.getFollowUserSendWechatTemplateMessageByCourse(Course);
    }

    /**
     * 取得课程列表
     * @throws Exception
     */
   /* @Test
    public void testgetSeriesCourseInfo() throws Exception {
        ActResultDto dto = courseService.getSeriesCourseInfo( 1466l ,  54l ,false,"0");
        log.info("{}", dto.getData() );

    }

     *//**
     * @throws Exception
     *//*
    @Test
    public void testClearScan() throws Exception {
         courseService.clearScreenByChatRoomId(4199l);
    }*/
}
