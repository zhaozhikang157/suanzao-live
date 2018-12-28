package com.longlian.live.service.impl;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.*;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseManagerService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.CourseManager;
import com.longlian.model.LiveRoom;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by admin on 2017/7/10.
 */
@Service("courseManagerService")
public class CourseManagerServiceImpl implements CourseManagerService {
    private static Logger log = LoggerFactory.getLogger(CourseManagerServiceImpl.class);

    @Autowired
    CourseManagerMapper courseManagerMapper;
    @Autowired
    LiveRoomMapper liveRoomMapper;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    CourseManagerRealMapper managerRealMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    RedisLock redisLock;

    /**
     * 查询更多管理员信息
     *
     * @param teacherId
     * @param pageSize
     * @param offset
     * @return
     */
    @Override
    public ActResultDto findAllManagersPage(Long teacherId, Integer pageSize, Integer offset) {
        ActResultDto resultDto = new ActResultDto();
        DataGridPage page = new DataGridPage();
        page.setPageSize(pageSize);
        page.setOffset(offset);
        List<Map> list = courseManagerMapper.findAllManagersPage(teacherId, page);
        if (list != null && list.size() > 0) {
            resultDto.setData(list);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 根据userId查询该用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public ActResultDto findAppUserById(Long userId, Integer pageSize,
                                        Integer offset, Long appId) {
        ActResultDto resultDto = new ActResultDto();
        DataGridPage page = new DataGridPage();
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (offset == null) offset = 0;
        page.setPageSize(pageSize);
        page.setOffset(offset);
        List<Map> map = courseManagerMapper.findAppUserByIdPage(userId, page, appId);
        String liveManagerRedis = redisUtil.get(RedisKey.ll_live_manager_appId + appId);
        List<Map> redisList = null;
        if (StringUtils.isNotEmpty(liveManagerRedis)) {
            redisList = JsonUtil.getObject(liveManagerRedis, List.class);
        } else {
            redisList = courseManagerMapper.findManagereByTeacherId(appId);
            redisUtil.set(RedisKey.ll_live_manager_appId + appId, JsonUtil.toJson(redisList));
        }
        for (Map dbMap : map) {
            String dbUserId = dbMap.get("userId").toString();
            for (Map redis : redisList) {
                String redisUserId = redis.get("userId").toString();
                if (dbUserId.equals(redisUserId)) {
                    dbMap.put("isManager", 1);
                    break;
                }
            }
        }
        if (map != null && map.size() > 0) {
            resultDto.setData(map);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        } else {
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }
    }

    /**
     * 创建管理员
     *
     * @param teacherId
     * @param userId
     * @return
     */
    @Override
    public ActResultDto createCourseManager(Long teacherId, Long userId) {
        ActResultDto resultDto = new ActResultDto();
        int i = courseManagerMapper.findManagerByTeacherIdAndUserId(teacherId, userId);
        AppUser appUser = appUserMapper.selectByPrimaryKey(userId);
        if (appUser == null) {
            resultDto.setCode(ReturnMessageType.NO_COURSE_MANAGER_INFO.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE_MANAGER_INFO.getMessage());
            return resultDto;
        }
        if (appUser.getStatus().equals("1")) {
            resultDto.setCode(ReturnMessageType.USER_NOT_USE.getCode());
            resultDto.setMessage(ReturnMessageType.USER_NOT_USE.getMessage());
            return resultDto;
        }
        if (i > 0) {
            resultDto.setCode(ReturnMessageType.ALREADY_EXIT_COURSE_MANAGER.getCode());
            resultDto.setMessage(ReturnMessageType.ALREADY_EXIT_COURSE_MANAGER.getMessage());
            return resultDto;
        }
        LiveRoom liveRoom = liveRoomMapper.findByAppId(teacherId);
        CourseManager courseManager = new CourseManager();
        if (liveRoom != null) {
            courseManager.setRoomId(liveRoom.getId());
        } else {
            resultDto.setCode(ReturnMessageType.CREATE_MANAGER_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.CREATE_MANAGER_ERROR.getMessage());
            return resultDto;
        }
        courseManager.setCreateTime(new Date());
        courseManager.setTeacherId(teacherId);
        courseManager.setUserId(userId);
        Map map = new HashMap<>();
        map.put("teacherId", teacherId);
        map.put("userId", userId);
        boolean flag = redisLock.lock(RedisKey.ll_room_manager_lock + teacherId + userId, 200 * 1000, 6);
        if (!flag) {
            //获取锁失败
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }
        try {
            if (!redisUtil.exists(RedisKey.ll_room_manager_lock_temp + teacherId + userId)) {
                //放入mq进行操作
                redisUtil.lpush(RedisKey.ll_live_room_create, JsonUtil.toJson(map));
                redisUtil.del(RedisKey.ll_live_manager_appId + teacherId);
                courseManagerMapper.creatrManager(courseManager);
                redisUtil.setex(RedisKey.ll_room_manager_lock_temp + teacherId + userId, 5 , teacherId + userId + "");
            }
        } finally {
            redisLock.unlock(RedisKey.ll_room_manager_lock + teacherId + userId);
        }
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 删除直播间的管理员
     *
     * @param id
     * @return
     */
    @Override
    public ActResultDto delCourseManagerById(Long id, Long teacherId) {
        ActResultDto resultDto = new ActResultDto();
        CourseManager courseManager = courseManagerMapper.findById(id);
        courseManagerMapper.delCourseManagerById(id);
        //放到mq中进行操作
        redisUtil.lpush(RedisKey.ll_live_room_manager, JsonUtil.toJson(courseManager));
        redisUtil.del(RedisKey.ll_live_manager_appId + teacherId);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 删除课程的管理人员
     *
     * @param courseId
     * @param userId
     * @return
     */
    @Override
    public ActResultDto delManagerRealById(Long courseId, Long userId, Long appId) {
        ActResultDto resultDto = new ActResultDto();
        boolean flag = redisLock.haslock(RedisKey.ll_del_course_manager_user + courseId + userId, 200 * 1000, 6);
        if(!flag){
            resultDto.setCode(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getCode());
            resultDto.setMessage(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getMessage());
            log.info("并发锁存在");
            return resultDto;
        }
        try {
            log.info("课程id："+courseId);
            Course course = courseMapper.getCourse(courseId);
            Map map = null;
            if (course != null) {
                log.info("id为："+courseId+" 的课程存在");
                map = yunxinChatRoomUtil.setRole(String.valueOf(course.getChatRoomId()), String.valueOf(appId), String.valueOf(userId), 1, "false");
            }
            if (map == null) {
                log.info("返回map为null");
                resultDto.setCode(ReturnMessageType.DEL_COURSE_MANAGER_REAL_ERROR.getCode());
                resultDto.setMessage(ReturnMessageType.DEL_COURSE_MANAGER_REAL_ERROR.getMessage());
                return resultDto;
            }
            managerRealMapper.delManagerRealById(courseId, userId);
            redisUtil.srem(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
            getManagerList(courseId);
            redisUtil.expire(RedisKey.ll_course_manager_real + courseId, 3 * 24 * 60 * 60);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }finally {
            redisLock.unlock(RedisKey.ll_del_course_manager_user + courseId + userId);
        }
    }
    @Autowired
    CourseBaseService courseBaseService;

    /**
     * 添加场控人员信息
     *
     * @param courseId
     * @param userId
     * @return
     */
    @Override
    public ActResultDto createManagerReal(Long courseId, Long userId, Long appId) {
        ActResultDto actResultDto = new ActResultDto();
        boolean flag = redisLock.haslock(RedisKey.ll_create_course_manager_user + courseId + userId, 200 * 1000, 6);
        if(!flag){
            actResultDto.setCode(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getCode());
            actResultDto.setMessage(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getMessage());
            return actResultDto;
        }
        try {
            int i = managerRealMapper.findManagerRealById(userId, courseId);
            if (i > 0) {
                actResultDto.setCode(ReturnMessageType.ALREADY_EXIT_COURSE_MANAGER.getCode());
                actResultDto.setMessage(ReturnMessageType.ALREADY_EXIT_COURSE_MANAGER.getMessage());
                return actResultDto;
            }
            Course course = courseBaseService.getCourseFromRedis(courseId);
            if (course != null) {
                actResultDto = insetReal(course.getChatRoomId(), appId, userId, courseId);
            } else {
                actResultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            }
            return actResultDto;
        }finally {
            redisLock.unlock(RedisKey.ll_create_course_manager_user + courseId + userId);
        }
    }

    public ActResultDto insetReal(Long roomId, Long appId, Long userId, Long courseId) {
        ActResultDto resultDto = new ActResultDto();
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        Long s = System.currentTimeMillis();
        Map map = yunxinChatRoomUtil.setRole(String.valueOf(roomId), String.valueOf(appId), String.valueOf(userId), 1, "true");
        Long s2 = System.currentTimeMillis();
        System.out.println("设置管理员1：" + (s2 - s));
        if (map == null) {
            resultDto.setCode(ReturnMessageType.CREATE_MANAGER_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.CREATE_MANAGER_ERROR.getMessage());
            return resultDto;
        }
        managerRealMapper.insertManagerReal(userId, courseId);
        Long s3 = System.currentTimeMillis();
        System.out.println("设置管理员2：" + (s3 - s2));
        getManagerList(courseId);
        Long s4 = System.currentTimeMillis();
        System.out.println("设置管理员3：" + (s4 - s3));
        redisUtil.srem(RedisKey.ll_course_manager_real + courseId, "0");
        redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
        redisUtil.expire(RedisKey.ll_course_manager_real + courseId, 3 * 24 * 60 * 60);
        Long s5 = System.currentTimeMillis();
        System.out.println("设置管理员4：" + (s5 - s4));
        return resultDto;
    }

    @Override
    public String findAllManagerRealByCourseId(Long courseId) {
        StringBuffer str = new StringBuffer();
        getManagerList(courseId);
        Set<String> redisValue = redisUtil.smembers(RedisKey.ll_course_manager_real + courseId);
        boolean exit = redisUtil.sismember(RedisKey.ll_course_manager_real + courseId, "0");
        if (exit) {
            return null;
        }
        for (String userId : redisValue) {
            str.append(userId);
            str.append(",");
        }
        if (str.length() > 0) {
            return str.substring(0, str.length() - 1);
        }
        return null;
    }

    public void getManagerList(Long courseId) {
        redisUtil.srem(RedisKey.ll_course_manager_real + courseId, "0");
        Set<String> redisValue = redisUtil.smembers(RedisKey.ll_course_manager_real + courseId);
        if (redisValue == null || redisValue.size() < 1) {
            Set<String> list = managerRealMapper.findAllManagerRealByCourseId(courseId);
            redisUtil.expire(RedisKey.ll_course_manager_real + courseId, 3 * 24 * 60 * 60);
            if (list.size() < 1) {
                redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, "0");
            } else {
                for (String userId : list) {
                    redisUtil.sadd(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
                }
            }
        }
    }

    /**
     * @param courseId
     * @param userId
     * @param type     空 : 代表是系列课,或者不确定是否是系列课  1:不是系列课
     * @return
     */
    @Override
    public Boolean isCourseManager(Long courseId, Long userId, String type) {
        if (StringUtils.isEmpty(type)) {
            Course course = courseMapper.getCourse(courseId);
            if (course == null) {
                return false;
            }
//            if ("1".equals(course.getIsSeriesCourse())) {
                int i = courseManagerMapper.findManagerByTeacherIdAndUserId(course.getAppId(),userId);
                if(i>0){
                    return true;
                }else{
                    return false;
                }
//            }
        }
        getManagerList(courseId);
        return redisUtil.sismember(RedisKey.ll_course_manager_real + courseId, String.valueOf(userId));
    }
}
