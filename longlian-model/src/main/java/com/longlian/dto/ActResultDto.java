package com.longlian.dto;

import com.longlian.type.ReturnMessageType;

/**
 * Created by syl on 2016/8/10.
 */
public class ActResultDto<T> {

    private String code;//返回成功/失败状态码  用枚举类 ReturnMessageType
    private String message;//返回状态码含义。
    private T data;//返回真实数据
    private Object ext;  //IOS需要返回一个PKCS8的一个密钥(仅供支付宝使用)
    private Object attach; //附加信息（需要额外返回一些信息时使用）
    private long orderId;//成功返回的订单ID 第三方支付
    public ActResultDto(){
        this.code = ReturnMessageType.CODE_MESSAGE_TRUE.getCode();
        this.message  = ReturnMessageType.CODE_MESSAGE_TRUE.getMessage();
    }
    
    public ActResultDto(String code){
        this.code = code;
        this.message  = "";
        ReturnMessageType rt = ReturnMessageType.get(code);
        if (rt != null) {
            this.message  = rt.getMessage();
        }  
    }

    public String getCode() {
        return code;
    }
    /**
     * 
     * @param code
     * @return ActResultDto 这样可以链式调用
     */ 
    public ActResultDto setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ActResultDto setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }
    
    public ActResultDto setData(T data) {
        this.data = data;
        return this;
    }
    
    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
    
    public static ActResultDto success(){
        return new ActResultDto();
    }
    public static ActResultDto fail(String code){
        return new ActResultDto(code);
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Object getAttach() {
        return attach;
    }

    public void setAttach(Object attach) {
        this.attach = attach;
    }
}
