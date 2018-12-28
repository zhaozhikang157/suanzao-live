package com.longlian.console.service;

import com.longlian.model.CdnVisit;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by liuhan on 2017-11-07.
 */
public interface SystemConfigService {
    /**
     * 根据类型查询系统参数
     * @param type
     * @return
     */
    Map<String,Object> findSystemConfigByType(Integer type);
}
