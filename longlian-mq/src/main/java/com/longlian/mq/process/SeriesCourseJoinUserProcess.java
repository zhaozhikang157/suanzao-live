package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lh on 2016/10/20.
 * 系列课下面的单节课处理加入问题
 */
@Service
public class SeriesCourseJoinUserProcess extends LongLianProcess {

    @Autowired
    private JoinCourseRecordService joinCourseRecordService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private RedisUtil redisUtil;
    public  int threadCount=10;

    private Logger logg = LoggerFactory.getLogger(SeriesCourseJoinUserProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map= JsonUtil.getObject(msg, HashMap.class);
            Long courseId = Long.parseUnsignedLong((String)map.get("courseId"));
            Long appId = Long.parseUnsignedLong((String)map.get("appId"));

            Course c = courseService.getCourseFromRedis(courseId);
            if ("1".equals(c.getIsSeriesCourse())) {
               List<Course> list = courseService.getCourseBySeriesId(courseId);
               for (Course course : list) {
                   if (appId != course.getAppId()) {
                       joinCourseRecordService.addJoinUser2Redis(course.getId() , appId, new Date());
                   }
               }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_series_course_join_user);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
