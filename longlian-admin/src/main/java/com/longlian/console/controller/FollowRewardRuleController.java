package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.FollowRewardRuleService;
import com.longlian.model.FollowRewardRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by pangchao on 2017/2/25.
 */
@RequestMapping("/followRewardRule")
@Controller
public class FollowRewardRuleController {
    private static Logger log = LoggerFactory.getLogger(FollowRewardRuleController.class);

    @Autowired
    FollowRewardRuleService followRewardService;

    /**
     * 主页
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/followReward/index");
    }

    /**
     * 展示数据
     *
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel datagridRequestModel, FollowRewardRule followRewardRule) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(followRewardService.getListPage(datagridRequestModel, followRewardRule));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 去修改页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/followReward/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 查询id
     *
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findById(long id) {
        ActResult ac = new ActResult();
        ac.setData(followRewardService.findById(id));
        return ac;
    }

    /**
     * 添加修改
     *
     * @param followRewardRule
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(@RequestBody FollowRewardRule followRewardRule) throws Exception {
        ActResult result = new ActResult();
        try {
            followRewardService.doSaveAndUpdate(followRewardRule);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            log.error("保存失败！！");
        }
        return result;

    }

    /**
     * 禁用
     *
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult deleteById(long id,long status) {
        ActResult ac = new ActResult();
        try {
            followRewardService.deleteById(id,status);
        } catch (Exception e) {
            e.printStackTrace();
            ac.setSuccess(false);
            ac.setMsg("禁用错误");
        }
        return ac;
    }


}