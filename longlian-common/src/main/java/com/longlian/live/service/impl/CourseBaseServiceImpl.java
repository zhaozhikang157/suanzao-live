package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.NumberUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.CourseBaseMapper;
import com.longlian.live.dao.JoinCourseRecordMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveChatRoom;
import com.longlian.model.Video;
import com.longlian.type.LogType;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by liuhan on 2017-05-24.
 */
@Service("courseBaseService")
public class CourseBaseServiceImpl implements CourseBaseService {

    private Logger logg= LoggerFactory.getLogger(CourseBaseServiceImpl.class);

    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    RedisLock redisLock;
    @Autowired
    JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    CourseAuditService courseAuditService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CountService countService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    LiveChatRoomService liveChatRoomService;

    /**
     * @param requestModel
     * @param map
     * @return
     */
    @Override
    public List<Map> getAuditListPage(DatagridRequestModel requestModel, Map map){
        List<Map> list = courseBaseMapper.getAuditListPage(requestModel, map);
        return list;
    }

    /**
     * @param requestModel
     * @param map
     * @return
     */
    @Override
    public List<Map> getAuditListNoPassPage(DatagridRequestModel requestModel,Map map){
        List<Map> list = courseBaseMapper.getAuditListNoPassPage(requestModel, map);
        return list;
    }

    @Override
    public void updateCreateTime(Long courseId, Date now) {
        courseBaseMapper.updateCreateTime(courseId, now);
    }


    @Override
    public void setLiveRoomInfo(Course course) {
        String str = "/" + course.getRoomId() + "/" + course.getId();
        if ("1".equals(course.getIsRecorded())) {
            course.setPushAddress(course.getVideoAddress());
            course.setHlsLiveAddress(course.getVideoAddress());
            course.setLiveAddress(course.getVideoAddress());
        } else {
            course.setPushAddress(str);
            course.setHlsLiveAddress(str);
            course.setLiveAddress(str);
        }
        setChatroomId(course, false);
        if (course.getRetryTime() == 0 ) {
            redisUtil.lpush(RedisKey.ll_course_manager_wait2db,JsonUtil.toJson(course));
        }
    }

