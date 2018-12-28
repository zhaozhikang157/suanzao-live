package com.longlian.console.service.impl;

import com.huaxin.util.EndDateParameteUtil;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.console.dao.SystemLogMapper;
import com.longlian.console.service.SystemLogService;
import com.longlian.dto.SystemLogDto;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
@Component("systemLogService")
public class SystemLogServiceImpl implements SystemLogService {
    private static Logger log = LoggerFactory.getLogger(SystemLogServiceImpl.class);

    @Autowired
    SystemLogMapper systemLogMapper;

    @Override
    @DataSource(value= DynamicDataSourceKey.DS_LOG)
    public List<SystemLogDto> getListPage(DatagridRequestModel datagridRequestModel, SystemLogDto systemLogDto) {
        systemLogDto.setCreateTimeEnd(EndDateParameteUtil.parserEndDate(systemLogDto.getCreateTimeEnd()));
        List<SystemLogDto> list=systemLogMapper.getListPage(datagridRequestModel, systemLogDto);
        for (int i=0;i<list.size();i++){  //设置日志类型显示和表类型显示
            list.get(i).setLogTypeStr(LogType.getNameByValue(list.get(i).getLogType()));
            list.get(i).setTableTypeStr(LogTableType.getNameByValue(list.get(i).getTableType()));
        }
        return list;
    }

    @Override
    public List<SystemLogDto> getList(String type) {
        return systemLogMapper.getListByType(type);
    }
}
