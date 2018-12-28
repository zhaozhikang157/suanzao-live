package com.longlian.console.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.model.MRes;
import com.longlian.model.MUser;
import com.longlian.res.service.ResService;
import com.longlian.console.service.ConsoleUserService;
import com.longlian.console.service.MenuService;
import com.longlian.token.ConsoleUserIdentity;

//import com.huaxin.util.SystemUtil;

/**
 * Created by lh on 2016/5/5.
 */
@Controller
@RequestMapping(value = "/main")
public class MainController {
    private static Logger log = LoggerFactory.getLogger(IndexController.class);

    //@Autowired
    //SystemUtil systemUtil;
    @Autowired
    ConsoleUserService consoleUserService;
    @Autowired
    MenuService menuService;
    @Autowired
    ResService resService;
    /**
     * 主页面
     * @return
     */
    @RequestMapping("/home")
    public ModelAndView main(){
        ModelAndView view = new ModelAndView("/frame/index");
        return view;
    }

    /**
     * 获取用户信息以及权限资源
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserInfoAndEmpRes" , method = RequestMethod.GET)
    @ResponseBody
    public ActResult getUserInfoAndRes(HttpServletRequest request){
        ActResult actResult = new ActResult();
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        String resIds = consoleUserService.getUserRes(token.getId() , "menu");
        List<MRes> resList = new ArrayList();
        
        if (!StringUtils.isEmpty(resIds)) {
            resList = menuService.getListByIds(resIds);
        } else {
            //是管理员
            if (MUser.isAdmin(token.getAccount())) {
                resList = menuService.getList();
            }
        }
        
        Collections.sort(resList, new Comparator<MRes>() {
            public int compare(MRes arg0, MRes arg1) {
                return arg0.getParentId().compareTo(arg1.getParentId());
            }
        });
        Map map = new HashMap();
        map.put("user" , token);
        map.put("resList", resList);
        actResult.setData(map);
        return actResult;
    }
    /**
     * 默认内容页面
     * @return
     */
    @RequestMapping("/center")
    public ModelAndView center(){
        ModelAndView view = new ModelAndView("/frame/grid");
        return view;
    }

    /**
     * 默认内容页面
     * @return
     */
    @RequestMapping("/resPage")
    public void resPage(HttpServletRequest request , HttpServletResponse response , String url){
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        String domain = resService.getResServerDomain();
        String accessToken = resService.getResAccessToken(token.getId());
        try {
            if (url.contains("?")) {
                url += "&accessToken=" + accessToken;
            } else {
                url += "?accessToken=" + accessToken;
            }
            response.sendRedirect("http://" + domain + url );
            return ;
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

}
