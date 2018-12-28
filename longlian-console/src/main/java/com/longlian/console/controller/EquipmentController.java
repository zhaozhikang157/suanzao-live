package com.longlian.console.controller;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.EquipmentService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/28.
 */
@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    @Resource
    private EquipmentService equipmentService;

    /**
     * 跳转到设备ID统计页面
     * @param request
     * @return
     */
    @RequestMapping("/toEquipmentInfoPage")
    public ModelAndView toEquipmentInfoPage(HttpServletRequest request){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("/func/equipment/equipmentInfo");
        return modelAndView;
    }

    /**
     * 查询设备ID统计列表
     * @param requestModel
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/queryEquipmentList")
    @ResponseBody
    public DatagridResponseModel queryEquipmentList(DataGridPage requestModel,@RequestParam Map map,HttpServletRequest request){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<Map> equipmentList=equipmentService.queryEquipmentListPage(requestModel, map);
        drm.setRows(equipmentList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 设备ID统计报表导出
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.GET)
    @ResponseBody
    public void importExcel(HttpServletRequest req, HttpServletResponse response, String name,
                            String equipmentType,  Date startDate,Date endDate) throws IOException {
        Map requestMap=new HashMap<>();
        if(name!=null){
            requestMap.put("name",name);
        }if(equipmentType!=null){
            requestMap.put("equipmentType",equipmentType);
        }if(startDate!=null){
            requestMap.put("startDate",startDate);
        }if(endDate!=null){
            requestMap.put("endDate",endDate);
        }

        ExportExcelWhaUtil exportExcelWhaUtil = equipmentService.importExcel(req, response, requestMap, startDate, endDate);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=courseStatistics.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
