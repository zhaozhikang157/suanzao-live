package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserRewardRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Mapper
public interface UserRewardRecordMapper {
    int selectById(Long id);
    int insert(UserRewardRecord record);

    List<Map> selectUserRewardSortPage(@Param("page")DataGridPage dg,@Param("courseId")long courseId);

    BigDecimal findSumAccount(long appId);

    List<Map> findRewInfoPage(@Param("appId")long appId , @Param("page")DataGridPage page);

    List<Map> findTodayRewInfoPage(@Param("appId")long appId , @Param("page")DataGridPage page);

    List<Map> findRewInfoPageByCourseIdPage(@Param("appId")long appId ,
                                        @Param("page")DataGridPage page,
                                        @Param("courseId")long courseId);

    List<String> findRewCountbyAppId(long courseId);

    int findTodayRewCountbyAppId(long courseId);

    List<Map> statRewardCountbyAppId(@Param("courseId") Long courseId);

    BigDecimal getTodayRewardIncome(@Param("appId") Long appId);

    List<Map> findTodayRelayDetailPage(@Param("appId") long id, @Param("page") DataGridPage dataGridPage);
}
