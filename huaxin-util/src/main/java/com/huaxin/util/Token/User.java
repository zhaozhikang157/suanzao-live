package com.huaxin.util.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by syl on 2016/4/13.
 * 用户信息
 */
public class User extends UserIdentity {
    private String password;//密码
    private String verifCode;//校验码

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifCode() {
        return verifCode;
    }

    public void setVerifCode(String verifCode) {
        this.verifCode = verifCode;
    }
    
    public Map<String , String> toMap() {
        Map<String , String> res = new HashMap();
        res.put("id", this.getId()+"");
        res.put("shopId", this.getShopId()+"");
        res.put("account", this.getAccount() == null ? "" :  this.getAccount() );
        res.put("mobile", this.getMobile()== null ? "" :  this.getMobile() );
        res.put("employeeType", this.getEmployeeType()== null ? "" :  this.getEmployeeType() );
        res.put("name", this.getName()== null ? "" :  this.getName());
        res.put("token", this.getToken());
        res.put("validDate", this.getValidDate() == null ? "" :  this.getValidDate());
        res.put("myStyle", this.getMyStyle() == null ? "" :  this.getMyStyle());
        return res;
    }
}
