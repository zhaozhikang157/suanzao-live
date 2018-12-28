package com.longlian.res.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.ztree.ZTreeModel;
import com.longlian.model.MRes;
import com.longlian.res.service.MResService;
import com.longlian.res.service.MenuService;
import com.longlian.token.ConsoleUserIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by pangchao on 2017/1/20.
 */
@RequestMapping("/mRes")
@Controller
public class MResController {

    @Autowired
    MResService mResService;

    @Autowired
    MenuService menuService;

    private Long parentId;

    /**
     * 角色管理界面
     * @return
     */
    @RequestMapping()
    public ModelAndView showView(){
        return new ModelAndView("/func/role/index");
    }
    /**
     *查看所有的角色
     * @param requestModel mRes
     * @return
     */
    @RequestMapping("/findAllRole")
    @ResponseBody
    public DatagridResponseModel findAllRole(DatagridRequestModel requestModel , MRes mRes){
        return mResService.getListPage(requestModel, mRes);
    }

    /**
     * 添加编辑页面
     * @param id
     * @return
     */
    @RequestMapping("/toAddOrUpdate")
    public ModelAndView toAddOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/role/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 根据id查询角色
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById" , method = RequestMethod.GET)
    @ResponseBody
    public ActResult findById(Long id){
        ActResult actResult = new ActResult();
        MRes mRes =mResService.findById(id);
        if(mRes==null) {
            actResult.setSuccess(false);
        }else{
            actResult.setData(mRes);
        }
        return actResult;
    }


    /**
     * 添加或者修改保存
     * @param mRes
     * @return
     */
    @RequestMapping(value = "/saveRole" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveRole(@RequestBody MRes mRes){
        ActResult actResult = new ActResult();
        mResService.saveRole(mRes);
        return actResult;
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteById(long id){
        ActResult actResult = new ActResult();
        List list = mResService.find(id);
        if(list.size()==0){
            actResult.setSuccess(mResService.deleteById(id));
        }
        actResult.setData(list);
        return actResult;
    }

    /**
     * 修改角色状态
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteByIds" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteByIds(String ids){
        ActResult actResult = new ActResult();
        mResService.deleteByIds(ids);
        return actResult;
    }

    /**
     * 根据角色名称去查询(判断是否有重复的角色)
     * @param roleName
     * @return
     */
    @RequestMapping(value = "/findRoleName" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult findRoleName(String roleName){
        ActResult actResult = new ActResult();
        if(StringUtils.isEmpty(roleName)){
            actResult.setSuccess(false);
            return actResult;
        }
        MRes mRes = mResService.findRoelName(roleName.trim());
        if(mRes==null){
            return actResult;
        }else{
            actResult.setData(mRes.getName());
            actResult.setSuccess(false);
            return actResult;
        }
    }
    /**
     * 菜单管理
     */
    @RequestMapping("/catalog")
    public ModelAndView showCatalog(){
        return new ModelAndView("/func/catalog/index");
    }

    /**
     * 展示所有的菜单结构
     * @return
     */
    @RequestMapping("/getAllCatalog")
    @ResponseBody
    public ActResult getAllCatalog(){
        ActResult actResult = new ActResult();
        List<ZTreeModel> list = mResService.getAllCatalog();
        actResult.setData(list);
        return actResult;
    }
    /**
     * 展示所有的菜单结构
     * @return
     */
    @RequestMapping("/findMenuByParentId")
    @ResponseBody
    public ActResult findMenuByParent(Long id){
        MRes mres = new MRes();
        mres.setType("003");
        mres.setStatus(0);
        mres.setParentId(id);
        ActResult actResult = new ActResult();
        List<ZTreeModel> list = mResService.findMenuByParent(mres);
        actResult.setData(list);
        return actResult;
    }

    /**
     * 保存排序后的菜单顺序
     * @return
     */
    @RequestMapping("/saveSortMenu")
    @ResponseBody
    public ActResult saveSortMenu(String parm){
        //字符串格式：id+"|"+sort
        ActResult actResult = new ActResult();
        mResService.saveSortMenu(parm);
        actResult.setSuccess(true);
        //清除redis菜单缓存(修改redisKey的时间)
        menuService.delAll();
        return actResult;
    }
    /**
     * 修改菜单
     * @param id pId
     * @return
     */
    @RequestMapping("/createOrUpdateCatalog")
    public ModelAndView createOrUpdateCatalog(Long id , Long pId){
        parentId = pId;
        ModelAndView view = new ModelAndView("/func/catalog/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 查询菜单
     * @param id
     * @return
     */
    @RequestMapping(value = "/findCataById" , method = RequestMethod.GET)
    @ResponseBody
    public ActResult findCataById(long id){
        ActResult actResult = new ActResult();
        MRes mRes = mResService.findById(id);
        actResult.setData(mRes);
        return actResult;
    }



    /**
     * 保存菜单
     * @param mRes
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveCatalog" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveCatalog(@RequestBody MRes mRes , HttpServletRequest request){
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        long userId = token.getId();
        ZTreeModel zTreeModel = mResService.saveCatalog(mRes, parentId,userId);
        menuService.delAll();
        ActResult actResult = new ActResult();
        actResult.setData(zTreeModel);
        return actResult;
    }


    /**
     * 删除菜单
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteCalatlog", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteCalatlog(long id){
        ActResult actResult = new ActResult();
        mResService.deleteCalatlog(id);
        menuService.delAll();
        return actResult;
    }

    /**
     * 修改菜单名字
     * @param id
     * @param newName
     * @return
     */
    @RequestMapping(value = "/updateCatalogName" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult updateCatalogName(Long id , String newName){
        ActResult actResult = new ActResult();
        mResService.updateCatalogname(id, newName);
        menuService.delAll();
        return actResult;
    }


    /**
     * 权限管理界面
     * @return
     */
    @RequestMapping("/menu")
    public ModelAndView showIndex(){
        return new ModelAndView("/func/menu/index");
    }

    /**
     *查看所有的角色
     * @param requestModel
     * @param mRes
     * @return
     */
    @RequestMapping("/findMenuAllRole")
    @ResponseBody
    public DatagridResponseModel findMenuAllRole(DatagridRequestModel requestModel , MRes mRes){
        mRes.setStatus(0);
        return mResService.getListPage(requestModel, mRes);
    }

    /**
     * 获取所有的菜单
     * @param roleId
     * @return
     */
    @RequestMapping("/getAllMenu")
    @ResponseBody
    public ActResult getAllMenu(Long roleId){
        ActResult actResult = new ActResult();
        List<ZTreeModel> list = mResService.findALLmenu(roleId);
        actResult.setData(list);
        return actResult;
    }


    /**
     * 根据角色ID获取全部菜单
     * @param id
     * @return
     */
    @RequestMapping(value = "/getMenus")
    @ResponseBody
    public ActResult getAllMenus(long id){
        List list = menuService.getAllMenus(id);
        return ActResult.success(list);
    }

    /**
     * 保存角色的权限
     * @param Resids
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/saveRoleRes" , method = RequestMethod.POST)
    @ResponseBody
    public ActResult saveRoleRes(String Resids,Long roleId){
        ActResult actResult = new ActResult();
        mResService.saveRoleIdResId(Resids, roleId);
        return actResult;
    }


}
