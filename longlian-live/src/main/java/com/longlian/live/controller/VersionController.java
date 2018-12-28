package com.longlian.live.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.MobileVersionService;
import com.longlian.model.MobileVersion;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.MobileVersionType;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Controller
@RequestMapping("version")
public class VersionController {
	
	private static Logger log = LoggerFactory.getLogger(VersionController.class);
	@Autowired
    RedisUtil redisUtil;
    
    public static String android_must_update = null;

    public static String ios_must_update = null;

    public static String android_max_version = null;

    public static String ios_max_version = null;

    public static Map androidUpdateDesc = new HashMap();

    public static Map iosUpdateDesc  = new HashMap();
    @Autowired
    private MobileVersionService mobileVersionService;
//    @Autowired
//    private CountService countService;
	 /**
     * 检查版本
     * @return
     */
    @RequestMapping(value = "check")
    @ResponseBody
    @ApiOperation(value = "检查版本", httpMethod = "GET", notes = "检查版本")
    public ActResultDto checkVersion(HttpServletRequest request,
                                      String version ,
                                     String type) throws  Exception{
        if (StringUtils.isEmpty(version)) {
            return ActResultDto.fail(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }
        log.info("检查版本开始");
        log.info("取身份信息");
        AppUserIdentity token =  SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (token != null) {
            //countService.activeUserCount(token.getId(), type);
            String value = type+"-"+version;
            log.info("当前用户使用的客户端版本信息:key='"+RedisKey.ll_app_type_version+"',file='"+token.getId()+""+"',value='"+value+"'");
            redisUtil.hset(RedisKey.ll_app_type_version,token.getId()+"", type+"-"+version);
        }
        log.info("结束取身份信息");
        //如果是android 2.0.0 或者 ios 3.0.0
       if ("ios".equals(type) ) {
           //还没加载过
       	if (ios_must_update == null || ios_max_version == null) {
       	    //从数据库中加载
       		String[] i = this.loadFromDB(MobileVersionType.ios.getValue());
               ios_must_update = i[0];
               ios_max_version = i[1];
           }
       	//强制升级的版本不为0，且小于必须升级的版本
           if (ios_max_version != null  && Utility.isGreaterThan(ios_max_version , version)) {
        	   return output(MobileVersionType.ios.getValue(),ios_must_update  != null && Utility.isGreaterThan(ios_must_update ,  version));
           }

       } else {
           if (android_must_update == null  || android_max_version == null) {
               String[] i = this.loadFromDB(MobileVersionType.android.getValue() );
               android_must_update = i[0];
               android_max_version = i[1];
           }
           if (android_max_version != null  && Utility.isGreaterThan(android_max_version , version) ) {
               return output( MobileVersionType.android.getValue() , android_must_update != null && Utility.isGreaterThan(android_must_update ,  version)  );
           }
       }
       String ver = "";
       if ("ios".equals(type) ) {
           ver = (String) iosUpdateDesc.get("version");
       } else {
           ver = (String) androidUpdateDesc.get("version");
       }

       return ActResultDto.success().setData(ver !=null ? ver :"");
    }


    
    /**
     * 输出
     */
    public ActResultDto output(String type , boolean isMustUpdate) {
    	ActResultDto dto = new ActResultDto();
    	dto.setCode(ReturnMessageType.APP_MUST_UPDATE.getCode());
    	dto.setMessage(ReturnMessageType.APP_MUST_UPDATE.getMessage());
    	
    	Map res = new HashMap();
    	//是不是必须升级,1为必须升级，0不是必须升级
    	res.put("isMustUpdate", isMustUpdate ? "1" : "0");
	    if (MobileVersionType.ios.getValue().equals(type))  {
	    	res.put("content", iosUpdateDesc.get("content"));
	    	res.put("version", iosUpdateDesc.get("version"));
        } else {
        	res.put("content", androidUpdateDesc.get("content"));
	    	res.put("version", androidUpdateDesc.get("version"));
	    	res.put("address", androidUpdateDesc.get("address"));
        }
	    dto.setData(res);
	    return dto;
    }
    /**
     * 从db中查询，强制升级的最大版本
     * @param type
     * @return
     */
    public String[] loadFromDB(String type) {
        //线上最大的版本
        MobileVersion superVer = mobileVersionService.getSuperVersion(type);

        //线上必须升级的版本
        MobileVersion ver = mobileVersionService.selectMaxVersion(type , superVer);


		 String v =  null;
		 if (ver!= null && !StringUtils.isEmpty(ver.getVersionNum())) {
		      v = ver.getVersionNum();
		 }
        String v2 = null ;
		 if (superVer!= null && !StringUtils.isEmpty(superVer.getVersionNum())) {
             v2 = superVer.getVersionNum();
		 }
		 if (superVer != null) {
		    if (MobileVersionType.ios.getValue().equals(type))  {
		        iosUpdateDesc.put("content", superVer.getVersionBrief());
		        iosUpdateDesc.put("version", superVer.getVersionNum());
		    } else {
		        androidUpdateDesc.put("content", superVer.getVersionBrief());
		        androidUpdateDesc.put("version", superVer.getVersionNum());
		        androidUpdateDesc.put("address",superVer.getDownloadAddress());
		    }
		 }
		 return new String[]{v , v2};
    }

}
