package com.longlian.third.controller;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.WechatOfficial;
import com.longlian.token.WechatOfficialIdentity;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by lh on 2017/2/8.
 */
@Controller
@RequestMapping(value = "/")
public class MainController {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    WechatOfficialService wechatOfficialService;

    /**
     * 用户登录
     * @param 
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "loginIn")
    @ResponseBody
    public ActResultDto loginIn(String appid, HttpServletResponse response) throws Exception {
        ActResultDto result = new ActResultDto();
        WechatOfficial user= new WechatOfficial();
        user.setAppid(appid);
        result = wechatOfficialService.loginIn(user );
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(result.getCode())){
            return  result;
        }
        WechatOfficialIdentity identity = (WechatOfficialIdentity) result.getData();
        String key = identity.getId() + "";
        response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        result.setData(identity);
        return result;
    }
    @RequestMapping(value = "")
    public ModelAndView defaultPage(String appid, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/func/thirdWechat/index");
        return modelAndView;
    }


    /**
     * 测试去redis时间
     * @param appid
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "testRedis")
    @ResponseBody
    public ActResultDto testRedis(String appid, HttpServletResponse response) throws Exception {
        ActResultDto result = new ActResultDto();
        long start = System.currentTimeMillis();
        String token = redisUtil.get(RedisKey.ll_live_third_component_access_token);
        long end = System.currentTimeMillis();
        String remark = "get token " + token + " ---time---》" +   + (end -start);
        result.setData(remark);
        return result;
    }
    @RequestMapping(value="/downloadAttachment")
    public ResponseEntity<byte[]> downloadAttachment(HttpServletRequest request, Model model)throws Exception {
        //下载文件路径
        String filename = "酸枣服务号对接合同.pdf";
        String path = request.getServletContext().getRealPath("/")+"web/res/file/";
        File file = new File(path + File.separator +filename);
        HttpHeaders headers = new HttpHeaders();
        //下载显示的文件名，解决中文名称乱码问题 
        String downloadFielName = new String(filename.getBytes("UTF-8"),"iso-8859-1");
        //通知浏览器以attachment（下载方式）打开图片
        headers.setContentDispositionFormData("attachment", downloadFielName);
        //application/octet-stream ： 二进制流数据（最常见的文件下载）。
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }



}
