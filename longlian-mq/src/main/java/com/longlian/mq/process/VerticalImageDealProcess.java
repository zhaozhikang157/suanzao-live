package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.StoreFileService;
import com.longlian.live.util.GaussianBlurUtil;
import com.longlian.model.StoreFile;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lh on 2016/10/20.
 * 创建课虚拟化图片(高斯模糊)处理
 */
@Service
public class VerticalImageDealProcess extends LongLianProcess {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreFileService storeFileService;
    @Autowired
    private CourseService courseService;

    public  int threadCount=5;

    private Logger logg = LoggerFactory.getLogger(VerticalImageDealProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getObject(msg , HashMap.class);
            Integer courseId = (Integer)map.get("courseId");
            String image = (String)map.get("image");

            BufferedImage backImage = ImageIO.read(new URL(image));
            //切图
            backImage = cutImg(backImage);
//            float[] matrix = new float[4900];
//            for (int i = 0; i < 4900; i++)
//                matrix[i] = 1.0f/4900.0f;
//
//            BufferedImageOp op = new ConvolveOp( new Kernel(70, 70, matrix), ConvolveOp.EDGE_NO_OP, null );
//            BufferedImage img2 =  backImage.getSubimage(0 , 0 , backImage.getWidth() , backImage.getHeight());
//
//            op.filter(backImage , img2);
            if(backImage!=null){
                int radius = 9;
                if (backImage.getWidth() > 100) {
                    radius = backImage.getWidth() / 50;
                }
                backImage = GaussianBlurUtil.blur(backImage ,radius );

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(backImage, "jpeg", outputStream);

                byte[] bytes = outputStream.toByteArray();

                StoreFile storeFile = new StoreFile();
                storeFile.setSize((long)bytes.length);
                storeFile.setCreateTime(new Date());
                storeFile.setModule("");
                storeFile.setName("vertical_image_"+courseId+".jpg");
                storeFile.setMd5(DigestUtils.md5DigestAsHex(bytes));
                storeFile = storeFileService.doUploadFile(bytes , storeFile);

                courseService.updateCourseVerticalImage((long)courseId , storeFile.getUrl());
                String courseKey = RedisKey.ll_course + courseId;
                redisUtil.del(courseKey);
            }
        }
    }


    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_set_vertical_coverss_img);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    public static BufferedImage cutImg(BufferedImage backImage) throws IOException {
        //放大1倍
        BufferedImage bufferedImage = zoomInImage(backImage, 2);
        if(bufferedImage != null) {
            //定图片的截取坐标 x ,y 截取的图片的宽高
            int base = backImage.getWidth() / 9;
            if (bufferedImage.getHeight() < (16 * base)) {
                base = (bufferedImage.getHeight() - 100) / 16;
            }
            Rectangle rectangle = new Rectangle(backImage.getWidth() - (base * 4),
                    backImage.getHeight() - (base * 8),
                    base * 9, base * 16);
            //截取
            return cutImage(bufferedImage, rectangle, "jpeg");
        }
        return null;
    }

    /**
     * 对图片进行放大
     * @param originalImage 原始图片
     * @param times 放大倍数
     * @return
     */
    public static BufferedImage  zoomInImage(BufferedImage  originalImage, Integer times){
        int width = originalImage.getWidth()*times;
        int height = originalImage.getHeight()*times;
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0,0,width,height,null);
        g.dispose();
        return newImage;
    }

    /**
     * <p>Title: cutImage</p>
     * <p>Description:  根据原图与裁切size截取局部图片</p>
     * @param rect        需要截取部分的坐标和大小
     */
    public static BufferedImage cutImage(BufferedImage image, Rectangle rect , String suffix){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, suffix, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(is);
            // 根据图片类型获取该种类型的ImageReader
            ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
            reader.setInput(imageInputStream, true);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(rect);
            return reader.read(0, param);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        String image = "D:\\360MoveData\\Pictures\\22.jpeg";
        BufferedImage backImage = ImageIO.read(new FileInputStream(image));
        //切图
        backImage = cutImg(backImage);
        if(backImage!=null) {
            int radius = 10;
            if (backImage.getWidth() > 100) {
                radius = backImage.getWidth() / 50;
            }
            backImage = GaussianBlurUtil.blur(backImage, radius);
            ImageIO.write(backImage, "jpeg", new File("d:\\123.jpg"));
        }
    }
}
