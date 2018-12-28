package com.huaxin.util.statusType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/20.
 */
public enum StatusType {
    START_USING("0","启用"),//启用
    DISABLE("1","禁用"),//禁用
    PERMANENT("0","永久"),//永久
    NOT_PERMANENT("1","非永久"),//非永久
    YEAR("0","年"),//年
    MONTHL("1","月"),//月
    DAY("2","日"),//日
    MAN("0","男"),//男
    WOMAN("1","女"),//女
    TAKE_EFFECT("0","生效"),//生效
    FORZEN("1","冻结"),//冻结
    TO_VOID("2","作废");//作废
    private StatusType(String code, String message){
        this.code = code;
        this.message = message;
    }
    private String code;
    private String message;
    public static List<Map> getPermanents(){//永久-非永久
        List<Map> list = new ArrayList<Map>();
        Map m1 = new HashMap();
        m1.put("name",PERMANENT.getMessage());
        m1.put("id",PERMANENT.getCode());
        list.add(m1);
        Map m2 = new HashMap();
        m2.put("name",NOT_PERMANENT.getMessage());
        m2.put("id",NOT_PERMANENT.getCode());
        list.add(m2);
        return list;
    }
    public static List<Map> getStartOrDisable(){//启用-禁用
        List<Map> list = new ArrayList<Map>();
        Map m1 = new HashMap();
        m1.put("name",START_USING.getMessage());
        m1.put("id",START_USING.getCode());
        list.add(m1);
        Map m2 = new HashMap();
        m2.put("name",DISABLE.getMessage());
        m2.put("id",DISABLE.getCode());
        list.add(m2);
        return list;
    }
    public static List<Map> getSexs(){//男-女
        List<Map> list = new ArrayList<Map>();
        Map m1 = new HashMap();
        m1.put("name",MAN.getMessage());
        m1.put("id",MAN.getCode());
        list.add(m1);
        Map m2 = new HashMap();
        m2.put("name",WOMAN.getMessage());
        m2.put("id",WOMAN.getCode());
        list.add(m2);
        return list;
    }
    public  static List<Map> getHasValidDates(){//年月日
        List<Map> list = new ArrayList<Map>();
        Map m1 = new HashMap();
        m1.put("name",YEAR.getMessage());
        m1.put("id", YEAR.getCode());
        list.add(m1);
        Map m2 = new HashMap();
        m2.put("name",MONTHL.getMessage());
        m2.put("id", MONTHL.getCode());
        list.add(m2);
        Map m3 = new HashMap();
        m3.put("name",DAY.getMessage());
        m3.put("id", DAY.getCode());
        list.add(m3);
        return list;
    }
    public static List<Map> getstatusMaps(){//生效-冻结-作废
        List<Map> list = new ArrayList<Map>();
        Map m1 = new HashMap();
        m1.put("name",TAKE_EFFECT.getMessage());
        m1.put("id", TAKE_EFFECT.getCode());
        list.add(m1);
        Map m2 = new HashMap();
        m2.put("name",FORZEN.getMessage());
        m2.put("id", FORZEN.getCode());
        list.add(m2);
        Map m3 = new HashMap();
        m3.put("name",TO_VOID.getMessage());
        m3.put("id", TO_VOID.getCode());
        list.add(m3);
        return list;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

