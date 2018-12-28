package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.AppUserCommentService;
import com.longlian.dto.AppUserCommentDto;
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

/**
 * Created by pangchao on 2017/1/23.
 */
@Controller
@RequestMapping("/appUserCommentController")
public class AppUserCommentController {
    private static Logger log = LoggerFactory.getLogger(AppUserCommentController.class);

    @Autowired
    AppUserCommentService appUserCommentService;

    /**
     * 用户反馈页面
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/appUserComment/index");
        return view;
    }

    /**
     * 待处理页面
     * @return
     */
    @RequestMapping("/pendingCommentIndex")
    public ModelAndView pendingCommentIndex(){
        ModelAndView view = new ModelAndView("/func/appUserComment/pendingCommentIndex");
        return view;
    }

    /**
     * 待处理页面
     * @return
     */
    @RequestMapping("/inHandOrAlreadyHandCommentIndex")
    public ModelAndView inHandOrAlreadyHandCommentIndex(String handStatus){
        ModelAndView view = new ModelAndView("/func/appUserComment/inHandOrAlreadyHandCommentIndex");
        view.addObject("handStatus",handStatus);
        return view;
    }

    /**
     * 待处理页面
     * handStatus :处理状态
     * @return
     */
    @RequestMapping("/hand")
    public ModelAndView hand(long id,String handStatus){
        ModelAndView view = new ModelAndView("/func/appUserComment/hand");
        view.addObject("id", id);
        view.addObject("handStatus", handStatus);
        return view;
    }

    /**
     * 获取待处理数据
     * @return
     */
    @RequestMapping(value = "/getPendingCommentList",method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getPendingCommentList(DatagridRequestModel datagridRequestModel,AppUserCommentDto appUserComment){
        DatagridResponseModel model=new DatagridResponseModel();
        model.setRows(appUserCommentService.getPendingCommentListPage(datagridRequestModel, appUserComment));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 设置处理反馈意见
     * @param appUserComment
     * @return
     */
    @RequestMapping(value = "/setHandComment",method = RequestMethod.POST)
    @ResponseBody
    public ActResult setHandComment(HttpServletRequest request,@RequestBody AppUserCommentDto appUserComment){
        ActResult actResult=new ActResult();
        try {
            appUserCommentService.setHandComment(request,appUserComment);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setMsg("系统错误");
            actResult.setSuccess(false);
            log.error("设置处理反馈意见失败！！");
        }
        return actResult;
    }

    /**
     * 获取处理状态
     * @param handStatus 当前处理状态
     * @return
     */
    @RequestMapping(value = "/getHandStatus",method = RequestMethod.GET)
    @ResponseBody
    public ActResult getHandStatus(String handStatus){
        ActResult actResult=new ActResult();
        actResult.setData(appUserCommentService.gethandStatusList(handStatus));
        return actResult;
    }
    /**
     *
     *通过ID查询
     *
     */
    @RequestMapping(value = "/getAppUserCommentById",method = RequestMethod.GET)
    @ResponseBody
    public ActResult getAppUserCommentById(long id){
        ActResult actResult=new ActResult();
        actResult.setData(appUserCommentService.getAppUserCommentById(id));
        return actResult;
    }


    /**
     * 获取处理中或者已处理的数据
     * @return
     */
    @RequestMapping(value = "/getInHandOrAlreadyHandCommentList",method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getInHandOrAlreadyHandCommentIndex(DatagridRequestModel datagridRequestModel,AppUserCommentDto appUserComment){
        DatagridResponseModel model=new DatagridResponseModel();
        model.setRows(appUserCommentService.getInHandOrAlreadyHandCommentListPage(datagridRequestModel, appUserComment));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 跳转至查看审核意见页面
     * @param id
     * @return
     */
    @RequestMapping("/toShowComment")
    @ResponseBody
    public ModelAndView toShowComment(long id){
        ModelAndView model=new ModelAndView("/func/appUserComment/showComment");
        model.addObject("id", id);
        return model;
    }

    @RequestMapping("/getCommentByCommentId")
    @ResponseBody
    public ActResult getCommentByCommentId(long id){
        ActResult actResult=new ActResult();
        actResult.setData(appUserCommentService.getCommentByCommentId(id));
        return actResult;
    }
}