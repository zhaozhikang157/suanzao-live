package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.AccountTrackDto;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.AccountTrackService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/2.
 */
@RequestMapping("/accountTrack")
@Controller
public class AccountTrackController {
    private static Logger log = LoggerFactory.getLogger(AccountTrackController.class);

    @Autowired
    AccountTrackService accountTrackService;
    @Autowired
    AccountService accountService;

    /**
     * 收支汇总
     *
     * @return
     */
    @RequestMapping("/showIndex")
    public String showIndex() {
        return "/func/accountTrack/index";
    }

    /**
     * 收支汇总明细
     *
     * @param trackDto
     * @return
     */
    @RequestMapping("/index")
    @ResponseBody
    public DatagridResponseModel getIncomeSummaryPage(DataGridPage requestModel, AccountTrackDto trackDto) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<AccountTrackDto> trackDtoList = accountTrackService.getIncomeSummaryPage(requestModel, trackDto);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 资金记录
     *
     * @return
     */
    @RequestMapping("/showTrackAndOrders")
    public String showTrackAndOrders() {
        return "/func/accountTrack/capital";
    }

    /**
     * 资金记录（分页）
     *
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/trackAndOrders", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel trackAndOrders(DataGridPage requestModel, AccountTrackDto trackDto) {
        DatagridResponseModel drm = new DatagridResponseModel();
        drm.setRows(accountTrackService.getTrackAndOrders(requestModel, trackDto));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 计算 资金池 收支记录 手续费
     *
     * @return
     */
    @RequestMapping(value = "/getCountAccount", method = RequestMethod.POST)
    @ResponseBody
    public Map getCountAccount(String orderNo, String orderType, String bankType,
                               Date startDate, Date endDate, String mobile) {
        return accountTrackService.getCountAccount(orderNo, orderType, bankType, startDate, endDate, mobile);
    }

    /**
     * 导出资金记录
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.GET)
    @ResponseBody
    public void importExcel(HttpServletRequest req, HttpServletResponse response, String orderNo,
                            String orderType, String bankType, Date startDate,
                            Date endDate, String mobile) throws IOException {
        ExportExcelWhaUtil exportExcelWhaUtil = accountTrackService.importExcel(req, response,
                orderNo, orderType, bankType, startDate, endDate, mobile);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=FundStatistics.xls");
            response.setContentType("application/octet-stream; charset=utf-8");

            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 收支详情
     *
     * @param appId
     * @return
     */
    @RequestMapping("/showDetailed")
    public ModelAndView showDetailed(Long appId, String startTime, String endTime) {
        ModelAndView view = new ModelAndView("/func/accountTrack/detailed");
        view.addObject("appId", appId);
        view.addObject("startTime", startTime);
        view.addObject("endTime", endTime);
        return view;
    }

    @RequestMapping(value = "/getDetailedPage", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getDetailedPage(DataGridPage requestModel, String startTime,
                                                 String endTime, Long appId) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<AccountTrackDto> trackDtoList = accountTrackService.getDetailedPage(requestModel, startTime, endTime, appId);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 会员钱包页面
     *
     * @return
     */
    @RequestMapping("/getAppUserAccount")
    public ModelAndView getAppUserAccountsPage() {
        ModelAndView mav = new ModelAndView("/func/accountTrack/wallet");
        return mav;
    }

    /**
     * 会员钱包
     *
     * @return
     */
    @RequestMapping(value = "/getAppUserAccounts", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getAppUserAccounts(DatagridRequestModel requestModel,
                                                    String name, String mobile,Long appId) {
        return accountTrackService.getAppUserAccountsPage(requestModel, name, mobile , appId);
    }

    /**
     * 导出会员钱包
     *
     * @return
     */
    @RequestMapping(value = "/importExcelUserAccounts", method = RequestMethod.GET)
    @ResponseBody
    public void importExcelUserAccounts(HttpServletRequest req, HttpServletResponse response, String name,
                                        String mobile,Long appId) throws IOException {
        ExportExcelWhaUtil exportExcelWhaUtil = accountTrackService.importExcelUserAccounts(req, name, mobile,appId);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=MemberWallet.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出收支汇总明细
     * @param toAccountId
     * @param mobile
     * @param startTime
     * @param endTime
     * @param returnMoneyLevel
     * @param isShow
     * @throws Exception
     */
    @RequestMapping("/importExcelBalanceOfPayments")
    @ResponseBody
    public void importExcelBalanceOfPayments(HttpServletRequest request , HttpServletResponse response , String toAccountId , String mobile ,
                                             String startTime , String endTime , String returnMoneyLevel , String isShow)throws Exception{
        ExportExcelWhaUtil exportExcelWhaUtil = accountTrackService.importExcelBalanceOfPayments(request,
                toAccountId, mobile, startTime, endTime, returnMoneyLevel, isShow);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=MemberWallet.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出收支汇总详情
     * @param request
     * @param response
     * @param appId
     * @param endTime
     * @throws Exception
     */
    @RequestMapping("/importDetails")
    @ResponseBody
    public void importDetails(HttpServletRequest request , HttpServletResponse response ,String appId ,
                                             String startTime , String endTime )throws Exception{
        ExportExcelWhaUtil exportExcelWhaUtil = accountTrackService.importDetails(request, appId, startTime, endTime);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=MemberWallet.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 收支汇总
     * @return
     */
    @RequestMapping("/toReDetail")
    public String toReDetail(){
        return "/func/accountTrack/reDetail";
    }

    /**
     * 收支汇总明细
     * @param requestModel
     * @param trackDto
     * @return
     */
    @RequestMapping("/getReDetailPage")
    @ResponseBody
    public DatagridResponseModel getReDetailPage(DataGridPage requestModel, AccountTrackDto trackDto) {
        DatagridResponseModel drm = new DatagridResponseModel();
        String type = "page";
        List<AccountTrackDto> trackDtoList = accountTrackService.getReDetailPage(requestModel,trackDto , type);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 收支明细导出
     * @param request
     * @param response
     * @param toAccountId
     * @param startTime
     * @param endTime
     * @throws Exception
     */
    @RequestMapping("/importExcelReDetail")
    @ResponseBody
    public void importExcelReDetail(HttpServletRequest request , HttpServletResponse response , String toAccountId , String mobile ,
                                    String startTime , String endTime , String returnMoneyLevel , String isShow)throws Exception {
        ExportExcelWhaUtil exportExcelWhaUtil = accountTrackService.importExcelReDetail(request, toAccountId, mobile,
                startTime, endTime, returnMoneyLevel, isShow);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=MemberWallet.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
