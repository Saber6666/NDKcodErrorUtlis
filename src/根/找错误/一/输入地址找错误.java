package 根.找错误.一;

import 根.找错误.工具.输入流工具;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/*
下个版本：
通过命令获取手机打印日志，然后搜ndk搜索信息，自动获取错误命令以及内存信息，自动输出错误信息
 */
public class 输入地址找错误 {
    static String sdk目录加命令 = "E:\\ruanjian\\andsdk2\\ndk\\21.1.6352462\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe"
            + " -C -f -e ";
    static ArrayList<String> so文件地址;
    static String so文件最近目录, 当前模块so文件相对位置 = "\\build\\intermediates\\merged_native_libs";

    @Deprecated
    public static void main22测试的(String[] args) {
//      获取当前目录位置，找到生成的so文件，并且保存
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

        so文件地址 = 搜索所有so文件(so文件最近目录);
        System.out.println(当前项目路径);
        while (true) {
            System.out.print("请输入内存地址：");
            Scanner scanner = new Scanner(System.in);
            String 内存地址 = scanner.next();
            for (int i = 0; i < so文件地址.size(); i++) {
                String s = so文件地址.get(i);
                String 命令 = sdk目录加命令 + s + " " + 内存地址;
                Runtime runtime = Runtime.getRuntime();
                try {
                    Process exec = runtime.exec(命令);
                    InputStream inputStream = exec.getInputStream();
                    String 读取 = 输入流工具.读取一行2(inputStream);
                    sc(读取);
                } catch (IOException e) {
                    e.printStackTrace();
//                    throw new RuntimeException(e);
                }
            }
        }

    }

    private static ArrayList<String> 搜索所有so文件(String so文件最近目录) {
        return 搜索文件回调方式(so文件最近目录, "so");
    }

    public static ArrayList<String> 搜索文件回调方式(String 目录, String 查找扩展名) {
        ArrayList<String> 查找文件 = new ArrayList<>();
        递归方式找所有文件(new File(目录), 查找文件, 查找扩展名);
        return 查找文件;
    }

    public static void 递归方式找所有文件(File 目录, ArrayList<String> 查找文件, String 查找扩展名) {
        if (目录.isDirectory()) {
            File[] files = 目录.listFiles();
            for (int i = 0; i < files.length; i++) {
                递归方式找所有文件(files[i], 查找文件, 查找扩展名);
            }
        } else {
            String 扩展名 = 获取扩展名(目录.getName());
            if (扩展名.equals(查找扩展名)) {
                查找文件.add(目录.toString());
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
}
