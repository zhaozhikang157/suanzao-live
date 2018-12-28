package com.longlian.live.service.impl;

import com.longlian.live.dao.BankMapper;
import com.longlian.live.service.BankService;
import com.longlian.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/2/25.
 */
@Service("bankService")
public class BankServiceImpl implements BankService{

    @Autowired
    BankMapper bankMapper;

    @Override
    public Bank getBankInfo(long id) {
        return bankMapper.getById(id);
    }
}
