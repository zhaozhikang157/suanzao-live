package com.huaxin.util.weixin.type;

/**
 * Created by Administrator on 2017/3/4.
 */
public class WechatTemplateMessage {
    private String tempalteId;//模板ID
    private String url;//跳转地址
    private String first;//标题
    private String firstColor = "";
    private String keyword1;
    private String keyword1Color = "#d53c3e";
    private String keyword2;
    private String keyword2Color = "";
    private String keyword3;
    private String keyword3Color = "";
    private String keyword4;
    private String keyword4Color = "";
    private String keyword5;
    private String keyword5Color = "";

    private String color;//控制所有颜色
    private String remark;//备注
    private String remarkColor = "#999999";


    public String getTempalteId() {
        return tempalteId;
    }

    public void setTempalteId(String tempalteId) {
        this.tempalteId = tempalteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }

    public String getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }

    public String getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(String keyword3) {
        this.keyword3 = keyword3;
    }

    public String getKeyword4() {
        return keyword4;
    }

    public void setKeyword4(String keyword4) {
        this.keyword4 = keyword4;
    }

    public String getKeyword5() {
        return keyword5;
    }

    public void setKeyword5(String keyword5) {
        this.keyword5 = keyword5;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        this.setFirstColor(color);
        this.setKeyword1Color(color);
        this.setKeyword2Color(color);
        this.setKeyword3Color(color);
        this.setKeyword4Color(color);
        this.setKeyword5Color(color);
        this.setRemarkColor(color);
    }

    public String getFirstColor() {
        return firstColor;
    }

    public void setFirstColor(String firstColor) {
        this.firstColor = firstColor;
    }

    public String getKeyword1Color() {
        return keyword1Color;
    }

    public void setKeyword1Color(String keyword1Color) {
        this.keyword1Color = keyword1Color;
    }

    public String getKeyword2Color() {
        return keyword2Color;
    }

    public void setKeyword2Color(String keyword2Color) {
        this.keyword2Color = keyword2Color;
    }

    public String getKeyword3Color() {
        return keyword3Color;
    }

    public void setKeyword3Color(String keyword3Color) {
        this.keyword3Color = keyword3Color;
    }

    public String getKeyword4Color() {
        return keyword4Color;
    }

    public void setKeyword4Color(String keyword4Color) {
        this.keyword4Color = keyword4Color;
    }

    public String getKeyword5Color() {
        return keyword5Color;
    }

    public void setKeyword5Color(String keyword5Color) {
        this.keyword5Color = keyword5Color;
    }

    public String getRemarkColor() {
        return remarkColor;
    }

    public void setRemarkColor(String remarkColor) {
        this.remarkColor = remarkColor;
    }
}
