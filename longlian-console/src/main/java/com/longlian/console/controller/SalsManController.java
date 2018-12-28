package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.UserCountService;
import com.longlian.console.service.VisitCourseRecordService;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.PageUrlService;
import com.longlian.model.PageUrl;
import com.longlian.type.Button;
import org.apache.commons.lang.time.DateFormatUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by admin on 2017/6/30.
 */
@Controller
@RequestMapping(value = "/salesMan")
public class SalsManController {
    @Autowired
    VisitCourseRecordService visitCourseRecordStatService;
    @Autowired
    UserCountService userCountService;
    @Autowired
    PageUrlService pageUrlService;
    @Autowired
    CourseService courseService;


    @RequestMapping("/getDateRangeMap")
    @ResponseBody
    public ActResultDto getDateRangeMap(DatagridRequestModel requestModel,@RequestParam String dateRange) throws Exception {
       ActResultDto result=new ActResultDto();

        Map map=new HashMap<>();
        map.put("dateRange",dateRange);
        Map dateRangeMap = userCountService.getDateRangeMap(map);
        result.setData(dateRangeMap);
        return result;
    }
    /**
     * 课程点击数统计 to 页面  （购买按钮，返回，播放统计）
     */
    @RequestMapping("/toCourseDetailClickDataStatistics")
    public ModelAndView toCourseDetailClickDataStatistics() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/toCourseDetailClickDataStatistics");
        return view;
    }

    @RequestMapping("/getCourseDetailClickDataStatistics")
    @ResponseBody
    public DatagridResponseModel getCourseDetailClickDataStatistics(DatagridRequestModel requestModel,@RequestParam Map map) throws Exception {
        DatagridResponseModel model = new DatagridResponseModel();
        List<Map> mapList = userCountService.getCourseDetailClickDataStatistics(requestModel, map);
        model.setRows(mapList);
        model.setTotal(requestModel.getTotal());
        return model;
    }
    /**
     * 课程详情统计时长
     * @Author:liuna
     * @return
     */
    @RequestMapping("/toCourseDetailDataStatistics")
    public ModelAndView toCourseDetailDataStatistics() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/toCourseDetailDataStatistics");
        return view;
    }

    /**
     * 统计课程详情 课程购买按钮，点击课程观看停留时长
     * 课程详情停留时长（课时 日期 秒）
     * 课程详情访问来源（课时，日期，来源 人数）
     * @return
     * @throws Exception
     */
    @RequestMapping("/getCourseDetailDataStatistics")
    @ResponseBody
    public DatagridResponseModel getCourseDetailDataStatistics(DatagridRequestModel requestModel,@RequestParam Map map) throws Exception {
        DatagridResponseModel model = new DatagridResponseModel();
      //  map.put("beginTime","2018-05-01");  map.put("endTime","2018-05-17");
        List<Map> mapList = userCountService.getCourseDetailDataStatistics(requestModel, map);
        model.setRows(mapList);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 主页面
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/index");
        setTime(  view , false);
        return view;
    }

    /**
     * 是否向前提一天
     * @param view
     * @param flag
     */
    public void setTime(ModelAndView view , boolean flag){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (flag) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        view.addObject("endTime", DateFormatUtils.format(calendar , "yyyy-MM-dd"));
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        view.addObject("beginTime", DateFormatUtils.format(calendar , "yyyy-MM-dd"));
    }

    @RequestMapping("/getCountMap")
    @ResponseBody
    public ActResult getCountMap(String beginTime , String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult=visitCourseRecordStatService.queryFriendCircleCountByFromType( beginTime ,   endTime);
        return actResult;
    }

    /**
     * 停留时长主页面
     * @return
     */
    @RequestMapping("/toStayLengthPage")
    public ModelAndView toStayLengthPage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/stayLength");
        setTime( view , true);
        view.addObject("urls" , userCountService.getUrls());
        return view;
    }


    /**
     * 停留时长
     * @return
     * @throws Exception
     */
    @RequestMapping("/getStayLength")
    @ResponseBody
    public ActResult getStayLength(Long objectId,String beginTime,String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult=userCountService.getLengthOfStay(objectId,beginTime,endTime);
        return actResult;
    }

    /**
     * 退出率主页面
     * @return
     */
    @RequestMapping("/toExitRatePage")
    public ModelAndView toExitRatePage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/exitRate");
        setTime(  view, true);
        view.addObject("urls" , userCountService.getUrls());
        return view;
    }

    /**
     * 退出率
     * @return
     * @throws Exception
     */
    @RequestMapping("/getExitRate")
    @ResponseBody
    public ActResult getExitRate(Long objectId,String beginTime,String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult=userCountService.getExitRate(objectId, beginTime, endTime);
        return actResult;
    }

    /**
     * 访问执行平均时间
     * @return
     */
    @RequestMapping("/toPageHandleTimePage")
    public ModelAndView toPageHandleTimePage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/handletime");
        setTime(view, true);
        view.addObject("urls" , userCountService.getUrls());
        return view;
    }

    /**
     * 访问执行平均时间
     * @return
     * @throws Exception
     */
    @RequestMapping("/getPageHandleTime")
    @ResponseBody
    public ActResult getPageHandleTime(Long objectId,String beginTime,String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult=userCountService.getPageHandleTime(objectId, beginTime, endTime);
        return actResult;
    }

    /**
     * 访问数主页面
     * @return
     */
    @RequestMapping("/toVisitsPage")
    public ModelAndView toVisitsPage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/visit");
        setTime(view, true);
        view.addObject("urls" , userCountService.getUrls());
        return view;
    }

    /**
     * 访问数
     * @return  
     * @throws Exception
     */
    @RequestMapping("/getVisits")
    @ResponseBody
    public ActResult getVisits(Long objectId,String beginTime,String endTime) throws Exception {
        ActResult actResult=new ActResult();
        actResult=userCountService.getVisit(objectId, beginTime, endTime);
        return actResult;
    }

    /**
     * 按钮点击次数主页面
     * @return
     */
    @RequestMapping("/toButtonClickPage")
    public ModelAndView toButtonClickPage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/buttonClick");
        view.addObject("buttons" , Button.getList());
        view.addObject("first" , Button.getList().get(0));
        setTime(view, true);
        return view;
    }

    /**
     * 按钮点击次数
     * @return
     * @throws Exception
     */
    @RequestMapping("/getButtonClick")
    @ResponseBody
    public ActResult getButtonClick(String beginTime,String endTime,String objectValue) throws Exception {
        ActResult actResult=new ActResult();
        actResult=userCountService.getButtonClick( beginTime, endTime,objectValue);
        return actResult;
    }
    /**
     * 按钮点击次数详情
     * @return
     * @throws Exception
     */
    @RequestMapping("/getButtonClickDetail")
    @ResponseBody
    public ActResult getButtonClickDetail(String objectValue , String dateStr) throws Exception {
        ActResult actResult=new ActResult();
        actResult = userCountService.getButtonClickDetail(objectValue , dateStr);
        return actResult;
    }

    /**
     * url管理主页面
     * @return
     */
    @RequestMapping("/toPageUrlPage")
    public ModelAndView toPageUrlPage() {
        ModelAndView view = new ModelAndView("/func/dataStatistics/pageUrl");
        return view;
    }
    @RequestMapping(value = "/getPageUrlList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getPageUrlList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(pageUrlService.getPageUrlListPage(requestModel, map));
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
        ModelAndView view = new ModelAndView("/func/dataStatistics/addOrUpdate");
        view.addObject("id", id);
        return view;
    }
    /**
     * 添加或者修改保存
     * @param 
     * @return
     */
    @RequestMapping(value = "/savePageUrl" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult savePageUrl(@RequestBody PageUrl pageUrl){
        ActResult actResult = new ActResult();
        pageUrlService.insertPageUrl(pageUrl);
        return actResult;
    }
    
    @RequestMapping(value = "/findById" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        PageUrl pageUrl =pageUrlService.findById(id);
        if(pageUrl==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(pageUrl);
        }
        return actResult;
    }
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(Long id) {
        ActResult actResult = new ActResult();
        try {
            pageUrlService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }
}
