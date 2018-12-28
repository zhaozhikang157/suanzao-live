package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.DataChargeRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/10/31.
 */
public interface DataChargeRecordService {

    DataChargeRecord getModelByOrderId(Long orderId);

    void insert(DataChargeRecord dataChargeRecord);

    void updateStatus(DataChargeRecord dataChargeRecord);

    List<DataChargeRecord> getAllRecordByRoomId(Long roomId);

    List<DataChargeRecord> getAllRecord();

    Map getAllRecordByAppIdPage(Integer pageSize , Integer offset , Long appId);

    List<Map> getAllBuyFlowPage(DatagridRequestModel requestModel,long roomId,String beginTime ,String endTime ,  Double amount);

    void deleteById(Long recordId) throws Exception;
    DataChargeRecord findById(long id);
}
