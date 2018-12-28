package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.live.service.RewardRecordService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pangchao on 2017/3/2. 奖励记录
 */
@Controller
@RequestMapping("/rewardRecord")
public class RewardRecordController {
    private static Logger log = LoggerFactory.getLogger(RewardRecordController.class);
    @Autowired
    RewardRecordService rewardRecordService;
    /**
     * 推介老师奖励
     */
    @RequestMapping("/recommendTeacherRewardRecord")
    public ModelAndView index() {
        return new ModelAndView("/func/rewardRecord/recommendTeacher");
    }

    /**
     * 获取推介老师奖励列表
     *
     * @param page
     * @param map
     * @return
     */
    @RequestMapping(value = "/getRecommendTeacherList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getRecommendTeacherList(DatagridRequestModel page,@RequestParam Map map) {
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        datagridResponseModel.setRows(rewardRecordService.getRecommendTeacherList(page, map));
        datagridResponseModel.setTotal(page.getTotal());
        return datagridResponseModel;
    }

    /**
     * 推介老师总金额
     * @param map
     * @return
     */
    @RequestMapping(value = "/getRecommendTeacherAccount", method = RequestMethod.POST)
    @ResponseBody
    public ActResult getRecommendTeacherAccount(@RequestParam Map map) {
        ActResult ac = new ActResult();
        ac.setData(rewardRecordService.getRecommendTeacherAccount(map));
        return ac;
    }

    /**
     * 推介老师导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelRecommendTeacher" )
    public void exportExcelRecommendTeacher(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("recommendId", request.getParameter("recommendId"));
            map.put("recommendName", request.getParameter("recommendName"));
            map.put("startTime", request.getParameter("startTime"));
            map.put("endTime", request.getParameter("endTime"));
            map.put("startAmount", request.getParameter("startAmount"));
            map.put("endAmount", request.getParameter("endAmount"));
            String path = rewardRecordService.exportExcelRecommendTeacher(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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
     * 老师粉丝关注奖励
     */
    @RequestMapping("/followRewardRecord")
    public ModelAndView followRewardRecord() {
        return new ModelAndView("/func/rewardRecord/followRewardRecord");
    }

    /**
     * 获取老师粉丝关注奖励列表
     *
     * @param page
     * @param map
     * @return
     */
    @RequestMapping(value = "/getFollowRewardRecordList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getFollowRewardRecordList(DatagridRequestModel page,@RequestParam Map map) {
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        datagridResponseModel.setRows(rewardRecordService.getFollowRewardRecordList(page, map));
        datagridResponseModel.setTotal(page.getTotal());
        return datagridResponseModel;
    }


    /**
     * 老师粉丝关注奖励总金额
     * @param map
     * @return
     */
    @RequestMapping(value = "/getFollowRewardRecordAccount", method = RequestMethod.POST)
    @ResponseBody
    public ActResult getFollowRewardRecordAccount(@RequestParam Map map) {
        ActResult ac = new ActResult();
        ac.setData(rewardRecordService.getFollowRewardRecordAccount(map));
        return ac;
    }

    /**
     * 老师粉丝关注导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelFollowRewardRecord" )
    public void exportExcelFollowRewardRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("appId", request.getParameter("appId"));
            map.put("name", request.getParameter("name"));
            map.put("startTime", request.getParameter("startTime"));
            map.put("endTime", request.getParameter("endTime"));
            map.put("startAmount", request.getParameter("startAmount"));
            map.put("endAmount", request.getParameter("endAmount"));
            String path = rewardRecordService.exportExcelFollowRewardRecord(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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
     * 开课奖明细
     */
    @RequestMapping("/courseRewardRecord")
    public ModelAndView courseRewardRecord() {
        return new ModelAndView("/func/rewardRecord/courseRewardRecord");
    }
    /**
     * 开课奖明细表
     *
     * @param page
     * @param map
     * @return
     */
    @RequestMapping(value = "/getCourseRecommendList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCourseRecommendList(DatagridRequestModel page,@RequestParam Map map) {
        DatagridResponseModel datagridResponseModel = new DatagridResponseModel();
        datagridResponseModel.setRows(rewardRecordService.getCourseRecommendList(page, map));
        datagridResponseModel.setTotal(page.getTotal());
        return datagridResponseModel;
    }


    /**
     * 开课奖总金额
     * @param map
     * @return
     */
    @RequestMapping(value = "/getCourseRecommendAccount", method = RequestMethod.POST)
    @ResponseBody
    public ActResult getCourseRecommendAccount(@RequestParam Map map) {
        ActResult ac = new ActResult();
        ac.setData(rewardRecordService.getCourseRecommendAccount(map));
        return ac;
    }
    /**
     * 开课奖导出
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportExcelCourseRewardRecord" )
    public void exportExcelCourseRewardRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream sos = response.getOutputStream();
        try {
            Map map = new HashMap();
            map.put("appId", request.getParameter("appId"));
            map.put("name", request.getParameter("name"));
            map.put("startTime", request.getParameter("startTime"));
            map.put("endTime", request.getParameter("endTime"));
            map.put("startAmount", request.getParameter("startAmount"));
            map.put("endAmount", request.getParameter("endAmount"));
            String path = rewardRecordService.exportExcelCourseRewardRecord(map, request);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=excel.xls");
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

}