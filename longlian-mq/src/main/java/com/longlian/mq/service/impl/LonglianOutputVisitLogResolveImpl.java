package com.longlian.mq.service.impl;

import com.longlian.model.CdnVisit;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by liuhan on 2017-11-07.
 * live日志解析
 */
@Service("longlianOutputVisitLogResolve")
public class LonglianOutputVisitLogResolveImpl implements ResolveVisitLogService {
    private static Logger log = LoggerFactory.getLogger(LonglianOutputVisitLogResolveImpl.class);

    private static Map<String , String> courseCache = new HashMap<>();

    static {
        //courseCache.put("ad60fb44e48c490e8d69bde47d4d4319" , "149_5793");
    }


    //访问前缀
    @Value("${longlianoutput.tsDir:Act-m3u8-segment-test}")
    private String env = "Act-m3u8-segment-test";
    @Autowired
    CourseService courseService;

    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public CdnVisit getLiveCdvisit(String s) {
        if (StringUtils.isEmpty(s)) return null;
        Matcher m = LogDealUtil.pattern.matcher(s);
        String head = "/" + env;
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
            String[] courseAndRoomId = this.getCourseAndRoomId(courseInfo[courseInfo.length - 2]);

            //不用判断，保证大部分数据能通过，即使有少部分转化失败，会抛异常
            //if (Utility.isNumber(roomId) && Utility.isNumber(courseId)) {
            CdnVisit cdnVisit = new CdnVisit();
            //是核心数据，一旦出错，会抛异常;
            cdnVisit.setRoomId(Long.valueOf(courseAndRoomId[0]));
            cdnVisit.setCourseId(Long.valueOf(courseAndRoomId[1]));
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


    private String[] getCourseAndRoomId(String fileId) {
        String value = courseCache.get(fileId);
        if (value == null) {
            Course course = courseService.getCourseByVideoAddress(fileId);
            if (course != null) {
                value = course.getRoomId() +"_" + course.getId();
                //第一次
                courseCache.put(fileId , value );
            }
        }
        if (StringUtils.isEmpty(value)) {
            //避免二次查询到
            courseCache.put(fileId , "");
            return null;
        } else {
            String[] val = LogDealUtil.split(value , "_");
            return val;
        }

    }

    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public String getUrl(String date, String index, String tail) {
        return  "http://longlian-output.oss-cn-beijing.aliyuncs.com/oss-accesslog/longlian-output" + date + "-" + index + "-00-00-" + tail;
    }

}