    @Override
    public void setChatroomId(Course course , boolean originTask ) {
        boolean isRetry = false;
        //200毫秒轮讯一次 ， 2秒算超时
        boolean flag = redisLock.lock(RedisKey.ll_create_chatroom_lock + course.getId() , 200 * 1000, 5);
        //获取锁失败，
        if (!flag) {
            logg.info("获取锁{}失败，请稍等!", RedisKey.ll_create_chatroom_lock + course.getId());
            GlobalExceptionHandler.sendEmail("获取锁" + RedisKey.ll_create_chatroom_lock + course.getId() + "失败，请等侍!", "注意");
            //return ;不设置chatroom,如果不是后台任务，则需要后面保存到数据库
            if (!originTask) {
                course.setChatRoomId(null);
                courseBaseMapper.setLiveRoomInfo(course);
            } else {
                //如果是后台任务，不需要保存到数据库，会报错，因为没有设置项
                return ;
            }
        } else{
            try {
                String temp = redisUtil.get(RedisKey.ll_chat_room_temp + course.getId());
                //已经创建好
                if (!StringUtils.isEmpty(temp)) {
                    course.setChatRoomId(Long.parseLong(temp));
                } else{
                    Integer roomId = 0;
                    //先取redis预先存入的聊天室ID
                    String redisValue = redisUtil.get(RedisKey.live_chat_room + course.getRoomId());
                    if(StringUtils.isNotEmpty(redisValue)){
                        roomId = Integer.valueOf(redisValue);
                    }else{
                        //预先申请的聊天室ID失效.去数据库查
                        LiveChatRoom liveChatRoom = liveChatRoomService.findByLiveRoomId(course.getRoomId());
                        if(liveChatRoom!=null){
                            roomId = Integer.valueOf(liveChatRoom.getChatRoomId().toString());
                        }else{
                            roomId = yunxinChatRoomUtil.createRoom(String.valueOf(course.getAppId()), course.getLiveTopic() , course.getId());
                        }
                    }
                    if ( roomId == 0 && !originTask && course.getRetryTime() < 4) {

                        isRetry = true;
                    } else {
                        course.setChatRoomId(Long.valueOf(roomId));
                        //缓存60s
                        redisUtil.setex(RedisKey.ll_chat_room_temp +   course.getId() ,    60  , String.valueOf(roomId));
                    }
                }
                courseBaseMapper.setLiveRoomInfo(course);

                //说明聊天室账号申请下来了,这个时候需要添加
                if (course.getChatRoomId() != null && course.getChatRoomId() != 0) {
                    //添加默认虚拟用户
                    redisUtil.lpush(RedisKey.ll_add_virtual_user ,String.valueOf(course.getId()));
                    //创建视频
                    //如果是录播的课,设置一直处于连接状态
                    if ("1".equals(course.getIsRecorded())) {
                        Video video = new Video();
                        video.setCourseId(course.getId());
                        video.setVideoAddress(course.getVideoAddress());
                        video.setCreateTime(new Date());
                        video.setCreateUserId(course.getAppId());
                        video.setConvertStatus(0);
                        redisUtil.lpush(RedisKey.ll_create_video_wait2db , JsonUtil.toJson(video));
                    }
                    //取到redis中预先申请的聊天室ID时,需要把redis清空,然后向mq发信息,重新申请聊天室ID,
                    //并且修改live_chat_room中,把该聊天室修改为使用
                    redisUtil.del(RedisKey.live_chat_room + course.getRoomId());
                    Map map = new HashMap();
                    map.put("liveRoomId",course.getRoomId());
                    map.put("chatRoomId",course.getChatRoomId());
                    map.put("isUse","1");//已经使用
                    map.put("appId",course.getAppId());
                    redisUtil.lpush(RedisKey.live_chat_room_create,JsonUtil.toJson(map));
                }

                if (isRetry) {
                    logg.info("课程：{} ,ID:{},获取roomid失败，重试：{}" , course.getLiveTopic() , course.getId() , course.getRetryTime());
                    course.setRetryTime(course.getRetryTime() + 1);
                    redisUtil.lpush(RedisKey.ll_live_create_course_roominfo , JsonUtil.toJsonString(course));
                }
            }finally {
                redisLock.unlock(RedisKey.ll_create_chatroom_lock + course.getId());
            }
        }

    }

    @Override
    public void setRelayChatrooId(CourseRelayDto course,long oriCourseId){
        if("1".equals(course.getIsSeriesCourse())){
            List<CourseRelayDto> ls = courseBaseMapper.getCourseRelayBySeriesId(course.getId()) ;
            for(CourseRelayDto courseRelayDto : ls){
                if(!"1".equals(courseRelayDto.getLiveWay())){
                    setRelayChatroomId(courseRelayDto,false);
                }
            }
        }else{
            Course oriCourse = courseBaseMapper.getCourse(oriCourseId);
            if(!"1".equals(oriCourse.getLiveWay())){  //语音课用原来的chatRoomId
                setRelayChatroomId(course,false);
            }
        }
    }


