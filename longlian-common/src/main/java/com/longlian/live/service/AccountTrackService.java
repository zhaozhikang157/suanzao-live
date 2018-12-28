package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.AccountTrackDto;
import com.longlian.model.AccountTrack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/8/18.WHA
 */
public interface AccountTrackService {
    /**
     * 收益记录
     * @param id
     * @return
     */
    public List<Map> getProfit(long id);

    List<Map> getWalletsPage(long id , Integer pageSize , Integer offset);

    
    /**
     * 查询明细
     * @param id
     * @param returnMoneyLevel 明细类型
     * @param pageSize
     * @param offset
     * @return
     */
    List<Map> getWalletsPageNew(long id , Integer returnMoneyLevel , Integer pageSize , Integer offset);


    List<Map>  getDistributionSortByCourseId(long courseId);

    List<Map> getDistributionMasterList(Long courseId, Integer pageNum, Integer pageSize);

    Map findAllWallet(long appId);

    Map findTodayWallet(long appId);

    List findTodayWalletByType(Integer type,Integer pageNum,Integer pageSize);

    List<AccountTrackDto> getIncomeSummaryPage(DataGridPage page , AccountTrackDto trackDto);

    public List<Map> getTrackAndOrders(DataGridPage page, AccountTrackDto trackDto);

    /**
     * 计算 资金池 收支记录 手续费
     * @return
     */
    public Map getCountAccount(String orderNo, String orderType, String bankType,
                               Date startDate, Date endDate, String mobile);

    /**
     * 导出资金记录
     * @return
     */
    public ExportExcelWhaUtil importExcel(HttpServletRequest req,HttpServletResponse response,String orderNo,
                                          String orderType, String bankType, Date startDate,
                                          Date endDate, String mobile);

    List<AccountTrackDto> getDetailedPage(DataGridPage requestModel,String startTime , String endTime , Long appId);

    /**
     * 会员钱包
     * @param requestModel
     * @param name
     * @param mobile
     * @return
     */
    DatagridResponseModel getAppUserAccountsPage(DatagridRequestModel requestModel,String name,String mobile,Long appId);

    /**
     * 导出会员钱包
     * @return
     */
    public ExportExcelWhaUtil importExcelUserAccounts(HttpServletRequest req,String name, String mobile,Long appId);
    public List<Map> getBankType();

    List<AccountTrack> getListByOrderId(long orderId);

    ExportExcelWhaUtil importExcelBalanceOfPayments(HttpServletRequest request ,  String toAccountId , String mobile ,
                                                    String startTime , String endTime ,
                                                    String returnMoneyLevel , String isShow);

    ExportExcelWhaUtil importDetails(HttpServletRequest request ,  String appId , String startTime , String endTime);

    List<AccountTrackDto> getReDetailPage(DataGridPage requestModel, AccountTrackDto trackDto,String type);


    ExportExcelWhaUtil importExcelReDetail(HttpServletRequest request ,  String toAccountId , String mobile ,
                                                    String startTime , String endTime ,
                                                    String returnMoneyLevel , String isShow);

    List<Map> getproxyIncomePage(Integer offset , Integer pageSize , Long appId);


    Map findTodayWalletNew(long id);
}
