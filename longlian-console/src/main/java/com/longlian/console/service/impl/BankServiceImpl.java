package com.longlian.console.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.BankMapper;
import com.longlian.console.service.BankService;
import com.longlian.dto.BankDto;
import com.longlian.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by U on 2016/8/1.
 */
@Service("bankService")
public class BankServiceImpl implements BankService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BankMapper bankMapper;

    /**
     * 获取列表
     * @param requestModel
     * @param bankDto
     * @return
     */
    @Override
    public List<BankDto> getListPage(DatagridRequestModel requestModel, BankDto bankDto) {
        return bankMapper.getListPage(requestModel,bankDto);
    }

    /**
     * 添加
     * @param bank
     */
    @Override
    public void create(Bank bank) {
        bankMapper.create(bank);
        resetRedisData();
    }

    /**
     * 修改
     * @param bank
     */
    @Override
    public void update(Bank bank) {
        bankMapper.update(bank);
        resetRedisData();
    }

    /**
     * 设置为禁用状态
     * @param id
     */
    @Override
    public void setStatusForbidden(long id) {
        bankMapper.setStatusForbidden(id);
    }

    /**
     * 设置为启用状态
     * @param id
     */
    @Override
    public void setStatusStart(long id) {
        bankMapper.setStatusStart(id);
    }

    @Override
    public Bank selectById(long id) {
        return bankMapper.selectById(id);
    }

    @Override
    public List<Bank> getBankNameList() {
        return bankMapper.getBankNameList();
    }


    /**
     * 重新设置redis 数据
     */
    public void resetRedisData(){
//        boolean isExistsKey =  redisUtil.exists(RedisKey.ll369_app_bank);//先去redis取
//        if(isExistsKey) redisUtil.del(RedisKey.ll369_app_bank);
//        List<String> redisList = new ArrayList<String>() ;
//        List<Bank> list =  bankMapper.getList4App();
//        for (Bank resource : list){
//            redisList.add(JsonUtil.toJson(resource));
//        }
//        redisUtil.rpushlist(RedisKey.ll369_app_bank , redisList);
    }

}
