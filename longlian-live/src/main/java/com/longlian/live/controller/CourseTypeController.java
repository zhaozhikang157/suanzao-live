package com.longlian.live.controller;

import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseTypeService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by pangchao on 2017/2/14.
 */
@Controller
@RequestMapping(value = "/courseType")
public class CourseTypeController {
    private static Logger log = LoggerFactory.getLogger(CourseTypeController.class);

    @Autowired
    CourseTypeService courseTypeService;

    /**
     *
     * 获取课程类型
     *
     */
    @RequestMapping(value = "/getCourseType", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程类型", httpMethod = "GET", notes = "获取课程类型")
    public ActResultDto getCourseType() {
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(courseTypeService.getCourseType4Redis());
        return resultDto;
    }


    /**
     *
     * 获取课程类型-1.6.4
     *
     */
    @RequestMapping(value = "/getCourseTypes", method = RequestMethod.GET)
    @ResponseBody
    public ActResultDto getCourseTypes() {
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(courseTypeService.getCourseTypes());
        return resultDto;
    }


}
