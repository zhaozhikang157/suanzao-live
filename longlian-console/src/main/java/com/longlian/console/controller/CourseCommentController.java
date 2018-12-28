package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.CourseCommentService;
import com.longlian.dto.BankDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by pangchao on 2017/2/25.
 */
@RequestMapping("/courseComment")
@Controller
public class CourseCommentController {
    private static Logger log = LoggerFactory.getLogger(CourseCommentController.class);

    @Autowired
    CourseCommentService courseCommentService;

    /**
     * 课程评论
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/courseComment/index");
        return view;
    }

    /**
     * @param requestModel map
     * @return model
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel requestModel, @RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(courseCommentService.getListPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 删除
     *
     * @param id
     * @return model
     */
    @RequestMapping(value = "/toDeleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult toDeleteById(long id) {
        ActResult result = new ActResult();
        try {
            courseCommentService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMsg("删除失败");
        }
        return result;
    }
}