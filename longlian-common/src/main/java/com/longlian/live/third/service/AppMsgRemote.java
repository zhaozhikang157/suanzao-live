package com.longlian.live.third.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

/**
 * Created by liuhan on 2017-10-21.
 */
public interface AppMsgRemote {

    /**
     * 读取系统消息
     * @param id
     * @return
     */
    @RequestLine("GET /updateAppMsg?id={id}")
    ActResultDto updateAppMsg(@Param("id")long id);

    /**
     * 删除系统消息
     * @param id
     * @return
     */
    @RequestLine("GET /deleteAppMsg?id={id}")
    ActResultDto deleteAppMsg(@Param("id")String id);

    /**
     * 清空系统消息
     * @param appId
     * @return
     */
    @RequestLine("GET /deleteAllAppMsg?appId={appId}")
    ActResultDto deleteAllAppMsg(@Param("appId")long appId);

    /**
     * 读取系统消息
     * @param appId
     * @return
     */
    @RequestLine("GET /readAllMessage?appId={appId}")
    ActResultDto readAllMessage(@Param("appId")long appId);

    @RequestLine("POST /getAppMsgList?id={id}&pageNum={pageNum}&pageSize={pageSize}")
    ActResultDto getAppMsgList(@Param("id") long id,@Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize);

    @RequestLine("POST /getNewAppMsgTypeInfo?id={id}&version={version}")
    ActResultDto getNewAppMsgTypeInfo(@Param("id") long id,@Param("version") String version);

    @RequestLine("POST /getNewAppMsgTypeList?type={type}&id={id}&pageNum={pageNum}&pageSize={pageSize}")
    ActResultDto getNewAppMsgTypeList(@Param("type")String type,@Param("id") long id,@Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize);

    @RequestLine("POST /updateStatusByMsgType?type={type}&id={id}")
    ActResultDto updateStatusByMsgType(@Param("type")String type,@Param("id") long id);

    @RequestLine("POST /deleteAppMsgByIds?ids={ids}")
    ActResultDto deleteAppMsgByIds(@Param("ids") String ids);

    /**
     * 插入消息
     * @param appMsg
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("GET /insertAppMsg")
    ActResultDto insertAppMsg(@RequestBody AppMsg appMsg);

    @RequestLine("GET /getIsAppMsg?appId={appId}")
    ActResultDto getIsAppMsg(@Param("appId") long appId);

    @RequestLine("GET /updateMsgCourseStatus?courseId={courseId}&status={status}&roomId={roomId}")
    ActResultDto updateMsgCourseStatus(@Param("courseId")long courseId, @Param("status")String status , @Param("roomId")long roomId);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("GET /insertV2")
    ActResultDto insertV2(@RequestBody AppMsg appMsg);

    @RequestLine("GET /delAppMsgBefore?time={time}&i={i}")
    ActResultDto delAppMsgBefore(@Param("time") Date time ,@Param("i") int i);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /updateMsgCourseStatues")
    ActResultDto updateMsgCourseStatues(@RequestBody List<Course> courseList);

}
