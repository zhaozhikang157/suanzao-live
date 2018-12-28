package com.longlian.console.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.dao.MSystemLogMapper;
import com.longlian.dto.MSystemLogDto;
import com.longlian.console.service.MSystemLogService;
import com.longlian.type.MSysLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
@Component("mSystemLogService")
public class MSystemLogServiceImpl implements MSystemLogService {
    private static Logger log = LoggerFactory.getLogger(MSystemLogServiceImpl.class);

    @Autowired
    MSystemLogMapper mSystemLogMapper;

    @Override
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, MSystemLogDto mSystemLog) {
        DatagridResponseModel model = new DatagridResponseModel();
        List<MSystemLogDto> list = mSystemLogMapper.getListPage(requestModel, mSystemLog);
        for (int i = 0; i < list.size(); i++) {
            String type = list.get(i).getType();
            list.get(i).setTypeName(MSysLogType.getNameByValue(type));
        }
        model.setRows(list);
        model.setTotal(requestModel.getTotal());
        return model;
    }
}