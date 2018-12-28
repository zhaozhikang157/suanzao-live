package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.ProxyTeacherService;
import com.longlian.live.service.ProxyUserService;
import com.longlian.model.AdvertisingDisplay;
import com.longlian.model.ProxyTeacher;
import com.longlian.model.ProxyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/5/4.
 */
@Controller
@RequestMapping(value = "/proxyUser")
public class ProxyUserController {

    private static Logger log = LoggerFactory.getLogger(ProxyUserController.class);

    @Autowired
    ProxyUserService proxyUserService;
    @Autowired
    ProxyTeacherService proxyTeacherService;

    /**
     * 跳转页面
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/proxyUser/index");
        return view;
    }
    /**
     *获取代理用户列表
     *
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/getProxyUserList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getUserCountRecordlist(DataGridPage requestModel,String status) {
        DatagridResponseModel drm = new DatagridResponseModel();
        drm.setRows(proxyUserService.getProxyUserListPage(requestModel,status));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }
    /**
     * 设置代理用户
     *
     * @param 
    * @return 
            */
    @RequestMapping(value = "/setProxyUser", method = RequestMethod.POST)
    @ResponseBody
    public ActResult setProxyUser(Long id) {
        ActResult result = new ActResult();
        try{
            ProxyUser ProxyUser = proxyUserService.getProxyUserByAppId(id);
            if(Utility.isNullorEmpty(ProxyUser)) {
                ProxyUser = new ProxyUser();
                ProxyUser.setAppId(id);
                ProxyUser.setCreateTime(new Date());
                ProxyUser.setStatus("0");
                proxyUserService.insertSelective(ProxyUser);
            }else
            {
                Map map = new HashMap();
                map.put("id",ProxyUser.getId());
                map.put("status",0);
                proxyUserService.updateProxyUser(map); 
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("设置异常");
        }
        return result;
    }
    //取消代理用户
    @RequestMapping(value = "/removeProxyUser", method = RequestMethod.POST)
    @ResponseBody
    public ActResult removeProxyUser(Long id) {
        ActResult result = new ActResult();
        try{
            Map map = new HashMap();
            map.put("id",proxyUserService.getProxyUserByAppId(id).getId());
            map.put("status",1);
            proxyUserService.updateProxyUser(map);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("设置异常");
        }
        return result;
    }

    /**
     * 去设置代理用户关系页面
     *
     * @param  
     * @return
     */
    @RequestMapping(value = "/toProxyTeacher", method = RequestMethod.GET)
    public ModelAndView toProxyTeacher(Long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/proxyUser/proxyTeacher");
        view.addObject("id",id);
        return view;
    }
    /**
     * 去查看详情页面
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/toLookDetail", method = RequestMethod.GET)
    public ModelAndView toLookDetail(Long id) throws Exception {
         Map map =  proxyUserService.getProxyUserDetail(id);
        String date_time_fmt2_str = "yyyy-MM-dd HH:mm";
        SimpleDateFormat date_time_fmt3 = new SimpleDateFormat(date_time_fmt2_str);
        Timestamp startTime = (Timestamp) map.get("createTime");
        Date d = new Date(startTime.getTime());
        String startTimeStr = Utility.getDateTimeStr(d, date_time_fmt3);
        map.put("startTimeStr", startTimeStr);
        ModelAndView view = new ModelAndView("/func/proxyUser/lookDetail");
        view.addObject("proxyUser",map);
        return view;
    }
    //确定代理用户关系
    @RequestMapping(value = "/sureProxyTeacher", method = RequestMethod.POST)
    @ResponseBody
    public ActResult sureProxyTeacher(Long id,Long userId) {
        ActResult result = new ActResult();
        try{
            if(id==userId)
            {
                result.setMsg("代理人不能是自己本人！");
                result.setCode(1111);
                return result;
            }
          ProxyTeacher ProxyTeacher= proxyTeacherService.getProxyTeacherByAppId(null,userId);
//            ProxyUser ProxyUser = proxyUserService.getProxyUserByAppId(id);
            if(Utility.isNullorEmpty(ProxyTeacher)) {
                 ProxyTeacher = new ProxyTeacher();
                ProxyTeacher.setStatus("0");
                ProxyTeacher.setCreateTime(new Date());
                ProxyTeacher.setProxyAppId(id);
                ProxyTeacher.setTeacherId(userId);
                proxyTeacherService.insertSelective(ProxyTeacher);
          }else
            {
                Map map = new HashMap();
                map.put("id",ProxyTeacher.getId());
                map.put("status",0);
                map.put("proxyAppId",id);
                proxyTeacherService.updateProxyTeacher(map);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("设置异常");
        }
        return result;
    }
    //解除代理用户关系
    @RequestMapping(value = "/removeProxyTeacher", method = RequestMethod.POST)
    @ResponseBody
    public ActResult removeProxyTeacher(Long id) {
        ActResult result = new ActResult();
        try{
            Map map = new HashMap();
            map.put("id", proxyTeacherService.getProxyTeacherByAppId(null,id).getId());
            map.put("status", 1);
            map.put("proxyAppId",null);
            proxyTeacherService.updateProxyTeacher(map);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("设置异常");
        }
        return result;
    }
}
