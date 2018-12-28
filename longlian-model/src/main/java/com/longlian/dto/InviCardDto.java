package com.longlian.dto;

import com.longlian.model.InviCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Created by admin on 2017/2/15.
 */
public class InviCardDto extends InviCard{
    private static Logger log = LoggerFactory.getLogger(InviCardDto.class);

    private Color buffColor;

    private String fontSize;

    private int isXCrenter; // 1: 居中 0: 不居中 x方向

    private int isYCrenter;

    private int isWrap;//是否需要换行 1:需要

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsWrap() {
        return isWrap;
    }

    public void setIsWrap(int isWrap) {
        this.isWrap = isWrap;
    }

    public int getIsXCrenter() {
        return isXCrenter;
    }

    public void setIsXCrenter(int isXCrenter) {
        this.isXCrenter = isXCrenter;
    }

    public int getIsYCrenter() {
        return isYCrenter;
    }

    public void setIsYCrenter(int isYCrenter) {
        this.isYCrenter = isYCrenter;
    }

    public Color getBuffColor() {
        return buffColor;
    }

    public void setBuffColor(Color buffColor) {
        this.buffColor = buffColor;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}
