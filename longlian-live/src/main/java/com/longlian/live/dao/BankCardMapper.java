package com.longlian.live.dao;

import com.longlian.dto.BankCardDto;
import com.longlian.model.Bank;
import com.longlian.model.BankCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */
public interface BankCardMapper {

    List<BankCardDto> findCardsByAppId(long appId);

    void insertBankCard(BankCard bankCard);

    BankCardDto getBankCardById(long id);

    void deleteByPrimaryKey(long id);

    List<Bank> getAllBank();

    BankCard findByAppIdAndCardId(@Param("cardId")long cardId,@Param("appId")long appId);

    List<BankCardDto> findBankCardByCardNo(@Param("cardNo") String cardNo);
}
