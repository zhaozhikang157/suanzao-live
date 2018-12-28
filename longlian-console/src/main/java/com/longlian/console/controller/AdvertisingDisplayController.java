package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.AdvertisingDisplayService;
import com.longlian.model.AdvertisingDisplay;
import com.longlian.type.AdvertisingStatus;
import com.longlian.type.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/23.
 */
@Controller
@RequestMapping("/advertising")
public class AdvertisingDisplayController {
    private static Logger log = LoggerFactory.getLogger(AdvertisingDisplayController.class);


    @Autowired
    AdvertisingDisplayService advertisingDisplayService;

    /**
     * 主页
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/advertising/index");
    }

    /**
     * 去修改页面
     *
     * @param advertising
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(AdvertisingDisplay advertising) throws Exception {
        long id = advertising.getId();
        ModelAndView view = new ModelAndView("/func/advertising/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 查询列表
     *
     * @param advertising requestModel
     * @return
     */
    @RequestMapping("/findAllAdvertising")
    @ResponseBody
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, AdvertisingDisplay advertising) {
        return advertisingDisplayService.getListPage(requestModel, advertising);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteById(long id) {
        ActResult result = new ActResult();
        try{
            advertisingDisplayService.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("广告删除失败");
        }
        return result;
    }

    /**
     * 根据id进行查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getSerById(long id) throws Exception {
        ActResult result = new ActResult();
        AdvertisingDisplay advertising = advertisingDisplayService.findById(id);
        result.setData(advertising);
        return result;
    }


    /**
     * 添加修改
     *
     * @param advertising
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(@RequestBody AdvertisingDisplay advertising) throws Exception {
        ActResult result = new ActResult();
        try{
            result = advertisingDisplayService.doSaveAndUpdate(advertising);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("广告保存失败");
        }
        return result;

    }

    /**
     * @return 获取广告状态
     * @throws Exception
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public ActResult getList() throws Exception {
        ActResult result = new ActResult();
        result.setData(AdvertisingStatus.getList());
        return result;
    }

    /**
     * @return 获取系统类型
     * @throws Exception
     */
    @RequestMapping(value = "/getSystemTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getSystemTypeList() throws Exception {
        ActResult result = new ActResult();
        Map map = new HashMap();
        map.put("systemTypes", SystemType.getList());
        result.setData(map);
        return result;
    }

}