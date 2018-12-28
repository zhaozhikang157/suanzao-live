package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserRewardRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 */
public interface UserRewardRecordService {
    void insert(UserRewardRecord rewardRecord);
    List<Map> selectUserRewardSort(DataGridPage dg,long courseId);
    List<Map> findRewInfoPage(long appId , DataGridPage page);

    List<Map> findTodayRewInfoPage(long appId , DataGridPage page) ;

    List<Map>findRewInfoPageByCourseIdPage(long appId ,DataGridPage page , long courseId );

    List<Map> loadUserReward2Redis(Long courseId  , boolean isLock);

    public BigDecimal addUserReward(Long courseId , Long userId , BigDecimal rewardMoney);

    BigDecimal getTodayRewardIncome(Long appId);

    List<Map> findTodayRelayDetail(long id, DataGridPage dataGridPage);
}
