package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.LiveConnect;
import com.longlian.model.LiveConnectRequest;
import com.longlian.token.AppUserIdentity;

/**
 * Created by liuhan on 2018-03-25.
 */
public interface MixLiveService {

    public ActResultDto getAllUsers(Long courseId, String ids, Integer pageSize ,Long onConnectId);

    public void saveLiveConnect(LiveConnectRequest request);

    void closeConnectRequest(LiveConnectRequest liveConnectRequest);
}


