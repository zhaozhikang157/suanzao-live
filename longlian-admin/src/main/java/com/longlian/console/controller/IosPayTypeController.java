package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.IosPayTypeService;
import com.longlian.model.IosPayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
@RequestMapping("/iosPayType")
@Controller
public class IosPayTypeController {
    private static Logger log = LoggerFactory.getLogger(IosPayTypeController.class);

    @Autowired
    IosPayTypeService iosPayTypeService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("")
    public String index(){
        return "/func/iosPayType/index";
    }

    @RequestMapping("/findIosPayTypeInfoPage")
    @ResponseBody
    public DatagridResponseModel findIosPayTypeInfoPage(DataGridPage requestModel, IosPayType iosPayType) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<IosPayType> trackDtoList = iosPayTypeService.findIosPayTypeInfoPage(requestModel, iosPayType);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    @RequestMapping("/toCreateOrUpdate")
    public ModelAndView toCreateOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/iosPayType/toCreateOrUpdate");
        view.addObject("id", id);
        return view;
    }


    @RequestMapping("/findById")
    @ResponseBody
    public ActResult findById(Long id){
        ActResult result = new ActResult();
        IosPayType iosPayType = iosPayTypeService.findById(id);
        result.setData(iosPayType);
        return result;
    }

    @RequestMapping("/createIosPayType")
    @ResponseBody
    public ActResult createIosPayType(@RequestBody IosPayType iosPayType){
        ActResult result = new ActResult();
        Long id = iosPayType.getId();
        if(id == null || id < 1){
            iosPayTypeService.createIosPayType(iosPayType);
        }else{
            iosPayTypeService.updateIosPayType(iosPayType);
        }
        return result;
    }

    @RequestMapping("/updateStatus")
    @ResponseBody
    public ActResult updateStatus(Long id , String status , String type){
        ActResult result = new ActResult();
        iosPayTypeService.updateStatus(id, status, type);
        return result;
    }
}
