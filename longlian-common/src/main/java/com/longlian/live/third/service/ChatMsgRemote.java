package com.longlian.live.third.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.model.ChatRoomMsg;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by liuhan on 2017-10-21.
 */
public interface ChatMsgRemote {
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /insert")
    public ActResultDto insert( ChatRoomMsg chatRoomMsg);

    @RequestLine("GET /getHistoryMsg?courseId={courseId}&offSet={offSet}")
    public ActResultDto getHistoryMsg(@Param("courseId") Long courseId,@Param("offSet") Integer offSet);

    @RequestLine("GET /getHistoryMsgByCourseId?courseId={courseId}")
    public ActResultDto getHistoryMsgByCourseId(@Param("courseId") Long courseId);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /getHistoryMsgPage")
    public ActResultDto getHistoryMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto);

    //取历史消息分页
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /getChatRoomMsgPage")
    public ActResultDto getChatRoomMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto);


    //修改课程名称
    @RequestLine("GET /updateCourseName?courseId={courseId}&name={name}")
    public ActResultDto updateCourseName(@Param("courseId") Long courseId,@Param("name") String name);

    //清屏
    @RequestLine("GET /clearScreenByChatRoomId?roomId={roomId}")
    public ActResultDto clearScreenByChatRoomId(@Param("roomId")  Long roomId) ;

    //查看问题
    @RequestLine("GET /findQuestry?courseId={courseId}&msgId={msgId}&pageSize={pageSize}")
    public ActResultDto findQuestry(@Param("courseId")  Long courseId,@Param("msgId")   Long msgId,@Param("pageSize")   Long pageSize);

    //查看所有消息
    @RequestLine("GET /findAllMsg?courseId={courseId}&teacherId={teacherId}&msgId={msgId}&pageSize={pageSize}")
    public ActResultDto findAllMsg(@Param("courseId") Long courseId,@Param("teacherId")  Long teacherId,@Param("msgId")   Long msgId,@Param("pageSize")   Long pageSize);

    //查看老师消息
    @RequestLine("GET /findTeacherMsgPage?courseId={courseId}&teacherId={teacherId}&offset={offset}&pageSize={pageSize}")
    public ActResultDto findTeacherMsgPage(@Param("courseId") Long courseId,@Param("teacherId")  Long teacherId,@Param("offset")  Integer offset,@Param("pageSize")  Integer pageSize) ;

    //最后三条
    @RequestLine("GET /findLastThreeMsg?courseId={courseId}&teacherId={teacherId}")
    public ActResultDto findLastThreeMsg(@Param("courseId") Long courseId,@Param("teacherId") Long teacherId);

    //最后一条
    @RequestLine("GET /findLastMsg?courseId={courseId}&teacherId={teacherId}")
    public ActResultDto findLastMsg(@Param("courseId") Long courseId,@Param("teacherId") Long teacherId);

    //修改是否是垃圾消息
    @RequestLine("GET /updateMsgGarbage?msgidClient={msgidClient}")
    public ActResultDto updateMsgGarbage(@Param("msgidClient") String msgidClient);

    //该课程消息
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /findMsgByCourseId")
    public ActResultDto findMsgByCourseId(ChatRoomMsgDto chatRoomMsgDto);

    //修改消息内容
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /updateAttach")
    public ActResultDto updateAttach( ChatRoomMsgDto chatRoomMsgDto);

    //查看最大消息ID
    @RequestLine("GET /findMaxMsgId")
    public ActResultDto findMaxMsgId();

}
