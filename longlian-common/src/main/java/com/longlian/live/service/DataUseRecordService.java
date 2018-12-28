package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.DataUseRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-11-13.
 */
public interface DataUseRecordService {
    public void save(DataUseRecord record);

    List<Map> getAllUseFlowPage(DatagridRequestModel requestModel , Long roomId,String beginTime , String endTime , String courseName);
}
