package com.longlian.live.controller;

import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.service.UserRewardTypeService;
import com.longlian.model.LlAccount;
import com.longlian.model.UserRewardType;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/22.
 */
@Controller
@RequestMapping(value = "/userRewardType")
public class UserRewardTypeController {

    @Autowired
    UserRewardTypeService userRewardTypeService;

    @Autowired
    LlAccountService llAccountService;

    /**
     * 获取有效的打赏类型列表
     * @return
     */
    @RequestMapping(value = "/getUserRewardList")
    @ResponseBody
    @ApiOperation(value = "获取有效的打赏类型列表", httpMethod = "GET", notes = "获取有效的打赏类型列表")
    public ActResultDto getUseList(HttpServletRequest request){
        ActResultDto actResultDto = new ActResultDto();
        List<UserRewardType> list = userRewardTypeService.getUseList("0");
        AppUserIdentity appUserIdentity =  SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        BigDecimal account_amount = new BigDecimal(0);
        if(appUserIdentity != null){
           LlAccount llAccount = llAccountService.getAccountByUserId(appUserIdentity.getId());
            if(llAccount != null){
                account_amount = llAccount.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        Map map = new HashMap();
        map.put("userRewardList" , list);
        map.put("accountAmount" , account_amount);
        actResultDto.setData(map);
        return  actResultDto;
    }
}
