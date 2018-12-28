package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.OrdersDto;
import com.longlian.model.Orders;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/1.
 */
public interface OrdersService {

    void handlerUnionSate(long id, boolean success);

     List<Map> findFailOrders(@Param("map") Map map);

    List<OrdersDto> getWithdrawDepositCheckListPage(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto);

    OrdersDto selectBankOutById(long id);

    ActResult updateAuditStatus(Orders orders) throws Exception;

    String exportExcelBankOutCheck(Map map, HttpServletRequest request) throws Exception;

    /**
     * 电子回单列表
     *
     * @return
     */
    public List<Map> getOrderElectronic(DatagridRequestModel datagridRequestModel,
                                        String orderNo,
                                        String orderType,
                                        Date startDate,
                                        Date endDate,
                                        String mobile);

    OrdersDto selectInfoById(long id);

    ActResult rollback(long optEmpId, Orders orders);

    List<OrdersDto> getCheckRecordListPage(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto);

    String exportExcelBankOutCheckRecord(Map map, HttpServletRequest request) throws Exception;

   Map getInfoBuyCourse(long id);

    int updateOptStatusFail(long id);

    String exportExcelorderElectronic(Map map, HttpServletRequest request) throws Exception;

    List batchPrint(Map map);

    List<Map> getwithdrawalsPage(DatagridRequestModel datagridRequestModel, Map map);

    /**
     * 收入明细
     * @param requestModel
     * @param trackDto
     * @return
     */
    List<OrdersDto> findIncomePage(DataGridPage requestModel, OrdersDto trackDto);

    /**
     * 收入总和
     * @param trackDto
     * @return
     */
    BigDecimal findIncomeCount(OrdersDto trackDto);

    /**
     * 支出明细
     * @param requestModel
     * @param trackDto
     * @return
     */
    List<OrdersDto> findExpenditurePage(DataGridPage requestModel, OrdersDto trackDto);


    /**
     * 支出总和
     * @param trackDto
     * @return
     */
    BigDecimal findExpenditureCount(OrdersDto trackDto);

    ExportExcelWhaUtil importExcelExpenditue(HttpServletRequest request, String appId, String appMobile, String startDate, String endDate);

    ExportExcelWhaUtil importExcelIncome(HttpServletRequest request, String appId, String appMobile,
                                         String startDate, String endDate, String bankType);

    Long findCourseId(long orderId);

    Map getCourseIds();
    /**
     * 查询审核通过待到账的订单，定时去扫描，到7天则自动到账
     * @return
     */
    List<Orders> findWithDrawList();
}
