package com.longlian.live.newdao;

import com.longlian.model.RoomFunc;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;
@org.apache.ibatis.annotations.Mapper
public interface RoomFuncMapper extends Mapper<RoomFunc> {
    List<RoomFunc> getRoomFuncList(Long roomId);

    Set<String> findRoomFunc(Long roomId);

    int isRoomFunc(@Param("roomId")Long roomId , @Param("code")String code);

    void deleteRoomFunc(@Param("roomId")Long roomId,@Param("code") String del);
}