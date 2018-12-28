package com.longlian.live.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.sun.javafx.iio.ImageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CompressPicUtil {

	private static Logger log = LoggerFactory.getLogger(CompressPicUtil.class);
	/*******************************************************************************
	 * 缩略图类（通用） 本java类能将jpg、bmp、png、gif图片文件，进行等比或非等比的大小转换。 具体使用方法
	 * compressPic(大图片路径,生成小图片路径,大图片文件名,生成小图片文名,生成小图片宽度,生成小图片高度,是否等比缩放(默认为true))
	 */

	private static int outputWidth = 900; // 默认输出图片宽
	private static int outputHeight = 900; // 默认输出图片高


	private static int smallWidth = 400; // 默认输出图片宽
	private static int smallHeight = 400; // 默认输出图片高


	// 图片处理
	public  byte[] compressPic(MultipartFile file , String ext , boolean isSmall ) {
		byte[] bytes = null;
		try {
			bytes = compressPic(file.getInputStream() ,   ext , isSmall );
			if (bytes == null) {
				return file.getBytes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	// 图片处理
	public  byte[] compressPic(MultipartFile file , String ext ) {
		return compressPic(file ,   ext , false);
	}

	// 图片处理
	public  byte[] compressPic(InputStream inputIo , String ext , boolean isSmall ) {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes = null;
		try {
			//bytes = file.getBytes();
			// 获得源文件
			Image img = ImageIO.read(inputIo);

			outputStream = new ByteArrayOutputStream();


			Metadata metadata = null;
			int orientation = -1;
			try {
				metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(inputIo),true);

				if (metadata != null) {
					Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
					if (directory != null) {
						try {
							orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
							log.info("图片orientation={}", orientation);
						} catch (MetadataException me) {
							log.info("图片orientation={}", orientation);
						}
					}
				}
			} catch (ImageProcessingException e) {
				//e.printStackTrace();
				log.error("读取图片属性报错",e);
			}

			int outputWidth = CompressPicUtil.outputWidth;
			int outputHeight = CompressPicUtil.outputHeight;
			int scal = Image.SCALE_SMOOTH;

			if (!isSmall) {
				if (img.getHeight(null) > outputHeight) {
					outputHeight = img.getHeight(null);
					outputWidth = outputHeight;
				}
			} else {
				if (img.getHeight(null) > img.getWidth(null) + 1024) {
					outputWidth = 800;
					outputHeight = 800;
				} else {
					outputWidth = 200;
					outputHeight = 200;
				}
				scal = Image.SCALE_FAST;
			}

			// 判断是否是等比缩放
			// 为等比缩放计算输出的图片宽度及高度
			double rate1 = ((double) img.getWidth(null))
					/ (double) outputWidth + 0.1;
			double rate2 = ((double) img.getHeight(null))
					/ (double) outputHeight + 0.1;
			// 根据缩放比率大的进行缩放控制
			double rate = rate1 > rate2 ? rate1 : rate2;
			int newWidth = (int) (((double) img.getWidth(null)) / rate);
			int newHeight = (int) (((double) img.getHeight(null)) / rate);

			BufferedImage tag = new BufferedImage((int) newWidth,
					(int) newHeight, BufferedImage.TYPE_INT_RGB);

			/*
			 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
			 */
			tag.getGraphics().drawImage(
					img.getScaledInstance(newWidth, newHeight,
							scal), 0, 0, null);
			if (orientation != 6) {
				ImageIO.write(tag , ext,outputStream);
			} else {
				BufferedImage des = RotateImage.Rotate(tag, 90);
				ImageIO.write(des , ext,outputStream);
			}
			bytes = outputStream.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}

	public static void main(String[] args) throws FileNotFoundException {
		testBase64ToUrl();
	}
	public static void testBase64ToUrl() throws FileNotFoundException {
		CompressPicUtil compressPicUtil = new CompressPicUtil();
		File file = new File("d://IMG_8518.JPG");
		System.out.println(file.length());
		System.out.println(1024 * 100);
		//byte[] compressPic = compressPicUtil.compressPic(new FileInputStream(file), "JPG");

		//byte2image(compressPic , "d://IMG_85182.JPG");
	}

	public static void byte2image(byte[] data,String path){
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
