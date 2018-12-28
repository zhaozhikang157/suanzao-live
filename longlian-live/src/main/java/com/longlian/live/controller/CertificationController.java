package com.longlian.live.controller;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.AppUserMapper;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.util.XunChengUtil;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/12.
 */
@RequestMapping("/certification")
@Controller
public class CertificationController {
   
    @Autowired
    XunChengUtil xunChengUtil;
    @Autowired
    LiveRoomService liveRoomService;;
    @Autowired
    AppUserService  appUserService;

    /**
     * 实名认证
     * @param request
     * @param 
     * @return
     */
        @RequestMapping("/certification.user")
        @ResponseBody
        @ApiOperation(value = "实名认证", httpMethod = "GET", notes = "实名认证")
        public ActResultDto certification(HttpServletRequest request , @RequestParam Map map) throws Exception{
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if (token==null){
                ac.setCode(ReturnMessageType.NO_LOGIN.getCode());
                ac.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
                return ac;
            }
            String cardNo= map.get("cardNo").toString();
            String realName= map.get("realName").toString();
            String remark= map.get("remark").toString();
           if (StringUtils.isEmpty(cardNo) || StringUtils.isEmpty(realName) || StringUtils.isEmpty(remark)) {
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
               return ac;
            }
            JSONObject returnJson = JSONObject.fromObject(xunChengUtil.identityCertification(cardNo, realName));
            if (returnJson != null) {
                JSONObject result = returnJson.getJSONObject("result");
                if(!result.getString("isok").equals("1"))
                {
                    ac.setCode(ReturnMessageType.BANK_CARD_AUTHENTICATION_FAIL.getCode());
                    ac.setMessage(ReturnMessageType.BANK_CARD_AUTHENTICATION_FAIL.getMessage());
                    return ac;
                }else{
                    //添加认证数据
                    appUserService.updateForAppUserById(token.getId(), cardNo, realName);
                    //创建直播间
                    return liveRoomService.createLiveRoom(token,map);
                }
            }
        return ac;
    }    
}
