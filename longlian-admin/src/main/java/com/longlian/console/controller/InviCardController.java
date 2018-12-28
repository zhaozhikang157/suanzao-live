package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.InviCardService;
import com.longlian.model.InviCard;
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
 * Created by admin on 2017/2/16.
 */
@RequestMapping("/inviCard")
@Controller
public class InviCardController {
    private static Logger log = LoggerFactory.getLogger(InviCardController.class);

    @Autowired
    InviCardService inviCardService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("")
    public String index(){
        return "/func/inviCard/index";
    }

    /**
     * 查询所有模板
     * @return
     */
    @RequestMapping("/findAllInviCardPage")
    @ResponseBody
    public DatagridResponseModel findAllInviCard(DatagridRequestModel requestModel){
        DatagridResponseModel model=new DatagridResponseModel();
        List<InviCard> inviCardList = inviCardService.getAllInviCard();
        model.setRows(inviCardList);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 根据模板id查询模板信息
     * @param id
     * @return
     */
    @RequestMapping("/findByInviCardId")
    @ResponseBody
    public ActResult findByInviCardId(Long id){
        ActResult result = new ActResult();
        if(id == 0){
            result.setSuccess(false);
            result.setMsg("获取邀请码模板信息失败!");
            return  result;
        }
        InviCard inviCard = inviCardService.findInviCardById(id);
        result.setData(inviCard);
        return result;
    }

    /**
     * 修改模板信息
     * @param inviCard
     * @return
     */
    @RequestMapping("/addOrupdateInviCard")
    @ResponseBody
    public ActResult updateInviCard(@RequestBody InviCard inviCard){
        ActResult result = new ActResult();
        if(inviCard.getId()!=0){
            inviCardService.updateInviCard(inviCard);
        }else{
            inviCardService.insertInviCard(inviCard);
        }
        return result;
    }

    /**
     * 添加或者修改界面
     * @param id
     * @return
     */
    @RequestMapping("/toAddOrUpdate")
    public ModelAndView toAddOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/inviCard/addOrUpdate");
        view.addObject("id",id);
        return view;
    }

    /**
     * 删除模板
     * @param id
     * @return
     */
    @RequestMapping("/delInviCardById")
    @ResponseBody
    public ActResult delInviCardById(Long id){
        ActResult result = new ActResult();
        if(id == 0){
            result.setSuccess(false);
            result.setMsg("删除失败!");
            return  result;
        }
        inviCardService.delInviCardById(id);
        result.setSuccess(true);
        return result;
    }
}
