package com.longlian.live.dao;

import com.longlian.dto.BankCardDto;
import com.longlian.model.Bank;
import com.longlian.model.BankCard;
import com.longlian.model.course.CourseCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 指定课程生成邀请卡mapper
 */
public interface CoursePrivateCardMapper {
    /**
     * 检查白名单中课程id是否配置
     * @param courseId
     * @return
     */
    int findCourseIsExist(@Param("courseId") Long courseId);

    /**
     * 插入邀请卡模板
     * @param courseCard
     * @return
     */
    int insertCourseCard(CourseCard courseCard);

    /**
     * 更新邀请卡url
     * @param courseCard
     * @return
     */
    int updateCourseCard(CourseCard courseCard);

    /**
     * 查询邀请卡
     * @param courseId
     * @return
     */
    CourseCard findCardUrlByCourseId(@Param("courseId") Long courseId);
    int deleteCourseCard(@Param("courseId") Long courseId);
}
