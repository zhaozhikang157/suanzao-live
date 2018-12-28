package com.longlian.live.dao;


import com.huaxin.util.DataGridPage;
import com.longlian.dto.AccountTrackDto;
import com.longlian.dto.CourseDto;
import com.longlian.model.AccountTrack;
import com.longlian.model.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/13.
 */
@Mapper
public interface EquipmentMapper {

    void insert(Equipment equipment);

    List<Map> queryEquipmentListPage(@Param("page")DataGridPage requestModel, @Param("map")Map map);

    List<Equipment> queryEquipmentList(@Param("map")Map requestMap);

    Equipment selectByEquipmentId(@Param("equipmentId")String equipmentId);

    void update(Equipment equipment);
}