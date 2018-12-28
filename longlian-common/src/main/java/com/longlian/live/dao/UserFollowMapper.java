package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Mapper
public interface UserFollowMapper {
    int insert(UserFollow userFollow);

    int update(UserFollow userFollow);

    List<Map> getCountUserFollow(Long roomId);

    List<Map> getUserFollowListByApp(@Param("roomId")Long roomId);

    List<Map> getUserFollowList(@Param("roomId")Long roomId,@Param("createTime")String createTime);

    List<Map> getUserFollowListByAppPage(@Param("roomId")Long roomId,@Param("page")DataGridPage dg,@Param("createTime")String createTime);

    long getCountByRoomId(Long liveRoomId);

    List<Map> followLiveRoomPage(@Param("appId") long id, @Param("page") DataGridPage dg);

    List<Map> followLiveRoomNewPage(@Param("appId") long id, @Param("page") DataGridPage dg);

    List<UserFollow> selectUserFollowByRoomId(@Param("roomId") long roomId);

    List<Map> getOpenIdByLiveRoomId(long roomId);

    List<UserFollow> selectFollowByRoomId(@Param("roomId") long roomId);

    Long getAppIdByLiveRoomId(@Param("roomId") Long liveRoomId);
}
