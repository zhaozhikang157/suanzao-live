package com.longlian.live.util;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.UUIDGenerator;
import com.longlian.model.StoreFile;
import com.longlian.type.OssBucket;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.Date;

/**
 * 文件上传工具类
 * @author Administrator
 *存储路径：/项目名/upload(配置文件可指定)/module(表名)/年/月/uuid.后缀
 */
@Service("storeFileUtilLonglian")
public class StoreFileUtil {
  @Value("${uploadFile.dir:upload}")
  private String uploadFile ;
  
  @Autowired
  private LonglianSsoUtil longlianSsoUtil;
  /**
   * 保存文件
   * @param file
   * @param storeFile
   * @return
   * @throws Exception
   */
  public String saveFile(MultipartFile file , StoreFile storeFile) throws Exception{
	return saveFile(  file ,  storeFile ,  null); 
  }
  /**
   * 保存文件
   * @param file
   * @param storeFile
   * @param useOriginName 1-应用来的名字上传
   * @return
   * @throws Exception
   */
  public String saveFile(MultipartFile file , StoreFile storeFile , String useOriginName) throws Exception{
    return saveFile(file.getBytes(),   storeFile  , useOriginName);
  }
  
  /**
   * 保存文件
   * @param bytes
   * @param storeFile
   * @param useOriginName 1-应用来的名字上传
   * @return
   * @throws Exception
   */
  public String saveFile(byte[] bytes, StoreFile storeFile , String useOriginName , OssBucket ossBucket) throws Exception{

      Date date  = storeFile.getCreateTime();
      String yearAndMonth = DateUtils.formatDate(date, "yyyy/MM");
      String uploadFilePath =  uploadFile + "/";

      if (StringUtils.isNotEmpty(storeFile.getBasePath())) {
          uploadFilePath = storeFile.getBasePath() + "/";
      }


      String path = uploadFilePath ;
      if (!StringUtils.isEmpty(storeFile.getModule())) {
          path +=  storeFile.getModule() + "/";
      }
      path += yearAndMonth;
      String ext = getExtensionName(storeFile.getName());
      storeFile.setType(ext);
      if (!"1".equals(useOriginName)) {
          path +=  "/" +  UUIDGenerator.generate() + "." + ext;
      } else {
          path +=  "/" + storeFile.getName();
      }
      
      String url = longlianSsoUtil.putObject(path, bytes , ossBucket);
      return url;
  }

    /**
     * 保存文件
     * @param bytes
     * @param storeFile
     * @param useOriginName 1-应用来的名字上传
     * @return
     * @throws Exception
     */
    public String saveFile(byte[] bytes, StoreFile storeFile , String useOriginName ) throws Exception{

        OssBucket bucket = null;
        if (StringUtil.isNotEmpty(storeFile.getBucket())) {
            bucket = OssBucket.getOssBucketByName(storeFile.getBucket());
        }
        if (bucket == null) {
            bucket = OssBucket.longlian_live;
        }

        return saveFile(  bytes,   storeFile ,   useOriginName , bucket) ;
    }
  
    /**
     * 保存文件
     * @param bytes
     * @param storeFile
     * @return
     * @throws Exception
     */
    public String saveFile(byte[] bytes, StoreFile storeFile) throws Exception{
        return saveFile( bytes,   storeFile ,null);
    }
  
  /*
   * Java文件操作 获取文件扩展名
   *
   */
  public static String getExtensionName(String filename) { 
      if ((filename != null) && (filename.length() > 0)) { 
          int dot = filename.lastIndexOf('.'); 
          if ((dot >-1) && (dot < (filename.length() - 1))) { 
              return filename.substring(dot + 1); 
          } 
      } 
      return filename; 
  }
}
