package com.longlian.console.service.res;

import java.util.List;

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
