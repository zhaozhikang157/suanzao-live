package com.longlian.live.util.cmd;

/**
 * Created by liuhan on 2017-12-23.
 */
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 处理音频转化音频命令
 */
public class FFConverAudioCommand implements ICommand {
    private static Logger logg = LoggerFactory.getLogger(FFConverAudioCommand.class);
    private Map result = new HashMap();

    private String srcFile ;
    private String descFile ;

    public FFConverAudioCommand(String srcFile , String descFile) {
        this.srcFile  = srcFile;
        this.descFile = descFile;
    }


    @Override
    public List<String> getCommand() {
        List<String> commend = new ArrayList<>();
        commend.add("/usr/local/wechat-speex-declib/bin/speex_decode");
        commend.add(this.srcFile);
        commend.add(this.descFile);
        return commend;
    }

    /**
     * 处理字符串
     *
     * @param str
     */
    @Override
    public void dealReturn(String str) {
        if (StringUtils.isEmpty(str)) {
            return ;
        }
        //取得时长
        if(str.contains("Duration")){
            System.out.println(str.substring(str.indexOf(":")+1,str.indexOf(",")));
            str = str.substring(str.indexOf(":")+1,str.indexOf(","));
            if(!StringUtils.isEmpty(str)){
                logg.info("获得时长：{}" , str);
                result.put("dur", TimeUtils.Test(str));
            }
        }
    }

    @Override
    public Map getMeta() {
        return this.result;
    }
}

