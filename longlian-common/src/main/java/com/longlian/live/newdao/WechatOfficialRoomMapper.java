package com.longlian.live.newdao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.WechatOfficial;
import com.longlian.model.WechatOfficialRoom;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
@org.apache.ibatis.annotations.Mapper
public interface WechatOfficialRoomMapper extends Mapper<WechatOfficialRoom> {

    List<Map> getBindRoomListByWechatByIdPage(@Param("page")DatagridRequestModel requestModel,@Param("wechatId") String wechatId);
    List<Map> getBindRoomListByWechatById(@Param("wechatId") String wechatId);
    int isBindedRoom(@Param("wechatId") String wechatId,@Param("liveId") Long liveId);
    WechatOfficialRoom findByRoomId(@Param("roomId") long roomId);
    void updateAudit(WechatOfficialRoom wechatOfficialRoom);
    List<WechatOfficialRoom>  selectUseList();
    void updateManager(WechatOfficialRoom wechatOfficialRoom);
    void updateMobile(@Param("id") Long id,@Param("mobile") String mobile);
    void deleteBindRoom(@Param("id") Long id);

    void updateManagerInfo(WechatOfficialRoom wechatOfficialRoom);
}

