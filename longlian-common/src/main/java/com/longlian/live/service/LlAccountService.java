package com.longlian.live.service;

import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.ActResultDto;
import com.longlian.model.LlAccount;
import com.longlian.model.LlAccountTrack;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2017/4/28.
 */
public interface LlAccountService {
    LlAccount getAccountByUserId(long id);
    LlAccount  getIdRowLockByAccountId(long id);
    AccountAddDelReturn addAccountBalance(long accountId, BigDecimal addMoney, LlAccountTrack accountTrack);
    AccountAddDelReturn delAccountBalance(long accountId, BigDecimal money, LlAccountTrack accountTrack ,  LlAccount queryAccount);
    void addAccount(LlAccount account) ;

    Boolean userCreateAccount(List<Long> list);

    ActResultDto findBalanceIsPay(String courseMoney , long appId);

    ActResultDto checkCode(String code, String mobile);

    ActResultDto resetTradePassword(long accountId, String password);

    ActResultDto checkTradePassword(long accountId, String password);

    ActResultDto sendCheckCode(String mobile);

    int getLlAccountCount();

    List<Long> findAllLlAccount();

}
