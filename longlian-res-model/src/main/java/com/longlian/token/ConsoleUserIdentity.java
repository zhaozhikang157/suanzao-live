package com.longlian.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.longlian.model.MUser;


public class ConsoleUserIdentity extends MUser implements Serializable{
     
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String token;//JWT生成的令牌
    
    private String account;//账号
    
    private String employeeType;

    private String yunxinToken;

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getAccount() {
        return account;
    }

    public String getYunxinToken() {
        return yunxinToken;
    }

    public void setYunxinToken(String yunxinToken) {
        this.yunxinToken = yunxinToken;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    private String verifCode;//校验码


    public String getVerifCode() {
        return verifCode;
    }

    public void setVerifCode(String verifCode) {
        this.verifCode = verifCode;
    }
    
    public Map<String , String> toMap() {
        Map<String , String> res = new HashMap();
        res.put("id", this.getId()+"");
        res.put("account", this.getAccount() == null ? "" :  this.getAccount() );
        res.put("name", this.getName()== null ? "" :  this.getName());
        res.put("token", this.getToken());
        return res;
    }

}
