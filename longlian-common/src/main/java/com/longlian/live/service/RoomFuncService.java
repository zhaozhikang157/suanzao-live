package com.longlian.live.service;

import com.longlian.model.RoomFunc;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/11/22.
 */
public interface RoomFuncService {

    void saveRoomFunc(RoomFunc roomFunc);

    List<RoomFunc> getRoomFuncList(Long roomId);

    Set<String> findRoomFunc(Long roomId);

    Boolean isRoomFunc(Long roomId , String code);

    void deleteRoomFunc(Long roomId, String del);
}
