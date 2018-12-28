package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.dto.InviCardDto;
import com.longlian.live.dao.AppUserMapper;
import com.longlian.live.dao.CourseMapper;
import com.longlian.live.dao.InviCardMapper;
import com.longlian.live.dao.LiveRoomMapper;
import com.longlian.live.newdao.WechatOfficialRoomMapper;
import com.longlian.live.service.InviCardService;
import com.longlian.live.util.ImageUtil;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.*;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
@Service("/inviCardService")
public class InviCardServiceImpl implements InviCardService {
    private static Logger log = LoggerFactory.getLogger(InviCardServiceImpl.class);

    @Autowired
    InviCardMapper inviCardMapper;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    WeixinUtil weixinUtil;
    @Value("${website}")
    private String website;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    LiveRoomMapper liveRoomMapper;

    @Autowired
    WechatOfficialRoomMapper wechatOfficialRoomMapper;

    /**
     * 根据编号获取模板编号
     *
     * @param code
     * @return
     */
    @Override
    public InviCard findOneCardByCode(String code, String type) {
        return inviCardMapper.findOneCardByCode(code, type);
    }

    /**
     * 获取课程或者直播间的所有邀请卡模板
     * @param type
     * @return
     */
    @Override
    public List<InviCard> getCourseOrRoomCard(String type) {
        return inviCardMapper.getCourseOrRoomCard(type);
    }

