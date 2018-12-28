package com.longlian.model;

import java.io.Serializable;

/**
 *
 * tablename:m_user
 */
public class MUser implements Serializable{
    /**
     *
     * field:id  column:id
     * 
     */
    private Long id;

    /**
     *
     * field:name  column:name
     * 
     */
    private String name;

    /**
     *
     * field:password  column:password
     * 
     */
    private String password;

    /**
     * 0-男 1-女
     *
     * field:gender  column:gender
     * 
     */
    private String gender;

    /**
     *
     * field:idCard  column:id_card
     * 
     */
    private String idCard;

    /**
     * 0-禁止登录 1--可用 -1-删除
     *
     * field:status  column:status
     * default:1
     * 
     */
    private String status;

    /**
     *
     * field:tel  column:tel
     * 
     */
    private String tel;

    /**
     *
     * field:userId  column:user_id
     * 
     */
    private String userId;

    /**
     *
     * field:qq  column:qq
     * 
     */
    private String qq;

    /**
     *
     * field:resId  column:res_id
     * 
     */
    private Long resId;

    /**
     *
     * field:email  column:email
     * 
     */
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }
    
    public static boolean isAdmin(String userId) {
        return "admin".equals(userId);
    }
}