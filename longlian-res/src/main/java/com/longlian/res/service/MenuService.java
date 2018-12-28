package com.longlian.res.service;

import java.util.List;

import com.longlian.model.MRes;

public interface MenuService {
    /**
     * 删除redis里的菜单
     */
    public void delAll();

    /**
     * 加载除了id的所有菜单
     * @param id
     * @return
     */
    public List getAllMenus(long id);
}
