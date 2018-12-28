package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-02-17.
 */
public interface ChatRoomMsgService {
    /**
     * 新增
     * @return
     */
    public void insertChatRoomMsg(ChatRoomMsg chatRoomMsg) ;

    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsg(Long courseId,Integer offSet);

    /**
     * 取得历史消息
     * @param courseId
     * @return
     */
    public List<Map> getHistoryMsgByCourseId(Long courseId);
    /**
     * 取得历史消息 分页
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    List<Map> getHistoryMsgPage(Long courseId,Integer offSet,ChatRoomMsg chatRoomMsg);
    /**
     * 取得历史消息 分页
     * @param courseId
     * @param chatRoomMsg
     * @return
     */
    List<Map> getChatRoomMsgPage(Long courseId,Integer offSet,ChatRoomMsg chatRoomMsg);



    void updateCourseName(long l, String name);


    void clearScreenByChatRoomId(Long chatRoomId);

    ActResultDto findQuestry(long courseId , Long msgId , Long pageSize);

    ActResultDto findAllMsg(long courseId, long teacherId, Long msgId, Long pageSize);

    ActResultDto findTeacherMsgPage(long courseId , long teacherId ,  Integer offset , Integer pageSize);

    ActResultDto findLastThreeMsg(long courseId,long teacherId);

    void updateMsgGarbage(String msgidClient);

    ActResultDto findMsgByCourseId(Course course , long msgId , long pageSize);

    void updateAttach(ChatRoomMsg chatRoomMsg);

    long findMaxMsgId();

    void setMsgRedis(ChatRoomMsg chatRoomMsg , long teacherId);

    public List<Map> findMsgByCourseIdDB(Long courseId , Long teacherId , Long msgId ,Integer pageSize );
}
