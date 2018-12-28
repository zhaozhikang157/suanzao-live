package com.longlian.live.service.impl;

import com.huaxin.util.Utility;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.AccountAddDelReturnType;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.LlAccountMapper;
import com.longlian.live.dao.LlAccountTrackMapper;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.service.LlAccountTrackService;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.LlAccount;
import com.longlian.model.LlAccountTrack;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */
@Service("llAccountService")
public class LlAccountServiceImpl implements LlAccountService {

    @Autowired
    LlAccountMapper accountMapper;

    @Autowired
    LlAccountTrackMapper accountTrackMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    @Autowired
    LlAccountTrackService accountTrackService;

    @Autowired
    SendMsgService sendMsgService;


    /**
     *  钱包账户增加费用
     * @param accountId  账户ID
     * @param addMoney 金额
     * @param accountTrack 保证金记录，费率等
     * @return
     */
    @Override
    public AccountAddDelReturn addAccountBalance(long accountId, BigDecimal addMoney ,LlAccountTrack accountTrack) {
        LlAccount account = new LlAccount();
        account.setAccountId(accountId);
        AccountAddDelReturn accountAddDelReturn = new AccountAddDelReturn();
        //先锁住账号金额
        LlAccount queryAccount = accountMapper.getIdRowLockByAccountId(account.getAccountId());
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
        //创建记录
        accountTrack.setToAccountId(accountId);
        accountTrack.setType("0");
        accountTrack.setCreateTime(new Date());
        accountTrack.setCurrBalance(account.getBalance());//当前余额
        accountTrackMapper.insert(accountTrack);
        LlAccountTrack accountTrack2 = new LlAccountTrack();
        BeanUtils.copyProperties(accountTrack, accountTrack2);
        accountAddDelReturn.setData(accountTrack2);
        //如果第一次则更新第一次关联记录ID
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
    public AccountAddDelReturn delAccountBalance(long accountId, BigDecimal money ,LlAccountTrack accountTrack ,  LlAccount queryAccount) {
        LlAccount account = new LlAccount();
        account.setAccountId(accountId);
        AccountAddDelReturn accountAddDelReturn = new AccountAddDelReturn();
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
        LlAccountTrack accountTrack2 = new LlAccountTrack();
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
    public LlAccount getAccountByUserId(long id) {
        LlAccount account  = accountMapper.getAccountByAppId(id);
        return account;
    }

    /**
     * 获取账户 行级锁
     * @param id
     * @return
     */
    @Override
    public LlAccount getIdRowLockByAccountId(long id) {
        return  accountMapper.getIdRowLockByAccountId(id);
    }

    /**
     * 创建用户账户
     * @param account
     */
    @Override
    public void addAccount(LlAccount account) {
        if(Utility.isNullorEmpty(account.getStatus())) account.setStatus("0");
        accountMapper.add(account);
    }

    @Override
    public Boolean userCreateAccount(List<Long> allAppIds) {
        List<Long> allLLAccountIds = accountMapper.findAllLLAccountIds();
        try {
            allAppIds.removeAll(allLLAccountIds);
            if(allAppIds.size()>0){
                accountMapper.addNoExitAppIds(allAppIds);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public ActResultDto findBalanceIsPay(String courseMoney, long appId) {
        return null;
    }

    @Override
    public ActResultDto checkCode(String code, String mobile) {
        return null;
    }

    @Override
    public ActResultDto resetTradePassword(long accountId, String password) {
        return null;
    }

    @Override
    public ActResultDto checkTradePassword(long accountId, String password) {
        return null;
    }

    @Override
    public ActResultDto sendCheckCode(String mobile) {
        return null;
    }

    @Override
    public int getLlAccountCount() {
        return accountMapper.getLlAccountCount();
    }

    @Override
    public List<Long> findAllLlAccount() {
        return accountMapper.findAllLLAccountIds();
    }

}

