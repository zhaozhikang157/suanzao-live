package com.longlian.console.service.impl;

import com.longlian.console.service.LongLianRewardService;
import com.longlian.console.service.OrdersService;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.util.SystemParaRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 龙链奖励
 * Created by Administrator on 2017/2/25.
 */
@Service("longLianRewardService")
public class LongLianRewardServiceImpl implements LongLianRewardService{

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    @Autowired
    EndLiveService endLiveService;

    @Autowired
    AccountService accountService;

    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    @Autowired
    OrdersService ordersService;

    /**
     * 推荐老师 奖励
     * @param invitationAppid
     * @param teachAppId
     * @param firstPayMenCount
     * @throws Exception
     */
    @Override
    public void invitationTeachReward(long invitationAppid , long teachAppId ,int firstPayMenCount) throws Exception {
        accountService.invicationTeachReward(invitationAppid ,teachAppId , firstPayMenCount);
    }

    /**
     * 老师粉丝关注奖励
     * @param teachAppId
     * @param followRewardId
     */
    @Override
    public void teachFollowReward(long teachAppId, long followRewardId  , BigDecimal amount ,String remark) {
        accountService.teachFollowReward( teachAppId,  followRewardId ,amount, remark);
    }

    /**
     * 处理支付中的订单  时间过长自动为支付失败
     * @param id
     */
    @Override
    public void handlerPayingJoinCourse(long id) {
        //更新改报名记录失败
        int count = joinCourseRecordService.handlerPayingJoinCourse(id);
        if(count > 0){
            //更新订单也为失败
            int oCount = ordersService.updateOptStatusFail(id);
        }
    }
}
