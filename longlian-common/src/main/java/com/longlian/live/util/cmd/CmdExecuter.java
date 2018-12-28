package com.longlian.live.util.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhan on 2017-12-23.
 */
public class CmdExecuter {
    private static Logger logg = LoggerFactory.getLogger(CmdExecuter.class);
    /**
     * 执行指令
     * @param command 执行指令对象
     */
    static public void exec(ICommand command){
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command.getCommand());
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                logg.info("处理返回结果：{}"  , line);
                command.dealReturn(line);
            }
            proc.waitFor();
            stdout.close();
        } catch (Exception e) {
            logg.error("命令执行处理报错："  ,e);
        }
    }
}



