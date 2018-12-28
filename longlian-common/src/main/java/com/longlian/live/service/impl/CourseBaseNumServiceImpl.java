package com.longlian.live.service.impl;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.service.CourseBaseNumService;
import com.longlian.model.CourseBaseNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 课程基数
 * Created by liuhan on 2017-07-14.
 */
@Service("courseBaseNumService")
public class CourseBaseNumServiceImpl implements CourseBaseNumService {
    @Autowired
    CourseBaseNumMapper courseBaseNumMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public CourseBaseNum selectByCourse(Long courseId , String type) {
        return courseBaseNumMapper.selectByCourse(courseId , type);
    }

    public void updateBaseNum(Long id  , Long roomId, Long addCount) {
        CourseBaseNum courseBaseNum = courseBaseNumMapper.selectByCourse(id , "1");
        if (courseBaseNum == null) {
            courseBaseNum = new CourseBaseNum();
            courseBaseNum.setCount(addCount);
            courseBaseNum.setCourseId(id);
            courseBaseNum.setType("1");
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(roomId);
            courseBaseNumMapper.insert(courseBaseNum);
        } else {
            courseBaseNum.setCount(courseBaseNum.getCount() + addCount);
            courseBaseNum.setUpdateTime(new Date());
            courseBaseNum.setRoomId(roomId);
            courseBaseNumMapper.updateByPrimaryKey(courseBaseNum);
        }
        //访问基数,后台设置的
       // redisUtil.hset(RedisKey.ll_course_base_visit_num, String.valueOf(id), String.valueOf(courseBaseNum.getCount()));
    }
}
