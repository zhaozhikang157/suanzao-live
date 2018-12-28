package com.longlian.live.frontcontroller;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.AppUserService;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.AppUser;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * 指定老师邀请卡
 */
@Controller
@RequestMapping("/festival")
public class FestivalController {

    @Autowired
    AppUserService appUserService;
    @Autowired
    WeixinUtil weixinUtil;

    public static String weixin = "/weixin/";//微信根目录
    public static String foolsDay = weixin + "foolsDay/";//记录人数

    private static Logger log = LoggerFactory.getLogger(FestivalController.class);

    /*
    1.	恋家
    2.	独立
    3.	暖心
    4.	叛逆
    5.	成熟
    6.	亲昵
    7.	责任
    8.	孩子气
    */

    private Map<Integer,Integer[][]> qust = new HashMap<Integer,Integer[][]>(); //题目和对应选项答案

    private int[] options = null;
    private String[] titles = null;
    //初始化好题库答案
    {
        options = new int[]{1,2,3,4,5,6,7,8};
        titles = new String[]{"恋家","独立","暖心","叛逆","成熟","亲昵","责任","孩子气"};

        qust.put(1,new Integer[][]{{1,6,8},{2,5}});
        qust.put(2,new Integer[][]{{3,6}});
        qust.put(3,new Integer[][]{{3,6},{4}});
        qust.put(4,new Integer[][]{{4,5,2},{8}});
        qust.put(5,new Integer[][]{{5,2,7},{8}});
        qust.put(6,new Integer[][]{{6,1},{2,5}});
        qust.put(7,new Integer[][]{{3,7},{8}});
        qust.put(8,new Integer[][]{{1,3,6,7},{2,7}});
        qust.put(9,new Integer[][]{{3,6},{4,5}});
        qust.put(10,new Integer[][]{{3,6},{2,4,5}});
    }

    @RequestMapping(value = "/doAnswer",method = RequestMethod.GET)
    public ModelAndView doAnswer(HttpServletRequest request,
                         @ApiParam(required = true, name = "答案", value = "答案") String ans){
        ModelAndView mav = new ModelAndView("/func/fathersDay/statistics");
        Map<String,String> map = null;
        List list = new ArrayList<Map>();
        if(ans != null && ans.length() > 0){
            try {
                ans = URLDecoder.decode(ans,"utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            map = new HashMap<>();
            String[] strings = ans.split(",");
            if(strings != null && strings.length > 0){
                for(int i=0; i<strings.length; i++){
                    String string = strings[i];
                    String[] strs = string.split(":");
                    map.put(strs[0],strs[1]);
                }
            }
        }
        System.out.println("获取微信信息。。。。");
        WeixinAppUser weixinAppUser = (WeixinAppUser) request.getAttribute(CecurityConst.ll_live_weixin_app_user);
        String nickName = null;
        String imgUrl = null;
        if(weixinAppUser != null) {
            nickName = weixinAppUser.getNickname();
            imgUrl = weixinAppUser.getHeadimgurl();
        }else{
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if(token != null){
                AppUser mu = appUserService.getById(token.getId());
                nickName = mu.getName();
                imgUrl = mu.getPhoto();
            }
        }
        if(map != null && map.size() > 0){
            Map res = new HashMap<Integer,Double>(); //返回结果
            int count = 0; //结果个数
            //循环跑每个题
            for(Map.Entry<String,String> entry : map.entrySet()){
                int quest = Integer.parseInt(entry.getKey()); //题目
                int answer = Integer.parseInt(entry.getValue()); //用户所选的答案 （1 0）
                Integer[][] ints = qust.get(quest);
                if(ints.length-1 >= answer){
                    Integer[] opt = ints[answer];
                    for(int o : opt){
                        if(res.containsKey(o)){
                            double c = (double) res.get(o);
                            res.put(o,c+1);
                            count++;
                        }else{
                            res.put(o,1D);
                            count++;
                        }
                    }
                }
            }
            Set set = res.keySet();
            Iterator iter = set.iterator();
            while (iter.hasNext()){
                Integer k = (Integer) iter.next();
                Double v = (Double) res.get(k);
                Map kv = new HashMap<String,String>();
                kv.put("title",titles[k-1]);
                kv.put("value",new BigDecimal(v / count).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                list.add(kv);
            }
            Map m = new HashMap<>();
            m.put("nickName",nickName);
            m.put("imgUrl", imgUrl);
            m.put("result", JsonUtil.toJson(list));
            mav.addObject("m", m);
        }
        return mav;
    }

    @RequestMapping(value = "/answer")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("/func/fathersDay/answer");
        AppUserIdentity identity = handlerWeixinLogin(request, response);
        return result;
    }

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
                ActResultDto resultDto = appUserService.weixinLogin(weixinAppUser, invitationAppId, "weixin");
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
