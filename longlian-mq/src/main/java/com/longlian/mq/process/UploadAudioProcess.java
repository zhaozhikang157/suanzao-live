 package com.longlian.mq.process;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.util.cmd.CmdExecuter;
import com.longlian.live.util.cmd.FFMpegAudioCommand;
import com.longlian.live.util.cmd.ICommand;
import com.longlian.model.ChatRoomMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by admin on 2018/1/26.
 *
 * 预热 消息中的语音地址
 */
@Service
public class UploadAudioProcess extends LongLianProcess{
    private static Logger log = LoggerFactory.getLogger(UploadAudioProcess.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;

    private final String aliyunUrl = "http://llkeji.oss-cn-beijing.aliyuncs.com/"; //阿里云地址存放目录
    private final String newAliyunUrl = "http://filemedia.llkeji.com/"; //阿里云地址存放目录

    public int threadCount = 10;

    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) {
            ChatRoomMsg chatRoomMsg = JsonUtil.getObject(msg, ChatRoomMsg.class);
            if(StringUtils.isNotEmpty(chatRoomMsg.getAttach())){
                Map map = JsonUtil.getMap4Json(chatRoomMsg.getAttach());
                String yunXinUrl = map.get("url").toString();
                InputStream inputStream = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    URL u = new URL(yunXinUrl);
                    inputStream = u.openStream();
                    outputStream = new ByteArrayOutputStream();
                    int ch;
                    while ((ch = inputStream.read()) != -1) {
                        outputStream.write(ch);
                    }
                    byte[] bytes = outputStream.toByteArray();
                    //取得处理时用到的路径
                    // String basePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
                    String fileName = UUIDGenerator.generate().toString();
                    URL urlUrl = UploadAudioProcess.class.getProtectionDomain().getCodeSource().getLocation();
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
                    String randomPath = UUIDGenerator.generate();
                    File dir = new File(basePath + "audiotmp/"+ randomPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String m4adesc = basePath + "audiotmp/" + randomPath +"/" + fileName + ".m4a";
                    String aacdesc = basePath + "audiotmp/" + randomPath +"/" + fileName + ".aac";
                    //待转化的aac文件
                    File file =  getFileFromBytes(bytes,  aacdesc);
                    file.createNewFile();
                    //aac转化成m4a
                    ICommand audioDeal = new FFMpegAudioCommand(aacdesc , m4adesc);
                    CmdExecuter.exec(audioDeal);
                    //读到m4a到内存
                    byte[] output = getBytesFromFile(new File(m4adesc));
                    //上传m4a
                    String path = "CHAT_MSG_AUDIO/" + fileName + ".m4a";
                    String url = ssoUtil.putObject(path, output);
                    url = url.replace(aliyunUrl,newAliyunUrl);
                    map.put("url", url);
                    map.put("ext", "m4a");
                    chatRoomMsg.setAttach(JsonUtil.toJson(map));
                    chatRoomMsgService.updateAttach(chatRoomMsg);
                    //预热发送消息中的图片地址
                    redisUtil.lpush(RedisKey.push_object_cache_chat_room_msg, JsonUtil.toJson(chatRoomMsg));
                    //删除目录
                    File f = new File(basePath + "audiotmp/"+ randomPath);
                    deleteFile(f);
                }catch (Exception e) {
                    log.error("音频转换上传异常:"+msg ,e);
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
                }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.chat_room_msg_audio);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return threadCount;
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
            log.error("音频转换上传-字节转文件异常",e);
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
            log.error("文件转字节异常：",e);
        }
        return null;
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
}