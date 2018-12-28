package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.BankCardDto;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.AppUserMapper;
import com.longlian.live.dao.BankCardMapper;
import com.longlian.live.dao.OrdersMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.OrderUtil;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.MsgType;
import com.longlian.type.PayType;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/8/19.
 */
@Service("withdrawalsService")
public class WithdrawalsServiceImpl implements WithdrawalsService {

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    BankCardMapper bankCardMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    BankService bankService;
    @Autowired
    AppMsgService appMsgService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Value("${website}")
    private String website;

    @Override
    @Transactional(readOnly = true)
    public ActResultDto findAccountBalance(long id,String cardId) {
        ActResultDto resultDto = new ActResultDto();
        Account account = accountMapper.getById(id);
        BigDecimal hisAccountMoney = ordersMapper.findAccountMoney(id);
        if(hisAccountMoney==null){
            hisAccountMoney = new BigDecimal(0);
        }
        Map map = new HashMap();
        if(StringUtils.isNotEmpty(cardId)){
            BankCard card = bankCardMapper.findByAppIdAndCardId(Long.valueOf(cardId),id);
            Bank bank = bankService.getBankInfo(card.getBankId());
            if(card!=null){
                map.put("cardNo", card.getCardNo());
                map.put("bankName", card.getBankName());
                map.put("cardId", cardId);
                map.put("bankPic",bank.getPicAddress());
            }else{
                map.put("cardNo","");
                map.put("bankName","");
                map.put("cardId","");
                map.put("bankPic","");
            }
        }else {
            List<BankCardDto> list = bankCardMapper.findCardsByAppId(id);
            if (list.size() == 0) {
                map.put("cardNo", "");
                map.put("bankName", "");
                map.put("cardId", "");
                map.put("bankPic","");
            } else {
                map.put("cardNo", list.get(0).getCardNo());
                map.put("bankName", list.get(0).getBankName());
                map.put("cardId", list.get(0).getId());
                map.put("bankPic", list.get(0).getPicAddress());
            }
        }
        map.put("bankOutRemark",systemParaRedisUtil.getBankOutRemark(id));
        if(account==null){
            resultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
            return resultDto;
        }else{
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            if(account.getBalance().subtract(hisAccountMoney).compareTo(new BigDecimal(0))<0){
            //orders的关于该appId所正在提现进行中的金额
                map.put("balance",0);
            }else{
                map.put("balance",account.getBalance().subtract(hisAccountMoney));
            }
            //获取提现手续费
            BigDecimal bankOutCharge = systemParaRedisUtil.getAppBankOutChargePercentFixed();
            BigDecimal bankOutMaxMoney = systemParaRedisUtil.getBankOutHignMoney();
            map.put("bankOutMinMoney" , bankOutCharge);
            map.put("bankOutMaxMoney" , bankOutMaxMoney);
            resultDto.setData(map);
            return resultDto;
        }
    }

