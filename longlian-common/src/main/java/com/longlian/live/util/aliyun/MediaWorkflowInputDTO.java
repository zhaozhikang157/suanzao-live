package com.longlian.live.util.aliyun;

import com.alibaba.fastjson.annotation.JSONField;

public class MediaWorkflowInputDTO {

    /**
     *
     */
    private static final long serialVersionUID = 5212690110801932598L;

    @JSONField(name = "InputFile")
    private FileDTO file;

    @JSONField(name = "UserData")
    private String userData;


    public MediaWorkflowInputDTO() {

    }


    public MediaWorkflowInputDTO(FileDTO file, String userData) {
        this.setFile(file);
        this.setUserData(userData);
    }

    public FileDTO getFile() {
        return file;
    }

    public void setFile(FileDTO file) {
        this.file = file;
    }


    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

}
