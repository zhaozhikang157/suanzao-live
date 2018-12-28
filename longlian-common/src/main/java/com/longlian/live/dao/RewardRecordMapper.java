package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.RewardRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Mapper
public interface RewardRecordMapper {
    int selectById(Long id);

    int update(RewardRecord record);

    int insert(RewardRecord record);

    List<Map> getRewardSuccessInvitationTeach();

    List<Map> getFollowRewardTeach();


    List<Map> getplatIncomePage(@Param("page")DataGridPage dg,@Param("appId")long appId);

    List<Map> getRecommendTeacherListPage(@Param("page") DatagridRequestModel page, @Param("map") Map map);

    BigDecimal getRecommendTeacherAccount(@Param("map")  Map map);

    List<Map> getRecommendTeacherList(@Param("map") Map map);

    List<Map> getFollowRewardRecordListPage(@Param("page") DatagridRequestModel page, @Param("map") Map map);

    BigDecimal getFollowRewardRecordAccount(@Param("map") Map map);

    List<Map> getFollowRewardRecordList(@Param("map") Map map);

    List<Map> getCourseRecommendListPage(@Param("page") DatagridRequestModel page, @Param("map") Map map);

    BigDecimal getCourseRecommendAccount(@Param("map")  Map map);

    List<Map> getCourseRecommendList(@Param("map") Map map);

    List<Long> findAlreadyCourseId();

}
