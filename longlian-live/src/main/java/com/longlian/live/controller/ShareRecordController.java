package com.longlian.live.controller;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.live.service.ShareRecordService;
import com.longlian.model.ShareRecord;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/2/24.
 */
@Controller
@RequestMapping("/shareRecord")
public class ShareRecordController {
    private static Logger log = LoggerFactory.getLogger(ShareRecordController.class);

    @Autowired
    ShareRecordService shareRecordService;

    /**
     * 添加分享记录
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "/insertRecord.user" , method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加分享记录", httpMethod = "POST", notes = "添加分享记录")
    public ActResultDto insertRecord(@ApiParam(required =true, name = "分享记录map数据", value = "分享记录map数据")@RequestBody Map map , HttpServletRequest request){
        log.info("---------------------shareRecord-----------------");
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ShareRecordDto share = new ShareRecordDto();
        share.setCourseId(Long.valueOf(String.valueOf(map.get("courseId"))));
        share.setRoomId(Long.valueOf(String.valueOf(map.get("roomId"))));
        share.setStatus(String.valueOf(map.get("status")));
        share.setAppId(Long.valueOf(String.valueOf(map.get("inviAppId"))));
        share.setSystemType(String.valueOf(map.get("systemType")));
        share.setWechatShareType(String.valueOf(map.get("wechatShareType")));
        share.setImgUrl(String.valueOf(map.get("imgUrl")));
        share.setCreateTime(new Date());
        return shareRecordService.insertShare(share);
    }
}
