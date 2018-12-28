package com.longlian.appmsg.controller;

import com.longlian.appmsg.service.AppMsgService;
import com.longlian.dto.ActResultDto;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AppMsgController {
    private static Logger log = LoggerFactory.getLogger(AppMsgController.class);

    @Autowired
    AppMsgService appMsgService;

    @ApiOperation(value = "查询最大消息ID", httpMethod = "GET", notes = "查询最大消息ID")
    @RequestMapping("/" +
            "")
    @ResponseBody
    public ActResultDto findAppMsgMaxId() throws Exception {
        ActResultDto resultDto = new ActResultDto();
        long maxId = appMsgService.findAppMsgMaxId();
        resultDto.setData(maxId);
        return resultDto;
    }

    @ApiOperation(value = "获取消息列表", httpMethod = "POST", notes = "获取消息列表")
    @RequestMapping("/getAppMsgList")
    @ResponseBody
    public ActResultDto getAppMsgList(long id, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = appMsgService.getAppMsgList(id,pageNum,pageSize);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "获取消息列表大类信息", httpMethod = "POST", notes = "获取消息列表")
    @RequestMapping("/getNewAppMsgTypeInfo")
    @ResponseBody
    public ActResultDto getNewAppMsgTypeInfo(long id,String version) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = appMsgService.getNewAppMsgTypeInfo(id,version);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "获取消息列表信息", httpMethod = "POST", notes = "获取消息列表")
    @RequestMapping("/getNewAppMsgTypeList")
    @ResponseBody
    public ActResultDto getNewAppMsgTypeList(String type,long id, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = appMsgService.getNewAppMsgTypeList(type,id, pageNum, pageSize);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "根据大类修改系统消息为已读", httpMethod = "POST", notes = "根据大类修改系统消息为已读")
    @RequestMapping("/updateStatusByMsgType")
    @ResponseBody
    public ActResultDto updateStatusByMsgType(String type,long id) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.updateStatusByMsgType(type,id);
        return resultDto;
    }

    @ApiOperation(value = "批量删除消息", httpMethod = "POST", notes = "批量删除消息")
    @RequestMapping("/deleteAppMsgByIds")
    @ResponseBody
    public ActResultDto deleteAppMsgByIds(String ids) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.deleteAppMsgByIds(ids);
        return resultDto;
    }

    @ApiOperation(value = "获取消息列表", httpMethod = "POST", notes = "获取消息列表")
    @RequestMapping("/getAppMsgListV2")
    @ResponseBody
    public ActResultDto getAppMsgListV2(long id, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = appMsgService.getAppMsgListV2(id,pageNum,pageSize);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "修改app_msg", httpMethod = "GET", notes = "修改app_msg")
    @RequestMapping("/updateAppMsg")
    @ResponseBody
    public ActResultDto updateAppMsg(long id) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        Map map = appMsgService.updateAppMsg(id);
        resultDto.setData(map);
        return resultDto;
    }

    @ApiOperation(value = "删除消息", httpMethod = "POST", notes = "删除消息")
    @RequestMapping("/deleteAppMsg")
    @ResponseBody
    public ActResultDto deleteAppMsg(String id) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.deleteAppMsg(id);
        return resultDto;
    }

    @ApiOperation(value = "插入消息", httpMethod = "GET", notes = "插入消息")
    @RequestMapping("/insertV2")
    @ResponseBody
    public ActResultDto insertV2(@RequestBody AppMsg appMsg) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.insertV2(appMsg);
        return resultDto;
    }

    @ApiOperation(value = "获取消息数量", httpMethod = "GET", notes = "获取消息数量")
    @RequestMapping("/getIsAppMsg")
    @ResponseBody
    public ActResultDto getIsAppMsg(long appId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        long count = appMsgService.getIsAppMsg(appId);
        resultDto.setData(count);
        return resultDto;
    }

    @ApiOperation(value = "读取消息", httpMethod = "POST", notes = "读取消息")
    @RequestMapping("/readAllMessage")
    @ResponseBody
    public ActResultDto readAllMessage(long appId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.readAllMessage(appId);
        return resultDto;
    }

    @ApiOperation(value = "读取消息", httpMethod = "GET", notes = "读取消息")
    @RequestMapping("/deleteAllAppMsg")
    @ResponseBody
    public ActResultDto deleteAllAppMsg(long appId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.deleteAllAppMsg(appId);
        return resultDto;
    }

    @ApiOperation(value = "修改课程消息", httpMethod = "GET", notes = "修改课程消息")
    @RequestMapping("/updateMsgCourseStatus")
    @ResponseBody
    public ActResultDto updateMsgCourseStatus(long courseId, String status , long roomId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.updateMsgCourseStatus(courseId,status,roomId);
        return resultDto;
    }

    @ApiOperation(value = "批量修改课程消息", httpMethod = "POST", notes = "批量修改课程消息")
    @RequestMapping("/updateMsgCourseStatues")
    @ResponseBody
    public ActResultDto updateMsgCourseStatues(List<Course> course) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.updateMsgCourseStatues(course);
        return resultDto;
    }

    @ApiOperation(value = "根据类型查询消息", httpMethod = "GET", notes = "根据类型查询消息")
    @RequestMapping("/findTypeMsg")
    @ResponseBody
    public ActResultDto findTypeMsg(int offset , int pageSize) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<AppMsg> list = appMsgService.findTypeMsg(offset,pageSize);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "修改信息", httpMethod = "GET", notes = "修改信息")
    @RequestMapping("/updateInfo")
    @ResponseBody
    public ActResultDto updateInfo(long id , long courseId , long roomId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.updateInfo(id,courseId,roomId);
        return resultDto;
    }

    @ApiOperation(value = "删除消息", httpMethod = "GET", notes = "删除消息")
    @RequestMapping("/delAppMsgBefore")
    @ResponseBody
    public ActResultDto delAppMsgBefore(Date time , int i) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.delAppMsgBefore(time,i);
        return resultDto;
    }

    @ApiOperation(value = "插入消息", httpMethod = "GET", notes = "插入消息")
    @RequestMapping("/insertAppMsg")
    @ResponseBody
    public ActResultDto insertAppMsg(@RequestBody AppMsg appMsg) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        appMsgService.insertAppMsg(appMsg);
        return resultDto;
    }

}
