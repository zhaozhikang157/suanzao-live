package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.MessageClient;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.AppUserService;
import com.longlian.dto.LiveRoomDto;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.AppUser;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.RoomFunc;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogType;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

/**
 * Created by pangchao on 2017/2/15.
 */
@RequestMapping("/liveRoom")
@Controller
public class LiveRoomController {
    private static Logger log = LoggerFactory.getLogger(LiveRoomController.class);
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    MessageClient messageClient;
    @Autowired
    AppUserService appUserService;
    @Autowired
    DataChargeRecordService recordService;
    @Autowired
    DataUseRecordService dataUseRecordService;
    @Autowired
    FuncService funcService;
    @Autowired
    RoomFuncService roomFuncService;
    @Autowired
    DataChargeRecordService dataChargeRecordService;

    /**
     * 审核反馈页面
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/liveRoom/index");
        return view;
    }


    /**
     * 待审核页面
     *
     * @return
     */
    @RequestMapping("/pendingAuditing")
    public ModelAndView pendingCommentIndex() {
        ModelAndView view = new ModelAndView("/func/liveRoom/pendingAuditing");
        return view;
    }


    /**
     * 获取待处理数据
     *
     * @return
     */
    @RequestMapping(value = "/getPendingList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getPendingList(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(liveRoomService.getPendingListPage(datagridRequestModel, liveRoom));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

   
    /**
     * 已审核
     *
     * @return
     */
    @RequestMapping("/audited")
    public ModelAndView Audited() {
        ModelAndView view = new ModelAndView("/func/liveRoom/audited");
        return view;
    }


    /**
     * 获取已处理数据
     *
     * @return
     */
    @RequestMapping(value = "/getAuditedList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getAuditedList(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(liveRoomService.getAuditedListPage(datagridRequestModel, liveRoom));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 跳转到处理页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/setPending")
    @ResponseBody
    public ModelAndView toShowComment(long id) {
        ModelAndView model = new ModelAndView("/func/liveRoom/hand");
        model.addObject("id", id);
        return model;
    }

    /**
     * 跳转到打印页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/print")
    @ResponseBody
    public ModelAndView print(long id) {
        ModelAndView model = new ModelAndView("/func/liveRoom/print");
        AppUser appUser = appUserService.getAppUserById(id);
        model.addObject("appUser", appUser);
        return model;
    }

    /**
     * 审核处理
     *
     * @param liveRoom request
     * @return
     */
    @RequestMapping(value = "/setHand", method = RequestMethod.POST)
    @ResponseBody
    public ActResult setHand(HttpServletRequest request, @RequestBody LiveRoomDto liveRoom) throws Exception {
        ActResult result = new ActResult();
        String content = "";
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            liveRoom.setAuthUserId(token.getId());
            liveRoom.setAuthUserName(token.getName());
            liveRoomService.updateHand(liveRoom);
            if (liveRoom.getStatus() == 1) {
                content = "恭喜您,您申请直播间通过审核,开始开课吧！【酸枣在线】";
            } else {
                content = "您申请开通直播间因身份证照片不清晰,不能通过,请重新上传!【酸枣在线】";
            }
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("处理失败", e);
            content = "您申请开通直播间因身份证照片不清晰,不能通过,请重新上传!【酸枣在线】";
        }
        messageClient.sendMessage(liveRoom.getMobile(), content);
        return result;

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findById(long id) {
        ActResult result = new ActResult();
        result.setData(liveRoomService.findById(id));
        return result;
    }

    @RequestMapping("/liveRoom_index")
    public ModelAndView liveRoom_index() {
        ModelAndView view = new ModelAndView("/func/liveRoom/liveRoom_index");
        return view;
    }
    @RequestMapping(value = "/getLiveRoomList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getLiveRoomList(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(liveRoomService.getLiveRoomListPage(datagridRequestModel, liveRoom));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 直播间流量充值记录
     * @param roomId
     * @return
     */
    @RequestMapping("/buyFlowInfo")
    public ModelAndView buyFlowInfo(Long roomId){
        ModelAndView view = new ModelAndView("/func/liveRoom/buyFlowInfo");
        view.addObject("roomId", roomId);
        return view;
    }

    /**
     * 直播间流量充值记录
     * @param roomId  TODO
     * @return
     */
    @RequestMapping("/getAllBuyFlowPage")
    @ResponseBody
    public DatagridResponseModel getAllBuyFlowPage(DatagridRequestModel datagridRequestModel,
                                                   long roomId , String beginTime , String endTime , Double amount) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(recordService.getAllBuyFlowPage(datagridRequestModel, roomId, beginTime, endTime, amount));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 直播间流量消耗记录
     * @param roomId
     * @return
     */
    @RequestMapping("/useFlowInfo")
    public ModelAndView useFlowInfo(Long roomId){
        ModelAndView view = new ModelAndView("/func/liveRoom/useFlowInfo");
        view.addObject("roomId", roomId);
        return view;
    }
    
    /**
     * 直播间流量消耗记录
     * @param roomId  TODO
     * @return
     */
    @RequestMapping("/getAllUseFlowPage")
    @ResponseBody
    public DatagridResponseModel getAllUseFlowPage(DatagridRequestModel datagridRequestModel, long roomId,
                                                   String beginTime , String endTime , String courseName) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(dataUseRecordService.getAllUseFlowPage(datagridRequestModel, roomId ,beginTime,endTime,courseName));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 去授权页面
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toAuthorize", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id) throws Exception {
        ModelAndView view = new ModelAndView("/func/liveRoom/toAuthorize");
        view.addObject("id", id);
        return view;
    }

    /**
     * 可用授权功能列表
     * @param datagridRequestModel
     * @param Map
     * @return
     */
    @RequestMapping(value = "/getAuthorizeList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getAuthorizeList(DatagridRequestModel datagridRequestModel,Map Map) {
        DatagridResponseModel model = new DatagridResponseModel();
        Map.put("status","1");
        model.setRows(funcService.getFuncListPage(datagridRequestModel, Map));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 确认授权
     * @param funcCodes
     * @param roomId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sureAuthorize", method = RequestMethod.POST)
    @ResponseBody
    public ActResult sureAuthorize(String funcCodes,Long roomId) throws Exception {
        ActResult result = new ActResult();
        try {
           //if(!Utility.isNullorEmpty(funcCodes) && roomId>0){
               //roomFuncService.deleteRoomFuncByRoomId(roomId);
               String[] codes = funcCodes.split(",");
               Set<String> findRoomFunc = roomFuncService.findRoomFunc(roomId);
               Set<String> addRoomFunc  = new HashSet<>();

               //清空完
               if (!StringUtils.isEmpty(funcCodes)) {
                   for(String funcCode : codes){
                       //移出已经有了的，保持不变，最后余下的，就是要删除的
                       if (findRoomFunc.contains(funcCode)) {
                           findRoomFunc.remove(funcCode);
                       } else {
                           addRoomFunc.add(funcCode);
                       }

                   }
               }

               //增加的
               for (String add : addRoomFunc) {
                   RoomFunc roomFunc  = new RoomFunc();
                   roomFunc.setCreateTime(new Date());
                   roomFunc.setRoomId(roomId);
                   roomFunc.setFuncCode(add);
                   roomFuncService.saveRoomFunc(roomFunc);
               }
                //删除 的
               for (String del : findRoomFunc) {
                   roomFuncService.deleteRoomFunc(roomId , del);
               }

               result.setSuccess(true);
               return result;
           //}
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            log.error("授权失败");
        }
        return result;
    }

    /**
     * 直播间已有的功能
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/getRoomFuncList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getRoomFuncList(long roomId) {
        ActResult result = new ActResult();
        List<RoomFunc> list = roomFuncService.getRoomFuncList(roomId);
        result.setData(list);
        return result;
    }

    @RequestMapping("/toDisableRoom")
    public ModelAndView toDisableRoom(long id) {
        ModelAndView model = new ModelAndView("/func/liveRoom/toDisableRoom");
        model.addObject("id", id);
        return model;
    }

    /**
     * 禁用直播间
     * @param roomId
     * @param roomStatus
     * @param disableRemark
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/disableRoom", method = RequestMethod.POST)
    @ResponseBody
    public ActResult disableRoom(Long roomId,String roomStatus,String disableRemark) throws Exception {
        ActResult result = new ActResult();
        try {
            if(roomId>0){
             liveRoomService.disableRoom(roomId,roomStatus,disableRemark);
                result.setSuccess(true);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            log.error("授权失败");
        }
        return result;
    }
    @RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(HttpServletRequest request,Long recordId) {
        ActResult actResult = new ActResult();
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        try {
            dataChargeRecordService.deleteById(recordId);
            DataChargeRecord dataChargeRecord=dataChargeRecordService.findById(recordId);
            SystemLogUtil.saveSystemLog(LogType.del_flow_record.getType(), "1", token.getId(), token.getName(), String.valueOf(recordId), token.getName() + "删除了平台赠送，失效日期为"+dataChargeRecord.getInvalidDate()+",编号为：" + dataChargeRecord.getId() + "充值流量记录");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actResult;
    }

    /**
     * 直播间流量充值记录
     * @param roomId
     * @return
     */
    @RequestMapping("/ossPage")
    public ModelAndView ossPage(Long roomId){
        ModelAndView view = new ModelAndView("/func/course/liveroomcourse");
        view.addObject("roomId", roomId);
        return view;
    }

    /**
     * 修改自动关闭时间
     */
    @RequestMapping(value = "/updateAutoCloseTime", method = RequestMethod.GET)
    @ResponseBody
    public ActResult updateAutoCloseTime(HttpServletRequest request , long id , int updateValue) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(liveRoomService.setAutoCloseTime(id, updateValue, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = "/updateIsShow", method = RequestMethod.GET)
    @ResponseBody
    public ActResult updateIsShow(long roomId,Integer isShow) {
        return liveRoomService.updateIsShow(roomId,isShow);
    }
}