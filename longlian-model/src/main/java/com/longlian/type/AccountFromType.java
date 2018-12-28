package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum AccountFromType {
    default_value(0,"默认"),
    distribution(1,"分销奖励"),
    introducer_teach(2,"推荐老师平台奖励"),
    teach_course(3,"老师课程授课奖励"),
    teach_follow(4,"老师粉丝关注奖励"),
    money_rechage(5,"钱包充值"),
    teach_bankOut_proxy_return_money(6,"老师提现给代理返钱"),
    user_reward(7,"用户打赏"),
    return_money(10,"提现返钱"),
    reward(11,"平台奖励"),
    deduct_money(12,"平台扣款"),
    buy_relay(13,"转播"),
    relay_yield(14,"转播分成");

    private int value;
    private String text;

    AccountFromType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据key get Text
     * @param value
     * @return
     * @throws Exception
     */
    public static String getNameByValue(int value) {
        AccountFromType[] bankTypes = AccountFromType.values();
        for (AccountFromType ws : bankTypes) {
            if (ws.getValue() == value) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        AccountFromType[] bankTypes = AccountFromType.values();
        for (AccountFromType ws : bankTypes) {
            Map map = new HashMap();
            map.put("value" , ws.getValue());
            map.put("text" , ws.getText());
            list.add(map);
        }
        return list;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
