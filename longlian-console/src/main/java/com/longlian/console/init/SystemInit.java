package com.longlian.console.init;

import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.RewardRecordService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 * Created by syl on 2017/2/25.
 */
@Component("systemInit")
public class SystemInit implements InitializingBean {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RewardRecordService rewardRecordService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化老师粉丝关注奖励记录存入redis
        rewardRecordService.getFollowRewardReSetRedisInit();
    }
}
