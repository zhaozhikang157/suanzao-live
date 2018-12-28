package com.longlian.live.controller;

import com.huaxin.util.MessageClient;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.MobileVersionService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.AppUser;
import com.longlian.type.ReturnMessageType;
import feign.Headers;
import feign.RequestLine;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/4.
 */
@Controller
@RequestMapping("/aboutUs")
public class AboutController {
    private static Logger log = LoggerFactory.getLogger(AboutController.class);
    @Autowired
    AppUserService appUserService;
    @Value("${website}")
    private String website;
    @Autowired
    MessageClient messageClient;

    @RequestMapping("/getInfo")
    @ResponseBody
    @ApiOperation(value = "关于我们", httpMethod = "GET", notes = "关于我们")
    public ActResultDto getInfo(){
        ActResultDto resultDto = new ActResultDto();
        String address = website+"/web/res/image/suanzao.png";
        Map map = new HashMap();
        map.put("address",address);
        map.put("appName","酸枣");
        map.put("company","北京龙链科技有限公司");
        resultDto.setData(map);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 发送申请验证码
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/getApplySms.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "发送手机验证码", httpMethod = "GET", notes = "发送手机验证码")
    public ActResultDto getApplySms(String mobile) {
        return appUserService.getApplySms(mobile, RedisKey.ll_live_mobile_register_sms,"1");
    }

    /**
     * 发送
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/sendSms", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "发送消息", httpMethod = "GET", notes = "发送手机验证码")
    public ActResultDto sendSms(String mobile) {
        ActResultDto result = new ActResultDto();
        mobile = mobile.trim();

        String content = "你的直播间由于直播违规视频已经被封禁，课程已被下架，我们会将你的账号交由公安机关处理！【酸枣在线】";
        messageClient.sendMessage(mobile, content);
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return result;
    }

    @Autowired
    ChatMsgRemote chatMsgRemote;

    /**
     * 发送
     *
     * @return
     */
    @RequestMapping(value = "/chatRoomMsg", method = RequestMethod.POST , consumes = "application/json")
    @ResponseBody
    @ApiOperation(value = "发送消息", httpMethod = "POST", notes = "chatRoomMsg")
    public ActResultDto chatRoomMsg(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        ActResultDto result = chatMsgRemote.insert(chatRoomMsgDto);
        return result;
    }

    /**
     * getHistoryMsgByCourseId
     *
     * @return
     */
    @RequestMapping(value = "/chatRoomMsg", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getHistoryMsgByCourseId", httpMethod = "GET", notes = "getHistoryMsgByCourseId")
    public ActResultDto getHistoryMsgByCourseId(Long courseId) throws Exception {
        ActResultDto result = chatMsgRemote.getHistoryMsgByCourseId(courseId);
        return result;
    }


    /**
     * getHistoryMsg
     *
     * @return
     */
    @RequestMapping(value = "/getHistoryMsg", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getHistoryMsg", httpMethod = "GET", notes = "getHistoryMsg")
    public ActResultDto getHistoryMsg(Long courseId , Integer offSet) throws Exception {
        ActResultDto result = chatMsgRemote.getHistoryMsg(courseId , offSet);
        return result;
    }

    /**
     * getHistoryMsgPage
     *
     * @return
     */
    @RequestMapping(value = "/getHistoryMsgPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getHistoryMsgPage", httpMethod = "POST", notes = "getHistoryMsgPage")
    public ActResultDto getHistoryMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        ActResultDto result = chatMsgRemote.getHistoryMsgPage(chatRoomMsgDto);
        return result;
    }

    /**
     * findMaxMsgId
     *
     * @return
     */
    @RequestMapping(value = "/findMaxMsgId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "findMaxMsgId", httpMethod = "GET", notes = "findMaxMsgId")
    public ActResultDto findMaxMsgId() {
        ActResultDto result = chatMsgRemote.findMaxMsgId();
        return result;
    }

    /**
     * 版本控制
     * @param request
     * @return
     */
    @RequestMapping(value = "/isOnline", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "isOnline", httpMethod = "GET", notes = "isOnline")
    public ActResultDto isOnline(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        String s = request.getParameter("v");
        Map<String,String> map = appUserService.getSystemVision();
        String v = map.get("content");
        String data = map.get("data");
        String[] stas = data.split(",");
        if(v.equals(s)){
            result.setData(stas[0]);
        }else{
            result.setData(stas[1]);
        }
        return result;
    }
}
