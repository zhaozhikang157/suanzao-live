package com.longlian.live.dao;

import com.longlian.dto.CourseAuditDto;
import com.longlian.model.CourseAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface CourseAuditMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseAudit record);

    int insertSelective(CourseAudit record);

    CourseAudit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseAudit record);

    int updateByPrimaryKey(CourseAudit record);

    void updateStatus(CourseAudit record);

    List<CourseAuditDto> getAllNoAudit();
}