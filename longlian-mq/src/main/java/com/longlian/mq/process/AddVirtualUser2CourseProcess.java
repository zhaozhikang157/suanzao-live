package com.longlian.mq.process;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.VirtualUserService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 自动添加虚拟用户到课程里面
 * Created by lh on 2016/10/20.
 */
@Service
public class AddVirtualUser2CourseProcess extends LongLianProcess {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    CourseService courseService;

    @Autowired
    VirtualUserService virtualUserService;
    @Autowired
    AppUserCommonService appUserCommonService;


    public  int threadCount=2;

    private Logger logg = LoggerFactory.getLogger(AddVirtualUser2CourseProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Long courseId = Long.parseLong(msg);
            Course course = courseService.getCourse(courseId);
            if (course != null) {
                BigDecimal bd = course.getChargeAmt();

                //系列课的价格来判断
                if (course.getSeriesCourseId() > 0) {
                    Course seriesCourse = courseService.getCourse(course.getSeriesCourseId());
                    bd = seriesCourse.getChargeAmt();
                }

                //价格为空，且等于零才添加虚拟用户
                if (bd == null || bd.compareTo(new BigDecimal(0)) == 0) {
                    appUserCommonService.loadVirtualUser2Redis();
                    String[] ids = appUserCommonService.getRandomCountUsers(10,40);
                    logg.info("课程：{},ID:{},需要添加{}个虚拟用户：{}" ,course.getLiveTopic() ,  course.getId() , ids.length , ids);
                    virtualUserService.addVirtualUser(course ,ids );
                }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_add_virtual_user);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
