package com.longlian.console.controller;

import cn.jpush.api.utils.StringUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.huaxin.util.ActResult;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.AppUserService;
import com.longlian.live.service.LiveService;
import com.longlian.model.AppUser;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by U on 2016/8/19.
 */
@Controller
@RequestMapping("/annualMeetingDraw")
public class AnnualMeetingDrawController {

    private static Logger log = LoggerFactory.getLogger(AnnualMeetingDrawController.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveService liveService;
    @Autowired
    AppUserService appUserService;
    /**
     * 抽奖页面
     *
     * @return
     */
    @RequestMapping("/toAnnualMeetingDraw")
    public ModelAndView toAnnualMeetingDraw() {
        ModelAndView model = new ModelAndView("/subsys/annualMeeting/toAnnualMeetingDraw");
        return model;
    }
    /**
     * 抽奖注册页面
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toRegister")
    public ModelAndView toRegister() throws Exception {
        ModelAndView model = new ModelAndView("/subsys/annualMeeting/annualMeetingDraw");
        return model;
    }


    /**
     * 年会注册页面
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ActResult register(String phone) {
        ActResult ac = new ActResult();
        if (redisUtil.sismember(RedisKey.annual_meeting_draw_for_2016_phone, phone) || redisUtil.sismember(RedisKey.annual_meeting_draw_for_2016_select_phone, phone)) {
            ac.setSuccess(false);
            ac.setMsg("该手机号已注册");
        } else {
            redisUtil.sadd(RedisKey.annual_meeting_draw_for_2016_phone, phone);
            ac.setSuccess(true);
            ac.setMsg("注册成功");
        }
        return ac;
    }
    /**
     * 获取50电话号码
     *
     * @return
     */
    @RequestMapping(value = "/getPhone")
    @ResponseBody
    public ActResult getPhone() {
        Set<String> allPhone = redisUtil.smembers(RedisKey.annual_meeting_draw_for_2017_select_phone);
        if(allPhone!=null&&allPhone.size()>0){
            redisUtil.del(RedisKey.annual_meeting_draw_for_2017_phone);
        }

        ActResult actResult = new ActResult();
        Set<String> allUsers =  liveService.getAllUses(6435L);
        //获取参与活动所有手机的数量
        Set phoneSet = new HashSet<>();
        for(int i=0;i<60;i++){
            redisUtil.sadd(RedisKey.annual_meeting_draw_for_2017_phone, "测试"+i);
            phoneSet.add("测试"+i );
        }
        for(String userId:allUsers){
            if(StringUtils.isNotEmpty(userId)){
                AppUser appUser = appUserService.getAppUserById(Long.parseLong(userId));
                if(null!=appUser){
                    if(StringUtils.isNotEmpty(appUser.getName())&&appUser.getId()>0){
                        String str  = appUser.getName()+"-ID:"+appUser.getId();
                        int i= 0;
                        if(allPhone!=null&&allPhone.size()>0){
                            for(String user:allPhone){
                                if(str.equals(user.toString())){
                                    i = 1;
                                }
                            }
                        }
                        if(i==1){
                            continue;
                        }
                        redisUtil.sadd(RedisKey.annual_meeting_draw_for_2017_phone, str);
                        phoneSet.add(str);
                    }
                }
            }
        }
        //从reids获取50个手机号
        Set set = subSet(phoneSet, 50);
        Map map = new HashMap();
        map.put("phoneList", set.toArray());
        map.put("number", redisUtil.scard(RedisKey.annual_meeting_draw_for_2017_phone));
        actResult.setData(map);
        return actResult;
    }

    /**
     * 重置
     *
     * @return
     */
    @RequestMapping(value = "/reset")
    @ResponseBody
    public void reset() {
        Set<String> phoneList = redisUtil.smembers(RedisKey.annual_meeting_draw_for_2017_select_phone);
        for (String s : phoneList) {
            redisUtil.sadd(RedisKey.annual_meeting_draw_for_2017_phone, s);
        }
        redisUtil.del(RedisKey.annual_meeting_draw_for_2017_select_phone);
    }


    /**
     * 抽奖
     *
     * @return
     */
    @RequestMapping(value = "/draw")
    @ResponseBody
    public ActResult draw() {
        ActResult actResult = new ActResult();
        //获取参与活动所有手机的号码
        Set allPhone = redisUtil.smembers(RedisKey.annual_meeting_draw_for_2017_phone);
    /*    String[] s = {"15510666680", "13611175882", "18001293543", "13301348966", "18811198536", "13811583415", "13801289652",
                "18611665603", "13321162333", "18810151818", "13901032329", "18872740091", "15807181255", "13381056768", "13601322933",
                "15300062855", "18510711111", "13601196389", "135226" +
                "00659", "13701226293", "18600576222", "13120155777", "13391988811",
                "13810577087", "15313179777", "18500044486", "18911649092", "13721057957", "15156999077", "13269335588", "13905589351",
                "13501315553", "18810109616", "18346379913", "18210626132", "13911194990", "13811320399", "18811429588", "18601289358",
                "13311562411", "15003317700", "18611240124", "18518900034", "13910751921", "18842325460", "13911676208", "15510498765",
                "13426007475", "13611118722", "18518547887", "13581749430", "13785085288", "13810313069", "13121573088", "13269667758"};

        for (int i = 0; i < s.length; i++) {
            if (allPhone.contains(s[i])) {
                allPhone.remove(s[i]);
            }
        }*/
        //随机抽一个
        String phone = getRandomElement(allPhone);
        //将所有的删除当前的一个
        redisUtil.srem(RedisKey.annual_meeting_draw_for_2017_phone, phone);
        //添加抽中的手机号
        redisUtil.sadd(RedisKey.annual_meeting_draw_for_2017_select_phone, phone);
        //返回数量和抽中的手机号
        Map map = new HashMap();
        map.put("selectPhone", phone);
        map.put("phoneNumber", redisUtil.scard(RedisKey.annual_meeting_draw_for_2017_phone));
        actResult.setData(map);
        return actResult;
    }

    /**
     * 从set中获取子集合
     *
     * @param objSet size
     * @return
     */
    public Set<Object> subSet(Set<Object> objSet, int size) {
        if (CollectionUtils.isEmpty(objSet)) {
            return Collections.emptySet();
        }
        return ImmutableSet.copyOf(Iterables.limit(objSet, size));
    }

    /**
     * 从set中随机取得一个元素
     *
     * @param set
     * @return
     */
    public String getRandomElement(Set<String> set) {
        int rn = (int) (Math.random() * set.size());
        int i = 0;
        for (String s : set) {
            if (i == rn) {
                return s;
            }
            i++;
        }
        return null;
    }
}
