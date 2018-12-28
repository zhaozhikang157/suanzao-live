package com.longlian.live.newdao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.DataChargeRecordDto;
import com.longlian.model.DataChargeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
@org.apache.ibatis.annotations.Mapper
public interface DataChargeRecordMapper extends Mapper<DataChargeRecord> {
    Long getBalAmount(@Param("roomId") Long roomId);

    Long getReduce(@Param("roomId") Long roomId);

    DataChargeRecord findModelByOrderId(long orderId);

    List<DataChargeRecord> getAllRecordByRoomId(long roomId);

    Long getBanlanceByUserId(@Param("userId") Long userId);

    List<DataChargeRecordDto> getAllRecordByAppIdPage(@Param("page")DataGridPage page , @Param("appId")long appId);

    Long getReduceDataCountbByAppId(@Param("appId") Long appId);

    List<Map> getAllBuyFlowPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("roomId")Long roomId,
                                @Param("startTime")Date startTime , @Param("endTime")Date endTime ,
                                @Param("amount")Double amount);

    void deleteByIds(@Param(value = "recordId")Long recordId);
}