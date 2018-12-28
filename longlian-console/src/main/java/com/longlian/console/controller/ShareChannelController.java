package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.ShareChannelService;
import com.longlian.live.service.ChannelVisitRecordService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.ChannelVisitRecord;
import com.longlian.model.ShareChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/6/16.
 */
@Controller
@RequestMapping("/shareChannel")
public class ShareChannelController {
    private static Logger log = LoggerFactory.getLogger(ShareChannelController.class);

    @Autowired
    ShareChannelService shareChannelService;
    @Autowired
    ChannelVisitRecordService channelVisitRecordService;
    @Autowired
    WechatOfficialService wechatOfficialService;

    
    /**
     * 渠道管理
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/shareChannel/index");
    }

    @RequestMapping(value = "/getShareChannelList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getShareChannelList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(shareChannelService.getShareChannelListPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    /**
     * 添加编辑页面
     * @param id
     * @return
     */
    @RequestMapping("/toAddOrUpdate")
    public ModelAndView toAddOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/shareChannel/addOrUpdate");
        view.addObject("id", id);
        return view;
    }
    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        ShareChannel shareChannel =shareChannelService.findById(id);
        if(shareChannel==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(shareChannel);
        }
        return actResult;
    }
    /**
     * 添加或者修改保存
     * @param shareChannel
     * @return
     */
    @RequestMapping(value = "/saveShareChannel" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveShareChannel(@RequestBody ShareChannel shareChannel){
        ActResult actResult = new ActResult();
        shareChannelService.saveShareChannel(shareChannel);
        return actResult;
    }

    @RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(String ids) {
        ActResult actResult = new ActResult();
        if (StringUtils.isEmpty(ids)) {
            return ActResult.fail("请选择要删除项!");
        }
        try {
            shareChannelService.deleteById(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }

    /**
     * 课程渠道数据统计
     * @param requestModel
     * @param 
     * @return
     */
    @RequestMapping(value = "/getChannelRecordList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getChannelRecordList(DatagridRequestModel requestModel,String name,Long courseId) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(shareChannelService.getChannelRecordListPage(requestModel,name,courseId));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    @RequestMapping(value = "/toCourseChannelCount", method = RequestMethod.GET)
    public ModelAndView toCourseChannelCount(Long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/shareChannel/courseChannelCount");
        view.addObject("id", id);
        return view;
    }

    /**
     * 生成微信二维码
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/generateWeiXinQRCode")
    @ResponseBody
    public ActResult generateWeiXinQRCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActResult actResult = new ActResult();
        String courseId = request.getParameter("courseId");
        String channelId =  request.getParameter("channelId");//
        if(Utility.isValidLong(courseId) &&  Utility.isValidLong(channelId) ) {
            String url = shareChannelService.generateWeiXinQRCode(Utility.parseLong(courseId), Utility.parseLong(channelId));
            actResult.setData(url);
        }else{
            actResult.setSuccess(false);
            actResult.setMsg("课程Id和渠道ID不能为空！");
        }
        return  actResult;
    }

 
}
