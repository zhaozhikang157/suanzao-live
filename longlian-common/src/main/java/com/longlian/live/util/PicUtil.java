package com.longlian.live.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.longlian.model.StoreFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.binary.Base64;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class PicUtil {
	@Autowired
	private StoreFileUtil storeFileUtil;
	private static Logger log = LoggerFactory.getLogger(PicUtil.class);

	/**
	 *
	 * @param base64
	 * @return
	 */
	public  String base64ToUrl(String base64 ) {
		String upper = base64.toUpperCase();
		 if (upper.startsWith("HTTP://") || upper.startsWith("HTTPS://")) {
		 	return base64;
		 }

		try {
			int index = base64.indexOf(",") + 1;
		 	String info = base64.substring(0 ,index );
		 	String image = base64.substring(index);
			log.info(info);
			log.info(image);
			byte[] bytes = this.generateImage(image);
			String md5 = DigestUtils.md5DigestAsHex(bytes);
			StoreFile storeFile  = new  StoreFile();
			storeFile.setSize((long)bytes.length);
			storeFile.setCreateTime(new Date());
			storeFile.setModule("");
			storeFile.setName(md5 + "." + getExt(info));
			storeFile.setMd5(md5);

			String url = storeFileUtil.saveFile(bytes , storeFile);
			return url;
		} catch (Exception e) {
			log.error("转图片报错:", e);
		}
		 return "";
	}
	private String getExt(String info) {
		info = info.substring(info.indexOf("/") + 1 , info.indexOf(";"));
		return info;
	}

	/**
	 * @Description: 将base64编码字符串转换为图片
	 * @Author:
	 * @CreateTime:
	 * @param imgStr base64编码字符串
	 * @return
	 */
	private byte[] generateImage(String imgStr) {
		try {
			byte[] b = Base64.decodeBase64(imgStr);

			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			return b;
		} catch (Exception e) {
			log.error("转图片报错:", e);
		}
		return null;
	}

	public static void main(String args[]) {
		String base64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAyAAAAHCCAYAAAAXY63IAAAgAElEQVR4Xuy9B5sc2XElej";
		String info = base64.substring(0 , base64.indexOf(",") + 1);
		String image = base64.substring(base64.indexOf(",") + 1);
		System.out.println(info);
		System.out.println(image);
		System.out.println( info.substring(info.indexOf("/") + 1, info.indexOf(";")));

	}

}
