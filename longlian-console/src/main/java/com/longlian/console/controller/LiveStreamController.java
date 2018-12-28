package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.LiveStreamService;
import com.longlian.console.util.Base64SecureUrl;
import com.longlian.live.util.aliyun.Main;
import com.longlian.model.Course;
import com.longlian.model.LiveStream;
import com.qiniu.pili.Client;
import com.qiniu.pili.Hub;
import com.qiniu.pili.Stream;
import com.qiniu.util.Auth;
import com.sun.javafx.collections.MappingChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;


/**
 * Created by liuna on 2018/4/16.
 */
@RequestMapping("/liveStream")
@Controller
public class LiveStreamController {
    private static Logger log = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    @Autowired
    private LiveStreamService liveStreamService;
    @Value("${qi-niu-live.hub_name}")
    private String hub_name;
    @Value("${qi-niu-live.accessKey}")
    private String accessKey;
    @Value("${qi-niu-live.secretKey}")
    private String secretKey;
    @Autowired
    CustomizedPropertyConfigurer customizedPropertyConfigurer;
    /**
     * 先进入获取直播流数据页面 mav
     * @return
     */
    @RequestMapping("/getLiveStreamList")
    public ModelAndView pendingCommentIndex() {
        ModelAndView view = new ModelAndView("/func/liveStream/liveStreamList");
        return view;
    }

    /**
     *  getListData获取正在直播的流
     * @param requestModel
     * @param map
     * @return
     */
    @RequestMapping(value = "/getListData", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();

        List<Map> streamList=liveStreamService.getStreamList();  //查询正在直播的流信息
        model.setRows(streamList);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     *  停播 直播流
     * @return
     */
    @RequestMapping("/disableStream")
    @ResponseBody
    public ActResult stopStream(HttpServletRequest request , String courseId) {
        ActResult actResult=liveStreamService.disableStream(courseId);

        return actResult;
    }
}
