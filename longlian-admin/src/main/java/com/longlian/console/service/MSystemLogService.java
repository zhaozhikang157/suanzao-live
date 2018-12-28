package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.MSystemLogDto;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface MSystemLogService {


    /**
     * 分页全查:日志管理
     *
     * @param requestModel
     * @param mSystemLog
     * @return
     */
    DatagridResponseModel getListPage(DatagridRequestModel requestModel, MSystemLogDto mSystemLog);
}
