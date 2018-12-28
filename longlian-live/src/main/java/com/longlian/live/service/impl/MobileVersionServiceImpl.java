package com.longlian.live.service.impl;


import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.dto.UserAgent;
import com.longlian.live.dao.MobileVersionMapper;
import com.longlian.live.service.MobileVersionService;
import com.longlian.model.MobileVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lh on 2016/5/26.
 */
@Service("mobileVersionService")
public class MobileVersionServiceImpl implements MobileVersionService {

    @Autowired
	MobileVersionMapper mobileVersionMapper;
    
	@Override
    @Transactional(readOnly = true)
	public MobileVersion selectMaxVersion(String versionType, MobileVersion superVersion) {
		if (superVersion == null) {
			return null;
		}

		List<MobileVersion> list = mobileVersionMapper.selectMaxVersionList(versionType);

		List<MobileVersion> newList = new ArrayList<>();
		//去掉比最大大的
		for (MobileVersion v : list) {
			if (!Utility.isGreaterThan(v.getVersionNum() , superVersion.getVersionNum())) {
				newList.add(v);
			}
		}
		//排序
		Collections.sort(newList , new Comparator<MobileVersion>() {
			public int compare(MobileVersion o1, MobileVersion o2) {
				String max = o1.getVersionNum();
				String min = o2.getVersionNum();

				if (!Utility.isGreaterThan(max ,  min)) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		//取最大的一个
		return newList.size() > 0 ? newList.get(0) : null;
	}
	@Override
    @Transactional(readOnly = true)
	 public MobileVersion getSuperVersion(String versionType){
		  return mobileVersionMapper.getSuperVersion(versionType);
	  }

	@Override
	public Boolean compareVersion(String v1) {
		if(StringUtils.isEmpty(v1)){
			return false;
		}
		String v1New = v1.replace(".", "");
		if(Long.valueOf(v1New) >= Long.valueOf(140)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isHaveRecordedCourse(HttpServletRequest request ) {
	 	String v1 = request.getParameter("v");
		if(UserAgentUtil.isWechatClient(request)){
			return true;
		}

		if(StringUtils.isEmpty(v1)){
			return false;
		}

		String v1New = v1.replace(".", "");
		if(Long.valueOf(v1New) >= Long.valueOf(143)){
			return true;
		}
		return false;
	}

	@Override
	public boolean lessThan160(HttpServletRequest request) {
		String v1 = request.getParameter("v");
		if ("1.6.0".equals(v1)) {
			return false;
		}
		//v1<1.6.0
		if (!Utility.isGreaterThan(v1,"1.6.0" )) {
			return true;
		}
		return false;
	}
}
