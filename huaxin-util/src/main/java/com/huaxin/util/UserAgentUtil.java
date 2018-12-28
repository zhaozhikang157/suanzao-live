package com.huaxin.util;

import com.huaxin.util.dto.UserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by syl on 2016/10/20.
 */
public class UserAgentUtil {
    public  static String weixin = "weixin";
    public  static String android = "android";
    public  static String ios = "ios";
    public  static String browser = "browser";
    /**
     * 用途：根据客户端 User Agent Strings 判断其浏览器、操作平台
     * if 判断的先后次序：
     * 根据设备的用户使用量降序排列，这样对于大多数用户来说可以少判断几次即可拿到结果：
     *  >>操作系统:Windows > 苹果 > 安卓 > Linux > ...
     *  >>Browser:Chrome > FF > IE > ...
     * @param userAgent
     * @return
     */
    public  static  UserAgent getUserAgentCustomer(String userAgent){
        //等待扩展哦 嘿嘿
        if (!Utility.isNullorEmpty(userAgent)) {
            UserAgent userAgent1 = null;
            //System.out.println("userAgent---->" + userAgent);
            userAgent = userAgent.toLowerCase();
            if (userAgent.contains("micromessenger")) {
                userAgent1 = new UserAgent();
                userAgent1.setCustomerType(weixin);
            }else  if(userAgent.contains("okhttp/")) {
                userAgent1 = new UserAgent();
                userAgent1.setCustomerType(android);
            }else  if (userAgent.contains("iphone; ios") || userAgent.contains("iphone;ios")) {
                userAgent1 = new UserAgent();
                userAgent1.setCustomerType(ios);
            } else { //浏览器进入按微信处理
                userAgent1 = new UserAgent();
                userAgent1.setCustomerType(browser);
            }
            return  userAgent1;
        }
        return null;
    }

    /**
     * 获取客户端访问信息
     * @param request
     * @return
     */
    public  static  UserAgent getUserAgentCustomer(HttpServletRequest request){
        String userAgentStr = request.getHeader("USER-AGENT");
        return   getUserAgentCustomer( userAgentStr);
    }

    public static UserAgent getUserAgent(String userAgent) {

        //等待扩展哦
        if (userAgent.contains("Windows")) {//主流应用靠前
            /**
             * ******************
             * 台式机 Windows 系列
             * ******************
             * Windows NT 6.2   -   Windows 8
             * Windows NT 6.1   -   Windows 7
             * Windows NT 6.0   -   Windows Vista
             * Windows NT 5.2   -   Windows Server 2003; Windows XP x64 Edition
             * Windows NT 5.1   -   Windows XP
             * Windows NT 5.01  -   Windows 2000, Service Pack 1 (SP1)
             * Windows NT 5.0   -   Windows 2000
             * Windows NT 4.0   -   Microsoft Windows NT 4.0
             * Windows 98; Win 9x 4.90  -   Windows Millennium Edition (Windows Me)
             * Windows 98   -   Windows 98
             * Windows 95   -   Windows 95
             * Windows CE   -   Windows CE
             * 判断依据:http://msdn.microsoft.com/en-us/library/ms537503(v=vs.85).aspx
             */
          /*  if (userAgent.contains("Windows NT 6.2")) {//Windows 8
                return judgeBrowser(userAgent, "Windows", "8" , null);//判断浏览器
            } else if (userAgent.contains("Windows NT 6.1")) {//Windows 7
                return judgeBrowser(userAgent, "Windows", "7" , null);
            } else if (userAgent.contains("Windows NT 6.0")) {//Windows Vista
                return judgeBrowser(userAgent, "Windows", "Vista" , null);
            } else if (userAgent.contains("Windows NT 5.2")) {//Windows XP x64 Edition
                return judgeBrowser(userAgent, "Windows", "XP" , "x64 Edition");
            } else if (userAgent.contains("Windows NT 5.1")) {//Windows XP
                return judgeBrowser(userAgent, "Windows", "XP" , null);
            } else if (userAgent.contains("Windows NT 5.01")) {//Windows 2000, Service Pack 1 (SP1)
                return judgeBrowser(userAgent, "Windows", "2000" , "SP1");
            } else if (userAgent.contains("Windows NT 5.0")) {//Windows 2000
                return judgeBrowser(userAgent, "Windows", "2000" , null);
            } else if (userAgent.contains("Windows NT 4.0")) {//Microsoft Windows NT 4.0
                return judgeBrowser(userAgent, "Windows", "NT 4.0" , null);
            } else if (userAgent.contains("Windows 98; Win 9x 4.90")) {//Windows Millennium Edition (Windows Me)
                return judgeBrowser(userAgent, "Windows", "ME" , null);
            } else if (userAgent.contains("Windows 98")) {//Windows 98
                return judgeBrowser(userAgent, "Windows", "98" , null);
            } else if (userAgent.contains("Windows 95")) {//Windows 95
                return judgeBrowser(userAgent, "Windows", "95" , null);
            } else if (userAgent.contains("Windows CE")) {//Windows CE
                return judgeBrowser(userAgent, "Windows", "CE" , null);
            }*/
        } else if (userAgent.contains("Mac OS X")) {
            /**
             * ********
             * 苹果系列
             * ********
             * iPod -       Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8G4 Safari/6533.18.5
             * iPad -       Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10
             * iPad2    -       Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3
             * iPhone 4 -   Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7
             * iPhone 5 -   Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3
             * 判断依据:http://www.useragentstring.com/pages/Safari/
             * 参考:http://stackoverflow.com/questions/7825873/what-is-the-ios-5-0-user-agent-string
             * 参考:http://stackoverflow.com/questions/3105555/what-is-the-iphone-4-user-agent
             */
            if (userAgent.contains("iPod")) {
                //return judgeBrowser(userAgent, "iPod", null , null);//判断浏览器
            }
        }
        return null;
    }

    /**
     * 判断是否是微信端
     * @param request
     * @return
     */
    public static  boolean isWechatClient(HttpServletRequest request){
        boolean isWechatClient = false;
        String userAgentStr = request.getHeader("USER-AGENT");
        if( userAgentStr != null) {
            userAgentStr = userAgentStr.toLowerCase();
            String uri = request.getRequestURI();
            UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(userAgentStr);
            if (userAgent != null && UserAgentUtil.weixin.equals(userAgent.getCustomerType()))isWechatClient = true;
            if (userAgent != null && UserAgentUtil.browser.equals(userAgent.getCustomerType()))isWechatClient = true;
        }
        return  isWechatClient;
    }
}
