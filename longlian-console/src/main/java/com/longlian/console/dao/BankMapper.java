package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.BankDto;
import com.longlian.model.Bank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by U on 2016/8/1.
 */
public interface BankMapper {

    List<BankDto> getListPage(@Param("page") DatagridRequestModel requestModel, @Param("bankDto") BankDto bankDto);
    List<Bank> getList4App();
    void update(@Param("bank") Bank bank);

    void create(@Param("bank") Bank bank);

    Bank selectById(@Param("id") long id);

    void setStatusForbidden(@Param("id") long id);

    void setStatusStart(@Param("id") long id);

    List<Bank> getBankNameList();
}
