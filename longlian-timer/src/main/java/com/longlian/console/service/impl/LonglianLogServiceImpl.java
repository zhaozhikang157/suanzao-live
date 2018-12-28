package com.longlian.console.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.CdnVisitService;
import com.longlian.console.service.LonglianLogService;
import com.longlian.console.service.ResolveVisitLogService;
import com.longlian.console.util.LogDealUtil;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.dao.CdnVisitMapper;
import com.longlian.model.CdnVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhan on 2017-11-07.
 */
@Service("longlianLogService")
public class LonglianLogServiceImpl implements LonglianLogService {

    private static Logger log = LoggerFactory.getLogger(LonglianLogServiceImpl.class);

    @Autowired
    LonglianLogService longlianLogService;
    @Autowired
    CdnVisitMapper cdnVisitMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CdnVisitService cdnVisitService;

    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public void resolveAndSave(ResolveVisitLogService resolveDetailService) {
        String date = LogDealUtil.getBeferDay();
        String index = "";
        List<String> logs = new ArrayList<>();
        for (int i = 1; i < 24; i++) {
            if (i < 10) {
                index = "0" + i;
            } else {
                index = i + "";
            }
            logs.addAll(LogDealUtil.downLoadFromUrl(resolveDetailService.getUrl( date, index, "0001")));
        }
        //Map<Long , Long> map = new HashMap<>();
        for (String logStr : logs) {
            CdnVisit r = null;
            try {
                //log.info("{}" , logStr);
                  r = resolveDetailService.getLiveCdvisit(logStr);
            } catch (Exception ex) {
                log.info("解析流量日志时出错：{} " , logStr);
                log.error("解析流量日志时出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "解析流量日志时出错");
            }
            if (r == null) continue;
            //解决注解不生效问题
            try {
                cdnVisitService.saveCdn(r , RedisKey.ll_oss_log_live_room_id + date , RedisKey.ll_oss_log_course_id + date , date);
//                Long data = map.get(r.getRoomId());
//                if (data == null) {
//                    data = 0L;
//                }
//                data += r.getResponseSize();
//                map.put(r.getRoomId() , data);

            } catch (Exception ex) {
                log.info("统计流量时出错：{} " , JsonUtil.toJson(r));
                log.error("统计流量时出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "统计流量时出错");
            }
            r = null;
    }

//        for (Long l : map.keySet()) {
//            System.out.println(l + ":" + map.get(l) / (1024 * 1024 *1024) + ":" + map.get(l) );
//        }
    }

    public static void main(String[] args) {
        LonglianLogService impl = new LonglianLogServiceImpl();
        ResolveVisitLogService resolve = new LonglianOutputVisitLogResolveImpl();
        impl.resolveAndSave(resolve);
    }
}
