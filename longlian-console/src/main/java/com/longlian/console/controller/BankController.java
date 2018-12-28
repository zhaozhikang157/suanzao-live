package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.BankService;
import com.longlian.dto.BankDto;
import com.longlian.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by U on 2016/8/1.
 */
@Controller
@RequestMapping("/bankController")
public class BankController {

    @Autowired
    BankService bankService;



    /**
     * 银行管理页面
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/bank/index");
        return view;
    }

    /**
     * 添加或修改页面
     * @return
     */
    @RequestMapping("/toAddOrUpdate")
    public ModelAndView toAddOrUpdate(long id){
        ModelAndView view = new ModelAndView("/func/bank/addOrUpdate");
        view.addObject("id",id);
        return view;
    }
    /**
     *
     * @param requestModel
     * @param bankDto
     * @return
     */
    @RequestMapping(value = "/getList",method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel requestModel,BankDto bankDto){
        DatagridResponseModel model=new DatagridResponseModel();
        model.setRows(bankService.getListPage(requestModel,bankDto));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 添加或修改
     * @return
     */
    @RequestMapping(value = "/doCreateOrUpdate",method = RequestMethod.POST)
    @ResponseBody
    public ActResult doCreateOrUpdate(HttpServletRequest request,@RequestBody Bank bank){
        ActResult actResult=new ActResult();
        try {
            if(bank.getId()>0){  //修改
                bankService.update(bank);
            }else {  //添加
                bankService.create(bank);
            }
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("系统错误!");
        }
        return actResult;
    }

    /**
     * 设置为禁用状态
     * @return
     */
    @RequestMapping(value = "/setStatusForbidden",method = RequestMethod.POST)
    @ResponseBody
    public ActResult setStatusForbidden(long id){
        ActResult actResult=new ActResult();
        try {
            bankService.setStatusForbidden(id);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("系统错误!");
        }
        return actResult;
    }


    /**
     * 设置为启用状态
     * @return
     */
    @RequestMapping(value = "/setStatusStart",method = RequestMethod.POST)
    @ResponseBody
    public ActResult setStatusStart(long id){
        ActResult actResult=new ActResult();
        try {
            bankService.setStatusStart(id);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("系统错误!");
        }
        return actResult;
    }

    /**
     * 根据ID查询该条信息
     * @return
     */
    @RequestMapping(value = "/selectById",method = RequestMethod.GET)
    @ResponseBody
    public ActResult selectById(long id){
        ActResult actResult=new ActResult();
        try {
            actResult.setData(bankService.selectById(id));
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("系统错误!");
        }
        return actResult;
    }

    /**
     * 获取银行名称list
     * @return
     */
    @RequestMapping(value = "/getBankNameList",method = RequestMethod.GET)
    @ResponseBody
    public ActResult getBankNameList(){
        ActResult actResult=new ActResult();
        actResult.setData(bankService.getBankNameList());
        return actResult;
    }

}
