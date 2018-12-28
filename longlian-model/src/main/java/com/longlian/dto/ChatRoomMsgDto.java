package com.longlian.dto;

import com.longlian.model.ChatRoomMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by admin on 2017/12/14.
 */
public class ChatRoomMsgDto extends ChatRoomMsg {
    private static Logger log = LoggerFactory.getLogger(ChatRoomMsgDto.class);

    private Map map;
    private Long teacherId;
    private Long courseId ;
    private Integer offSet;
    private Integer pageSize;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public Long getCourseId() {
        return courseId;
    }

    @Override
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getOffSet() {
        return offSet;
    }

    public void setOffSet(Integer offSet) {
        this.offSet = offSet;
    }
}