    @Override
    public void setRelayChatroomId(CourseRelayDto course, boolean originTask) {
        boolean isRetry = false;
        //200毫秒轮讯一次 ， 2秒算超时
        boolean flag = redisLock.lock(RedisKey.ll_create_chatroom_lock + course.getId() , 200 * 1000, 5);
        //获取锁失败，
        if (!flag) {
            logg.info("获取锁{}失败，请稍等!", RedisKey.ll_create_chatroom_lock + course.getId());
            GlobalExceptionHandler.sendEmail("获取锁" + RedisKey.ll_create_chatroom_lock + course.getId() + "失败，请等侍!", "注意");
            //return ;不设置chatroom,如果不是后台任务，则需要后面保存到数据库
            if (!originTask) {
                course.setChatRoomId(null);
                courseBaseMapper.setRelayLiveRoomInfo(course);
            } else {
                //如果是后台任务，不需要保存到数据库，会报错，因为没有设置项
                return ;
            }
        } else{
            try {
                String temp = redisUtil.get(RedisKey.ll_chat_room_temp + course.getId());
                //已经创建好
                if (!StringUtils.isEmpty(temp)) {
                    course.setChatRoomId(Long.parseLong(temp));
                } else{
                    Integer roomId = 0;
                    //先取redis预先存入的聊天室ID
                    String redisValue = redisUtil.get(RedisKey.live_chat_room + course.getRoomId());
                    if(StringUtils.isNotEmpty(redisValue)){
                        roomId = Integer.valueOf(redisValue);
                    }else{
                        //预先申请的聊天室ID失效.去数据库查
                        LiveChatRoom liveChatRoom = liveChatRoomService.findByLiveRoomId(course.getRoomId());
                        if(liveChatRoom!=null){
                            roomId = Integer.valueOf(liveChatRoom.getChatRoomId().toString());
                        }else{
                            roomId = yunxinChatRoomUtil.createRoom(String.valueOf(course.getAppId()), course.getLiveTopic() , course.getId());
                        }
                    }
                    if ( roomId == 0 && !originTask && course.getRetryTime() < 4) {

                        isRetry = true;
                    } else {
                        course.setChatRoomId(Long.valueOf(roomId));
                        //缓存60s
                        redisUtil.setex(RedisKey.ll_chat_room_temp +   course.getId() , 60 , String.valueOf(roomId));
                    }
                }
                courseBaseMapper.setRelayLiveRoomInfo(course);

                //说明聊天室账号申请下来了,这个时候需要添加
                if (course.getChatRoomId() != null && course.getChatRoomId() != 0) {
                    //添加默认虚拟用户
                    redisUtil.lpush(RedisKey.ll_add_virtual_user ,String.valueOf(course.getId()));
                    //取到redis中预先申请的聊天室ID时,需要把redis清空,然后向mq发信息,重新申请聊天室ID,
                    //并且修改live_chat_room中,把该聊天室修改为使用
                    redisUtil.del(RedisKey.live_chat_room + course.getRoomId());
                    Map map = new HashMap();
                    map.put("liveRoomId",course.getRoomId());
                    map.put("chatRoomId",course.getChatRoomId());
                    map.put("isUse","1");//已经使用
                    map.put("appId",course.getAppId());
                    redisUtil.lpush(RedisKey.live_chat_room_create,JsonUtil.toJson(map));
                }

                if (isRetry) {
                    logg.info("课程：{} ,ID:{},获取roomid失败，重试：{}" , course.getLiveTopic() , course.getId() , course.getRetryTime());
                    course.setRetryTime(course.getRetryTime() + 1);
                    redisUtil.lpush(RedisKey.ll_live_create_course_roominfo , JsonUtil.toJsonString(course));
                }
            }finally {
                redisLock.unlock(RedisKey.ll_create_chatroom_lock + course.getId());
            }
        }
    }

    @Override
    public void updateIsInviteCode(Long courseId) {
        courseBaseMapper.updateIsInviteCode(courseId);
    }

    @Override
    public void updateVideoAddr(Course course) {
        courseBaseMapper.updateVideoAddr(course);
        String courseKey = RedisKey.ll_course + course.getId();
        redisUtil.del(courseKey);
    }

    @Override
    public void updeteAddress(long courseId, String convertAddress) {
        courseBaseMapper.updateAddress(courseId, convertAddress);
        String courseKey = RedisKey.ll_course + courseId;
        redisUtil.del(courseKey);
    }


    @Override
    public void updateStatus(long id, String status) {
        courseBaseMapper.updateStatus(id, status);
    }

