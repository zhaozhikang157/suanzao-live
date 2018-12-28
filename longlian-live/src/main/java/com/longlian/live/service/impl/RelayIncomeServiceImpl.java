package com.longlian.live.service.impl;

import com.longlian.live.dao.RelayIncomeMapper;
import com.longlian.live.service.RelayIncomeService;
import com.longlian.model.RelayIncome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/7/13.
 */
@Service
public class RelayIncomeServiceImpl implements RelayIncomeService {

    @Autowired
    private RelayIncomeMapper relayIncomeMapper;

    /**
     * 新增
     * @param relayIncome
     */
    @Override
    public void insert(RelayIncome relayIncome) {
        relayIncomeMapper.insert(relayIncome);
    }
}
