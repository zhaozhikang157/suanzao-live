package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.Const;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.AccountAddDelReturnType;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.AccountTrackMapper;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.dao.CourseCommonMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.ShortMessage;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.*;
import com.longlian.type.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2016/8/13.
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    AccountTrackMapper accountTrackMapper;

    @Autowired
    private CourseBaseNumMapper courseBaseNumMapper;

    @Autowired
    MessageClient messageClient;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    @Autowired
    RewardRecordService rewardRecordService;

    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    @Autowired
    AccountTrackService accountTrackService;

    @Autowired
    SendMsgService sendMsgService;

    @Autowired
    CourseCommonMapper courseCommonMapper;

    @Autowired
    ShortMessage shortMessage;

    private static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    /**
     *  第三方充值 --VIP  年费
     * @param accountId
     * @param addMoney
     * @param orderId
     * @return
     */
    @Override
    public AccountAddDelReturn addThirdPayZiJinChi(long accountId,BigDecimal addMoney ,long orderId ,String payType )    {
        //资金池
        AccountTrack shopBondTrack = new AccountTrack();
        shopBondTrack.setAmount(addMoney);
        shopBondTrack.setFormAccountId(accountId);
        shopBondTrack.setOrderId(orderId);
        shopBondTrack.setToAccountId(Const.longlian_live_zi_jin_chi_account_id);
        //关联的
        AccountAddDelReturn accountAddDelReturn = addAccountBalance(Const.longlian_live_zi_jin_chi_account_id, shopBondTrack.getAmount(), shopBondTrack);
        return  accountAddDelReturn;
    }

    /**
     *  提现
     * @param accountId
     * @param addMoney
     * @param orderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountAddDelReturn delZiJinChiBalance(long accountId,BigDecimal addMoney ,long orderId ,String payType )    {
        //资金池
        AccountTrack shopBondTrack = new AccountTrack();
        shopBondTrack.setAmount(addMoney);
        shopBondTrack.setFormAccountId(accountId);
        shopBondTrack.setOrderId(orderId);
        shopBondTrack.setToAccountId(Const.longlian_live_zi_jin_chi_account_id);
        //关联的
        AccountAddDelReturn accountAddDelReturn = delAccountBalance(Const.longlian_live_zi_jin_chi_account_id, shopBondTrack.getAmount(), shopBondTrack);
        return  accountAddDelReturn;
    }

    /**
     *  钱包账户增加费用
     * @param accountId  账户ID
     * @param addMoney 金额
     * @param accountTrack 保证金记录，费率等
     * @return
     */
    @Override
    public AccountAddDelReturn addAccountBalance(long accountId, BigDecimal addMoney ,AccountTrack accountTrack) {
        Account account = new Account();
        account.setAccountId(accountId);
        AccountAddDelReturn accountAddDelReturn = new AccountAddDelReturn();
        //先锁住账号金额
        Account queryAccount = accountMapper.getIdRowLockByAccountId(account.getAccountId());
        if(queryAccount == null){
            accountAddDelReturn.setCode(AccountAddDelReturnType.not_exstis.getValue());
            accountAddDelReturn.setDesc(AccountAddDelReturnType.not_exstis.getText());
            return accountAddDelReturn;
        }
        accountAddDelReturn.setPreBalance(queryAccount.getBalance());

        //更新保证金
        account.setBalance(queryAccount.getBalance().add(addMoney));
        account.setAddTotalAmount(queryAccount.getAddTotalAmount().add(addMoney));
        accountMapper.addUpdateByAccountId(account);
        accountAddDelReturn.setAfterBalance(account.getBalance());
        //创建保证金记录
        accountTrack.setToAccountId(accountId);
        accountTrack.setType("0");
        accountTrack.setCreateTime(new Date());
        accountTrack.setCurrBalance(account.getBalance());//当前保证金余额
        accountTrackMapper.insert(accountTrack);
        AccountTrack accountTrack2 = new AccountTrack();
        BeanUtils.copyProperties(accountTrack, accountTrack2);
        accountAddDelReturn.setData(accountTrack2);
        //如果第一次则更新保证金第一次关联记录ID
        if(queryAccount.getFormTrackId() == 0){
            queryAccount.setFormTrackId(accountTrack.getId());
            accountMapper.updateTrackIdByAccountId(queryAccount);
        }
        return accountAddDelReturn;
    }


    /**
     *  余额点钱
     * @param accountId  账户ID
     * @param money 金额
     * @param accountTrack 保证金记录，费率等
     * @return
     */
    @Override
    public AccountAddDelReturn delAccountBalance(long accountId, BigDecimal money ,AccountTrack accountTrack) {
        Account account = new Account();
        account.setAccountId(accountId);
        AccountAddDelReturn accountAddDelReturn = new AccountAddDelReturn();
        //先锁住店铺保证金金额
        Account queryAccount = accountMapper.getIdRowLockByAccountId(account.getAccountId());
        if(queryAccount == null){
            accountAddDelReturn.setCode(AccountAddDelReturnType.not_exstis.getValue());
            accountAddDelReturn.setDesc(AccountAddDelReturnType.not_exstis.getText());
            return accountAddDelReturn;
        }
        //判断保证金是否够
        if(money.compareTo(queryAccount.getBalance()) > 0){
            accountAddDelReturn.setPreBalance(queryAccount.getBalance());
            accountAddDelReturn.setCode(AccountAddDelReturnType.bal_not_enough.getValue());
            accountAddDelReturn.setDesc(AccountAddDelReturnType.bal_not_enough.getText());
            return accountAddDelReturn;
        }
        accountAddDelReturn.setPreBalance(queryAccount.getBalance());

        //更新保证金
        account.setBalance(queryAccount.getBalance().subtract(money));
        account.setDelTotalAmount(queryAccount.getDelTotalAmount().add(money));
        accountMapper.delUpdateByAccountId(account);
        accountAddDelReturn.setAfterBalance(account.getBalance());
        //创建保证金记录
        accountTrack.setToAccountId(accountId);
        accountTrack.setType("1");
        accountTrack.setCreateTime(new Date());
        accountTrack.setCurrBalance(account.getBalance());//当前保证金余额
        accountTrackMapper.insert(accountTrack);
        AccountTrack accountTrack2 = new AccountTrack();
        BeanUtils.copyProperties(accountTrack, accountTrack2);
        accountAddDelReturn.setData(accountTrack2);
        //如果第一次则更新保证金第一次关联记录ID
        if(queryAccount.getFormTrackId() == 0){
            queryAccount.setFormTrackId(accountTrack.getId());
            accountMapper.updateTrackIdByAccountId(queryAccount);
        }
        return accountAddDelReturn;
    }


    /**
     * 根据ID获取账户信息
     */
    @Override
    public Account getAccountByUserId(long id) {
        Account account  = accountMapper.getAccountByAppId(id);
        return account;
    }

    /**
     * 获取账户 行级锁
     * @param id
     * @return
     */
    @Override
    public Account getIdRowLockByAccountId(long id) {
        return  accountMapper.getIdRowLockByAccountId(id);
    }

    /**
     * 创建用户账户
     * @param account
     */
    @Override
    public void addAccount(Account account) {
        if(Utility.isNullorEmpty(account.getStatus())) account.setStatus("0");
        accountMapper.add(account);
    }

    /**
     * 重置交易密码
     * @param accountId
     * @param password
     * @return
     */
    @Override
    public ActResultDto resetTradePassword(long accountId, String password) {
        ActResultDto resultDto = new ActResultDto();
        if(StringUtils.isEmpty(password)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }
        Account account =  accountMapper.getAccountByAppId(accountId);
        if(account==null){
            resultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
        }
        try {
            String newPassword = MD5PassEncrypt.crypt(password);
            accountMapper.resetTradePassword(accountId,newPassword);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            resultDto.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return resultDto;
    }

    /**
     * 校验原密码
     * @param accountId
     * @param password
     * @return
     */
    @Override
    public ActResultDto checkTradePassword(long accountId, String password) {
        ActResultDto resultDto = new ActResultDto();
        if(StringUtils.isEmpty(password)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(checkPassword(accountId, password)){
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }else{
            resultDto.setCode(ReturnMessageType.TRADE_PASSWORD_IS_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.TRADE_PASSWORD_IS_ERROR.getMessage());
            return resultDto;
        }
    }

    /**
     * 发送验证码
     * @param mobile
     * @return
     */
    @Override
    @Transactional
    public ActResultDto sendCheckCode(String mobile) {
        ActResultDto actResultDto = new ActResultDto();
        String number = getCheckCode();
        String content = "您的验证码为:" + number + "请在有效期1分钟内验证,注意安全！【酸枣在线】";
        //messageClient.sendMessage(mobile, content);
        shortMessage.send_message2( Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getName()),
                Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getCode()), Optional.of(mobile),number);
        redisUtil.set(RedisKey.ll_live_trade_password_sms + mobile, number);          //存放到redis
        redisUtil.expire(RedisKey.ll_live_trade_password_sms + mobile, 2 * 60);     //设置验证码有效期120秒
        actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return actResultDto;
    }

    /**
     * 校验验证码
     * @param code
     * @param mobile
     * @return
     */
    @Override
    @Transactional
    public ActResultDto checkCode(String code,String mobile) {
        ActResultDto result = new ActResultDto();
        String extend = redisUtil.get(RedisKey.ll_live_trade_password_sms + mobile);
        if(StringUtils.isEmpty(extend)){//验证码超时
            result.setCode(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getCode());
            result.setMessage(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getMessage());
            return result;
        }
        if (!code.equals(extend)) {       //验证
            result.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return result;
        }
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        redisUtil.del(RedisKey.ll_live_trade_password_sms + mobile); //删除redis里面的验证码
        return result;
    }

    /**
     * 忘记交易密码
     * @param accountId
     * @return
     */
    @Override
    @Transactional
    public ActResultDto forgetTradePwd(long accountId) {
        ActResultDto resultDto = new ActResultDto();
        int status = accountMapper.forgetTradePwd(accountId);
        if(status>0){
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }else{
            resultDto.setCode(ReturnMessageType.DATABASE_ERR.getCode());
            resultDto.setMessage(ReturnMessageType.DATABASE_ERR.getMessage());
        }
        return resultDto;
    }

    @Override
    public boolean checkPassword(long accountId, String password) {
        if(StringUtils.isEmpty(password)){
            return false;
        }
        Account account = accountMapper.getById(accountId);
        try {
            if (MD5PassEncrypt.checkCrypt(password, account.getTradePwd() == null  ? "" :  account.getTradePwd())) {
                return true;
            }else{
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }


    // 产生6位长度的验证码
    public String getCheckCode() {
        String checkCode = "";
        for (int i = 0; i < 6; i++) {
            String code = (int) Math.floor(Math.random() * 10) + "";
            checkCode += code;
        }
        return checkCode;
    }

    /**
     * 老师讲课奖励 ---结束就奖励
     * @param course 课程
     * @param payMenCount  付费人数
     */
    @Override
    public boolean teachCourseReward(Course course , int payMenCount)throws  Exception{
        boolean isSuccess = false;
        /*String expr = "courseAmout>=1 && coursePayMenCount>=5 && cycTime>=3 && " +
                "(appointedDate==20170223 || appointedDate==20170224) ";*/
        String expr = systemParaRedisUtil.getTeachCourseRewardRule();
        String teach_course_reward_men_count_isfree = systemParaRedisUtil.getTeachCourseRewardMenCountIsFree();//老师讲课奖励,统计人数（不免费、所有的） 0-所有的 1-收费的且成功的
        long teachAppId = course.getAppId();
        if(!Utility.isNullorEmpty(expr)){
            payMenCount = joinCourseRecordService.getPaySuccessRecordCount(course.getId() , teach_course_reward_men_count_isfree);
            if (payMenCount <= 0) {
                return  false;
            }
            String lastDate = redisUtil.hget(RedisKey.ll_live_teach_course_last_reward_time , teachAppId + "");
            int spaceDays = 999999999;//为空设置最大的
            if(!Utility.isNullorEmpty(lastDate)){
                Date date = Utility.parseDate(lastDate);
                spaceDays = DateUtil.getIntervalOfDays(date , new Date());//获取两个日期相隔天数
            }
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            engine.put("courseAmout",course.getChargeAmt().doubleValue());//课程最低金额(元)
            engine.put("coursePayMenCount",payMenCount);//课程付费人数
            engine.put("cycTime",spaceDays);//奖励周期天数
            String currDateSrt = Utility.getCurDateTimeStr("yyyy-MM-dd");
            engine.put("appointedDate", currDateSrt);//指定日期
            Object result = engine.eval(expr);

            System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);
            Boolean isReward = (Boolean) result;
            BigDecimal rewardSingleAmount = systemParaRedisUtil.getTeachCourseRewardSingleAmount();
            if(isReward &&
                    rewardSingleAmount != null &&
                    rewardSingleAmount.compareTo(new BigDecimal(0))> 0 ){
                //奖励 老师
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setFormAccountId(0);
                accountTrack.setAmount(rewardSingleAmount.multiply(BigDecimal.valueOf(payMenCount)));
                accountTrack.setReturnMoneyLevel(AccountFromType.teach_course.getValue());
                accountTrack.setOrderId(0);
                addAccountBalance(teachAppId, accountTrack.getAmount(), accountTrack);
                //存取奖励记录
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.setCreateTime(accountTrack.getCreateTime());
                rewardRecord.setAppId(teachAppId);
                rewardRecord.setAmount(accountTrack.getAmount());
                rewardRecord.setSingleAmount(rewardSingleAmount);
                rewardRecord.setMenCount(payMenCount);
                rewardRecord.setType(LongLianRewardType.teach_course.getValue());
                rewardRecord.setCourseId(course.getId());
                rewardRecordService.insert(rewardRecord);
                //存取redis 老师奖励最后一次时间
                redisUtil.hset(RedisKey.ll_live_teach_course_last_reward_time, teachAppId + "", currDateSrt);
                isSuccess = true;
                //发送通知
                String count = "恭喜您！您的直播间听课人数达到"+payMenCount+"人，获得平台奖励"+accountTrack.getAmount()+"元，请在我的收益中查看！";
                sendMsgService.sendMsg(teachAppId, "", MsgType.SYS_AWARD.getType(), 0, count, "");
            }
        }
        return  isSuccess;
    }

    /**
     * 老师讲课奖励 ---结束就奖励
     */
    @Override
    public boolean newTeachCourseReward(Course course , boolean isNeedAddBaseNum,Long payMenCount)throws  Exception{
        boolean isSuccess = false;
        String expr = systemParaRedisUtil.getNewTeachCourseRewardRule();
//        String teach_course_reward_men_count_isfree = systemParaRedisUtil.getTeachCourseRewardMenCountIsFree();//老师讲课奖励,统计人数（不免费、所有的） 0-所有的 1-收费的且成功的
        long teachAppId = course.getAppId();
//        int payMenCount = 0;
        if(!Utility.isNullorEmpty(expr)){
            //找到该课程的付费人数
//            payMenCount = joinCourseRecordService.getPaySuccessRecordCount(course.getId() , teach_course_reward_men_count_isfree);
            long baseCount = 0l;
            if (isNeedAddBaseNum) {
                //课程参加学习基数,后台设置的
                CourseBaseNum base = courseBaseNumMapper.selectByCourse(course.getId()  , "0");
                if (base != null) {
                    baseCount = base.getCount();
                    //实际人数+后台设置人数（虚数）
                    payMenCount += baseCount;
                }
            }


            log.info("平台奖励的课程:" + course.getId() + " , 付费人数:" + payMenCount + " ,appId=" + course.getAppId());
            if (payMenCount <= 0) {
                return  false;
            }
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            engine.put("courseAmout",course.getChargeAmt().doubleValue());//课程最低金额(元)
            engine.put("coursePayMenCount",payMenCount);//课程付费人数
            Object result = engine.eval(expr);
            System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);
            Boolean isReward = (Boolean) result;
            //老师讲课奖励单个人金额
            BigDecimal rewardSingleAmount = systemParaRedisUtil.getTeachCourseRewardSingleAmount();
            if(isReward &&
                    rewardSingleAmount != null &&
                    rewardSingleAmount.compareTo(new BigDecimal(0))> 0 ){
                //奖励 老师
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setFormAccountId(0);
                accountTrack.setAmount(rewardSingleAmount.multiply(BigDecimal.valueOf(payMenCount)));
                accountTrack.setReturnMoneyLevel(AccountFromType.teach_course.getValue());
                accountTrack.setOrderId(0);
                addAccountBalance(teachAppId, accountTrack.getAmount(), accountTrack);
                //存取奖励记录
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.setCreateTime(accountTrack.getCreateTime());
                rewardRecord.setAppId(teachAppId);
                rewardRecord.setAmount(accountTrack.getAmount());
                rewardRecord.setSingleAmount(rewardSingleAmount);
                rewardRecord.setMenCount(payMenCount.intValue());
                rewardRecord.setType(LongLianRewardType.teach_course.getValue());
                rewardRecord.setCourseId(course.getId());
                //后台设置人数（虚数）是多少
                rewardRecord.setLlAddMenCount((int) baseCount);
                rewardRecordService.insert(rewardRecord);
                log.info("返现奖励:" + JsonUtil.toJson(rewardRecord));
                isSuccess = true;
                //发送通知
                String count = "恭喜您！您的直播间听课人数达到"+payMenCount+"人，获得平台奖励"+accountTrack.getAmount()+"元，请在我的收益中查看！";
                log.info("发送通知: count = "+count);
                sendMsgService.sendMsg(teachAppId, "", MsgType.SYS_AWARD.getType(), 0, count, "");
            }
        }
        return  isSuccess;
    }

    /**
     * 推介老师有奖
     * @param invitationAppId 邀请人ID
     * @param teachAppId 老师ID
     * @param payMenCount 人数
     * @return
     * @throws Exception
     */
    @Override
    public boolean invicationTeachReward(long invitationAppId, long teachAppId, int payMenCount) throws Exception {
        boolean isSuccess = false;
        String expr = systemParaRedisUtil.getInvitatitonTeachRewardRule();
        if(!Utility.isNullorEmpty(expr) && payMenCount >0){
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            engine.put("firstPayMenCount",payMenCount);//课程最低金额(元)
            Object result = engine.eval(expr);
            System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);
            Boolean isReward = (Boolean) result;
            BigDecimal rewardAmount = systemParaRedisUtil.getInvitatitonTeachRewardAmount();
            if(isReward &&
                    rewardAmount != null &&
                    rewardAmount.compareTo(new BigDecimal(0))> 0 ){
                //奖励 老师
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setFormAccountId(0);
                accountTrack.setAmount(rewardAmount);
                accountTrack.setReturnMoneyLevel(AccountFromType.introducer_teach.getValue());
                accountTrack.setOrderId(0);
                addAccountBalance(invitationAppId, accountTrack.getAmount(), accountTrack);
                //存取奖励记录
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.setCreateTime(accountTrack.getCreateTime());
                rewardRecord.setAppId(invitationAppId);
                rewardRecord.setRelationAppId(teachAppId);
                rewardRecord.setAmount(accountTrack.getAmount());
                rewardRecord.setSingleAmount(accountTrack.getAmount());
                rewardRecord.setMenCount(1);
                rewardRecord.setType(LongLianRewardType.invitation_teach.getValue());
                rewardRecord.setCourseId(0);
                rewardRecordService.insert(rewardRecord);
                //存取redis 老师奖励最后一次时间
                redisUtil.sadd(RedisKey.ll_live_reward_success_invitation_teach_record, teachAppId + "");
                isSuccess = true;
                //发送通知
                String count = "恭喜您！您邀请老师授课成功，获得平台奖励"+rewardAmount+"元，请在我的收益中查看！";
                sendMsgService.sendMsg(invitationAppId, "", MsgType.SYS_AWARD.getType(), 0, count, "");
            }
        }
        return false;
    }

    /**
     * 老师粉丝关注奖励
     * @param teachAppId 老师appId
     * @param followRewardId  //奖励规则ID
     * @param amount  //奖励钱
     * @return
     */
    @Override
    public boolean teachFollowReward(long teachAppId, long followRewardId  , BigDecimal amount ,String remark) {
        boolean isSuccess = false;
        //奖励 老师
        AccountTrack accountTrack = new AccountTrack();
        accountTrack.setFormAccountId(0);
        accountTrack.setAmount(amount);
        accountTrack.setReturnMoneyLevel(AccountFromType.teach_follow.getValue());
        accountTrack.setOrderId(0);
        addAccountBalance(teachAppId, accountTrack.getAmount(), accountTrack);
        //存取奖励记录
        RewardRecord rewardRecord = new RewardRecord();
        rewardRecord.setCreateTime(accountTrack.getCreateTime());
        rewardRecord.setAppId(teachAppId);
        rewardRecord.setRelationAppId(0);
        rewardRecord.setAmount(accountTrack.getAmount());
        rewardRecord.setSingleAmount(accountTrack.getAmount());
        rewardRecord.setMenCount(1);
        rewardRecord.setType(LongLianRewardType.teach_follow.getValue());
        rewardRecord.setFollowRewardId(followRewardId);
        rewardRecord.setCourseId(0);
        rewardRecord.setRemark(remark);
        rewardRecordService.insert(rewardRecord);
        //存取redis 老师粉丝关注奖励
        redisUtil.sadd(RedisKey.ll_live_follow_reward_record_ + teachAppId,  followRewardId + "");
        isSuccess = true;
        //发送通知
        Long number = redisUtil.scard(RedisKey.ll_live_follow_reward_record_ + teachAppId);
        String count = "恭喜您！您的直播间关注人数达到"+number+"人，获得平台奖励"+amount+"元，请在我的收益中查看！";
        sendMsgService.sendMsg(teachAppId, "", MsgType.SYS_AWARD.getType(), 0, count, "");
        return isSuccess;
    }

    @Override
    public Account getAccountByAppId(long appId) {
        return accountMapper.getById(appId);
    }

    /**
     * 账号返钱
     * @param orders
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResult accountReward(Orders orders) {
        List<AccountTrack> list = accountTrackService.getListByOrderId(orders.getId());
        if(list.size() != 2){
            return ActResult.fail("该订单不能作废,并返钱,可能是数据存在问题");
        }
        for(int i = 0 ,len = list.size() ; i<len ; i++){
            AccountTrack accountTrack = list.get(i);
            accountTrack.setOrderId(orders.getId());
            accountTrack.setReturnMoneyLevel(AccountFromType.return_money.getValue());
            addAccountBalance(accountTrack.getToAccountId(), accountTrack.getAmount() ,accountTrack);
        }
        try{
            //发送通知
            String levelName = "";
            String message ="您申请的一笔提现金额"
                    + orders.getRealAmount()+"元，提现手续费为" + orders.getLlCharge()
                    + "元，实际提现" +  orders.getRealAmount().subtract( orders.getLlCharge()) + "元，由于银行原因，未能到帐。提现金额"
                    + orders.getRealAmount() + "元已经退回到您的钱包中，请留意钱包资金变化。给您带来的不便深感抱歉，如有疑问，请致电400客户服务热线。";
            //通知
            Map map1 = new HashMap();
            map1.put("NotificationType",MsgType.withdraw_money_not.getType());
            JPushLonglian.sendPushNotificationByUserId(orders.getAppId() + "", message, map1);
            //创建消息记录
//            appMsgService.insert(1 ,orders.getAppId() , message, AppMsgType.bankout_return_money.getType() , orders.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ActResult.successSetMsg("返钱成功");
    }

    /**
     * 获取钱包余额并和需支付课程费用对比
     * @param courseMoney
     * @param appId
     * @return
     */
    @Override
    public ActResultDto findBalanceIsPay(String courseMoney, long appId) {
        ActResultDto resultDto  = new ActResultDto();
        if(StringUtils.isEmpty(courseMoney)){
            resultDto.setMessage(ReturnMessageType.GET_COURSE_MONEY_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.GET_COURSE_MONEY_ERROR.getCode());
            return resultDto;
        }
        Account account = accountMapper.getById(appId);
        if(account == null){
            resultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            return resultDto;
        }
        if(!"0".equals(account.getStatus())){
            resultDto.setMessage(ReturnMessageType.ACCOUNT_FREEZE.getMessage());
            resultDto.setCode(ReturnMessageType.ACCOUNT_FREEZE.getCode());
            return resultDto;
        }
        if(StringUtils.isEmpty(account.getTradePwd())){
            resultDto.setMessage(ReturnMessageType.TRADE_PASSWORD_IS_NULL.getMessage());
            resultDto.setCode(ReturnMessageType.TRADE_PASSWORD_IS_NULL.getCode());
            return resultDto;
        }
        BigDecimal withMoneyIng = accountMapper.findAmountAllIng(appId,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        BigDecimal balance = account.getBalance().subtract(withMoneyIng);
        if(balance.compareTo(new BigDecimal(courseMoney)) < 0) {
            resultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
            resultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
            return resultDto;
        }
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return resultDto;
    }

    /**
     * 枣币转换为学币
     * @return
     */
    public ActResultDto transforZb2Xb(long appId, String amount){
        ActResultDto dto = new ActResultDto();
        Account account = accountMapper.getAccountByAppId(appId);
        BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(amount));
        if(decimal.compareTo(account.getBalance()) > 0){
            dto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
            dto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
            return dto;
        }
        AccountTransRecord record = new AccountTransRecord();
        int res = accountMapper.updateZbDown(appId, decimal);
        if(res > 0){
            res = accountMapper.updateXbUp(appId, decimal);
            if(res > 0){
                //记录枣币转换学币记录
                record.setAppId(appId);
                record.setAmount(decimal);
                record.setStatus("1");
                accountMapper.createAccountTransRecord(record);
                dto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                dto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                return dto;
            }
        }
        record.setAppId(appId);
        record.setAmount(decimal);
        record.setStatus("0");
        accountMapper.createAccountTransRecord(record);
        dto.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
        dto.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        return dto;
    }

    /**
     * 获取枣币转换学币历史记录
     * @param appId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List getAccountTransRecord(long appId, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if(pageNum != null) dg.setCurrentPage(pageNum);
        if(pageSize != null) dg.setPageSize(pageSize);
        return accountMapper.getAccountTransRecordPage(appId,dg);
    }

    /**
     * 是否可以枣币兑换学币
     */
    public String getIsZbTransXb(){
        return accountMapper.getIsZbTransXb();
    }
}

