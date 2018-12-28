package com.longlian.live.frontcontroller;

import com.alibaba.fastjson.JSONPObject;
import com.huaxin.util.JsonUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.RenBenService;
import com.longlian.model.RenBenUser;
import com.longlian.model.course.CourseCard;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 指定老师邀请卡
 */
@Controller
@RequestMapping("/renben")
public class ApiRenbenController {
    private static Logger log = LoggerFactory.getLogger(ApiRenbenController.class);
    @Autowired
    RenBenService renBenService;

    /**
     * 删除邀请卡
     * @param request
     * @return
     */
    @RequestMapping(value = "/insertRenBenUser")
    @ResponseBody
    public MappingJacksonValue  delCourseCard(HttpServletRequest request, RenBenUser user,String callback){
        ActResultDto ac = new ActResultDto();
        try {
            int result = renBenService.insertRenBenUser(user);
            ac.setData(result);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.SERVER_ERROR_RETY.getCode());
            ac.setMessage(ReturnMessageType.SERVER_ERROR_RETY.getMessage());
        }
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(ac);
        if(callback != null && callback != "") {
            mappingJacksonValue.setJsonpFunction(callback);
        }
        return mappingJacksonValue;
    }
}
