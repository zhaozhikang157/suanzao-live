package com.longlian.dto;

/**
 * Created by liuhan on 2017-11-18.
 */
public class MixliveNotificationData {
    private String mainMixDomain;
    private String mainMixApp;
    private String mainMixStream;
    private String mixDomain;
    private String mixApp;
    private String mixStream;
    private String mixTemplate;
    private String event;
    private String code;

    public String getMainMixDomain() {
        return mainMixDomain;
    }

    public void setMainMixDomain(String mainMixDomain) {
        this.mainMixDomain = mainMixDomain;
    }

    public String getMainMixApp() {
        return mainMixApp;
    }

    public void setMainMixApp(String mainMixApp) {
        this.mainMixApp = mainMixApp;
    }

    public String getMainMixStream() {
        return mainMixStream;
    }

    public void setMainMixStream(String mainMixStream) {
        this.mainMixStream = mainMixStream;
    }

    public String getMixDomain() {
        return mixDomain;
    }

    public void setMixDomain(String mixDomain) {
        this.mixDomain = mixDomain;
    }

    public String getMixApp() {
        return mixApp;
    }

    public void setMixApp(String mixApp) {
        this.mixApp = mixApp;
    }

    public String getMixStream() {
        return mixStream;
    }

    public void setMixStream(String mixStream) {
        this.mixStream = mixStream;
    }

    public String getMixTemplate() {
        return mixTemplate;
    }

    public void setMixTemplate(String mixTemplate) {
        this.mixTemplate = mixTemplate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}
