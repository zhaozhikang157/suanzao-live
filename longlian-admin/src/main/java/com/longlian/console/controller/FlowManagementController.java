package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.util.FlowManagementUtil;
import com.longlian.model.LiveStreamOnlineInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by longlian007 on 2018/2/7.
 * 流管理控制器
 */
@Controller
@RequestMapping("/flowManagementController")
public class FlowManagementController {
    //日志输出
    private static Logger log = LoggerFactory.getLogger(FlowManagementController.class);
    @Autowired
    private FlowManagementUtil flowManagementUtil;

    /**
     *直播流列表
     * @return
     */
    @RequestMapping(value = "/getFlowManagements",method = RequestMethod.GET)
    @ResponseBody
    public DatagridResponseModel getFlowManagements(){
        DatagridResponseModel model=new DatagridResponseModel();
        List<LiveStreamOnlineInfo> liveStreamOnlineInfos = flowManagementUtil.findLiveStreamOnlineInfos();
        model.setRows(liveStreamOnlineInfos);
        model.setTotal(liveStreamOnlineInfos.size());
        return model;
    }

    /**
     * 直播流列表页面
     * @return
     */
    @RequestMapping("/getFlowManagementsPage")
    public ModelAndView toAddOrUpdate(){
        ModelAndView view = new ModelAndView("/func/flowManagement/index");
        return view;
    }

    /**
     * 直播黑名单列表页面
     * @return
     */
    @RequestMapping("/blockListPage")
    public ModelAndView blockListPage(){
        ModelAndView view = new ModelAndView("/func/flowManagement/indexBlockList");
        return view;
    }

    /**
     * 加入黑名单
     * @return
     */
    @RequestMapping(value = "/liveAddBlacklist",method = RequestMethod.POST)
    @ResponseBody
    public ActResult liveAddBlacklist(String appName,String streamName){
        ActResult actResult=new ActResult();
        try {
            String s = flowManagementUtil.liveAddBlacklist(appName, streamName);
            log.info("该条任务请求 ID:"+s);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("加入黑名单失败");
        }
        return actResult;
    }

    /**
     * 恢复
     * @return
     */
    @RequestMapping(value = "/recoveryBlacklist",method = RequestMethod.POST)
    @ResponseBody
    public ActResult recoveryBlacklist(String appName,String streamName){
        ActResult actResult=new ActResult();
        try {
            String s = flowManagementUtil.recoveryBlacklist(appName, streamName);
            log.info("该条任务请求 ID:"+s);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("恢复失败");
        }
        return actResult;
    }

    /**
     * 黑名单列表
     * @return
     */
    @RequestMapping(value = "/getDescribeLiveStreamsBlockList",method = RequestMethod.GET)
    @ResponseBody
    public DatagridResponseModel getDescribeLiveStreamsBlockList(){
        DatagridResponseModel model=new DatagridResponseModel();
        List<LiveStreamOnlineInfo> liveStreamOnlineInfos = flowManagementUtil.findDescribeLiveStreamsBlockList();
        model.setRows(liveStreamOnlineInfos);
        model.setTotal(liveStreamOnlineInfos.size());
        return model;
    }

}
