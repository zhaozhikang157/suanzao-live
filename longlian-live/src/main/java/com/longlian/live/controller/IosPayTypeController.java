package com.longlian.live.controller;

import com.huaxin.util.StringUtil;
import com.huaxin.util.alipay.app.HttpRequest;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.IosPayTypeService;
import com.longlian.model.IosPayType;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/19.
 */
@RequestMapping("/iosPayType")
@Controller
public class IosPayTypeController {
    private static Logger log = LoggerFactory.getLogger(IosPayTypeController.class);

    @Autowired
    IosPayTypeService iosPayTypeService;

    @RequestMapping("/getIosPayInfo.user")
    @ResponseBody
    @ApiOperation(value = "获取支付信息", httpMethod = "GET", notes = "获取支付信息")
    public ActResultDto getIosPay(HttpServletRequest request ,@ApiParam(required = true,name = "支付类型",value = "支付类型")String type,String v,String clientType){
        ActResultDto resultDto = new ActResultDto();
        //如果是版本号 小于等于2.1.0  不让支付
        int version=0;
        log.info("ios支付 版本号："+v);
        if(StringUtils.isNotBlank(v)){
            version=Integer.valueOf(v.replaceAll("\\.", ""));
        }
        if("ios".equals(clientType) && StringUtils.isNotBlank(v) && version<=210){
            resultDto.setMessage(ReturnMessageType.ERROR_PAY_BY_IOS.getMessage());
            resultDto.setCode(ReturnMessageType.ERROR_PAY_BY_IOS.getCode());
            return resultDto;
        }

        if(StringUtils.isEmpty(type)){
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        List<IosPayType> list = iosPayTypeService.getIosPay(type);
        if(list != null && list.size()>0){
            resultDto.setData(list);
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if(StringUtils.isEmpty(token.getMobile())){
                resultDto.setExt("0");
            }else{
                resultDto.setExt("1");
            }
        }else{
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }
        return resultDto;
    }
}
