package com.longlian.live.util;

import com.huaxin.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-10-17.
 */
public class ReadFileMain {
    public static void main1(String[] args) {
        String filePath = "d:\\ddd.csv";
        List<String> lines = readFileByLines(filePath);

        List<Map> res = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] ars = line.split(",");
            Map m = new HashMap<>();
            m.put("context", ars[0]);
            if ("1".equals(ars[1])) {
                m.put("index", 0);
            } else if ("1".equals(ars[2])) {
                m.put("index", 1);
            }else if ("1".equals(ars[3])) {
                m.put("index", 2);
            }

            String[] labels = ars[4].split("、");
            m.put("label", labels);
            res.add(m);
        }

        System.out.println(JsonUtil.toJson(res));
    }
    public static void main(String[] args) {
        String filePath = "d:\\b.csv";
        List<String> lines = readFileByLines(filePath);


        List<List> result = new ArrayList<>();

        List<Map> res = new ArrayList<>();

        for (String line : lines) {
            if (line.equals(",")) {
                result.add(res);
                res = new ArrayList<>();
                continue;
            }

            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] ars = line.split(",");
            Map m = new HashMap<>();
            m.put("val", ars[0]);
            m.put("content", ars[1]);
            res.add(m);
        }

        System.out.println(JsonUtil.toJson(result));
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
        List<String> res = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
            reader = new BufferedReader(isr);
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                line++;
                if (StringUtils.isEmpty(tempString)) {
                    continue;
                }
                res.add(tempString);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return res;
    }
}
