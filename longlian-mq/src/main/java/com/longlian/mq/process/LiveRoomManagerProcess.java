package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.model.CourseManager;
import com.longlian.mq.service.CourseManagerRealService;
import com.longlian.mq.service.CourseService;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/8/1.
 */
@Service
public class LiveRoomManagerProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(LiveRoomManagerProcess.class);

    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseService courseService;
    @Autowired
    CourseManagerRealService managerRealService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppUserCommonService appUserCommonService;

    private  int threadCount=10;


    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg1) throws Exception {
            CourseManager courseManager = JsonUtil.getObject(msg1,CourseManager.class);
            List<Course> courseList = courseService.getAllNoEndCourseByTeahcerId(courseManager.getTeacherId());
            for (Course course : courseList) {
                Map map = yunxinChatRoomUtil.setRole(String.valueOf(course.getChatRoomId()),
                        String.valueOf(course.getAppId()), String.valueOf(courseManager.getUserId()), 1, "false");
                if (map == null) {
                    continue;
                }
                Map msg = new HashMap();
                msg.put("type", YunxinCustomMsgType.CANCEL_MANAGER.getType());
                Map val = new HashMap();
                val.put("value", courseManager.getUserId());
                Map user = appUserCommonService.getUserInfoFromRedis(courseManager.getUserId());
                val.put("name", user.get("name"));
                msg.put("data", val);
                log.info(JsonUtil.toJson(msg));
                yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg));
                managerRealService.delManagerRealById(course.getId(), courseManager.getUserId());
                redisUtil.srem(RedisKey.ll_course_manager_real + course.getId(), String.valueOf(courseManager.getUserId()));
                getManagerList(course.getId());
                redisUtil.expire(RedisKey.ll_course_manager_real + course.getId(), 3 * 24 * 60 * 60);
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_live_room_manager);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    public void getManagerList(Long courseId){
        Set<String> redisValue = redisUtil.smembers(RedisKey.ll_course_manager_real + courseId);
        if(redisValue == null || redisValue.size()<1){
            Set<String> list = managerRealService.findAllManagerRealByCourseId(courseId);
            redisUtil.expire(RedisKey.ll_course_manager_real + courseId, 3 * 24 * 60 * 60);
            if(list.size()<1){
                redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, "0");
            }else{
                for(String userId : list){
                    redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
                }
            }
        }
    }
}
