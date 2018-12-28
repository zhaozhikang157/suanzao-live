package com.longlian.console.service;

import com.longlian.model.Course;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/25.
 */
public interface LongLianRewardService {

    void invitationTeachReward(long invitationAppid, long teachAppId, int firstPayMenCount) throws Exception;

    void teachFollowReward(long teachAppId, long followRewardId, BigDecimal amount, String remark) ;


     //void  handlerPayingJoinCourse(long id);

}
