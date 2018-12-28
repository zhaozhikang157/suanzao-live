package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.longlian.dto.CourseAuditDto;
import com.longlian.live.dao.CourseAuditMapper;
import com.longlian.live.service.CourseAuditService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.model.CourseAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/9/5.
 */
@Service("courseAuditService")
public class CourseAuditServiceImpl implements CourseAuditService {

    private static Logger log = LoggerFactory.getLogger(CourseAuditServiceImpl.class);

    @Autowired
    CourseAuditMapper courseAuditMapper;
    @Autowired
     CourseBaseService courseBaseService;

    
    @Override
    public  void insert(CourseAudit record){
        courseAuditMapper.insert(record);
    }
    @Override
    public   CourseAudit selectByPrimaryKey(Long id){
        return  courseAuditMapper.selectByPrimaryKey(id);
    }
    @Override
    public void updateAudit(CourseAudit courseAudit){
        if(StringUtils.isNotEmpty(courseAudit.getIsGarbage())&&"1".equals(courseAudit.getIsGarbage())){
            try{
                courseBaseService.updatePassStatus(courseAudit.getCourseId());
            }catch (Exception e){
                log.error("",e);
            }
        }
        courseAuditMapper.updateByPrimaryKeySelective(courseAudit);
    }
    @Override
    public void updateGarbageStatus(Long courseId, String remark, String status) {
        CourseAudit record = new CourseAudit();
        record.setCourseId(courseId);
        record.setAuditTime(new Date());
        record.setGarbageTip(remark);
        record.setIsGarbage(status);
        courseAuditMapper.updateStatus(record);
    }

    @Override
    public void updateGarbageStatusAndStatus(Long courseId, String remark, String gabageStatus, String status) {
        CourseAudit record = new CourseAudit();
        record.setCourseId(courseId);
        record.setAuditTime(new Date());
        record.setGarbageTip(remark);
        record.setIsGarbage(gabageStatus);
        //record.setStatus(status);
        courseAuditMapper.updateStatus(record);
    }



    @Override
    public List<CourseAuditDto> getAllNoAudit() {
        return courseAuditMapper.getAllNoAudit();
    }

    @Override
    public void updateStatus(Long courseId, String remark, String status) {
        CourseAudit record = new CourseAudit();
        record.setCourseId(courseId);
        record.setAuditTime(new Date());
        record.setRemark(remark);
        record.setStatus(status);
        courseAuditMapper.updateStatus(record);
    }

}
