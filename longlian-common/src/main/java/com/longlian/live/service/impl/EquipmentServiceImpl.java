package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.longlian.dto.CourseDto;
import com.longlian.live.dao.EquipmentMapper;
import com.longlian.live.service.EquipmentService;
import com.longlian.model.Equipment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2018/6/27.
 */
@Service("equipmentService")
public class EquipmentServiceImpl implements EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Override
    public void insertEquipment(Equipment equipment) {
        equipmentMapper.insert(equipment);
    }

    @Override
    public void updateByEquipmentId(Equipment equipment) {
        equipmentMapper.update(equipment);
    }

    @Override
    public List<Map> queryEquipmentListPage(DataGridPage requestModel, Map map) {
        return equipmentMapper.queryEquipmentListPage(requestModel, map);
    }

    @Override
    public ExportExcelWhaUtil importExcel(HttpServletRequest req, HttpServletResponse response, Map requestMap, Date startDate, Date endDate) {
        String startTime = DateUtil.format(startDate, "yyyy-MM-dd");
        String endTime = DateUtil.format(endDate, "yyyy-MM-dd");
        String endDateStr = endTime + " 23:59:59";
        String startDateStr = startTime + " 00:00:00";
        requestMap.put("startDate",startDateStr); requestMap.put("endDate",endDateStr);
        List<Equipment> courseList = equipmentMapper.queryEquipmentList(requestMap);
        List<Map> maps=new ArrayList<>();
        try {
            maps = convertListBean2ListMap(courseList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "设备ID统计";
        listStr.add(top1);

        String top3 = "用户id,用户昵称,设备id,设备类型，收集时间";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "appId,name,equipmentId,equipmentType,createTime";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "设备ID统计", req, exceltitel);
        return excel;
    }

    @Override
    public Equipment selectByEquipmentId(String equipmentId) {
        return equipmentMapper.selectByEquipmentId(equipmentId);
    }

    public List<Map> convertListBean2ListMap(List<Equipment> beanList) throws Exception {
        List<Map> mapList = new ArrayList<Map>();
        for(int i=0, n=beanList.size(); i<n; i++){
            Equipment bean = beanList.get(i);
            Map map = convertBean2Map(bean);
            mapList.add(map);
        }
        return mapList;
    }

    public static Map convertBean2Map(Object bean) throws Exception {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0, n = propertyDescriptors.length; i <n ; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    if ("createTime".equals(propertyName)) {
                        returnMap.put(propertyName, DateUtil.format((Date) result));
                    } else{
                        returnMap.put(propertyName, result);
                    }
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

    public List<ExcelTop> getExceltitel(List<String> strs) {
        List<ExcelTop> ets = new ArrayList<ExcelTop>();
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }
        }
        return ets;
    }
}
