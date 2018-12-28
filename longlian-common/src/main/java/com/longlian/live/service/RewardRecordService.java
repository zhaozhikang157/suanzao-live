package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.Course;
import com.longlian.model.RewardRecord;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/23.
 */
public interface RewardRecordService {

    void insert(RewardRecord rewardRecord);

    String getRewardSuccessInvitationTeach();

    List<Map> getFollowRewardTeach();

    void getFollowRewardReSetRedisInit();

    Set getFollowRewardByTeachAppId(long appId);


    List<Map> getplatIncomePage(Integer offset, Integer pageSize, long appId);

    List<Map> getRecommendTeacherList(DatagridRequestModel page, Map map);

    BigDecimal getRecommendTeacherAccount(Map map);

    String exportExcelRecommendTeacher(Map map, HttpServletRequest request) throws Exception;

    List<Map> getFollowRewardRecordList(DatagridRequestModel page, Map map);

    BigDecimal getFollowRewardRecordAccount(Map map);

    String exportExcelFollowRewardRecord(Map map, HttpServletRequest request) throws Exception;

    List<Map> getCourseRecommendList(DatagridRequestModel page, Map map);

    BigDecimal getCourseRecommendAccount(Map map);

    String exportExcelCourseRewardRecord(Map map, HttpServletRequest request) throws Exception;

    List<Long> findAlreadyCourseId();

}
