package com.longlian.console.controller;

import com.alibaba.druid.util.StringUtils;
import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.SystemParaService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.SystemPara;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
@Controller
@RequestMapping(value = "/systemParaController")
public class SystemParaController {
    private static Logger log = LoggerFactory.getLogger(SystemParaController.class);

    @Autowired
    SystemParaService systemParaService;

    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    /**
     *
     * @return
     */
    @RequestMapping("index")
    public ModelAndView index(){
        return new ModelAndView("/func/systemPara/index");
    }


    /**
     * 跳转至添加或编辑页面
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate" ,method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(Long id) throws Exception {
        ModelAndView model= new ModelAndView("/func/systemPara/addOrUpdate");
        model.addObject("id", id);
        return model;

    }

    /**
     * 获取列表
     * @param datagridRequestModel
     * @param systemPara
     * @return
     */
    @RequestMapping(value = "/getList" ,method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList( DatagridRequestModel datagridRequestModel,SystemPara systemPara){
        DatagridResponseModel model =new DatagridResponseModel();
        List<SystemPara> list=systemParaService.getList(datagridRequestModel,systemPara);
        model.setRows(list);
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/selectById" ,method = RequestMethod.GET)
    @ResponseBody
    public ActResult selectById(Long id){
        ActResult actResult=new ActResult();
        if(id!=null && id!=0){
            actResult.setData(systemParaService.selectById(id));
        }
        return actResult;

    }

    /**
     * 添加或修改
     * @param request
     * @param systemPara
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate" ,method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(HttpServletRequest request ,@RequestBody SystemPara systemPara){
        ActResult actResult=new ActResult();
        try{
            if (systemPara.getId()>0){  //修改
                systemParaService.update(systemPara);
            }else {  //添加
                systemParaService.create(systemPara);
            }
        }catch(Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            log.error("系统参数修改失败");
        }
        systemParaRedisUtil.resetSystemParam();
        return actResult;
    }

    /**
     * 根据id删除
     * @return
     */
    @RequestMapping(value = "/deleteById" ,method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteById(String ids){
        ActResult actResult=new ActResult();
        try{
            if(!StringUtils.isEmpty(ids)){
                systemParaService.deleteById(ids);
            }else {
                actResult.setSuccess(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            log.error("系统参数删除失败");
        }
        return actResult;
    }
}
