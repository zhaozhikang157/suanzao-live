package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.DataChargeLevelService;
import com.longlian.model.DataChargeLevel;
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
 * Created by admin on 2017/10/27.
 */

@RequestMapping("/dataChargeLevel")
@Controller
public class DataChargeLevelController {

    private static Logger log = LoggerFactory.getLogger(DataChargeLevelController.class);
    @Autowired
    DataChargeLevelService dataChargeLevelService;

    @Autowired
    RedisUtil redisUtil;
    
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/dataChargeLevel/index");
        return view;
    }
    @RequestMapping(value = "/getDataChargeLevelList",method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getPendingCommentList(DatagridRequestModel datagridRequestModel,Map map){
        DatagridResponseModel model=new DatagridResponseModel();
        model.setRows(dataChargeLevelService.getDataChargeLevelList(datagridRequestModel, map));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }
    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/dataChargeLevel/addOrUpdate");
        view.addObject("id", id);
        return view;
    }
    /**
     * 添加修改
     *
     * @param 
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ActResult doSaveAndUpdate(@RequestBody DataChargeLevel dataChargeLevel) throws Exception {
        ActResult result = new ActResult();
        try {
            dataChargeLevelService.doSaveAndUpdate(dataChargeLevel);
            redisUtil.del(RedisKey.ll_data_charge_level);
            result.setMsg("修改成功！！");
        } catch (Exception e) {
            log.error("直播流后台添加或修改失败！！", e);
            result.setSuccess(false);
            result.setMsg("修改失败！！");
        }
        return result;

    }
    @RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(String ids) {
        ActResult actResult = new ActResult();
        if (StringUtils.isEmpty(ids)) {
            return ActResult.fail("请选择要删除项!");
        }
        try {
            dataChargeLevelService.deleteById(ids);
            redisUtil.del(RedisKey.ll_data_charge_level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }
    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        DataChargeLevel dataChargeLevel =dataChargeLevelService.findById(id);
        if(dataChargeLevel==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(dataChargeLevel);
        }
        return actResult;
    }
}
