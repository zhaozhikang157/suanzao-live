package com.longlian.live.newdao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.DataUseRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
@org.apache.ibatis.annotations.Mapper
public interface DataUseRecordMapper extends Mapper<DataUseRecord> {

    List<Map> getAllUseFlowPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("roomId")Long roomId,
                                @Param("startTime")Date startTime,@Param("endTime")Date endTime,
                                @Param("courseName")String courseName);
}