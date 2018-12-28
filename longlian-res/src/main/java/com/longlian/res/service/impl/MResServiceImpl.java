package com.longlian.res.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huaxin.type.MResType;
import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.ztree.ZTreeModel;
import com.longlian.model.MRes;
import com.longlian.model.MResRel;
import com.longlian.model.MUser;
import com.longlian.res.dao.MResMapper;
import com.longlian.res.dao.MResRelMapper;
import com.longlian.res.dao.MUserMapper;
import com.longlian.res.service.MResService;

/**
 * Created by wtc on 2016/5/11.
 */
@Service("MResService")
public class MResServiceImpl implements MResService{

    @Autowired
    MResMapper mResMapper;
    @Autowired
    MResRelMapper mResRelMapper;
    @Autowired
    MUserMapper mUserMapper;

    /**
     * 查看所有正常的角色
     * @param page
     * @param mRes
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public DatagridResponseModel getListPage(DatagridRequestModel page, MRes mRes) {
        mRes.setType(MResType.role.getType());
        List<MRes> listPage = mResMapper.getListPage(page, mRes);
        DatagridResponseModel responseModel = new DatagridResponseModel();
        responseModel.setRows(listPage);
        responseModel.setTotal(page.getTotal());
        return responseModel;
    }

    /**
     * 保存修改角色
     * @param mRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(MRes mRes) {
        if(mRes.getId()==0){    //保存
            mRes.setUrl("");
            mRes.setSort(0);
            mRes.setParentId(0L);
            mRes.setType(MResType.role.getType());
            mRes.setResPic("");
            mResMapper.insert(mRes);
        }else{      //修改
            mResMapper.updateByPrimaryKeySelective(mRes);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MRes findRoelName(String roleName) {
        return mResMapper.findRoleName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public MRes findById(long id) {
        return mResMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(String ids) {
        mResMapper.deleteRoleById(ids);
    }

    /**
     * 加载权限树
     * @param roleId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ZTreeModel> findALLmenu(long roleId) {
        List selectedRes = mResMapper.findMenuByRoleId(roleId);
        String type = MResType.menu.getType();
        List<MRes> listRes = mResMapper.selectByType(type);
        List<ZTreeModel> zTreeModelList = new ArrayList();
        for(MRes m:listRes){
            boolean isSelect = false;
            if(selectedRes.contains(m.getId())){
                isSelect = true;
            }
            ZTreeModel zTreeModel = new ZTreeModel(m.getId() + "", m.getName(), true, m.getParentId() + "",
                    true, "", m.getName() , false , isSelect);
            zTreeModelList.add(zTreeModel);
        }
        return zTreeModelList;
    }

    /**
     * 在资源角色表中添加数据
     * @param resIds
     * @param roleId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleIdResId(String resIds ,long roleId) {
        mResRelMapper.deleteByRelId(roleId);
        String [] ids = resIds.split(",");
        for(int i = 0 ;i<ids.length;i++){
            String id = ids[i];
            MResRel mResRel = new MResRel();
            mResRel.setResId(roleId);
            try {
                mResRel.setRelId(Utility.parseLong(id));
                mResRelMapper.insert(mResRel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取所有的菜单
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ZTreeModel> getAllCatalog() {
        List<MRes> listRes = mResMapper.selectByType(MResType.menu.getType());
        List<ZTreeModel> zTreeModelList = new ArrayList();
        for (MRes mRes : listRes) {
            ZTreeModel zTreeModel = new ZTreeModel(mRes.getId() + "", mRes.getName(), true, mRes.getParentId() + "",
                    true, "", mRes.getName());
            zTreeModelList.add(zTreeModel);
        }
        /*添加根节点*/
        ZTreeModel zTreeModel = new ZTreeModel("0", "根节点", true,"-1",
                true, "","根节点");
        zTreeModelList.add(zTreeModel);
        return zTreeModelList;
    }
    
    /**
     * 获取所有的菜单
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ZTreeModel> findMenuByParent(MRes mres) {
        List<MRes> listRes = mResMapper.findMenuByParent(mres);
        List<ZTreeModel> zTreeModelList = new ArrayList();
        for (MRes mRes : listRes) {
            ZTreeModel zTreeModel = new ZTreeModel(mRes.getId() + "", mRes.getName(), true, mRes.getParentId() + "",
                    true, "", mRes.getName());
            zTreeModelList.add(zTreeModel);
        }
        return zTreeModelList;
    }


    /**
     * 保存修改添加菜单管理
     * @param mRes
     * @param parentId
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ZTreeModel saveCatalog(MRes mRes , long parentId , long userId) {
        if(parentId==0){
            mRes.setSort(0);    //父节点
        }else{
            mRes.setSort(1);    //子节点
        }
        if(mRes.getId()==0){    //添加
            mRes.setResPic("");
            mRes.setType(MResType.menu.getType());
            mRes.setDescription("");
            mRes.setParentId(parentId);
            mResMapper.insert(mRes);
//            createMresRel(mRes.getId(),userId);//插入中间表
        }else{
            mResMapper.updateByPrimaryKeySelective(mRes);
        }
        ZTreeModel zTreeModel = new ZTreeModel(mRes.getId() + "", mRes.getName(), true, mRes.getParentId() + "",
                false, "", mRes.getName());
        return zTreeModel;
    }

    /**
     * 修改菜单名字
     * @param id
     * @param name
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCatalogname(long id, String name) {
        MRes mRes = new MRes();
        mRes.setId(id);
        mRes.setName(name);
        mResMapper.updateByPrimaryKeySelective(mRes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCalatlog(long id) {
        mResMapper.deleteCalatlog(id);
    }


    /**
     * 保存菜单时,添加中间表
     * @param relId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void createMresRel(long relId,long userId){
        MUser mUser = mUserMapper.selectByPrimaryKey(userId);
        long resId = mUser.getResId();                               //查询该用户在mRes表中所对应的ID
        long roleId = mResRelMapper.findRelId(resId).getRelId();    //根据ID,查看所对应的角色ID
        MResRel mResRel = new MResRel();
        mResRel.setRelId(relId);
        mResRel.setResId(roleId);
        mResRelMapper.insert(mResRel);
    }
    
    public void saveSortMenu(String param){
    	//param 格式： id+"#"+sort+","
    	List<MRes> list = new ArrayList<MRes>();
    	String[] array = param.split(",");
    	for(int i =0;i < array.length;i++){
    		String[] idAndSorts = array[i].split("#");
    		MRes mres = new MRes();
    		String id = idAndSorts[0];
    		String sort = idAndSorts[1];
    		mres.setId(Long.valueOf(id));
    		mres.setSort(Integer.valueOf(sort));
    		list.add(mres);
    		mResMapper.updateMenuSort(mres);
    		//最后一个逗号不解析，直接跳出循环
    		if(i == array.length - 1){
    			break;
    		}
    	}
    }

    @Override
    @Transactional(readOnly = true)
    public List find(long id) {
        return mResMapper.find(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(long id) {
        mResRelMapper.deleteByRelId(id);
        if (mResMapper.deleteById(id)>0){return true;}else {return false;}
    }
    
    /**
     * 
    * @Title: findResourceIdByUrl
    * @Description: TODO(根据url查询id)
    * @param mres
    * @return
     */
    public  List<MRes> findResourceIdByUrl(MRes mres){
    	return mResMapper.findResourceIdByUrl(mres);
    }

}
