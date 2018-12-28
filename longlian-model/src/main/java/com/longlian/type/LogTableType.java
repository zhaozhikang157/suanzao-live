package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LogTableType {
    def("默认","0"),order("订单","1"), ;
    private String val;
    private String name;

    private LogTableType(String name, String val) {
        this.val = val;
        this.name = name;
    }

    public  static List<Map> getList(){
        List list = new ArrayList();
        LogTableType[] logLevels = LogTableType.values();
        for (LogTableType ll : logLevels) {
            Map map = new HashMap();
            map.put("val" , ll.getVal());
            map.put("name" , ll.getName());
            list.add(map);
        }
        return list;
    }

    public static String getNameByValue(String value) {
        LogTableType[] LogTableTypes = LogTableType.values();
        for (LogTableType ltt : LogTableTypes) {
            if ((ltt.getVal()+"").equalsIgnoreCase(value)) {
                return ltt.getName();
            }
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
    
}
