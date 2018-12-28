package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum OrderType {
    buyCourse("0","购买课程"),
    bankOut("1","提现"),
    recharge("2","充值枣币"),
    recharge_learn_coinpay("3","充值学币"),
    a_reward_learn_coin("4","打赏学币"),
    recharge_data("5","充值流量"),
    RELAY_COURSE("6","转播课程");

    private String value;
    private String text;

    OrderType(String value, String text) {
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
        OrderType[] bankTypes = OrderType.values();
        for (OrderType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        OrderType[] bankTypes = OrderType.values();
        for (OrderType ws : bankTypes) {
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
