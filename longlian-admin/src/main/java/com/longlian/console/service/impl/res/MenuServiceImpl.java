package com.longlian.console.service.impl.res;

import com.huaxin.type.MResType;
import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.MRes;
import com.longlian.console.service.res.MenuService;
import com.longlian.console.service.res.ResService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统菜单服务类，初始化放入redis
 * Created by lh on 2016/4/29.
 */
@Component("menuvService")
public class MenuServiceImpl implements MenuService  {

    @Autowired
    ResService resService;

    @Autowired
    RedisUtil redisUtil;
     
    private List<MRes> loadList() {
        return resService.getAllResByType(MResType.menu.getType());
    }
    
    private void loadMenuTree(List<MRes> res , int level  , List<MRes> old , long id  , long outId ) {
        for (MRes r : old) {
            if (r.getId() == outId) {
                continue;
            }
            
            if (r.getParentId() == id) {
                String pre = getPreByLevel(level);
                r.setName(pre + r.getName());
                res.add(r);
                loadMenuTree(  res ,   level+1  ,   old , r.getId() , outId );
            }
        }
        return ;
    }
    
    private String getPreByLevel(int level) {
        String str = "|--";
        for (int i = 0; i < level ;i++) {
            str += "--";
        }
        return str;
    }

    @Override
    public void delAll() {
//        redisUtil.del(RedisKey.longlian_res_system_menu);
        redisUtil.set(RedisKey.longlian_res_system_menu_time, DateUtil.format(new Date()));
    }

    @Override
    public List getAllMenus(long id) {
        List<MRes> list =  loadList();
        List<MRes> res = new ArrayList();
        
        MRes r = new MRes();
        r.setId(0l);
        r.setName("根节点");
        res.add(r);
        loadMenuTree(res , 0  , list , 0 , id);
        return res;
    }

}
