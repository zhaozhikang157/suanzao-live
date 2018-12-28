package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.token.AppUserIdentity;

import java.math.BigDecimal;

/**
 * Created by admin on 2016/8/19.
 */
public interface WithdrawalsService {

    ActResultDto bankOutOption(AppUserIdentity token, BigDecimal amount, Long cardId, String tradePassword,String type);

    ActResultDto findAccountBalance(long id, String cardId);
}
