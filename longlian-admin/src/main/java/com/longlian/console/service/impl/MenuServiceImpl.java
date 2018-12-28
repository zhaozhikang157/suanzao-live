package com.longlian.console.service.impl;

import com.huaxin.type.MResType;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.MenuService;
import com.longlian.model.MRes;
import com.longlian.console.service.res.ResService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单服务类，初始化放入redis
 * Created by lh on 2016/4/29.
 */
@Component("menuService")
public class MenuServiceImpl implements MenuService , InitializingBean {

    @Autowired
    ResService resService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 初史化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
//        boolean isExistsKey =  redisUtil.exists(RedisKey.ll369_manage_system_menu);//先去redis取
//        if(!isExistsKey){
            redisUtil.del(RedisKey.longlian_console_system_menu);
            try {
                loadList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

       // }
    }

    /**
     * 从longlian-console 调用 ``longlian-res`..法
     */
    private void loadList() {
        List<String> redisList = new ArrayList<String>() ;
        List<MRes> list = resService.getAllResByType(MResType.menu.getType());
        if (list != null && list.size() > 0) {
            for (MRes resource : list){
                redisList.add(JsonUtil.toJson(resource));
            }
            redisUtil.rpushlist(RedisKey.longlian_console_system_menu , redisList);
        }
    }
    /**
     * 从redis里加载菜单
     */
    @Override
    public List<MRes> getList() {
        String menuTime = redisUtil.get(RedisKey.longlian_console_system_menu_time);
        if(StringUtils.isEmpty(menuTime)) menuTime = "";
        String menuUpdateTime = resService.getMenuUpdateTime();  //菜单修改时间
        if(menuTime.equals(menuUpdateTime)){   //没有修改菜单
            boolean isExistsKey =  redisUtil.exists(RedisKey.longlian_console_system_menu);//先去redis取
            if(!isExistsKey){
                loadList();
            }


        }else{
               redisUtil.del(RedisKey.longlian_console_system_menu);
            if(menuUpdateTime!=null){
                redisUtil.set(RedisKey.longlian_console_system_menu_time,menuUpdateTime);
            }
            loadList();
        }
        List<String> all = redisUtil.lrangeall(RedisKey.longlian_console_system_menu);
        List res = new ArrayList();
        if (all != null && all.size() > 0) {
            for (String str : all) {
                MRes r = JsonUtil.getObject(str, MRes.class);
                res.add(r);
            }
        }
        return res;
    }
    /**
     * 根据id加载菜单
     */
    @Override
    public List<MRes> getListByIds(String ids) {
        List<MRes> resList = getList();
        List<MRes> result = new ArrayList();
        String[] id = ids.split(",");
        String idChild = null;
        for(int i=0;i<id.length;i++){
            idChild = id[i];
            for (MRes res : resList){
                if(idChild.equals(res.getId() + "")){
                    result.add(res);
                    break;
                }
            }
        }
        return result;
    }
    @Override
    public void delAll() {
        redisUtil.del(RedisKey.longlian_console_system_menu);
    }
    

}