    @Override
    public void updateStatusToRelay(long id, String s) {
        courseBaseMapper.updateStatusToRelay(id,s);
    }

    public Course getCourse(Long courseId) {
        return courseBaseMapper.getCourse(courseId);
    }
    public Course getRelayCourse(Long courseId) {
        return courseBaseMapper.getRelayCourse(courseId);
    }
    @Override
    public List<Course> getCourses(Integer i) {
        Date monthbeteen = DateUtil.getMonthAfter(new Date(),-i);
        if(monthbeteen!=null){
            String format = DateUtil.format(monthbeteen);
            return courseBaseMapper.getCourses(format);
        }
        return null;
    }

    @Override
    public Course getCourseFromRedis(Long courseId) {
        String key = RedisKey.ll_course + courseId;
        String courseStr = redisUtil.get(key);
        //没找到
        if (org.apache.commons.lang3.StringUtils.isEmpty(courseStr) || "null".equals(courseStr)) {
            Course course = null;
            if(courseId >= SystemCofigConst.RELAY_COURSE_ID_SIZE){
                course = this.getRelayCourse(courseId);
            }else{
                course = this.getCourse(courseId);
            }

            //缓存三天
            redisUtil.setex(key, 3 * 24 * 60 * 60, JsonUtil.toJson(course));
            return course;
        } else {
            return JsonUtil.getObject(courseStr, Course.class);
        }
    }

    @Override
    @UpdateSeriesCourseTime
    public void closeSeries(long courseId) {
        Course course = courseBaseMapper.getCourse(courseId);
        if (course != null) {
            if (course.getChargeAmt().compareTo(new BigDecimal(0)) == 0) {
                setCourseDown(course);
            }else{
                int num = joinCourseRecordMapper.getPayCourseNum(course.getId());
                if (num < 1) {
                    setCourseDown(course);
                }
            }
            UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
        }
    }

