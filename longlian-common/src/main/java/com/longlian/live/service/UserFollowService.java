package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pangchao on 2017/2/12.
 */
public interface UserFollowService {

    ActResultDto getCountUserFollow(Long roomId,Integer offSet,Date createTime);
    long getCountByRoomId(Long liveRoomId);
    List<Map> followLiveRoom(long appId, Integer pageNum, Integer pageSize);

    List<Map> followLiveRoomNew(long appId, Integer pageNum, Integer pageSize);

    ActResultDto follow(long id, Long liveRoomId);

    ActResultDto follow(long id, Long liveRoomId,String  thirdOpenId,String  thirdWechatAppId);

    ActResultDto cancelfollow(long id, Long liveRoomId);

    /**
     * 判断人员是否关注过没有
     * @param liveRoomId
     * @param appId
     * @return
     */
    boolean isFollowRoom(Long liveRoomId, long appId);

    /**
     * 根据room查出所有的关注人
     * @param liveRoomId
     * @return
     */
    Set<String> getFollowUser(Long liveRoomId);

    List<Map> getOpenIdByLiveRoomId(long liveRoomId);


}
