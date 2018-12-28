package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.SystemPara;

import java.util.List;
import java.util.Map;

/**
 * Created by U on 2016/8/10.
 */
public interface SystemParaService {

    List<SystemPara> getList(DatagridRequestModel datagridRequestModel, SystemPara systemPara);
    public List<Map<String , String>> getAllList();
    SystemPara selectById(Long id);


    void create(SystemPara systemPara);

    void update(SystemPara systemPara);

    void deleteById(String ids);

    SystemPara selectByCode(String code);
}
