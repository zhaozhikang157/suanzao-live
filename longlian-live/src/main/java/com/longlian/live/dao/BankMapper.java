package com.longlian.live.dao;

import com.longlian.dto.BankCardDto;
import com.longlian.model.Bank;
import com.longlian.model.BankCard;

import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */
public interface BankMapper {


    List<Bank> getAllBank();

    Bank getById(long id);
}
