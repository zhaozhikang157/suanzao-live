package com.longlian.chatmsg.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.model.ChatRoomMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatRoomMsgMapper {

    int insert(ChatRoomMsg record);

    List<Map> selectByCoursePage(@Param("courseId") Long courseId, @Param("page") DataGridPage dg);

    List<Map> selectByCourseId(@Param("courseId") Long courseId);

    List<Map> getHistoryMsgPage(@Param("courseId") Long courseId, @Param("page") DataGridPage dg, @Param("chatRoomMsg") ChatRoomMsg chatRoomMsg);

    List<Map> getChatRoomMsgPage(@Param("courseId") Long courseId, @Param("page") DataGridPage dg, @Param("chatRoomMsg") ChatRoomMsg chatRoomMsg);

    void clearScreenByChatRoomId(long chatRoomId);

    void updateCourseName(@Param("courseId") long courseId, @Param("courseName") String name);

    List<ChatRoomMsg> findQuestry(@Param("courseId") long courseId, @Param("msgId") Long msgId, @Param("limit") Long limit);

    List<ChatRoomMsg> findAllMsg(@Param("courseId") long courseId, @Param("teacherId") long teacherId, @Param("msgId") Long msgId, @Param("limit") Long limit);

    List<ChatRoomMsgDto> findTeacherMsgPage(@Param("courseId") long courseId, @Param("teacherId") long teacherId, @Param("page") DataGridPage dg);

    List<ChatRoomMsg> findLastThreeMsg(@Param("courseId") long courseId, @Param("teacherId") long teacherId, @Param("limit") int limit);

    List<ChatRoomMsg> findLastMsg(@Param("courseId") long courseId, @Param("teacherId") long teacherId, @Param("limit") int limit);

    void updateMsgGarbage(String msgidClient);

    List<ChatRoomMsg> findMsgByCourseId(@Param("courseId") long courseId, @Param("teacherId") long teacherId, @Param("msgId") long msgId, @Param("pageSize") long pageSize);

    void updateAttach(@Param("msgidClient") String msgidClient, @Param("attach") String attach);

    long findMaxId();

}