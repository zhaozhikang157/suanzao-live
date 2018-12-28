package com.longlian.live.util.yunxin;

import com.alibaba.fastjson.JSONArray;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.live.util.HttpClientManage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云信用户相关处理类
 * Created by lh on 2017-02-13.
 */
@Component("yunxinUserUtil")
public class YunxinUserUtil {
    private static Logger log = LoggerFactory.getLogger(YunxinUserUtil.class);


    /**
     * 创建用户（微信首次登录时用）
     * @param accid
     * @param name
     * @return
     * @throws Exception
     */
    public String createUser(String accid , String name , String icon) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.CREATE_USER_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            if (icon == null) {
                icon = "";
            }
            if (name == null) {
                name = "";
            }

            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("accid", accid));
            nvps.add(new BasicNameValuePair("name", name));
            nvps.add(new BasicNameValuePair("icon", icon));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            // 执行请求
            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("创建用户请求报错,accid:{} ,用户名：{} , 返回状态码：{}"  , accid , name , status);
                throw new Exception("创建用户请求报错");
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);
                //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer)msg.get("code");
                if (code == 200) {
                    Map info = (Map)msg.get("info");
                    return (String)info.get("token");
                }
            }
        } catch (Exception e){
            String tip = "创建用户请求报错,accid:"+accid+" ,用户名："+name;
            HttpClientManage.dealException(response , e , tip);
        }
        return "";
    }


    /**
     * 修改用云信信息(更改我的的用户名信息)，如果信息项为null则表示 这次不更新
     * @param accid
     * @param name
     * @throws Exception
     */
    public void updateUserInfo(String accid , String name , String icon){
        HttpResponse response = null;
        try {
            String url = YunXinConst.UPDATE_USER_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("accid", accid));
            if (name != null) {
                nvps.add(new BasicNameValuePair("name", name));
            }
            if (icon != null) {
                nvps.add(new BasicNameValuePair("icon", icon));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            // 执行请求
             response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
             if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("修改用户请求报错,accid:{} ,用户名：{} , 头像：{}, 返回状态码：{}"  , accid , name , icon, status);
                throw new Exception("修改用户请求报错");
            } else {
                // 打印执行结果
                log.info(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (Exception e){
            String tip = "修改用户请求报错,accid:"+accid+" ,用户名："+name + " , 头像：" + icon;
            HttpClientManage.dealException(response , e , tip);
        }
    }

    /**
     * 取得用户信息
     * @param accid
     * @throws Exception
     */
    public Map getUserInfo(String accid) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.GET_USER_INFO_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            JSONArray ja = new JSONArray();
            ja.add(accid);
            nvps.add(new BasicNameValuePair("accids",ja.toJSONString()));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("取得用户信息报错,accid:{} , 返回状态码：{}"  , accid , status);
                throw new Exception("取得用户信息报错");
            } else {
                // 打印执行结果
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                log.info(str);
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    JSONArray info = (JSONArray) msg.get("uinfos");
                    Map res = (Map) info.get(0);
                    return res;
                }
            }
        }catch (Exception e){
            String tip = "取得用户信息报错,accid:" + accid ;
            HttpClientManage.dealException(response , e , tip);
        }
        return new HashMap();
    }

    /**
     * 刷新yunxinToken
     * @param accid
     * @return
     * @throws Exception
     */
    public String refreshToken(String accid) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.REFRESH_TOKEN_URL;

            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("accid", accid));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("刷新yunxinToken,accid:{} , 返回状态码：{}"  , accid , status);
                throw new Exception("刷新yunxinToken报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);

                //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("info");
                    return (String) info.get("token");
                }
            }
        }catch (Exception e){
            String tip = "刷新yunxinToken,accid:" + accid ;
            HttpClientManage.dealException(response , e , tip);
        }
        return "";

    }

    /**
     * addRobot
     * @param accid
     * @return
     * @throws Exception
     */
    public Map addRobot( String roomId , String[] accid) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.ADD_ROBOT_URL;

            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            JSONArray ja = new JSONArray();
            for ( String id : accid) {
                ja.add(id);
            }
            nvps.add(new BasicNameValuePair("roomid", String.valueOf(roomId)));
            nvps.add(new BasicNameValuePair("accids", ja.toJSONString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("添加机器人失败,accid:{} , roomId:{} , 返回状态码：{}"  , accid , roomId , status);
                throw new Exception("添加机器人报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);

                //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    return covertResult(   info);
                }
            }
        }catch (Exception e){
            String tip = "添加机器人失败,accid:" + accid +" , roomId: " + roomId ;
            HttpClientManage.dealException(response , e , tip);
        }
        return null;

    }
    public Map covertResult( Map info) {
        String successAccids = (String) info.get("successAccids");
        String failAccids = (String) info.get("failAccids");

        List<String> successAccidsList = JSONArray.parseArray(successAccids, String.class);
        List<String> failAccidsList = JSONArray.parseArray(failAccids, String.class);
        Map result = new HashMap();
        result.put("success" , successAccidsList.toArray());
        result.put("fail" , failAccidsList.toArray());
        return result;
    }

    /**
     * addRobot
     * @param accid
     * @return
     * @throws Exception
     */
    public Map removeRobot( String roomId , String[] accid) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.REMOVE_ROBOT_URL;

            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            JSONArray ja = new JSONArray();
            for ( String id : accid) {
                ja.add(id);
            }
            nvps.add(new BasicNameValuePair("roomid", String.valueOf(roomId)));
            nvps.add(new BasicNameValuePair("accids", ja.toJSONString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("移出机器人失败,accid:{} , roomId:{} , 返回状态码：{}"  , accid , roomId , status);
                throw new Exception("移出机器人报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);

                //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    return covertResult( info);
                }
            }
        }catch (Exception e){
            String tip = "移出机器人失败,accid:" + accid +" , roomId: " + roomId ;
            HttpClientManage.dealException(response , e , tip);
        }
        return null;

    }
}
