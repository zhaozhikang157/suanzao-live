package com.longlian.console.service.impl;

import com.huaxin.util.ActResult;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.AdvertisingDisplayMapper;
import com.longlian.console.dao.CourseMapper;
import com.longlian.console.service.AdvertisingDisplayService;
import com.longlian.console.util.SystemUtil;
import com.longlian.model.AdvertisingDisplay;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/23.
 */
@Service("advertisingDisplayService")
public class AdvertisingDisplayServiceImpl implements AdvertisingDisplayService {
    private static Logger log = LoggerFactory.getLogger(AdvertisingDisplayServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    SystemUtil systemUtil;

    @Autowired
    AdvertisingDisplayMapper advertisingDisplayMapper;

    @Override
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, AdvertisingDisplay advertising) {
        DatagridResponseModel model = new DatagridResponseModel();
        List<AdvertisingDisplay> list = advertisingDisplayMapper.getListPage(requestModel, advertising);
        model.setRows(list);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    @Override
    public void deleteById(long id)  throws Exception{
        advertisingDisplayMapper.deleteById(id);
        //更新数缓存数据
        resetRedisData();
    }

    @Override
    public AdvertisingDisplay findById(long id){
        return advertisingDisplayMapper.findById(id);

    }

    @Override
    public ActResult doSaveAndUpdate(@RequestBody AdvertisingDisplay advertising) throws Exception {
        ActResult result = new ActResult();
        Long courseId = advertising.getCourseId();
        Course course = null;
        Map map = new HashMap<>();
        if("2".equals(advertising.getAdvertType())){
            if(courseId!=null && courseId >0){
                course = courseMapper.getCourse(courseId);
            }
            if(course == null){
                result.setSuccess(false);
                result.setMsg("没有相关的课程信息,请核对课程ID是否正确!");
                return result;
            }
            if(course.getSeriesCourseId()>0){
                result.setSuccess(false);
                result.setMsg("不能填写系列课单节课!");
                return result;
            }else{
                advertising.setIsSeriesCourse(course.getIsSeriesCourse());
            }
            if(!"0".equals(course.getStatus())){
                result.setSuccess(false);
                result.setMsg("该课程已下架,请填写其他的课程ID!");
                return result;
            }
            String isSeriesCourse =course.getIsSeriesCourse();
            map.put("isSeriesCourse", isSeriesCourse);
            Long appId = course.getAppId();
            Long roomId = course.getRoomId();
            map.put("appId",appId);
            map.put("roomId", roomId);
        }
        map.put("id",courseId);
        advertising.setCourseInfo(JsonUtil.toJson(map));
        if (advertising.getId() > 0) {//修改
            advertisingDisplayMapper.update(advertising);
        } else {//添加
            advertisingDisplayMapper.create(advertising);
        }
        //更新数缓存数据
        resetRedisData();
        return result;
    }

    /**
     * 重新设置redis 数据  广告首页
     */
    public void resetRedisData(){
        redisUtil.del(RedisKey.ll_live_app_advertising);
    }

}
