package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.FuncService;
import com.longlian.model.Func;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by admin on 2017/11/21.
 */
@Controller
@RequestMapping("/roomFunc")
public class RoomFuncController {
    private static Logger log = LoggerFactory.getLogger(RoomFuncController.class);

    @Autowired
    FuncService funcService;
    
    
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/roomFunc/index");
        return view;
    }
    @RequestMapping(value = "/getFuncList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getFuncList(DatagridRequestModel datagridRequestModel,Map Map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(funcService.getFuncListPage(datagridRequestModel, Map));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/roomFunc/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        Func func = funcService.findModelById(id);
        if(func==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(func);
        }
        return actResult;
    }
    /**
     * 添加或者修改保存
     * @param func
     * @return
     */
    @RequestMapping(value = "/saveFunc" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveFunc(@RequestBody Func func) throws  Exception{
        ActResult actResult = new ActResult();
        if(!Utility.isNullorEmpty(func.getFuncCode())) {
            if (funcService.isExistFunc(func.getFuncCode().trim())) {
                return ActResult.fail("功能代码重复，请重新填写!");
            }
        }
        funcService.doSaveAndUpdate(func);
        return actResult;
    }

    @RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(String ids) {
        ActResult actResult = new ActResult();
        if (StringUtils.isEmpty(ids)) {
            return ActResult.fail("请选择要删除项!");
        }
        try {
            funcService.deleteById(Long.parseLong(ids));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }


}
