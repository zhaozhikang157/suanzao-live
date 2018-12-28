package com.longlian.mq.service.impl;

import com.longlian.live.newdao.LiveConnectRequestMapper;
import com.longlian.model.LiveConnectRequest;
import com.longlian.mq.service.LiveConnectRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhan on 2018-03-22.
 */
@Service("liveConnectRequestService")
public class LiveConnectRequestServiceImpl implements LiveConnectRequestService {

    @Autowired
    LiveConnectRequestMapper liveConnectRequestMapper;


    @Override
    public void setLiveConnectStudenOnlineStatus(LiveConnectRequest liveConnectRequest) {
        liveConnectRequestMapper.updateLiveConnectStudentOnlineStatus(liveConnectRequest);
    }
}
