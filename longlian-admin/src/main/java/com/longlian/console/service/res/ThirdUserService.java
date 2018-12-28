package com.longlian.console.service.res;

import com.huaxin.util.ActResult;
import com.longlian.model.MUser;
import com.longlian.token.ConsoleUserIdentity;

import java.util.List;
import java.util.Map;

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
