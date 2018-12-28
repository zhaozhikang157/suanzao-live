package com.longlian.live.controller;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.service.UserRewardRecordService;
import com.longlian.live.service.UserRewardTypeService;
import com.longlian.model.LlAccount;
import com.longlian.model.UserRewardType;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/22.
 */
@Controller
@RequestMapping(value = "/userRewardRecord")
public class UserRewardController {

    @Autowired
    UserRewardRecordService userRewardRecordService;

    @Autowired
    LiveRoomService liveRoomService;
    /**
     * 获取有效的打赏类型列表
     * @return
     */
    @RequestMapping(value = "/getUserRewardSort")
    @ResponseBody
    @ApiOperation(value = "获取有效的打赏类型列表", httpMethod = "GET", notes = "获取有效的打赏类型列表")
    public ActResultDto getUseList(HttpServletRequest request ,
                                   @ApiParam(required =true, name = "偏移量", value = "偏移量") Integer offset ,
                                   @ApiParam(required =true, name = "课程ID", value = "课程ID")  Long courseId){
        DataGridPage dataGridPage =  new DataGridPage();
        if(offset == null)offset = 0;
        dataGridPage.setOffset(offset);
        ActResultDto actResultDto = new ActResultDto();
        if(courseId == null || courseId == 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return  actResultDto;
        }
        List list =  userRewardRecordService.selectUserRewardSort(dataGridPage, courseId);
        if(list!=null && list.size()>0){
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setData(list);
        }else {
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return  actResultDto;
    }

    /**
     * 打赏明细 -  课程分组
     * @param request
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping(value = "/findRewInfoPage.user")
    @ResponseBody
    @ApiOperation(value = "打赏明细 -  课程分组", httpMethod = "GET", notes = "打赏明细 -  课程分组")
    public ActResultDto findRewInfoPage(HttpServletRequest request ,
                                        @ApiParam(required =true, name = "每页数量", value = "每页数量")Integer pageSize ,
                                        @ApiParam(required =true, name = "偏移量", value = "偏移量") Integer offset){
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DataGridPage dataGridPage =  new DataGridPage();
        if(offset == null)offset = 0;
        if(pageSize == null)pageSize = 10;
        dataGridPage.setOffset(offset);
        dataGridPage.setPageSize(pageSize);
        List<Map> userReward = userRewardRecordService.findRewInfoPage(token.getId(),dataGridPage);
        if(userReward!=null && userReward.size()>0){
            actResultDto.setData(userReward);
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return actResultDto;
        }
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return actResultDto;
    }

    /**
     * 打赏明细(Today) -  课程分组
     * @param request
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping(value = "/findTodayRewInfoPage.user")
    @ResponseBody
    @ApiOperation(value = "打赏明细(Today) -  课程分组", httpMethod = "GET", notes = "打赏明细 -  课程分组")
    public ActResultDto findTodayRewInfoPage(HttpServletRequest request ,
                                        @ApiParam(required =true, name = "每页数量", value = "每页数量")Integer pageSize ,
                                        @ApiParam(required =true, name = "偏移量", value = "偏移量") Integer offset){
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DataGridPage dataGridPage =  new DataGridPage();
        if(offset == null)offset = 0;
        if(pageSize == null)pageSize = 10;
        dataGridPage.setOffset(offset);
        dataGridPage.setPageSize(pageSize);
        List<Map> userReward = userRewardRecordService.findTodayRewInfoPage(token.getId(), dataGridPage);
        actResultDto.setData(userReward);
        if(userReward!=null && userReward.size()>0){
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return actResultDto;
        }
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return actResultDto;
    }

    @RequestMapping(value = "/income/today/relay/course/list.user")
    @ResponseBody
    @ApiOperation(value = "今日收益-转播课列表", httpMethod = "GET", notes = "今日收益-转播收益列表")
    public ActResultDto relayCourseIncomeTodayCourseList(HttpServletRequest request ,
                                             @ApiParam(required =true, name = "每页数量", value = "每页数量")Integer pageSize ,
                                             @ApiParam(required =true, name = "偏移量", value = "偏移量") Integer offset){
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DataGridPage dataGridPage =  new DataGridPage();
        if(offset == null)offset = 0;
        if(pageSize == null)pageSize = 10;
        dataGridPage.setOffset(offset);
        dataGridPage.setPageSize(pageSize);
        List<Map> userReward = userRewardRecordService.findTodayRelayDetail(token.getId(), dataGridPage);
        actResultDto.setData(userReward);
        if(userReward!=null && userReward.size()>0){
//            actResultDto.setData(userReward);
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return actResultDto;
        }
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return actResultDto;
    }

    /**
     * 打赏明细 -  单个课程
     * @param request
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping(value = "/findRewInfoPageByCourseId.user")
    @ResponseBody
    @ApiOperation(value = "打赏明细 -  单个课程", httpMethod = "GET", notes = "打赏明细 -  单个课程")
    public ActResultDto findRewInfoPageByCourseId(HttpServletRequest request ,
                                                  @ApiParam(required =true, name = "课程ID", value = "课程ID")Long courseId ,
                                                  @ApiParam(required =true, name = "每页数量", value = "每页数量")Integer pageSize ,
                                                  @ApiParam(required =true, name = "偏移量", value = "偏移量") Integer offset){
        ActResultDto actResultDto = new ActResultDto();
        if(courseId == null || courseId < 1){
            actResultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return actResultDto;
        }
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DataGridPage dataGridPage =  new DataGridPage();
        if(offset == null)offset = 0;
        if(pageSize == null)pageSize = 10;
        dataGridPage.setOffset(offset);
        dataGridPage.setPageSize(pageSize);
        List<Map> userReward = userRewardRecordService.findRewInfoPageByCourseIdPage(token.getId(), dataGridPage, courseId);
        if(userReward!=null && userReward.size()>0){
            actResultDto.setData(userReward);
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return actResultDto;
        }
        actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        return actResultDto;
    }


    @RequestMapping(value = "/getTodayIncomeNew.user")
    @ResponseBody
    @ApiOperation(value = "今日收益总额", httpMethod = "GET", notes = "今日收益总额")
    public ActResultDto getTodayIncomeNew(HttpServletRequest request){
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(token != null){
            Map map = new HashMap<>();
            BigDecimal rewardIncome = userRewardRecordService.getTodayRewardIncome(token.getId());
            if(rewardIncome == null || rewardIncome.compareTo(new BigDecimal(0)) <= 0){
                rewardIncome = new BigDecimal(0);
            }
            BigDecimal disIncome = liveRoomService.getTodayDisIncome(token.getId());
            if(disIncome == null || disIncome.compareTo(new BigDecimal(0))<=0){
                disIncome = new BigDecimal(0);
            }
            BigDecimal courseIncome = liveRoomService.getTodayCourseIncome(token.getId());
            if(courseIncome == null || courseIncome.compareTo(new BigDecimal(0)) <= 0){
                courseIncome = new BigDecimal(0);
            }

            //今日转播收益
            BigDecimal relayIncome = liveRoomService.getTodayRelayIncome(token.getId());
            if(relayIncome == null || relayIncome.compareTo(new BigDecimal(0)) <= 0){
                relayIncome = new BigDecimal(0);
            }

            BigDecimal totalIncome = rewardIncome.add(disIncome).add(courseIncome).add(relayIncome);
            map.put("rewardIncome",rewardIncome!=null?rewardIncome:0);
            map.put("disIncome",disIncome!=null?disIncome:0);
            map.put("courseIncome",courseIncome!=null?courseIncome:0);
            map.put("relayIncome",relayIncome);
            map.put("totalIncome",totalIncome!=null?totalIncome:0);
            actResultDto.setData(map);
        }
        return actResultDto;
    }

    /**
     * 获取 用户 单日收益，课程，打赏，分销 总收益
     */
    @RequestMapping(value = "/getTodayIncome.user")
    @ResponseBody
    @ApiOperation(value = "今日收益总额", httpMethod = "GET", notes = "今日收益总额")
    public ActResultDto getTodayIncome(HttpServletRequest request){
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(token != null){
            Map map = new HashMap<>();
            BigDecimal rewardIncome = userRewardRecordService.getTodayRewardIncome(token.getId());
            if(rewardIncome == null || rewardIncome.compareTo(new BigDecimal(0)) <= 0){
                rewardIncome = new BigDecimal(0);
            }
            BigDecimal disIncome = liveRoomService.getTodayDisIncome(token.getId());
            if(disIncome == null || disIncome.compareTo(new BigDecimal(0))<=0){
                disIncome = new BigDecimal(0);
            }
            BigDecimal courseIncome = liveRoomService.getTodayCourseIncome(token.getId());
            if(courseIncome == null || courseIncome.compareTo(new BigDecimal(0)) <= 0){
                courseIncome = new BigDecimal(0);
            }

            BigDecimal totalIncome = rewardIncome.add(disIncome).add(courseIncome);
            map.put("rewardIncome",rewardIncome!=null?rewardIncome:0);
            map.put("disIncome",disIncome!=null?disIncome:0);
            map.put("courseIncome",courseIncome!=null?courseIncome:0);
            map.put("totalIncome",totalIncome!=null?totalIncome:0);
            actResultDto.setData(map);
        }
        return actResultDto;
    }
}
