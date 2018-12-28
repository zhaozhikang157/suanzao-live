package com.longlian.mq.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.longlian.model.CdnVisit;
import com.longlian.model.Course;
import com.longlian.mq.service.ResolveVisitLogService;
import com.longlian.mq.util.LogDealUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuhan on 2017-11-07.
 * live日志解析
 */
@Service("cdnLogResolve")
public class CdnLogResolveImpl implements ResolveVisitLogService {
    private static Logger log = LoggerFactory.getLogger(CdnLogResolveImpl.class);
    private static Pattern pattern = Pattern.compile("(\\[[0-9a-zA-Z\\+-:/ ]+\\])(\\s)(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})(\\s)(\\S+|-)(\\s)(\\d+)(\\s\")(\\S+|-)(\"\\s\")(\\S+|-)(\\s)(\\S+|-)(\"\\s)(\\d+|-)(\\s)(\\d+|-)(\\s)(\\d+|-)(\\s)(\\S+|-)(\\s)");
    @Value("${live.domain:livedev.llkeji.com}")
    private String domain = "livedev.llkeji.com";

    //[7/Nov/2017:00:14:42 +0800] 121.57.253.181 - 0 "-" "GET rtmp://live.llkeji.com/1058/4010?auth_key=1510028081-0-0-b79aeca3236279dd37e4d42213b66d08" 200 364 436 HIT "" " "
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public CdnVisit getLiveCdvisit(String s) {
        if(StringUtils.isEmpty(s)) return null;
        Matcher m = pattern.matcher(s);
            if (m.find()) {
                String[] r = new String[m.groupCount()/2];
                for (int i = 0 ;i <m.groupCount() ;i++) {
                    if (i%2 == 1) {
                        r[i/2] = m.group(i);
                    }
                }
                String url = r[6];
                String[] courseInfo = this.getCourseAndRoomId(url);
                if(courseInfo == null) return null;
                //if (courseInfo.length > 3) {
                String roomId = courseInfo[0];
                String courseId = courseInfo[1];

                if ("".equals(roomId) || "".equals(courseId) || "-".equals(roomId) || "-".equals(courseId)) {
                    return null;
                }

                //不用判断，保证大部分数据能通过，即使有少部分转化失败，会抛异常
                //if (Utility.isNumber(roomId) && Utility.isNumber(courseId)) {
                CdnVisit cdnVisit = new CdnVisit();
            //是核心数据，一旦出错，会抛异常;
            cdnVisit.setRoomId(Long.valueOf(roomId));
            cdnVisit.setCourseId(Long.valueOf(courseId));
            cdnVisit.setResponseSize(Long.valueOf(r[9]));
            //不是核心数据，不怕出错
            try {
                String date = r[0].substring(1 , r[0].length() - 1);

                cdnVisit.setVisitDate(LogDealUtil.getDate(date));
                cdnVisit.setClientIp(r[1]);
                cdnVisit.setMethod(r[5]);
                cdnVisit.setUrl(url);
                cdnVisit.setResponseTime(Long.valueOf(r[3]));
                //cdnVisit.setContent(s);
                cdnVisit.setHttpCode(r[7]);
                cdnVisit.setIsReview("1");
                cdnVisit.setReferer(r[4]);
                cdnVisit.setRequestSize(Long.valueOf(r[8]));
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


    private String[] getCourseAndRoomId(String url) {
        if (url.endsWith(".ts")) {
            String[] courseInfo = LogDealUtil.split(url , "/");
            String roomId = courseInfo[courseInfo.length - 3];
            String courseId = courseInfo[courseInfo.length - 2];
            return new String[]{roomId , courseId};
        }
        String start = "rtmp://" + domain + "/";
        if (url.startsWith(start)) {
            url = url.substring(start.length());
            String[] courseInfo = LogDealUtil.split(url , "/");
            String roomId = courseInfo[0];
            String courseId = courseInfo[1];
            if (courseId.indexOf("?") >= 0 ) {
                courseId =  courseId.substring(0 , courseId.indexOf("?"));
            }
            return new String[]{roomId , courseId};
        }
        if (url.contains(".m3u8")) {
            url = url.substring(start.length());
            String[] courseInfo = LogDealUtil.split(url , "/");
            String roomId = courseInfo[0];
            String courseId = courseInfo[1].substring(0 , courseInfo[1].indexOf(".m3u8"));
            return new String[]{roomId , courseId};
        }
        return null;

    }

    @Override
    public String getUrl(String date, String index, String tail) {
        return  "";
    }

    public static void main(String[] args) {
        String ss = "[7/Nov/2017:00:14:42 +0800] 121.57.253.181 - 0 \"-\" \"GET rtmp://live.llkeji.com/1058/4010?auth_key=1510028081-0-0-b79aeca3236279dd37e4d42213b66d08\" 200 364 436 HIT \"\" \" \"";
        String url = "rtmp://live.llkeji.com/992/4012?";
        url = url.substring("rtmp://live.llkeji.com/".length());
        String[] courseInfo = LogDealUtil.split(url , "/");
        String roomId = courseInfo[0];

        String courseId = courseInfo[1];
        if (courseId.indexOf("?") >= 0 ) {
            courseId =  courseId.substring(0 , courseId.indexOf("?"));
        }
        System.out.println(url);
        System.out.println(courseId);

        Matcher m = pattern.matcher(ss);
        if (m.find()) {
            String[] result = new String[m.groupCount()/2];
            for (int i = 0 ;i <m.groupCount() ;i++) {
                if (i%2 == 1) {
                    result[i/2] = m.group(i);
                }
            }
            for (int i = 0 ;i <result.length ;i++) {
                System.out.println(i + ":" + result[i]);

            }
//            String[] courseInfo = LogDealUtil.split(result[5] , "/");
//            String roomId = courseInfo[1];
//            String courseId = courseInfo[2];
//            System.out.println( roomId + ";" + courseId);
        }
    }
}
