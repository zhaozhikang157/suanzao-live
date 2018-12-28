package com.longlian.console.controller;
import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.UserCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by admin on 2017/3/7.
 */
@RequestMapping("/userCount")
@Controller
public class UserCountController {

    private static Logger log = LoggerFactory.getLogger(UserCountController.class);

    @Autowired
    UserCountService UserCountService;

    /**
     * 跳转页面
     * @return
     */
    @RequestMapping("/userCountRecord")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/func/user/userCountRecord");
        return view;
    }

    /**
     *获取用户统计记录
     *
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/getUserCountRecordlist", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getUserCountRecordlist(DataGridPage requestModel,String machineType) {
        DatagridResponseModel drm = new DatagridResponseModel();
        drm.setRows(UserCountService.getUserCountPage(requestModel, machineType));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }
    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteById(long id) {
        ActResult result = new ActResult();
        try{
            UserCountService.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("删除异常");
        }
        return result;
    }
}
