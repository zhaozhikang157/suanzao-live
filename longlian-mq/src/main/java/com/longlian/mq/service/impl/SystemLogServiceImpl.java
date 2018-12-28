package com.longlian.mq.service.impl;


import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.longlian.model.SystemLog;
import com.longlian.mq.dao.SystemLogMapper;
import com.longlian.mq.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/9/9.
 */
@Service("systemLogService")
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    SystemLogMapper systemLogMapper;

    @Override
    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    @Transactional(rollbackFor = Exception.class)
    public void insert(SystemLog systemLog) {
        systemLogMapper.insert(systemLog);
    }
}
