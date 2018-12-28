package com.longlian.live.service.impl;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.newdao.RecoCourseMapper;
import com.longlian.live.service.RecoCourseServive;
import com.longlian.model.RecoCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("recoCourse")
public class RecoCourseServiveImpl implements RecoCourseServive{

    @Autowired
    RecoCourseMapper recoCourseMapper;

  @Override
   public List<Map> getRecoCourseList(DatagridRequestModel dg, Map map){
      return recoCourseMapper.getRecoCourseListPage(dg,map);
  }

    @Override
    public  RecoCourse findModelById(Long id){
      return  recoCourseMapper.getRecoCoursesById(id);
  }

    @Override
    public  void  doSaveAndUpdate(RecoCourse recoCourse) throws Exception{
        if(null == recoCourse.getId()){    //保存
            recoCourse.setCreateTime(new Date());
            recoCourseMapper.insert(recoCourse);
        }else{      //修改
            recoCourseMapper.updateRecoCoursesById(recoCourse);
        }
    }
    @Override
    public    void deleteById(Long id){
        recoCourseMapper.deleteById(id);
    }

    @Override
    public List<Map> getRecoCourses() {
        return recoCourseMapper.getRecoCourses();
    }

    @Override
    public boolean isExistRecoCourse(Long courseId){
        boolean flag = false;
      int count=  recoCourseMapper.isExistRecoCourse(courseId);
      if(count>0){
          flag = true;
      }
      return  flag;
    }
}
