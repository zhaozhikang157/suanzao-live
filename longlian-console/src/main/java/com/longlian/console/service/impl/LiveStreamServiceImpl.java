package com.longlian.console.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.*;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.LiveStreamService;
import com.longlian.console.util.LiveRoomCourseOSSUtil;
import com.longlian.dto.CourseDto;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.dao.JoinCourseRecordMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.*;
import com.longlian.type.LogType;
import com.qiniu.pili.Client;
import com.qiniu.pili.Hub;
import com.qiniu.pili.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author liu.na
 */
@Service
public class LiveStreamServiceImpl implements LiveStreamService {
    private static Logger log = LoggerFactory.getLogger(LiveStreamServiceImpl.class);
    @Autowired
    private CourseService courseService;

    @Value("${qi-niu-live.hub_name}")
    private String hub_name;
    @Value("${qi-niu-live.accessKey}")
    private String accessKey;
    @Value("${qi-niu-live.secretKey}")
    private String secretKey;

    @Override
    public List<Map> getStreamList() {
        Client cli = new Client(accessKey, secretKey);
        Hub hub = cli.newHub(hub_name);
        List<Map> streamList = new ArrayList<>();
        try {
            Hub.ListRet listRest = hub.listLive("", 0, "");
            Object[] streamArray = listRest.keys;   // 获取正在直播的流信息  streamKeys;
            if (streamArray != null) {
                for (Object obj : streamArray) {
                    Stream streamA = hub.get(obj.toString());    //
                    String jsonStr = streamA.toJson();
                    System.out.print("-------------stream json" + streamA.toJson());
                    Course course = courseService.getCourse(Long.parseLong(obj.toString()));  //根据 流 streamKey 获取当前的课程信息；
                    Map<String, Object> dataMap = new HashMap<>();
                    if (course != null) {
                        dataMap.put("courseId", obj.toString());
                        dataMap.put("remark", course.getRemark());
                        dataMap.put("liveTopic", course.getLiveTopic());
                        dataMap.put("startTime", course.getStartTime());
                        dataMap.put("endTime", course.getEndTime());
                        streamList.add(dataMap);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return streamList;
    }

    /**
     * 禁播流
     * @param courseId
     * @return
     */
    @Override
    public ActResult disableStream(String courseId) {
        Client cli = new Client(accessKey,secretKey);
        Hub hub = cli.newHub(hub_name);
        ActResult actResult=new ActResult();
        try{
            Stream streamA = hub.get(courseId);
            streamA.disable();
            actResult.setSuccess(true);
            actResult.setMsg("成功");
            log.info("禁播流成功");
        }catch (Exception e){
            actResult.setSuccess(false);
            // e.printStackTrace();
            log.info("流不存在或者禁播流失败");
        }
        return actResult;
    }

    private static String printArrary(Object[] arr){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object a : arr){
            sb.append(a.toString()+" ");
        }
        sb.append("]");
        return sb.toString();
    }

}
