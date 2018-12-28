package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.BankDto;
import com.longlian.model.Bank;

import java.util.List;

/**
 * Created by U on 2016/8/1.
 */
public interface BankService {
    List<BankDto> getListPage(DatagridRequestModel requestModel, BankDto bankDto);

    void create(Bank bank);

    void update(Bank bank);

    void setStatusForbidden(long id);

    void setStatusStart(long id);

    Bank selectById(long id);

    List<Bank> getBankNameList();
}