    public  void setCourseDown(Course course){
        courseBaseMapper.closeSeries(course.getId());
        SystemLogUtil.saveSystemLog(LogType.system_colse_course.getType(), "0"
                , course.getAppId(), String.valueOf(course.getAppId())
                , String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被系统下架关闭");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassStatus(long id) throws Exception{
        this.updateStatus(id, "0");
        courseAuditService.updateStatus(id, "" , "1");

        Course course = this.getCourse(id);
        UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());

        String isCreateCourse = redisUtil.get(RedisKey.ll_is_create_course + course.getId());
        if ("1".equals(isCreateCourse)) {
            Date startTime = course.getStartTime();
            Date now = new Date();
            if (startTime.getTime() < now.getTime()) {
                course.setStartTime(now);
                this.updateCreateTime(id, now);
            }

            countService.newCourseCount(id);

            CourseDto dto = new CourseDto();
            BeanUtils.copyProperties(dto, course);
            Map map = appUserCommonService.getUserInfoFromRedis(dto.getAppId());
            dto.setAppUserName((String)map.get("name"));

            wechatOfficialService.getSendWechatTemplateMessageSaveRedis(dto);
            redisUtil.del(RedisKey.ll_is_create_course + course.getId());
        }

        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
    }

    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param
     * @return
     */
    @Override
    public ActResultDto findCourseWeeklySelection() {
        ActResultDto actResultDto = new ActResultDto();//创建返回数据模型对象
        List<Map> weeklySelection = new ArrayList<Map>(4);
        Date date = new Date();//创建当前时间
        Date firstDayOfWeek = DateUtil.getMonthbeteen(new Date(), -3);
        Date lastDayOfWeek = DateUtil.getLastDayOfWeek(date);//获取当前日期所在周的最后一天的日期
        String formatFirstDayOfWeek = DateUtil.format(firstDayOfWeek);
        String formatLastDayOfWeek = DateUtil.format(lastDayOfWeek);
        List<Map> courseWeeklySelection1 = courseBaseMapper.findCourseWeeklySelection("1", formatFirstDayOfWeek, formatLastDayOfWeek);//每周精选单节课
        List<Map> courseWeeklySelection0 = courseBaseMapper.findCourseWeeklySelection("0", formatFirstDayOfWeek, formatLastDayOfWeek);//每周精选单节课
        int[] ints1 = NumberUtil.randomNumber(courseWeeklySelection1.size());
        int[] ints0 = NumberUtil.randomNumber(courseWeeklySelection0.size());
        weeklySelection.add(courseWeeklySelection1.get(ints1[0]));
        weeklySelection.add(courseWeeklySelection1.get(ints1[1]));
        weeklySelection.add(courseWeeklySelection0.get(ints0[0]));
        weeklySelection.add(courseWeeklySelection0.get(ints0[1]));
        if (weeklySelection != null && weeklySelection.size() == 4) {//确认数据数量完整
            for (Map map : weeklySelection) {//设置时间 今天 明天 或者几天后
                if(map.get("id")!=null){
                    redisUtil.sadd(RedisKey.course_live_weekly_selection_id,map.get("id").toString());
                }
                if (map.get("startTime") != null) {
                    String startTime = map.get("startTime").toString();
                    String s = DateUtil.transFormationStringDate(startTime);
                    map.put("startTimeStr", s);
                    String endTime = null;
                    if (map.get("endTime") != null) {
                        endTime = map.get("endTime").toString();
                    }
                    map.put("statusStr", DateUtil.getStatusStr(map.get("startTime").toString(), endTime));
                } else {
                    map.put("startTimeStr", "暂无");
                }
            }
            actResultDto.setData(weeklySelection);
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            actResultDto.setData(weeklySelection);
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return actResultDto;
    }
    @Override
    public Course selectCourseMsgByChatRoomId(long chatRoomId){
        return courseBaseMapper.selectCourseMsgByChatRoomId(chatRoomId);
    }

    @Override
    public List<Course> findChildCourse(long seriesId) {
        return courseBaseMapper.findChildCourse(seriesId);
    }

    @Override
    public void updateIsConnection(long courseId, String isConnection) {
        courseBaseMapper.updateIsConnection(courseId,isConnection);
    }

    @Override
    public void deal(Long seriesId) throws Exception {
        //查询所有的单节课
        List<Course> list = courseBaseMapper.getCourseBySeriesId(seriesId);
        Date startTime = null;
        Date endTime = null;
        //修改所有的时间为null
        if (list == null || list.size() == 0 ) {
        } else if (list.size() == 1) {
            startTime = list.get(0).getStartTime();
            endTime = list.get(0).getEndTime();
        } else {
            for (int i = 0 ;i < list.size() ;i++) {
                Course c = list.get(i);
                if (c.getEndTime() == null) {
                    startTime = c.getStartTime();
                    endTime = null;
                    break;
                }
                //最后一个的endTime != null
                if (i == list.size() - 1) {
                    startTime = c.getStartTime();
                    endTime = c.getEndTime();
                }
            }
        }
        //计算已结束 的课
        int count = 0 ;
        for (int i = 0 ;i < list.size() ;i++) {
            Course c = list.get(i);
            if (c.getEndTime() != null) {
                count++;
            }
        }
        Course course = this.getCourseFromRedis(seriesId);
        course.setStartTime(startTime);
        course.setEndTime(endTime);
        course.setUpdatedCount(list.size());
        course.setEndedCount(count);
        courseBaseMapper.update(course);
        String courseKey = RedisKey.ll_course + seriesId;
        redisUtil.del(courseKey);
    }

    /**
     * 临时添加 后期更改
     * @param appId
     * @return
     */
    @Override
    public String getConvert(Long appId){
        Map<Long,String> map = new HashMap<Long,String>();
        return map.get(appId);
    }

    /**
     * 获取视频码配置
     * @return
     */
    public List getVideoConverts(){
        List ls = courseBaseMapper.getVideoConverts();
        return ls ;
    }
}
