package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.BankCard;

/**
 * Created by admin on 2017/2/25.
 */
public interface BankCardService {

    ActResultDto getMyBank(long appId);

    ActResultDto insertBankCard(BankCard bankCard);

    ActResultDto findBankCardInfo(Long id);

    ActResultDto delBankCardById(Long id,Long appId);

    ActResultDto getAllBank();

    ActResultDto findBankCardByCardNo(String cardNo);
}
