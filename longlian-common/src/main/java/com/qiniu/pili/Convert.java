package com.qiniu.pili;
import com.qiniu.pili.utils.UrlSafeBase64; import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException; import java.net.URL;
/**
 * Created by Misty on 16/11/16. */
public class Convert {
    private static Logger log = LoggerFactory.getLogger(Convert.class);
    private static OkHttpClient client ;
    private static MediaType mediaType = MediaType.parse("application/json");
    public static void updateConvert(Mac mac,String hub,String key){
        client = new OkHttpClient();
        try {
            URL url = new URL("http://pili.qiniuapi.com/v2/hubs/"+hub+"/streams/"+ UrlSafeBase64.encodeToString(key.getBytes())+"/converts");
            byte[] bodycontent = "{\"converts\":[\"720p\",\"480p\"]}".getBytes();
            String token = "Qiniu "+mac.signRequest(url,"POST",bodycontent,"application/json");
            log.info("七牛token："+token);
            RequestBody requestBody = RequestBody.create(mediaType,bodycontent);
            Request request = new Request.Builder().url(url).addHeader("Authorization",token).post(requestBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.info("IO异常：",e);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    log.info("请求返响应码："+response.code());
                    log.info("请求返回响应消息："+response.message());
                    ResponseBody  body = response.body();//获取响应体
                    log.info("请求返回数据："+response.body().string());
                    body.close();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
