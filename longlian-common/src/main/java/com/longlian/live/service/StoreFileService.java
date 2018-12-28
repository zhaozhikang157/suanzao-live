package com.longlian.live.service;

import com.longlian.model.StoreFile;


public interface StoreFileService {
  
  StoreFile doUploadFile(byte[] bytes, StoreFile storeFile) throws Exception;
}
