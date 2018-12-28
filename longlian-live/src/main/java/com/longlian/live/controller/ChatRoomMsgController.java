package com.longlian.live.controller;

import com.alibaba.fastjson.JSON;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseService;
import com.longlian.live.util.yunxin.CheckSumBuilder;
import com.longlian.live.util.yunxin.YunXinUtil;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/chatmsg")
public class ChatRoomMsgController {

    private static Logger log = LoggerFactory.getLogger(ChatRoomController.class);
    @Autowired
    private ChatRoomMsgService chatRoomMsgService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;


    @RequestMapping(value = "/receiveMsg", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ActResultDto receiveMsg(HttpServletRequest request , @RequestBody Map map ) throws Exception {
        String CheckSum = request.getHeader("CheckSum");
        String MD5 = request.getHeader("MD5");
        String CurTime = request.getHeader("CurTime");
        String checkSum =  CheckSumBuilder.getCheckSum(YunXinUtil.appSecret , MD5 , CurTime);
        if (map.isEmpty()) {
            return ActResultDto.success();
        }
        if (checkSum.equals(CheckSum)) {
            String msgMxiId = redisUtil.get(RedisKey.chat_room_msg_max_id);
            if(cn.jpush.api.utils.StringUtils.isEmpty(msgMxiId)){
                long maxId = chatRoomMsgService.findMaxMsgId();
                redisUtil.set(RedisKey.chat_room_msg_max_id , maxId + "");
            }
            map.put("id",String.valueOf(redisUtil.incr(RedisKey.chat_room_msg_max_id)));
            //System.out.println("收到消息：" +JsonUtil.toJson(map));
            map.put("courseId","0");
            ChatRoomMsg chatRoomMsg = new ChatRoomMsg().mapToValue(map);
            Long courseId = null;
            String liveTopic = "";
            long teacherId = 0;
            String key = RedisKey.ll_chat_room_msg_By_chatRoomId + "_" + chatRoomMsg.getChatRoomId();
            Map<String , String> courseMap =  redisUtil.hmgetAll(key);
            if(courseMap != null && !courseMap.isEmpty()){
                courseId  = Long.parseLong(courseMap.get("courseId"));
                liveTopic = courseMap.get("liveTopic");
                teacherId = courseMap.get("teacherId") == null ? 0 : Long.parseLong(courseMap.get("teacherId"));
            }else{
                Course course = courseBaseService.selectCourseMsgByChatRoomId(chatRoomMsg.getChatRoomId());
                courseId  =course.getId() ;
                teacherId = course.getAppId();
                liveTopic = course.getLiveTopic();
                Map<String,String> m = new HashMap<String,String>();
                m.put("courseId",courseId.toString());
                m.put("liveTopic",liveTopic);
                m.put("teacherId",course.getAppId()+"");
                redisUtil.hmset(key,m,RedisKey.ll_chat_room_msg_By_chatRoomId_valid_time);
            }
            chatRoomMsg.setCourseId(courseId);
            chatRoomMsg.setLiveTopic(liveTopic);
            chatRoomMsgService.setMsgRedis(chatRoomMsg,teacherId);
            //发送mq处理
            map.put("courseId",courseId);
            map.put("liveTopic",liveTopic);
            redisUtil.set(RedisKey.chat_room_msg_task + map.get("msgidClient") , JsonUtil.toJson(map));
            redisUtil.expire(RedisKey.chat_room_msg_task + map.get("msgidClient") , 2*24*60*60);
            redisUtil.lpush(RedisKey.ll_chat_room_msg_wait2db,  JsonUtil.toJson(map));
        }
        return ActResultDto.success();
    }
}
