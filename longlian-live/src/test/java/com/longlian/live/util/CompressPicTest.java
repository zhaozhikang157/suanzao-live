package com.longlian.live.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * 导入数据
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CompressPicTest {
    private static Logger log = LoggerFactory.getLogger(CompressPicTest.class);
    @Autowired
    CompressPicUtil compressPicUtil;

  /* @Test
   public void testBase64ToUrl() throws Exception {
       File file = new File("d://IMG_8518.JPG");
//       byte[] compressPic = compressPicUtil.compressPic(new FileInputStream(file), ".jpg");

<<<<<<< Updated upstream
       byte2image(null , "d://IMG_85182.JPG");
   }
=======
       byte2image(compressPic , "d://IMG_85182.JPG");
   }*/

    public void byte2image(byte[] data,String path){
        try{
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch(Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
}
