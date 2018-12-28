package com.longlian.console.controller;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.YellowResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by admin on 2017/3/16.
 */
@RequestMapping("/yellowResult")
@Controller
public class YellowResultController {
    private static Logger log = LoggerFactory.getLogger(UserCountController.class);
    @Autowired
    YellowResultService yellowResultService;
    /**
     * 跳转页面
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/course/yellowResultList");
        return view;
    }
    /**
     *直播鉴黄详情
     *
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/getYellowResultList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getYellowResultList(DataGridPage requestModel,@RequestParam Map map) {
        System.out.print("********************直播鉴黄详情*************************");
        DatagridResponseModel drm = new DatagridResponseModel();

        drm.setRows(yellowResultService.queryYellowResultByCondition(requestModel, map));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }
}

