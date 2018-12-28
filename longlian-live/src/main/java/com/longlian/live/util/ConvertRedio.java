package com.longlian.live.util;

/**
 * Created by liuhan on 2018-06-25.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConvertRedio {

    private static String inputPath = "";

    private static String outputPath = "";

    private static String ffmpegPath = "";

    public static void main(String args[]) throws IOException {

        getPath();

        if (!checkfile(inputPath)) {
            System.out.println(inputPath + " is not file");
            return;
        }
        if (process()) {
            System.out.println("ok");
        }
    }

    // 先获取当前项目路径，在获得源文件、目标文件、转换器的路径
    private static void getPath() {
        File diretory = new File("");
        try {
            //输入音频路径
            inputPath = "C:\\Users\\xj\\Desktop\\1.m4a";
            //输出路径
            outputPath = "C:\\Users\\xj\\Desktop\\输出音频\\";
            //ffmpeg工具路径
            ffmpegPath = "E:\\ffmpeg-20180619-a990184-win64-static\\bin\\";
        } catch (Exception e) {
            System.out.println("getPath出错");
        }
    }

    private static boolean process() {
        int type = checkContentType();
        boolean status = false;
        if (type == 0) {
            System.out.println("直接转成m4a格式");
            status = processFLV(inputPath);// 直接转成flv格式
        } else if (type == 1) {
            String avifilepath = processAVI(type);
            if (avifilepath == null)
                return false;// 没有得到avi格式
            status = processFLV(avifilepath);// 将avi转成flv格式
        }
        return status;
    }

    private static int checkContentType() {
        String type = inputPath.substring(inputPath.lastIndexOf(".") + 1, inputPath.length())
                .toLowerCase();
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv, m4a等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        } else if (type.equals("m4a")) {
            return 0;
        }else if (type.equals("aac")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }

    private static boolean checkfile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
    private static String processAVI(int type) {
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath + "mencoder");
        commend.add(inputPath);
        commend.add("-oac");
        commend.add("lavc");
        commend.add("-lavcopts");
        commend.add("acodec=mp3:abitrate=64");
        commend.add("-ovc");
        commend.add("xvid");
        commend.add("-xvidencopts");
        commend.add("bitrate=600");
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add(outputPath + "a.m4a");
        try {
            ProcessBuilder builder = new ProcessBuilder();
            Process process = builder.command(commend).redirectErrorStream(true).start();
            new PrintStream(process.getInputStream());
            new PrintStream(process.getErrorStream());
            process.waitFor();
            System.out.println(outputPath + "a.m4a");
            return outputPath + "a.m4a";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    private static boolean processFLV(String oldfilepath) {

        if (!checkfile(inputPath)) {
            System.out.println(oldfilepath + " is not file");
            return false;
        }
        File directory = new File("C:\\Users\\xj\\Desktop");//设定为当前文件夹
        try {
            System.out.println(directory.getCanonicalPath());//获取标准的路径
            System.out.println(directory.getAbsolutePath());//获取绝对路径
            //将需要合成的音频路径写到 text 文件中
            File text = new File("C:\\Users\\xj\\Desktop\\list.txt");
            text.createNewFile();
            PrintWriter writer = new PrintWriter(text);
            writer.write("file 'C:\\Users\\xj\\Desktop\\8.amr'\n" +
                    "file 'C:\\Users\\xj\\Desktop\\4.aac'\n");
            writer.flush();
            writer.close();
            List<String> command = new ArrayList<String>();
            command.add(ffmpegPath + "ffmpeg");
            command.add("-y");//直接强制替换
            command.add("-f");
            command.add("concat");
            command.add("-safe");
            command.add("0");
            command.add("-i");
            command.add(directory.getCanonicalPath() + "\\list.txt");

            command.add("-ac");//通道 声道  1  或 2
            command.add("1");
            command.add("-ar");//声音的采样频率，好像PSP只能支持24000Hz
            command.add("8000");
            command.add("-ab");// 音频数据流量，一般选择32、64、96、128
            command.add("44100");//此处不是很懂，设置成这个参数音频底噪会小，但是音色会有些发闷，希望大神解答。
            command.add("-vol");//设置音量
            command.add("200");//放大2倍
            // command.add("-acodec");// 音频编码用AAC
            // command.add("aac");
            // command.add("-ab");// 音频数据流量，一般选择32、64、96、128
            // command.add("64");
            // command.add("-ac");//通道 声道  1  或 2
            // command.add("1");
            // command.add("-ar");//声音的采样频率，好像PSP只能支持24000Hz
            // command.add("44100");
            command.add(outputPath + "7890.aac");

            try {

                Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();

                new PrintStream(videoProcess.getErrorStream()).start();

                new PrintStream(videoProcess.getInputStream()).start();

                videoProcess.waitFor();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
        }

        //File.getCanonicalPath()和File.getAbsolutePath()大约只是对于new File(".")和new File("..")两种路径有所区别。
        return false;

    }
}

class PrintStream extends Thread {
    java.io.InputStream __is = null;

    public PrintStream(java.io.InputStream is) {
        __is = is;
    }

    public void run() {
        try {
            while (this != null) {
                int _ch = __is.read();
                if (_ch != -1)
                    System.out.print((char) _ch);
                else break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
