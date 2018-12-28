package com.longlian.live.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20161216.ImageDetectionRequest;
import com.aliyuncs.green.model.v20161216.ImageDetectionResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.huaxin.util.constant.MsgConst;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.service.YellowResultService;
import com.longlian.live.service.YellowService;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveChannel;
import com.longlian.model.YellowResult;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by liuhan on 2017-03-16.
 */
@Service("yellowService")
public class YellowServiceImpl  implements YellowService{
    private static Logger log = LoggerFactory.getLogger(YellowServiceImpl.class);

    private IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret);
    @Autowired
    private LiveChannelService liveChannelService;
    @Autowired
    private YellowResultService yellowResultService;
    @Autowired
    private SendMsgService sendMsgService;

    /**
     * 鉴黄服务
     *
     * @param url
     */
    @Override
    public boolean yellow(String url, Course course) throws Exception {
        IAcsClient client = new DefaultAcsClient(profile);
        ImageDetectionRequest imageDetectionRequest = new ImageDetectionRequest();
        /**
         * 是否同步调用
         * false: 同步
         */
        imageDetectionRequest.setAsync(false);
        /**
         * 同步图片检测支持多个场景, 但不建议一次设置太多场景:
         * porn:  黄图检测
         * ocr:  ocr文字识别
         * illegal: 暴恐敏感检测
         * ad: 图片广告
         * sensitiveFace: 指定人脸
         * qrcode: 二维码
         */
        imageDetectionRequest.setScenes(Arrays.asList("porn"));
        imageDetectionRequest.setConnectTimeout(4000);
        imageDetectionRequest.setReadTimeout(4000);
        /**
         * 同步图片检测一次只支持单张图片进行检测
         */
        imageDetectionRequest.setImageUrls(Arrays.asList(url));
        try {
            ImageDetectionResponse imageDetectionResponse = client.getAcsResponse(imageDetectionRequest);
            System.out.println(JSON.toJSONString(imageDetectionResponse));
            if("Success".equals(imageDetectionResponse.getCode())){
                List<ImageDetectionResponse.ImageResult> imageResults = imageDetectionResponse.getImageResults();
                if(imageResults != null && imageResults.size() > 0) {
                    //同步图片检测只有一个返回的ImageResult
                    ImageDetectionResponse.ImageResult imageResult = imageResults.get(0);
                    //porn场景对应的检测结果放在pornResult字段中
                    //ocr场景对应的检测结果放在ocrResult字段中
                    //illegal场景对应的检测结果放在illegalResult字段中
                    //ad场景对应的检测结果放在adResult字段中
                    //sensitiveFace场景对应的检测结果放在sensitiveFaceResult字段中
                    //qrcode场景对应的检测结果放在qrcodeResult字段中
                    //请按需获取
                    /**
                     * 黄图检测结果
                     */
                    ImageDetectionResponse.ImageResult.PornResult pornResult = imageResult.getPornResult();
                    if(pornResult != null) {
                        /**
                         * 绿网给出的建议值, 0表示正常，1表示色情，2表示需要review
                         */
                        Integer label = pornResult.getLabel();
                        /**
                         * 黄图分值, 0-100
                         */
                        Float rate = pornResult.getRate();
                        log.info("绿网给出黄图的建议值:{}，分值：{}",label , rate);

                        YellowResult result = new YellowResult();
                        result.setCreateTime(new Date());
                        result.setCourseId(course.getId());
                        result.setCourseName(course.getLiveTopic());
                        result.setCreenLabel(label);
                        result.setRate(new BigDecimal(rate));
                        result.setUrl(url);
                        yellowResultService.insert(result);
                        if (label == 1) {
                            //结束直播流
                            LiveChannel liveChannel = liveChannelService.getCourseLiveAddr(course);
                            liveChannelService.forbidLiveStream(liveChannel , course);
                            sendMsgService.sendMsg(course.getAppId() , MsgType.YELLOW_RESULT_REMIND.getType() , result.getId() ,MsgConst.replace( MsgConst.YELLOW_COMMENT_REMIND, course.getLiveTopic()), "");
                            return false;
                        }
                        // 根据业务情况来做处理
                    }
                    //其他的场景的类似向黄图检测结果一样去get结果，进行处理
//                    ImageDetectionResponse.ImageResult.IllegalResult illegalResult = imageResult.getIllegalResult();
//                    if(illegalResult != null) {
//                        /**
//                         * 绿网给出的建议值, 0表示正常，1表示暴恐敏感，2表示需要review
//                         */
//                        Integer label = illegalResult.getLabel();
//                        /**
//                         * 分值, 0-100
//                         */
//                        Float rate = illegalResult.getRate();
//                        log.info("绿网给出暴恐敏感图的建议值:{}，分值：{}",label , rate);
//
//
//
//
//                        yellowResultService.insert();
//
//                        if (label == 1) {
//                            //结束直播流
//                            LiveChannel liveChannel = liveChannelService.getCourseLiveAddr(course);
//                            liveChannelService.forbidLiveStream(liveChannel , course);
//                            return false;
//                        }
//                    }
                }
            }else{
                /**
                 * 检测失败
                 */
                log.error("鉴黄检测失败");
            }
        } catch (ServerException e) {
            e.printStackTrace();
            log.error("鉴黄检测失败" , e);
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("鉴黄检测失败" , e);
        }
        return true;
    }

    public static void main(String args[]) throws  Exception {
        YellowService impl = new YellowServiceImpl();
        String url = "http://longlian-live.oss-cn-hangzhou.aliyuncs.com/10/QQ%E5%9B%BE%E7%89%8720170316122620.jpg";
        //impl.yellow(url);
    }


}
