package com.longlian.mq.task;

import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.service.DataChargeRecordService;
import com.longlian.live.service.DataUseService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.LiveRoom;
import com.longlian.mq.service.LonglianLogService;
import com.longlian.mq.service.ResolveVisitLogService;
import com.longlian.mq.util.LogDealUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 2017/10/23.
 */
@Component("ossLogTask")
public class OssLogTask {
    private static Logger log = LoggerFactory.getLogger(OssLogTask.class);
    //访问前缀
    @Value("${longlian.tsDir:dev}")
    private String env = "dev";

    @Autowired
    ResolveVisitLogService liveVisitLogResolve;

    @Autowired
    LonglianLogService longlianLogService;
    @Autowired
    ResolveVisitLogService live2VisitLogResolve;

    @Autowired
    ResolveVisitLogService longlianOutputVisitLogResolve;

    private static int dateLong = 3 * 24 * 60 * 60 ;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    DataUseService dataUseService;

    @Scheduled(cron = "0 7 0 * * ?")
    public void doJob() throws Exception {
        String date = LogDealUtil.getBeferDay();
        redisUtil.del(RedisKey.ll_oss_log_live_room_id + date);
        redisUtil.del(RedisKey.ll_oss_log_course_id + date);

        //longlianliveOSS上面的日志，开发环境目前已迁移至longlianlive2OSS
        if ("prod".equals(env)) {
            //解析日志，并保存到数据库
            longlianLogService.resolveAndSave(liveVisitLogResolve);
        }
        //longlianlive2OSS日志解析,
        longlianLogService.resolveAndSave(live2VisitLogResolve);
        //录播回看longlianOutputOss日志解析
        longlianLogService.resolveAndSave(longlianOutputVisitLogResolve);


        log.info("录播和回放流量消耗统计开始:" , date);
        Set<String> set = redisUtil.hkeys(RedisKey.ll_oss_log_live_room_id + date);
        for (String roomId : set) {
            String countStr = redisUtil.hget(RedisKey.ll_oss_log_live_room_id + date, roomId);
            Long cdnFlow = 0l ;
            if (!StringUtils.isEmpty(countStr)) {
                cdnFlow = Long.parseLong(countStr);
            }
            liveRoomService.updateReviewCount(Long.valueOf(roomId), cdnFlow);
            try {
                dataUseService.useData(Long.valueOf(roomId),cdnFlow);
            } catch (Exception ex) {
                log.info("消耗流量出错：roomId:{} , 消耗流量：{}" , roomId , cdnFlow);
                log.error("消耗流量出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "直播间" + roomId + "消耗流量出错");
            }
        }
        //设置3天过期
        redisUtil.expire(RedisKey.ll_oss_log_live_room_id + date , dateLong);
        redisUtil.expire(RedisKey.ll_use_end_time  + date , dateLong);
        redisUtil.expire(RedisKey.ll_use_start_time  + date , dateLong);
        log.info("录播和回放消耗统计结束:" , date);
    }
}
