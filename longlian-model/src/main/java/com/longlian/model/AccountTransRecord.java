package com.longlian.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 枣币转换为学币记录
 * Created by Administrator on 2018/5/9 0009.
 */
public class AccountTransRecord implements Serializable {

    private long id;            //ID
    private long appId;        //用户ID
    private BigDecimal amount;  //转换金额
    private String status;      //转换状态
    private Date create_time;   //转换时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_time() {
        if(create_time != null){
            return sdf.format(create_time);
        }
        return null;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}