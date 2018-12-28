package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.WechatOfficialRoom;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WechatOfficialRoomService {


    void addWechatOfficialRooom(WechatOfficialRoom wechatOfficialRoom);
 
    List<Map> getBindRoomListByWechatById(DatagridRequestModel requestModel, String wechatId);
    WechatOfficialRoom findById(Long id);

    void deleteBindRoom(Long id);

    boolean isBindedRoom(String wechatId, Long liveId);

    void updateAudit(WechatOfficialRoom wechatOfficialRoom);
    List<WechatOfficialRoom>  selectUseList();

    void updateManager(WechatOfficialRoom wechatOfficialRoom);

    void updateMobile(Long id,String mobile);

    List<Map> getBindRoomListByWechatById( String wechatId);

    void updateManagerInfo(WechatOfficialRoom wechatOfficialRoom,BigDecimal payAmount,String appId);
}
