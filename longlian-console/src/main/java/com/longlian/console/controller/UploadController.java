package com.longlian.console.controller;

import com.alibaba.fastjson.JSON;
import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.huaxin.util.ActResult;
import com.longlian.live.service.StoreFileService;
import com.longlian.model.StoreFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("file")
public class UploadController {
  
  @Value("${exclude_file:jsp,classes,js,css,html}")
  private String exclude_file;
  
  private String[] exts;
    
  @Autowired
  private StoreFileService storeFileService;
  /**
   * 上传文件公共功能
   * @param request
   * @return {"code":0,"data":[{"createTime":1460432349459,"id":10
   * ,"md5":"32b536b74f8d23e25a8ae3de8f276da3"
   * ,"name":"工作日志-20160325.xlsx"
   * ,"size":11106,"type":"xlsx"
   * ,"url":"/upload/2016/04/94820d5e8c744a0c84e87d42e31f6a25.xlsx"}]
   * ,"msg":"","success":true}
   * @throws Exception
   */
  @RequestMapping("upload")
  public String upload(HttpServletRequest request , HttpServletResponse response , String basePath) throws Exception {
    this.output(response, this.uploadFile(request, response , basePath));
    return null;
  }
  
  public  ActResult uploadFile(HttpServletRequest request , HttpServletResponse response , String basePath) throws Exception {
      MultipartHttpServletRequest multipartRequest = null;
      try {
        multipartRequest = (MultipartHttpServletRequest) request;
      } catch (ClassCastException e) {
        e.printStackTrace();
        output(response , ActResult.fail("没有文件"));
        return null;
      }
      ActResult result = new ActResult();
      Map<String, MultipartFile> map = multipartRequest.getFileMap();
      //那个模块的
      String module = multipartRequest.getParameter("module");
      //不检查MD5，不去重，默认是去重
      String notCheckMd5 = multipartRequest.getParameter("notCheckMd5");
      List<StoreFile> flist = new ArrayList();
      for (String fname : map.keySet()) {
        // 获得文件：
        MultipartFile file = multipartRequest.getFile(fname);
        if (file == null) {
            continue;
        }
        if (file != null && excludeFile(file.getOriginalFilename())) {
            output(response , ActResult.fail("抱歉,出于安全的考虑,不支持此文件类型"));
            return null;
        }

          StoreFile storeFile = new StoreFile();
          storeFile.setSize(file.getSize());
          storeFile.setCreateTime(new Date());
          storeFile.setModule(module);
          storeFile.setName(file.getOriginalFilename());
          storeFile.setMd5(DigestUtils.md5DigestAsHex(file.getInputStream()));
          flist.add(storeFileService.doUploadFile(file.getBytes(), storeFile));
      }

      if (flist.size() < 1) {
        result.setMsg("没有上传文件");
        result.setSuccess(false);
      }
      //如果只有一个文件的话
      if (flist.size() == 1) {
          result.setData(flist.get(0));
      } else {
          result.setData(flist);
      }
      return result;
  }


  /**
   * 输出
   * @param response
   */
  public void output(HttpServletResponse response , ActResult dto) {
      response.setCharacterEncoding("UTF-8");  
      response.setContentType("text/html; charset=utf-8");  
      PrintWriter out = null;  
      try {  
          out = response.getWriter();  
          out.append(JSON.toJSONString(dto));  
      } catch (IOException e) {  
          e.printStackTrace();  
      } finally {  
          if (out != null) {  
              out.close();  
          }  
      }  
  }
  @RequestMapping(value = "/page" ,method = RequestMethod.GET)
  public ModelAndView index(){
      return new ModelAndView("/func/msg/upload");
  }
  /**
   * 判断文件是否能上传
   * @param fileName
   * @return
   */
  private boolean excludeFile(String fileName) {
      if (exts == null) {
          exts = exclude_file.split(",");
      }
      if (exts != null) {
          for (String ext : exts) {
              if (fileName.endsWith("." + ext)) {
                  return true;
              }
          }
      }
      return false;
  }
  /**
   * baidu editor
   * @param request
   * @param response
   * @param action
   * @throws Exception
   */
  @RequestMapping(value="uedit.json")
  public void uedit(HttpServletRequest request,HttpServletResponse response, String action) throws Exception {
      String json="";
      if(action.startsWith("upload")){
          ActResult ar= uploadFile(request , response , null);
          if(ar.isSuccess()){
              State state=new BaseState(true);
              //目前只支持单文件上传
              StoreFile file = null;
              if (ar.getData() instanceof StoreFile) {
                  file = (StoreFile)ar.getData(); 
              } else {
                  file = ((List<StoreFile>)ar.getData()).get(0);
              } 
              state.putInfo("url", file.getUrl());
              json=state.toJSONString();
          }else{
              json=  "{\"state\": \"" +ar.getMsg()+"\"}";
          }
          
      }else{
          String rootPath =UploadController.class.getResource("/").getPath();
           json= new ActionEnter( request, rootPath ).exec() ;
      }
      
      try {
          response.getWriter().write(json);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  
}
