package com.longlian.mq.service;

import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.longlian.dto.ActResultDto;
import com.longlian.model.UserFollow;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/12.
 */
public interface UserFollowService {
 

    void insertUserFollow(UserFollow userFollow);

    void followOrConcelFollow(UserFollow userFollow);

    Integer getCount(UserFollow userFollow);

    void updataIsReaderd(long id);


}
