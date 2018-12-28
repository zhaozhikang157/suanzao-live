package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.SystemLogDto;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface SystemLogService {
    List<SystemLogDto> getListPage(DatagridRequestModel datagridRequestModel, SystemLogDto systemLogDto);
    List<SystemLogDto> getList(String type);

}
