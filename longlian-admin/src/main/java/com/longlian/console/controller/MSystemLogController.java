package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.MSystemLogService;
import com.longlian.dto.MSystemLogDto;
import com.longlian.type.MSysLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/22.
 */
@RequestMapping("/mSystemLog")
@Controller
public class MSystemLogController {

    private static Logger log = LoggerFactory.getLogger(MSystemLogController.class);
    @Autowired
    MSystemLogService mSystemLogService;

    /**
     * 模块主页面
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/mSystemLog/index");
    }
    /**
     * 获取日志类型
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/getMSysLogTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getList() throws Exception {
        ActResult result = new ActResult();
        Map map = new HashMap();
        map.put("mSysLogTypes", MSysLogType.getList());
        result.setData(map);
        return result;
    }

    /**
     * 展示页面
     *
     * @param mSystemLog requestModel
     * @return
     */
    @RequestMapping("/findAll")
    @ResponseBody
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, MSystemLogDto mSystemLog) {
        return mSystemLogService.getListPage(requestModel, mSystemLog);
    }
}
