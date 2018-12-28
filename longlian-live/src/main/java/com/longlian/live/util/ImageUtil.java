package com.longlian.live.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.longlian.dto.InviCardDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/2/17.
 */
public class ImageUtil {
    private static Logger log = LoggerFactory.getLogger(ImageUtil.class);

    private static Font font = null;

    /**
     * 针对高度与宽度进行等比缩放
     *
     * @param img
     * @param maxSize 要缩放到的尺寸
     * @param type 1:高度与宽度的最大值为maxSize进行等比缩放 , 2:高度与宽度的最小值为maxSize进行等比缩放
     * @return
     */
    private static Image getScaledImage(BufferedImage img, int maxSize, int type) {
        int w0 = img.getWidth();
        int h0 = img.getHeight();
        int w = w0;
        int h = h0;
        if (type == 1) {
            w = w0 > h0 ? maxSize : (maxSize * w0 / h0);
            h = w0 > h0 ? (maxSize * h0 / w0) : maxSize;
        } else if (type == 2) {
            w = w0 > h0 ? (maxSize * w0 / h0) : maxSize;
            h = w0 > h0 ? maxSize : (maxSize * h0 / w0);
        }
        Image image = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);//在适当的位置画出
        return result;
    }

    /**
     * 先按最小宽高为size等比例绽放, 然后图像居中抠出直径为size的圆形图像
     *
     * @param img
     * @param size
     * @return
     */
    private static BufferedImage getRoundedImage(BufferedImage img, int size) {
        return getRoundedImage(img, size, size / 2, 2);
    }

    /**
     * 先按最小宽高为size等比例绽放, 然后图像居中抠出半径为radius的圆形图像
     *
     * @param img
     * @param size 要缩放到的尺寸
     * @param radius 圆角半径
     * @param type 1:高度与宽度的最大值为maxSize进行等比缩放 , 2:高度与宽度的最小值为maxSize进行等比缩放
     * @return
     */
    public static BufferedImage getRoundedImage(BufferedImage img, int size, int radius, int type) {

        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        //先按最小宽高为size等比例绽放, 然后图像居中抠出直径为size的圆形图像
        Image fixedImg = getScaledImage(img, size, type);
        g.drawImage(fixedImg, (size - fixedImg.getWidth(null)) / 2, (size - fixedImg.getHeight(null)) / 2, null);//在适当的位置画出

        //圆角
        if (radius > 0) {
            RoundRectangle2D round = new RoundRectangle2D.Double(0, 0, size, size, radius * 2, radius * 2);
            Area clear = new Area(new Rectangle(0, 0, size, size));
            clear.subtract(new Area(round));
            g.setComposite(AlphaComposite.Clear);

            //抗锯齿
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.fill(clear);
            g.dispose();
        }
        return result;
    }

    /**
     * 使用删除alpha值的方式去掉图像的alpha通道
     *
     * @param $image
     * @return
     */
    protected static BufferedImage get24BitImage(BufferedImage $image) {
        int __w = $image.getWidth();
        int __h = $image.getHeight();
        int[] __imgARGB = getRGBs($image.getRGB(0, 0, __w, __h, null, 0, __w));
        BufferedImage __newImg = new BufferedImage(__w, __h, BufferedImage.TYPE_INT_RGB);
        __newImg.setRGB(0, 0, __w, __h, __imgARGB, 0, __w);
        return __newImg;
    }

    /**
     * 使用绘制的方式去掉图像的alpha值
     *
     * @param $image
     * @param $bgColor
     * @return
     */
    protected static BufferedImage get24BitImage(BufferedImage $image, Color $bgColor) {
        int $w = $image.getWidth();
        int $h = $image.getHeight();
        BufferedImage img = new BufferedImage($w, $h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor($bgColor);
        g.fillRect(0, 0, $w, $h);
        g.drawRenderedImage($image, null);
        g.dispose();
        return img;
    }

    /**
     * 将32位色彩转换成24位色彩（丢弃Alpha通道）
     *
     * @param $argb
     * @return
     */
    public static int[] getRGBs(int[] $argb) {
        int[] __rgbs = new int[$argb.length];
        for (int i = 0; i < $argb.length; i++) {
            __rgbs[i] = $argb[i] & 0xFFFFFF;
        }
        return __rgbs;
    }


    public static void toPNG(File img, File save, int size) throws IOException {
        ImageIO.write((BufferedImage) getRoundedImage(ImageIO.read(img), size, 0, 2), "PNG", save);//默认无圆角
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image,
                                                  int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius,
                cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static Font getFont(String contextUrl){
        if(font!=null){
            return font;
        }else{
            File file = new java.io.File(contextUrl + "font/s.ttf");
            FileInputStream fi = null;
            java.io.BufferedInputStream fb = null;
            try {
                fi = new FileInputStream(file);
                fb = new java.io.BufferedInputStream(fi);
                font = Font.createFont(Font.TRUETYPE_FONT, fb);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if(fb!=null){
                    try {
                        fb.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fi!=null){
                    try {
                        fi.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return font;
            }
        }
    }

    /**
     * 图片中加上汉字
     *
     * @return
     */
    public static BufferedImage ImgYin(java.util.List<InviCardDto> inviCardList , BufferedImage bufferedImage ,
                                       String contextUrl,int imgWidth,int hight) {
        Font f = getFont(contextUrl);
        try {
            int wideth = bufferedImage.getWidth(null);
            int height = bufferedImage.getHeight(null);
            for(InviCardDto inviCard:inviCardList){
                Graphics2D graphics = (Graphics2D)bufferedImage.createGraphics();
                graphics.setFont(f.deriveFont(Font.PLAIN, Integer.valueOf(inviCard.getFontSize())));
                graphics.drawImage(bufferedImage, 0, 0, wideth, height, null);
                graphics.setColor(inviCard.getBuffColor());
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int xWidth = Integer.valueOf(inviCard.getXy().split(",")[0]);
                if(1 == inviCard.getIsXCrenter()){ //需要x方向居中
                    FontMetrics fm = graphics.getFontMetrics();
                    int allFontWidth = fm.stringWidth(inviCard.getName());
                    int allFontLength = inviCard.getName().length();
                    if(inviCard.getIsWrap() == 1){ //需要换行
                        if(allFontLength >= 12){
                            for(int i = 0 ; i< (allFontLength/12)+1 ; i++){
                                int end = 12 * (i +1);
                                if(end>=allFontLength){
                                    end = allFontLength;
                                }
                                String font = inviCard.getName().substring(12*i, end);
                                int fontWidth = fm.stringWidth(font);
                                xWidth = imgWidth -  fontWidth/2; //文字的中心到文字边的距离
                                if(i != 0){
                                    Graphics grap = bufferedImage.createGraphics();
                                    grap.setFont(f.deriveFont(Font.PLAIN , Integer.valueOf(inviCard.getFontSize())));
                                    grap.drawImage(bufferedImage, 0, 0, wideth, height, null);
                                    grap.setColor(inviCard.getBuffColor());
                                    grap.drawString(font, xWidth, Integer.valueOf(inviCard.getXy().split(",")[1]) + hight * i);
                                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                    grap.dispose();
                                }else {
                                    graphics.drawString(font,
                                            xWidth,
                                            Integer.valueOf(inviCard.getXy().split(",")[1]));
                                    graphics.dispose();
                                }
                            }
                        }else{
                            xWidth = imgWidth -  allFontWidth/2; //文字的中心到文字边的距离
                            graphics.drawString(inviCard.getName(), xWidth, Integer.valueOf(inviCard.getXy().split(",")[1]));
                            graphics.dispose();
                        }
                    }else{
                        xWidth = imgWidth -  allFontWidth/2; //文字的中心到文字边的距离
                        graphics.drawString(inviCard.getName(), xWidth, Integer.valueOf(inviCard.getXy().split(",")[1]));
                        graphics.dispose();
                    }
                }else{
                    graphics.drawString(inviCard.getName(), xWidth, Integer.valueOf(inviCard.getXy().split(",")[1]));
                    graphics.dispose();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bufferedImage;
    }

    /**
     *  生成二维码
     * @param address 分享地址
     */
    public static BitMatrix buildQRCode(String address){
        InputStream inStream = null;
        BitMatrix bitMatrix = null;
        int width = 440; // 图像宽度
        int height = 440; // 图像高度
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            bitMatrix = new MultiFormatWriter().encode(address,
                    BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        } catch (WriterException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bitMatrix;
    }
    public static void main(String[] args) throws ImageProcessingException, IOException {
        BufferedImage backImage = ImageIO.read(new File("d:\\a4b79bd4bf7d48b295e1339a7f266147.jpg"));
        float[] matrix = new float[400];
        for (int i = 0; i < 400; i++)
            matrix[i] = 1.0f/400.0f;

        BufferedImageOp op = new ConvolveOp( new Kernel(20, 20, matrix), ConvolveOp.EDGE_NO_OP, null );
        BufferedImage img2 =  backImage.getSubimage(0 , 0 , backImage.getWidth() , backImage.getHeight());

        op.filter(backImage , img2);
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img2, "jpeg", new File("d:\\11.jpg"));
    }

    public static int getOrientation(String orientation)
    {
        int tag = 0;
        if ("Top, left side (Horizontal / normal)".equalsIgnoreCase(orientation)) {
        tag = 1;
    } else if ("Top, right side (Mirror horizontal)".equalsIgnoreCase(orientation)) {
        tag = 2;
    } else if ("Bottom, right side (Rotate 180)".equalsIgnoreCase(orientation)) {
        tag = 3;
    } else if ("Bottom, left side (Mirror vertical)".equalsIgnoreCase(orientation)) {
        tag = 4;
    } else if ("Left side, top (Mirror horizontal and rotate 270 CW)".equalsIgnoreCase(orientation)) {
        tag = 5;
    } else if ("Right side, top (Rotate 90 CW)".equalsIgnoreCase(orientation)) {
        tag = 6;
    } else if ("Right side, bottom (Mirror horizontal and rotate 90 CW)".equalsIgnoreCase(orientation)) {
        tag = 7;
    } else if ("Left side, bottom (Rotate 270 CW)".equalsIgnoreCase(orientation)) {
        tag = 8;
    }
        return  tag;
    }

    /**
     * 读取Image对象并转为BufferedImage
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
}
