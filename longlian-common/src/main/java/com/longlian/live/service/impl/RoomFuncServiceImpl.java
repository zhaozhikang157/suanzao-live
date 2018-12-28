package com.longlian.live.service.impl;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.newdao.RoomFuncMapper;
import com.longlian.live.service.RoomFuncService;
import com.longlian.live.util.aliyun.MixLiveUtil;
import com.longlian.model.RoomFunc;
import com.longlian.type.FuncCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/11/22.
 */
@Service("roomFuncService")
public class RoomFuncServiceImpl implements RoomFuncService {
    @Autowired
    RoomFuncMapper roomFuncMapper;
    @Autowired
    RedisUtil redisUtil;

  @Override
   public void saveRoomFunc(RoomFunc roomFunc){
         roomFuncMapper.insert(roomFunc);
        redisUtil.del(RedisKey.ll_room_fun + roomFunc.getRoomId());
        //增加连麦配置
        if (roomFunc.getFuncCode().equals(FuncCode.live_connect.getValue())) {
            //MixLiveUtil.addMixConfig(mainDomain , String.valueOf(roomFunc.getRoomId()));
        }
  }

    @Override
    public List<RoomFunc> getRoomFuncList(Long roomId){
        return roomFuncMapper.getRoomFuncList(roomId);
    }

    /**
     * 查询所有的roomId权限
     * @param roomId
     * @return
     */
    @Override
    public Set<String> findRoomFunc(Long roomId) {
        loadToRedis(roomId);
        return redisUtil.smembers(RedisKey.ll_room_fun + roomId);
    }

    private void loadToRedis(Long roomId) {
        if (!redisUtil.exists(RedisKey.ll_room_fun + roomId)) {
            Set<String> setFunc = roomFuncMapper.findRoomFunc(roomId);
            for(String s : setFunc){
                redisUtil.sadd(RedisKey.ll_room_fun + roomId, s);
            }
            redisUtil.expire(RedisKey.ll_room_fun + roomId, 60 * 60 * 24 * 3);
        }
    }

    @Override
    public Boolean isRoomFunc(Long roomId , String code) {
        loadToRedis(roomId);
        return redisUtil.sismember(RedisKey.ll_room_fun + roomId , code);
    }

    @Override
    public void deleteRoomFunc(Long roomId, String del) {
        roomFuncMapper.deleteRoomFunc(  roomId,   del);
        redisUtil.del(RedisKey.ll_room_fun + roomId);
    }
}
