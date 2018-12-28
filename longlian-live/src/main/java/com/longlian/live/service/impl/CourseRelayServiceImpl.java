package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.CourseRelayMapper;
import com.longlian.live.service.CourseRelayService;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.type.ReturnMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/7/11.
 */
@Service
public class CourseRelayServiceImpl implements CourseRelayService {

    @Autowired
    private CourseRelayMapper courseRelayMapper;
    @Autowired
    private CourseService courseService;
    @Autowired
    private LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    /**
     * 创建转播
     * @param appId
     * @param oriCourseId
     */
    @Override
    public ActResultDto createRelayCourse(long appId, String oriCourseId, String relayCharge, String relayScale) {
        ActResultDto actResultDto = new ActResultDto();

        //根据原始课程id查询，原始老师id
        Course course=courseService.getCourse(Long.valueOf(oriCourseId));

        //没有课程或者课程不支持转播
        if(course==null || course.getIsRelay()==0){
            actResultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            actResultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            return actResultDto;
        }
        /*//不能转播系列课
        if("1".equals(course.getIsSeriesCourse())){
            actResultDto.setCode(ReturnMessageType.CAN_NOT_RELAY_SERIES_COURSE.getCode());
            actResultDto.setMessage(ReturnMessageType.CAN_NOT_RELAY_SERIES_COURSE.getMessage());
            return actResultDto;
        }
        //不能转播语音课
        if("1".equals(course.getLiveWay())){
            actResultDto.setCode(ReturnMessageType.CAN_NOT_RELAY_VOICE_COURSE.getCode());
            actResultDto.setMessage(ReturnMessageType.CAN_NOT_RELAY_VOICE_COURSE.getMessage());
            return actResultDto;
        }*/
        //判断是否是自己的课
        if(course.getAppId()==appId) {
            actResultDto.setCode(ReturnMessageType.TEACHER_CAN_NOY_RELAY_COURSE.getCode());
            actResultDto.setMessage(ReturnMessageType.TEACHER_CAN_NOY_RELAY_COURSE.getMessage());
            return actResultDto;
        }
        //判断是否有自己的个人直播间
        LiveRoom liveRoom = liveRoomService.findByAppId(appId);
        if(liveRoom==null){
            actResultDto.setCode(ReturnMessageType.NO_LIVE_ROOM.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_LIVE_ROOM.getMessage());
            return actResultDto;
        }
       /* //查询最近的一条转播记录
        CourseRelayDto courseRelayDto = queryByCreateTime(appId);
        //只能有一节转播课为 没有直播结束
        if(courseRelayDto!=null && courseRelayDto.getEndTime()==null){
            actResultDto.setCode(ReturnMessageType.WAIT_RELAY_END.getCode());
            actResultDto.setMessage(ReturnMessageType.WAIT_RELAY_END.getMessage());
            return actResultDto;
        }*/
        /*//转播正在直播的课，判断是否有正在直播的课程，有则不让转播
        String endTime=null;
        String startTime=null;
        if(course.getEndTime()!=null){
            endTime=DateUtil.format(course.getEndTime());
        }
        if(course.getStartTime()!=null){
            startTime=DateUtil.format(course.getStartTime());
        }

        if("2".equals(DateUtil.getStatusStr(startTime, endTime))){
            List<CourseRelayDto> courseRelay = queryByPlayingCourse(appId);
            if(courseRelay!=null && courseRelay.size()>0){
                actResultDto.setCode(ReturnMessageType.PALYING_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.PALYING_COURSE.getMessage());
                return actResultDto;
            }
        }*/
        //判断是否添加过
        if(isTransmitted(appId,oriCourseId)){
            actResultDto.setCode(ReturnMessageType.RELAY_COURSE_TRANSMITTED.getCode());
            actResultDto.setMessage(ReturnMessageType.RELAY_COURSE_TRANSMITTED.getMessage());
            return actResultDto;
        }

        //判断转播的是否是系列课
        if("1".equals(course.getIsSeriesCourse())){
            //将系列课下所有单节课添加到转播表
            long relaySeriesId = courseRelayMapper.createRelaySeriesCourse(Long.valueOf(oriCourseId),appId, liveRoom.getId());
            courseRelayMapper.createRelaySeriesSingleCourse(Long.valueOf(oriCourseId),appId, liveRoom.getId());
            redisUtil.del(RedisKey.series_course_count + liveRoom.getId());
            return actResultDto;
        }

        if(relayScale==null || "".equals(relayScale)){
            relayScale="0";
        }
        if(relayCharge==null || "".equals(relayCharge)){
            relayCharge="0";
        }
        CourseRelayDto courseRelayDto = new CourseRelayDto();
        courseRelayDto.setOriCourseId(Long.valueOf(oriCourseId));
        courseRelayDto.setAppId(appId);
        courseRelayDto.setRelayCharge(new BigDecimal(relayCharge));
        courseRelayDto.setRelayScale(Float.valueOf(relayScale));
        courseRelayDto.setOriAppId(course.getAppId());
        courseRelayDto.setRoomId(liveRoom.getId());
        courseRelayDto.setIsSeriesCourse("0");
        courseRelayDto.setSeriesCourseId(0L);
        if("1".equals(course.getLiveWay())){  //语音课用原来的chatroomid
            courseRelayDto.setChatRoomId(course.getChatRoomId());
        }

        //创建转播
        courseRelayMapper.createRelayCourse(courseRelayDto);
        redisUtil.del(RedisKey.teacher_course_count + appId);
        return actResultDto;
    }

