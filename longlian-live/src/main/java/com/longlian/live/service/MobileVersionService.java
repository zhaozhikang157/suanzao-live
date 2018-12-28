package com.longlian.live.service;


import com.longlian.model.MobileVersion;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lh on 2016/5/26.
 */
public interface MobileVersionService {
    
    MobileVersion selectMaxVersion(String versionType , MobileVersion superVersion);
	
	 public MobileVersion getSuperVersion(String versionType);

    Boolean compareVersion(String v1);

    boolean isHaveRecordedCourse(HttpServletRequest request );

    boolean lessThan160(HttpServletRequest request);
}
