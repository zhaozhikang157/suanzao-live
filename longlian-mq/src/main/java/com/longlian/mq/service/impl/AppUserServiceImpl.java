package com.longlian.mq.service.impl;

import com.google.zxing.common.BitMatrix;
import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.InviCardDto;
import com.longlian.model.*;
import com.longlian.mq.dao.AppUserMapper;
import com.longlian.mq.service.AppUserService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuhan on 2017/2/8.
 */
@Service("appUserService")
public class AppUserServiceImpl implements AppUserService {


    @Autowired
    AppUserMapper appUserMapper;

    @Override
    public Map getNames(List<Long> ids) {
        Map res = new HashMap();
        List<AppUser> list = appUserMapper.getNames(ids);
        for (AppUser m : list) {
            res.put(m.getId(),m.getName());
        }

        return res;
    }

    /**
     * 根据ID 获取微信号
     * @param id
     * @return
     */
    @Override
    public String getOpenidById(long id) {
        return getOpenidById(id);
    }

    @Override
    public Map getNameAndPhoto(Long id) {
        Map map = appUserMapper.getNameAndPhoto(id);
        return map;
    }

    @Override
    public AppUser getById(long id) {
        return appUserMapper.selectByPrimaryKey(id);
    }
}