    /**
     * 正在直播的课程
     * @param appId
     * @return
     */
    public List<CourseRelayDto> queryByPlayingCourse(long appId) {
        return courseRelayMapper.queryByPlayingCourse(appId);
    }

    /**
     * 给转播人新增记录
     * @param course
     */
    @Override
    public void insertSeriesSingleCourse(CourseDto course) {
        courseRelayMapper.insertSeriesSingleCourse(course);
    }

     /* 创建转播课
     * @param courseRelayDto
     * @return
     */
    @Override
    public ActResultDto createCourseRelay(CourseRelayDto courseRelayDto) {
        ActResultDto act = new ActResultDto();
        courseRelayMapper.createRelayCourse(courseRelayDto);
        return act;
    }

    @Override
    public List<CourseRelayDto> getRelayCourse(CourseRelayDto courseRelayDto) throws Exception{
        return courseRelayMapper.getRelayCourse(courseRelayDto);
    }

    @Override
    public boolean hasRelayIncome(long courseId) {
        double cnt = this.courseRelayMapper.queryRelayIncomeCntByOriCourse(courseId);
        return cnt>0;
    }

    @Override
    public boolean hasRelayCoursePayIncome(long courseId) {
        int cnt = this.courseRelayMapper.queryProfitIncomeCntByOriCourse(courseId);
        return cnt>0 ;
    }

    @Override
    public void deleteRelayCourseByOriCourse(long id) {
        /*删除单节课的转播课*/
        this.courseRelayMapper.deleteRelayCourse(id);
        /*删除系列课的转播课*/
        this.courseRelayMapper.deleteSeriesRelayCourse(id);
    }

    /**
     * 删除转播课
     * @param id
     */
    @Override
    public void deleteRelayCourseById(Long id) {
        this.courseRelayMapper.deleteRelayCourseById(id);
    }

    /**
     * 查看是否有人买过此转播课，并且原课程不是免费课
     * @param courseId
     * @return
     */
    @Override
    public boolean hasRelayCoursePayIncomeById(Long courseId) {
        int cnt = this.courseRelayMapper.hasRelayCoursePayIncomeById(courseId);
        return cnt>0 ;
    }

    /**
     * 查询最近的一条转播记录
     * @param appId
     * @return
     */
    @Override
    public CourseRelayDto queryByCreateTime(long appId) {
        return courseRelayMapper.queryByCreateTime(appId);
    }

    /**
     * 判断是否添加过转播
     * @param appId
     * @param oriCourseId
     * @return
     */
    @Override
    public Boolean isTransmitted(long appId,String oriCourseId){
        CourseRelayDto courseRelayDto = queryByAppidAndOriCourseId(appId,oriCourseId);
        if(courseRelayDto!=null){
            return true;
        }
        return false;
    }

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    @Override
    public CourseRelayDto queryById(long id) {
        return courseRelayMapper.queryById(id);
    }

    /**
     * 获取转播课
     *
     * @param courseId
     * @return
     */
    @Override
    public CourseRelayDto getCourseRelayFromRedis(Long courseId) {
        String key = RedisKey.ll_course + courseId;
        String CourseRelayStr = redisUtil.get(key);
        //没找到
        if (org.apache.commons.lang3.StringUtils.isEmpty(CourseRelayStr) || "null".equals(CourseRelayStr)) {
            CourseRelayDto CourseRelayDto = this.queryById(courseId);
            //缓存三天
            redisUtil.setex(key, 3 * 24 * 60 * 60, JsonUtil.toJson(CourseRelayDto));
            return CourseRelayDto;
        } else {
            return JsonUtil.getObject(CourseRelayStr, CourseRelayDto.class);
        }
    }

    /**
     * 通过appid和原课程id查询
     * @param appId
     * @param oriCourseId
     * @return
     */
    @Override
    public CourseRelayDto queryByAppidAndOriCourseId(long appId,String oriCourseId){
        CourseRelayDto courseRelayDto=courseRelayMapper.queryByAppidAndOriCourseId(appId,oriCourseId);
        if (courseRelayDto != null) {
            courseRelayDto.setLiveStatus(DateUtil.getStatusStr(String.valueOf(courseRelayDto.getStartTime()), String.valueOf(courseRelayDto.getEndTime())));
        }
        return courseRelayDto;
    }

	@Override
	public ActResultDto myRelayCourseDown(Long relayCourseId) {
		ActResultDto ac = new ActResultDto();
		//判断该课程是否有收益，有则不允许下架
        if(hasRelayCoursePayIncomeById(relayCourseId)){
        	ac.setCode(ReturnMessageType.COURSE_HAVE_PAY.getCode());
        	ac.setMessage(ReturnMessageType.COURSE_HAVE_PAY.getMessage());
            return ac;
        }
        //删除转播课
        deleteRelayCourseById(relayCourseId);
        return ac;
	}
}
