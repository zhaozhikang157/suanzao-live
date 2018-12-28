package com.longlian.console.service;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/2/25.
 */
public interface LongLianRewardService {

    void invitationTeachReward(long invitationAppid, long teachAppId, int firstPayMenCount) throws Exception;

    void teachFollowReward(long teachAppId, long followRewardId, BigDecimal amount, String remark) ;


     void  handlerPayingJoinCourse(long id);

}
