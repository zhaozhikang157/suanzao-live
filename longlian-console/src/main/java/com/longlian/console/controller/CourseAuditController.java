package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.CourseService;
import com.longlian.dto.CourseDto;
import com.longlian.dto.OrdersDto;
import com.longlian.live.service.CourseAuditService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.util.log.Log;
import com.longlian.model.CourseAudit;
import com.longlian.model.Orders;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogType;
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
import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/9/5.
 */
@Controller
@RequestMapping("/courseAudit")
public class CourseAuditController {
    private static Logger log = LoggerFactory.getLogger(CourseAuditController.class);
    @Autowired
    CourseAuditService courseAuditService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseBaseService courseBaseService;

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/courseAudit/courseAuditList");
        return view;
    }
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        map.put("auditStatus","-1");
        map.put("isGarbage","-1");
        model.setRows(courseBaseService.getAuditListPage(requestModel,map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    /**
     * 跳转至录播审核页面
     *
     * @return
     */
    @RequestMapping("/toAudit")
    public ModelAndView toAudit(String id) {
        ModelAndView model = new ModelAndView("/func/courseAudit/audit");
        model.addObject("id", id);
        return model;
    }

    @RequestMapping(value = "/selectAuditCourseById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult selectAuditCourseById(long id) {
        ActResult actResult = new ActResult();
        actResult.setData(courseService.getCourse(id));
        return actResult;
    }

    /***
     * 审核（更新审核状态）
     *
     * @param id
     * @param auditStatus 审核状态  1-审核通过 -1审核不通过
     * @param
     * @return
     */
    @RequestMapping(value = "/updateAuditStatus", method = RequestMethod.POST)
    @ResponseBody
    public ActResult updateAuditStatus(HttpServletRequest request, long id, String auditStatus, String remark,String isGarbage) {
        ActResult actResult = new ActResult();
        try {
            if (id > 0) {
                ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
                CourseAudit courseAudit = courseAuditService.selectByPrimaryKey(id);
                courseAudit.setAuditTime(new Date());
                courseAudit.setStatus(auditStatus);
                courseAudit.setAuditUserId(token.getId());
                courseAudit.setAuditUserName(token.getName());
                courseAudit.setRemark(remark);
                courseAudit.setIsGarbage(isGarbage);
                courseAuditService.updateAudit(courseAudit);
                actResult.setSuccess(true);
                return actResult;
            } else {
                actResult.setSuccess(false);
                actResult.setMsg("审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("审核失败");
        }
        return actResult;
    }
    /**
     * 获取审核记录
     * @param
     * @param 
     * @return
     */
    @RequestMapping(value = "/getCheckRecordList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCheckRecordList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(courseBaseService.getAuditListPage(requestModel,map));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 获取审核记录 --录播审核未通过列表
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/getCheckRecordListNoPass", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCheckRecordListNoPass(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(courseBaseService.getAuditListNoPassPage(requestModel,map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    @RequestMapping("/toCheckRecord")
    public ModelAndView toCheckRecord() {
        return new ModelAndView("/func/courseAudit/toCheckRecord");
    }
    @RequestMapping("/toCheckRecordByAuditStatusPass")
    public ModelAndView toCheckRecordByAuditStatusPass() {
        ModelAndView model = new ModelAndView("/func/courseAudit/checkRecordByPass");
        return model;
    }
    @RequestMapping("/toCheckRecordByAuditStatusNoPass")
    public ModelAndView toCheckRecordByAuditStatusNoPass() {
        ModelAndView model = new ModelAndView("/func/courseAudit/checkRecordByNoPass");
        return model;
    }

}
