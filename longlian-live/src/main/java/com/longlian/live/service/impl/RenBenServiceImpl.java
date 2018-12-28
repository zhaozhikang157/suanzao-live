package com.longlian.live.service.impl;

import com.longlian.live.dao.RenBenMapper;
import com.longlian.live.service.RenBenService;
import com.longlian.model.RenBenUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhan on 2018-05-30.
 */
@Service
public class RenBenServiceImpl implements RenBenService {
    @Autowired
    private RenBenMapper renBenMapper;
    /**
     * 插入人本录入用户
     *
     * @param user
     */
    @Override
    public int insertRenBenUser(RenBenUser user) {
        return renBenMapper.insertRenBenUser(user);
    }

    /**
     * 判断手机号是否存在
     *
     * @param phone
     */
    @Override
    public int findRenBenUserByPhone(String phone) {
        return renBenMapper.findRenBenUserByPhone(phone);
    }
}
