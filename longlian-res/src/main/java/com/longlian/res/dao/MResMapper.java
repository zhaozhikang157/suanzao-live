package com.longlian.res.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.MRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MResMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MRes record);

    int insertSelective(MRes record);

    MRes selectByPrimaryKey(Long id);
    
    List<MRes> selectByType(String type);
    
    List<MRes> selectByRes(@Param("ids") String ids, @Param("type") String type);

    int updateByPrimaryKeySelective(MRes record);

    int updateByPrimaryKey(MRes record);

    List find(long id);

    /**
     * 查找所有的角色
     * @param page
     * @param mRes
     * @return
     */
    List<MRes> getListPage(@Param("page") DatagridRequestModel page, @Param("mRes") MRes mRes);

    /**
     * 查看角色的唯一性
     * @param name
     * @return
     */
    MRes findRoleName(String name);

    /**
     * 删除角色
     * @param ids
     */
    void deleteRoleById(@Param("item") String ids);

    /**
     * 根据角色ID查找对应的菜单
     * @param roleId
     * @return
     */
    List findMenuByRoleId(long roleId);

    void deleteCalatlog(long id);
    
    public List<MRes> findMenuByParent(@Param("mRes") MRes mres);
    
    public void updateMenuSort(MRes mres);

    int deleteById(long id);
    
    public  List<MRes> findResourceIdByUrl(MRes mres);

}
