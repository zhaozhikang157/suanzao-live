package com.longlian.mq.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.CdnVisitMapper;
import com.longlian.model.CdnVisit;
import com.longlian.mq.service.ResolveVisitLogService;
import com.longlian.mq.util.LogDealUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuhan on 2017-11-07.
 * live日志解析
 */
@Service("live2VisitLogResolve")
public class Live2VisitLogResolveImpl implements ResolveVisitLogService {
    private static Logger log = LoggerFactory.getLogger(Live2VisitLogResolveImpl.class);
    @Value("${longlian2.tsDir:dev}")
    private String env = "dev";
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public CdnVisit getLiveCdvisit(String s) {
        if (StringUtils.isEmpty(s)) return null;
        Matcher m = LogDealUtil.pattern.matcher(s);
        String head = "/" + env +"/record";
        if (m.find()) {
            String[] r = new String[m.groupCount()/2];
            for (int i = 0 ;i <m.groupCount() ;i++) {
                if (i%2 == 1) {
                    r[i/2] = m.group(i);
                }
            }
            String url = r[5];
            if (!url.endsWith(".ts") || !url.contains(head)) {
                return null;
            }
            String[] courseInfo = LogDealUtil.split(url , "/");
            //if (courseInfo.length > 3) {
            String roomId = courseInfo[courseInfo.length - 3];
            String courseId = courseInfo[courseInfo.length - 2];

            if ("".equals(roomId) || "".equals(courseId) || "-".equals(roomId) || "-".equals(courseId)) {
                return null;
            }


            //不用判断，保证大部分数据能通过，即使有少部分转化失败，会抛异常
            //if (Utility.isNumber(roomId) && Utility.isNumber(courseId)) {
            CdnVisit cdnVisit = new CdnVisit();
            //是核心数据，一旦出错，会抛异常;
            cdnVisit.setRoomId(Long.valueOf(roomId));
            cdnVisit.setCourseId(Long.valueOf(courseId));
            cdnVisit.setResponseSize(Long.valueOf(r[8]));
            //不是核心数据，不怕出错
            try {
                cdnVisit.setVisitDate(LogDealUtil.getDate(r[3]));
                cdnVisit.setClientIp(r[0]);
                cdnVisit.setMethod(r[4]);
                cdnVisit.setUrl(url);
                cdnVisit.setResponseTime(Long.valueOf(r[9]));
                //cdnVisit.setContent(s);
                cdnVisit.setHttpCode(r[7]);
                cdnVisit.setIsReview("1");
                cdnVisit.setReferer(r[10]);
                cdnVisit.setRequestSize(Long.valueOf(r[22]));
            } catch (Exception ex) {
                log.info("解析流量日志时出错：{} " , s);
                log.error("解析流量日志时出错： " , ex);
            }
            return cdnVisit;
            // }
            //}
        }
        return null;
    }

    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public String getUrl(String date, String index, String tail) {
        return  "http://longlian-live2.oss-cn-shanghai.aliyuncs.com/oss-accesslog/longlian-live2" + date + "-" + index + "-00-00-" + tail;
    }

}
