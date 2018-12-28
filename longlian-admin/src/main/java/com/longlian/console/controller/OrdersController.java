package com.longlian.console.controller;

import chinapay.Base64;
import chinapay.PrivateKey;
import chinapay.SecureLink;
import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.bank.TransactionBean;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.OrdersService;
import com.longlian.dto.OrdersDto;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.model.Orders;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import com.longlian.type.PayType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/1.
 */
@Controller
@RequestMapping("/ordersController")
public class OrdersController {
    private static Logger log = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    OrdersService ordersService;
    @Autowired
    CourseService courseService;

    /**
     * 提现审核页面
     *
     * @return
     */
    @RequestMapping("/toWithdrawDepositCheckIndex")
    public ModelAndView toWithdrawDepositCheckIndex() {
        ModelAndView model = new ModelAndView("/func/orders/toWithdrawDepositCheckIndex");
        TransactionBean pay = balanceQuery();

        BigDecimal b1 = new BigDecimal(pay.getMerAmt());
        BigDecimal b2 = new BigDecimal(100);

        BigDecimal ff = b1.divide(b2, 2, RoundingMode.HALF_UP);

        model.addObject("meramt", ff.doubleValue());
        return model;
    }

    /**
     * 获取提现审核列表
     *
     * @param datagridRequestModel
     * @param ordersDto
     * @return
     */
    @RequestMapping(value = "/getWithdrawDepositCheckList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getWithdrawDepositCheckList(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto) {
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        datagridResponseModel.setRows(ordersService.getWithdrawDepositCheckListPage(datagridRequestModel, ordersDto));
        datagridResponseModel.setTotal(datagridRequestModel.getTotal());
        return datagridResponseModel;
    }


    /**
     * 跳转至会员提现审核页面
     *
     * @return
     */
    @RequestMapping("/toAudit")
    public ModelAndView toAudit(String id) {
        ModelAndView model = new ModelAndView("/func/orders/audit");
        model.addObject("id", id);
        return model;
    }


    @RequestMapping(value = "/selectBankOutById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult selectBankOutById(long id) {
        ActResult actResult = new ActResult();
        actResult.setData(ordersService.selectBankOutById(id));
        return actResult;
    }


