package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.CourseTypeMapper;
import com.longlian.live.service.CourseTypeService;
import com.longlian.model.AdvertisingDisplay;
import com.longlian.model.CourseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/14.
 */
@Service("courseTypeService")
public class CourseTypeServiceImpl implements CourseTypeService {
    private static Logger log = LoggerFactory.getLogger(CourseTypeServiceImpl.class);
    @Autowired
    CourseTypeMapper courseTypeMapper;

    @Autowired
    RedisUtil redisUtil;


    /**
     * 从redis读取数据
     * @return
     */
    @Override
    public List getCourseType4Redis() {
        List<CourseType> list = null;
        Boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_course_type);//先取redis取
        if (isExistsKey) {
            list = getObjList();
            return list;
        }
        //没有从数据库中取值，且存入缓存
        list = courseTypeMapper.getCourseType();
        resetRedisData(list);
        return list;
    }

    /**
     * 将从redis取出的list 字符串 转对象
     *
     * @return
     */
    public List<CourseType> getObjList() {
        List<CourseType> list = new ArrayList<CourseType>();
        List<String> arg = redisUtil.lrangeall(RedisKey.ll_live_course_type);
        if (arg == null) return list;
        for (String temp : arg) {
            if (!Utility.isNullorEmpty(temp)) {
                CourseType courseType = JsonUtil.getObject(temp, CourseType.class);
                list.add(courseType);
            }
        }
        return list;
    }

    /**
     * 重新设置redis 数据
     */
    public void resetRedisData(List<CourseType> list) {
        boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_course_type);//先去redis取
        if (isExistsKey) redisUtil.del(RedisKey.ll_live_course_type);
        List<String> redisList = new ArrayList<String>();
        for (CourseType resource : list) {
            redisList.add(JsonUtil.toJson(resource));
        }
        redisUtil.rpushlist(RedisKey.ll_live_course_type, redisList);
    }

    /**
     * WHA 获取所有可用课程类型
     *
     * @return
     */
    public List<Map<String,String>> getCourseTypes() {
        boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_course_type_new);//先去redis取
        if (isExistsKey) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            List<String> arg = redisUtil.lrangeall(RedisKey.ll_live_course_type_new);
            if (arg == null||arg.size()==0) {
                List<Map<String, String>> courseTypes = courseTypeMapper.getCourseTypes();
                return courseTypes;
            }
            for (String temp : arg) {
                if (!Utility.isNullorEmpty(temp)) {
                    Map <String,String> map = JsonUtil.getObject(temp, Map.class);
                    list.add(map);
                }
            }
            return list;
        }else{
            List<Map<String, String>> courseTypes = courseTypeMapper.getCourseTypes();
            List<String> redisList = new ArrayList<String>();
            for (Map<String,String> resource : courseTypes) {
                redisList.add(JsonUtil.toJson(resource));
            }
            redisUtil.rpushlist(RedisKey.ll_live_course_type_new, redisList);
            return courseTypes;
        }
    }
}
