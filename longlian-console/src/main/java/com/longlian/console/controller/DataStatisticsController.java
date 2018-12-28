package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.dao.CourseMapper;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.UserCountService;
import com.longlian.console.service.VisitCourseRecordService;
import com.longlian.console.util.SystemUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.PageUrlService;
import com.longlian.model.PageUrl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import com.longlian.type.Button;
import org.apache.commons.lang.time.DateFormatUtils;

import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by admin on 2017/6/30.
 */
@Controller
@RequestMapping(value = "/dataStatistics")
public class DataStatisticsController {
    @Autowired
    VisitCourseRecordService visitCourseRecordStatService;
    @Autowired
    UserCountService userCountService;
    @Autowired
    PageUrlService pageUrlService;
    @Autowired
    CourseService courseService;


    /**
     *  课程 详情页面 用户访问时长统计统计导出功能
     * @return
     */
    @RequestMapping(value = "/importCoursePageCountExcel", method = RequestMethod.GET)
    @ResponseBody
    public void importCoursePageCountExcel(HttpServletRequest req, HttpServletResponse response, String liveTopic,
                                       String appUserName, String beginDate, String endDate,String portType,String pageUrl) throws IOException {
        Map requestMap=new HashMap<>();

        requestMap.put("liveTopic",liveTopic);requestMap.put("appUserName",appUserName);
        requestMap.put("beginDate",beginDate);requestMap.put("endDate",endDate);
        requestMap.put("portType", portType);requestMap.put("type", pageUrl);
        ExportExcelWhaUtil exportExcelWhaUtil = userCountService.importButtonCountExcel(req,response,requestMap);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=coursePageDetail.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *  课程 详情 按钮统计导出功能
     * @return
     */
    @RequestMapping(value = "/importButtonCountExcel", method = RequestMethod.GET)
    @ResponseBody
    public void importButtonCountExcel(HttpServletRequest req, HttpServletResponse response, String liveTopic,
                            String appUserName, String beginDate, String endDate,String portType) throws IOException {
        Map requestMap=new HashMap<>();

        requestMap.put("liveTopic",liveTopic);requestMap.put("appUserName",appUserName);
        requestMap.put("beginDate",beginDate);requestMap.put("endDate",endDate);
       requestMap.put("portType", portType);
        ExportExcelWhaUtil exportExcelWhaUtil = userCountService.importButtonCountExcel(req,response,requestMap);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=coursePageButton.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    @RequestMapping("/getDateRangeMap")
    @ResponseBody
    public ActResultDto getDateRangeMap(DatagridRequestModel requestModel,@RequestParam String dateRange,@RequestParam String pageUrl) throws Exception {
       //pageurl 0:课程详情  1：直播间
        ActResultDto result=new ActResultDto();
        Map map=new HashMap<>();
        map.put("dateRange",dateRange);
        map.put("type",pageUrl);
        if(pageUrl==null){
            map.put("type",0);
        }
        Map dateRangeMap = userCountService.getDateRangeMap(map);
        result.setData(dateRangeMap);
        return result;
    }
    @RequestMapping("/getClickDateRangeMap")
    @ResponseBody
    public ActResultDto getClickDateRangeMap(DatagridRequestModel requestModel,@RequestParam String dateRange) throws Exception {
        ActResultDto result=new ActResultDto();
        Map map=new HashMap<>();
        map.put("dateRange",dateRange);
        Map dateRangeMap = userCountService.getClickDateRangeMap(map);
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
    public DatagridResponseModel getCourseDetailClickDataStatistics(@RequestParam(value ="sort", required = false)String sort,
                                                                    @RequestParam(value = "order", required = false)String orderType,
                                                                    DatagridRequestModel requestModel,@RequestParam Map map) throws Exception {
        if(sort!=null && !"".equals(sort) ){
            //  String field =  com.huaxin.util.StringUtil.propertyToField(requestModel.getSort());
            map.put("sort",sort);
        }
        map.put("orderType",orderType);
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
     * 统计课程详情/直播间详情 课程购买按钮，点击课程观看停留时长
     * 课程详情停留时长（课时 日期 秒）
     * 课程详情访问来源（课时，日期，来源 人数）
     * @return
     * @throws Exception
     */
    @RequestMapping("/getCourseDetailDataStatistics")
    @ResponseBody
    public DatagridResponseModel getCourseDetailDataStatistics(@RequestParam(value ="sort", required = false)String sort,
                                                               @RequestParam(value = "order", required = false)String orderType,
                                                               DatagridRequestModel requestModel,@RequestParam Map map) throws Exception {
        DatagridResponseModel model = new DatagridResponseModel();
        String str="";
        //  map.put("beginTime","2018-05-01");  map.put("endTime","2018-05-17");
        if(sort!=null && !"".equals(sort) ){
            map.put("sort",sort);    //不指定端口
            /**     排序判断 实际排序字段  by portType start------*/
            if(sort.equals("pCount")){  //访问人数
                if("1".equals(map.get("portType")+"")){  //ios
                    map.put("sort","iosCountp");
                }else if("2".equals(map.get("portType")+"")){  //Android
                    map.put("sort","androidCountp");
                }else if("3".equals(map.get("portType")+"")){  //微信
                    map.put("sort","weixinCountp");
                }
            }else if(sort.equals("vCount")){   //访问次数
                if("1".equals(map.get("portType")+"")){  //ios
                    map.put("sort","iosCountc");
                }else if("2".equals(map.get("portType")+"")){  //Android
                    map.put("sort","androidCountc");
                }else if("3".equals(map.get("portType")+"")){  //微信
                    map.put("sort","weixinCountc");
                }
            }else if(sort.equals("totalStayTime")){
                if("1".equals(map.get("portType")+"")){  //ios
                    map.put("sort","iosStayTime");
                }else if("2".equals(map.get("portType")+"")){  //Android
                    map.put("sort","androidStayTime");
                }else if("3".equals(map.get("portType")+"")){  //微信
                    map.put("sort","weixinStayTime");
                }
            }
            /**     排序判断 实际排序字段  by portType end ------*/
        }
        map.put("orderType", orderType);


        map.get("orderType");
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
