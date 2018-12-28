package com.longlian.type;

/**
 * Created by admin on 2018/3/19.
 * 固定使用的短信模板名称和使用规则
 */
public enum SMSTemplate {

    SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD("酸枣在线","SMS_127161347");

    private String name;
    private String code;

    SMSTemplate(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
