package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseManagerRealService;
import com.longlian.mq.service.CourseManagerService;
import com.longlian.mq.service.CourseService;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.commons.lang3.StringUtils;
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
public class ManagerProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(ManagerProcess.class);

    private  int threadCount=10;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseService courseService;
    @Autowired
    CourseManagerService courseManagerService;
    @Autowired
    CourseManagerRealService managerRealService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    AppUserCommonService appUserCommonService;

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg1) throws Exception {
            Map map = JsonUtil.getObject(msg1,Map.class);
            Long teacherId = Long.valueOf(String.valueOf(map.get("teacherId")));
            Long userId = Long.valueOf(String.valueOf(map.get("userId")));
            List<Course> list = courseService.getAllNoEndCourseByTeahcerId(teacherId);
            for(Course course : list){
                getManagerList(course.getId());
                Boolean bo = isCourseManager(course.getId(), userId, "1");
                if(!bo){
                    Boolean b = insetReal(course.getChatRoomId(), teacherId, userId, course.getId());
                    if(b){
                        Map msg = new HashMap();
                        msg.put("type", YunxinCustomMsgType.ADD_MANAGER.getType());
                        Map val = new HashMap();
                        val.put("value",userId);
                        Map user = appUserCommonService.getUserInfoFromRedis(userId);
                        val.put("name", user.get("name"));
                        msg.put("data",val);
                        log.info(JsonUtil.toJson(msg));
                        yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()),
                                String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg));
                    }
                }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_live_room_create);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    public void getManagerList(Long courseId){
        redisUtil.srem(RedisKey.ll_course_manager_real + courseId, "0");
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

    public Boolean isCourseManager(Long courseId, Long userId, String type) {
        if(StringUtils.isEmpty(type)){
            Course course = courseService.getCourse(courseId);
            if(course == null){
                return false;
            }
            if("1".equals(course.getIsSeriesCourse())){
                List<Course> childCourse = courseService.findSeriesCourseBySeriesId(courseId);
                if(childCourse.size()>0){
                    courseId = childCourse.get(0).getId();
                }
            }
        }
        getManagerList(courseId);
        boolean exit = redisUtil.sismember(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
        return exit;
    }

    public Boolean insetReal(Long roomId,Long appId,Long userId,Long courseId){
        Map map = yunxinChatRoomUtil.setRole(String.valueOf(roomId), String.valueOf(appId), String.valueOf(userId),1,"true") ;
        if(map != null){
            managerRealService.insertManagerReal(String.valueOf(userId), courseId);
            getManagerList(courseId);
            redisUtil.srem(RedisKey.ll_course_manager_real + courseId, "0");
            redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
            redisUtil.expire(RedisKey.ll_course_manager_real + courseId, 3 * 24 * 60 * 60);
            return true;
        }else{
            return false;
        }
    }
}
