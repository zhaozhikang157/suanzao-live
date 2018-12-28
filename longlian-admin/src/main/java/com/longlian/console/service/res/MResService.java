package com.longlian.console.service.res;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.ztree.ZTreeModel;
import com.longlian.model.MRes;

import java.util.List;

/**
 * Created by admin on 2016/5/11.
 */
public interface MResService {

    /**
     * 查看所有的角色
     * @param page
     * @param mRes
     * @return
     */
    DatagridResponseModel getListPage(DatagridRequestModel page, MRes mRes);

    /**
     * 角色修改添加保存
     * @param mRes
     */
    void saveRole(MRes mRes);

    /**
     * 根据角色名称查看
     * @param roleName
     * @return
     */
    MRes findRoelName(String roleName);

    /**
     * 根据id去查看角色
     * @param id
     * @return
     */
    MRes findById(long id);

    /**
     * 删除角色
     * @param ids
     */
    void deleteByIds(String ids);

    /**
     * 查看所有菜单
     * @return
     */
    List<ZTreeModel> findALLmenu(long roleId);

    void saveRoleIdResId(String Resids, long roleId);

    /**
     * 菜单管理-树结构
     * @return
     */
    List<ZTreeModel> getAllCatalog();

    /**
     * 添加修改返回树状
     * @param mRes
     * @param parentId
     * @param userId
     * @return
     */
    ZTreeModel saveCatalog(MRes mRes, long parentId, long userId);

    /**
     * 修改菜单名字
     * @param id
     * @param name
     */
    void updateCatalogname(long id, String name);

    void deleteCalatlog(long id);
    
    public List<ZTreeModel> findMenuByParent(MRes mres);
    
    public void saveSortMenu(String param);

    List find(long id);

    boolean deleteById(long id);
    
    public  List<MRes> findResourceIdByUrl(MRes mres);


}
