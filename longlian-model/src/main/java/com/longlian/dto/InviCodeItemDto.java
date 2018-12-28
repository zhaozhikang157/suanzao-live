package com.longlian.dto;

import com.longlian.model.InviCodeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by admin on 2017/8/29.
 */
public class InviCodeItemDto extends InviCodeItem{
    private static Logger log = LoggerFactory.getLogger(InviCodeItemDto.class);

    private String useName;

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }
}
