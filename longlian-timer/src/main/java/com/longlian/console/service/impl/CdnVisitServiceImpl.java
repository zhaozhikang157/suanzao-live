package com.longlian.console.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.CdnVisitService;
import com.longlian.live.dao.CdnVisitMapper;
import com.longlian.model.CdnVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuhan on 2017-11-10.
 */
@Service("cdnVisitService")
public class CdnVisitServiceImpl implements CdnVisitService {
    private static Logger log = LoggerFactory.getLogger(CdnVisitServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CdnVisitMapper cdnVisitMapper;
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveCdn(CdnVisit visit, String roomKey, String courseKey , String date) {
        //放到缓存异步保存
        String courseId = visit.getCourseId() + "";
        redisUtil.lpush( RedisKey.ll_cdn_visit_wait2db , JsonUtil.toJson(visit));
        redisUtil.hinc(roomKey , visit.getRoomId() + "" , visit.getResponseSize());
        redisUtil.hinc(courseKey , courseId , visit.getResponseSize());


        if (visit.getVisitDate() != null ) {
            String time = String.valueOf(visit.getVisitDate().getTime());
            if (!redisUtil.hexists(RedisKey.ll_use_start_time + date , courseId)) {
                redisUtil.hset(RedisKey.ll_use_start_time + date , courseId , time);
            }
            redisUtil.hset(RedisKey.ll_use_end_time + date , courseId , time);
        }

    }

    @Override
    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveCdnVisit(CdnVisit visit) {
            cdnVisitMapper.insertCdnVisit(visit);
    }
}
