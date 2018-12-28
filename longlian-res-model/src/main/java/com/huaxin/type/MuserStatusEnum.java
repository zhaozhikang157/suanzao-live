package com.huaxin.type;

/**
 * Created by baohj on 2016/5/18.
 */
public enum MuserStatusEnum {

	/*'0-禁止登录 1--可用 -2-删除',*/
    muser_no_permit("0","禁止"),
    muser_permit("1","可用 "),
    muser_isdel("2","删除");
	
    private String value;
    private String text;

    MuserStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