    /***
     * 审核（更新审核状态）
     *
     * @param id
     * @param auditStatus 审核状态   1:通过； 2：不通过
     * @param auditAgreed 审核意见
     * @return
     */
    @RequestMapping(value = "/updateAuditStatus", method = RequestMethod.POST)
    @ResponseBody
    @Log(content = "{logModel.optTypeDesc},订单号：{logModel.orderNo}，支付描述:{#.success},{#.msg},{#.data}", type = LogType.console_bankout_audit, systemType = "1")
    public ActResult updateAuditStatus(HttpServletRequest request, long id, int auditStatus, String auditAgreed, String orderNo) {
        ActResult actResult = new ActResult();
        try {
            if (id > 0) {
                ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
                Orders orders = new Orders();
                orders.setId(id);
                orders.setOrderNo(orderNo);
                orders.setAuditorId(token.getId());
                orders.setAuditStatus(auditStatus);
                orders.setAuditTime(new Date());
                orders.setAuditAgreed(auditAgreed);
                actResult = ordersService.updateAuditStatus(orders);
                //发送日志
                sendSystemLog(orders);
                return actResult;
            } else {
                actResult.setSuccess(false);
                actResult.setMsg("审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("审核失败");
        }
        return actResult;
    }

    /**
     * 发送日志
     *
     * @param o
     */
    public void sendSystemLog(Orders o) {
        RequestInfoContext.getRequestInfo().setIsCreateLog(true);
        Map mapP = new HashMap();
        if ("1".equals(o.getAuditStatus())) {
            mapP.put("optTypeDesc", "银联提现审核:通过操作");
        } else {
            mapP.put("optTypeDesc", "银联提现审核:不通过");
        }
        mapP.put("orderNo", "\"" + o.getOrderNo() + "\"");
        RequestInfoContext.getRequestInfo().setTableId(o.getId());
        RequestInfoContext.getRequestInfo().setObject(o.getOrderNo());
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
    }


    public static TransactionBean balanceQuery() {

        TransactionBean pay = new TransactionBean();
        try {
            String path = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERPRKKEY_FILE");//"classpath:yinlianAssets/MerPrK_808080211303632_20160429151236.key";
            String MerKeyPath = ResourceUtils.getFile(path).getPath();
            String pubpath = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_PGPUBKKEY_FILE");//"classpath:yinlianAssets/PgPubk.key";
            String PubKeyPath = ResourceUtils.getFile(pubpath).getPath();
            String pay_url = "http://sfj.chinapay.com/dac/BalanceQueryGBK";
            String merId = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERID");//"808080211303632";
            String version = "20090501";
            String signFlag = "1";
            String Data = merId + version;
            log.info("字符串数据拼装结果：" + Data);
            String plainData = new String(Base64.encode(Data.getBytes()));
            log.info("转换成Base64后数据：" + plainData);

            log.info("====================>>>>>备付金余额查询--start");
            String chkValue = null;
            int KeyUsage = 0;
            PrivateKey key = new PrivateKey();
            key.buildKey(merId, KeyUsage, MerKeyPath);
            SecureLink sl = new SecureLink(key);
            chkValue = sl.Sign(plainData);
            log.info("签名内容:" + chkValue);
            log.info("====================>>>>>备付金余额查询--end");

            HttpClient httpClient = new HttpClient();
            log.info("HttpClient方法创建！");
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
            String url = pay_url;
            System.out.println(url);
            PostMethod postMethod = new PostMethod(url);
            log.info("Post方法创建！");
            //填入各个表单域的值
            NameValuePair[] data = {
                    new NameValuePair("merId", merId),
                    new NameValuePair("version", version),
                    new NameValuePair("chkValue", chkValue),
                    new NameValuePair("signFlag", signFlag)
            };

            // 将表单的值放入postMethod中
            postMethod.setRequestBody(data);
            // 执行postMethod
            httpClient.executeMethod(postMethod);
            // 读取内容
            InputStream resInputStream = null;
            try {
                resInputStream = postMethod.getResponseBodyAsStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 处理内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(resInputStream));
            String tempBf = null;
            StringBuffer html = new StringBuffer();
            while ((tempBf = reader.readLine()) != null) {

                html.append(tempBf);
            }
            String resMes = html.toString();
            log.info("备付金余额查询返回报文：" + resMes);
            int dex = resMes.lastIndexOf("|");
            String Res_Code = resMes.substring(0, 3);

            //提取返回数据
            if (Res_Code.equals("000")) {
                String Res_merId = resMes.substring(4, 19);
                String Res_merAmt = resMes.substring(20, dex);
                String Res_chkValue = resMes.substring(dex + 1);

                log.info("Res_Code=" + Res_Code);
                log.info("Res_merId=" + Res_merId);
                log.info("Res_merAmt=" + Res_merAmt);
                log.info("Res_chkValue=" + Res_chkValue);

                String plainData1 = resMes.substring(0, dex + 1);
                log.info("需要验签的字段：" + plainData1);
                String plainData2 = new String(Base64.encode(plainData1.getBytes()));
                log.info("转换成Base64后数据：" + plainData2);

                pay.setResponseCode(Res_Code);
                pay.setMerId(Res_merId);
                pay.setMerAmt(Res_merAmt);
                /*pay.setChkValue(Res_chkValue);*/
                pay.setData(resMes);

                //对收到的ChinaPay应答传回的域段进行验签
                boolean buildOK = key.buildKey("999999999999999", KeyUsage, PubKeyPath);

                if (!buildOK) {
                    log.info("银联应答字段验签失败!");
                }
                boolean res = sl.verifyAuthToken(plainData2, Res_chkValue);
                System.out.println(res);
                if (res) {
                    log.info("验签数据正确!");
                } else {
                    log.info("签名数据不匹配！");
                }
            } else {
                String Res_chkValue = resMes.substring(dex + 1);

                log.info("Res_Code=" + Res_Code);
                log.info("Res_chkValue=" + Res_chkValue);

                String plainData1 = resMes.substring(0, dex + 1);
                log.info("需要验签的字段：" + plainData1);
                String plainData2 = new String(Base64.encode(plainData1.getBytes()));
                log.info("转换成Base64后数据：" + plainData2);


                pay.setResponseCode(Res_Code);
                pay.setData(resMes);


                //对收到的ChinaPay应答传回的域段进行验签
                boolean buildOK = key.buildKey("999999999999999", KeyUsage, PubKeyPath);

                if (!buildOK) {
                    log.info("银联应答字段验签失败!");
                }
                boolean res = sl.verifyAuthToken(plainData2, Res_chkValue);
                if (res) {
                    log.info("验签数据正确!");
                } else {
                    log.info("签名数据不匹配！");
                }
            }

        } catch (Exception e) {
            log.error("", e);
        }
        return pay;
    }


    /**
     * 提现审核页面导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelBankOutCheck")
    public void exportExcelBankOutCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("createTimeBegin", request.getParameter("createTimeBegin"));
            map.put("createTimeEnd", request.getParameter("createTimeEnd"));
            map.put("bankName", request.getParameter("bankName"));
            String path = ordersService.exportExcelBankOutCheck(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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
     * 电子回单
     *
     * @return
     */
    @RequestMapping("/orderElectronic")
    public ModelAndView orderElectronic() {
        ModelAndView mav = new ModelAndView("/func/orders/orderElectronic");
        return mav;
    }

    /**
     * 电子回单记录（分页）
     *
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/getOrderElectronic", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel orderElectronic(DatagridRequestModel requestModel,
                                                 String orderNo,
                                                 String orderType,
                                                 Date startDate,
                                                 Date endDate,
                                                 String mobile) {
        DatagridResponseModel drm = new DatagridResponseModel();
        drm.setRows(ordersService.getOrderElectronic(requestModel,
                orderNo,
                orderType,
                startDate,
                endDate, mobile));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }


    /**
     * 电子回单打印
     *
     * @param orderType 订单类型  0-购买课程 1-提现 2-充值
     * @return
     */
    @RequestMapping("/orderElectronicPrint")
    public ModelAndView orderElectronicPrint(String orderType, long id) {
        ModelAndView mav = null;
        if ("0".equals(orderType)) {
            mav = new ModelAndView("/func/orders/buyCoursePrint");
            Map map = ordersService.getInfoBuyCourse(id);
            mav.addObject("order", map);
        } else if ("1".equals(orderType)) {
            mav = new ModelAndView("/func/orders/withdrawalsPrint");
            OrdersDto ordersDto = ordersService.selectInfoById(id);
            mav.addObject("order", ordersDto);
        }
        return mav;
    }

    /**
     * 提现记录页面
     *
     * @return
     */
    @RequestMapping("/toCheckRecord")
    public ModelAndView toCheckRecord() {
        return new ModelAndView("/func/orders/toCheckRecord");
    }


    /**
     * 已通过提现记录页面
     *
     * @return
     */
    @RequestMapping("/toCheckRecordByAuditStatusPass")
    public ModelAndView toCheckRecordByAuditStatusPass() {
        ModelAndView model = new ModelAndView("/func/orders/checkRecordByPass");
        return model;
    }

    /**
     * 返钱
     *
     * @param orders
     * @return
     */
    @RequestMapping(value = "/rollback", method = RequestMethod.POST)
    @ResponseBody
    public ActResult rollback(HttpServletRequest request, Orders orders) {
        ActResult actResult = new ActResult();
        if (orders.getId() > 0) {
            ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            actResult = ordersService.rollback(token.getId(), orders);
        } else {
            actResult.setMsg("参数为空");
            actResult.setSuccess(false);
        }
        return actResult;
    }


    /**
     * 未通过提现记录页面
     *
     * @return
     */
    @RequestMapping("/toCheckRecordByAuditStatusNoPass")
    public ModelAndView toCheckRecordByAuditStatusNoPass() {
        ModelAndView model = new ModelAndView("/func/orders/checkRecordByNoPass");
        return model;
    }

    /**
     * 获取审核记录
     *
     * @param datagridRequestModel
     * @param ordersDto
     * @return
     */
    @RequestMapping(value = "/getCheckRecordList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCheckRecordList(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(ordersService.getCheckRecordListPage(datagridRequestModel, ordersDto));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 提现记录（成功、失败）导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelBankOutCheckRecord")
    public void exportExcelBankOutCheckRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("auditStatus", request.getParameter("auditStatus"));
            map.put("createTimeBegin", request.getParameter("createTimeBegin"));
            map.put("createTimeEnd", request.getParameter("createTimeEnd"));
            map.put("bankName", request.getParameter("bankName"));
            map.put("orderNo", request.getParameter("orderNo"));
            map.put("appMobile", request.getParameter("appMobile"));
            map.put("optStatus", request.getParameter("optStatus"));
            map.put("optStatusSrt", request.getParameter("optStatusSrt"));
            String path = ordersService.exportExcelBankOutCheckRecord(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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
     * 获取所有的支付类型
     * @return
     */
    @RequestMapping("/getAllBankType")
    @ResponseBody
    public ActResult getAllBankType(){
        ActResult result = new ActResult();
        List<Map> list = PayType.getList();
        result.setData(list);
        return result;
    }


    /**
     * 电子回单导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelorderElectronic")
    public void exportExcelorderElectronic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("orderNo", request.getParameter("orderNo"));
            map.put("orderType", request.getParameter("orderType"));
            map.put("startDate", request.getParameter("startDate"));
            map.put("endDate", request.getParameter("endDate"));
            map.put("appMobile", request.getParameter("appMobile"));
            String path = ordersService.exportExcelorderElectronic(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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
     * 电子回单批量打印数据---预览
     * Batch print
     */
    @RequestMapping(value = "/batchPrint")
    public ModelAndView batchPrint(String orderNo , String orderType , String createTimeBeginStr ,
                                   String createTimeEndStr , String appMobile){
        ModelAndView view = new ModelAndView("/func/print/index");
        Map map = new HashMap();
        map.put("orderNo",orderNo);
        map.put("orderType",orderType);
        map.put("createTimeBeginStr",createTimeBeginStr);
        map.put("createTimeEndStr",createTimeEndStr);
        map.put("appMobile",appMobile);
        List list  =  ordersService.batchPrint(map);
        view.addObject("list", list);
        view.addObject("size", list.size());
        return view;
    }

    /**
     *  打印
     * @return
     */
    @RequestMapping(value = "/batchPrint_1")
    public ModelAndView batchPrint_1(){
        ModelAndView view = new ModelAndView("/func/print/index_1");
        return view;
    }

    /**
     * 电子回单导出word
     * @param orderNo
     * @param orderType
     * @param createTimeBeginStr
     * @param createTimeEndStr
     * @param appMobile
     * @return
     */
    @RequestMapping(value = "/exportWord")
    public ModelAndView exprotWord(String orderNo , String orderType , String createTimeBeginStr ,
                                   String createTimeEndStr , String appMobile){
        ModelAndView view = new ModelAndView("/func/print/exportWord");
        Map map = new HashMap();
        map.put("orderNo",orderNo);
        map.put("orderType",orderType);
        map.put("createTimeBeginStr",createTimeBeginStr);
        map.put("createTimeEndStr",createTimeEndStr);
        map.put("appMobile",appMobile);
        List list  =  ordersService.batchPrint(map);
        view.addObject("list",list);
        view.addObject("size", list.size());
        return view;
    }

    /**
     * 提现 - 电子回单
     * @return
     */
    @RequestMapping("/withdrawals")
    public String withdrawals(){
        return "/func/orders/withdrawals_new";
    }

    /**
     * 电子回单 - 提现
     * @param requestModel
     * @param map
     * @return
     */
    @RequestMapping("/getwithdrawalsPage")
    @ResponseBody
    public DatagridResponseModel getRechargePage(DataGridPage requestModel,@RequestParam Map map){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<Map> trackDtoList = ordersService.getwithdrawalsPage(requestModel, map);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }


    /**
     * 收入明细
     * @return
     */
    @RequestMapping("/infoIndex")
    public String infoIndex(){
        return "/func/orders/infoIndex";
    }


    /**
     * 收入明细
     * @return
     */
    @RequestMapping("/income")
    public String income(){
        return "/func/orders/income";
    }


    @RequestMapping("/courseOrders")
    public String courseOrders(){
        return "/func/orders/courseOrdersList";
    }
    /**
     * 资金记录（分页）
     *
     * @param requestModel
     * @return
     */


    /**
     * 收入明细
     * @param requestModel
     * @param ordersDto
     * @return
     */
    @RequestMapping("/findIncomePage")
    @ResponseBody
    public DatagridResponseModel findIncomePage(DataGridPage requestModel, OrdersDto ordersDto){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<OrdersDto> trackDtoList = ordersService.findIncomePage(requestModel, ordersDto);
        BigDecimal incomeCount = ordersService.findIncomeCount(ordersDto);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        drm.setExt(incomeCount==null?new BigDecimal(0):incomeCount);
        return drm;
    }

    /**
     * 收入明细 - 导出
     * @return
     */
    @RequestMapping("/importExcelIncome")
    @ResponseBody
    public void importExcelIncome(HttpServletRequest request , HttpServletResponse response , String appId , String bankType ,
                                    String appMobile , String startTime , String endTime)throws Exception {
        ExportExcelWhaUtil exportExcelWhaUtil = ordersService.importExcelIncome(request, appId, appMobile,
                startTime, endTime, bankType);
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
     * 支出明细
     * @return
     */
    @RequestMapping("/expenditure")
    public String expenditure(){
        return "/func/orders/expenditure";
    }

    /**
     * 支出明细
     * @param requestModel
     * @param trackDto
     * @return
     */
    @RequestMapping("/findExpenditurePage")
    @ResponseBody
    public DatagridResponseModel findExpenditurePage(DataGridPage requestModel, OrdersDto trackDto){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<OrdersDto> trackDtoList = ordersService.findExpenditurePage(requestModel, trackDto);
        BigDecimal amountCount = ordersService.findExpenditureCount(trackDto);
        drm.setExt(amountCount == null ? new BigDecimal(0):amountCount);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 支出明细导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/importExcelExpenditue")
    public void importExcelReDetail(HttpServletRequest request , HttpServletResponse response , String appId ,
                                    String appMobile , String startTime , String endTime)throws Exception {
        ExportExcelWhaUtil exportExcelWhaUtil = ordersService.importExcelExpenditue(request, appId, appMobile, startTime, endTime);
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