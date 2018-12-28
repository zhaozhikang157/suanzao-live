package com.longlian.live.dao;

import com.longlian.model.UserDistribution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/22.
 */
public interface UserDistributionMapper {
    int checkUpByRoomIdAndAppId(@Param("roomId") long roomId, @Param("appId") long appId);

    void insert(UserDistribution userDistribution);

    List<UserDistribution> getAllList();
}
