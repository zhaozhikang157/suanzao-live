package com.longlian.console.task;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.dao.ChatRoomMsgMapper;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.util.yunXinAnti.TextQueryByTaskIds;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/10/11.
 * 定时器,处理云信消息抄送
 */
@Component("chatRoomMsgTask")
public class ChatRoomMsgTask extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(ChatRoomMsgTask.class);

    private final long time = 300000 ; //5分钟

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    CourseBaseService courseBaseService;

    @Override
    public String getTaskName() {
        return "处理云信消息抄送";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理云信消息抄送 异常："+e.getMessage());
        }
    }

    //@Scheduled(cron = "0 0/5 * * * ?")
    public void doJob() throws Exception {
        Set<String> set = redisUtil.keys(RedisKey.chat_room_msg_task + "*");
        for(String s : set){
            String redisVal = redisUtil.get(s);
            if(StringUtils.isNotEmpty(redisVal)){
                long expirTime = redisUtil.ttl(s);
                if(expirTime > time ){
                    Map map = (Map) JsonUtil.getObject(redisVal, HashMap.class);
                    ChatRoomMsg chatRoomMsg = new ChatRoomMsg().mapToValue(map);
                    Course course =  courseBaseService.selectCourseMsgByChatRoomId(chatRoomMsg.getChatRoomId());
                    chatRoomMsg.setCourseId(course.getId());
                    chatRoomMsg.setLiveTopic(course.getLiveTopic());
                    chatRoomMsgService.setMsgRedis(chatRoomMsg , course.getAppId());
                    chatRoomMsgService.insertChatRoomMsg(chatRoomMsg);
                }
            }
        }
    }

}
