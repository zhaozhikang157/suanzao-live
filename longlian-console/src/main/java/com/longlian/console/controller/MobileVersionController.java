package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.MobileVersionDto;
import com.longlian.model.MobileVersion;
import com.longlian.console.service.MobileVersionService;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.MobileVersionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/22.
 */
@RequestMapping("/mobileVersion")
@Controller
public class MobileVersionController {
    private static Logger log = LoggerFactory.getLogger(MobileVersionController.class);


    @Autowired
    MobileVersionService mobileVersionService;


    /**
     * 模块主页面
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/mobileVersion/index");
    }

    /**
     * 展示页面
     *
     * @param mobileVersion requestModel
     * @return
     */
    @RequestMapping("/findAll")
    @ResponseBody
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, MobileVersionDto mobileVersion) {
        return mobileVersionService.getListPage(requestModel, mobileVersion);
    }

    /**
     * 去修改页面
     *
     * @param mobileVersion
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(MobileVersion mobileVersion) throws Exception {
        long id = mobileVersion.getId();
        ModelAndView view = new ModelAndView("/func/mobileVersion/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getSerById(long id) throws Exception {
        ActResult result = new ActResult();
        MobileVersion mobileVersion = mobileVersionService.findById(id);
        result.setData(mobileVersion);
        return result;
    }


    /**
     * 保存修改
     *
     * @param mobileVersion
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(HttpServletRequest request, @RequestBody MobileVersion mobileVersion){
        ActResult result = new ActResult();
        ConsoleUserIdentity user = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        mobileVersion.setUserName(user.getAccount());
        try{
            mobileVersionService.doSaveAndUpdate(mobileVersion);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("版本保存失败");
        }
        return result;
    }


    /**
     * 获取版本类型
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getVersionType", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getVersionType() throws Exception {
        ActResult result = new ActResult();
        Map map = new HashMap();
        map.put("versionTypes", MobileVersionType.getList());
        result.setData(map);
        return result;
    }

    /**
     * 获取版本状态
     *
     * @throws Exception
     */
    @RequestMapping(value = "/getStateList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getStateList() throws Exception {
        ActResult result = new ActResult();
        Map map = new HashMap();
        List<Map> list = new ArrayList<Map>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "下线");
        map1.put("id", "0");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("name", "上线");
        map2.put("id", "1");
        list.add(map1);
        list.add(map2);
        map.put("statuses", list);
        result.setData(map);
        return result;
    }

    /**
     * 获取是否强制上传状态
     *
     * @throws Exception
     */
    @RequestMapping(value = "/getIsFoceUpdate", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getIsFoceUpdate() throws Exception {
        ActResult result = new ActResult();
        Map map = new HashMap();
        List<Map> list = new ArrayList<Map>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "不强制升级");
        map1.put("id", "0");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("name", "强制升级");
        map2.put("id", "1");
        list.add(map1);
        list.add(map2);
        map.put("isFoceUpdates", list);
        result.setData(map);
        return result;
    }
}
