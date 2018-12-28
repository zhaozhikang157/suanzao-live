package com.longlian.live.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 导入数据
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class PicUtilTest {
    private static Logger log = LoggerFactory.getLogger(PicUtilTest.class);
    @Autowired
    PicUtil picUtil;

   @Test
   public void testBase64ToUrl() throws Exception {
        String base64 = readString2();
       String url = picUtil.base64ToUrl(base64);
       System.out.println(url);
   }

    private static String readString2()

    {
        StringBuffer str=new StringBuffer("");
        File file=new File("d:\\a.txt");
        try {
            FileReader fr=new FileReader(file);
            int ch = 0;
            while((ch = fr.read())!=-1 ){
                str.append((char)ch);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File reader出错");
        }
        return str.toString();

    }
}
