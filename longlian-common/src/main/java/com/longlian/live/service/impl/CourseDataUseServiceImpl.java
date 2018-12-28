package com.longlian.live.service.impl;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.CourseDataUseMapper;
import com.longlian.live.service.CourseDataUseService;
import com.longlian.model.CourseDataUse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/11/9.
 */
@Service("courseDataUseService")
public class CourseDataUseServiceImpl implements CourseDataUseService {
    private static Logger log = LoggerFactory.getLogger(CourseDataUseServiceImpl.class);

    @Autowired
    CourseDataUseMapper courseDataUseMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public void insert(CourseDataUse courseDataUse) {
        courseDataUseMapper.insert(courseDataUse);
    }

    @Override
    public Set<String> findAllCourseId() {
        return courseDataUseMapper.findAllCourseId();
    }

    @Override
    public void updateUseSize(CourseDataUse courseDataUse) {
        courseDataUseMapper.updateUseSize(courseDataUse);
    }

    public void createOrUpdate(String redisKey, Set<String> list, String type, String date) {
        Map<String, String> map = redisUtil.hmgetAll(redisKey);
        Set<String> set = map.keySet();
        for (String str : set) {
            String size = map.get(str);
            Long courseId = Long.parseLong(str);
            CourseDataUse dataUse = new CourseDataUse();
            dataUse.setCourseId(courseId);
            String dateStartTime = redisUtil.hget(RedisKey.ll_use_start_time  + date ,str );
            String dateEndTime = redisUtil.hget(RedisKey.ll_use_end_time  + date ,str );

            dataUse.setUpdateTime(new Date());
            if("1".equals(type)){   //直播
                dataUse.setLiveDataUse(Long.parseLong(size));
                dataUse.setReviewUse(0);
            }else{
                dataUse.setLiveDataUse(0);  //回放
                dataUse.setReviewUse(Long.parseLong(size));
            }
            if (list.contains(str)) { //存在
                if (!StringUtils.isEmpty(dateEndTime)) {
                    dataUse.setEndUseTime(new Date(Long.parseLong(dateEndTime)));
                }
                this.updateUseSize(dataUse);
            } else {                    //不存在
                if (!StringUtils.isEmpty(dateStartTime)) {
                    dataUse.setStartUseTime(new Date(Long.parseLong(dateStartTime)));
                }
                if (!StringUtils.isEmpty(dateEndTime)) {
                    dataUse.setEndUseTime(new Date(Long.parseLong(dateEndTime)));
                }
                this.insert(dataUse);
            }
        }
    }
}
