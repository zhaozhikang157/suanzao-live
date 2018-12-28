package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.longlian.console.service.CourseTypeService;
import com.longlian.model.CourseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pangchao on 2017/2/13.
 */
@RequestMapping("/courseType")
@Controller
public class CourseTypeController {
    private static Logger log = LoggerFactory.getLogger(CourseTypeController.class);
    @Autowired
    CourseTypeService courseTypeService;
    /**
     * 课程分类管理页面
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/courseType/index");
        List list = courseTypeService.getList(new CourseType());
        view.addObject("courseTypes",list);
        return view;
    }

    /**
     * 展示页面
     *
     * @param courseType
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public ActResult getList(@RequestBody CourseType courseType)  {
        ActResult result = new ActResult();
        List list = courseTypeService.getList(courseType);
        result.setData(list);
        return result;
    }


    /**
     * 根据id进行查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findById(long id)  {
        ActResult result = new ActResult();
        CourseType courseType = courseTypeService.findById(id);
        result.setData(courseType);
        return result;
    }


    /**
     * 根据id进行删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult deleteById(long id) throws Exception  {
        ActResult actResult=new ActResult();
        try {
            courseTypeService.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setMsg("系统错误");
            actResult.setSuccess(false);
            log.error("操作失败！！");
        }
        return actResult;
    }

    /**
     * 添加修改
     *
     * @param courseType
     * @return
     */
    @RequestMapping(value = "/doSaveAndUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doSaveAndUpdate(@RequestBody CourseType courseType)throws Exception  {

        ActResult actResult=new ActResult();
        try {
             //courseTypeService.doSaveAndUpdate(courseType);
            if(courseType.getParentId()>0L){
                CourseType pct = courseTypeService.findById(courseType.getParentId());//获取父类类型
                if(pct.getPath()!=null && pct.getPath().length()>0){
                    courseType.setPath(pct.getPath() + "|" + courseType.getId());
                }else{
                    courseType.setPath(courseType.getParentId() + "|" + courseType.getId());
                }
            }
           // courseType.setId(id);
            courseTypeService.doSaveAndUpdate(courseType);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setMsg("系统错误");
            actResult.setSuccess(false);
            log.error("保存失败！！");
        }
        return actResult;

    }


    /**
     * 排序
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/toOrder", method = RequestMethod.POST)
    @ResponseBody

    public ActResult toOrder(String ids) throws Exception {
        ActResult actResult=new ActResult();
        try {
            courseTypeService.toOrder(ids);
        }catch (Exception e){
            e.printStackTrace();
            actResult.setMsg("系统错误");
            actResult.setSuccess(false);
            log.error("排序失败！！");
        }
        return actResult;
    }

    /**
     * 去修改页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/toAddOrUpdate", method = RequestMethod.GET)
    public ModelAndView toAddOrUpdate(long id){
        ModelAndView view = new ModelAndView("/func/courseType/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    /**
     * 获取全部课程分类
     */
    @RequestMapping(value = "/getCourseTypesList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getCourseType4Redis(){
        ActResult ac = new ActResult();
        List<CourseType> list = courseTypeService.getCourseType4Redis();
        ac.setData(list);
        return ac;
    }


    /**
     * 获取全部课程分类
     */
    @RequestMapping(value = "/getCourseTypeListBySORT", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getCourseTypeListBySort(){
        ActResult ac = new ActResult();
        List<CourseType> list = courseTypeService.getCourseType4Redis();
        List<CourseType> newList = new ArrayList<>();
       CourseType courseType=new CourseType();
        courseType.setId(0l);
        courseType.setName("根节点");
        newList.add(courseType);
        List<CourseType> courseTypes = courseTypeService.buildCourseTypeTree(newList, list);
        ac.setData(courseTypes);
        return ac;
    }
}
