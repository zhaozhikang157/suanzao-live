package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.JoinCourseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Mapper
public interface JoinCourseRecordMapper {

    int insert(JoinCourseRecord joinCourseRecord);

    int update(JoinCourseRecord joinCourseRecord);

    String getJoinStatus(@Param("appId") long appId, @Param("courseId") long courseId);

    JoinCourseRecord getByAppIdAndCourseId(@Param("appId") long appId, @Param("courseId") long courseId);
    long getCourseRecordCount(long id);
    long getFirstJoinByAppId(long appId);

    int getPaySuccessRecordCount(@Param("courseId")long courseId  ,@Param("payStatus") String payStatus);
    int getFirstPaySuccessRecordCount(@Param("roomId")long roomId  ,@Param("payStatus") String payStatus);

    List<Map> getCourseRecordList(long id);

    List<Map> getMyBuyCourseListPage(@Param("appId") long appId, @Param("page") DataGridPage dg ,   @Param("isRecorded") String isRecorded);

    List<Map> getMyBuyCourseListV2Page(@Param("appId") long appId, @Param("page") DataGridPage dg ,   @Param("isRecorded") String isRecorded);
    List<Map> getMyBuyCourseListV3Page(@Param("appId") long appId, @Param("page") DataGridPage dg ,   @Param("isRecorded") String isRecorded);

    int   updateSignUpStatusById(@Param("id") long id, @Param("signUpStatus") String signUpStatus , @Param("invitationAppId")  long  invitationAppId);
    int   updateSignUpStatusByCourseIdAndAppId(@Param("courseId") long courseId,@Param("appId") long appId, @Param("signUpStatus") String signUpStatus , @Param("invitationAppId")  long  invitationAppId);

    Long getCourseIdById(long id);

    List<Map> getJoinCourseUser(@Param("courseId") Long courseId);

    List<Map> getCourseMessageTaskPage(@Param("page")DataGridPage page);

    int getPayCourseNum(long courseId);

    void updateCourseJoinCount(@Param("courseId") long courseId, @Param("joinCount") long joinCount);

    void updateCourseRelayJoinCount(@Param("courseId") long courseId, @Param("joinCount") long joinCount);

    Map getPaySourceNum(long courseId);

    List<Map> getHandlerPayingJoinCourse(Date date);
    int  handlerPayingJoinCourse(long id);

    long getCountByLiveId(long roomId);

    List<Map> getListCourseId(long courseId);

    List<Map> getMyBuyCourseListH5Page(@Param("appId") long appId, @Param("page") DataGridPage dg ,   @Param("isRecorded") String isRecorded);
}
