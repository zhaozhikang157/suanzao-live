package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.RecoCourseServive;
import com.longlian.model.RecoCourse;
import com.longlian.type.PosType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recoCourse")
public class RecoCourseController {

    @Autowired
    RecoCourseServive recoCourseServive;

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/recoCourse/index");
        return view;
    }

    @RequestMapping(value = "/getRecoCourseList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getRecoCourseList(DatagridRequestModel datagridRequestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(recoCourseServive.getRecoCourseList(datagridRequestModel, map));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/recoCourse/addOrUpdate");
        view.addObject("id", id);
        return view;
    }
    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        RecoCourse recoCourse = recoCourseServive.findModelById(id);
        if(recoCourse==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(recoCourse);
        }

        return actResult;
    }

    @RequestMapping(value = "/posList" , method = RequestMethod.GET)
    @ResponseBody
    public ActResult posList(){
        ActResult actResult = new ActResult();
        List<Map> posList = PosType.getList();
        actResult.setData(posList);
        return actResult;
    }

    @RequestMapping(value = "/saveRecoCourse" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveFunc( RecoCourse recoCourse) throws  Exception{
        ActResult actResult = new ActResult();
        if(Utility.isNullorEmpty(recoCourse.getId())){
            boolean bo= recoCourseServive.isExistRecoCourse(recoCourse.getCourseId());
            if(bo){
                return ActResult.fail("该课程已经设为推荐了!");
            }
        }
        recoCourseServive.doSaveAndUpdate(recoCourse);
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
            recoCourseServive.deleteById(Long.parseLong(ids));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }


}
