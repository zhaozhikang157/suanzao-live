package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台日志 类型
 *
 * @author pangchao
 */
public enum MSysLogType {
    login("001", "登录"), del("002", "删除"), add("003", "增加"), edit("004", "修改"), priv("005", "设置店铺权限");

    private final String name;
    private String type;

    MSysLogType(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByValue(String type) {
        MSysLogType[] mSysLogType = MSysLogType.values();
        for (MSysLogType ms : mSysLogType) {
            if (ms.getType().equalsIgnoreCase(type)) {
                return ms.getName();
            }
        }
        return "";
    }

    public static List<Map> getList() {
        List list = new ArrayList();
        MSysLogType[] mSysLogType = MSysLogType.values();
        for (MSysLogType ms : mSysLogType) {
            Map map = new HashMap();
            map.put("type", ms.getType());
            map.put("name", ms.getName());
            list.add(map);
        }
        return list;
    }
}
