package com.longlian.console.controller;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.LlAccountTrackDto;
import com.longlian.live.service.LlAccountTrackService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/6.
 */
@RequestMapping("/llAccountTrack")
@Controller
public class LlAccountTrackController {
    private static Logger log = LoggerFactory.getLogger(LlAccountTrackController.class);

    @Autowired
    LlAccountTrackService llAccountTrackService;

    /**
     * 充值 - 电子回单
     * @return
     */
    @RequestMapping("")
    public String index(){
        return "/func/llAccountTrack/index";
    }


    /**
     * 充值 - 电子回单
     * @return
     */
    @RequestMapping("/recharge")
    public String recharge(){
        return "/func/llAccountTrack/recharge";
    }

    /**
     * 电子回单充值学币
     * @param requestModel
     * @param llAccountTrackDto
     * @return
     */
    @RequestMapping("/getRechargePage")
    @ResponseBody
    public DatagridResponseModel getRechargePage(DataGridPage requestModel, LlAccountTrackDto llAccountTrackDto){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<LlAccountTrackDto> trackDtoList = llAccountTrackService.getRechargePage(requestModel, llAccountTrackDto);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 电子回单(充值)导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcel")
    public void exportExcelorderElectronic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("orderNo", request.getParameter("orderNo"));
            map.put("bankType", request.getParameter("bankType"));
            map.put("startTime", request.getParameter("startTime"));
            map.put("endTime", request.getParameter("endTime"));
            map.put("appMobile", request.getParameter("appMobile"));
            String path = llAccountTrackService.exportExcel(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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

    /**
     * 充值 - 电子回单 - 详情
     * @return
     */
    @RequestMapping("/findOrderInfo")
    public ModelAndView rechargePrint(long id){
        ModelAndView view = new ModelAndView("/func/llAccountTrack/rechargePrint");
        Map map = llAccountTrackService.findOrderInfo(id);
        view.addObject("map",map);
        return view;
    }
}
