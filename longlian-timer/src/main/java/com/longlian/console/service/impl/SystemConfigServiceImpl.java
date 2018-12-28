package com.longlian.console.service.impl;

import com.longlian.console.dao.SystemConfigMapper;
import com.longlian.console.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by liuhan on 2018-06-07.
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService{
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    /**
     * 根据类型查询系统参数
     *
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> findSystemConfigByType(Integer type) {
        return systemConfigMapper.findSystemConfigByType(type);
    }
}
