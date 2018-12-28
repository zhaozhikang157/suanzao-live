package com.longlian.console.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.ChinaPayConst;
import com.huaxin.util.constant.Const;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.console.dao.OrdersMapper;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.OrdersService;
import com.longlian.console.util.DateAddWha;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.ChinaPayDto;
import com.longlian.dto.OrdersDto;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.AccountTrackService;
import com.longlian.live.service.ProxyTeacherService;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Account;
import com.longlian.model.AccountTrack;
import com.longlian.model.Orders;
import com.longlian.type.MsgType;
import com.longlian.type.OrderType;
import com.longlian.type.PayType;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/3/1.
 */
@Service("ordersService")
public class OrdersServiceImpl implements OrdersService {
    private static Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    MessageClient messageClient;
    @Autowired
    AccountService accountService;

    @Autowired
    SendMsgService sendMsgService;

    @Autowired
    AccountTrackService trackService;

    @Autowired
    ProxyTeacherService proxyTeacherService;

    @Autowired
    AppUserService appUserService;@Value("${website}")
    private String website;


    /**
     * 轮询处理银联处理中的订单  syl
     *
     * @param id      订单ID
     * @param success 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerUnionSate(long id, boolean success) {
        Orders orders1 = ordersMapper.selectLockById(id);
        if (!"0".equals(orders1.getOptStatus())) {
            return;
        }
        //处理手续费
        BigDecimal bankOutCharge = systemParaRedisUtil.getAppBankOutChargeMin();//提现最低多少走费率
        BigDecimal bankOutChargePercentFixed = systemParaRedisUtil.getAppBankOutChargePercentFixed();// 收取手续费
        BigDecimal bankOutChinaCharge = systemParaRedisUtil.getAppBankOutChargePercent(orders1.getAppId());//收取的提现手续费率
        BigDecimal BankOutChinapayCharge = systemParaRedisUtil.getAppBankOutChinapayCharge();//银联收费
        BigDecimal charge = new BigDecimal(0);//手续费
        if (orders1.getAmount().compareTo(bankOutCharge) < 0) {
            charge = bankOutChargePercentFixed;
            orders1.setLlChargePercent(new BigDecimal(0));
        } else {
            orders1.setLlChargePercent(bankOutChinaCharge);
            charge = Utility.parseUseTwoPointNumMoney(orders1.getAmount().multiply(bankOutChinaCharge));
        }
        orders1.setLlCharge(charge);
        orders1.setCharge(BankOutChinapayCharge);
        orders1.setChargePercent(new BigDecimal(0));
        if (success) {
            BigDecimal chinaPayAmount = orders1.getAmount().subtract(orders1.getLlCharge());//发送银联金额
            //获取用户账户
            Account account = accountService.getIdRowLockByAccountId(orders1.getAppId());
            orders1.setOptStatus("1");
            orders1.setAuditStatus(1);
            orders1.setSuccessTime(new Date());
            orders1.setUnionStat("0");
            ordersMapper.handlerAuditStatusUnionStat(orders1);
            //扣除钱包余额
            AccountTrack accountTrack = new AccountTrack();
            accountTrack.setAmount(orders1.getAmount());
            accountTrack.setOrderId(orders1.getId());
            AccountAddDelReturn accountAddDelReturn = accountService.delAccountBalance(account.getAccountId(), accountTrack.getAmount(), accountTrack);
            //处理资金池
            accountAddDelReturn = accountService.delZiJinChiBalance(account.getAccountId(), chinaPayAmount.add(orders1.getCharge()), orders1.getId(), orders1.getBankType());
            sendMessage(orders1, true);
        } else {
            orders1.setOptStatus("2");
            orders1.setAuditStatus(2);
            orders1.setUnionStat("0");
            ordersMapper.handlerAuditStatusUnionStat(orders1);
            sendMessage(orders1, false);
        }
    }

    /**
     * 查询失败的订单
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> findFailOrders(@Param("map") Map map) {
        return ordersMapper.findFailOrders(map);
    }

    /**
     * 获取提现审核列表
     *
     * @param datagridRequestModel
     * @param ordersDto
     * @return
     */
    @Override
    public List<OrdersDto> getWithdrawDepositCheckListPage(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto) {
        ordersDto.setCreateTimeEnd(EndDateParameteUtil.parserEndDate(ordersDto.getCreateTimeEnd()));
        return ordersMapper.getWithdrawDepositCheckListPage(datagridRequestModel, ordersDto);
    }

    @Override
    public OrdersDto selectBankOutById(long id) {
        OrdersDto ordersDto = ordersMapper.selectBankOutById(id);
        ordersDto.setBalance(accountService.getAccountByAppId(ordersDto.getAppId()).getBalance());  //获取用户余额
        return ordersDto;
    }

