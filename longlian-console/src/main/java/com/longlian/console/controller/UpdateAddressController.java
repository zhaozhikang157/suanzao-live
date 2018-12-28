package com.longlian.console.controller;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.ActResult;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.PushCacheService;
import com.longlian.live.util.HttpClientManage;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.SignatureUtils;
import com.longlian.live.util.cmd.CmdExecuter;
import com.longlian.live.util.cmd.FFMpegAudioCommand;
import com.longlian.live.util.cmd.ICommand;
import com.longlian.model.ChatRoomMsg;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2018/1/29.
 */
@RequestMapping("/updateAddress")
@Controller
public class UpdateAddressController {
    private static Logger log = LoggerFactory.getLogger(UpdateAddressController.class);

    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    PushCacheService pushCacheService;
    @Autowired
    RedisUtil redisUtil;

    private final String aliyunUrl = "http://llkeji.oss-cn-beijing.aliyuncs.com/"; //云信地址存放目录
    private final String newAliyunUrl = "http://filemedia.llkeji.com/"; //阿里云地址存放目录

    @RequestMapping("/update")
    @ResponseBody
    public ActResult update(){
        long pageSize = 2000;
        long offset = 0;
        int size = 0;
        List<String> stringList = new ArrayList<String>();
        do {
            List<ChatRoomMsg> list = new ArrayList<>();
                    //chatRoomMsgService.findPictureAndVideo(offset,pageSize);
            size = list.size();
            offset = offset + list.size();
            for (ChatRoomMsg chatRoomMsg : list) {
                String attach = chatRoomMsg.getAttach();
                if (StringUtils.isNotEmpty(attach)) {
                    Map map = JsonUtil.getMap4Json(attach);
                    String ext = map.get("ext").toString();
                    Object object = map.get("url");
                    if (object != null) {
                        String mapUrl = object.toString();
                        if (!mapUrl.contains(newAliyunUrl)) {
                            String url = uploadFile(mapUrl , chatRoomMsg.getMsgType() , "."+ext); //上传到阿里云的地址
                            if(StringUtils.isNotEmpty(url)){
                                map.put("url", url);
                                ext = url.substring(url.length() - 3 , url.length());
                                map.put("ext",ext);
                                chatRoomMsg.setAttach(JsonUtil.toJson(map));
                                chatRoomMsgService.updateAttach(chatRoomMsg);
                                stringList.add(url);
                            }
                        }
                    }
                }
            }
        }while (size == pageSize);
        //执行预热
        if(stringList.size() > 0){
            pushCacheService.pushCachListeUrl(stringList);
        }
        String catalog = redisUtil.get(RedisKey.update_chat_room_msg_audio_catalog + DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        if(StringUtils.isNotEmpty(catalog)){
            File file = new File(catalog);
            deleteFile(file);
        }
        redisUtil.expire(RedisKey.update_chat_room_msg_audio_catalog + DateFormatUtils.format(new Date(), "yyyy-MM-dd") , 2 * 24 * 60 * 60);
        return ActResult.success();
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0;i < files.length;i ++) {
                    this.deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }

    private String uploadFile(String uu, String msgType, String ext) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        String url = "";
        try {
            URL u = new URL(uu);
            inputStream = u.openStream();
            outputStream = new ByteArrayOutputStream();
            int ch;
            while ((ch = inputStream.read()) != -1) {
                outputStream.write(ch);
            }
            byte[] bytes = outputStream.toByteArray();
            String path = "";
            if ("AUDIO".equals(msgType)) {    //音频
                if(!".m4a".equals(ext)){
                    path = "CHAT_MSG_AUDIO/" + UUIDGenerator.generate().toString() + ".m4a";
                    byte[] b = updateM4a(bytes);
                    url = ssoUtil.putObject(path, b);
                    url = url.replace(aliyunUrl, newAliyunUrl);
                }
            } else {
                path = "CHAT_MSG_IMG/" + UUIDGenerator.generate() + ext;
                url = ssoUtil.putObject(path, bytes);
                url = url.replace(aliyunUrl,newAliyunUrl);
            }
        } catch (IOException e) {
            log.error("上传失败:{}" + e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return url;
        }
    }

    public byte[] updateM4a(byte[] bytes){
        byte[] output = null;
        try {
            String fileName = UUIDGenerator.generate().toString();
            URL urlUrl = UpdateAddressController.class.getProtectionDomain().getCodeSource().getLocation();
            String basePath = null;
            try {
                basePath = URLDecoder.decode(urlUrl.getPath(), "utf-8");// 转化为utf-8编码
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (basePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
                // 截取路径中的jar包名
                basePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
            }
            String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
            File dir = new File(basePath + "audiotmp/"+ date);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            redisUtil.set(RedisKey.update_chat_room_msg_audio_catalog + date , basePath + "audiotmp/"+ date);
            String m4adesc = basePath + "audiotmp/" + date +"/" + fileName + ".m4a";
            String aacdesc = basePath + "audiotmp/" + date +"/" + fileName + ".aac";
            //待转化的aac文件
            File file =  getFileFromBytes(bytes,  aacdesc);
            file.createNewFile();
            //aac转化成m4a
            ICommand audioDeal = new FFMpegAudioCommand(aacdesc , m4adesc);
            CmdExecuter.exec(audioDeal);
            //读到m4a到内存
            output = getBytesFromFile(new File(m4adesc));
        }catch (Exception e){
           log.error("消息上传m4a错误:{}"+e);
        }
        return output;
    }

    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static byte[] getBytesFromFile(File f)  {
        if (f == null)  {
            return null;
        }
        try  {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e)  {

        }
        return null;
    }

    public static void main(String[] args) {
        String s = "http://filemedia.llkeji.com/CHAT_MSG_AUDIO/e8d24740ef004cdf96a8d6e25a49c895.m4a";
        String ss = s.substring(s.length() - 3 , s.length());
        System.out.println(ss);
    }
}
