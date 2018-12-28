package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.Account;
import com.longlian.model.AccountTransRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/13.
 */
@Mapper
public interface AccountMapper {

    Account getById(long id);
    Account getAccountByAppId(long id);
    int addUpdateByAccountId(Account account);
    int delUpdateByAccountId(Account account);

    int  updateTrackIdByAccountId(Account account);
    Account  getIdRowLockByAccountId(long id);

    void add(Account account);

    int resetTradePassword(@Param("id") long id, @Param("tradePwd") String tradePwd);

    int forgetTradePwd(long id);

    /**
     * WHA ADD --- 会员钱包
     * @return
     */
    List<Map> getAppUserAccountsPage(@Param("page")DatagridRequestModel requestModel, @Param("name")String name,
                                     @Param("mobile")String mobile,@Param("appId")Long appId);

    BigDecimal getSumAmount( @Param("appID")long appID);

    /**
     * 会员钱包-提现总额
     * @param id
     * @return
     */
    Map sumWithdrawDeposit( @Param("id")long id);

    /**
     * 会员钱包-提现记录
     * @param id
     * @return
     */
    List<Map> withdrawDeposit( @Param("id")long id);

    List<Map> membershiprebate(@Param("id")long id,@Param("map")Map map);

    /**
     * 会员钱包（不分页 导出数据使用）
     * @return
     */
    List<Map>  getAppUserAccounts(@Param("name")String name,@Param("mobile")String mobile,@Param("appId")Long appId);

    BigDecimal findAmountAllIng(@Param("appId")long appId,@Param("nowTime")String dataTime);

    List<Long> findAvatars();

    void addNoExitAppIds(List<Long> list);

    void addAppId(Long id);

    /**
     * 枣币减少
     * @return
     */
    int updateZbDown(@Param("appId") long appId, @Param("amount") BigDecimal amount);

    /**
     * 学币增加
     * @param appId
     * @param amount
     * @return
     */
    int updateXbUp(@Param("appId") long appId, @Param("amount") BigDecimal amount);

    /**
     * 记录枣币转换学币记录
     * @param record
     */
    void createAccountTransRecord(@Param("record") AccountTransRecord record);

    /**
     * 分页获取枣币转换学币记录
     * @param appId
     * @param dg
     * @return
     */
    List<AccountTransRecord> getAccountTransRecordPage(@Param("appId") long appId, @Param("page") DataGridPage dg);

    /**
     * 是否枣币兑换学币
     */
    String getIsZbTransXb();
}
