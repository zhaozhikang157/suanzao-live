package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcel;
import com.huaxin.util.Token.UserIdentity;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.AvatarService;
import com.longlian.console.util.SystemUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.AppUserDto;
import com.longlian.live.service.StoreFileService;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.AppUser;
import com.longlian.model.Avatar;
import com.longlian.token.ConsoleUserIdentity;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/6/14.
 */
@RequestMapping("/avatar")
@Controller
public class AvatarController {
    private static Logger log = LoggerFactory.getLogger(AvatarController.class);

    @Autowired
    AvatarService avatarService;
    @Autowired
    StoreFileService storeFileService;
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    SystemUtil systemUtil;
    @Autowired
    AppUserService appUserService;

    @RequestMapping("/index")
    public String index(Long courseId , Model model){
        model.addAttribute("courseId", courseId);
        return "/func/appUser/avatar";
    }
    /**
     * 查看虚拟用户信息
     * @param requestModel
     * @return
     */
    @RequestMapping("/findAllAvatar")
    @ResponseBody
    public DatagridResponseModel findAllAvatar(DataGridPage requestModel,String name , String isInRoom, Long courseId){
        DatagridResponseModel drm = new DatagridResponseModel();
        List<AppUserDto> trackDtoList = avatarService.findAllAvatarPage(requestModel,name , isInRoom , courseId);
        drm.setRows(trackDtoList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }
    /**
     * 添加虚拟用户
     * @param
     * @return
     */
    @RequestMapping("/addRoboot")
    @ResponseBody
    public ActResult addRoboot(String courseId , Long count) throws Exception {
        return avatarService.addRobot(courseId ,  count);
    }

    /**
     * 移出虚拟用户
     * @param
     * @return
     */
    @RequestMapping("/removeRoboot")
    @ResponseBody
    public ActResult removeRoboot(String courseId , Long count){
        return avatarService.removeRoboot(courseId, count);
    }
    /**
     * 移出虚拟用户
     * @param
     * @return
     */
    @RequestMapping("/removeRobootByUserId")
    @ResponseBody
    public ActResult removeRobootByUserId(String courseId , Long userId){
        return avatarService.removeRobootByUserId(courseId, userId, true);
    }

    /**
     * 移出虚拟用户
     * @param
     * @return
     */
    @RequestMapping("/dealToken")
    @ResponseBody
    public ActResult dealToken(){
        avatarService.dealYunxinToken();
        return ActResult.success();
    }


    /**
     * 图片导入
     *
     * @return
     */
    @RequestMapping(value = "/importPhoto")
    public ModelAndView importPhoto() {
        ModelAndView view = new ModelAndView("/func/appUser/importPhoto");
        return view;
    }

    /**
     * 上传图片
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadUrl")
    @ResponseBody
    public ActResult uploadUrl(HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");//获取到input里的数据
            String fileName = file.getOriginalFilename();
            String filepath = request.getSession().getServletContext().getRealPath("/");//获取服务器根目录
            String path = filepath + "uploadZip" + File.separator;//文件最终上传的位置
            new File(path).mkdirs();
            file.transferTo(new File(path, fileName));
            avatarService.uploadUrl(path + fileName, fileName);
            log.info("path="+path);
            File file1 = new File(path, fileName);
            file1.delete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导入用户
     *
     * @return
     */
    @RequestMapping(value = "/importUser")
    public ModelAndView importUser() {
        ModelAndView view = new ModelAndView("/func/appUser/importUser");
        return view;
    }

    /**
     * 上传用户信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/uploadUser", method = RequestMethod.POST)
    public void importEmployee(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ConsoleUserIdentity userIdentity = systemUtil.getUserTokenModel(request);//获取登录用户信息
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");//获取到input里的数据
        String filepath = request.getSession().getServletContext().getRealPath("/");//获取服务器根目录
        String excelFilepath = filepath + "uploadUser" + File.separator + userIdentity.getId() + File.separator;//创建子级目录
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //格式化当前系统日期
        String fileName = dateFm.format(new Date()) + "_" + file.getOriginalFilename();//设置新的文件名
        new File(excelFilepath).mkdirs();
        file.transferTo(new File(excelFilepath, fileName));
        avatarService.batchImportAvatarUser(excelFilepath + fileName, fileName, userIdentity);/* 得到存储路径 */
        File file1 = new File(excelFilepath, fileName);
        file1.delete();
    }

    /**
     * 修改用户
     * @param id
     * @return
     */
    @RequestMapping("/toAddOrUpdate")
    public ModelAndView toAddOrUpdate(Long id){
        ModelAndView view = new ModelAndView("/func/appUser/addOrUpdate");
        view.addObject("id", id);
        return view;
    }

    @RequestMapping("/getById")
    @ResponseBody
    public ActResult getById(Long id){
        ActResult result = new ActResult();
        AppUser appUser = appUserService.getAppUserById(id);
        result.setData(appUser);
        return result;
    }

    @RequestMapping("/doSave")
    @ResponseBody
    public ActResult doSave(@RequestBody AppUser appUser){
        ActResult result = new ActResult();
        if(appUser!=null){
            if(appUser.getId()>0){
                avatarService.updateAvatar(appUser);
            }else{
                result.setSuccess(false);
                result.setMsg("保存失败!");
            }
        }else{
            result.setSuccess(false);
            result.setMsg("保存失败!");
        }
        return result;
    }
}
