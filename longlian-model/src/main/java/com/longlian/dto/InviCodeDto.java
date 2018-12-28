package com.longlian.dto;

import com.longlian.model.InviCard;
import com.longlian.model.InviCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/8/29.
 */
public class InviCodeDto extends InviCode {
    private static Logger log = LoggerFactory.getLogger(InviCodeDto.class);

    private Date courseStartTime;       //开课时间
    private String liveTopic;           //课程标题
    private String theUseOfNum;         //使用情况 10/100;
    private String termOfValidity;      //有效期
    private String copyHref;

    public String getCopyHref() {
        return copyHref;
    }

    public void setCopyHref(String copyHref) {
        this.copyHref = copyHref;
    }

    public String getTheUseOfNum() {
        return theUseOfNum;
    }

    public void setTheUseOfNum(String theUseOfNum) {
        this.theUseOfNum = theUseOfNum;
    }

    public String getTermOfValidity() {
        return termOfValidity;
    }

    public void setTermOfValidity(String termOfValidity) {
        this.termOfValidity = termOfValidity;
    }

    public String getLiveTopic() {
        return liveTopic;
    }

    public void setLiveTopic(String liveTopic) {
        this.liveTopic = liveTopic;
    }

    public Date getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseStartTime(Date courseStartTime) {
        this.courseStartTime = courseStartTime;
    }
}
