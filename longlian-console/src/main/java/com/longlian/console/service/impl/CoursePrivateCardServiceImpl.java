package com.longlian.console.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.console.dao.CoursePrivateCardMapper;
import com.longlian.console.service.CoursePrivateCardService;
import com.longlian.dto.CoursePrivateCardDto;
import com.longlian.model.course.CoursePrivateCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/8.
 */
@Service("coursePrivateCardService")
public class CoursePrivateCardServiceImpl implements CoursePrivateCardService{

    @Autowired
    private CoursePrivateCardMapper coursePrivateCardMapper;

    @Override
    public List<Map> getListByPage(DatagridRequestModel requestModel, Map map) {

        return coursePrivateCardMapper.getListPage(requestModel, map);
    }

    @Override
    public Map getCoursePrivateCardById(long id) {

        return coursePrivateCardMapper.getCoursePrivateCardById(id);
    }

    @Override
    public CoursePrivateCardDto getCourseByCourseId(long courseId) {
        Map map = coursePrivateCardMapper.getCourseByCourseId(courseId);
        CoursePrivateCardDto cpcd=new CoursePrivateCardDto();
        cpcd.setLiveTopic(map.get("liveTopic") + "");
        cpcd.setAppUserName(map.get("appUserName") + "");
        cpcd.setUserId(Long.parseLong(map.get("userId")+""));
        return cpcd;
    }

    @Override
    public int addOrUpdate(CoursePrivateCard coursePrivateCard) {
       // CoursePrivateCard cpc=new CoursePrivateCard();
        if(coursePrivateCard.getId()!=null && coursePrivateCard.getId()>0){
            coursePrivateCardMapper.updateCoursePrivateCard(coursePrivateCard);
        }else{
             coursePrivateCardMapper.addCoursePrivateCard(coursePrivateCard);
        }
        return 0;
    }

    @Override
    public int deleteById(long id) {
        coursePrivateCardMapper.deleteById(id);
        return 0;
    }
}
