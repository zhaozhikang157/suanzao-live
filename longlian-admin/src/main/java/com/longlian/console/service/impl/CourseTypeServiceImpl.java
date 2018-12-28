package com.longlian.console.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.CourseTypeMapper;
import com.longlian.console.service.CourseTypeService;
import com.longlian.model.CourseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by pangchao on 2017/2/13.
 */
@Service("courseTypeService")
public class CourseTypeServiceImpl implements CourseTypeService {
    private static Logger log = LoggerFactory.getLogger(CourseTypeServiceImpl.class);
    @Autowired
    CourseTypeMapper courseTypeMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<CourseType> getList(CourseType courseType) {
        return courseTypeMapper.getList(courseType);
    }

    @Override
    public CourseType findById(long id) {
        return courseTypeMapper.findById(id);
    }

    @Override
    public long doSaveAndUpdate(CourseType courseType) throws Exception  {
        long i=0L;
        if (courseType.getId()>0){
            courseType.setUpdateTime(new Date());
             i = courseTypeMapper.update(courseType);
        }else{
            courseType.setUpdateTime(new Date());
            courseType.setCreateTime(new Date());
            i = courseTypeMapper.insert(courseType);
        }
        resetRedisData();
        return i;
    }

    @Override
    public void toOrder(String ids) throws Exception  {
        List list = new ArrayList();
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            Map map = new HashMap();
            map.put("sortOrder", i + 1);
            map.put("id", id[i]);
            list.add(map);
        }
         courseTypeMapper.toOrder(list);
        resetRedisData();
    }

    @Override
    public void deleteById(long id) throws Exception  {
         courseTypeMapper.deleteById(id);
        resetRedisData();
    }

    /**
     * 重新设置redis 数据  广告首页
     */
    public void resetRedisData(){
        redisUtil.del(RedisKey.ll_live_course_type);
        redisUtil.del(RedisKey.ll_live_course_type_new);
    }

    /**
     * 从redis读取数据
     * @return
     */
    @Override
    public List getCourseType4Redis() {
        List<CourseType> list = null;
       // redisUtil.del(RedisKey.ll_live_course_type);
        Boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_course_type);//先取redis取
        if (isExistsKey) {
            list = getObjList();
            return list;
        }
        //没有从数据库中取值，且存入缓存
        list = courseTypeMapper.getCourseType();
        resetRedisData(list);
        return list;
    }

    /**
     * 将从redis取出的list 字符串 转对象
     *
     * @return
     */
    public List<CourseType> getObjList() {
        List<CourseType> list = new ArrayList<CourseType>();
        List<String> arg = redisUtil.lrangeall(RedisKey.ll_live_course_type);
        if (arg == null) return list;
        for (String temp : arg) {
            if (!Utility.isNullorEmpty(temp)) {
                CourseType courseType = JsonUtil.getObject(temp, CourseType.class);
                list.add(courseType);
            }
        }
        return list;
    }


    /**
     * 重新设置redis 数据
     */
    public void resetRedisData(List<CourseType> list) {
        boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_course_type);//先去redis取
        if (isExistsKey) redisUtil.del(RedisKey.ll_live_course_type);
        List<String> redisList = new ArrayList<String>();
        for (CourseType resource : list) {
            redisList.add(JsonUtil.toJson(resource));
        }
        redisUtil.rpushlist(RedisKey.ll_live_course_type, redisList);
    }

    /**
     * 构建课程类型树
     * @param newList
     * @param oldList
     * @return
     */
    @Override
    public List<CourseType> buildCourseTypeTree(List<CourseType> newList, List<CourseType> oldList) {
        //------
        String str="|--";
        List<CourseType> pList = new ArrayList<>();
        for (CourseType ct: oldList){
            if(ct.getParentId()==0L){
                ct.setName(str+ct.getName());
                pList.add(ct);
            }
        }
        for (CourseType pct:pList){
            loadTree(str,pct,newList,oldList);
        }
        return newList;
    }

    public void loadTree(String str,CourseType pct,List<CourseType> newList,List<CourseType> oldList){
            newList.add(pct);
            for (CourseType sct:oldList){
                if(pct.getId()==sct.getParentId()){   //父元素id ==子元素 父id
                    System.out.print("1:"+pct.getParentId()+"2:"+sct.getId()+(pct.getParentId()==sct.getId()));
                    str+="--";
                    sct.setName(str + sct.getName());
                   // newList.add(sct);
                    loadTree(str,sct,newList,oldList);
                }
            }
        return;
    }


}
