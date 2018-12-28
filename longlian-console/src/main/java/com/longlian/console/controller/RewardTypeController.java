package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.UserRewardTypeService;
import com.longlian.model.UserRewardType;
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
 * 打赏设置
 * Created by admin on 2017/4/20.
 */
@RequestMapping("/userReward")
@Controller
public class RewardTypeController {
    private static Logger log = LoggerFactory.getLogger(RewardTypeController.class);

    @Autowired
    UserRewardTypeService userRewardTypeService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("")
    public String index(){
        return "/func/rewardType/index";
    }

    /**
     * 所有的打赏设置
     * @param requestModel
     * @param userRewardType
     * @return
     */
    @RequestMapping("/findrewardTypeInfoPage")
    @ResponseBody
    public DatagridResponseModel findrewardTypeInfoPage(DataGridPage requestModel, UserRewardType userRewardType) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<UserRewardType> trackDtoList = userRewardTypeService.findrewardTypeInfoPage(requestModel, userRewardType);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 添加或者修改打赏设置
     * @param id
     * @return
     */
    @RequestMapping("/toCreateOrUpdate")
    public ModelAndView toCreateOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/rewardType/toCreateOrUpdate");
        view.addObject("id", id);
        return view;
    }


    /**
     * 根据ID查询打赏设置
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    @ResponseBody
    public ActResult findById(Long id){
        ActResult result = new ActResult();
        UserRewardType userRewardType = userRewardTypeService.getById(id);
        result.setData(userRewardType);
        return result;
    }

    /**
     * 创建或者修改打赏设置
     * @param userRewardType
     * @return
     */
    @RequestMapping("/createUserRewardType")
    @ResponseBody
    public ActResult createUserRewardType(@RequestBody UserRewardType userRewardType){
        ActResult result = new ActResult();
        Long id = userRewardType.getId();
        if(id == null || id < 1){
            userRewardTypeService.createUserRewardType(userRewardType);
        }else{
            userRewardTypeService.updateUserRewardType(userRewardType);
        }
        return result;
    }

    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    @ResponseBody
    public ActResult updateStatus(Long id , String status ){
        ActResult result = new ActResult();
        userRewardTypeService.updateStatus(id, status);
        return result;
    }
}
