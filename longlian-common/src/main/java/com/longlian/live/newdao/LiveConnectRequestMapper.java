package com.longlian.live.newdao;

import com.longlian.model.LiveConnectRequest;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface LiveConnectRequestMapper extends Mapper<LiveConnectRequest> {

    void updateLiveConnectStudentOnlineStatus(@Param("liveConnectRequest") LiveConnectRequest liveConnectRequest);

    void closeLiveConnectRequest(@Param("req") LiveConnectRequest liveConnectRequest);
    int updateStatus(@Param("reqId")long reqId , @Param("status")String status , @Param("msg")String msg);
}