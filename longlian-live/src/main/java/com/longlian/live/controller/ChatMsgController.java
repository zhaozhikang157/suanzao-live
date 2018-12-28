package com.longlian.live.controller;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-10-21.
 * chat_room_msg
 */
//@Controller
//@RequestMapping("/chatMsg")
public class ChatMsgController {

    @Autowired
    ChatMsgRemote chatMsgRemote;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;

    @ApiOperation(value = "增加消息", httpMethod = "POST", notes = "增加消息")
    @RequestMapping("/insert")
    @ResponseBody
    public ActResultDto insert(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        return  chatMsgRemote.insert(chatRoomMsgDto);
    }

    @ApiOperation(value = "取历史消息", httpMethod = "GET", notes = "取历史消息")
    @RequestMapping("/getHistoryMsg")
    @ResponseBody
    public ActResultDto getHistoryMsg(Long courseId, Integer offSet) throws Exception {
        List list = chatRoomMsgService.getHistoryMsg(courseId , offSet);
        return ActResultDto.success().setData(list);
    }

    @ApiOperation(value = "取历史消息", httpMethod = "GET", notes = "取历史消息")
    @RequestMapping("/getHistoryMsgByCourseId")
    @ResponseBody
    public ActResultDto getHistoryMsgByCourseId(Long courseId) throws Exception {
        List list =    chatRoomMsgService.getHistoryMsgByCourseId(courseId);
        return ActResultDto.success().setData(list);
    }

    @ApiOperation(value = "取历史消息分页", httpMethod = "POST", notes = "取历史消息分页")
    @RequestMapping("/getHistoryMsgPage")
    @ResponseBody
    public ActResultDto getHistoryMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        List list = chatRoomMsgService.getHistoryMsgPage(chatRoomMsgDto.getCourseId(),chatRoomMsgDto.getOffSet(),chatRoomMsgDto);
        return ActResultDto.success().setData(list);
    }

    @ApiOperation(value = "取历史消息分页", httpMethod = "GET", notes = "取历史消息分页")
    @RequestMapping("/getChatRoomMsgPage")
    @ResponseBody
    public ActResultDto getChatRoomMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        ActResultDto resultDto =  chatMsgRemote.getChatRoomMsgPage(chatRoomMsgDto);
        return resultDto;
    }

    @ApiOperation(value = "修改课程名称", httpMethod = "GET", notes = "修改课程名称")
    @RequestMapping("/updateCourseName")
    @ResponseBody
    public ActResultDto updateCourseName(Long courseId, String name) throws Exception {
        ActResultDto resultDto =  chatMsgRemote.updateCourseName(courseId, name);
        return resultDto;
    }

    @ApiOperation(value = "清屏", httpMethod = "GET", notes = "清屏")
    @RequestMapping("/clearScreenByChatRoomId")
    @ResponseBody
    public ActResultDto clearScreenByChatRoomId(Long roomId) throws Exception {
        ActResultDto resultDto =  chatMsgRemote.clearScreenByChatRoomId(roomId);
        return resultDto;
    }

    @ApiOperation(value = "查看问题", httpMethod = "GET", notes = "查看问题")
    @RequestMapping("/findQuestry")
    @ResponseBody
    public ActResultDto findQuestry(Long courseId, Long msgId, Long pageSize) throws Exception {
        return chatMsgRemote.findQuestry(courseId,msgId,pageSize);
    }

    @ApiOperation(value = "查看所有消息", httpMethod = "GET", notes = "查看所有消息")
    @RequestMapping("/findAllMsg")
    @ResponseBody
    public ActResultDto findAllMsg(Long courseId, Long teacherId, Long msgId, Long pageSize) throws Exception {
        return chatMsgRemote.findAllMsg(courseId, teacherId, msgId, pageSize);
    }

    @ApiOperation(value = "查看老师消息", httpMethod = "GET", notes = "查看老师消息")
    @RequestMapping("/findTeacherMsgPage")
    @ResponseBody
    public ActResultDto findTeacherMsgPage(Long courseId, Long teacherId, Integer offset, Integer pageSize) throws Exception {
        return chatMsgRemote.findTeacherMsgPage(courseId, teacherId, offset, pageSize);
    }

    @ApiOperation(value = "最后三条", httpMethod = "GET", notes = "最后三条")
    @RequestMapping("/findLastThreeMsg")
    @ResponseBody
    public ActResultDto findLastThreeMsg(Long courseId, Long teacherId) throws Exception {
        return chatRoomMsgService.findLastThreeMsg(courseId, teacherId);
    }

    @ApiOperation(value = "修改是否是垃圾消息", httpMethod = "GET", notes = "修改是否是垃圾消息")
    @RequestMapping("/updateMsgGarbage")
    @ResponseBody
    public ActResultDto updateMsgGarbage(String msgidClient) throws Exception {
       return chatMsgRemote.updateMsgGarbage(msgidClient);
    }

    @ApiOperation(value = "该课程消息", httpMethod = "GET", notes = "该课程消息")
    @RequestMapping("/findMsgByCourseId")
    @ResponseBody
    public ActResultDto findMsgByCourseId(Long courseId , Long teacher , long msgId , int pageSize) throws Exception {
        ChatRoomMsgDto chatRoomMsgDto = new ChatRoomMsgDto();
        chatRoomMsgDto.setCourseId(courseId);
        chatRoomMsgDto.setTeacherId(teacher);
        chatRoomMsgDto.setId(msgId);
        chatRoomMsgDto.setPageSize(pageSize);
        return chatMsgRemote.findMsgByCourseId(chatRoomMsgDto);
    }

    @ApiOperation(value = "修改消息内容", httpMethod = "POST", notes = "修改消息内容")
    @RequestMapping("/updateAttach")
    @ResponseBody
    public ActResultDto updateAttach(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        return chatMsgRemote.updateAttach(chatRoomMsgDto);
    }

    @ApiOperation(value = "查看最大消息ID", httpMethod = "GET", notes = "查看最大消息ID")
    @RequestMapping("/findMaxMsgId")
    @ResponseBody
    public ActResultDto findMaxMsgId() throws Exception {
        long data =  chatRoomMsgService.findMaxMsgId();
        return ActResultDto.success().setData(data);
    }

    @ApiOperation(value = "findMsgByCourseIdDB", httpMethod = "GET", notes = "findMsgByCourseIdDB")
    @RequestMapping("/findMsgByCourseIdDB")
    @ResponseBody
    public ActResultDto findMsgByCourseIdDB(Long courseId , Long teacherId , Long msgId , Integer pageSize) throws Exception {
        List<Map> list = chatRoomMsgService.findMsgByCourseIdDB(courseId , teacherId , msgId , pageSize);
        return ActResultDto.success().setData(list);
    }


}
