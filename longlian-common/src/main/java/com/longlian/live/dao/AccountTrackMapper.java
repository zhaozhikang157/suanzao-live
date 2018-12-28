package com.longlian.live.dao;


import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.AccountTrackDto;
import com.longlian.model.Account;
import com.longlian.model.AccountTrack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/13.
 */
@Mapper
public interface AccountTrackMapper {

    //Account getAccountByAppId(long id);

    void insert(AccountTrack accountTrack);

    /**
     * 收益记录
     *
     * @param id
     * @return
     */
    List<Map> getProfit(long id);

    Map getYearMonthWeekDateOamount(@Param("map") Map map);
    /**
     * 我的钱包收支明细
     *
     * @param id
     * @return
     */
    List<Map> getWalletsPage(@Param("id") long id , @Param("page") DataGridPage dg);

    List<Map> getDistributionSortByCourseId(long courseId);

    List<Map> getDistributionMasterListPage(@Param("page") DataGridPage dg, @Param("courseId") long courseId);

    List<Map> findAllWallet(long appId);

    BigDecimal findTodayAccount(long appId);

    List<Map> findTodayWalletByTypePage(@Param("type")Integer type,@Param("page") DataGridPage page);

    BigDecimal findAccountMoney(long appid) ;

    List<AccountTrackDto> getIncomeSummaryPage(@Param("page")DataGridPage dataGridPage ,
                                               @Param("accountTrackDto")AccountTrackDto accountTrackDto);
    Map getIncomeSummaryTotalCount(@Param("accountTrackDto")AccountTrackDto accountTrackDto);
    List<AccountTrackDto> getIncomeSummary(@Param("accountTrackDto")AccountTrackDto accountTrackDto);


    List<Map> findAllBanlanceByTime(Date endTime);

    /**
     *资金池 收支记录
     * @param page
     * @return
     */
    List<Map> getTrackAndOrdersPage(@Param(value = "page") DataGridPage page,
                                    @Param(value = "ziJinChiAccountId")long ziJinChiAccountId,
                                    @Param(value = "trackDto") AccountTrackDto trackDto);

    /**
     * 计算资金池总收入----WHA ADD
     *
     * @param id
     * @return
     */
    BigDecimal getCountOamount(@Param(value = "id") long id, @Param(value = "orderNo") String orderNo,
                               @Param(value = "orderType") String orderType, @Param(value = "bankType") String bankType,
                               @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                               @Param(value = "mobile") String mobile);

    /**
     * 计算资金池总支出----WHA ADD
     *
     * @param id
     * @return
     */
    BigDecimal getCountTamount(@Param(value = "id") long id, @Param(value = "orderNo") String orderNo,
                               @Param(value = "orderType") String orderType, @Param(value = "bankType") String bankType,
                               @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                               @Param(value = "mobile") String mobile);

    /**
     * WHA----369资金池 收支记录(不分页 导出数据使用)
     *
     * @param id
     * @return
     */
    List<Map> getTrackAndOrders(@Param(value = "id") long id, @Param(value = "orderNo") String orderNo,
                                @Param(value = "orderType") String orderType, @Param(value = "bankType") String bankType,
                                @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                                @Param(value = "mobile") String mobile);

    List<AccountTrackDto> getDetailedPage(@Param("page")DataGridPage page,@Param("startTime")Date start,
                                          @Param("endTime")Date end , @Param("appId")Long appId);

    /**
     * 计算资金池总支出----WHA ADD
     *
     * @param id
     * @return
     */
    BigDecimal getCountCharge(@Param(value = "id") long id, @Param(value = "orderNo") String orderNo,
                              @Param(value = "orderType") String orderType, @Param(value = "bankType") String bankType,
                              @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                              @Param(value = "mobile") String mobile);

    /**
     * 计算资金池总支出----WHA ADD
     *
     * @param id
     * @return
     */
    BigDecimal getCountllCharge(@Param(value = "id") long id, @Param(value = "orderNo") String orderNo,
                                @Param(value = "orderType") String orderType, @Param(value = "bankType") String bankType,
                                @Param(value = "startDate") Date startDate,
                                @Param(value = "endDate") Date endDate, @Param(value = "mobile") String mobile);


    List<AccountTrack> getListByOrderId(long orderId);

    List<Map> importExcelBalanceOfPayments(@Param(value = "accountTrackDto")AccountTrackDto accountTrackDto);

    List<Map> importDetails(@Param("startTime")Date start, @Param("endTime")Date end , @Param("appId")Long appId);

    List<AccountTrackDto> getReDetailPage(@Param("page")DataGridPage page,
                                          @Param("appIds")String appIds,
                                          @Param("accountTrackDto")AccountTrackDto accountTrackDto);

    List<AccountTrackDto> getImportReDetail(@Param("appIds") String appIds ,
                                            @Param("accountTrackDto")AccountTrackDto accountTrackDto);

    BigDecimal findAuditing(long appId);

    BigDecimal findAuditSuccess(long appId);

    List<Map> getproxyIncomePage(@Param("page")DataGridPage page , @Param("appId")Long appId);

    List<Map> getWalletsPageNew(@Param("id") long id ,@Param("returnMoneyLevel")Integer returnMoneyLevel, @Param("page") DataGridPage dg);

}