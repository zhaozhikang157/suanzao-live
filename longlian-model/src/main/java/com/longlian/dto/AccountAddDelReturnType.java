package com.longlian.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syl on 2016/8/16.
 */

public enum AccountAddDelReturnType {
    success("000","操作成功"),
    not_exstis("001","该账户没有余额"),
    bal_not_enough("002","账号余额不足");
    private String value;
    private String text;

    AccountAddDelReturnType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据key get Text
     * @param value
     * @return
     * @throws Exception
     */
    public static String getNameByValue(String value) {
        AccountAddDelReturnType[] bankTypes = AccountAddDelReturnType.values();
        for (AccountAddDelReturnType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        AccountAddDelReturnType[] bankTypes = AccountAddDelReturnType.values();
        for (AccountAddDelReturnType ws : bankTypes) {
            Map map = new HashMap();
            map.put("value" , ws.getValue());
            map.put("text" , ws.getText());
            list.add(map);
        }
        return list;
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
