package live.process;

import com.longlian.live.service.StoreFileService;
import com.longlian.model.StoreFile;
import com.longlian.mq.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Date;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class VerticalImageDealTest {
    private static Logger log = LoggerFactory.getLogger(VerticalImageDealTest.class);
    @Autowired
    private CourseService courseService;
    @Autowired
    private StoreFileService storeFileService;

    @Test
    public void testProcess() throws Exception {
        BufferedImage backImage = ImageIO.read(new URL("https://longlian-live.oss-cn-hangzhou.aliyuncs.com/upload/2017/07/c1cac2e2aab44993bba77f6022ea8478.jpg"));
        float[] matrix = new float[4900];
        for (int i = 0; i < 4900; i++)
            matrix[i] = 1.0f/4900.0f;

        BufferedImageOp op = new ConvolveOp( new Kernel(70, 70, matrix), ConvolveOp.EDGE_NO_OP, null );
        BufferedImage img2 =  backImage.getSubimage(0 , 0 , backImage.getWidth() , backImage.getHeight());

        op.filter(backImage , img2);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img2, "jpeg", outputStream);

        byte[] bytes = outputStream.toByteArray();

        StoreFile storeFile = new StoreFile();
        storeFile.setSize((long)bytes.length);
        storeFile.setCreateTime(new Date());
        storeFile.setModule("");
        storeFile.setName("vertical_image_2746.jpg");
        storeFile.setMd5(DigestUtils.md5DigestAsHex(bytes));
        storeFile = storeFileService.doUploadFile(bytes , storeFile);
        String cdnPoint = "http://file.llkeji.com/";
        //String cdnPointHttps = "https://"+ LonglianSsoUtil.bucketName+".oss-cn-hangzhou.aliyuncs.com/";
        //storeFile.setUrl(storeFile.getUrl().replace(cdnPoint , cdnPointHttps));

        //courseService.updateCourseVerticalImage(2746l , storeFile.getUrl());
    }


}
