package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.LiveRoom;
import com.longlian.model.MUser;
import com.longlian.model.WechatOfficialRoom;
import com.longlian.console.service.res.ThirdUserService;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/10/31.
 */
@RequestMapping("/wechatOfficial")
@Controller
public class WechatOfficialController {

    private static Logger log = LoggerFactory.getLogger(WechatOfficialController.class);

    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    DataChargeLevelService dataChargeLevelService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    ThirdUserService thirdUserService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    DataUseService dataUseService;
    @Autowired
    WechatOfficialRoomService wechatOfficialRoomService ;
    
    
    /**
     * 微信公众号管理
     * @return
     */
    @RequestMapping("/wechatIndex")
    public ModelAndView wechatIndex() {
        return new ModelAndView("/func/wechatOfficial/wechatIndex");
    }
    @RequestMapping(value = "/getWechatOfficialRoomListPage", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getWechatOfficialRoomListPage(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        map.put("auditStatus",0);
        model.setRows(wechatOfficialService.getWechatOfficialRoomListPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    
    @RequestMapping("/toCheckRecord")
    public ModelAndView toCheckRecord() {
        return new ModelAndView("/func/wechatOfficial/toCheckRecord");
    }
    @RequestMapping("/toCheckRecordByAuditStatusPass")
    public ModelAndView toCheckRecordByAuditStatusPass() {
        ModelAndView model = new ModelAndView("/func/wechatOfficial/checkRecordByPass");
        return model;
    }
    @RequestMapping("/toCheckRecordByAuditStatusNoPass")
    public ModelAndView toCheckRecordByAuditStatusNoPass() {
        ModelAndView model = new ModelAndView("/func/wechatOfficial/checkRecordByNoPass");
        return model;
    }
    /**
     * 获取审核记录
     * @return
     */
    @RequestMapping(value = "/getCheckRecordList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCheckRecordList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(wechatOfficialService.getWechatOfficialRoomListPage(requestModel, map));
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
        ModelAndView model = new ModelAndView("/func/wechatOfficial/audit");
        model.addObject("id", id);
        return model;
    }
    @RequestMapping(value = "/getManagerList")
    @ResponseBody
    public ActResultDto getSeriesList(HttpServletRequest request) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        List<MUser>  userList= thirdUserService.getMUserByRole(495l);
        actResultDto.setData(userList);
        return  actResultDto;
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
    public ActResult updateAuditStatus(HttpServletRequest request, long id, String auditStatus, String remark,Long managerId,Date freeDate) {
        ActResult actResult = new ActResult();
        try {
            if (id > 0) {
                ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
                WechatOfficialRoom wechatOfficial = wechatOfficialRoomService.findById(id);
                wechatOfficial.setAuditTime(new Date());
                wechatOfficial.setAuditStatus(auditStatus);
                wechatOfficial.setAuditUserId(token.getId());
                wechatOfficial.setAuditUserName(token.getName());
                wechatOfficial.setAuditRemark(remark);
                wechatOfficial.setManager(managerId);
                wechatOfficial.setFreeDate(freeDate);
                wechatOfficialRoomService.updateAudit(wechatOfficial);
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

  
    @RequestMapping("/toDataRecharge")  
    public ModelAndView toDataRecharge(String id,Long liveId) {
        ModelAndView model = new ModelAndView("/func/wechatOfficial/toDataRecharge");
        model.addObject("id", id);
        model.addObject("liveId", liveId);
        return model;
    }

    @RequestMapping(value = "/dataCharge", method = RequestMethod.POST)
    @ResponseBody
    public ActResult dataCharge(HttpServletRequest request,Long totalAmount, Integer invalidDate,Long liveId) {
        ActResult actResult = new ActResult();
        try {
            if (!Utility.isNullorEmpty(liveId) && liveId>0) {
                ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
                DataChargeRecord dataChargeRecord = new DataChargeRecord();
                LiveRoom liveRoom = liveRoomService.findById(liveId);
                dataChargeRecord.setTotalAmount(totalAmount*1024*1024*1024);
                dataChargeRecord.setBalAmount(totalAmount*1024*1024*1024);
                dataChargeRecord.setCanUseTime(new Date());
                dataChargeRecord.setInvalidDate(invalidDate);
                dataChargeRecord.setRoomId(liveId);
                dataChargeRecord.setStatus("1");
                dataChargeRecord.setIsPlatformGift("1");
                dataChargeRecord.setUsedAmount(0l);
                dataChargeRecord.setUseOriginAmount(0l);
                dataChargeRecord.setLevelId(0l);
                dataChargeRecord.setInvalidDateUnit("0");
                dataChargeRecord.setOrderTime(new Date());
                dataChargeRecord.setUserId(liveRoom.getAppId());
                
                Calendar now =Calendar.getInstance();
                now.setTime(new Date());
                now.set(Calendar.DATE, now.get(Calendar.DATE) + invalidDate);
                dataChargeRecord.setInvalidRealDate(now.getTime());
                dataChargeLevelService.dataCharge(dataChargeRecord);
                try {
                    dataUseService.useData(Long.valueOf(liveId),0l);
                } catch (Exception ex) {
                    log.info("消耗流量出错：roomId:{} , 消耗流量：{}" , liveId , 0l);
                    log.error("消耗流量出错： " , ex);
                    MobileGlobalExceptionHandler.sendEmail(ex, "直播间" + liveId + "消耗流量出错");
                }
                SystemLogUtil.saveSystemLog(LogType.recharge_flow.getType(), "1", token.getId(), token.getName(), String.valueOf(liveId), token.getName() + "为直播间：" + liveRoomService.findById(liveId).getName() + "成功充值流量" + totalAmount+"GB");
                actResult.setSuccess(true);
                return actResult;
            } else {
                actResult.setSuccess(false);
                actResult.setMsg("充值失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("充值失败");
        }
        return actResult;
    }

    @RequestMapping("/toUpdateManager")
    public ModelAndView toUpdateManager(Long id) {
        ModelAndView model = new ModelAndView("/func/wechatOfficial/toUpdateManager");
        model.addObject("id", id);
        return model;
    }

    @RequestMapping(value = "/findByIdForEdit", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findByIdForEdit(long id) {
        ActResult result = new ActResult();
        WechatOfficialRoom  wechatOfficialRoom = wechatOfficialRoomService.findById(id);
        result.setData(wechatOfficialRoom);
        return result;
    }
    @RequestMapping(value = "/doSaveAndUpdate" ,method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(HttpServletRequest request ,@RequestBody WechatOfficialRoom  wechatOfficialRoom)throws  Exception{
               ActResult actResult =  new ActResult();
                 try {
                 wechatOfficialRoomService.updateManager(wechatOfficialRoom);
                    actResult.setSuccess(true);
                    return actResult;
            } catch (Exception e) {
                e.printStackTrace();
                actResult.setSuccess(false);
                actResult.setMsg("修改责任人失败");
            }
            return actResult;
     }
    @RequestMapping(value = "/updateMobile" ,method = RequestMethod.POST)
    @ResponseBody
    public ActResult updateMobile(HttpServletRequest request ,Long id,String mobile)throws  Exception{
        ActResult actResult =  new ActResult();
        try {
            if(!Utility.isNullorEmpty(mobile)){
                wechatOfficialRoomService.updateMobile(id,mobile);
                actResult.setSuccess(true);
                return actResult;
            }else{
                actResult.setSuccess(false);
                actResult.setMsg("修改联系方式失败");
                return actResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            actResult.setSuccess(false);
            actResult.setMsg("修改联系方式失败");
        }
        return actResult;
    }


    /**
     * 解绑
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "unBindLiveRoom")
    @ResponseBody
    public ActResult unBindLiveRoom(HttpServletRequest request,Long id) throws Exception {
        WechatOfficialRoom wechatOfficialRoom = wechatOfficialRoomService.findById(id);
        if(!Utility.isNullorEmpty(wechatOfficialRoom)){
            wechatOfficialRoomService.deleteBindRoom(id);
        }
        redisUtil.hdel(RedisKey.ll_live_appid_use_authorizer_room_info, wechatOfficialRoom.getLiveId() + "");
        return ActResult.success();
    }

    @RequestMapping("/toEdit")
    public ModelAndView toEdit(Long id) {
        ModelAndView model = new ModelAndView("/func/wechatOfficial/toEdit");
        model.addObject("id", id);
        return model;
    }
}
