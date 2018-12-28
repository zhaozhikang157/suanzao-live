package com.longlian.mq.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserFollow;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserFollowMqMapper {
    int insert(UserFollow userFollow);

    int update(UserFollow userFollow);

    Integer getCountByUserIdAndRoomId(@Param("appId") long appId,@Param("roomId") long roomId);

    int updataIsReaderd(long id);
}