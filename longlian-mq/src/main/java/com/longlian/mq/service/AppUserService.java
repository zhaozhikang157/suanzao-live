package com.longlian.mq.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.model.AppUser;
import com.longlian.token.AppUserIdentity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserService {

    public Map getNames(List<Long> ids );

    String getOpenidById(long id);

    public Map getNameAndPhoto(Long id);
    AppUser getById(long id);
}
