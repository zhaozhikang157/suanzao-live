package com.longlian.live.service.impl;


import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.dao.ChatRoomMsgMapper;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    @Autowired
    CourseBaseService courseBaseService;

    private final int fPageSize = 20;
    @Autowired
    private ChatMsgRemote chatMsgRemote;

    private ChatMsgRemote getChatMsgRemote() {
        return chatMsgRemote;
    }

    /**
     * 插入历史表中
     * @return
     */
    @Override
    public void insertChatRoomMsg(ChatRoomMsg chatRoomMsg) {
        getChatMsgRemote().insert(chatRoomMsg);
    }

    @Override
    public void setMsgRedis(ChatRoomMsg chatRoomMsg , long teacherId){
        if(chatRoomMsg.getId() != null && chatRoomMsg.getId() > 0){
            if("0".equals(chatRoomMsg.getIsGarbage()) && !StringUtils.isEmpty(chatRoomMsg.getAttach())){
                if("13".equals(chatRoomMsg.getCustomMsgType()) || "14".equals(chatRoomMsg.getCustomMsgType()) || StringUtils.isEmpty(chatRoomMsg.getCustomMsgType())){
                    if((chatRoomMsg.getFromAccount()+"").equals(teacherId+"")){
                        // 从数据库拉取,并且设置缓存 或者直接放入缓存
                        findDbAndSetRedis(chatRoomMsg,teacherId);
                    }
                }
                if("11".equals(chatRoomMsg.getCustomMsgType())){
                    // 从数据库拉取,并且设置缓存 或者直接放入缓存
                    findDbAndSetRedis(chatRoomMsg,teacherId);
                }
            }
//            //获取图片信息,则发送mq,上传到阿里云,然后把图片地址替换掉
//            if("PICTURE".equals(chatRoomMsg.getMsgType())){
//                redisUtil.lpush(RedisKey.chat_room_msg_img, JsonUtil.toJson(chatRoomMsg));
//            }
//            if("AUDIO".equals(chatRoomMsg.getMsgType())){
//                redisUtil.lpush(RedisKey.chat_room_msg_audio, JsonUtil.toJson(chatRoomMsg));
//            }
        }
    }

    /**
     *  从数据库拉取,并且设置缓存 或者直接放入缓存
     * @param chatRoomMsg
     * @param teacherId
     */
    private void findDbAndSetRedis(ChatRoomMsg chatRoomMsg , Long teacherId){
        long size = redisUtil.llen(RedisKey.chat_room_msg + chatRoomMsg.getCourseId());
        if(size == 0){
             List<Map> list = findMsgByCourseIdDB(chatRoomMsg.getCourseId() , teacherId , 0l , fPageSize);
            if(list != null && list.size()>0){
                //设置缓存
                setRedis(list,chatRoomMsg.getCourseId());
            }
        }else{
            redisUtil.lpush(RedisKey.chat_room_msg + chatRoomMsg.getCourseId(), JsonUtil.toJson(chatRoomMsg));
        }
        redisUtil.expire(RedisKey.chat_room_msg + chatRoomMsg.getCourseId() , 3*24*60*60);
    }
    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsg(Long courseId,Integer offSet){
        ActResultDto dto = getChatMsgRemote().getHistoryMsg(courseId  , offSet);
        List<Map> list = new ArrayList<>();
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            list = (List<Map>) dto.getData();
        }
       return list;
    }

    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsgByCourseId(Long courseId){
        ActResultDto dto = getChatMsgRemote().getHistoryMsgByCourseId(courseId);
        List<Map> list = new ArrayList<>();
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            list = (List<Map>) dto.getData();
        }
        return list;
    }
  

    /**
     * 取得历史消息 分页
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    @Override
    public List<Map> getHistoryMsgPage(Long courseId,Integer offSet, ChatRoomMsg chatRoomMsg) {
        ChatRoomMsgDto chatRoomMsgDto = new ChatRoomMsgDto();
        chatRoomMsgDto.setOffSet(offSet);
        chatRoomMsgDto.setCourseId(courseId);
        chatRoomMsgDto.setMsgTimestamp(chatRoomMsg.getMsgTimestamp());
        ActResultDto dto = getChatMsgRemote().getHistoryMsgPage(chatRoomMsgDto);
        List<Map> list = new ArrayList<>();
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            list = (List<Map>) dto.getData();
        }
        return list;
    }
    /**
     * 取得历史消息 分页IOS
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    @Override
    public List<Map> getChatRoomMsgPage(Long courseId,Integer offSet, ChatRoomMsg chatRoomMsg) {
        ChatRoomMsgDto chatRoomMsgDto = new ChatRoomMsgDto();
        chatRoomMsgDto.setOffSet(offSet);
        chatRoomMsgDto.setCourseId(courseId);
        chatRoomMsgDto.setMsgTimestamp(chatRoomMsg.getMsgTimestamp());
        chatRoomMsgDto.setFromAccount(chatRoomMsg.getFromAccount());
        ActResultDto dto = getChatMsgRemote().getChatRoomMsgPage(chatRoomMsgDto);
        List<Map> list = new ArrayList<>();
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            list = (List<Map>) dto.getData();
        }
        return list;
    }


    @Override
    public void updateCourseName(long courseId, String name) {
        getChatMsgRemote().updateCourseName(courseId, name);
    }



    @Override
    public void clearScreenByChatRoomId(Long chatRoomId) {
        getChatMsgRemote().clearScreenByChatRoomId(chatRoomId);
    }


    @Override
    public ActResultDto findQuestry(long courseId, Long msgId , Long pageSize) {
        return getChatMsgRemote().findQuestry(courseId , msgId , pageSize);
    }

    @Override
    public ActResultDto findAllMsg(long courseId, long teacherId, Long msgId, Long pageSize) {
        return getChatMsgRemote().findAllMsg(courseId , teacherId , msgId , pageSize);
    }

    @Override
    public ActResultDto findTeacherMsgPage(long courseId, long teacherId, Integer offset, Integer pageSize) {
        return getChatMsgRemote().findTeacherMsgPage(courseId , teacherId , offset , pageSize);
    }

    @Override
    public ActResultDto findLastThreeMsg(long courseId,long teacherId) {
        return getChatMsgRemote().findLastThreeMsg(courseId , teacherId);
    }

    public List<Map> findMsgByCourseIdDB(Long courseId , Long teacherId , Long msgId ,Integer pageSize ) {
        ChatRoomMsgDto chatRoomMsgDto = new ChatRoomMsgDto();
        chatRoomMsgDto.setTeacherId(teacherId);
        chatRoomMsgDto.setCourseId(courseId);
        chatRoomMsgDto.setPageSize(pageSize);
        chatRoomMsgDto.setId(msgId);
        ActResultDto dto = getChatMsgRemote().findMsgByCourseId(chatRoomMsgDto);
        List list = new ArrayList();
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            list = (List<Map>) dto.getData();
        }
        return list;
    }

    @Override
    public void updateMsgGarbage(String msgidClient) {
        getChatMsgRemote().updateMsgGarbage(msgidClient);
    }

    @Override
    public ActResultDto findMsgByCourseId(Course course, long msgId, long pageSize) {
        ActResultDto resultDto = new ActResultDto();
        long size = redisUtil.llen(RedisKey.chat_room_msg + course.getId());
        List<Map> list = new ArrayList<Map>();
        if(msgId == 0){ //第一页
            if(size == 0){  //缓存--不存在
                list =  findMsgByCourseIdDB(course.getId() , course.getAppId(), msgId, (int)pageSize);
                if(list != null && list.size() > 0 ){
                    resultDto.setData(list);
//                    setRedis(list,course.getId());
                }else{
                    resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
                    resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
                }
                return resultDto;
            }else{  //存在缓存
                List<String> redisList = redisUtil.lrangeall(RedisKey.chat_room_msg + course.getId());
                if(redisList.size() < pageSize){
                   pageSize = redisList.size();
                }
                for(int i =  0 ; i < pageSize ; i++){
                    Map chatRoomMsg = JsonUtil.getObject(redisList.get(i),Map.class);
                    list.add(chatRoomMsg);
                }
                resultDto.setData(list);
                return resultDto;
            }
        }else{ //不是第一页 -- 去数据库取
            list =  findMsgByCourseIdDB(course.getId() , course.getAppId(), msgId, (int)pageSize);
            if(list != null && list.size() > 0){
                resultDto.setData(list);
            }else {
                resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
                resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            }
            return resultDto;
        }
    }

    public void setRedis(List<Map> list , long courseId){
        redisUtil.del(RedisKey.chat_room_msg + courseId);
        Collections.reverse(list);
        for(Map msg : list){
            redisUtil.lpush(RedisKey.chat_room_msg + courseId,JsonUtil.toJson(msg));
        }
        redisUtil.expire(RedisKey.chat_room_msg + courseId,3*24*60*60);
    }

    @Override
    public void updateAttach(ChatRoomMsg chatRoomMsg) {
        String msgidClient = chatRoomMsg.getMsgidClient();
        String attach = chatRoomMsg.getAttach();
        ChatRoomMsgDto chatRoomMsgDto = new ChatRoomMsgDto();
        chatRoomMsgDto.setMsgidClient(msgidClient);
        chatRoomMsgDto.setAttach(attach);
        getChatMsgRemote().updateAttach(chatRoomMsgDto);
    }

    @Override
    public long findMaxMsgId() {
        ActResultDto dto = getChatMsgRemote().findMaxMsgId();
        long result = 0;
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(dto.getCode())) {
            result = Long.parseLong(dto.getData() + "");
        }
        return result;
    }

}
