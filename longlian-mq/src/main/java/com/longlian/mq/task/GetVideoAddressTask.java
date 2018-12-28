package com.longlian.mq.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by admin on 2017/5/19.
 */
//@Component("GetVideoAddressTask")
public class GetVideoAddressTask  {
    //private static Logger log = LoggerFactory.getLogger(GetVideoAddressTask.class);

    //@Autowired
    //CourseService courseService;
    //@Autowired
   // CourseEndProcess courseEndProcess;
/*
    @Scheduled(cron = "0 0/30 * * * ?") //测试
    public void doJob() throws Exception {
        int index = 0 ;
        List<Course> list= courseService.GetVideoAddressPage(index);
        do {
            for (Course course : list) {
                courseEndProcess.deal(course,"livedev.llkeji.com");
            }
            index += list.size();
            list =  courseService.GetVideoAddressPage(index);
        } while (!list.isEmpty());

    }
    */
}
