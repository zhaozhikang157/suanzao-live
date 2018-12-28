package com.longlian.live.controller;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.*;
import com.longlian.live.service.impl.LiveEndServiceImpl;
import com.longlian.live.service.impl.QiNiuLiveChannelServiceImpl;
import com.longlian.live.util.aliyun.MixLiveUtil;
import com.longlian.live.util.yunxin.CheckSumBuilder;
import com.longlian.live.util.yunxin.YunXinUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.live.util.yunxin.YunxinChatUtil;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.model.LiveConnect;
import com.longlian.type.MsgType;
import com.longlian.type.YunxinCustomMsgType;
import io.swagger.annotations.ApiOperation;
import org.da.expressionj.model.ValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by liuhan on 2017-08-14.
 */
@Controller
@RequestMapping(value = "/")
public class LiveNotifyController {
    private static Logger log = LoggerFactory.getLogger(LiveNotifyController.class);

    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private LiveChannelService liveChannelService;
    @Autowired
    private LiveChannelService qiNiuliveChannelService;

    @Autowired
    private SendMsgService sendMsgService;

    @Autowired
    LiveConnectService liveConnectService;

    /**
     * aliyun直播流通知
     * @param request
     * @return
     */
    @RequestMapping(value = "live/liveNotify")
    @ResponseBody
    @ApiOperation(value = "aliyun直播流通知", httpMethod = "GET", notes = "aliyun直播流通知")
    public ActResultDto liveNotify(HttpServletRequest request ) throws Exception {
        String action = request.getParameter("action");//直播流断开 publish_done ,直播流愎复 publish
        String roomId = request.getParameter("appname");
        String courseId = request.getParameter("id");
        String time = request.getParameter("time");
        if (courseId == null ) {
            log.error("收到的courseId为空");
            return ActResultDto.success();
        }
        return liveChannelService.liveNotify(  courseId , action );
    }

    /***
     * {
     "hub": "<Hub>",//直播空间
     "streamId": "<Stream ID>",// 流ID，形如z1.test.stream1
     "stream": "<Stream>",// 流名
     "rate": <Rate>,// 介于0-1间的浮点数，表示该图像被识别为色情的概率值，概率越高、机器越肯定 0.9
     "time": <Timestamp>,
     "ts": "<TS>",// 对应的ts文件名
     "label": "<porn|sexy|normal>",
     "review": <Bool> // true表示需要人工审核，false为不需要
     }
     * @param request
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "live/liveQiNiuYelloNotify", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    @ApiOperation(value = "qiniu直播鉴黄", httpMethod = "POST", notes = "qiniu直播鉴黄")
    public ActResultDto liveQiNiuYelloNotify(HttpServletRequest request , @RequestBody Map map ) throws Exception {
        //直播空间
        String hub = (String)map.get("hub");
        //流名
        String courseId = (String)map.get("stream");
        //介于0-1间的浮点数，表示该图像被识别为色情的概率值，概率越高、机器越肯定 0.9
        Double rate = (Double) map.get("rate");
        if (courseId == null ) {
            log.error("收到的courseId为空");
            return ActResultDto.success();
        }
        //机器判定为黄图,禁掉
        if (rate >= 0.9) {
            Course course = courseService.getCourseFromRedis(Long.parseLong(courseId));
            if (course != null) {
                //禁播
                qiNiuliveChannelService.forbidLiveStream(null ,course );
                //发送app消息给相应的人
                sendMsgService.sendMsg(course.getAppId() , MsgType.YELLOW_RESULT_REMIND.getType() , course.getId() , MsgConst.replace( MsgConst.YELLOW_COMMENT_REMIND, course.getLiveTopic()), "");
            }
        }

        return ActResultDto.success();
    }

    /**
     * http://shuyoulin.imwork.net/live/liveQiNiuNotify
     * http://api.longlianwang.com/live/liveQiNiuNotify
     * http://suanzao.llkeji.com/live/liveQiNiuNotify
     * qiniu直播流通知
     * @param request
     * @return
     */
    @RequestMapping(value = "live/liveQiNiuNotify", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "qiniu直播流通知", httpMethod = "GET", notes = "qiniu直播流通知")
    public ActResultDto liveQiNiuNotify(HttpServletRequest request ) throws Exception {
        //在直播推流域名下发送的参数
        String action = request.getParameter("connection");//直播流断开 0 ,直播流愎复 1
        //取得课程ID
        String courseId = request.getParameter("title");
        if (courseId == null ) {
            log.error("收到的courseId为空");
            return ActResultDto.success();
        }
        return qiNiuliveChannelService.liveNotify(  courseId , action );
    }



}


