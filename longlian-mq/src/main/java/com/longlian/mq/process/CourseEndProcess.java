package com.longlian.mq.process;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.live.service.AppMsgService;
import com.longlian.live.service.CourseAvatarUserService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import com.longlian.type.OssBucket;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.dsig.SignatureMethod;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lh on 2016/10/20.
 */
@Service
public class CourseEndProcess extends LongLianProcess {
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    private AppMsgService appMsgService;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${courseEnd.threadCount:10}")
    private  int threadCount=10;
    @Autowired
    private LiveChannelService qiNiuliveChannelService;

    private static String env = "dev";

    @Value("${longlian2.tsDir:dev}")
    public void setEnv(String env) {
        CourseEndProcess.env = env;
    }

    private static Logger logg= LoggerFactory.getLogger(CourseEndProcess.class);
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseAvatarUserService courseAvatarUserService;

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_course_end_event_key);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }



        @Override
        public void process(String msg) throws Exception {
            logg.info("结束课程:{}" ,msg);
            Map map = JsonUtil.getMap4Json(msg);
            Course course = courseService.getCourseFromRedis(Long.parseLong((String)map.get("courseId")));

            //结束云信
            try{
                Map msg2 = new HashMap();
                //直播流断开 publish_done ,直播流愎复 publish
                msg2.put("type",  YunxinCustomMsgType.CHAT_ROOM_CLOSE.getType() );
                Map val = new HashMap();
                val.put("value", "聊天室关闭了");
                msg2.put("data", val);
                //给相关的聊天室人员发送一个直播流断开(恢复) 的消息
                logg.info(JsonUtil.toJson(msg2));
                yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg2));
                yunxinChatRoomUtil.toggleCloseRoom(String.valueOf(course.getAppId()), String.valueOf(course.getChatRoomId()), "false");
            } catch (Exception ex) {
                logg.error("结束云信报错",ex);
            }


            //如果是视频直播,且不是录播
            if ("0".equals(course.getLiveWay()) && !"1".equals(course.getIsRecorded())) {
                String domain = (String)map.get("domain");
                if (StringUtils.isEmpty(domain)) {
                    domain = "live.llkeji.com";
                }
                //生成回放
                qiNiuliveChannelService.saveRecord(course , domain ,env );
            }
            //删除譔课的所有虚拟用户
            courseAvatarUserService.deleteAll(course.getId());
            //删除课程课件页码
            redisUtil.del(RedisKey.ll_course_class_index + course.getId());
            //删除课程直播流信息
            redisUtil.del(RedisKey.ll_course_live_connected  + course.getId());
            String courseKey = RedisKey.ll_course + course.getId();
            redisUtil.del(courseKey);

            //删除房间
            qiNiuliveChannelService.delRoom(course.getId());

            //不设置状态了

            redisUtil.del(RedisKey.join_room_user + course.getId());

        }

    }



}
