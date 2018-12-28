package com.longlian.dto;

import java.math.BigDecimal;

/**
 * 返回信息对象
 * Created by syl on 2016/8/16.
 */

public class AccountAddDelReturn<T> {
    private String code  = AccountAddDelReturnType.success.getValue();//返回类型
    private String desc = AccountAddDelReturnType.success.getText();//返回描述
    private BigDecimal preBalance;//变动之前金额
    private BigDecimal afterBalance;//变动后金额
    private T data;//其他扩展数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getPreBalance() {
        return preBalance;
    }

    public void setPreBalance(BigDecimal preBalance) {
        this.preBalance = preBalance;
    }

    public BigDecimal getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.getCode() + ":" + this.getDesc() + ";pre:" + this.getPreBalance() + ";after:" + this.getAfterBalance();
    }
}
