package 根.找错误.二;

import 根.找错误.工具.输入流工具;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static 根.找错误.一.输入地址找错误.sc;

/*
05-15 06:51:17.823 11738 11738 F DEBUG   :       #03 pc 002c8c45  /data/app/com.example.ffmpeghbsp-ei8CZqXCsVUFiow2RURe7g==/lib/arm/libbfsp.so (Java_com_example_ffmpeghbsp_jni_hbsp_cs+124) (BuildId: 726f09fd340a63cd28c0993b00f6d35678141a15)
05-15 06:51:17.823 11738 11738 F DEBUG   :       #12 pc 00000514  [anon:dalvik-classes18.dex extracted in memory from /data/app/com.example.ffmpeghbsp-ei8CZqXCsVUFiow2RURe7g==/base.apk!classes18.dex] (com.example.ffmpeghbsp.MainActivity.点击+4)
规律
通过包名找到 #、数字、空格、pc、空格、内存地址、空格、内部存储的so文件路径，
其中“内部存储的so文件路径”只能在app内获取，最重要的是找到".so"截取"libbfsp.so"，最后搜索到的so文件里面找到对应的"libbfsp.so"用内存执行命令
最重要匹配到”内部存储的so文件路径“
 */
public class 自动从日志里面获取错误信息自动输出错误信息 {
    //下面两个要自己配置
    static String sdk目录加命令 = "E:\\ruanjian\\andsdk2\\ndk\\21.1.6352462\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe"
            + " -C -f -e ";
    static String adb位置 = "E:\\ruanjian\\andsdk2\\platform-tools\\adb.exe";

    static String so文件最近目录, 当前模块so文件相对位置 = "\\build\\intermediates\\merged_native_libs";
    static String 应用包名 = "com.example.ffmpeghbsp";//不用配置
    static Thread 读取日志信息线程;
    //so文件名 so文件路径
    static HashMap<String, String> so文件地址;
    static String 文件编码 = "utf-8";

