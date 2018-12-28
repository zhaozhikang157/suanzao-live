package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.longlian.model.Equipment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/27.
 */
public interface EquipmentService {
    void insertEquipment(Equipment equipment);

    List<Map> queryEquipmentListPage(DataGridPage requestModel, Map map);

    ExportExcelWhaUtil importExcel(HttpServletRequest req, HttpServletResponse response, Map requestMap, Date startDate, Date endDate);

    Equipment selectByEquipmentId(String equipmentId);

    void updateByEquipmentId(Equipment newEquipment);
}
