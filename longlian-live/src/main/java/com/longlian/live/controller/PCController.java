package com.longlian.live.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.weixin.ParamesAPI.WeixinApp;
import com.huaxin.util.weixin.encryption.AesException;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.AppUser;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.OssBucket;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
@Controller
@RequestMapping(value = "/pc")
public class PCController {
    private static Logger log = LoggerFactory.getLogger(PCController.class);

    @Autowired
    WeixinUtil weixinUtil;

    @Autowired
    AppUserService appUserService;

    @RequestMapping(value = "/login")
    @ApiOperation(value = "录播主页", httpMethod = "GET", notes = "录播主页")
    public ModelAndView login(HttpServletRequest request) throws Exception {
        ModelAndView view = new ModelAndView("/func/pc/index");
        return  view;
    }
    @RequestMapping(value = "/logins")
    @ApiOperation(value = "登录主页", httpMethod = "GET", notes = "登录主页")
    public ModelAndView logins(HttpServletRequest request) throws Exception {
        ModelAndView view = new ModelAndView("/func/pc/login");
        view.addObject("userStatus","0");
        return  view;
    }

    /**
     * 微信登录授权
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "/loginBackcall")
    @ApiOperation(value = "微信登录授权", httpMethod = "GET", notes = "微信登录授权")
    public ModelAndView loginBackcall(HttpServletRequest request, HttpServletResponse response , String code ,String state) throws IOException, AesException, DocumentException ,Exception {
        System.out.println("loginBackcall_code --------->" + code);
        ModelAndView modelAndView = new ModelAndView("/func/pc/login");
        WeixinApp weixinApp =  weixinUtil.getThirdComponentWebUser(code);
        if(weixinApp == null){
            modelAndView = new ModelAndView("redirect:/pc/login");
            return modelAndView;
        }
        //根据unionid ，找到用户和直播间ID和名称
        AppUser appUser = appUserService.getByUnionid(weixinApp.getUnionid());
        if(null==appUser || !"1".equals(appUser.getUserType())){
             //modelAndView = new ModelAndView("redirect:/pc/login");
             modelAndView = new ModelAndView("/func/pc/login");
            modelAndView.addObject("userStatus","0");
        }else{
            ActResultDto AppLoginResult = appUserService.getWeixinLoginFormWebsite(appUser);
            AppUserIdentity identity = (AppUserIdentity) AppLoginResult.getData();
            response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
            modelAndView = new ModelAndView("redirect:/pc/main.user");
        }
        return modelAndView;
    }


    /**
     * 主页面
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/main.user")
    @ApiOperation(value = "主页面", httpMethod = "GET", notes = "主页面")
    public ModelAndView main(HttpServletRequest request) throws Exception {
        ModelAndView view = new ModelAndView("/func/pc/main");
        AppUserIdentity identity =  (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        view.addObject("user" ,identity);
        return  view;
    }
    @Value("${videoBasePath:longlian_test}")
    private String videoBasePath;
    @Autowired
    private LiveRoomService liveRoomService;
    /**
     * 主页面
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getPolicy.user")
    public void getPolicy(HttpServletRequest request,HttpServletResponse response) throws Exception {
        AppUserIdentity identity =  (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        OSSClient client = new OSSClient(OssBucket.longlian_input.getEndpoint(), LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret);
        String host = "https://" + OssBucket.longlian_input.getName() + "." + OssBucket.longlian_input.getEndpoint();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);    //获取年
        int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份

        String fullMonth = (month < 10 ? "0":"") + month;

        String dir = videoBasePath +"/" + year + "/" + fullMonth +"/" ;

        LiveRoom liveRoom = liveRoomService.findByAppId(identity.getId());
        if (liveRoom != null) {
            dir += liveRoom.getId() + "/";
        }
        try {
            long expireTime = 60 * 60 * 5;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000 * 5l );
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", LonglianSsoUtil.accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            //respMap.put("expire", formatISO8601Date(expiration));
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            JSONObject ja1 = JSONObject.fromObject(respMap);
            System.out.println(ja1.toString());
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
            response(request, response, ja1.toString());

        } catch (Exception e) {
            log.error("错误：{}" , e);
        }
    }
    private void response(HttpServletRequest request, HttpServletResponse response, String results) throws IOException {
        String callbackFunName = request.getParameter("callback");
        if (callbackFunName==null || callbackFunName.equalsIgnoreCase(""))
            response.getWriter().println(results);
        else
            response.getWriter().println(callbackFunName + "( "+results+" )");
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }
}
