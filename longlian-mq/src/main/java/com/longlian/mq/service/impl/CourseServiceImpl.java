package com.longlian.mq.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;

import com.longlian.live.dao.JoinCourseRecordMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.Course;
import com.longlian.model.CourseImg;
import com.longlian.model.CourseWare;
import com.longlian.mq.dao.CourseImgMapper;
import com.longlian.mq.dao.CourseMapper;
import com.longlian.mq.dao.CourseRelayMapper;
import com.longlian.mq.dao.CourseWareMapper;
import com.longlian.mq.service.CourseService;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by pangchao on 2017/2/12.
 */
@Service("courseService")
public class CourseServiceImpl implements CourseService {
    private static Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseRelayMapper courseRelayMapper;
    @Autowired
    CourseImgMapper courseImgMapper;
    @Autowired
    JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    CourseWareMapper courseWareMapper;

    @Override
    public Course getCourse(Long courseId) {
        return courseMapper.getCourse(courseId);
    }

    @Override
    public Course getRelayCourse(Long courseId) {
        return courseMapper.getRelayCourse(courseId);
    }

    @Override
    public Course getCourseFromRedis(Long courseId) {
        String key = RedisKey.ll_course + courseId;
        String courseStr = redisUtil.get(key);
        //没找到
        if (org.apache.commons.lang3.StringUtils.isEmpty(courseStr) || "null".equals(courseStr)) {
            Course course = this.getCourse(courseId);
            //缓存三天
            redisUtil.setex(key , 3 * 24 * 60 * 60 , JsonUtil.toJson(course));
            return course;
        } else {
            return JsonUtil.getObject(courseStr , Course.class);
        }
    }
    public void addVisitCount(long courseId){
        courseMapper.addVisitCount(courseId);
    }

    @Override
    public void updateVisitCount(long courseId, long count) {
        courseMapper.updateVisitCount(courseId, count);
    }



    @Override
    public List<Course> getCourseBySeriesId(Long courseId) {
        return courseMapper.getCourseBySeriesId(courseId);
    }

    @Override
    public List<Course> findSeriesCourseBySeriesId(long id) {
        return courseMapper.findSeriesCourseBySeriesId(id);
    }

    /**
     * 更新系列课的时间
     *
     */
    @Override
    public void updateCourse(Course course) {
        courseMapper.update(course);
    }

    @Override
    public void updateCourseVerticalImage(Long courseId, String url) {
        courseMapper.updateCourseVerticalImage(courseId, url);
    }

    @Override
    public List<Course> getAllNoEndCourseByTeahcerId(long teacherId) {
        return courseMapper.getAllNoEndCourseByTeahcerId(teacherId);
    }

    @Override
    public void updateClassImg(String url, long imgId) {
        courseImgMapper.updateAddressById(url, imgId);
    }


    @Override
    public Course getCourseByVideoAddress(String md5) {
        return courseMapper.getCourseByVideoAddress(md5);
    }

    @Override
    public Set<Course> getStartCourse() {
        return courseMapper.getStartCourse();
    }

    @Override
    public Set<Course> getAllStartCourse() {
        return courseMapper.getAllStartCourse();
    }

    @Override
    public List<Course> findAllCourse(int offset,int pageSize) {
        return courseMapper.findAllCourse(offset,pageSize);
    }

    @Override
    public Boolean isHaveWare(long courseId) {
        String value = redisUtil.get(RedisKey.ll_course_ware + courseId);
        if(StringUtils.isNotEmpty(value)){
            if(JsonUtil.getList(value, CourseWare.class).size() > 0){
                return true;
            }
        }
        List<CourseWare> list = courseWareMapper.getCourseWare(courseId);
        if(list.size()>0){
            redisUtil.setex(RedisKey.ll_course_ware, 60 * 60 * 24 * 3, JsonUtil.toJson(list));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void updateCourseSort(long courseId, BigDecimal sort) {
        courseMapper.updateCourseSort(courseId , sort);
    }

    @Override
    public void updateRelayVisitCount(long courseId, long count) {
        courseRelayMapper.updateRelayVisitCount(courseId, count);
    }

    @Override
    public List<CourseImg> getCourseImgList(long courseId) {
        return courseImgMapper.getCourseImgList(courseId);
    }

    public void setCourseDown(Course course){
        courseMapper.closeSeries(course.getId());
        SystemLogUtil.saveSystemLog(LogType.system_colse_course.getType(), "0"
                , course.getAppId(), String.valueOf(course.getAppId())
                , String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被系统下架关闭");
    }
}
