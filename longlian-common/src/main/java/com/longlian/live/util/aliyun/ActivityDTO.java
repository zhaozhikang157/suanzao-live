package com.longlian.live.util.aliyun;

import com.alibaba.fastjson.annotation.JSONField;

public class ActivityDTO {
    /**
     *
     */
    private static final long serialVersionUID = 39871077951199522L;

    public ActivityDTO() {

    }



    @JSONField(name = "RunId", ordinal = 1)
    private String runId;
    /**
     * activity name
     */

    @JSONField(name = "Name", ordinal = 2)
    private String name;

    @JSONField(name = "Type", ordinal = 3)
    private String type;

    @JSONField(name = "JobId", ordinal = 4)
    private String jobId;

    @JSONField(name = "State", ordinal = 5)
    private String state;

    @JSONField(name = "Code", ordinal = 6)
    private String code;

    @JSONField(name = "Message", ordinal = 7)
    private String msg;

    @JSONField(name = "StartTime", ordinal = 8)
    private String startTime;

    @JSONField(name = "EndTime", ordinal = 9)
    private String endTime;


    @JSONField(name = "MediaWorkflowExecution", ordinal = 10)
    private MediaWorkflowExecutionDTO mediaWorkflowExecutionDTO;


    public String getRunId() {
        return runId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getJobId() {
        return jobId;
    }

    public String getState() {
        return state;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public MediaWorkflowExecutionDTO getMediaWorkflowExecutionDTO() {
        return mediaWorkflowExecutionDTO;
    }

    public void setMediaWorkflowExecutionDTO(MediaWorkflowExecutionDTO mediaWorkflowExecutionDTO) {
        this.mediaWorkflowExecutionDTO = mediaWorkflowExecutionDTO;
    }


}
