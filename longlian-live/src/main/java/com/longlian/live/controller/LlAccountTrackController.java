package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.LlAccountTrackService;
import com.longlian.model.Course;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/29.
 */
@RequestMapping("/llAccountTrack")
@Controller
public class LlAccountTrackController {
    private static Logger log = LoggerFactory.getLogger(LlAccountTrackController.class);

    @Autowired
    LlAccountTrackService llAccountTrackService;

    @Autowired
    CourseService courseService;


    /**
     * 我的钱包收支明细 --- llaccount 新版 学币
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getWalletsPage.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "我的钱包收支明细 --- llaccount 新版 学币", httpMethod = "POST", notes = "我的钱包收支明细 --- llaccount 新版 学币")
    public ActResultDto getWalletsPageNew(HttpServletRequest request,Integer returnMoneyLevel,Integer pageSize , Integer offset) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> list = llAccountTrackService.getWalletsPage(token.getId(),returnMoneyLevel, pageSize, offset);
        if(list.size()>0){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setData(list);
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return result;
    }

    /**
     * 我的课程收益(详情:谁正常购买,谁通过分销购买)
     * @param request
     * @param courseId 课程ID
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping(value = "/findMyCourseProfit.user")
    @ResponseBody
    @ApiOperation(value = "我的课程收益(详情:谁正常购买,谁通过分销购买)", httpMethod = "GET", notes = "我的课程收益(详情:谁正常购买,谁通过分销购买)")
    public ActResultDto findMyCourseProfit(HttpServletRequest request ,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                           Integer pageSize , Integer offset){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountTrackService.findMyCourseProfit(courseId,pageSize,offset,token.getId());
    }

    @RequestMapping(value = "/income/relay/relaier/list.user")
    @ResponseBody
    @ApiOperation(value = "转播课收益-转播人列表", httpMethod = "GET", notes = "转播课收益详情统计信息(顶部的统计信息以及下方的列表信息)")
    public ActResultDto relayCourseIncomeRelaierList(HttpServletRequest request ,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                           Integer pageSize , Integer offset){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Long appId = token.getId();

        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(appId == null || appId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }

        List<Map> list =llAccountTrackService.relayCourseIncomeDetail(courseId, pageSize, offset, appId);
        if(list == null|| list.size() < 1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }

        //获取课程信息
        Course course = this.courseService.getCourse(courseId);


        HashMap<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("course", course);
        data.put("TOTAL_RELAY", "0");
        data.put("TOTAL_PROFIT", "0");

        //获取课程的转播收益和转播分成
        List<Map> stat = llAccountTrackService.relayCourseIncomeStat(courseId, appId, false);
        if(stat != null && stat.size() > 0){
            int relayerCnt = 0;
            for (Map m : stat){
                if(m.get("RELAYER_CNT") != null){
                    relayerCnt += Integer.valueOf(m.get("RELAYER_CNT").toString());
                }
                if(m.get("type").equals("1")){
                    //转播
                    data.put("TOTAL_RELAY", m.get("CHARGE"));
                }else if(m.get("type").equals("2")){
                    //分成
                    data.put("TOTAL_PROFIT", m.get("CHARGE"));
                }
            }
            data.put("relayerCnt", relayerCnt);
        }

        resultDto.setData(data);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    @RequestMapping(value = "/income/today/relay/relaier/list.user")
    @ResponseBody
    @ApiOperation(value = "今日转播课收益-转播人列表", httpMethod = "GET", notes = "今日转播课收益详情统计信息(顶部的统计信息以及下方的列表信息)")
    public ActResultDto relayCourseTodayIncomeRelaierList(HttpServletRequest request ,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                                Integer pageSize , Integer offset){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Long appId = token.getId();

        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(appId == null || appId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }

        List<Map> list =llAccountTrackService.relayCourseIncomeDetail(courseId, pageSize, offset, appId, true);
        if(list == null|| list.size() < 1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }

        //获取课程信息
        Course course = this.courseService.getCourse(courseId);


        HashMap<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("course", course);
        data.put("TOTAL_RELAY", "0");
        data.put("TOTAL_PROFIT", "0");

        //获取课程的转播收益和转播分成
        List<Map> stat = llAccountTrackService.relayCourseIncomeStat(courseId, appId, true);
        if(stat != null && stat.size() > 0){
            int relayerCnt = 0;
            for (Map m : stat){
                if(m.get("RELAYER_CNT") != null){
                    relayerCnt += Integer.valueOf(m.get("RELAYER_CNT").toString());
                }
                if(m.get("type").equals("1")){
                    //转播
                    data.put("TOTAL_RELAY", m.get("CHARGE"));
                }else if(m.get("type").equals("2")){
                    //分成
                    data.put("TOTAL_PROFIT", m.get("CHARGE"));
                }
            }
            data.put("relayerCnt", relayerCnt);
        }

        resultDto.setData(data);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }
}
