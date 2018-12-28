package com.longlian.dto;


import com.longlian.model.DataChargeRecord;

import java.math.BigDecimal;

public class DataChargeRecordDto extends DataChargeRecord{

    private String statusName;
    private String timeName;
    private BigDecimal prefPrice;
    private Long amount;
    private String valitTime;

    public String getValitTime() {
        return valitTime;
    }

    public void setValitTime(String valitTime) {
        this.valitTime = valitTime;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public BigDecimal getPrefPrice() {
        return prefPrice;
    }

    public void setPrefPrice(BigDecimal prefPrice) {
        this.prefPrice = prefPrice;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }
}