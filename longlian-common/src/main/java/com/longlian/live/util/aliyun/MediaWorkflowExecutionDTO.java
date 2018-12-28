package com.longlian.live.util.aliyun;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MediaWorkflowExecutionDTO {

    /**
     *
     */
    private static final long serialVersionUID = -8124470267990996867L;

    @JSONField(name = "MediaWorkflowId", ordinal = 1)
    private String mediaWorkflowId;
    @JSONField(name = "Name", ordinal = 2)
    private String name;

    @JSONField(name = "RunId", ordinal = 3)
    private String runId;

    @JSONField(name = "MediaId", ordinal = 4)
    private String mediaId;

    @JSONField(name = "Input", ordinal = 5)
    private MediaWorkflowInputDTO input;

    @JSONField(name = "State", ordinal = 6)
    private String state;

    @JSONField(name = "ActivityList", ordinal = 7)
    private List<ActivityDTO> activities;

    @JSONField(name = "CreationTime", ordinal = 8)
    private String creationTime;


    public String getName() {
        return name;
    }

    public String getRunId() {
        return runId;
    }

    public MediaWorkflowInputDTO getInput() {
        return input;
    }

    public String getState() {
        return state;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public void setInput(MediaWorkflowInputDTO input) {
        this.input = input;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getMediaWorkflowId() {
        return mediaWorkflowId;
    }

    public void setMediaWorkflowId(String mediaWorkflowId) {
        this.mediaWorkflowId = mediaWorkflowId;
    }
}
