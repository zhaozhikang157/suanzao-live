package com.longlian.live.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.dao.SystemParaMapper;
import com.longlian.live.service.SystemParaService;
import com.longlian.model.SystemPara;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by syl on 2016/8/10.
 */
@Service("systemParaService")
public class SystemParaServiceImpl implements SystemParaService {

    @Autowired
    SystemParaMapper systemParaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SystemPara> getList(DatagridRequestModel datagridRequestModel,SystemPara systemPara) {
        return systemParaMapper.getListPage(datagridRequestModel, systemPara);
    }

    /**
     * 获取所有系统参数
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String , String>> getAllList() {
        return systemParaMapper.getAllList();
    }

    @Override
    @Transactional(readOnly = true)
    public SystemPara selectById(Long id) {
        return systemParaMapper.selectById(id);
    }

    @Override
    public void create(SystemPara systemPara) {
        systemParaMapper.create(systemPara);
    }

    @Override
    public void update(SystemPara systemPara) {
        systemParaMapper.update(systemPara);
    }

    @Override
    public void deleteById(String ids) {
        systemParaMapper.deleteById(ids);
    }
    /**
     * 根据code获取
     * @param code
     * @return
     */
    @Override
    public SystemPara selectByCode(String code) {
        return systemParaMapper.selectByCode(code);
    }

}