    @Override
    public ActResult updateAuditStatus(Orders orders) throws Exception {
        ActResult actResult = new ActResult();
        Orders orders1 = ordersMapper.selectLockById(orders.getId());
        if (!"0".equals(orders1.getOptStatus())) {
            actResult.setSuccess(false);
            actResult.setMsg("该订单已处理");
            return actResult;
        }

        //处理手续费
        BigDecimal bankOutCharge = systemParaRedisUtil.getAppBankOutChargeMin();//提现最低多少走费率
        BigDecimal bankOutChargePercentFixed = systemParaRedisUtil.getAppBankOutChargePercentFixed();//低于多少100 收取手续费
        BigDecimal bankOutChinaCharge = systemParaRedisUtil.getAppBankOutChargePercent(orders1.getAppId());//银联收取的提现手续费率
        BigDecimal BankOutChinapayCharge = systemParaRedisUtil.getAppBankOutChinapayCharge();//银联收费
        BigDecimal proxyReturnMoneyChargePercent = systemParaRedisUtil.getProxyReturnMoneyChargePercent();//老师提现代理手续费率
        BigDecimal charge = new BigDecimal(0);//手续费
        if (orders1.getAmount().compareTo(bankOutCharge) < 0) {
            charge = bankOutChargePercentFixed;
            orders1.setLlChargePercent(new BigDecimal(0));
            orders1.setLlCharge(charge);
        } else {
            //orders1.setLlChargePercent(bankOutChinaCharge);
            //charge = Utility.parseUseTwoPointNumMoney(orders1.getAmount().multiply(bankOutChinaCharge));
        }
        orders1.setCharge(BankOutChinapayCharge);
        orders1.setChargePercent(new BigDecimal(0));

        if (orders.getAuditStatus() == 1) { //通过
            if (orders1.getAmount().compareTo(charge) <= 0) {//订单金额不能少且等于手续费金额
                actResult.setSuccess(false);
                actResult.setMsg("该订单金额要大于手续费金额" + charge + "元");
                return actResult;
            }
            orders1.setAuditorId(orders.getAuditorId());
            orders1.setAuditTime(new Date());
            orders1.setAuditAgreed(orders.getAuditAgreed());
            ActResult result = auditPass(orders1);
            return result;
        } else if (orders.getAuditStatus() == 2) { //不通过
            orders.setOptStatus("2");
            ordersMapper.updateAuditStatus(orders);
            actResult.setMsg("操作成功");
            sendMessage(orders1, false);
            return actResult;
        }
        actResult.setMsg("操作成功");
        return actResult;
    }

