package com.longlian.token;

import com.longlian.model.AppUser;

import java.math.BigDecimal;

public class AppUserIdentity extends AppUser{
    
    public String token ;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 是不是老师客户端登录 1-是 0或者null-不是
     */
    public String isTeacherClient;

    public String getIsTeacherClient() {
        return isTeacherClient;
    }

    public void setIsTeacherClient(String isTeacherClient) {
        this.isTeacherClient = isTeacherClient;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    /**
     * 就否是新用户
     */
    public boolean isNewUser = false;


    /**
     * 用戶 -- 账户余额
     */
    public BigDecimal balance = new BigDecimal(0);

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
