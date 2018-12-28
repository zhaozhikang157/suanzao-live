package com.longlian.appmsg.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface AppMsgMapper {

    long findAppMsgMaxId();

    int update(AppMsg record);

    List<Map> getAppMsgListPage(@Param("id") long id, @Param("page") DataGridPage dg);

    Map getNewAppMsgTypeInfo(@Param("id") long id,@Param("type")String type,@Param("status")String status);

    Long getMsgTypeInfoCount(@Param("id") long id,@Param("type")String type,@Param("status")String status);

    List<Map> getNewAppMsgTypeListPage(@Param("type") String type, @Param("id") long id, @Param("page") DataGridPage dg);

    void updateStatusByMsgType(@Param("type") String type, @Param("id") long id);

    Map getAppMsg(long id);

    int updateReadTime(long id);

    int deleteAppMsg(@Param("id")  long id);

    long getIsAppMsg(long appId);

    int readAllMessage(long appId);

    int deleteAllAppMsg(@Param("appId") long appId);

    void updateMsgCourseStatus(@Param("courseId") long courseId, @Param("status") String status, @Param("roomId") long roomId);

    void updateMsgCourseStatues(@Param("courses") List<Course> courses);

    void updateParam(@Param("teacherId") long teacherId, @Param("isSeriesCourse") String isSeriesCourse,
                     @Param("seriesCourseId") long seriesCourseId, @Param("courseId") long courseId,
                     @Param("roomId") long roomId, @Param("status") String status);

    List<AppMsg> findOrderId(@Param("offset") int offset, @Param("pageSize") int pageSize);

    void updateTableId(@Param("msgId") long msgId, @Param("courseId") long courseId);

    List<AppMsg> findAllMsg();

    void setRoomIdById(@Param("id") long id, @Param("roomId") long roomId);

    List<AppMsg> findAllMsgP(@Param("offset") int offset, @Param("pageSize") int pageSize);

    List<AppMsg> findTypeMsg(@Param("offset") int offset, @Param("pageSize") int pageSize);

    void updateInfo(@Param("id") long id, @Param("courseId") long courseId, @Param("roomId") long roomId);

    void bateUpdate(List<Map> update);

    void delAppMsgBefore(@Param("createTime") Date time, @Param("type") int type);

    int insert(AppMsg record);

    void deleteAppMsgByIds(@Param("ids") Long[] ids);
    //极光推送消息分页
    List<Map> getPushAppMsgTypeListPage(@Param("type") String type, @Param("page") DataGridPage dg);
}