package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/1/23.
 * 直播流断开或者连接,需要修改course是否连接的字段
 */
@Service
public class CourseIsConnectionProcess extends LongLianProcess{
    private static Logger log = LoggerFactory.getLogger(CourseIsConnectionProcess.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;

    public  int threadCount=10;

    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getMap4Json(msg);
            Long courseId = Long.valueOf((String)map.get("courseId"));
            String isConnection = map.get("isConnection") + "";
            Course course = courseBaseService.getCourseFromRedis(courseId);
            if(course != null){
                long seriesCourseId = course.getSeriesCourseId();
                if(seriesCourseId > 0){ //系列课下的单节课
                    courseBaseService.updateIsConnection(courseId,isConnection);//修改单节课的是否连接
                    List<Course> courseList = courseBaseService.findChildCourse(seriesCourseId);//查询该系列课下的单节课
                    String i = "0";
                    for(Course c : courseList){
                        if("1".equals(c.getIsConnection())){ //正在直播,则系列课字段也为直播
                            courseBaseService.updateIsConnection(seriesCourseId,"1");
                            i = "1";
                            break;
                        }
                    }
                    if("0".equals(i)){
                        courseBaseService.updateIsConnection(seriesCourseId,"0");
                    }
                }else{
                    //单节课
                    courseBaseService.updateIsConnection(courseId,isConnection);
                }
                if("0".equals(isConnection)){
                    setRedis(courseId);
                }
            }
        }
    }

    public void setRedis(long courseId){
        redisUtil.expire(RedisKey.ll_live_notify_cation + courseId, 20 * 60);           //判断直播流是否断开
        redisUtil.expire(RedisKey.ll_course_live_notify_cation + courseId, 20 * 60);   //直播流断开 创建了Timer
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.course_is_connection);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
