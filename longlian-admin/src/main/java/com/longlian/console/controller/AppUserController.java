package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.console.service.AppUserService;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.model.AppUser;
import com.longlian.model.system.SystemAdmin;
import com.longlian.type.ReturnMessageType;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/14.
 */
@RequestMapping("/appUser")
@Controller
public class AppUserController {
    private static Logger log = LoggerFactory.getLogger(AppUserController.class);

    @Autowired
    AppUserService appUserService;

    @Autowired
    AppUserCommonService appUserCommonService;

    /**
     * 用户管理界面
     * @return
     */
    @RequestMapping("")
    public String appUserManage(){
        return "/func/appUser/index";
    }

    /**
     * 用户信息-分页
     * @param name
     * @param mobile
     * @param userType
     * @return
     */
    @RequestMapping(value = "/getAllAppUserInfoPage" ,method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getAllAppUserInfoPage(String name , String mobile ,
                                           String userType , DatagridRequestModel datagridRequestModel){
        return appUserService.getAllAppUserinfoPage(name, mobile, userType , datagridRequestModel);
    }

    /**
     * 银行卡详情页面
     * @return
     */
    @RequestMapping("/bankinfo")
    public ModelAndView getBankInfo(long id,String balance){
        ModelAndView view = new ModelAndView("/func/accountTrack/bank");
        view.addObject("id", id);
        view.addObject("balance", balance);
        return view;
    }

    /**
     * 银行卡
     * @param id
     * @return
     */
    @RequestMapping(value = "/getAppUsers" , method = RequestMethod.GET)
    @ResponseBody
    public ActResult getAppUsers(long id){
        ActResult actResult = new ActResult();
        actResult.setData(appUserService.getAppUsers(id));
        return actResult;
    }

    /**
     *会员钱包--提现记录页面
     * @return
     */
    @RequestMapping("/withdrawDepositPage")
    public ModelAndView withdrawDepositPage(long id){
        ModelAndView view = new ModelAndView("/func/accountTrack/withdraw");
        view.addObject("id", id);
        return view;
    }

    /**
     * 会员钱包--提现记录
     * @return
     */
    @RequestMapping(value = "/withdrawDeposit",method =RequestMethod.GET)
    @ResponseBody
    public ActResult withdrawDeposit(long id){
        ActResult ar = new ActResult();
        Map map = appUserService.withdrawDeposit(id);
        ar.setData(map);
        return ar;
    }

    /**
     *会员钱包--奖励记录页面
     * @return
     */
    @RequestMapping("/membershiprebatePage")
    public ModelAndView membershiprebatePage(long id){
        ModelAndView view = new ModelAndView("/func/accountTrack/shiprebate");
        view.addObject("id", id);
        return view;
    }
    /**
     * 会员钱包--奖励记录
     * @return
     */
    @RequestMapping(value = "/membershiprebate",method =RequestMethod.GET)
    @ResponseBody
    public ActResult membershiprebate(long id,@RequestParam(value = "map",required = false)String mapRequet){
        ActResult ar = new ActResult();
        Map mapr=new HashMap<>();
        if(mapRequet!=null){
            JSONObject jb = JSONObject.fromObject(mapRequet);
             mapr=(Map)jb;
        }

        Map map = appUserService.membershiprebate(id,mapr);
        ar.setData(map);
        return ar;
    }

    /**
     * 会员详情
     * @return
     */
    @RequestMapping("member")
    public String member(){
        return "/func/appUser/member";
    }

    /**
     * 会员详情-分页
     * @return
     */
    @RequestMapping(value = "/getMemberDetailsList" ,method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getMemberDetailsList( DatagridRequestModel page,@RequestParam Map map){
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        List<Map> listMaps =appUserService.getMemberDetailsList(page, map);
        /*if(listMaps.size()>0)
        {
            for(Map map1 :listMaps)
            {
                if(!Utility.isNullorEmpty(map1.get("proxyAppId"))) {
                    AppUser appUser = appUserService.getAppUserById(Long.parseLong(map1.get("proxyAppId").toString()));
                    map1.put("tName", appUser.getName());
                }
            }
        }*/
        datagridResponseModel.setRows(listMaps);
        datagridResponseModel.setTotal(page.getTotal());
        return datagridResponseModel;
    }

    @RequestMapping("toTeacher")
    public String toTeacher(){
        return "/func/appUser/teacher";
    }
    /**
     * 老师查询
     * @param page
     * @param map   
     * @return  
     */
    @RequestMapping(value = "/getTeacherList" ,method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getTeacherList( DatagridRequestModel page,@RequestParam Map map){
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        List<Map> listMaps =appUserService.getTeacherListPage(page, map);
        datagridResponseModel.setRows(listMaps);
        datagridResponseModel.setTotal(page.getTotal());
        return datagridResponseModel;
    }


    /**
     * 重置用户unionidAndOpenid
     * @return
     */
    @RequestMapping(value = "/resetUnionidAndOpenid2Redis",method =RequestMethod.GET)
    @ResponseBody
    public ActResult resetUnionidAndOpenid2Redis(){
        ActResult ar = new ActResult();
        appUserCommonService.getAndHandlerSaveRedis();
        return ar;
    }
    @RequestMapping(value = "/resetOrUpdatePwd",method =RequestMethod.POST)
    @ResponseBody
    public ActResult resetOrUpdatePwd(Long id,String password){
         ActResult ar = new ActResult();
         AppUser appUser=appUserService.getAppUserById(id);
         String pwd= "";
            if(Utility.isNullorEmpty(appUser)){
                return ar.fail("用户不存在!");
            }
            if(Utility.isNullorEmpty(password)){
                pwd=MD5PassEncrypt.getMD5Str("123456");
            }else{
                pwd=MD5PassEncrypt.getMD5Str(password);
            }
        appUserService.resetPwd(id, pwd);
        return ar;
    }

    @RequestMapping("/toUpdatePwd")
    public ModelAndView toUpdatePwd(Long id) {
        ModelAndView model = new ModelAndView("/func/appUser/toUpdatePwd");
        AppUser appUser=appUserService.getAppUserById(id);
        model.addObject("id", id);
        model.addObject("password", appUser.getPassword());
        return model;
    }

    @RequestMapping(value = "/updateProportion",method =RequestMethod.GET)
    @ResponseBody
    public ActResult resetOrUpdatePwd(Long id,Integer addCount){
        return appUserService.updateProportion(id, addCount);
    }
    /**
     * 超级管理员列表
     */
    @RequestMapping("/managerList")
    public ModelAndView adminManager(){
        ModelAndView model = new ModelAndView("/func/user/adminList");
        return model;
    }
    @RequestMapping("/loadData")
    @ResponseBody
    public DatagridResponseModel loadData(Model model){
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        List<SystemAdmin> adminList = appUserService.getSystemAdminList();
        datagridResponseModel.setRows(adminList);
        return datagridResponseModel;
    }
    /**
     * 添加管理员弹出页面
     * @param model
     * @return
     */
    @RequestMapping("/to_add_system_admin")
    public String toAddSystemAdminPage(Model model){
        return "/func/user/add_system_admin";
    }

    @RequestMapping(value = "/add_system_admin",method =RequestMethod.POST)
    @ResponseBody
    public ActResultDto addSystemAdmin(SystemAdmin admin){
        ActResultDto ac = new ActResultDto();
        int result = 0;
        try {
            result = appUserService.insertSystemAdmin(admin);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());//000000
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("插入超级管理员异常:"+e.getMessage());
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());//000003
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        ac.setData(result);
        return ac;
    }
    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/findUserByWhere",method =RequestMethod.POST)
    @ResponseBody
    public ActResultDto findUserByWhere(Long id){
        ActResultDto ac = new ActResultDto();
        AppUser appUser=appUserService.getAppUserById(id);
        if(Utility.isNullorEmpty(appUser)){
            ac.setCode(ReturnMessageType.CODE_UPDATE_RETURN.getCode());//000003
            ac.setMessage(ReturnMessageType.CODE_UPDATE_RETURN.getMessage());
            ac.setData(null);
            return ac;
        }
        ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());//000000
        ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        ac.setData(appUser);
        return ac;
    }

    /**
     * 操作删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteSystemAdmin",method =RequestMethod.POST)
    @ResponseBody
    public ActResultDto deleteSystemAdmin(Long id){
        ActResultDto ac = new ActResultDto();
        int result = 0;
        try {
            result = appUserService.deleteSystemAdmin(id);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());//000000
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            ac.setData(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除超级管理员异常:"+e.getMessage());
            result = 3;
            ac.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());//000000
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            ac.setData(result);
        }
        return ac;
    }
}
