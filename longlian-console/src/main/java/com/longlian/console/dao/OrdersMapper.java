package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.OrdersDto;
import com.longlian.model.Orders;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/1.
 */
public interface OrdersMapper {
    Orders selectById(@Param("id") long id);

    Orders selectLockById(@Param("id") long id);

    void handlerAuditStatusUnionStat(@Param("orders") Orders orders);

    public List<Map> findFailOrders(@Param("map") Map map);


    List<OrdersDto> getWithdrawDepositCheckListPage(@Param("page") DatagridRequestModel datagridRequestModel, @Param("ordersDto") OrdersDto ordersDto);

    OrdersDto selectBankOutById(@Param("id") long id);

    void updateAuditStatus(@Param("orders") Orders orders);

    void updateAuditStatusUnionStat(@Param("orders") Orders orders);

    List<Map> getBankOutCheckList(@Param("map") Map map);
    /**
     * 电子回单列表
     *
     * @param page
     * @return
     */
    List<Map> getOrderElectronicPage(@Param(value = "page") DatagridRequestModel page,
                                     @Param(value = "orderNo") String orderNo,
                                     @Param(value = "orderType") String orderType,
                                     @Param(value = "startDate") Date startDate,
                                     @Param(value = "endDate") Date endDate,
                                     @Param(value = "mobile") String mobile);

    OrdersDto selectInfoById(long id);

    void updateOptStatusById(@Param("id") long id, @Param("optStatus") String optStatus);

    List<OrdersDto> getCheckRecordListPage(@Param("page") DatagridRequestModel datagridRequestModel, @Param("ordersDto") OrdersDto ordersDto);

    List<Map> getBankOutCheckRecordList(@Param("map") Map map);

    Map getInfoBuyCourse(long id);

    int updateOptStatusFail(long joinCourseId);

    List<Map> getOrderElectronic(@Param("map")Map map);

    List<Map> getwithdrawalsPage(@Param("page")DatagridRequestModel requestModel,@Param("map")Map map);

    /**
     * 收入明细
     * @param datagridRequestModel
     * @param ordersDto
     * @return
     */
    List<OrdersDto> findIncomePage(@Param("page") DatagridRequestModel datagridRequestModel,
                                   @Param("ordersDto") OrdersDto ordersDto);

    /**
     * 总收入
     * @param ordersDto
     * @return
     */
    BigDecimal findIncomeCount(@Param("ordersDto") OrdersDto ordersDto);

    /**
     * 收入明细 - 导出
     * @param ordersDto
     * @return
     */
    List<Map> findIncome(@Param("ordersDto") OrdersDto ordersDto);

    /**
     * 支出明细
     * @param datagridRequestModel
     * @param ordersDto
     * @return
     */
    List<OrdersDto> findExpenditurePage(@Param("page") DatagridRequestModel datagridRequestModel,
                                   @Param("ordersDto") OrdersDto ordersDto);

    /**
     * 支出明细 - 总和
     * @param ordersDto
     * @return
     */
    BigDecimal findExpenditureCount(@Param("ordersDto") OrdersDto ordersDto);

    /**
     * 支出明细 导出
     * @param ordersDto
     * @return
     */
    List<Map> findExpenditure(@Param("ordersDto") OrdersDto ordersDto);

    Long findCourseId(long orderId);

    List<Map> findCourseIds();

    /**
     * 订单提现审核操作，不操作转账，改为审核通过延迟7天到账
     * @param order
     * @return
     */
    int updateOrderAuditById(@Param("order") Orders order);

    /**
     * 查询审核通过待到账的订单，定时去扫描，到7天则自动到账
     * @return
     */
    List<Orders> findWithDrawList();

    /**
     * 提现延迟天数
     * @return
     */
    String getWithDrawTime();
}
