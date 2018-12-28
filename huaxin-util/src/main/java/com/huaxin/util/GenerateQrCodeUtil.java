package com.huaxin.util;

import java.awt.image.BufferedImage;   
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.lang3.StringUtils;
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
/**
 * 生成二维码
 *2015年7月7日
 * @author clc
 *
 */
import java.awt.image.BufferedImage;   
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.lang3.StringUtils;
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
/**
 * 生成二维码
 *2015年7月7日
 * @author clc
 *
 */
public class GenerateQrCodeUtil {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static final String UPLOAD ="upload";
    private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
         int width = matrix.getWidth();
         int height = matrix.getHeight();
         BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
         for (int x = 0; x < width; x++) {
           for (int y = 0; y < height; y++) {
             image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
           }
         }
         return image;
    }    
    /**
     * 生成二维码图片 不存储 直接以流的形式输出到页面
     * @param content
     * @param response
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void encodeQrcode(String content,HttpServletResponse response){
        if(StringUtils.isBlank(content))
            return;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //设置字符集编码类型
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300,hints);
            BufferedImage image = toBufferedImage(bitMatrix);
            //输出二维码图片流
            try {
                ImageIO.write(image, "png", response.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (WriterException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }         
    }
}
