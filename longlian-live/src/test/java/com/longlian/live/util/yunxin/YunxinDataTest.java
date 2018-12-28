package com.longlian.live.util.yunxin;

import com.longlian.live.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * 导入数据
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class YunxinDataTest {
    private static Logger log = LoggerFactory.getLogger(YunxinDataTest.class);
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseService courseService;
//    @Autowired
//    AppUserService appUserService;
//    @Autowired
//    CourseService courseService;


    //@Test
   // public void testImport() throws Exception {

        //导入,有就修改，没有就导入
//        List<AppUser> list = appUserService.getAllAppUser();
//        for (AppUser user : list) {
//           String token =  yunxinUserUtil.createUser(String.valueOf(user.getId() ), user.getName() , user.getPhoto());
//           if ("".equals(token)) {
//               yunxinUserUtil.updateUserInfo(String.valueOf(user.getId() ), user.getName() , user.getPhoto());
//               token = yunxinUserUtil.refreshToken(String.valueOf(user.getId() ));
//           }
//            appUserService.updateYunxinToken(user.getId() , token);
//        }
//        //清空聊天室表
//        //创建聊天室并设置
//        List<Course>  list2 = courseService.getAllCourse();
//        for (Course course : list2) {
//            Integer chartRoomId =  yunxinChatRoomUtil.createRoom(String.valueOf(course.getAppId() ), course.getLiveTopic());
//            course.setChatRoomId(Long.valueOf(chartRoomId));
//            courseService.updateCourseInfo(course);
//        }

  //  }
    //@Tespublic void testUpdate() throws Exception {
        //String accid = "4";
        //yunxinUserUtil.updateUserInfo(accid, "test", "http://dddd.com");
        //Map res  = yunxinUserUtil.getUserInfo(accid);
        //String name = (String)res.get("icon");
        //Assert.assertEquals("http://dddd.com" , name);}
//    @Test
//    public void testGet() throws Exception {
//        String accid = "24";
//
//        Map res  = yunxinUserUtil.getUserInfo(accid);
//
//
//    }

   // @Test
   // public void testAdd() throws Exception {
        //String accid = "4";
        //yunxinUserUtil.createUser("6" , "nu", "http://hhseal.imwork.net/images/logo1.jpg");

   // }

//   @Test
//   public void testChatRoomClose() throws Exception {
////        //String accid = "6";
////        //yunxinUserUtil.updateUserInfo(accid, "飞飞", "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=319449236,1530880033&fm=58");
////        //Map res  = yunxinUserUtil.getUserInfo(accid);
////        //String name = (String)resget("icon");
////        //Assert.assertEquals("http://dddd.com" , name);
//            //Boolean flag = yunxinChatRoomUtil.toggleCloseRoom("2003" , "7975346","true");
////        //Assert.assertEquals( true,    flag);
//      }
//
//    //    @Test
////    public void testGetChatRoomAddr() throws Exception {
////        String accid = "24";
////        //Map res  = yunxinUserUtil.getUserInfo(accid);
////        String[] args  = yunxinChatRoomUtil.getChatRoomAddress(accid,"7599455");
////        for (String s : args)
////            System.out.println(s);
////    }

    @Test
    public void addRobot() throws Exception {
       Map map = yunxinUserUtil.addRobot("9629774", new String[]{"71","72"});
       log.info("i:{}",map);
    }

    //    @Test
//    public void testGetChatRoomAddr() throws Exception {
//        String accid = "24";
//        //Map res  = yunxinUserUtil.getUserInfo(accid);
//        String[] args  = yunxinChatRoomUtil.getChatRoomAddress(accid,"7599455");
//        for (String s : args)
//            System.out.println(s);
//    }


//    @Test
//    public void testEndLive() throws Exception {
//        String accid = "24";
//        //Map res  = yunxinUserUtil.getUserInfo(accid);
//        //courseService.endLive(95l,28l);
//        //courseService.getCoursePushAddr(140l);
//        System.out.println(UUID.randomUUID().toString().replace("-",""));
//        System.out.println(UUID.randomUUID().toString().replace("-","").length());
//    }

}
