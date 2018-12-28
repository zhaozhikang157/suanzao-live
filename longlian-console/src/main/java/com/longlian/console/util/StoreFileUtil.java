package com.longlian.console.util;

import com.huaxin.util.LL369SsoUtil;
import com.longlian.model.StoreFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 * @author Administrator
 *存储路径：/项目名/upload(配置文件可指定)/module(表名)/年/月/uuid.后缀
 */
@Service("longLianStoreFileUtil")
public class StoreFileUtil {
  @Value("${uploadFile.dir:upload}")
  private String uploadFile ;
  
  @Autowired
  private LL369SsoUtil ll369SsoUtil;
  /**
   * 保存文件
   * @param file
   * @param storeFile
   * @return
   * @throws Exception
   */
  public String saveFile(MultipartFile file , StoreFile storeFile) throws Exception{
	return saveFile(  file ,  storeFile ,  null , null);
  }
  /**
   * 保存文件
   * @param file
   * @param storeFile
   * @param useOriginName 1-应用来的名字上传
   * @return
   * @throws Exception
   */
  public String saveFile(MultipartFile file , StoreFile storeFile , String useOriginName , String basePath) throws Exception{
    return saveFile(file.getBytes(),   storeFile  , useOriginName ,basePath );
  }
  
  /**
   * 保存文件
   * @param bytes
   * @param storeFile
   * @param useOriginName 1-应用来的名字上传
   * @return
   * @throws Exception
   */
  public String saveFile(byte[] bytes, StoreFile storeFile , String useOriginName , String basePath) throws Exception{

      Date date  = storeFile.getCreateTime();
      String yearAndMonth = DateUtils.formatDate(date, "yyyy/MM");
      String uploadFilePath =  (StringUtils.isEmpty(basePath) ? uploadFile : basePath)  + "/";

      String path = uploadFilePath ;
      if (!StringUtils.isEmpty(storeFile.getModule())) {
          path +=  storeFile.getModule() + "/";
      }
      path += yearAndMonth;
      String ext = getExtensionName(storeFile.getName());
      storeFile.setType(ext);
      if (!"1".equals(useOriginName)) {
          path +=  "/" +  generate() + "." + ext;
      } else {
          path +=  "/" + storeFile.getName();
      }
      
      String url = ll369SsoUtil.putObject(path, bytes);
      return url;
  }
  
    /**
     * 保存文件
     * @param bytes
     * @param storeFile
     * @return
     * @throws Exception
     */
    public String saveFile(byte[] bytes, StoreFile storeFile) throws Exception{
        return saveFile( bytes,   storeFile ,null , null);
    }
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
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
