package com.longlian.live.dao;

import org.apache.ibatis.annotations.Param;

/**
 * Created by admin on 2017/2/14.
 */
public interface SystemAdminMapper {
    /** 查询管理员用户是否存在 */
    int findSystemAdminByUserId(@Param("adminId") Long adminId);
}
