package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信交易状态
 * Created by Administrator on 2017/2/27.
 */
public enum WechatTradeStateType {
    SUCCESS("SUCCESS","支付成功"),
    REFUND("REFUND","转入退款"),
    NOTPAY("NOTPAY","未支付"),
    CLOSED("CLOSED","已关闭"),
    REVOKED("REVOKED","已撤销（刷卡支付）"),
    USERPAYING("USERPAYING","用户支付中"),
    PAYERROR("PAYERROR","支付失败(其他原因，如银行返回失败)");
    private String value;
    private String text;
    WechatTradeStateType(String value, String text) {
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
        WechatTradeStateType[] types = WechatTradeStateType.values();
        for (WechatTradeStateType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatTradeStateType[] types = WechatTradeStateType.values();
        for (WechatTradeStateType ws : types) {
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
