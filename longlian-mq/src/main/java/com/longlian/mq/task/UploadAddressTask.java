package com.longlian.mq.task;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.UUIDGenerator;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.model.ChatRoomMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by admin on 2018/1/26.
 */
@Component("UploadAddressTask")
public class UploadAddressTask {
    private static Logger log = LoggerFactory.getLogger(UploadAddressTask.class);

    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    SsoUtil ssoUtil;

    private final String aliyunUrl = "http://llkeji.oss-cn-beijing.aliyuncs.com/"; //阿里云地址存放目录

    @Scheduled(cron = "0 10 12 * * ?")
    public void doJob() throws Exception {
        long pageSize = 1000;
        long offset = 0;
        int size = 0;
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
                    String mapUrl = map.get("url").toString();
                    if (StringUtils.isNotEmpty(mapUrl)) {
                        if (!mapUrl.contains(aliyunUrl)) {
                            String url = uploadFile(mapUrl , chatRoomMsg.getMsgType() , "."+ext); //上传到阿里云的地址
                            map.put("url", url);
                        }
                    }
                    chatRoomMsg.setAttach(JsonUtil.toJson(map));
                    chatRoomMsgService.updateAttach(chatRoomMsg);
                }
            }
        }while (size == pageSize);
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
                path = "CHAT_MSG_AUDIO/" + UUIDGenerator.generate() + ext;
            } else {
                path = "CHAT_MSG_IMG/" + UUIDGenerator.generate() + ext;
            }
            url = ssoUtil.putObject(path, bytes);
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


}
