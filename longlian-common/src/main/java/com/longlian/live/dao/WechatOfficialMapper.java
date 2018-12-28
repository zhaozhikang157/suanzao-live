package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.WechatOfficial;
import com.longlian.model.WechatOfficialRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Mapper
public interface WechatOfficialMapper {

    void add(WechatOfficial wechatOfficial);

    //void update(WechatOfficial wechatOfficial);

    void updateWechat(WechatOfficial wechatOfficial);
    int  unauthorized(String appid);
    long getLiveIdByAppid(String appid);
    WechatOfficial selectByAppid(String appid);

    WechatOfficial selectById(long id);
    Map selectBindListById(long id);
    void updateForWechatOfficial(WechatOfficial wechatOfficial);
    void updateTokenByAppid(WechatOfficial wechatOfficial);
    List<WechatOfficial> selectAllOfficial();

    List<WechatOfficial> selectUseList();
    List<WechatOfficial> selectUseTokenList();
    List<Map> getWechatOfficialRoomListPage(@Param("page")DatagridRequestModel requestModel,@Param("map")Map map);
    void updateForWechatOfficialById(WechatOfficial wechatOfficial);
    Map selectLiveRoomById(@Param("roomId") Long roomId);
    void updateAudit(WechatOfficial wechatOfficial);
    void updateManager(WechatOfficial wechatOfficial);

    WechatOfficial findByRoomId(@Param("roomId") long roomId);

    List<WechatOfficial> getAllWechat();

    void updateFreeDate(long id);

    String getContentByWechat();

    void updatePayAmount(@Param("map")Map map);
}
