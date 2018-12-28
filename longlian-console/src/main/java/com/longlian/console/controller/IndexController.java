package com.longlian.console.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.longlian.live.util.yunxin.YunxinUserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.images.VerifyCodeUtils;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.util.SystemUtil;
import com.longlian.res.service.ThirdUserService;
import com.longlian.token.ConsoleUserIdentity;

/**
 * Created by lh on 2016/5/5.
 */
@Controller
@RequestMapping("/")
public class IndexController {
    private static Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ThirdUserService userService;
    @Autowired
    private SystemUtil systemUtilLonglian;
    @Autowired
    private YunxinUserUtil yunxinUserUtil;


    /**
     * 首页默认跳转到登录页面
     * @param model
     * @return
     */
    @RequestMapping
    public ModelAndView index(ModelMap model) {
        return login(model);
    }

    /**
     * 登录界面
     * @param model
     * @return
     */
    @RequestMapping(value = "login")
    public ModelAndView login(ModelMap model) {
        ModelAndView view = new ModelAndView("login");
        return view;
    }

    /**
     * 登录界面
     * @return
     */
    @RequestMapping(value = "doLoginIn")
    @ResponseBody
    public ActResult doLoginIn( ConsoleUserIdentity user, HttpServletRequest request , HttpServletResponse response ) throws  Exception{
        ActResult<Map> result = new ActResult();
        if(Utility.isNullorEmpty(user.getAccount())){ 
            result.setMsg("账号不能为空！");
            result.setSuccess(false);
            return result;
        }
        if (request.getSession().getAttribute("vcode") == null) {
            result.setMsg("验证码过期！");
            result.setSuccess(false);
            result.setCode(-1);
            return result; 
        }
        if (!user.getVerifCode().equalsIgnoreCase(request.getSession().getAttribute("vcode").toString()))//验证码错误
        {
            result.setMsg("验证码错误！");
            result.setSuccess(false);
            return result;
        }
          result = userService.login(user);
        if (result.isSuccess()) {
            Map m = result.getData();
            String accId = "a_" + m.get("id");
            //取出最新的token
            String yunxinToken = yunxinUserUtil.refreshToken(accId);
            //如果为空，则说明没有这个用户,需要创建
            String userName = user.getName();
            if (Utility.isNullorEmpty(userName)) {
                userName = "管理员";
            }
            if ("".equals(yunxinToken)) {
                yunxinToken = yunxinUserUtil.createUser(accId,userName,"");
            } else {
                //更新用户名
                yunxinUserUtil.updateUserInfo(accId, userName,"");
            }
            //放入token

            m.put("yunxinToken", yunxinToken);
            //放到redis
           redisUtil.hmset(RedisKey.user_manage_login_prefix + result.getData().get("id"), result.getData(), RedisKey.user_login_valid_time);
           response.setHeader("SET-COOKIE", "LONGLIAN-JWT=" + result.getData().get("token") + ";timeout="+15*60*1000+"; HttpOnly");
        }
        return result;
    }

    /**
     * 退出
     * @return
     */
    @RequestMapping(value = "logout")
    public String logout(HttpServletRequest request  ) throws  Exception{
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        redisUtil.del(RedisKey.user_manage_login_prefix + token.getId());
        userService.logout(token);
        return "/login";
    }
    
    /**
     * 获取登录校验码
     *
     * @param req
     * @param resp
     */
    @RequestMapping(value = "vcodeimg")
    public void getValidateImage(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String code = VerifyCodeUtils.generateVerifyCode(4);
            resp.setContentType("image/jpeg");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Pragma", "no-cache");
            req.getSession().setAttribute("vcode", code);
            VerifyCodeUtils.outputImage(200, 80, resp.getOutputStream(), code);
        } catch (IOException e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }
}
