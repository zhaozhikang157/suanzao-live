package com.longlian.console.controller.res;


import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.MRes;
import com.longlian.model.MUser;
import com.longlian.console.service.res.MenuService;
import com.longlian.console.service.res.UserService;
import com.longlian.token.ConsoleUserIdentity;
import org.apache.commons.lang3.StringUtils;
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
 * Created by lh on 2016/5/5.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {


    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;

    /**
     * 主页面
     * @return
     */
    @RequestMapping
    public ModelAndView main(HttpServletRequest request){
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView view = new ModelAndView("/func/user/index");
        view.addObject("loginId",token.getId());
        return view;
    }

    /**
     * 获取所有的员工
     * @param requestModel
     * @param muser
     * @return
     */
    @RequestMapping(value = "/findAllUser" , method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel findAllUser(DatagridRequestModel requestModel , MUser muser){
        return userService.getListPage(requestModel, muser);
    }

    /**
     * 添加修改页面
     * @param id
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate" )
    public ModelAndView toAddOrUpdate(String id){
        ModelAndView view = new ModelAndView("/func/user/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 添加修改保存
     * @param user
     * @return
     */
    @RequestMapping(value = "/saveUser" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveUser(@RequestBody MUser user){
        ActResult actResult = new ActResult();
        userService.createOrUpdate(user);
        return actResult;
    }

    /**
     * 获取角色
     * @return
     */
    @RequestMapping(value = "/getAllRole" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult getAllRole(){
        List<MRes> list = userService.getAllRole("002");
        ActResult actResult = new ActResult();
        actResult.setData(list);
        return actResult;
    }

    /**
     * 通过ID查询员工信息
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(String id) throws Exception{
        ActResult actResult = new ActResult();
        if(StringUtils.isEmpty(id)){
            actResult.setMsg("请选择员工进行修改!");
            return actResult;
        }
        MUser mUser = userService.findById(Utility.parseLong(id));
        actResult.setData(mUser);
        return actResult;
    }

    /**
     * 删除,改变状态
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping(value = "/deleteByIds" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(String ids , String status){
        ActResult actResult = new ActResult();
        userService.deleteByIds(ids, status);
        return actResult;
    }

    /**
     * 密码重置
     * @param ids
     * @return
     */
    @RequestMapping(value = "/passwordReset", method = RequestMethod.POST)
    @ResponseBody
    public ActResult passwordReset(String ids){
        ActResult actResult = new ActResult();
        userService.passwordReset(ids);
        return actResult;
    }

    /**
     * 判断用户名是否重复
     * @param userId
     * @return
     */
    @RequestMapping(value = "/findUserId" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findUserId(String userId){
        ActResult actResult = new ActResult();
        if(StringUtils.isEmpty(userId)){
            actResult.setSuccess(false);
            return actResult;
        }
        MUser mUser = userService.findUserId(userId.trim());
        if(mUser==null){
            return actResult;
        }else{
            actResult.setData(mUser.getUserId());
            actResult.setSuccess(false);
            return actResult;
        }
    }

    /**
     * 密码修改窗
     * @return
     */
    @RequestMapping(value = "/showUpdatePassWord")
    public ModelAndView showUpdatePassWord(){
        ModelAndView view = new ModelAndView("/func/user/updatePassword");
        return view;
    }

    /**
     * 修改密码
     * @param request
     * @return
     */
    @RequestMapping(value = "/updatePassWord" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult updatePassWord(HttpServletRequest request , String newPassword , String oldPassword) throws Exception {
        ActResult actResult = new ActResult();
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        long userId = token.getId();
        MUser user = userService.findById(userId);
        if(userService.checkPassword(user,oldPassword)){
            userService.updatePassWord(userId,newPassword);
//            menuService.delAll();   //删除redis缓存
            return actResult;
        }else{
            actResult.setSuccess(false);
            return actResult;
        }
    }

}
