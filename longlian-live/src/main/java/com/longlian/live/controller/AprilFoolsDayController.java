package com.longlian.live.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.AppUserService;
import com.longlian.token.AppUserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by wanghuaan on 2018/3/30.
 * 愚人节活动
 */
@Controller
@RequestMapping(value = "/")
public class AprilFoolsDayController {
    public final static String april_fools_day_share_sum = "aprilfoolsdaysharesum "; //统计分享次数
    public final static String april_fools_day_user = "aprilfoolsdayuser "; //记录使用分享的微信OpenID 不重复数据
    public final static String april_fools_day_user_look = "aprilfoolsdayuserlook "; //统计多少人看
    public final static String april_fools_day_sum = "aprilfoolsdaysum"; //统计被整蛊人数
    public final static String every_people = "people:"; //统计被整蛊人数

    private static Logger log = LoggerFactory.getLogger(AprilFoolsDayController.class);
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AppUserService appUserService;
    /**
     * 跳转整蛊页面
     * @return
     */
    @RequestMapping("weixin/toFoolsDayPage")
    public ModelAndView toFoolsDayPage(Optional<String> titleStr,Optional<String> uuid,HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView view = new ModelAndView("/func/weixin/foolsDay/foolsnext");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        try{
            if(identity!=null){
                if(titleStr.isPresent()){
                    view = new ModelAndView("/func/weixin/foolsDay/foolsDay");
                    view.addObject("titleStr",titleStr.get());
                    log.info("愚人节标题：" + titleStr.get());
                    log.info("愚人节OpenId：" + identity.getOpenid());
                    if(!redisUtil.sismember(april_fools_day_user_look, identity.getOpenid())){//记录所有点看链接的微信ID 不重复数据
                        redisUtil.sadd(april_fools_day_user_look, identity.getOpenid());
                    }

                    if(!redisUtil.exists(april_fools_day_sum)){
                        redisUtil.incrBy(april_fools_day_sum, 798);
                    }
                    if(uuid.isPresent()){
                        log.info("愚人节UUID：" + uuid.get());
                        if(!redisUtil.hexists(every_people + uuid.get(), identity.getOpenid())) {//记录所有点看链接的微信ID 不重复数据
                            redisUtil.hset(every_people + uuid.get(), identity.getOpenid(), redisUtil.incrBy(april_fools_day_sum, 10) + "");
                        }
                        view.addObject("sumPeople", redisUtil.hget(every_people + uuid.get(),identity.getOpenid()));//获取用户是第几个被整的
                    }
                    log.info("打开愚人节活动分享页面 目前被整人数：" + redisUtil.get(april_fools_day_sum));
                    return view;
                }
            }else{
                log.error("获取用户微信信息失败");
                return null;
            }
        }catch (Exception e){
            log.error("愚人节",e);
            return null;
        }
        view.addObject("uuid", UUIDGenerator.generate());
        log.info("进入公众号设置愚人节活动分享页面");
        return view;
    }

    /**
     * 记录数据
     * @return
     */
    @RequestMapping("weixin/foolsDay")
    @ResponseBody
    public ActResult foolsDay(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info("记录分享次数");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        if(identity!=null){
            if(!redisUtil.sismember(april_fools_day_user,identity.getOpenid())){//记录使用分享的微信OpenID 不重复数据
                redisUtil.sadd(april_fools_day_user,identity.getOpenid());
            }
        }else{
            log.error("获取用户微信信息失败");
            return null;
        }
        redisUtil.incr(april_fools_day_share_sum);//记录分享次数
        return new ActResult();
    }

    /**
     * 处理微信登录
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public AppUserIdentity handlerWeixinLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //AppUserIdentity identity = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);//获取用户信息
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (identity == null) {
            WeixinAppUser weixinAppUser = (WeixinAppUser) request.getAttribute(CecurityConst.ll_live_weixin_app_user);
            if (weixinAppUser != null) {
                String nickname = Utility.getCheckNum(weixinAppUser.getNickname());
                weixinAppUser.setNickname(nickname);
                long invitationAppId = Utility.parseLong(request.getParameter("invitationAppId"));
                //System.out.println("接受---" + invitationAppId);
                ActResultDto resultDto = appUserService.weixinLogin(weixinAppUser, invitationAppId , "weixin");
                identity = (AppUserIdentity) resultDto.getData();
                if (identity != null) {
                    //处理cookie
                    response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
                }
            }
        }
        return identity;
    }
}
