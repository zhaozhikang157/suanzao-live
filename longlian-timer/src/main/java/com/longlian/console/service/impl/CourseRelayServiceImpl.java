package com.longlian.console.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.CourseRelayMapper;
import com.longlian.console.service.CourseRelayService;
import com.longlian.console.service.CourseService;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/11.
 */
@Service
public class CourseRelayServiceImpl implements CourseRelayService {

    @Autowired
    private CourseRelayMapper courseRelayMapper;
    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;

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
    public List<CourseRelayDto> getRelayCourse(CourseRelayDto courseRelayDto) {
        return courseRelayMapper.getRelayCourse(courseRelayDto);
    }

    @Override
    public List<Long> getCourseIdsBySeries(long id) {
        return courseRelayMapper.getCourseIdsBySeries(id);
    }

    @Override
    public Map<Long, Long> getRelayCourseIds(long id) {
        Map<Long,Long> map = new HashMap<Long,Long>();
        List<Map<Long,Long>> ls = courseRelayMapper.getRelayCourseIds(id);
        if(ls != null && ls.size() > 0){
            for(int i=0; i<ls.size(); i++){
                Map<Long,Long> m = ls.get(i);
                long key = 0;
                long value = 0;
                for(Map.Entry<Long,Long> entry : m.entrySet()){
                    if("ORI_COURSE_ID".equals(entry.getKey())){
                        value = entry.getValue();
                    }else if("ID".equals(entry.getKey())){
                        key = entry.getValue();
                    }
                }
                map.put(key,value);
            }
        }
        return map;
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
}
