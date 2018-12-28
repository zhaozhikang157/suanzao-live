package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.model.Course;
import com.longlian.model.JoinCourseRecord;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ThirdPayDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017/2/12.
 */
public interface JoinCourseRecordService {

    public boolean isJoinCourse(Long userId, Course course);
    String getJoinCourseStatus(Long userId , Long courseId );
    JoinCourseRecord getByAppIdAndCourseId(long userId, long courseId);
    JoinCourseRecord getByAppIdAndCourseIdByRedis(long userId, long courseId , boolean isLock)throws Exception;
    boolean getFirstJoinByAppId(long userId);

    void insert(JoinCourseRecord joinCourseRecord);

    long getCourseRecordCount(long id);
    int getPaySuccessRecordCount(long id , String payStatus);
    int getFirstPaySuccessRecordCount(long roomId , String payStatus);

    List<Map> getCourseRecordList(long id);
    List<Map> getMyBuyCourseList(long appId, Integer pageNum, Integer pageSize, boolean isHaveRecord);
    List<Map> getMyBuyCourseListV2(long id, Integer pageNum, Integer pageSize, boolean isHaveRecord);
    List<Map> getMyBuyCourseListV3(long id, Integer pageNum, Integer pageSize, boolean isHaveRecord,String clientType);
    int updateSignUpStatus(long id, String signUpStatus , long  invitationAppId );
    int updateSignUpStatusByCourseIdAndAppId(long courseId , long appId, String signUpStatus  , long  invitationAppId);
    long getCourseIdById(long id);
    /**
     * 取得课程参加人数
     * @param courseId
     * @return
     */
    long getCountByCourseId(Long courseId);

    public void addJoinUser2Redis(Long courseId , Long userId , Date createTime);

    public void addJoinUser2Redis(Long courseId , Long userId , Date createTime , boolean isVirtualUser);

    List<Map> getCourseMessageTask(DataGridPage page);

    public  void writeJoinCount2DB(long courseId, long count);

    List<Map>  getHandlerPayingCourse(Date date);
    int handlerPayingJoinCourse(long id);

    /**
     * 根据直播间ID 获取所有报名学习人数
     * @param liveId
     * @return
     */
    long getCountByRoomId(long liveId);

    public void loadJoinCourseUser(Long courseId);

    JoinCourseRecord handlerJoinCourseRecord(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId)throws Exception;

    JoinCourseRecord handlerJoinCourseRecordRelay(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId)throws Exception;

}
