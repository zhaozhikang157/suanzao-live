package com.longlian.live.service;

import com.longlian.model.RenBenUser;
import org.apache.ibatis.annotations.Param;

/**
 * Created by liuhan on 2018-05-30.
 */
public interface RenBenService {
    /** 插入人本录入用户 */
    int insertRenBenUser(RenBenUser user);
    /** 判断手机号是否存在 */
    int findRenBenUserByPhone(String phone);
}
