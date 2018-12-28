package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.CoursePrivateCardService;
import com.longlian.console.service.CourseService;
import com.longlian.dto.CoursePrivateCardDto;
import com.longlian.model.course.CoursePrivateCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by Administrator on 2018/5/8.
 * @author liuna
 */
@RequestMapping("/coursePrivateCard")
@Controller
public class CoursePrivateCardController {

    @Autowired
    private CoursePrivateCardService coursePrivateCardService;
    @Autowired
    private CourseService courseService;

    /**
     *  课程邀请卡页面
     * @param id
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/coursePrivateCard/index");
        return view;
    }

    /**
     * 获取邀请卡 指定课程 列表
     */
    @RequestMapping(value = "/getCoursePrivateCardPage")
    @ResponseBody
    public DatagridResponseModel getCourseTypeListBySort(DatagridRequestModel requestModel,@RequestParam Map map){
        List<Map> listByPage = coursePrivateCardService.getListByPage(requestModel, map);
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(listByPage);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 进入添加或者编辑邀请卡设置页面
     * @return
     */
    @RequestMapping("/addOrUpdate")
    public ModelAndView addOrUpdate(long id) {
        ModelAndView view = new ModelAndView("/func/coursePrivateCard/addOrUpdate");
       /* if(id>0L){
            Map coursePrivateCardMap = coursePrivateCardService.getCoursePrivateCardById(id);
            view.addObject("coursePrivateCardMap",coursePrivateCardMap);
        }*/
        view.addObject("id", id);
        return view;
    }
    @RequestMapping(value = "/doAddOrUpdate")
    @ResponseBody
    public ActResult doAddOrUpdate(@RequestBody CoursePrivateCardDto coursePrivateCardDto){
        ActResult ac = new ActResult();
        CoursePrivateCard coursePrivateCard=new CoursePrivateCard();
        coursePrivateCard.setId(coursePrivateCardDto.getId());
        coursePrivateCard.setUserId(coursePrivateCardDto.getUserId());
        coursePrivateCard.setCourseId(coursePrivateCardDto.getCourseId());
        coursePrivateCard.setAddTime(new Date());
       coursePrivateCardService.addOrUpdate(coursePrivateCard);
        ac.setSuccess(true);
        return ac;
    }
    @RequestMapping(value = "/getCoursePrivateById")
    @ResponseBody
    public ActResult getCoursePrivateById(long id){
        ActResult ac = new ActResult();
        Map coursePrivateCardMap = coursePrivateCardService.getCoursePrivateCardById(id);
        ac.setData(coursePrivateCardMap);
        ac.setSuccess(true);

        return ac;
    }

    /**
     * 根据课程拉取课程下的系列信息
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getCourseInfo")
    @ResponseBody
    public ActResult getCourseInfo(long courseId){
        ActResult ac = new ActResult();
        CoursePrivateCardDto cpcd = coursePrivateCardService.getCourseByCourseId(courseId);
        ac.setData(cpcd);
        ac.setSuccess(true);
        return ac;
    }


    /**
     * 删除 邀请奖课程设置
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/deleteById")
    @ResponseBody
    public ActResult deleteById(long id){
        ActResult ac = new ActResult();
        coursePrivateCardService.deleteById(id);
        ac.setSuccess(true);
        return ac;
    }



}
