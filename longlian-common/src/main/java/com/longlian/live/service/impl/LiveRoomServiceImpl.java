package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LiveRoomDto;
import com.longlian.live.constant.Const;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.AccountTrackMapper;
import com.longlian.live.dao.LiveRoomMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.XunChengUtil;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.LogType;
import com.longlian.type.MsgType;
import com.longlian.type.ReturnMessageType;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/10.
 */
@Service("liveRoomService")
public class LiveRoomServiceImpl implements LiveRoomService {


    private final BigDecimal k = new BigDecimal(1024l);
    private final BigDecimal  m =  new BigDecimal(1048576l);
    private final BigDecimal g =  new BigDecimal(1073741824l);
    private final BigDecimal t =  new BigDecimal(1099511627776l);
    private static Logger log = LoggerFactory.getLogger(LiveRoomServiceImpl.class);
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    LiveRoomMapper liveRoomMapper;
    @Autowired
    AccountTrackMapper accountTrackMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    CountService countService;
    @Autowired
    WeixinUtil weixinUtil;
    @Value("${website}")
    private String website;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    XunChengUtil xunChengUtil;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;


    @Override
    public LiveRoom findByAppId(long appId) {
        return liveRoomMapper.findByAppId(appId);
    }


    @Override
    public ActResultDto updateApply(long appId, Map map) throws Exception {
        ActResultDto ac = new ActResultDto();
        if (StringUtils.isEmpty(map.get("mobile")) || StringUtils.isEmpty(map.get("realName")) || StringUtils.isEmpty(map.get("cardNo"))) {    //手机号码为空
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return ac;
        }
        //判断手机号是否存在
        AppUser a = appUserCommonService.queryByMobile(map.get("mobile").toString());


        if (a != null && a.getId() != appId) {
            ac.setCode(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getMessage());
            return ac;
        }
        String number = redisUtil.get(RedisKey.ll_live_mobile_register_sms + map.get("mobile"));//从redis获取
        if (Utility.isNullorEmpty(number)) {
            ac.setCode(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getCode());
            ac.setMessage(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getMessage());
            return ac;
        }
        if (!number.equals(map.get("code"))) {
            ac.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return ac;
        }
        JSONObject returnJson = JSONObject.fromObject(xunChengUtil.identityCertification(map.get("cardNo").toString(), map.get("realName").toString()));
        if (returnJson != null) {
            JSONObject result = returnJson.getJSONObject("result");
           if(!result.getString("isok").equals("1"))
           {
               ac.setCode(ReturnMessageType.BANK_CARD_AUTHENTICATION_FAIL.getCode());
               ac.setMessage(ReturnMessageType.BANK_CARD_AUTHENTICATION_FAIL.getMessage());
               return ac;
           }
        }

        map.put("password", MD5PassEncrypt.getMD5Str(map.get("password").toString()));
        AppUser appUser = appUserCommonService.getAppUser(appId);
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setAppId(appId);
        liveRoom.setName(appUser.getName() + "的直播间");
        liveRoom.setStatus(1);
//        liveRoom.setWeixinNum(map.get("weixinNum").toString());
        liveRoom.setRemark(map.get("remark").toString());
        try {
            //修改用户信息
            map.put("teacherCreateTime",new Date());
            appUserCommonService.updateAppUserById(appId, map);
            //当前用户的直播间
            LiveRoom myLiveRoom = liveRoomMapper.findByAppId(appId);
            long liveRoomAppId = 0;
            if (myLiveRoom != null) {
                liveRoomAppId = myLiveRoom.getAppId();
            }
            if (StringUtils.isEmpty(map.get("id")) && liveRoomAppId > 0) {//创建直播间 用户有直播间
                ac.setCode(ReturnMessageType.LIVE_ROOM_HEADED.getCode());
                ac.setMessage(ReturnMessageType.LIVE_ROOM_HEADED.getMessage());
                return ac;
            }
            if (!StringUtils.isEmpty(map.get("id")) && liveRoomAppId != appId) {//修改直播间 用户有直播间
                ac.setCode(ReturnMessageType.LIVE_ROOM_HEADED.getCode());
                ac.setMessage(ReturnMessageType.LIVE_ROOM_HEADED.getMessage());
                return ac;
            }
            if (StringUtils.isEmpty(map.get("id")) && liveRoomAppId == 0) {//创建直播间，用户没有直播间
                liveRoom.setCoverssAddress(website+Const.DEFAULT_LIVETOOM_COVERSSADDRESS);//创建直播间时封面默认封面
                liveRoom.setLiveRoomNo(UUIDGenerator.generate());
                liveRoomMapper.insert(liveRoom);//创建直播间
                sendMsgService.sendMsg(appId, MsgType.LIVE_ROOM_APPLY.getType(), liveRoom.getId()
                        , MsgConst.replace(MsgConst.APPLY_ROOM_REMIND_STUDENT_CONTENT, DateUtil.getCurrDate()), "");
                //新增老师
                countService.newTeacherCount(liveRoom.getAppId());
                //创建好新的直播间之后,向mq发送消息,然后申请一个chatRoomId;
                if(liveRoom.getId() > 0){
                    Map val = new HashMap();
                    val.put("liveRoomId",liveRoom.getId());
                    val.put("appId",liveRoom.getAppId());
                    val.put("isUse","0");
                    val.put("chatRoomId","0");
                    redisUtil.lpush(RedisKey.live_chat_room_create,JsonUtil.toJson(val));
                }
            } else {
                liveRoom.setId(Long.parseLong(map.get("id").toString()));
                liveRoomMapper.updateByLive(liveRoom);//修改直播间
                sendMsgService.sendMsg(appId, MsgType.LIVE_ROOM_APPLY.getType(), liveRoom.getId()
                        , MsgConst.replace(MsgConst.APPLY_ROOM_REMIND_STUDENT_CONTENT_RP, DateUtil.getCurrDate()), "");
            }
            //重新设置token 中mobile
            redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + appId, "mobile", map.get("mobile").toString());
            ac.setData(liveRoom.getId());
        } catch (Exception e) {
            log.error("创建直播间失败！", e);
            ac.setMessage("创建直播间失败！");
            throw e;
        }
        return ac;
    }

    @Override
    public ActResultDto createLiveRoom(AppUserIdentity appUserIdentity,Map map) throws Exception{
        ActResultDto ac = new ActResultDto();
        AppUser appUser = appUserCommonService.getAppUser(appUserIdentity.getId());
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setAppId(appUser.getId());
        liveRoom.setName(appUser.getName() + "的直播间");
        liveRoom.setStatus(1);
//        liveRoom.setWeixinNum(map.get("weixinNum").toString());
        liveRoom.setRemark(map.get("remark").toString());
        try {
            //当前用户的直播间
            LiveRoom myLiveRoom = liveRoomMapper.findByAppId(appUserIdentity.getId());
            long liveRoomAppId = 0;
            if (myLiveRoom != null) {
                liveRoomAppId = myLiveRoom.getAppId();
                //liveRoomMapper.updateByLive(liveRoom);//修改直播间
                sendMsgService.sendMsg(appUser.getId(), MsgType.LIVE_ROOM_APPLY.getType(), liveRoom.getId()
                        , MsgConst.replace(MsgConst.APPLY_ROOM_REMIND_STUDENT_CONTENT_RP, DateUtil.getCurrDate()), "");
            }else{
                liveRoom.setCoverssAddress(website+Const.DEFAULT_LIVETOOM_COVERSSADDRESS);//创建直播间时封面默认封面
                liveRoom.setLiveRoomNo(UUIDGenerator.generate());
                liveRoomMapper.insert(liveRoom);//创建直播间
                sendMsgService.sendMsg(appUser.getId(), MsgType.LIVE_ROOM_APPLY.getType(), liveRoom.getId()
                        , MsgConst.replace(MsgConst.APPLY_ROOM_REMIND_STUDENT_CONTENT, DateUtil.getCurrDate()), "");
                //新增老师
                countService.newTeacherCount(liveRoom.getAppId());
            }
            ac.setData(liveRoom.getId());
        } catch (Exception e) {
            log.error("创建直播间失败！", e);
            ac.setMessage("创建直播间失败！");
        }
        return ac;
    }
    @Override
    public ActResultDto getLiveRoomInfo(long appId) {
        ActResultDto ac = new ActResultDto();
        HashMap map = new HashMap();
        AppUser appUser = appUserCommonService.getAppUser(appId);
        LiveRoom liveRoom = liveRoomMapper.findByAppId(appId);
        map.put("mobile", appUser.getMobile());
        map.put("idCardFornt", appUser.getIdCardFront());
        map.put("idCardRear", appUser.getIdCardRear());

        map.put("weixinNum", liveRoom != null ? liveRoom.getWeixinNum() : "");
        map.put("remark", liveRoom != null ? liveRoom.getRemark() : "");
        map.put("name", liveRoom != null ? liveRoom.getName() : "");
        map.put("coverssAddress", liveRoom != null ? liveRoom.getCoverssAddress() : "");
        map.put("bgAddress", liveRoom != null ? liveRoom.getBgAddress() : "");
        map.put("authStatus", liveRoom != null ? liveRoom.getStatus() : "");
        map.put("authRemark", liveRoom != null ? liveRoom.getAuthRemark() : "");
        ac.setData(map);
        return ac;
    }

    /**
     * 更新直播间邀请卡模板编号
     *
     * @param appId
     * @param tempCode
     */
    @Override
    public void updateLiveRoom(long appId, String tempCode) {
        liveRoomMapper.updateLiveRoom(appId, tempCode);

    }

    /**
     * 获取邀请卡模板编号
     *
     * @param appId
     * @return
     */
    @Override
    public ActResultDto getUserTempCode(long appId) {
        LiveRoom liveRoom = liveRoomMapper.findByAppId(appId);
        ActResultDto resultDto = new ActResultDto();
        try {
            resultDto.setData(liveRoom.getInviteTmp());
        } catch (Exception e) {
            resultDto.setData("");
        }
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return resultDto;
    }

    @Override
    public ActResultDto getFollowRoom(long appId) {
        ActResultDto ac = new ActResultDto();
        List<Map> list = liveRoomMapper.getFollowRoom(appId);
        if (list.size() > 0) {
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            ac.setData(list);
        } else {
            ac.setCode(ReturnMessageType.NO_FOLLOW_ROOM.getCode());
            ac.setMessage(ReturnMessageType.NO_FOLLOW_ROOM.getMessage());
        }
        return ac;
    }

    @Override
    public void setLiveRoom(LiveRoom liveRoom) {
        String roomName = liveRoom.getName();
        roomName = Utility.getCheckNum(roomName);
        liveRoom.setName(roomName);
        liveRoomMapper.setLiveRoom(liveRoom);
    }

    @Override
    public Map getLiveRoomById(Long appId, Long liveRoomId) {
        Map map = new HashMap();
        System.out.println("appId->" + appId + "    liveRoomId-->" + liveRoomId);
        //平台收益
        List<Map> map2 = accountTrackMapper.findAllWallet(appId);
        BigDecimal todayAmount = accountTrackMapper.findTodayAccount(appId);
        BigDecimal livAmount = new BigDecimal(0);
        for (Map map1 : map2) {
            if ("0".equals(String.valueOf(map1.get("level")))) {
                livAmount = livAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
        }
        map.put("account", livAmount);
        map.put("todayAmount",todayAmount);
        LiveRoom liveRoom = liveRoomMapper.findById(liveRoomId);
        map.put("liveRoom", liveRoom);
        AppUser appUser = appUserCommonService.getAppUser(appId);
        map.put("appUser", appUser);
        map.put("followNum", userFollowService.getCountByRoomId(liveRoomId));
        return map;
    }

    @Override
    public LiveRoom findById(long id) {
        return liveRoomMapper.findById(id);
    }

    @Override
    public List<Map> getCourseIncomeDetailsPage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = liveRoomMapper.getCourseIncomeDetailsPage(dg, appId);
//        for (Map map : list) {
//            long courseId = Long.valueOf(String.valueOf(map.get("courseId")));
//            long joinCount = joinCourseRecordService.getCountByCourseId(courseId);
//            map.put("joinCount", joinCount);
//        }
        return list;
    }

    @Override
    public List<Map> getRelayCourseIncomeDetailsPage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = liveRoomMapper.getRelayCourseIncomeDetailsPage(dg, appId);
        return list;
    }

    @Override
    public List<Map> getCourseIncomeTodayDetailsPage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = liveRoomMapper.getCourseIncomeTodayDetailsPage(dg, appId);
        return list;
    }

    @Override
    public BigDecimal getTodayCourseIncome(long appId) {
        BigDecimal bigDecimal = liveRoomMapper.getTodayCourseIncome(appId);
        return bigDecimal;
    }

    @Override
    public BigDecimal getTodayDisIncome(Long appId) {
        BigDecimal bigDecimal = liveRoomMapper.getTodayDisIncome(appId);
        return bigDecimal;
    }

    @Override
    public List<Map> getdisIncomeDetailsPage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = liveRoomMapper.getdisIncomeDetailsPage(dg, appId);
        for (Map map : list) {
            long courseId = Long.valueOf(String.valueOf(map.get("courseId")));
            long joinCount = joinCourseRecordService.getCountByCourseId(courseId);
            map.put("joinCount", joinCount);
        }
        return list;
    }

    @Override
    public List<Map> getdisIncomeTodayDetailsPage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = liveRoomMapper.getdisIncomeTodayDetailsPage(dg, appId);
        for (Map map : list) {
            long courseId = Long.valueOf(String.valueOf(map.get("courseId")));
            long joinCount = joinCourseRecordService.getCountByCourseId(courseId);
            map.put("joinCount", joinCount);
            map.put("creteTime", DateUtil.transFormationStringDate2(map.get("creteTime").toString()));
        }
        return list;
    }

    /**
     * 获取直播间 公众号二维码 特殊处理，有更新方法
     * @param roomId
     * @param appId
     * @return
     */
    @Override
    @DataSource(value = DynamicDataSourceKey.DS_MASTER)
    @Transactional(rollbackFor = Exception.class)
    public String getQrAddress(long roomId, long appId) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(weixinUtil.getParaLiveQrcode( roomId)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String path = appId + roomId + "_qrCode.png";
            ImageIO.write(bufferedImage, "jpg", outputStream);
            byte[] bytes = outputStream.toByteArray();
            String url = ssoUtil.putObject(path, bytes);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Map getLiveRoomInfoByApp(long id) {
        Map map = new HashMap();
        LiveRoom liveRoom = liveRoomMapper.findById(id);
        Map userMap = appUserCommonService.getUserInfo(liveRoom.getAppId());
        map.put("headPhoto",userMap.get("headPhoto"));
        map.put("name", liveRoom.getName());
        map.put("remark", liveRoom.getRemark());
        map.put("coverssAddress", liveRoom.getCoverssAddress());
        map.put("otherAppId", liveRoom.getAppId());
        //获取直播间关注数
        map.put("followNum", userFollowService.getCountByRoomId(id));
        map.put("userName", userMap.get("userName"));
        return map;
    }
    @Override
    public void updateLiveRoomRemark(long appId,String remark) {
        liveRoomMapper.updateLiveRoomRemark(appId, remark);
    }

    /**
     * 根据直播间唯一标示获取直播间ID
     * @param liveRoomNo
     * @return
     */
    @Override
    public Long getLiveRoomId(String liveRoomNo) {
        return liveRoomMapper.getLiveRoomId(liveRoomNo);
    }

    /**
     * 批量处理所有的liveRoomNo
     */
    @Override
    public void updateBatchLiveRoomNo() {
        //查出所有的liveRoom
        List<LiveRoom> list = liveRoomMapper.selectAll();
        //设置
        for (LiveRoom liveRoom : list) {
            String uuid = UUIDGenerator.generate();
            if (Utility.isNullorEmpty(liveRoom.getLiveRoomNo())) {
                liveRoomMapper.updateLiveRoomNo(liveRoom.getId() , uuid);
            }
        }

    }

    @Override
    public String[] getRoomAddress(String userId, String roomId) {
        String key = RedisKey.ll_chatroom_address + roomId;
        String value = redisUtil.get(key);
        if (StringUtils.isEmpty(value) ) {
            String[] result =  yunxinChatRoomUtil.getChatRoomAddress(userId, roomId);
            if (result != null) {
                String str = "";
                for (String r : result) {
                    str += r + ",";
                }
                redisUtil.setex(key , 3 *24 * 60 * 60 , str);
            }
            return result;
        } else {
            return  value.split(",");
        }
    }
    @Override
    public void updateMessageFlag(String messageFlag ,long roomId){
        liveRoomMapper.updateMessageFlag(messageFlag,roomId);
    }

    @Override
    public void updateReduceDataCountById(long roomId, long balance) {
        liveRoomMapper.updateReduceDataCountById(roomId, balance);
    }


    @Override
    public void updateSize(Long id, Long size) {
        liveRoomMapper.updateSize(id, size);
    }

    @Override
    public void updateDaySize(Long id, Long size) {
        liveRoomMapper.updateDaySize(id, size);
    }

    @Override
    public void updateReviewCount(Long id, Long size) {
        liveRoomMapper.updateReviewCount(id, size);
    }


    @Override
    public List<LiveRoomDto> getPendingListPage(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom) {
        return liveRoomMapper.getPendingListPage(datagridRequestModel,liveRoom);
    }

    @Override
    public List<LiveRoomDto> getAuditedListPage(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom) {
        return liveRoomMapper.getAuditedListPage(datagridRequestModel, liveRoom);
    }

    @Override
    public void updateHand(LiveRoomDto liveRoom) throws Exception  {
        int i = liveRoomMapper.updateHand(liveRoom);
        if(i>0 && liveRoom.getStatus()==1){
            appUserCommonService.updateUserType(liveRoom.getAppId());
        }
    }

    @Override
    public LiveRoomDto findDtoById(long id) {
        return liveRoomMapper.findDtoById(id);
    }

    /**
     * 获取所有有效的直播间  做后台轮询 关注奖励
     * @return
     */
    @Override
    public List<Map> getAllUseRoom() {
        return liveRoomMapper.getAllUseRoom();
    }

    @Override
    public List<Map> getCourseCount() {
        return liveRoomMapper.getCourseCount();
    }


    @Override
    public List<LiveRoomDto>  getLiveRoomListPage(DatagridRequestModel datagridRequestModel,LiveRoomDto liveRoom){
        List<LiveRoomDto> list = liveRoomMapper.getLiveRoomListPage(datagridRequestModel, liveRoom);
        if(list.size()>0){
            for(LiveRoomDto lr : list){
                Map reduceMap = this.utinFormat(new BigDecimal(lr.getReduceDataCount()));
                lr.setReduceDataCounts(reduceMap.get("utinAmounts").toString()+reduceMap.get("unit").toString());
                Map reviewMap = this.utinFormat(new BigDecimal(lr.getReviewCount()));
                lr.setReviewCounts(reviewMap.get("utinAmounts").toString()+reviewMap.get("unit").toString());
                Map dataMap = this.utinFormat(new BigDecimal(lr.getDataCount()));
                lr.setDataCounts(dataMap.get("utinAmounts").toString()+dataMap.get("unit").toString());
            }
        }
        return  list;
    }


    public Map  utinFormat(BigDecimal amount){
        Map  map = new HashMap();
        String utinAmounts = "0";
        String unit = "";
        if (amount.compareTo(t) >= 0) {
            utinAmounts ="" +amount.divide(t,2, RoundingMode.HALF_UP);
            unit = "T";
        } else if (amount.compareTo(g) >= 0) {
            utinAmounts ="" +amount.divide(g,2, RoundingMode.HALF_UP);
            unit = "G";
        } else if (amount.compareTo(  m) >= 0)  {
            utinAmounts ="" +amount.divide(m,2, RoundingMode.HALF_UP);
            unit = "M";
        } else if (amount.compareTo(  k) >= 0)  {
            utinAmounts ="" +amount.divide(k,2, RoundingMode.HALF_UP);
            unit = "KB";
        }
        map.put("utinAmounts",utinAmounts);
        map.put("unit",unit);
        return map;
    }

    @Override
    public  void disableRoom(Long roomId,String roomStatus,String disableRemark){
        liveRoomMapper.disableRoom(roomId, roomStatus, disableRemark);
    }

    @Override
    public List<String> findAllRoomId() {
        return liveRoomMapper.findAllRoomId();
    }

    public static void main(String[] args) {
        System.out.println(MD5PassEncrypt.getMD5Str("608608")) ;
    }

    @Override
    public  long setAutoCloseTime(long id, int autoCloseTime , long optId , String optName){
        LiveRoom lr = liveRoomMapper.findById(id);

        Integer old = lr.getAutoCloseTime();
        liveRoomMapper.setAutoCloseTime(id, autoCloseTime);
        SystemLogUtil.saveSystemLog(LogType.console_liveroom_autoclosetime.getType(), "1", optId, optName, String.valueOf(id), "直播间：" + lr.getName() + "的自动关闭时间由原来的：" + old + "修改为：" + autoCloseTime);
        return autoCloseTime;

    }

    @Override
    public ActResult updateIsShow(long id,Integer isShow) {
        ActResult ar  = new ActResult();
        try{
            liveRoomMapper.updateIsShow(id,isShow);
            return ar;
        }catch (Exception e){
            log.error("修改IS_ShOW出错",e);
            ar.setSuccess(false);
        }
        return ar;
    }

    @Override
    public BigDecimal getTodayDisIncome(long appId) {
        BigDecimal bigDecimal = liveRoomMapper.getTodayDisIncome(appId);
        return bigDecimal;
    }

    @Override
    public BigDecimal getRelayCourseIncomeTotal(long id) {
        return this.liveRoomMapper.getRelayCourseIncomeTotal(id, false);
    }

    @Override
    public BigDecimal getRelayCourseIncomeTotal(long id, boolean today) {
        return this.liveRoomMapper.getRelayCourseIncomeTotal(id, true);
    }

    @Override
    public BigDecimal getTodayRelayIncome(long id) {
        return this.liveRoomMapper.getTodayRelayIncome(id);
    }
}
