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
 * Created by admin on 2017/10/11.
 */
@Component("cdnLogTask")
public class CdnLogTask {
    private static Logger log = LoggerFactory.getLogger(CdnLogTask.class);

    @Autowired
    ResolveVisitLogService cdnLogResolve;
    @Autowired
    LonglianLogService cdnLogService;

    private static int dateLong = 3 * 24 * 60 * 60 ;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    DataUseService dataUseService;

    @Scheduled(cron = "0 5 0 * * ?")
    public void doJob() throws Exception {
        String date = LogDealUtil.getBeferDay();
        redisUtil.del(RedisKey.ll_cdn_log_live_room_id + date);
        redisUtil.del(RedisKey.ll_cdn_log_course_id + date);
        cdnLogService.resolveAndSave(cdnLogResolve);

        log.info("直播流量消耗统计开始:" , date);
        Set<String> set = redisUtil.hkeys(RedisKey.ll_cdn_log_live_room_id + date);
        for (String roomId : set) {
            String countStr = redisUtil.hget(RedisKey.ll_cdn_log_live_room_id + date, roomId);
            Long cdnFlow = 0l ;
            if (!StringUtils.isEmpty(countStr)) {
                cdnFlow = Long.parseLong(countStr);
            }
            liveRoomService.updateSize(Long.parseLong(roomId), cdnFlow);
            try {
                dataUseService.useData(Long.valueOf(roomId),cdnFlow);
            } catch (Exception ex) {
                log.info("消耗流量出错：roomId:{} , 消耗流量：{}" , roomId , cdnFlow);
                log.error("消耗流量出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "直播间" + roomId + "消耗流量出错");
            }
        }
        //设置3天过期
        redisUtil.expire(RedisKey.ll_cdn_log_live_room_id + date , dateLong);
        redisUtil.expire(RedisKey.ll_use_end_time  + date , dateLong);
        redisUtil.expire(RedisKey.ll_use_start_time  + date , dateLong);
        log.info("直播流量消耗统计结束:" , date);
    }
}
