package com.longlian.console.controller;


import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.console.service.ConsoleUserService;
import com.longlian.console.service.MenuService;
import com.longlian.console.util.UrlConnection;
import com.longlian.model.MRes;
import com.longlian.model.MUser;
import com.longlian.console.service.res.ResService;
import com.longlian.token.ConsoleUserIdentity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
    public String main(){
        return "/main/index";
    }
    @RequestMapping("/header")
    public String head(HttpServletRequest request,Model model){
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        model.addAttribute("loginName",token.getName());
        return "/main/header";
    }
    @RequestMapping("/menu")
    public String menu(HttpServletRequest request,Model model){
        ActResult result = this.getUserMenuList(request);
        model.addAttribute("menus",result);
        return "/main/menu";
    }
    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, Model model) {
        return "redirect:/login.jsp";
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

    public ActResult getUserMenuList(HttpServletRequest request){
        ActResult actResult = new ActResult();
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        String resIds = consoleUserService.getUserRes(token.getId() , "menu");
        List<MRes> resList = new ArrayList();
        List<MRes> menuList = new ArrayList();
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
        Map<Long,MRes> maps = new HashMap<Long,MRes>();
        if(resList != null && resList.size() > 0){
            for(MRes res:resList) {
                maps.put(res.getId(),res);
            }
            for(MRes res:resList) {
               if(res != null && res.getParentId().longValue() == 0L){
                   menuList.add(res);
               } else {
                   MRes pRes = maps.get(res.getParentId());
                   if(pRes == null){
                       menuList.add(res);
                   }else{
                       if(pRes.getChildren() == null){
                           pRes.setChildren(new ArrayList<MRes>());
                       }
                       pRes.getChildren().add(res);
                   }
               }
            }
        }
        Map map = new HashMap();
        map.put("user" , token);
        map.put("resList", menuList);
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
    public String resPage(HttpServletRequest request , HttpServletResponse response , String url){
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        String domain = resService.getResServerDomain();
        String accessToken = resService.getResAccessToken(token.getId());
        try {
            if (url.contains("?")) {
                url += "&accessToken=" + accessToken;
            } else {
                url += "?accessToken=" + accessToken;
            }
         //   response.sendRedirect("http://www.baidu.com");
            response.sendRedirect( url );
        //    UrlConnection.sendPost("http://" + domain + url,new HashMap<>());
          //  return "http://" + domain + url;
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
        return null;
    }

}
