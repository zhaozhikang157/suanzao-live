package com.longlian.mq.service.impl;

import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.model.UserFollow;
import com.longlian.mq.dao.UserFollowMqMapper;
import com.longlian.mq.service.UserFollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhan on 2017/2/12.
 */
@Service("userFollowServiceMq")
public class UserFollowServiceImpl implements UserFollowService {
    private static Logger log = LoggerFactory.getLogger(UserFollowServiceImpl.class);
    @Autowired
    UserFollowMqMapper userFollowMqMapper;
    @Autowired
    WeixinUtil weixinUtil;

    /**
     * 保存用户关注
     * @param userFollow
     */
    @Override
    public void insertUserFollow(UserFollow userFollow) {
        userFollowMqMapper.insert(userFollow);
    }

    /**
     * 关注&取消关注
     * @param userFollow
     */
    @Override
    public void followOrConcelFollow(UserFollow userFollow) {
        userFollowMqMapper.update(userFollow);
    }

    /**
     * 查询用户是否曾关注直播间
     * @param userFollow
     * @return
     */
    @Override
    public Integer getCount(UserFollow userFollow) {
        return userFollowMqMapper.getCountByUserIdAndRoomId(userFollow.getAppId() , userFollow.getRoomId());
    }

    /**
     * 修改关注未已看状态
     * @param id
     */
    public void  updataIsReaderd(long id){
        userFollowMqMapper.updataIsReaderd(id);
    }

}