    /**
     * 合成老师制定二维码邀请卡
     * @param appId
     * @param course
     * @param contextUrl
     * @param inviCard
     * @param loginAppId
     * @return
     */
    public BufferedImage createPrivateDrawImage(Long appId, Course course, String contextUrl,
                                         InviCard inviCard,Long loginAppId) {
        URLConnection urlConn = null;
        InputStream input = null;
        try {
            //模板背景图片
           /* URL url = new URL(contextUrl);
            urlConn = url.openConnection();
            input = urlConn.getInputStream();
            BufferedImage bgImage = ImageIO.read(input);*/
            URL url = new URL(contextUrl);
            Image src=Toolkit.getDefaultToolkit().getImage(url);
            BufferedImage bgImage = ImageUtil.toBufferedImage(src);
            //二维码生成以及存入阿里服务器
            BufferedImage qrImage = null;
            if (course != null) {
                String liveQrAddress = weixinUtil.getParaQrcode(WechatQRCodeType.third_wechat_or_course_param.getValue(), course.getRoomId(), course.getId(), loginAppId, 100l);
                log.info("获取老师制定二维码地址:liveQrAddress=" + liveQrAddress);
                System.out.println("获取老师制定二维码地址:liveQrAddress=" + liveQrAddress);
                URL codeUrl = new URL(liveQrAddress);
                Image ercodeSrc = Toolkit.getDefaultToolkit().getImage(codeUrl);
                qrImage = ImageUtil.toBufferedImage(ercodeSrc);
            }
            //size=28; 110,60; 30; 180,0; 33; 25()
            String[] size = inviCard.getSize().split(";");
            String qrSize = size[3];
            //绘制二维码
            qrImage = ImageUtil.getRoundedImage(qrImage, Integer.valueOf(qrSize.split(",")[0]), Integer.valueOf(qrSize.split(",")[1]), 2);
            //log.info("开始合成图片........");
            //合成图片
            //Graphics graphics = bgImage.getGraphics();
            Graphics2D back = bgImage.createGraphics();
            back.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            back.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT);
            //所有坐标
            String[] str = inviCard.getXy().split(";");
            //二维码坐标
            String[] strQr = str[3].split(",");
            if(qrImage != null){
                back.drawImage(qrImage,Integer.valueOf(strQr[0]), Integer.valueOf(strQr[1]), Integer.valueOf(qrSize.split(",")[0]), Integer.valueOf(qrSize.split(",")[0]), null);
            }
            return bgImage;
        } catch (Exception e) {
            log.info("合成老师二维码异常:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
    /**
     * 合成课程邀请卡
     *
     * @param appId
     * @param course
     * @param contextUrl
     * @param inviCard
     * @return
     */
    @Override
    public BufferedImage courseDrawImage(Long appId, Course course, String contextUrl,
                                         InviCard inviCard,Long loginAppId) {
        AppUser teacherUser = appUserMapper.selectByPrimaryKey(appId);
        AppUser loginUser = null;
        if(loginAppId> 0){
            loginUser = appUserMapper.selectByPrimaryKey(loginAppId);
        }
        try {
            //背景图片
            BufferedImage bgImage = ImageIO.read(new FileInputStream(contextUrl + "image/" + inviCard.getAddress()));
            //头像
            BufferedImage photoImage = getUserPhotoImg(loginUser,contextUrl,inviCard);
            //二维码生成以及存入阿里服务器
            BufferedImage qrImage = null;
            if (course != null) {
          
                String liveQrAddress = weixinUtil.getParaQrcode(WechatQRCodeType.third_wechat_or_course_param.getValue(), course.getRoomId(), course.getId(), loginAppId, 100l);
                log.info("获取直播间二维码地址:liveQrAddress=" + liveQrAddress);
                qrImage = ImageIO.read(new URL(liveQrAddress));
//                测试使用的二维码(没有实际意义)
//                String qrAddress = website + "/weixin/courseInfo?id=0";
//                BitMatrix bitMatrix = ImageUtil.buildQRCode(qrAddress);
//                qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            }
            String[] size = inviCard.getSize().split(";");
            String qrSize = size[3];
            qrImage = ImageUtil.getRoundedImage(qrImage, Integer.valueOf(qrSize.split(",")[0]), Integer.valueOf(qrSize.split(",")[1]), 2);
            log.info("开始合成图片........");
            //合成图片
            Graphics graphics = bgImage.getGraphics();
            //所有坐标
            String[] str = inviCard.getXy().split(";");
            //头像坐标 xy中第二组数据
            String[] strPhoto = str[1].split(",");
            if(!"0".equals(strPhoto[1].toString())){
                graphics.drawImage(photoImage, Integer.valueOf(strPhoto[0]), Integer.valueOf(strPhoto[1]), null);
            }
            //二维码坐标
            String[] strQr = str[3].split(",");
            graphics.drawImage(qrImage, Integer.valueOf(strQr[0]), Integer.valueOf(strQr[1]), null);
            //存放到对象中
            List<InviCardDto> inviCardList = getInviCardDto(str,inviCard ,teacherUser , "1",course,null,loginUser);
            BufferedImage result =  ImageUtil.ImgYin(inviCardList, bgImage, contextUrl,
                    inviCard.getImgWidth(),Integer.valueOf(inviCard.getHight().split(";")[1]));
            return result;
        } catch (Exception e) {
            log.info("e=" + e);
            return null;
        }
    }

    @Override
    public BufferedImage roomDrawImage(Long appId, LiveRoom liveRoom, String contextUrl,
                                       InviCard inviCard,Long loginAppId) {
        AppUser teacherUser = appUserMapper.selectByPrimaryKey(appId);
//        AppUser loginUser = appUserMapper.selectByPrimaryKey(loginAppId);
        try {
            //背景图片
            BufferedImage bgImage = ImageIO.read(new FileInputStream(contextUrl + "image/" + inviCard.getAddress()));
            //头像
            BufferedImage photoImage = getUserPhotoImg(teacherUser, contextUrl,inviCard);
            //二维码生成以及存入阿里服务器
            BufferedImage qrImage = null;
            if (liveRoom != null) {
                String liveQrAddress = weixinUtil.getParaLiveQrcode( liveRoom.getId());
                log.info("获取直播间二维码地址:liveQrAddress=" + liveQrAddress);
                qrImage = ImageIO.read(new URL(liveQrAddress));
                //测试使用的二维码(没有实际意义)
//                String qrAddress = website + "/weixin/courseInfo?id=0";
//                BitMatrix bitMatrix = ImageUtil.buildQRCode(qrAddress);
//                qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            }
            String[] strSize = inviCard.getSize().split(";");
            qrImage = ImageUtil.getRoundedImage(qrImage, Integer.valueOf(strSize[3].split(",")[0]), Integer.valueOf(strSize[3].split(",")[1]), 2);
            log.info("开始合成图片........");
            //合成图片
            Graphics graphics = bgImage.getGraphics();
            //所有坐标
            String[] str = inviCard.getXy().split(";");
            //头像坐标 xy中第二组数据
            String[] strPhoto = str[1].split(",");
            if(!"0".equals(strPhoto[1].toString())){
                graphics.drawImage(photoImage, Integer.valueOf(strPhoto[0]), Integer.valueOf(strPhoto[1]), null);
            }
            //二维码坐标
            String[] strQr = str[3].split(",");
            graphics.drawImage(qrImage, Integer.valueOf(strQr[0]), Integer.valueOf(strQr[1]), null);
            //存放到对象中
            List<InviCardDto> inviCardList = getInviCardDto(str, inviCard, teacherUser, "", null, liveRoom, teacherUser);
            BufferedImage result = ImageUtil.ImgYin(inviCardList, bgImage, contextUrl,
                    inviCard.getImgWidth(), Integer.valueOf(inviCard.getHight().split(";")[1]));
            return result;
        } catch (Exception e) {
            log.info("e=" + e);
            return null;
        }
    }

    public List<InviCardDto> getInviCardDto(String[] str , InviCard inviCard , AppUser teacherUser ,
                                            String type ,Course course,LiveRoom liveRoom ,AppUser loginUser ){
        //老师或者机构坐标
        String[] strTeach = str[2].split(",");
        //老师介绍坐标(课程名称)
        String[] strTeachRemark = str[5].split(",");
        //推荐人坐标
        String[] strLoginUser = str[0].split(",");
        //尺寸
        String[] strSize = inviCard.getSize().split(";");
        //颜色
        String[] strColor = inviCard.getColor().split(";");
        List<InviCardDto> inviCardList = new ArrayList<InviCardDto>();
        if(!StringUtils.isEmpty(type)){
            //登录人名称
            InviCardDto loginDto = new InviCardDto();
            String loginName = "游客";
            if(loginUser!=null){
                loginName = loginUser.getName();
            }
            if(!StringUtils.isEmpty(loginName)){
                if(loginName.length()>15){
                    loginName = loginName.substring(0,12)+"...";
                }
            }
            loginDto.setName(loginName);
            String[] loginColor = strColor[0].split(",");
            loginDto.setBuffColor(new Color(Integer.valueOf(loginColor[0]), Integer.valueOf(loginColor[1]), Integer.valueOf(loginColor[2])));
            loginDto.setFontSize(strSize[0]);
            loginDto.setIsXCrenter(1);
            loginDto.setXy(strLoginUser[0] + "," + strLoginUser[1]);
            inviCardList.add(loginDto);
            //讲师名称
            String teacherName = teacherUser.getName();
            InviCardDto teaDto = new InviCardDto();
            if(!StringUtils.isEmpty(teacherName)){
                if(teacherName.length()>15){
                    teacherName = teacherName.substring(0,12)+"...";
                }
            }
            teaDto.setName(teacherName);
            teaDto.setXy(strTeach[0] + "," + strTeach[1]);
            String[] teaColor = strColor[1].split(",");
            teaDto.setBuffColor(new Color(Integer.valueOf(teaColor[0]), Integer.valueOf(teaColor[1]), Integer.valueOf(teaColor[2])));
            teaDto.setFontSize(strSize[2]);
            teaDto.setIsXCrenter(1);
            inviCardList.add(teaDto);
            //开课时间
            if(course.getStartTime() != null && "0".equals(inviCard.getType())){
                String[] strTime = str[6].split(",");
                InviCardDto timeDto = new InviCardDto();
                String time = DateUtil.format(course.getStartTime(), "yyyy-MM-dd HH:mm");
                timeDto.setName(time);
                timeDto.setXy(strTime[0] + "," + strTime[1]);
                String[] timeColor = strColor[3].split(",");
                timeDto.setBuffColor(new Color(Integer.valueOf(timeColor[0]), Integer.valueOf(timeColor[1]), Integer.valueOf(timeColor[2])));
                timeDto.setFontSize(strSize[5]);
                if("0".equals(strTime[0].toString())) timeDto.setIsXCrenter(1);
                inviCardList.add(timeDto);
            }
            //课程主题
            InviCardDto courseName = new InviCardDto();
            String topic = course.getLiveTopic();
            Long additionalY = 0l;
            if(!StringUtils.isEmpty(topic)){
                if(topic.length()>20){
                    topic = topic.substring(0,19)+"...";
                }
                if(topic.length()>29){
                    topic = topic.substring(0,27)+"...";
                    additionalY = Long.parseLong(strTeachRemark[1]);
                }else if(topic.length()<13){
                    additionalY = Long.parseLong(strTeachRemark[1]) + (Integer.valueOf(inviCard.getHight().split(";")[0])*2);
                }else if(topic.length()<30 && topic.length()>12){
                    additionalY = Long.parseLong(strTeachRemark[1]) + Integer.valueOf(inviCard.getHight().split(";")[0]);
                }
            }
            courseName.setXy(strTeachRemark[0] + "," + additionalY);
            courseName.setName(topic);
            String[] courseColor = strColor[2].split(",");
            courseName.setBuffColor(new Color(Integer.valueOf(courseColor[0]), Integer.valueOf(courseColor[1]), Integer.valueOf(courseColor[2])));
            courseName.setFontSize(strSize[4]);
            courseName.setIsXCrenter(1);
            courseName.setIsWrap(1);
            if("0".equals(strTeachRemark[0].toString())) courseName.setIsXCrenter(1);
            inviCardList.add(courseName);
        }else{
            //直播间信息
            //老师名称
            String teacherName = teacherUser.getName();
            if (StringUtils.isEmpty(teacherName)) teacherName = "酸枣在线";
            InviCardDto teaDto = new InviCardDto();
            teaDto.setName(teacherName);
            teaDto.setXy(strTeach[0] + "," + strTeach[1]);
            String[] teaColor = strColor[0].split(",");
            teaDto.setBuffColor(new Color(Integer.valueOf(teaColor[0]), Integer.valueOf(teaColor[1]), Integer.valueOf(teaColor[2])));
            teaDto.setFontSize(strSize[0]);
            teaDto.setIsXCrenter(1);
            inviCardList.add(teaDto);
            //老师简介
            InviCardDto teacherRemark = new InviCardDto();
            String remark = liveRoom.getRemark();
            Long additionalY = 0l;
            if(!StringUtils.isEmpty(remark)){
                if(remark.length()>20) {
                    remark = remark.substring(0, 19) + "...";
                }
                if(remark.length()>29){
                    remark = remark.substring(0,27)+"...";
                    additionalY = Long.parseLong(strTeachRemark[1]);
                }else if(remark.length()<13){
                    additionalY = Long.parseLong(strTeachRemark[1]) + (Integer.valueOf(inviCard.getHight().split(";")[0])*2);
                }else if(remark.length()<30 && remark.length()>12){
                    additionalY = Long.parseLong(strTeachRemark[1]) + (Integer.valueOf(inviCard.getHight().split(";")[0]));
                }
            }
            teacherRemark.setName(remark);
            teacherRemark.setXy(strTeachRemark[0] + "," + additionalY);
            teacherRemark.setIsWrap(1);
            teacherRemark.setIsXCrenter(1);
            String[] teacherColor = strColor[1].split(",");
            teacherRemark.setBuffColor(new Color(Integer.valueOf(teacherColor[0]), Integer.valueOf(teacherColor[1]), Integer.valueOf(teacherColor[2])));
            teacherRemark.setFontSize(strSize[2]);
            if("0".equals(strTeachRemark[0].toString())) teacherRemark.setIsXCrenter(1);
            inviCardList.add(teacherRemark);
        }
        return inviCardList;
    }

    public BufferedImage getUserPhotoImg(AppUser appUser , String contextUrl,InviCard inviCard){
        String photoUrl = "";
        if(appUser!=null){
            photoUrl = appUser.getPhoto();
        }
        BufferedImage photoImage = null;
        try {
            if (!StringUtils.isEmpty(photoUrl)) {
                URL url = new URL(photoUrl);
                photoImage = ImageIO.read(url);
            } else {
                photoUrl = contextUrl + "image/defult.png";
                photoImage = ImageIO.read(new FileInputStream(photoUrl));
            }
            String[] size = inviCard.getSize().split(";");
            String photo = size[1];
            photoImage = ImageUtil.getRoundedImage(photoImage, Integer.parseInt(photo.split(",")[0]), Integer.parseInt(photo.split(",")[1]), 2);
            return photoImage;
        }catch (Exception e){
            log.info("e:"+e);
            return null;
        }
    }
}
