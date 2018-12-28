package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LiveRoomDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.StoreFileUtil;
import com.longlian.live.util.pic.BlurPicUtil;
import com.longlian.model.AppUser;
import com.longlian.model.LiveRoom;
import com.longlian.model.StoreFile;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/10.
 */
@Controller
@RequestMapping(value = "/liveRoom")
public class LiveRoomController {
    private static Logger log = LoggerFactory.getLogger(LiveRoomController.class);

    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    CourseService courseService;
    @Autowired
    private StoreFileUtil storeFileUtilLonglian;
    @Autowired
    UserFollowService userFollowService;
    /**
     * 创建直播间
     *
     * @param request map
     * @return
     */
    @RequestMapping(value = "/apply.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建直播间", httpMethod = "POST", notes = "创建直播间")
    public ActResultDto apply(HttpServletRequest request, @RequestParam Map map) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return liveRoomService.updateApply(token.getId(), map);
    }

    /**
     * 取得直播间信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLiveRoomInfo.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "取得直播间信息", httpMethod = "GET", notes = "取得直播间信息")
    public ActResultDto getLiveRoomInfo(HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return liveRoomService.getLiveRoomInfo(token.getId());
    }

    /**
     * 获取邀请卡模板编号
     *
     * @param request
     * @return
     */
    @RequestMapping("/getUserTempCode.user")
    @ResponseBody
    @ApiOperation(value = "获取邀请卡模板编号", httpMethod = "GET", notes = "获取邀请卡模板编号")
    public ActResultDto getUserTempCode(HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return liveRoomService.getUserTempCode(token.getId());
    }

    /**
     * 设置直播间
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/setLiveRoom.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "设置直播间", httpMethod = "POST", notes = "设置直播间")
    public ActResultDto setLiveRoom(HttpServletRequest request, LiveRoomDto liveRoom) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if(liveRoom.getId()==0){
                    ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                    ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                    return ac;
            }
           if(!org.apache.commons.lang.StringUtils.isEmpty(liveRoom.getUserAvatar()))
            {
               appUserService.updatePhone(token.getId(),liveRoom.getUserAvatar());
                updateBlurPhotoUrl(liveRoom.getUserAvatar(), token.getId());
            }
            LiveRoom lr = liveRoomService.findById(liveRoom.getId());
            if (lr ==null || token.getId() != lr.getAppId()) {//判断直播间是否是当前用户的
                ac.setCode(ReturnMessageType.LIVEROOM_NOT_MODIFY.getCode());
                ac.setMessage(ReturnMessageType.LIVEROOM_NOT_MODIFY.getMessage());
            } else {
                liveRoomService.setLiveRoom(liveRoom);
            }
        } catch (Exception e) {
            log.error("聊天室修改失败", e);
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }
    public String updateBlurPhotoUrl(String headPhoto,Long id){
        String blurUrl = "";
        try {
            URL url = new URL(headPhoto);
            URLConnection urlConn = url.openConnection();
            InputStream input = urlConn.getInputStream();
            BufferedImage blurredImage = ImageIO.read(input);
            //保存处理后的图
            ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
            //模糊处理图片
            blurredImage = BlurPicUtil.blur(blurredImage, 200);
            ImageIO.write(blurredImage, "jpg", bos);
            try {
                String fileName = "";
                StoreFile storeFile = null;
                blurredImage = Thumbnails.of(blurredImage).scale(0.5f).allowOverwrite(true).asBufferedImage();
                //阿里云文件
                if (headPhoto.indexOf("longlian-live") != -1) {
                    fileName = headPhoto.substring(headPhoto.lastIndexOf("/") + 1);//截取文件名
                    String prevName = fileName.substring(0, fileName.lastIndexOf("."));
                    String ext = StoreFileUtil.getExtensionName(fileName);
                    fileName = prevName + "_blur." + ext;
                    byte[] bytes = imageToBytes(blurredImage, ext);
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bytes), 0L, null);
                } else if (headPhoto.indexOf("thirdwx.qlogo.cn") != -1 || headPhoto.indexOf("wx.qlogo.cn") != -1) {//没有阿里云存储头像文件
                    fileName = id + "_blur.jpg";
                    byte[] bytes = imageToBytes(blurredImage, "jpg");
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bytes), 0L, null);
                }
                storeFile.setCreateTime(new Date());
                blurUrl = storeFileUtilLonglian.saveFile(bos.toByteArray(), storeFile, "1");
                appUserService.updateUserBlurPhoto(id, blurUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("高斯模糊图片处理异常:", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blurUrl;
    }
    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
    /**
     * 创建直播间时数据回显
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getLiveRoomById.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "创建直播间时数据回显", httpMethod = "GET", notes = "创建直播间时数据回显")
    public ActResultDto getLiveRoomById(HttpServletRequest request,
    @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long liveRoomId) {
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (liveRoomId == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            ac.setData(liveRoomService.getLiveRoomById(token.getId(), liveRoomId));
        }
        return ac;
    }

    /**
     * 直播间,单节课收益详情
     **/
    @RequestMapping(value = "/courseIncomeDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间,单节课收益详情", httpMethod = "GET", notes = "直播间,单节课收益详情")
    public ActResultDto courseIncomeDetails(Integer offset, Integer pageSize, HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null) pageSize = 10;
        if (offset == null) offset = 0;
        List<Map> list = liveRoomService.getCourseIncomeDetailsPage(offset, pageSize, token.getId());
        resultDto.setData(list);
        if (list != null && list.size() > 0) {
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    @RequestMapping(value = "/income/relay/course/list.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "转播收益-转播课程列表", httpMethod = "GET", notes = "我的收益-转播收益-所有转播课程的收益")
    public ActResultDto relayCourseIncomeCourseList(Integer offset, Integer pageSize, HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null) pageSize = 10;
        if (offset == null) offset = 0;

        Map<String, Object> data = new HashMap<>();
        List<Map> list = liveRoomService.getRelayCourseIncomeDetailsPage(offset, pageSize, token.getId());
        data.put("list", list);

        BigDecimal total = new BigDecimal(0);
        if (list != null && list.size() > 0) {
            //转播收益总值
            total = liveRoomService.getRelayCourseIncomeTotal(token.getId());
            data.put("total", total);
            resultDto.setData(data);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            data.put("total", total);
            resultDto.setData(data);
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 直播间,单节课收益详情(Today)
     **/
    @RequestMapping(value = "/courseIncomeTodayDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间,单节课收益详情", httpMethod = "GET", notes = "直播间,单节课收益详情")
    public ActResultDto courseIncomeTodayDetailsPage(Integer offset, Integer pageSize, HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null) pageSize = 10;
        if (offset == null) offset = 0;
        List<Map> list = liveRoomService.getCourseIncomeTodayDetailsPage(offset, pageSize, token.getId());
        resultDto.setData(list);
        if (list != null && list.size() > 0) {
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 分销,单节课收益
     **/
    @RequestMapping(value = "/disIncomeDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分销,单节课收益", httpMethod = "GET", notes = "分销,单节课收益")
    public ActResultDto disIncomeDetailsPage(Integer offset, Integer pageSize, HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null) pageSize = 10;
        if (offset == null) offset = 0;
        List<Map> list = liveRoomService.getdisIncomeDetailsPage(offset, pageSize, token.getId());
        resultDto.setData(list);
        if (list != null && list.size() > 0) {
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 分销,单节课收益(Today)
     **/
    @RequestMapping(value = "/disIncomeTodayDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分销,单节课收益", httpMethod = "GET", notes = "分销,单节课收益")
    public ActResultDto disIncomeTodayDetailsPage(Integer offset, Integer pageSize, HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null) pageSize = 10;
        if (offset == null) offset = 0;
        List<Map> list = liveRoomService.getdisIncomeTodayDetailsPage(offset, pageSize, token.getId());
        resultDto.setData(list);
        if (list != null && list.size() > 0) {
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    @RequestMapping(value = "/getQrcode.user")
    @ResponseBody
    @ApiOperation(value = "获取二维码", httpMethod = "GET", notes = "获取二维码")
    public ActResultDto getQrcode( @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId,
                                  HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if (roomId == null) {
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            return resultDto;
        }
        if (roomId == 0) {
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            return resultDto;
        }
        String address = liveRoomService.getQrAddress(roomId, token.getId());
        if (StringUtils.isEmpty(address)) {
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            return resultDto;
        }
        resultDto.setData(address);
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return resultDto;
    }

    /**
     * app调用我的直播间接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getLiveRoomInfoByApp")
    @ResponseBody
    public ActResultDto getLiveRoomInfoByApp(Long id,String token) {
        ActResultDto ac = new ActResultDto();
        if (id == null || id == 0 || "".equals(id)) {
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return ac;
        }
        Map map = liveRoomService.getLiveRoomInfoByApp(id);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(token)){
            AppUser appUser = SpringMVCIsLoginInterceptor.getUserTokenModelByToken(token);
            if(null != appUser && 0 != appUser.getId()){
                if (userFollowService.isFollowRoom(id,appUser.getId())){
                    map.put("isFollow",1);
                }else {
                    map.put("isFollow",0);
                }
            }
        }
        ac.setData(map);
        return ac;
    }
    /**
     * app调用我的直播间接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getLiveRoomInfoByApp.user")
    @ResponseBody
    @ApiOperation(value = "app调用我的直播间接口", httpMethod = "GET", notes = "app调用我的直播间接口")
    public ActResultDto getLiveRoomInfoByAppUser(@ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long id,String token) {
        return getLiveRoomInfoByApp( id,token) ;
    }

    /**
     * 获取直播间用户信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLiveUserInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取直播间用户信息", httpMethod = "GET", notes = "获取直播间用户信息")
    public ActResultDto userInfo(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            Map map = appUserService.getUserInfo(token.getId());
            result.setData(map);
        }
        return result;
    }
    /**
     * 设置直播间推送消息
     * @param
     * @return
     */
    @RequestMapping(value = "/updateMessageFlag.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "设置直播间推送消息", httpMethod = "POST", notes = "设置直播间推送消息")
    public ActResultDto updateMessageFlag(HttpServletRequest request, LiveRoomDto liveRoom) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if(liveRoom.getId()==0){
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return ac;
            }
          liveRoomService.updateMessageFlag(liveRoom.getMessageFlag(),liveRoom.getId());
        } catch (Exception e) {
            log.error("直播间推送消息设置失败", e);
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }

    /**
     * 获取直播间流量
     * @param roomId
     * @return
     */
    @RequestMapping("/findFlow.user")
    @ResponseBody
    @ApiOperation(value = "获取直播间流量", httpMethod = "GET", notes = "获取直播间流量")
    public ActResultDto findFlowByRoomId(@ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId, HttpServletRequest request){
        ActResultDto resultDto = new ActResultDto();
        LiveRoom liveRoom = null;
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(roomId == null || roomId < 1){
            liveRoom = liveRoomService.findByAppId(token.getId());
            if(liveRoom!=null){
                roomId = liveRoom.getId();
            }
        }
        Boolean boo = wechatOfficialService.isWechatOfficial(roomId);
        if(boo){
            if(liveRoom==null){
                liveRoom = liveRoomService.findById(roomId);
            }
            if(liveRoom == null){
                resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return resultDto;
            }
            long reduceCount = liveRoom.getReduceDataCount();
            if(reduceCount > 0){
                resultDto.setData("0");
            }else{
                resultDto.setData("1");
            }
        }else{
            resultDto.setData("1");
        }
        return resultDto;
    }

    /**
     * 直播间是否禁用
     * @param request
     * @return
     */
    @RequestMapping(value = "/isForbiddenRoom.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间是否禁用", httpMethod = "GET", notes = "直播间是否禁用")
    public ActResultDto isForbiddenRoom(HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
        if (liveRoom != null && "1".equals(liveRoom.getRoomStatus())) {
            resultDto.setCode(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getCode());
            resultDto.setMessage(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getMessage());
        }
        return resultDto;
    }

    @RequestMapping(value = "/findLiveRoomByNamePage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据名称查询直播间", httpMethod = "GET", notes = "根据名称查询直播间")
    public ActResultDto findLiveRoomByNamePage(HttpServletRequest request,
                                               @ApiParam(required = true,name = "直播间名称",value = "直播间名称")String name,
                                               Integer offset, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (offset == null) {
            offset = 0;
        }
        /*if(offset==0){
            pageSize = 3;
        }else if(offset==3){
            pageSize = 7;
        }*/
        return courseService.findLiveRoomByName(name, offset, pageSize);
    }

    /**
     * WHA
     * @param name 搜索使用字段 直播主题
     * @param offset  分页参数
     * @param pageSize  分页参数
     * @param   isSeriesCourse 1-是系列课0-是单节课
     * @return
     */
    @RequestMapping(value = "/findCourseByLiveTopic", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "搜索使用字段 直播主题", httpMethod = "POST", notes = "搜索使用字段 直播主题")
    public ActResultDto findCourseByLiveTopic(HttpServletRequest request  , String name, Integer offset, Integer pageSize,String isSeriesCourse) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (offset == null) {
            offset = 0;
        }
      /*  if(offset==0){
            pageSize = 3;
        }else if(offset==3){
            pageSize = 7;
        }*/
        return courseService.findCourseByLiveTopic(name, offset, pageSize, isSeriesCourse);
    }

    /**
     * 首页 搜索默认
     * @param request
     * @param name
     * @return
     */
    @RequestMapping(value = "/findCourseAndLiveRoomByCondition", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "首页 搜索默认", httpMethod = "POST", notes = "首页 搜索默认")
    public ActResultDto findCourseAndLiveRoomByCondition(HttpServletRequest request  ,@ApiParam(required = true,name = "直播间名称",value = "直播间名称")String name,
            @ApiParam(required = true,name = "类型",value = "类型")String type) {
        return courseService.findCourseAndLiveRoomByCondition(name,type);
    }
}
