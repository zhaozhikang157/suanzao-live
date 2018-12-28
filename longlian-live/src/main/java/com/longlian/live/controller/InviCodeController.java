package com.longlian.live.controller;

import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.InviCodeDto;
import com.longlian.dto.InviCodeItemDto;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.InviCodeItemService;
import com.longlian.live.service.InviCodeService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.model.InviCode;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by admin on 2017/8/29.
 *
 * 邀请码--处理类
 */
@RequestMapping("/inviCode")
@Controller
public class InviCodeController {
    private static Logger log = LoggerFactory.getLogger(InviCodeController.class);

    @Autowired
    InviCodeService inviCodeService;
    @Autowired
    CourseService courseService;
    @Autowired
    InviCodeItemService itemService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    /**
     * 邀请码
     * @return
     */
    @RequestMapping("index.user")
    @ApiOperation(value = "邀请码页面", httpMethod = "GET", notes = "邀请码页面")
    public String jumpInviCode(){
        return "/func/pc/inviCode";
    }

    /**
     * 生成邀请码
     * @return
     */
    @RequestMapping("/insertCode")
    @ApiOperation(value = "生成邀请码", httpMethod = "GET", notes = "生成邀请码")
    public String jumpInsertInviCode(){
        return "/func/pc/insertCode";
    }

    /**
     * 点击批次
     * @return
     */
    @RequestMapping("/jumpItem")
    @ApiOperation(value = "邀请码批次", httpMethod = "GET", notes = "邀请码批次")
    public ModelAndView jumpItem(@ApiParam(required = true , name = "邀请码ID" , value = "邀请码ID")Long id){
        ModelAndView view = new ModelAndView("/func/pc/inviCodeItem");
        InviCodeDto inviCodeDto = inviCodeService.findInviCodeInfo(id);
        view.addObject("inviCodeDto", inviCodeDto);
        view.addObject("id", id);
        return view;
    }


    /**
     * 获取所有的邀请码
     * @param requestModel
     * @param request
     * @return
     */
    @RequestMapping("/getAllInviCodePage.user")
    @ResponseBody
    @ApiOperation(value = "获取所有的邀请码", httpMethod = "GET", notes = "获取所有的邀请码")
    public DatagridResponseModel getAllInviCodePage(DataGridPage requestModel ,
                                                    HttpServletRequest request , InviCodeDto inviCode){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DatagridResponseModel responseModel = new DatagridResponseModel();
        requestModel.setPageSize(requestModel.getLimit());
        List<InviCodeDto> list = inviCodeService.getAllInviCodePage(requestModel, token.getId(), inviCode);
        responseModel.setRows(list);
        responseModel.setTotal(requestModel.getTotalCount());
        return responseModel;
    }

    /**
     * 获取单节课
     * @param request
     * @return
     */
    @RequestMapping("/getAllSingleClass.user")
    @ResponseBody
    @ApiOperation(value = "获取单节课", httpMethod = "GET", notes = "获取单节课")
    public ActResultDto getAllSingleClass(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> singeCourse = courseService.getAllSingleClass(token.getId());
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(singeCourse);
        return resultDto;
    }

    /**
     * 获取系列课
     * @param request
     * @return
     */
    @RequestMapping("/getAllseriesClass.user")
    @ResponseBody
    @ApiOperation(value = "获取系列课", httpMethod = "GET", notes = "获取系列课")
    public ActResultDto getAllseriesClass(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> seriesCourse = courseService.getAllseriesClass(token.getId());
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(seriesCourse);
        return resultDto;
    }

    /**
     * 插入邀请码
     * @param request
     * @param inviCode
     * @return
     */
    @RequestMapping("/insertInviCode.user")
    @ResponseBody
    @ApiOperation(value = "插入邀请码", httpMethod = "POST", notes = "插入邀请码")
    public ActResult insertInviCode(HttpServletRequest request , @RequestBody InviCode inviCode){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        inviCode.setAppId(token.getId());
        String timeStr = new Date().getTime() + inviCode.getCourseId()+"";
        inviCode.setCodeUnion(timeStr);
        inviCodeService.insertInviCode(inviCode);
        return ActResult.success();
    }

    /**
     * 根据批次获取邀请码的使用详情
     * @param id
     * @return
     */
    @RequestMapping("/getInviCodeItem.user")
    @ResponseBody
    @ApiOperation(value = "根据批次获取邀请码的使用详情", httpMethod = "GET", notes = "根据批次获取邀请码的使用详情")
    public DatagridResponseModel getInviCodeItem(@ApiParam(required = true,name = "批次ID",value = "批次ID")long id , DataGridPage requestModel){
        DatagridResponseModel responseModel = new DatagridResponseModel();
        requestModel.setPageSize(requestModel.getLimit());
        List<InviCodeItemDto> list = itemService.getInviCodeItemPage(requestModel, id);
        responseModel.setRows(list);
        responseModel.setTotal(requestModel.getTotalCount());
        return responseModel;
    }

    /**
     * 导出邀请码详情列表
     *
     * @return
     */
    @RequestMapping(value = "/importExcelInviCodeItem", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "导出邀请码详情列表", httpMethod = "GET", notes = "导出邀请码详情列表")
    public void importExcelInviCodeItem(HttpServletRequest request, HttpServletResponse response ,@ApiParam(required = true,name = "批次ID",value = "批次ID")long id) throws IOException {
        ExportExcelWhaUtil exportExcelWhaUtil = itemService.importExcelInviCodeItem(request, response, id);
        String name = id+itemService.findCourseName(id)+"邀请码使用情况";
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(name.getBytes("gbk"), "iso8859-1") + ".xls");
            byte b[] = FileUtils.readFileToByteArray(new File(path));
            sos.write(b);
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
