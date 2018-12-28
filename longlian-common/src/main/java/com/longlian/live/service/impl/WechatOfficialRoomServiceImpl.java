package com.longlian.live.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.dao.WechatOfficialMapper;
import com.longlian.live.newdao.WechatOfficialRoomMapper;
import com.longlian.live.service.WechatOfficialRoomService;
import com.longlian.model.WechatOfficialRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("wechatOfficialRoomService")
public class WechatOfficialRoomServiceImpl implements WechatOfficialRoomService {

    @Autowired
    WechatOfficialRoomMapper wechatOfficialRoomMapper;
    @Autowired
    WechatOfficialMapper wechatOfficialMapper;

    @Override
    public void addWechatOfficialRooom(WechatOfficialRoom wechatOfficialRoom) {
        wechatOfficialRoomMapper.insert(wechatOfficialRoom);
    }

    @Override
    public List<Map> getBindRoomListByWechatById(DatagridRequestModel requestModel, String wechatId) {
        return wechatOfficialRoomMapper.getBindRoomListByWechatByIdPage(requestModel, wechatId);
    }

    @Override
    public WechatOfficialRoom findById(Long id) {
        return wechatOfficialRoomMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteBindRoom(Long id) {
        wechatOfficialRoomMapper.deleteBindRoom(id);
    }
    @Override
    public boolean isBindedRoom( String wechatId, Long liveId){
        boolean flag=false;
         int isBind =wechatOfficialRoomMapper.isBindedRoom(wechatId,liveId);
         if(isBind>0){
             flag = true;
         }
        return flag;
    }

    @Override
    public  void updateAudit(WechatOfficialRoom wechatOfficialRoom){
        wechatOfficialRoomMapper.updateAudit(wechatOfficialRoom);
    }
    @Override
    public   List<WechatOfficialRoom>  selectUseList(){
        return  wechatOfficialRoomMapper.selectUseList();
    }

    @Override
    public void updateManager(WechatOfficialRoom wechatOfficialRoom){
        wechatOfficialRoomMapper.updateManager(wechatOfficialRoom);
    }

    @Override
    public void updateMobile(Long id,String mobile){
        wechatOfficialRoomMapper.updateMobile(id,mobile);
    }

    @Override
    public List<Map> getBindRoomListByWechatById( String wechatId){
        return  wechatOfficialRoomMapper.getBindRoomListByWechatById(wechatId);
    }

    @Override
    public void updateManagerInfo(WechatOfficialRoom wechatOfficialRoom,BigDecimal payAmount,String appId) {
        wechatOfficialRoomMapper.updateManagerInfo(wechatOfficialRoom);
        Map map=new HashMap<>();
        map.put("appId",appId);
        map.put("payAmount",payAmount);
        wechatOfficialMapper.updatePayAmount(map);
    }
}