    /**
     * 提现操作
     * @param token
     * @param amount
     * @param cardId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto bankOutOption(AppUserIdentity token, BigDecimal amount, Long cardId , String tradePassword,String type) {
        ActResultDto resultDto = new ActResultDto();
        BigDecimal hignMoney = systemParaRedisUtil.getBankOutHignMoney();
        LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
        if(liveRoom != null){
            Boolean boo = wechatOfficialService.isWechatOfficial(liveRoom.getId());
            if(boo && liveRoom.getReduceDataCount() > 0){
                if("1".equals(type)){
                    resultDto.setCode(ReturnMessageType.NEED_BUY_FLOW.getCode());
                    resultDto.setMessage(ReturnMessageType.NEED_BUY_FLOW.getMessage());
                }else{
                    resultDto.setCode(ReturnMessageType.NEED_BUY_FLOW_APP.getCode());
                    resultDto.setMessage(ReturnMessageType.NEED_BUY_FLOW_APP.getMessage());
                }
                return resultDto;
            }
        }
        if(cardId == null || cardId < 1){
            resultDto.setCode(ReturnMessageType.BANK_CARD_ID_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.BANK_CARD_ID_ERROR.getMessage());
            return resultDto;
        }
        //判断交易密码是否为空
        if(StringUtils.isEmpty(tradePassword)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        //判断提现金额是否大于3块
        if(amount.compareTo(systemParaRedisUtil.getAppBankOutChargePercentFixed())<1){
            resultDto.setMessage("提现金额不能低于"+systemParaRedisUtil.getAppBankOutChargePercentFixed() + "元!");
            resultDto.setCode(ReturnMessageType.BANK_OUT_ERROR.getCode());
            return resultDto;
        }
        //判断今天申请提现金额是否大于今天最高提现额度
        if(amount.compareTo(hignMoney)>0){
            resultDto.setMessage(ReturnMessageType.BANK_OUT_MONRY_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.BANK_OUT_MONRY_ERROR.getCode());
            return resultDto;
        }
        //判断这个自然月是否超过两次(提现申请中的和提现成功的次数相加不能超过两次)
        /*try {
            String[] dates = DateUtil.getMonthLimitStr(new Date());
            Long withIngCount = ordersMapper.getWithIngCount(dates[0], dates[1], token.getId());
            if(withIngCount == null) withIngCount = 0l;
            Long withCount = ordersMapper.getWithSuccessCount(dates[0],dates[1],token.getId());
            if(withCount == null) withCount = 0l;
            if(withCount+withIngCount>=2){
                resultDto.setMessage(ReturnMessageType.WITH_OPT_OVERRUN.getMessage());
                resultDto.setCode(ReturnMessageType.WITH_OPT_OVERRUN.getCode());
                return resultDto;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultDto.setCode(ReturnMessageType.ERROR_500.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_500.getMessage());
            return resultDto;
        }*/
        Account account = accountMapper.getIdRowLockByAccountId(token.getId());
        if(StringUtils.isEmpty(account.getTradePwd())){
            resultDto.setMessage(ReturnMessageType.TRADE_PASSWORD_IS_NULL.getMessage());
            resultDto.setCode(ReturnMessageType.TRADE_PASSWORD_IS_NULL.getCode());
            return resultDto;
        }
        if("1".equals(account.getStatus())){
            resultDto.setMessage(ReturnMessageType.ACCOUNT_NOT_USE.getMessage());
            resultDto.setCode(ReturnMessageType.ACCOUNT_NOT_USE.getCode());
            return resultDto;
        }
        if( "2".equals(account.getStatus())){
            resultDto.setMessage(ReturnMessageType.ACCOUNT_FREEZE.getMessage());
            resultDto.setCode(ReturnMessageType.ACCOUNT_FREEZE.getCode());
            return resultDto;
        }
        try {
            if (!MD5PassEncrypt.checkCrypt(tradePassword, account.getTradePwd())) {
                resultDto.setCode(ReturnMessageType.TRADE_PASSWORD_IS_ERROR.getCode());
                resultDto.setMessage(ReturnMessageType.TRADE_PASSWORD_IS_ERROR.getMessage());
                return resultDto;
            }
        } catch (NoSuchAlgorithmException e) {
            resultDto.setCode(ReturnMessageType.ERROR_500.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_500.getMessage());
            return resultDto;
        }
        //所有正在申请中的提现金额
        BigDecimal hisAllAmount = ordersMapper.findAmountAllIng(token.getId(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        if(hisAllAmount==null){
            hisAllAmount = new BigDecimal(0);
        }
        //判断今日提现的金额(成功和申请中)是否大于最高当天提现的额度
        BigDecimal totalWithAccount = ordersMapper.findAmountToday(token.getId(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        if((totalWithAccount.add(amount)).compareTo(hignMoney)>0){
            if(totalWithAccount!=null){
                String message = "今日已提现"+totalWithAccount+"元，还可提现"+hignMoney.subtract(totalWithAccount)+"元";
                if(hignMoney.compareTo(account.getBalance())>0){
                    message = "今日已提现"+totalWithAccount+"元，还可提现"+account.getBalance().subtract(totalWithAccount)+"元";
                }
                resultDto.setMessage(message);
                resultDto.setCode(ReturnMessageType.BANK_OUT_MONRY_ERROR.getCode());
                return resultDto;
            }else{
                resultDto.setMessage(ReturnMessageType.BANK_OUT_MONRY_ERROR.getMessage());
                resultDto.setCode(ReturnMessageType.BANK_OUT_MONRY_ERROR.getCode());
                return resultDto;
            }
        }
        //所有申请中的提现金额和今天要申请的提现金额的综合 是否大于钱包的金额
        if(account.getBalance().compareTo(hisAllAmount.add(amount))<0){
            if(totalWithAccount!=null){
                String message = "今日已提现"+totalWithAccount+"元，还可提现"+hignMoney.subtract(totalWithAccount)+"元";
                if(hignMoney.compareTo(account.getBalance())>0){
                    message = "今日已提现"+totalWithAccount+"元，还可提现"+account.getBalance().subtract(totalWithAccount)+"元";
                }
                resultDto.setMessage(message);
                resultDto.setCode(ReturnMessageType.NOT_BANK_CARD.getCode());
                return resultDto;
            }else{
                resultDto.setMessage(ReturnMessageType.NOT_BANK_OUT_MONEY.getMessage());
                resultDto.setCode(ReturnMessageType.NOT_BANK_OUT_MONEY.getCode());
                return resultDto;
            }
        }
        //计算出今天还能申请提现多少金额
        BigDecimal needAmount = account.getBalance().subtract(hisAllAmount);
        if(amount.compareTo(needAmount)>0){
            resultDto.setMessage(ReturnMessageType.NOT_BANK_OUT_MONEY.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_BANK_OUT_MONEY.getCode());
            return resultDto;
        }
        BankCard bankCard =  bankCardMapper.getBankCardById(cardId);
        if(bankCard==null){
            resultDto.setMessage(ReturnMessageType.NOT_BANK_CARD.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_BANK_CARD.getCode());
            return resultDto;
        }
        Bank bank = bankService.getBankInfo(bankCard.getBankId());
        BigDecimal chargeMin = systemParaRedisUtil.getAppBankOutChargeMin();    //提现最低多少走费率 :100元
        BigDecimal charge = new BigDecimal(0);
        BigDecimal chargePercent = new BigDecimal(0);
        if(amount.compareTo(chargeMin)<0){   //如果提现金额小于100,则扣手续费
            charge = systemParaRedisUtil.getAppBankOutChargePercentFixed(); //手续费
        }else{
            chargePercent = systemParaRedisUtil.getAppBankOutChargePercent(token.getId());
            charge = (chargePercent.multiply(amount)).setScale(2,BigDecimal.ROUND_HALF_UP);//0.03 手续费
        }
        BigDecimal realAmount = amount.subtract(charge);
        if(realAmount.compareTo(new BigDecimal(0))<0){
            resultDto.setCode("提现金额不能低于"+systemParaRedisUtil.getAppBankOutChargePercentFixed() + "元!");
            resultDto.setMessage(ReturnMessageType.BANK_OUT_ERROR.getMessage());
            return resultDto;
        }
        Orders orders = new Orders();
        orders.setCreateTime(new Date());
        orders.setAmount(amount);
        orders.setAppId(token.getId());
        if(StringUtils.isNotEmpty(bankCard.getCardNo().trim())){
            orders.setBankCardNo(bankCard.getCardNo().trim().replaceAll(" ",""));
        }
        orders.setOrderNo(OrderUtil.getBankOutMerSeqId4Random());
        orders.setRealAmount(realAmount);
        orders.setBankType(PayType.unionpay.getValue());
        orders.setOrderType("1");
        orders.setOptStatus("0");
        orders.setName(bankCard.getName());
        orders.setBankName(bank.getName());
        orders.setMobile(token.getMobile());
        orders.setLlCharge(charge);
        orders.setLlChargePercent(chargePercent);
        ordersMapper.create(orders);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        try{
            //发送通知
            String message ="您刚刚进行了提现操作。提现金额"
                    + orders.getAmount()+"元，提现手续费为" + charge
                    + "元，实际提现" + realAmount + "元，审核通过后，预计将在T+1个工作日内到账至您尾号为"
                    + bankCard.getCardNo().substring(bankCard.getCardNo().length() -4) + "的" + (bank.getName() == null ?  "" : bank.getName()) + "银行卡中，请留意您的银行卡资金变化";
            //message = URLEncoder.encode(message,"utf-8");
            //通知
            Map map1 = new HashMap();
            map1.put("NotificationType", MsgType.withdraw_money_wait.getType() + "");
            JPushLonglian.sendPushNotificationByUserId(orders.getAppId() + "", message, map1);
            //创建消息记录
            String url = website + "/weixin/messageDetails?msg="+message;
            appMsgService.insertV2(MsgType.withdraw_money_wait.getType(), orders.getAppId(), message, "", orders.getId(),url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultDto;
    }


}
