package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.ActResultDto;
import com.longlian.model.DataChargeLevel;
import com.longlian.model.DataChargeRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/10/27.
 */
public interface DataChargeLevelService {

     ActResultDto getDataChargeLevelList(Long userId);
     DataChargeLevel findModelById(Long id);
     List<Map> getDataChargeLevelList(DatagridRequestModel datagridRequestModel,Map map);
     void  doSaveAndUpdate(DataChargeLevel dataChargeLevel) throws Exception;
     void deleteById(String ids) throws Exception;
     DataChargeLevel findById(long id);
     
     void dataCharge(DataChargeRecord dataChargeRecord);
}
