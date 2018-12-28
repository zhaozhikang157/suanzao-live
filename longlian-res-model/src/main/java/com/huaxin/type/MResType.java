package com.huaxin.type;

public enum MResType {
    user("001","用户"),role("002", "角色"),menu("003", "菜单");
    
    private final String name;
    private String type;
    
    MResType(String type , String name){
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
}
