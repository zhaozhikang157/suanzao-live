package com.huaxin.util.weixin.ParamesAPI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2017/2/28.
 */
public class  TrustAnyHostnameVerifier implements HostnameVerifier
{
    public boolean verify(String hostname, SSLSession session)
    {
        return true;
    }
}