    //反射找到包名、当前项目路径
    public static void main(String[] args) {
//        获取当前目录位置，找到生成的so文件，并且保存
        sc("jar和bat文件要放在模块的根目录下");
//获取当前目录所在而路径
        File directory = new File("");//参数为空
        String 当前项目路径 = null;
        try {
            当前项目路径 = directory.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        当前项目路径 = "E:\\klg\\cjhr_azx\\ffmpeghbsp";
        so文件最近目录 = 当前项目路径 + 当前模块so文件相对位置;
        so文件地址 = new HashMap<>();
        ArrayList<File> files = 搜索所有so文件(so文件最近目录);
        System.out.println(当前项目路径);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            so文件地址.put(file.getName(), file.toString());
        }
        //找到自己的应用包名：
        String build_gradle = 当前项目路径 + "\\build.gradle";
        File file = new File(build_gradle);
        if (!file.exists()) {
            throw new RuntimeException("找不到文件：" + 当前项目路径 + "\\build.gradle");
        }
        String 文件字符 = 读文件字符串(build_gradle, 文件编码);

        int applicationId = 文件字符.indexOf("applicationId");
        StringBuilder builder = new StringBuilder();
        跳出:
        for (int i = applicationId; i < 文件字符.length(); i++) {
            if (文件字符.charAt(i) == '\"') {
                i++;
                while (i < 文件字符.length()) {
                    if (文件字符.charAt(i) == '\"') {
                        break 跳出;
                    }
//                    sc("找：" + 文件字符.charAt(i));
                    builder.append(文件字符.charAt(i));
                    i++;
                }
            }
        }

        应用包名 = builder.toString();
//        sc("hah :" + build_gradle);
        sc(应用包名);
        if (应用包名 != null && 应用包名.length() > 0) {

        } else {
            throw new RuntimeException("找不到应用包名");
        }

        读取日志信息线程 = new Thread() {
            @Override
            public void run() {
                byte[] sj = new byte[1024 * 1024];
                try {
                    Runtime runtime = Runtime.getRuntime();
                    Process exec = runtime.exec(adb位置 + " shell logcat");
                    InputStream inputStream = exec.getInputStream();
                    StringBuilder stringBuilder = null;
                    int cd, 搜文件地址;
                    String 文件名, 内存地址;
                    while ((cd = inputStream.read(sj)) != -1) {
                        String 日志信息 = new String(sj, 0, cd, "utf-8");
                        String[] split = 日志信息.split("\n");//不用正则性能才最好，测试阶段暂时这样
                        for (int i = 0; i < split.length; i++) {
                            if (split[i].contains(应用包名) &&
                                    split[i].contains(".so")) {
                                搜文件地址 = split[i].indexOf(".so");
                                if (搜文件地址 >= 0) {
                                    跳出:
                                    for (int j = 搜文件地址; j >= 0; j--) {
                                        if (split[i].charAt(j) == '/') {//截取so文件名
                                            文件名 = split[i].substring(j + 1, 搜文件地址 + 3);
                                            String so文件位置 = so文件地址.get(文件名);      //看有没有找到对应的so文件
                                            if (so文件位置 != null) {//#03 pc 002c8c45
                                                for (int k = 0; k < split[i].length(); k++) {
                                                    if (split[i].charAt(k) == '#') {
                                                        if (是否为数字(split[i].charAt(k + 1)) && 是否为数字(split[i].charAt(k + 2)) &&
                                                                split[i].charAt(k + 3) == ' ' &&
                                                                split[i].charAt(k + 4) == 'p' && split[i].charAt(k + 5) == 'c' &&
                                                                split[i].charAt(k + 6) == ' ') {
                                                            stringBuilder = new StringBuilder();
                                                            for (int l = k + 7; l < split[i].length(); l++) {
                                                                if (split[i].charAt(l) == ' ') {
                                                                    break;
                                                                }
                                                                stringBuilder.append(split[i].charAt(l));
                                                            }
                                                            if (stringBuilder != null) {
                                                                内存地址 = stringBuilder.toString();
                                                                //执行命令2
                                                                String 命令 = sdk目录加命令 + so文件位置 + " " + 内存地址;
                                                                Runtime runtime2 = Runtime.getRuntime();
                                                                Process exec2 = runtime2.exec(命令);
                                                                InputStream inputStream2 = exec2.getInputStream();
                                                                String 读取 = 输入流工具.读取一行2(inputStream2);
                                                                sc("报错位置：" + 读取);
                                                                break 跳出;
                                                            } else {
//                                                    sc("找不到"+文件名);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
//                                                sc("找不到so文件");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        读取日志信息线程.start();
    }

    private static ArrayList<File> 搜索所有so文件(String so文件最近目录) {
        return 搜索文件回调方式(so文件最近目录, "so");
    }

    public static ArrayList<File> 搜索文件回调方式(String 目录, String 查找扩展名) {
        ArrayList<File> 查找文件 = new ArrayList<>();
        递归方式找所有文件(new File(目录), 查找文件, 查找扩展名);
        return 查找文件;
    }

    public static void 递归方式找所有文件(File 目录, ArrayList<File> 查找文件, String 查找扩展名) {
        if (目录.isDirectory()) {
            File[] files = 目录.listFiles();
            for (int i = 0; i < files.length; i++) {
                递归方式找所有文件(files[i], 查找文件, 查找扩展名);
            }
        } else {
            String 扩展名 = 获取扩展名(目录.getName());
            if (扩展名.equals(查找扩展名)) {
                查找文件.add(目录);
            }
        }
    }

    public static String 获取扩展名(String name) {
        String[] kz = name.split("\\.");
        if (kz.length > 1) return kz[kz.length - 1];
        return "";
    }

    public static void sc(Object o) {
        System.out.println(o);
    }

    public static boolean 是否为数字(char c) {
        //先判断是否小于9，因为大于9的有很多，小于0的很少
        if (c <= '9' && c >= '0') {
            return true;
        }
        return false;
    }

    public static boolean 是否为十六进制(char c) {
        //先判断是否小于9，因为大于9的有很多，小于0的很少
        if ((c <= '9' && c >= '0') || (c >= 'a' && c <= 'f')) {
            return true;
        }
        return false;
    }

    public static String 读文件字符串(String 路径, String 文字编码) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = null;
        byte[] data = new byte[1024 * 1024];
        int cd = 0;
        String 文本 = "";
        try {
            inputStream = new FileInputStream(路径);
            while ((cd = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, cd);
            }
            outputStream.flush();
            data = outputStream.toByteArray();
            文本 = new String(data, 0, data.length, 文字编码);
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            sc("问题" + e);
        }
        return 文本;
    }
}

