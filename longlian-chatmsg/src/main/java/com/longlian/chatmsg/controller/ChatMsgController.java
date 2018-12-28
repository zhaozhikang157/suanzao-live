package com.longlian.chatmsg.controller;

import com.longlian.chatmsg.service.ChatRoomMsgService;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-10-21.
 * chat_room_msg
 */
@RestController
public class ChatMsgController {

    @Autowired
    ChatRoomMsgService chatRoomMsgService;

    @ApiOperation(value = "增加消息", httpMethod = "POST", notes = "增加消息")
    @RequestMapping("/insert")
    @ResponseBody
    public ActResultDto insert(@RequestBody ChatRoomMsg chatRoomMsg) throws Exception {
        chatRoomMsgService.insertChatRoomMsg(chatRoomMsg);
        return ActResultDto.success();
    }

    @ApiOperation(value = "取历史消息", httpMethod = "GET", notes = "取历史消息")
    @RequestMapping("/getHistoryMsg")
    @ResponseBody
    public ActResultDto getHistoryMsg(Long courseId, Integer offSet) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = chatRoomMsgService.getHistoryMsg(courseId , offSet);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "取历史消息", httpMethod = "GET", notes = "取历史消息")
    @RequestMapping("/getHistoryMsgByCourseId")
    @ResponseBody
    public ActResultDto getHistoryMsgByCourseId(Long courseId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = chatRoomMsgService.getHistoryMsgByCourseId(courseId);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "取历史消息分页", httpMethod = "POST", notes = "取历史消息分页")
    @RequestMapping("/getHistoryMsgPage")
    @ResponseBody
    public ActResultDto getHistoryMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = chatRoomMsgService.getHistoryMsgPage(chatRoomMsgDto.getCourseId(),
                chatRoomMsgDto.getOffSet(), chatRoomMsgDto);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "取历史消息分页", httpMethod = "GET", notes = "取历史消息分页")
    @RequestMapping("/getChatRoomMsgPage")
    @ResponseBody
    public ActResultDto getChatRoomMsgPage(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        List<Map> list = chatRoomMsgService.getChatRoomMsgPage(chatRoomMsgDto.getCourseId(),
                chatRoomMsgDto.getOffSet(), chatRoomMsgDto);
        resultDto.setData(list);
        return resultDto;
    }

    @ApiOperation(value = "修改课程名称", httpMethod = "GET", notes = "修改课程名称")
    @RequestMapping("/updateCourseName")
    @ResponseBody
    public ActResultDto updateCourseName(Long courseId, String name) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        chatRoomMsgService.updateCourseName(courseId, name);
        return resultDto;
    }

    @ApiOperation(value = "清屏", httpMethod = "GET", notes = "清屏")
    @RequestMapping("/clearScreenByChatRoomId")
    @ResponseBody
    public ActResultDto clearScreenByChatRoomId(Long roomId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        chatRoomMsgService.clearScreenByChatRoomId(roomId);
        return resultDto;
    }

    @ApiOperation(value = "查看问题", httpMethod = "GET", notes = "查看问题")
    @RequestMapping("/findQuestry")
    @ResponseBody
    public ActResultDto findQuestry(Long courseId, Long msgId, Long pageSize) throws Exception {
        return chatRoomMsgService.findQuestry(courseId,msgId,pageSize);
    }

    @ApiOperation(value = "查看所有消息", httpMethod = "GET", notes = "查看所有消息")
    @RequestMapping("/findAllMsg")
    @ResponseBody
    public ActResultDto findAllMsg(Long courseId, Long teacherId, Long msgId, Long pageSize) throws Exception {
        return chatRoomMsgService.findAllMsg(courseId, teacherId, msgId, pageSize);
    }

    @ApiOperation(value = "查看老师消息", httpMethod = "GET", notes = "查看老师消息")
    @RequestMapping("/findTeacherMsgPage")
    @ResponseBody
    public ActResultDto findTeacherMsgPage(Long courseId, Long teacherId, Integer offset, Integer pageSize) throws Exception {
        return chatRoomMsgService.findTeacherMsgPage(courseId, teacherId, offset, pageSize);
    }

    @ApiOperation(value = "最后三条", httpMethod = "GET", notes = "最后三条")
    @RequestMapping("/findLastThreeMsg")
    @ResponseBody
    public ActResultDto findLastThreeMsg(Long courseId, Long teacherId) throws Exception {
        return chatRoomMsgService.findLastThreeMsg(courseId, teacherId);
    }

    @ApiOperation(value = "最后一条", httpMethod = "GET", notes = "最后三条")
    @RequestMapping("/findLastMsg")
    @ResponseBody
    public ActResultDto findLastMsg(Long courseId, Long teacherId) throws Exception {
        return chatRoomMsgService.findLastMsg(courseId, teacherId);
    }

    @ApiOperation(value = "修改是否是垃圾消息", httpMethod = "GET", notes = "修改是否是垃圾消息")
    @RequestMapping("/updateMsgGarbage")
    @ResponseBody
    public ActResultDto updateMsgGarbage(String msgidClient) throws Exception {
        chatRoomMsgService.updateMsgGarbage(msgidClient);
        return ActResultDto.success();
    }

    @ApiOperation(value = "该课程消息", httpMethod = "GET", notes = "该课程消息")
    @RequestMapping("/findMsgByCourseId")
    @ResponseBody
    public ActResultDto findMsgByCourseId(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        return chatRoomMsgService.findMsgByCourseId(  chatRoomMsgDto);
    }

    @ApiOperation(value = "修改消息内容", httpMethod = "GET", notes = "修改消息内容")
    @RequestMapping("/updateAttach")
    @ResponseBody
    public ActResultDto updateAttach(@RequestBody ChatRoomMsgDto chatRoomMsgDto) throws Exception {
        chatRoomMsgService.updateAttach(chatRoomMsgDto);
        return ActResultDto.success();
    }

    @ApiOperation(value = "查看最大消息ID", httpMethod = "GET", notes = "查看最大消息ID")
    @RequestMapping("/findMaxMsgId")
    @ResponseBody
    public ActResultDto findMaxMsgId() throws Exception {
        ActResultDto resultDto = new ActResultDto();
        long id = chatRoomMsgService.findMaxMsgId();
        resultDto.setData(id);
        return resultDto;
    }


}
