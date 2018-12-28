package com.longlian.chatmsg.service.impl;


import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.chatmsg.util.yunXinAnti.TextQueryByTaskIds;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.chatmsg.dao.ChatRoomMsgMapper;
import com.longlian.chatmsg.service.ChatRoomMsgService;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by liuhan on 2017-02-17.
 */
@Service("chatRoomMsgService")
public class ChatRoomMsgServiceImpl implements ChatRoomMsgService {
    private static Logger log = LoggerFactory.getLogger(ChatRoomMsgServiceImpl.class);
    @Autowired
    ChatRoomMsgMapper chatRoomMsgMapper;
    @Autowired
    RedisUtil redisUtil;

    private final int fPageSize = 20;


    /**
     * 插入历史表中
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertChatRoomMsg(ChatRoomMsg chatRoomMsg) {
        int action = -1;
        chatRoomMsg.setQuizOrReply("0");
        if ("CUSTOM".equals(chatRoomMsg.getMsgType())) {
            String attach = chatRoomMsg.getAttach();
            Map map2 = (Map) JsonUtil.getObject(attach, HashMap.class);
            if (map2 != null) {
                Integer type = 0;
                try {
                    type = (Integer) map2.get("type");
                } catch (Exception ex) {
                    String temp = (String) map2.get("type");
                    type = Integer.parseInt(temp);
                }
                if (type == 5) {
                    chatRoomMsg.setQuizOrReply("1");
                    Map m = (Map) map2.get("data");
                    action = TextQueryByTaskIds.Yunxin(m.get("value").toString(), chatRoomMsg.getMsgidClient());
                } else if (type == 6) {
                    chatRoomMsg.setQuizOrReply("2");
                    Map m = (Map) map2.get("data");

                    String nick = (String) m.get("nick");
                    chatRoomMsg.setReplyName(nick);
                    try {
                        String account = (String) m.get("account");
                        String avator = (String) m.get("avatar");
                        chatRoomMsg.setReplyId(Long.parseLong(account));
                        chatRoomMsg.setReplyAvator(avator);
                    } catch (Exception ex) {
                        log.error("聊天历史消息处理报错", ex);
                    }
                }
                if (type == YunxinCustomMsgType.TEARCHER_INPUTING.getType()) {
                    redisUtil.set(RedisKey.course_teacher_inputing   + chatRoomMsg.getChatRoomId() , "1");
                    redisUtil.expire(RedisKey.course_teacher_inputing  + chatRoomMsg.getChatRoomId() , 20 * 5);
                }
                if (type == YunxinCustomMsgType.TEARCHER_DISABLE_INPUT.getType()) {
                    redisUtil.set(RedisKey.course_teacher_inputing  + chatRoomMsg.getChatRoomId() , "0");
                }
                chatRoomMsg.setCustomMsgType(String.valueOf(type));
            } else {
                //如果不是自定义消息，则设置为0
                chatRoomMsg.setCustomMsgType("0");
            }
        }  else  {
            //如果不是自定义消息，则设置为0
            chatRoomMsg.setCustomMsgType("0");
        }
        if ("TEXT".equals(chatRoomMsg.getMsgType())) {
            String attach = chatRoomMsg.getAttach();
            action = TextQueryByTaskIds.Yunxin(attach, chatRoomMsg.getMsgidClient());
        }
        if(action == 2 ){    //云信 1:通过
            chatRoomMsg.setIsGarbage("1");//本地库 :1 不通过
        }else {
            chatRoomMsg.setIsGarbage("0");
        }
        try {
            chatRoomMsgMapper.insert(chatRoomMsg);
        }finally {
            redisUtil.del(RedisKey.chat_room_msg_task + chatRoomMsg.getMsgidClient());
        }
        //先插入，再发送消息
        if(chatRoomMsg.getId() != null && chatRoomMsg.getId() > 0){
            //获取图片信息,则发送mq,上传到阿里云,然后把图片地址替换掉
            if("PICTURE".equals(chatRoomMsg.getMsgType())){
                redisUtil.lpush(RedisKey.chat_room_msg_img, JsonUtil.toJson(chatRoomMsg));
            }
            if("AUDIO".equals(chatRoomMsg.getMsgType())){
                redisUtil.lpush(RedisKey.chat_room_msg_audio, JsonUtil.toJson(chatRoomMsg));
            }
        }
    }
    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsg(Long courseId,Integer offSet){
        DataGridPage dg =  new DataGridPage();
        if (offSet!=null){
            dg.setCurrentPage(offSet);
            dg.setPageSize(100);
        }
        return chatRoomMsgMapper.selectByCoursePage(courseId, dg);
    }

    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsgByCourseId(Long courseId){
     
        return chatRoomMsgMapper.selectByCourseId(courseId);
    }
  

    /**
     * 取得历史消息 分页
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    @Override
    public List<Map> getHistoryMsgPage(Long courseId,Integer offSet, ChatRoomMsg chatRoomMsg) {
        DataGridPage dg =  new DataGridPage();
        if (offSet!=null){
            dg.setOffset(offSet);
        }
        return chatRoomMsgMapper.getHistoryMsgPage(courseId, dg, chatRoomMsg);
    }
    /**
     * 取得历史消息 分页IOS
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    @Override
    public List<Map> getChatRoomMsgPage(Long courseId,Integer offSet, ChatRoomMsg chatRoomMsg) {
        DataGridPage dg =  new DataGridPage();
        if (offSet!=null){
            dg.setOffset(offSet);
        }
        return chatRoomMsgMapper.getChatRoomMsgPage(courseId, dg, chatRoomMsg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourseName(long courseId, String name) {
        chatRoomMsgMapper.updateCourseName(courseId, name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearScreenByChatRoomId(Long chatRoomId) {
        chatRoomMsgMapper.clearScreenByChatRoomId(chatRoomId);
    }


    @Override
    public ActResultDto findQuestry(long courseId, Long msgId , Long pageSize) {
        ActResultDto resultDto = new ActResultDto();
        if(msgId == null) msgId = 0l;
        if(pageSize == null) pageSize = 10l;
        List<ChatRoomMsg> list = chatRoomMsgMapper.findQuestry(courseId, msgId, pageSize);
        if(list.size() < 1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }
        for(ChatRoomMsg  msg : list){
            msg.setFromExt(DateUtil.format(msg.getMsgTimestamp()));
            msg.setAttach(JsonUtil.getMap4Json(JsonUtil.toJson(JsonUtil.getMap4Json(msg.getAttach()).get("data"))).get("value").toString());
        }
        resultDto.setData(list);
        return resultDto;
    }

    @Override
    public ActResultDto findAllMsg(long courseId, long teacherId, Long msgId, Long pageSize) {
        ActResultDto resultDto = new ActResultDto();
        if(msgId == null) msgId = 0l;
        if(pageSize == null) pageSize = 10l;
        List<ChatRoomMsg> list = chatRoomMsgMapper.findAllMsg(courseId, teacherId, msgId, pageSize);
        if(list.size() < 1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }
        for(ChatRoomMsg  msg : list){
            msg.setFromExt(DateUtil.format(msg.getMsgTimestamp()));
        }
        resultDto.setData(list);
        return resultDto;
    }

    @Override
    public ActResultDto findTeacherMsgPage(long courseId, long teacherId, Integer offset, Integer pageSize) {
        ActResultDto resultDto = new ActResultDto();
        if(offset == null) offset = 0;
        if(pageSize == null) pageSize = 10;
        DataGridPage page = new DataGridPage();
        page.setPageSize(pageSize);
        page.setOffset(offset);
        List<ChatRoomMsgDto> list = chatRoomMsgMapper.findTeacherMsgPage(courseId, teacherId, page);
        if(list.size() < 1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }
        for(ChatRoomMsgDto msg : list){
            if("11".equals(msg.getCustomMsgType())){
                msg.setAttach(JsonUtil.getMap4Json(JsonUtil.toJson(JsonUtil.getMap4Json(msg.getAttach()).get("data"))).get("value").toString());
            }
            if("PICTURE".equals(msg.getMsgType()) || "AUDIO".equals(msg.getMsgType())){
                Map map = JsonUtil.getMap4Json(msg.getAttach());
                if(map.get("url") == null){
                    map.put("url","");
                }
                msg.setMap(map);
            }
            msg.setFromExt(DateUtil.format(msg.getMsgTimestamp()));
        }
        resultDto.setData(list);
        return resultDto;
    }

    @Override
    public ActResultDto findLastThreeMsg(long courseId,long teacherId) {
        ActResultDto resultDto = new ActResultDto();
        int limit = 3;
        List<ChatRoomMsg> list = chatRoomMsgMapper.findLastThreeMsg(courseId, teacherId, limit);
        if(list.size() > 0){
            for(ChatRoomMsg  msg : list){
                msg.setFromExt(DateUtil.format(msg.getMsgTimestamp()));
                if("5".equals(msg.getCustomMsgType())){
                    msg.setAttach(JsonUtil.getMap4Json(JsonUtil.toJson(JsonUtil.getMap4Json(msg.getAttach()).get("data"))).get("value").toString());
                }
            }
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(list);
            return resultDto;
        }
        resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return resultDto;
    }
    @Override
    public ActResultDto findLastMsg(long courseId,long teacherId) {
        ActResultDto resultDto = new ActResultDto();
        int limit = 1;
        List<ChatRoomMsg> list = chatRoomMsgMapper.findLastMsg(courseId, teacherId, limit);
        if(list.size() > 0){
            for(ChatRoomMsg  msg : list){
                msg.setFromExt(DateUtil.format(msg.getMsgTimestamp()));
                if("5".equals(msg.getCustomMsgType())){
                    msg.setAttach(JsonUtil.getMap4Json(JsonUtil.toJson(JsonUtil.getMap4Json(msg.getAttach()).get("data"))).get("value").toString());
                }
            }
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(list);
            return resultDto;
        }
        resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return resultDto;
    }

    @Override
    public void updateMsgGarbage(String msgidClient) {
        chatRoomMsgMapper.updateMsgGarbage(msgidClient);
    }

    @Override
    public ActResultDto findMsgByCourseId(ChatRoomMsgDto chatRoomMsgDto) {
        ActResultDto resultDto = new ActResultDto();
        long pageSize = chatRoomMsgDto.getPageSize();
        List<ChatRoomMsg> list  =   chatRoomMsgMapper.findMsgByCourseId(chatRoomMsgDto.getCourseId() , chatRoomMsgDto.getTeacherId(), chatRoomMsgDto.getId(), pageSize);
        if(list != null && list.size() > 0){
            resultDto.setData(list);
        } else {
            resultDto.setData(new ArrayList<>());
        }
        return resultDto;
    }

    public void setRedis(List<ChatRoomMsg> list , long courseId){
        redisUtil.del(RedisKey.chat_room_msg + courseId);
        Collections.reverse(list);
        for(ChatRoomMsg msg : list){
            redisUtil.lpush(RedisKey.chat_room_msg + courseId,JsonUtil.toJson(msg));
        }
        redisUtil.expire(RedisKey.chat_room_msg + courseId,3*24*60*60);
    }

    @Override
    public void updateAttach(ChatRoomMsg chatRoomMsg) {
        String msgidClient = chatRoomMsg.getMsgidClient();
        String attach = chatRoomMsg.getAttach();
        chatRoomMsgMapper.updateAttach(msgidClient,attach);
    }

    @Override
    public long findMaxMsgId() {
        return chatRoomMsgMapper.findMaxId();
    }

}
