package com.longlian.live.service;


import com.huaxin.util.ActResult;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.ActResultDto;
import com.longlian.model.Account;
import com.longlian.model.AccountTrack;
import com.longlian.model.Course;
import com.longlian.model.Orders;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2016/8/1.
 */
public interface AccountService {
    Account getAccountByUserId(long id);
    Account  getIdRowLockByAccountId(long id);
    AccountAddDelReturn addThirdPayZiJinChi(long accountId, BigDecimal addMoney, long orderId, String payType);
    AccountAddDelReturn delZiJinChiBalance(long accountId,BigDecimal addMoney ,long orderId ,String payType );
    AccountAddDelReturn addAccountBalance(long accountId, BigDecimal addMoney, AccountTrack accountTrack);
    AccountAddDelReturn delAccountBalance(long accountId, BigDecimal money, AccountTrack accountTrack);
    void addAccount(Account account) ;
    ActResultDto resetTradePassword(long accountId, String password);
    ActResultDto checkTradePassword(long accountId, String password);
    ActResultDto sendCheckCode(String mobile);
    ActResultDto checkCode(String code, String mobile);
    ActResultDto forgetTradePwd(long accountId);
    boolean checkPassword(long accountId, String password);

    boolean teachCourseReward(Course course,int payMenCount )throws Exception;
    boolean invicationTeachReward(long invitationAppId ,long teachAppId ,int payMenCount )throws Exception;
    boolean teachFollowReward(long teachAppId, long followRewardId , BigDecimal amount ,String remark);
    Account getAccountByAppId(long appId);
    ActResult accountReward(Orders orders);
    ActResultDto findBalanceIsPay(String courseMoney , long appId);

    boolean newTeachCourseReward(Course course , boolean isNeedAddBaseNum,Long payMenCount)throws Exception;

    ActResultDto transforZb2Xb(long appId, String amount);

    List getAccountTransRecord(long appId, Integer pageNum, Integer pageSize);

    String getIsZbTransXb();

}
