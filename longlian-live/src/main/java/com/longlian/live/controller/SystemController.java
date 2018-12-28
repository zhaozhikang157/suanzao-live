package com.longlian.live.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.type.LogType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Controller
@RequestMapping("/system")
public class SystemController {
	
	private static Logger log = LoggerFactory.getLogger(SystemController.class);
	@Autowired
    RedisUtil redisUtil;
    @Autowired
    WeixinUtil weixinUtil;

    /**
     * 删除web页面导航记录 用户版本升级
     * @param request
     * @return
     */
    @RequestMapping(value = "/delNavigation" , method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除web页面导航记录", httpMethod = "GET", notes = "删除web页面导航记录")
    public ActResult delNavigation(HttpServletRequest request) {
        ActResult actResult = new ActResult();
        redisUtil.del(RedisKey.ll_live_web_user_navigation_record);
        return  actResult;
    }


    /**
     * author syl
     * 外部页面JS攻擊處理 打印，body
     * @return
     */
    @RequestMapping("/printBody")
    @ResponseBody
    @Log(content="{$3}", type= LogType.handler_out_js, systemType = "0" ,deviceNo="")
    public ActResult handlerOutPringBody(HttpServletRequest request , HttpServletResponse response , String body) {
        ActResult actResult = new ActResult();
        //log.error(body);
        return actResult;
    }


    /**
     * rest
     * @return
     */
    @RequestMapping("/rest/{name:[a-z]}")
    @ResponseBody
    public ActResult rest(HttpServletRequest request , HttpServletResponse response , @PathVariable(value ="name") String body) {
        ActResult actResult = new ActResult();
        log.error(body);
        return actResult;
    }

    @RequestMapping("/upload")
    @ResponseBody
    public ActResult upload(HttpServletRequest request , HttpServletResponse response , String serverId) {
        ActResult actResult = new ActResult();
        log.error(serverId);
        //InputStream inputStream = weixinUtil.getTemporaryMediaByMediaid("TGQODCdAcPtT-9iJu1JrdGfedW_37A_fUIDGYM1ev4nrSEIv0nPDfCdxstEHjjALIVhR0b4ucYJv3AQJmGW3FcGl66A7p9zSaZw95JbHs2pOyg9aj2_XLq4VOoQ94yXsPTAeAKDXBI", serverId);
        //System.out.println(inputStream);
        return actResult;
    }


}
