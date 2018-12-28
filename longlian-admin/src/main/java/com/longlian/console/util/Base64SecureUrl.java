package com.longlian.console.util;

import org.bouncycastle.util.encoders.UrlBase64;

import java.io.UnsupportedEncodingException;


/**
 * Created by Administrator on 2018/4/16.
 */
public class Base64SecureUrl {

    public final static String ENCODING="UTF-8";
    //����
    public static String encoded(String data) throws UnsupportedEncodingException{
        byte[] b= UrlBase64.encode(data.getBytes(ENCODING));
        return new String(b,ENCODING);
    }
    //����
    public static String decode(String data) throws UnsupportedEncodingException{
        byte[] b= UrlBase64.decode(data.getBytes(ENCODING));
        return new String(b,ENCODING);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
       // Base64SecureUrl.decode("");
       String str= Base64SecureUrl.decode("MTA2NA..");  //Y2FydGVyMjAwMA=="
        System.out.println("-----------"+str);
        String encoded = Base64SecureUrl.encoded("1064");
        System.out.println("ggg:"+encoded);
    }

}
