package com.huaxin.util.type;

/**
 * Created by longlian007 on 2018/1/29.
 */
public enum CourseOSS {

    longlian_live("longlian-live","http://file.llkeji.com"),
    longlian_live2("longlian-live2","http://file2.llkeji.com"),
    longlian_output("longlian-output","http://file3.llkeji.com");

    private String value;
    private String text;

    CourseOSS(String value, String text) {
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
