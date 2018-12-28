package com.longlian.live.service.impl;


import com.longlian.live.dao.StoreFileMapper;
import com.longlian.live.service.StoreFileService;
import com.longlian.live.util.StoreFileUtil;
import com.longlian.model.StoreFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("storeFileService")
public class StoreFileServiceImpl implements StoreFileService {
  
  @Autowired
  private StoreFileMapper storeFileMapper;
  
  @Autowired
  private StoreFileUtil storeFileUtilLonglian;

  private int save(StoreFile storeFile) {
    return storeFileMapper.insert(storeFile);
  }

  /**
   * 文件上传
   * @return
   * @throws Exception 
   */
  public StoreFile doUploadFile(byte[] bytes , StoreFile  storeFile ) throws Exception {
      String url=null;
      try {
        url = storeFileUtilLonglian.saveFile(bytes, storeFile);
      } catch (Exception e) {
         throw e;
      }
      if(url!=null){
        storeFile.setUrl(url);
        this.save(storeFile);
      }
      return storeFile;
  }
 
}
