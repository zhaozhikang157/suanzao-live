package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseManagerRealService;
import com.longlian.mq.service.CourseManagerService;
import com.longlian.mq.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 添加课程场 控人员
 * Created by admin on 2017/8/1.
 */
@Service
public class CourseManagerRealProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(CourseManagerRealProcess.class);

    @Autowired
    CourseManagerService courseManagerService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private RedisUtil redisUtil;

    private int threadCount = 5;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseManagerRealService managerRealService;

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Course course = JsonUtil.getObject(msg, Course.class);
            String liveManagerRedis = redisUtil.get(RedisKey.ll_live_manager_appId + course.getAppId());
            List<Map> redisList = null;
            if(StringUtils.isNotEmpty(liveManagerRedis)){
                redisList = JsonUtil.getObject(liveManagerRedis, List.class);
            }else{
                redisList = courseManagerService.findManagereByTeacherId(course.getAppId());
                redisUtil.set(RedisKey.ll_live_manager_appId + course.getAppId(), JsonUtil.toJson(redisList));
            }
            for (Map map : redisList) {
                Map m = yunxinChatRoomUtil.setRole(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), String.valueOf(map.get("userId")), 1, "true");
                if(m != null){
                    managerRealService.insertManagerReal(String.valueOf(map.get("userId")), course.getId());
                    redisUtil.del(RedisKey.ll_course_manager_real + course.getId());
                }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_course_manager_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
