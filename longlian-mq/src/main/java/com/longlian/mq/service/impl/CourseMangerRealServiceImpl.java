package com.longlian.mq.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.Gag;
import com.longlian.mq.dao.CourseManagerRealMapper;
import com.longlian.mq.process.CourseGagSetProcess;
import com.longlian.mq.service.CourseManagerRealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by admin on 2017/8/1.
 */
@Service("courseManagerRealService")
public class CourseMangerRealServiceImpl implements CourseManagerRealService {
    private static Logger log = LoggerFactory.getLogger(CourseMangerRealServiceImpl.class);

    @Autowired
    CourseManagerRealMapper courseManagerRealMapper;

    @Autowired
    RedisUtil redisUtil;



    @Override
    public void insertManagerReal(String userId, long courseId) {

        //如果在禁言里，去掉禁言
        Gag gag = new Gag();
        gag.setCourseId(courseId);
        gag.setUserId(Long.parseLong(userId));
        redisUtil.lpush(RedisKey.ll_del_user_gag_wait2db, JsonUtil.toJson(gag));

        courseManagerRealMapper.insertManagerReal(userId,courseId);
    }

    @Override
    public void delManagerRealById(Long courseId, Long userId) {
        courseManagerRealMapper.delManagerRealById(courseId,userId);
    }

    @Override
    public Set<String> findAllManagerRealByCourseId(Long courseId) {
        return courseManagerRealMapper.findAllManagerRealByCourseId(courseId);
    }
}
