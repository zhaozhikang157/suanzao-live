package com.longlian.mq.process;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.model.ChatRoomMsg;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Created by admin on 2017/12/21.
 *
 * 预热 消息中的图片地址
 */
@Service
public class MsgImgProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(MsgImgProcess.class);

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
                BufferedImage bufferedImage = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    bufferedImage = ImageIO.read(new URL(yunXinUrl));
                    if(bufferedImage != null){
                        BufferedImage bi_scale = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB );
                        Graphics2D g = bi_scale.createGraphics();
                        g.drawImage( bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null );
                        g.dispose();
                        outputStream = new ByteArrayOutputStream();
                        String path = "CHAT_MSG_IMG/" + UUIDGenerator.generate() + ".jpg";
                        ImageIO.write(bi_scale, "jpg", outputStream);
                        byte[] bytes = outputStream.toByteArray();
                        String url = ssoUtil.putObject(path, bytes);
                        url = url.replace(aliyunUrl,newAliyunUrl);
                        map.put("url", url);
                        chatRoomMsg.setAttach(JsonUtil.toJson(map));
                        chatRoomMsgService.updateAttach(chatRoomMsg);
                        //预热发送消息中的图片地址
                        redisUtil.lpush(RedisKey.push_object_cache_chat_room_msg,JsonUtil.toJson(chatRoomMsg));
                    }
                } catch (IOException e) {
                    log.error("上传图片失败:{}"+e);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
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
        GetData t1 = new GetData(this, redisUtil, RedisKey.chat_room_msg_img);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }
}
