package com.longlian.type;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方专题页面类型
 */
public enum ThirdSpecialType {

    healthy_people(0,"健人直播")
    ;
    private int type;
    private final String name;

    ThirdSpecialType(int type, String name){
        this.name = name;
        this.type = type;
    }

    public  static List<Map> getList(){
        List list = new ArrayList();
        ThirdSpecialType[] logTypes = ThirdSpecialType.values();
        for (ThirdSpecialType lt : logTypes) {
            Map map = new HashMap();
            map.put("type" , lt.getType());
            map.put("name" , lt.getName());
            list.add(map);
        }
        return list;
    }

    public static String getNameByValue(String value) {
        ThirdSpecialType[] logTypes = ThirdSpecialType.values();
        for (ThirdSpecialType lt : logTypes) {
            if ((lt.getType()+"").equalsIgnoreCase(value)) {
                return lt.getName();
            }
        }
        return "";
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    
     
    
}
