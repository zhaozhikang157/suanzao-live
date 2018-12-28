package com.longlian.res.service;

import java.util.List;
import java.util.Map;

import com.huaxin.util.ActResult;
import com.longlian.model.MUser;
import com.longlian.token.ConsoleUserIdentity;

public interface ThirdUserService {
    
    /**
     * 登录接口
     * @return
     */
   public ActResult<Map> login(ConsoleUserIdentity user) throws Exception ;
   
  
   public ActResult logout(ConsoleUserIdentity user);

   public List<MUser> getMUserByRole(Long roleId);

   public String getUserName(Long userId);
}
