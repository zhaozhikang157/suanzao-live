package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum PayType {
    learn_coin_pay("07","学币"),
    offlinepay("08","线下支付"),
    moneybag("09","钱包"),
    unionpay("10","银联"),
    alipay("11","支付宝"),
    abc("12","农行"),
    weixin("13","微信"),
    weixin_h5("14","微信H5"),
    ios("15","IOS内购"),
    invi_code("16","邀请码"),
    weixin_H5_buy_flow("17","微信H5_充值流量"),
    weixin_buy_flow("18","APP微信充值流量");
    private String value;
    private String text;

    PayType(String value, String text) {
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
        PayType[] bankTypes = PayType.values();
        for (PayType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        PayType[] bankTypes = PayType.values();
        for (PayType ws : bankTypes) {
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
