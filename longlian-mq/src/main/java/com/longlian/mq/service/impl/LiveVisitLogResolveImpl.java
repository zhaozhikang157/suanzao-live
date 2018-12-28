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
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuhan on 2017-11-07.
 * live日志解析
 *
 */
@Service("liveVisitLogResolve")
public class LiveVisitLogResolveImpl implements ResolveVisitLogService {
    private static Logger log = LoggerFactory.getLogger(LiveVisitLogResolveImpl.class);


    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public CdnVisit getLiveCdvisit(String s) {
        if (StringUtils.isEmpty(s)) return null;
        Matcher m = LogDealUtil.pattern.matcher(s);
        if (m.find()) {
            String[] r = new String[m.groupCount()/2];
            for (int i = 0 ;i <m.groupCount() ;i++) {
                if (i%2 == 1) {
                    r[i/2] = m.group(i);
                }
            }
            String url = r[5];
            if (!url.endsWith(".ts")) {
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
        return  "http://longlian-live.oss-cn-hangzhou.aliyuncs.com/oss-accesslog/longlian-live" + date + "-" + index + "-00-00-" + tail;
    }

    public static void main(String[] args) {
        String ss = "183.93.12.101 - - [08/Nov/2017:01:14:20 +0800] \"GET /record/894/2988/1507636047_1.ts HTTP/1.1\" 206 1868034 1126 \"https://suanzao.llkeji.com/weixin/index.user?courseId=2988\" \"Mozilla/5.0 (Linux; U; Android 7.1.1; zh-cn; 1607-A01 Build/NMF26F) AppleWebKit/533.1 (KHTML, like Gecko) Mobile Safari/533.1\" \"longlian-live.oss-cn-hangzhou.aliyuncs.com\" \"5A01E9EB3DD12491CCDFF972\" \"true\" \"-\" \"GetObject\" \"longlian-live\" \"record%2F894%2F2988%2F1507636047_1.ts\" 2221784 66 \"-\" 392 \"1872266391331581\" - \"-\" \"-\"";
        String ss2 = "106.14.2.198 - - [08/Nov/2017:18:04:05 +0800] \"PUT /dev/record/2017-11-08/139/5832/1510135445_5.ts HTTP/1.1\" 200 0 118 \"-\" \"aliyun-sdk-java/1.1.2(Linux/2.6.32-220.23.2.ali878.el6.x86_64/amd64;1.7.0_51)\" \"longlian-live2.oss-cn-shanghai.aliyuncs.com\" \"5A02D69560097C235954B83B\" \"true\" \"378841469027963223\" \"PutObject\" \"longlian-live2\" \"dev%2Frecord%2F2017-11-08%2F139%2F5832%2F1510135445_5.ts\" - 85 \"-\" 2849026 \"1872266391331581\" 2847824 \"-\" \"-\"";
        Matcher m = LogDealUtil.pattern.matcher(ss2);
        if (m.find()) {
            String[] result = new String[m.groupCount()/2];
            for (int i = 0 ;i <m.groupCount() ;i++) {
                if (i%2 == 1) {
                    result[i/2] =  m.group(i);
                }
            }
            for (int i = 0 ;i <result.length ;i++) {
                System.out.println(i + ":" + result[i]);

            }
            String[] courseInfo = LogDealUtil.split(result[5] , "/");
            String roomId = courseInfo[1];
            String courseId = courseInfo[2];
            System.out.println( roomId + ";" + courseId);
        }
    }
}
