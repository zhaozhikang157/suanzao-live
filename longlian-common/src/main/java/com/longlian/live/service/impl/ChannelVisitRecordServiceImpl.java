package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.ChannelVisitRecordMapper;
import com.longlian.live.service.ChannelVisitRecordService;
import com.longlian.model.ChannelVisitRecord;
import com.longlian.model.ShareChannel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-16.
 */
@Service("channelVisitRecordService")
public class ChannelVisitRecordServiceImpl implements ChannelVisitRecordService {
    @Autowired
    ChannelVisitRecordMapper channelVisitRecordMapper;

    @Autowired
    RedisUtil redisUtil;

    private void loadChannelVisitRecord2Redis(Long courseId) {
        String key = RedisKey.ll_channel_visit_record + courseId;
        if (redisUtil.exists(key)) {
            return ;
        }

        List<ChannelVisitRecord> list = channelVisitRecordMapper.selectByCourse(courseId);
        for (ChannelVisitRecord r : list) {
            redisUtil.sadd(key ,String.valueOf(r.getAppId()));
        }
        redisUtil.expire(key , 60 * 60 * 24 * 3);
    }
    @Override
    public void saveChannelVisitRecord(ChannelVisitRecord channelVisitRecord) {
        //如果有了，就不用新增加了
        loadChannelVisitRecord2Redis(channelVisitRecord.getCourseId());
        String key = RedisKey.ll_channel_visit_record + channelVisitRecord.getCourseId();
        //已经存在
        if (redisUtil.sadd(key ,String.valueOf(channelVisitRecord.getAppId())) == 1) {
            channelVisitRecordMapper.insert(channelVisitRecord);
        }
        redisUtil.expire(key , 60 * 60 * 24 * 3);
    }
  
}
