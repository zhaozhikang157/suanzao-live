package com.huaxin.util.Token;

/**
 * 用户身份
 * Created by syl on 2016/4/13.
 */
public class UserIdentity {
    private long id;//ID
    private long shopId;//店铺Id
    private String account;//账号
    private String mobile;//手机号
    private String employeeType;//员工类型 0-员工 1-系统管理员
    private String name;//账号姓名
    private String token;//JWT生成的令牌
    private String validDate;//有效时间
    private long positionId;//职位ID
    private String positionName;//职位名称
    private String myStyle;//  //我的样式风格 default（blue） -默认（蓝色） green-绿色 pink- 粉红色

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getMyStyle() {
        return myStyle;
    }

    public void setMyStyle(String myStyle) {
        this.myStyle = myStyle;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}
