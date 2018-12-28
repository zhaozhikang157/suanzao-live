package com.longlian.live.service.impl;

import com.huaxin.util.Utility;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.newdao.FuncMapper;
import com.longlian.live.service.FuncService;
import com.longlian.model.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/21.
 */
@Component("funcService")
public class FuncServiceImpl implements FuncService{

    @Autowired
    FuncMapper funcMapper;
    
    @Override
    public  List<Map> getFuncListPage(DatagridRequestModel datagridRequestModel,Map map){
        return  funcMapper.getFuncListPage(datagridRequestModel,map);
    }

    @Override
    public  Func findModelById(Long id){
        return funcMapper.selectByPrimaryKey(id);
    }
    @Override
    public void  doSaveAndUpdate(Func func) throws Exception{
        if(null == func.getId()){    //保存
            funcMapper.insert(func);
        }else{      //修改
            funcMapper.updateByPrimaryKeySelective(func);
        }
    }
    @Override
    public boolean isExistFunc(String funcCode){
        boolean bool = false;     
       if(funcMapper.isExistFunc(funcCode)>0){
           bool = true;
       }
        return bool;
    }
    @Override
    public void deleteById(Long id) throws Exception{
        funcMapper.deleteById(id);
    }
}