    @Override
    public String exportExcelBankOutCheck(Map map, HttpServletRequest request) throws Exception {
        List list = countHead();
        List list1 = columnHeader(map);
        List<ExcelTop> result = new ArrayList<ExcelTop>();
        //封装列头
        for (int i = 0; i < list1.size(); i++) {
            ExcelTop excelTopss1 = new ExcelTop();
            excelTopss1.setRowIndex(i);
            excelTopss1.setRowNum(i);
            excelTopss1.setRowText(0);
            excelTopss1.setStart(0);
            excelTopss1.setEnd(9);
            excelTopss1.setSl(true);
            excelTopss1.setData(list1.get(i).toString());
            result.add(excelTopss1);
        }

        List<List<Object>> lists = numerical(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "提现审核 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }


    /**
     * 表头
     *
     * @return
     */
    private List countHead() {
        List list = new ArrayList();
        list.add("申请时间");
        list.add("提现金额");
        list.add("申请人");
        list.add("开户人");
        list.add("开户银行");
        list.add("收款银行卡号");
        list.add("描述");
        return list;
    }

    private List columnHeader(Map map) {
        List list = new ArrayList();
        list.add("提现审核 ");
        list.add("申请时间:" + map.get("createTimeBegin").toString() + " 至 " + map.get("createTimeEnd").toString());
        list.add("开户行:" + map.get("bankName").toString());
        return list;
    }

    private List<List<Object>> numerical(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        List<Map> list = ordersMapper.getBankOutCheckList(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("createTime") == null ? "" : m.get("createTime"));
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            data.add(m.get("appName") == null ? "" : m.get("appName"));
            data.add(m.get("bankCaidOpenName") == null ? "" : m.get("bankCaidOpenName"));
            data.add(m.get("bankName") == null ? "" : m.get("bankName"));
            data.add(m.get("bankCardNo") == null ? "" : m.get("bankCardNo"));
            data.add(m.get("auditAgreed") == null ? "" : m.get("auditAgreed"));
            count.add(data);
        }
        return count;
    }

    /**
     * 将订单转银联支付订单对象
     *
     * @param orders
     * @return
     */
    public ChinaPayDto parseChinaPayDto4Orders(Orders orders) {
        ChinaPayDto chinaPayDto = new ChinaPayDto();
        chinaPayDto.setMerId(orders.getMerId());
        chinaPayDto.setUsrName(orders.getName());
        chinaPayDto.setCardNo(orders.getBankCardNo());
        chinaPayDto.setOpenBank(orders.getBankName());
        chinaPayDto.setSubBank("");
        chinaPayDto.setCity("");
        chinaPayDto.setProv("");
        chinaPayDto.setPurpose(Const.longlian_live_bank_out_defaul_remark);
        chinaPayDto.setMerId(orders.getMerId());
        String merDate = DateUtil.format(new Date(), "yyyyMMdd");
        chinaPayDto.setMerDate(merDate);
        chinaPayDto.setTermType(ChinaPayConst.DF_PAY_TERMTYPE);
        chinaPayDto.setSignFlag(ChinaPayConst.DF_QUERY_SIGNFLAG);
        chinaPayDto.setVersion(ChinaPayConst.DF_QUERY_VERSION);
        chinaPayDto.setFlag(ChinaPayConst.DF_QUERY_FLAG);
        return chinaPayDto;
    }


    /**
     * 通过
     *
     * @param orders
     * @return
     */
    public ActResult auditPass(Orders orders) throws Exception {
        ActResult actResult = new ActResult();
        //ChinaPayDto chinaPayDto = parseChinaPayDto4Orders(orders);
        //chinaPayDto.setMerId(CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERID"));
        Account account = accountService.getIdRowLockByAccountId(orders.getAppId());
        if (account == null) return ActResult.fail("该用户钱包账号不存在！");
        if ("1".equals(account.getStatus())) {
            actResult.setSuccess(false);
            actResult.setMsg("该用户钱包已禁止提现！");
            return actResult;
        }
        if ("2".equals(account.getStatus())) {
            actResult.setSuccess(false);
            actResult.setMsg("该用户钱包已冻结，不能提现！");
            return actResult;
        }
        //判断是否已经
        BigDecimal maxHign = systemParaRedisUtil.getBankOutHignMoney();
        if (orders.getAmount().compareTo(maxHign) > 0) return ActResult.fail("提现最高金额不能高于" + maxHign + "！");

        if (orders.getAmount().compareTo(account.getBalance()) > 0) {
            actResult.setSuccess(false);
            actResult.setMsg("钱包余额不足！");
            return actResult;
        }

        BigDecimal chinaPayAmount = orders.getAmount().subtract(orders.getLlCharge());//发送银联金额
        if (chinaPayAmount.compareTo(new BigDecimal(0)) < 0) {
            actResult.setSuccess(false);
            actResult.setMsg("提现金额不能小于" + orders.getLlCharge() + "元");
            return actResult;
        }
        try {
            orders.setOptStatus(String.valueOf(orders.getAuditStatus()));
            ordersMapper.updateOrderAuditById(orders);
            actResult.setSuccess(true);
            if("1".equals(orders.getAuditStatus())) {
                actResult.setMsg("审核通过");
            } else if("2".equals(orders.getAuditStatus())) {
                actResult.setMsg("审核不通过");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("提现审核异常:"+e.getMessage());
            actResult.setSuccess(false);
            actResult.setMsg("审核异常");
        }
        /*BigDecimal proxyReturnMoneyChargePercent = systemParaRedisUtil.getProxyReturnMoneyChargePercent();//老师提现代理手续费率
        //等待处理
        //获取订单提现人Id
        long appId = orders.getAppId();
        //查询是否是代理人的老师
        BigDecimal proxyMoney = new BigDecimal(0);
        Map proxyApp =  proxyTeacherService.getProxyAppIdByTeacherId(orders.getAppId());//获取老师
        long proxyAppId = 0;
        if(proxyApp != null && Utility.parseLong(proxyApp.get("proxyAppId").toString()) > 0){
            proxyAppId =  Utility.parseLong(proxyApp.get("proxyAppId").toString()) ;
            proxyMoney = orders.getAmount().multiply(proxyReturnMoneyChargePercent).setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数，四舍五入
            chinaPayAmount = chinaPayAmount.subtract(proxyMoney);//减去代理的钱
            orders.setLlCharge(orders.getLlCharge().add(proxyMoney));//手续费加上代理的费用,手续费不和代理的费用合并
        }

        BigDecimal transAmt = chinaPayAmount.multiply(new BigDecimal(100));
        String transAmtStr = Utility.getFixLengthNum(transAmt.longValue() + "", 12);

        if (!Utility.isNullorEmpty(chinaPayDto.getMerSeqId())) {
            //chinaPayDto.setMerSeqId(ChinaPayUtil.getBankOutMerSeqId4Random());
            // bankOut.setMerSeqId(systemUtil.getBankOutMerSeqId4Random(bankOut.getId()));
        } else {
            chinaPayDto.setMerSeqId(ChinaPayUtil.getBankOutMerSeqId4Random());
        }
        orders.setOrderNo(chinaPayDto.getMerSeqId());
        chinaPayDto.setTranAmt(transAmtStr);
      chinaPayDto.setSubBank("十里河支行");
        chinaPayDto.setProv("北京");
        chinaPayDto.setCity("北京");
        String signs = chinaPayDto.getMerId() + chinaPayDto.getMerDate() + chinaPayDto.getMerSeqId() +
                chinaPayDto.getCardNo() + chinaPayDto.getUsrName() + chinaPayDto.getOpenBank()
                + chinaPayDto.getProv() + chinaPayDto.getCity() + chinaPayDto.getTranAmt()
                + Const.longlian_live_bank_out_defaul_remark + chinaPayDto.getSubBank() + chinaPayDto.getFlag()
                + ChinaPayConst.DF_PAY_VERSION + ChinaPayConst.DF_PAY_TERMTYPE;

        String plainData = new String(Base64.encode(signs.getBytes("GBK")));
        //签名
        String chkValue = null;
        int KeyUsage = 0;
        PrivateKey key = new PrivateKey();
        String CHINA_PAY_DF_MERPRKKEY_FILE = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERPRKKEY_FILE");
        String MerKeyPath = ResourceUtils.getFile(CHINA_PAY_DF_MERPRKKEY_FILE).getPath();
        ;
        boolean flage = key.buildKey(chinaPayDto.getMerId(), KeyUsage, MerKeyPath);
        if (flage == false) {
            actResult.setSuccess(false);
            actResult.setMsg("绑定签名KEY出错！");
            return actResult;
        } else {
            SecureLink sl = new SecureLink(key);
            chkValue = sl.Sign(plainData);
        }
        //发送银联数据
        actResult = ChinaPayUtil.sendHttpData(chinaPayDto, chkValue, transAmtStr);
        String cpSeqId = "";
        if (actResult.getData() != null) {
            Map map = (Map) actResult.getData();
            cpSeqId = map.get("cpSeqId").toString();//银联内部
        }
        orders.setTranNo(cpSeqId);
        if (actResult.isSuccess()) {
            if (actResult.getCode() == 2 || actResult.getCode() == 3
                    || actResult.getCode() == 4 || actResult.getCode() == 5
                    || actResult.getCode() == 7 || actResult.getCode() == 8) {
                //如果返回状态为“处理中”,则给客服发送短信
                log.info("============>>>>>>银联返回订单状态【处理中】,发送短信--start");
                String _mobile = CustomizedPropertyConfigurer.getContextProperty("withdraw_order_fail_notice_mobile");
                log.info("短信接收手机号：" + _mobile);
                String content = "订单【" + chinaPayDto.getMerSeqId() + "】提现失败,原因:银行未成功打款,请及时处理！【龙链科技】";
                messageClient.sendMessage(_mobile, content);
                log.info("============>>>>>>银联返回订单状态【处理中】,发送短信--end");
                //处理更新银联返回状态
                orders.setMerId(chinaPayDto.getMerId());
                orders.setAuditStatus(1);
                orders.setUnionStat(actResult.getCode() + "");
                ordersMapper.updateAuditStatusUnionStat(orders);
            } else {
                //处理订单
                orders.setOptStatus("1");
                orders.setAuditStatus(1);
                orders.setSuccessTime(new Date());
                orders.setMerId(chinaPayDto.getMerId());
                ordersMapper.updateAuditStatus(orders);
                //扣除钱包余额
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setAmount(orders.getAmount());
                accountTrack.setOrderId(orders.getId());
                AccountAddDelReturn accountAddDelReturn = accountService.delAccountBalance(account.getAccountId(), accountTrack.getAmount(), accountTrack);
                //处理代理返钱
                if(proxyMoney.compareTo(new BigDecimal(0)) > 0 && proxyAppId > 0){
                    AccountTrack accountTrackProxy = new AccountTrack();
                    accountTrackProxy.setFormAccountId(orders.getAppId());
                    accountTrackProxy.setAmount(proxyMoney);
                    accountTrackProxy.setOrderId(orders.getId());
                    accountTrackProxy.setReturnMoneyLevel(AccountFromType.teach_bankOut_proxy_return_money.getValue());
                    accountService.addAccountBalance(proxyAppId, accountTrackProxy.getAmount(), accountTrackProxy);
                    //发送短信
                    //查询老师名称
                    AppUser appUser = appUserService.getAppUserById(orders.getAppId());
                    String teacherName = "";
                    if(appUser != null) teacherName = appUser.getName();
                    sendMsgService.sendMsg(orders.getAppId(), MsgType.TEACHER_WITHDRAW_RERUEN_PROXY_REMIND.getType(), orders.getId(),
                            MsgConst.replace(MsgConst.TEACHER_WITHDRAW_RERUEN_PROXY_REMIND_CONTENT,teacherName , accountTrackProxy.getAmount().toString() ,
                                    proxyReturnMoneyChargePercent.toString(), accountTrackProxy.getAmount().toString()), "");
                }
                //处理资金池
                //accountAddDelReturn = accountService.delZiJinChiBalance(account.getAccountId(), chinaPayAmount.add(orders.getCharge()), orders.getId(), orders.getBankType());
                sendMessage(orders, true);
            }
        } else {
            sendMessage(orders, false);
            orders.setOptStatus("2");
            orders.setAuditStatus(2);
            orders.setRemark(actResult.getMsg());
            orders.setChargePercent(new BigDecimal(0));
            orders.setCharge(new BigDecimal(0));
            orders.setLlCharge(new BigDecimal(0));
            orders.setLlChargePercent(new BigDecimal(0));
            ordersMapper.updateAuditStatus(orders);
        }*/

        return actResult;
    }

    /**
     * 发送消息和创建消息
     *
     * @param orders
     * @param success
     */
    public void sendMessage(Orders orders, boolean success) {
        try {
            String message = "";
            if (success) {
                //发送通知
                message = " 您申请的一笔提现金额"
                        + orders.getAmount() + "元，提现手续费为" + orders.getLlCharge()
                        + "元，实际提现" + orders.getAmount().subtract(orders.getLlCharge()) + "元，已审核通过，预计将在7-10个工作日内到账至您尾号为"
                        + orders.getBankCardNo().substring(orders.getBankCardNo().length() - 4) + "的银行卡中，请留意您的银行卡资金变化";
                // msgType = MsgType.bankout_success.getType();
                //创建消息记录
                String url = website + "/weixin/messageDetails?msg="+message;
                sendMsgService.sendMsg(orders.getAppId(), MsgType.withdraw_money_ok.getType(), orders.getId(), message, url);
            } else {
                message = " 您申请的一笔提现金额"
                        + orders.getAmount() + "元，提现手续费为" + orders.getLlCharge()
                        + "元，实际提现" + orders.getAmount().subtract(orders.getLlCharge()) + "元，未能通过审核，不能提现。如有疑问，请致电400客户服务热线";
                String url = website + "/weixin/messageDetails?msg="+message;
                sendMsgService.sendMsg(orders.getAppId(), MsgType.withdraw_money_not.getType(), orders.getId(), message, url);
            }
            //通知
            //创建消息记录
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map> getOrderElectronic(DatagridRequestModel page, String orderNo, String orderType, Date startDate, Date endDate, String mobile) {
        endDate = DateAddWha.addDay(endDate, +1);
        List<Map> ops = ordersMapper.getOrderElectronicPage(page, orderNo, orderType, startDate, endDate, mobile);
        for(Map map : ops){
            if(map.get("payType").toString().equals("09")){
                map.put("orderType","009");
            }else if(map.get("payType").toString().equals("14")){
                map.put("orderType","014");
            }
        }
        return ops;
    }

    /**
     * 获取电子打印详情 -提现
     *
     * @param id
     * @return
     */
    @Override
    public OrdersDto selectInfoById(long id) {
        OrdersDto ordersDto = ordersMapper.selectInfoById(id);
        if (ordersDto != null && Utility.isNullorEmpty(ordersDto.getMerId())) {
            ordersDto.setMerId(CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERID"));
        }
        ordersDto.setCapitalAmount(TransformationUtil.getInstance().format(ordersDto.getAmount()));
        List<Map> list = trackService.getBankType();
        for (Map map : list) {
            if (ordersDto.getBankType().equals(map.get("id").toString())) {
                ordersDto.setBankType(map.get("name").toString());
            }
        }
        return ordersDto;
    }

    /**
     * 返钱
     *
     * @param orders
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResult rollback(long optEmpId, Orders orders) {
        ActResult actResult = new ActResult();
        try {
            Orders orders1 = ordersMapper.selectById(orders.getId());
            if (!"1".equals(orders1.getOptStatus())) {
                actResult.setMsg("该订单不能返钱！");
                actResult.setSuccess(false);
                return actResult;
            }
            actResult = accountService.accountReward(orders1);
            if (!actResult.isSuccess()) return actResult;
            ordersMapper.updateOptStatusById(orders.getId(), "3");  //更改操作状态
//            addAccountReward(optEmpId, orders);   //添加记录
        } catch (Exception e) {
            e.printStackTrace();
            actResult.setMsg("系统错误");
            actResult.setSuccess(false);
        }
        return actResult;
    }

    @Override
    public List<OrdersDto> getCheckRecordListPage(DatagridRequestModel datagridRequestModel, OrdersDto ordersDto) {
        ordersDto.setCreateTimeEnd(EndDateParameteUtil.parserEndDate(ordersDto.getCreateTimeEnd()));
        return ordersMapper.getCheckRecordListPage(datagridRequestModel, ordersDto);
    }

    /**
     * 提现记录导出
     *
     * @param map
     * @param request
     * @return
     */
    @Override
    public String exportExcelBankOutCheckRecord(Map map, HttpServletRequest request) throws Exception {
        List list = countHeadRecord();
        List list1 = columnHeaderRecord(map);
        List<ExcelTop> result = new ArrayList<ExcelTop>();
        //封装列头
        for (int i = 0; i < list1.size(); i++) {
            ExcelTop excelTopss1 = new ExcelTop();
            excelTopss1.setRowIndex(i);
            excelTopss1.setRowNum(i);
            excelTopss1.setRowText(0);
            excelTopss1.setStart(0);
            excelTopss1.setEnd(9);
            excelTopss1.setSl(true);
            excelTopss1.setData(list1.get(i).toString());
            result.add(excelTopss1);
        }

        List<List<Object>> lists = numericalRecord(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "提现记录 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }


    /**
     * 表头
     *
     * @return
     */
    private List countHeadRecord() {
        List list = new ArrayList();
        list.add("申请时间");
        list.add("提现金额");
        list.add("订单编号");
        list.add("申请人");
        list.add("用户手机");
        list.add("开户人");
        list.add("开户银行");
        list.add("收款银行卡号");
        list.add("操作状态");
        list.add("审核人ID");
        list.add("审核日期");
        list.add("描述");
        return list;
    }

    private List columnHeaderRecord(Map map) {
        List list = new ArrayList();
        list.add("提现审核 ");
        list.add("申请时间:" + map.get("createTimeBegin").toString() + " 至 " + map.get("createTimeEnd").toString());
        list.add("开户行:" + map.get("bankName").toString() + "    用户手机:" + map.get("appMobile").toString());
        list.add("平台流水编号:" + map.get("orderNo").toString() + "    操作状态:" + map.get("optStatusSrt").toString());
        return list;
    }

    private List<List<Object>> numericalRecord(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        List<Map> list = ordersMapper.getBankOutCheckRecordList(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("createTime") == null ? "" : m.get("createTime"));
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            data.add(m.get("orderNo") == null ? "" : m.get("orderNo"));
            data.add(m.get("appName") == null ? "" : m.get("appName"));
            data.add(m.get("appMobile") == null ? "" : m.get("appMobile"));
            data.add(m.get("bankCaidOpenName") == null ? "" : m.get("bankCaidOpenName"));
            data.add(m.get("bankName") == null ? "" : m.get("bankName"));
            data.add(m.get("bankCardNo") == null ? "" : m.get("bankCardNo"));
            data.add(m.get("optStatus") == null ? "" : getOptStatus(m.get("optStatus").toString()));
            data.add(m.get("auditorId") == null ? "" : m.get("auditorId"));
            data.add(m.get("auditTime") == null ? "" : m.get("auditTime"));
            data.add(m.get("auditAgreed") == null ? "" : m.get("auditAgreed"));
            count.add(data);
        }
        return count;
    }

    private Object getOptStatus(String optStatus) {
        if ("1".equals(optStatus)) {
            return "成功";
        } else if ("2".equals(optStatus)) {
            return "失败";
        } else if ("0".equals(optStatus)) {
            return "进行中";
        } else if ("3".equals(optStatus)) {
            return "已返钱";
        } else {
            return "";
        }
    }

    @Override
    public Map getInfoBuyCourse(long id) {
        Map map = ordersMapper.getInfoBuyCourse(id);

        if (!StringUtils.isEmpty(map.get("divideScale"))) {
            map.put("capitalRealAmount", TransformationUtil.getInstance().format(map.get("realAmount").toString()));
            map.put("divideScale", systemParaRedisUtil.getCourseDivideScaleByValue(map.get("divideScale").toString()));//获取分销比例
        }

        List<Map> list = trackService.getBankType();
        for (Map m : list) {
            if (map.get("bankType").toString().equals(m.get("id").toString())) {
                map.put("bankType", m.get("name"));
            }
        }
        return map;
    }

    /**
     * 更新支付中的订单 为失败
     *
     * @param joinCourseId 课程报名ID
     * @return
     */
    @Override
    public int updateOptStatusFail(long joinCourseId) {
        return ordersMapper.updateOptStatusFail(joinCourseId);
    }


    /**
     * 电子回单导出
     *
     * @param map
     * @param request
     * @return
     */
    @Override
    public String exportExcelorderElectronic(Map map, HttpServletRequest request) throws Exception {
        List list = countHeadReceipt();
        List list1 = columnHeaderReceipt(map);
        List<ExcelTop> result = new ArrayList<ExcelTop>();
        //封装列头
        for (int i = 0; i < list1.size(); i++) {
            ExcelTop excelTopss1 = new ExcelTop();
            excelTopss1.setRowIndex(i);
            excelTopss1.setRowNum(i);
            excelTopss1.setRowText(0);
            excelTopss1.setStart(0);
            excelTopss1.setEnd(9);
            excelTopss1.setSl(true);
            excelTopss1.setData(list1.get(i).toString());
            result.add(excelTopss1);
        }

        List<List<Object>> lists = numericalReceipt(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "电子回单 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }


    /**
     * 表头
     *
     * @return
     */
    private List countHeadReceipt() {
        List list = new ArrayList();
        list.add("姓名");
        list.add("用户手机");
        list.add("银行卡号");
        list.add("银行名称");
        list.add("订单编号");
        list.add("服务类型");
        list.add("金额");
        list.add("日期");
        return list;
    }

    private List columnHeaderReceipt(Map map) {
        List list = new ArrayList();
        list.add("电子回单 ");
        list.add("申请时间:" + map.get("startDate").toString() + " 至 " + map.get("endDate").toString());
        list.add("订单编号:" + map.get("orderNo").toString() + "    用户手机:" + map.get("appMobile").toString() + "    服务类型:" + getOrderType(map.get("orderType").toString()));
        return list;
    }

    private String getOrderType(String orderType) {
        if ("1".equals(orderType)) {
            return "提现";
        }
        if ("0".equals(orderType)) {
            return "购买课程";
        }
        return "";
    }

    private List<List<Object>> numericalReceipt(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        if (Utility.isNullorEmpty(map.get("endDate"))) {
            Date d = DateAddWha.addDay(DateUtil.format(map.get("endDate").toString(), "yyyy-MM-dd HH:mm:ss"), +1);
            map.put("endDate", d);
        }
        List<Map> list = ordersMapper.getOrderElectronic(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("userName") == null ? "" : m.get("userName"));
            data.add(m.get("mobile") == null ? "" : m.get("mobile"));
            data.add(m.get("bankCardNo") == null ? "" : m.get("bankCardNo"));
            data.add(m.get("bankName") == null ? "" : m.get("bankName"));
            data.add(m.get("orderNo") == null ? "" : m.get("orderNo"));
            if(m.get("bankType").toString().equals("09")){
                data.add("钱包购买课程");
            }else if(m.get("bankType").toString().equals("14")){
                data.add("微信购买课程");
            }else {
                data.add(getOrderType(m.get("orderType").toString()));
            }
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            data.add(m.get("successTime") == null ? "" : m.get("successTime"));
            count.add(data);
        }
        return count;
    }

    public List batchPrint(Map map) {
        List newList = new ArrayList();
        if (Utility.isNullorEmpty(map.get("createTimeEndStr"))) {
            Date d = DateAddWha.addDay(DateUtil.format(map.get("createTimeEndStr").toString(), "yyyy-MM-dd HH:mm:ss"), +1);
            map.put("createTimeEndStr", d);
        }
        map.put("startDate",map.get("createTimeBeginStr"));
        map.put("endDate",map.get("createTimeEndStr"));
        List<Map> list = ordersMapper.getOrderElectronic(map);
        if (list.size() > 0) {
            for (Map m : list) {
                long orderType = Long.parseLong(m.get("orderType").toString());//订单交易类型 0-购买课程1- 提现
                long id = Long.parseLong(m.get("id").toString());//订单id
                if (orderType == 0) {
                    newList.add(getInfoBuyCourse(id));
                } else {
                    newList.add(selectInfoById(id));
                }
            }
        }
        return newList;
    }

    /**
     * 电子回单 - 提现
     * @param page
     * @param map
     * @return
     */
    @Override
    public List<Map> getwithdrawalsPage(DatagridRequestModel page, Map map) {
        String startDate = map.get("startTime").toString() + " 00:00:00";
        String endDate = map.get("endTime").toString() + " 23:59:59";
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        List<Map> ops = ordersMapper.getwithdrawalsPage(page, map);
        List<Map> orderType = OrderType.getList();
        for(Map op : ops){
            for(Map type : orderType){
                if(op.get("orderType").toString().equals(type.get("value").toString())){
                    op.put("orderType",type.get("text"));
                }
            }
        }
        return ops;
    }

    /**
     * 收入明细
     * @param requestModel
     * @param ordersDto
     * @return
     */
    @Override
    public List<OrdersDto> findIncomePage(DataGridPage requestModel, OrdersDto ordersDto) {
        if(ordersDto.getCreateTimeEnd()!=null){
            String endTime = DateUtil.format(ordersDto.getCreateTimeEnd(), "yyyy-MM-dd");
            ordersDto.setCreateTimeEnd(DateUtil.format(endTime + " 23:59:59"));
        }
        List<OrdersDto> list = ordersMapper.findIncomePage(requestModel, ordersDto);
        for(Orders orders : list){
            orders.setBankType(getPayTypeByValue(orders.getBankType()));
        }
        return list;
    }

    @Override
    public BigDecimal findIncomeCount(OrdersDto trackDto) {
        return ordersMapper.findIncomeCount(trackDto);
    }

    /**
     * 收入明细导出
     * @param request
     * @param appId
     * @param appMobile
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public ExportExcelWhaUtil importExcelIncome(HttpServletRequest request, String appId, String appMobile, String startDate, String endDate, String bankType) {
        Date start = DateUtil.format(startDate + " 00:00:00");
        Date end = DateUtil.format(endDate + " 23:59:59");
        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setCreateTimeEnd(end);
        ordersDto.setCreateTimeBegin(start);
        ordersDto.setAppMobile(appMobile);
        ordersDto.setBankType(bankType);
        if(!StringUtils.isEmpty(appId)){
            ordersDto.setAppId(Long.parseLong(appId));
        }
        List<Map> trackMap = ordersMapper.findIncome(ordersDto);
        for(Map map : trackMap){
            map.put("BANK_TYPE", getPayTypeByValue(map.get("BANK_TYPE").toString()));
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "收入详情";
        listStr.add(top1);
        String top3 = "序号,交易日期,第三方交易号,用户名,手机号,支付类型,收入金额,备注,交易订单号";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "CREATE_TIME,ORDER_NO,APP_NAME,APP_MOBILE,BANK_TYPE,AMOUNT,REMARK,TRAN_NO";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(trackMap, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "收支明细", request, exceltitel);
        return excel;
    }

    @Override
    public Long findCourseId(long orderId) {
        return ordersMapper.findCourseId(orderId);
    }

    @Override
    public Map getCourseIds() {
        List<Map> list = ordersMapper.findCourseIds();
        Map re = new HashMap();
        for (Map m : list) {
            re.put(  m.get("ID") , m.get("COURSE_ID"));
        }
        return re;
    }

    /**
     * 查询审核通过待到账的订单，定时去扫描，到7天则自动到账
     *
     * @return
     */
    @Override
    public List<Orders> findWithDrawList() {
        return ordersMapper.findWithDrawList();
    }

    /**
     * 支出明细
     * @param requestModel
     * @param ordersDto
     * @return
     */
    @Override
    public List<OrdersDto> findExpenditurePage(DataGridPage requestModel, OrdersDto ordersDto) {
        List<OrdersDto> list = ordersMapper.findExpenditurePage(requestModel, ordersDto);
        return list;
    }


    @Override
    public BigDecimal findExpenditureCount(OrdersDto trackDto) {
        return ordersMapper.findExpenditureCount(trackDto);
    }

    /**
     * 支出明细导出
     * @param request
     * @param appId
     * @param appMobile
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public ExportExcelWhaUtil importExcelExpenditue(HttpServletRequest request, String appId, String appMobile, String startDate, String endDate) {
        Date start = DateUtil.format(startDate + " 00:00:00");
        Date end = DateUtil.format(endDate + " 23:59:59");
        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setCreateTimeEnd(end);
        ordersDto.setSuccessTime(start);
        ordersDto.setAppMobile(appMobile);
        if(!StringUtils.isEmpty(appId)){
            ordersDto.setAppId(Long.parseLong(appId));
        }
        List<Map> trackMap = ordersMapper.findExpenditure(ordersDto);
        List<String> listStr = new ArrayList<String>();
        String top1 = "收支详情";
        listStr.add(top1);
        String top3 = "序号,交易日期,交易订单号,第三方交易号,用户名,手机号,支出金额";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "CREATE_TIME,ORDER_NO,TRAN_NO,APP_NAME,APP_MOBILE,AMOUNT";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(trackMap, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "收支明细", request, exceltitel);
        return excel;
    }


    public String getPayTypeByValue(String value){
        List<Map> list = PayType.getList();
        for(Map map : list){
            if(map.get("value").toString().equals(value)){
                return map.get("text").toString();
            }
        }
        return "";
    }

    public List<ExcelTop> getExceltitel(List<String> strs) {
        List<ExcelTop> ets = new ArrayList<ExcelTop>();//行集合
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();//每行第一个位置
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();//每行内的 每列集合
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }

        }
        return ets;
    }

}
