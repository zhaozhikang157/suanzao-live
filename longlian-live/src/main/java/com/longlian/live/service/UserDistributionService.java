package com.longlian.live.service;

/**
 * Created by syl on 2017/2/22.
 */
public interface UserDistributionService {
    void addUserDistributionByCourseId(long courseId , long appId , long invitationAppId);

    void addUserDistributionByRoomId(long roomId , long appId , long invitationAppId);

    String getInvitationAppId(long roomId, long appId);
}
