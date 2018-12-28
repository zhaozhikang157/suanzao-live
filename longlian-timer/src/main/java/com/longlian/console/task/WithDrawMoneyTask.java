package com.longlian.console.task;

import chinapay.Base64;
import chinapay.PrivateKey;
import chinapay.SecureLink;
import com.huaxin.util.ActResult;
import com.huaxin.util.DateUtil;
import com.huaxin.util.MessageClient;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.ChinaPayConst;
import com.huaxin.util.constant.Const;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.dao.OrdersMapper;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.OrdersService;
import com.longlian.console.util.ChinaPayUtil;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.ChinaPayDto;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.ProxyTeacherService;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Account;
import com.longlian.model.AccountTrack;
import com.longlian.model.AppUser;
import com.longlian.model.Orders;
import com.longlian.type.AccountFromType;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2018-05-29.
 */
@Component("withDrawMoneyTask")
public class WithDrawMoneyTask extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(WithDrawMoneyTask.class);
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    ProxyTeacherService proxyTeacherService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    AccountService accountService;
    @Autowired
    OrdersService ordersService;
    @Autowired
    MessageClient messageClient;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    SendMsgService sendMsgService;
    @Value("${website}")
    private String website;
    @Override
    public String getTaskName() {
        return "审核通过7天后自动提现到账";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("审核通过7天后自动提现到账1："+e.getMessage());
        }
    }

    /***
     * 每天晚上1点触发
     */
    //@Scheduled(cron = "0 0 2 * * ?")
    public void doJob() throws Exception {
        //查询审核通过且待提现的订单
        List<Orders> ordersList = ordersService.findWithDrawList();
        if(ordersList != null && ordersList.size() > 0){
            for(Orders orders:ordersList) {
                Date auditTime = orders.getAuditTime();//审核通过时间
                String withDrawTime = ordersMapper.getWithDrawTime();
                Date d=DateUtil.getDayDiff(auditTime,withDrawTime!=null?Integer.parseInt(withDrawTime):0);//审核通过时间+7天
                if(d.getTime() <= new Date().getTime()) {
                    Account account = accountService.getIdRowLockByAccountId(orders.getAppId());
                    ChinaPayDto chinaPayDto = parseChinaPayDto4Orders(orders);
                    chinaPayDto.setMerId(CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_DF_MERID"));
                    BigDecimal proxyReturnMoneyChargePercent = systemParaRedisUtil.getProxyReturnMoneyChargePercent();//老师提现代理手续费率
                    //等待处理
                    //获取订单提现人Id
                    long appId = orders.getAppId();
                    //查询是否是代理人的老师
                    BigDecimal proxyMoney = new BigDecimal(0);
                    Map proxyApp = proxyTeacherService.getProxyAppIdByTeacherId(orders.getAppId());//获取老师
                    long proxyAppId = 0;
                    BigDecimal chinaPayAmount = orders.getAmount().subtract(orders.getLlCharge());//发送银联金额
                    if (chinaPayAmount.compareTo(new BigDecimal(0)) < 0) {
                        continue;
                    }
                    if (proxyApp != null && Utility.parseLong(proxyApp.get("proxyAppId").toString()) > 0) {
                        proxyAppId = Utility.parseLong(proxyApp.get("proxyAppId").toString());
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
                        log.error("审核通过7天后自动提现到账2:【绑定签名KEY出错！" + chinaPayDto.getMerId() + ",MerKeyPath=" + MerKeyPath + "】");
                        continue;
                    } else {
                        SecureLink sl = new SecureLink(key);
                        chkValue = sl.Sign(plainData);
                    }
                    //发送银联数据
                    ActResult actResult = ChinaPayUtil.sendHttpData(chinaPayDto, chkValue, transAmtStr);
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
                            if (proxyMoney.compareTo(new BigDecimal(0)) > 0 && proxyAppId > 0) {
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
                                if (appUser != null) teacherName = appUser.getName();
                                sendMsgService.sendMsg(orders.getAppId(), MsgType.TEACHER_WITHDRAW_RERUEN_PROXY_REMIND.getType(), orders.getId(),
                                        MsgConst.replace(MsgConst.TEACHER_WITHDRAW_RERUEN_PROXY_REMIND_CONTENT, teacherName, accountTrackProxy.getAmount().toString(),
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
                    }
                }
            }
        }
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
                        + "元，实际提现" + orders.getAmount().subtract(orders.getLlCharge()) + "元，已审核通过，预计将在48小时内到账至您尾号为"
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
}
