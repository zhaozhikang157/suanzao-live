package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.longlian.console.service.AppUserService;
import com.longlian.live.service.LlAccountService;
import com.longlian.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by admin on 2017/4/29.
 */
@RequestMapping("/llAccount")
@Controller()
public class LlAccountController {
    private static Logger log = LoggerFactory.getLogger(LlAccountController.class);

    @Autowired
    LlAccountService llAccountService;
    @Autowired
    AppUserService appUserService;

    /**
     * 查询AppId,钱币表中是否都存在,然后把不存在的插入
     * @return
     */
    @RequestMapping("")
    @ResponseBody
    public ActResult createLlAccount(){
        ActResult result = new ActResult();
        List<Long> allAppIds = appUserService.findAllAppUser();
        Boolean boo = llAccountService.userCreateAccount(allAppIds);
        if(boo){
            return result;
        }else{
            result.setSuccess(false);
            return result;
        }
    }
}
