package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.type.WechatTemplateMessage;
import com.longlian.console.service.SystemLogService;
import com.longlian.dto.SystemLogDto;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/22.
 */
@Controller
@RequestMapping(value = "/systemLogController")
public class SystemLogController {
    private static Logger log = LoggerFactory.getLogger(SystemLogController.class);

    @Autowired
    SystemLogService systemLogService;

    @Autowired
    WeixinUtil weixinUtil;

    @RequestMapping("index")
    public ModelAndView index(){
        return new ModelAndView("/func/systemLog/index");
    }


    @RequestMapping(value = "/getList" ,method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList( DatagridRequestModel datagridRequestModel,SystemLogDto systemLogDto){
        DatagridResponseModel model=new DatagridResponseModel();
        model.setRows(systemLogService.getListPage(datagridRequestModel,systemLogDto));
        model.setTotal(datagridRequestModel.getTotal());

        return model;
    }

    /**
     * 获取日志类型和表类型下拉选条件
     * @return
     */
    @RequestMapping("/getLogTypeAndTableType" )
        @ResponseBody
        public ActResult getLogTypeAndTableType(){
            ActResult actResult=new ActResult();
            Map map=new HashMap();
            map.put("logTypeList", LogType.getList());  //日志类型
            map.put("logTableTypeList", LogTableType.getList());  //日志类型
            actResult.setData(map);
            return actResult;
    }

    /**
     * 发送升级消息页面
     * @return
     */
    @RequestMapping("toUpgradeMsg")
    public ModelAndView toUpgradeMsg(){
        return new ModelAndView("/func/systemLog/toUpgradeMsg");
    }

    /**
     * 发送升级消息
     * @param 
     * @return
     */
    @RequestMapping(value = "/sendUpgradeMsg",method = RequestMethod.POST)
    @ResponseBody
    public ActResult sendUpgradeMsg(@RequestBody  WechatTemplateMessage WechatTemplateMessage){
        ActResult actResult=new ActResult();
        Map map=new HashMap();
       String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        String website = CustomizedPropertyConfigurer.getContextProperty("live_website");
        String templateId = CustomizedPropertyConfigurer.getContextProperty("sytem_update_template_id");
        WechatTemplateMessage.setKeyword1Color("");
        WechatTemplateMessage.setUrl(website + LocalOauth2Url.personalCenter + "?fromWeixinMsg=1");
        WechatTemplateMessage.setTempalteId(templateId);
        weixinUtil.sendTemplateMassageAll(ll_appid, WechatTemplateMessage);
        actResult.setData(map);
        return actResult;
    }


}
