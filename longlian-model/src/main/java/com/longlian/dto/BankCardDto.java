package com.longlian.dto;

import com.longlian.model.BankCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by admin on 2017/2/25.
 */
public class BankCardDto extends BankCard {
    private static Logger log = LoggerFactory.getLogger(BankCardDto.class);

    private String picAddress;

    private String backgroundStart;

    private String backgroundEnd;


    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public String getBackgroundStart() {
        return backgroundStart;
    }

    public void setBackgroundStart(String backgroundStart) {
        this.backgroundStart = backgroundStart;
    }

    public String getBackgroundEnd() {
        return backgroundEnd;
    }

    public void setBackgroundEnd(String backgroundEnd) {
        this.backgroundEnd = backgroundEnd;
    }
}
