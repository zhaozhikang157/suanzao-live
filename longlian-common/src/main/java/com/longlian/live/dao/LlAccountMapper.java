package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.LlAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/28.
 */
@Mapper
public interface LlAccountMapper {

    LlAccount getById(long id);
    LlAccount getAccountByAppId(long id);
    int addUpdateByAccountId(LlAccount account);
    int delUpdateByAccountId(LlAccount account);
    int  updateTrackIdByAccountId(LlAccount account);
    LlAccount  getIdRowLockByAccountId(long id);
    void add(LlAccount account);
    List<Long> findAllLLAccountIds();
    void addNoExitAppIds(List<Long> list);
    int getLlAccountCount();
    void addAppId(Long id);

}
