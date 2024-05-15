package 根.找错误.工具;

import java.io.*;

import static 根.找错误.一.输入地址找错误.sc;

//
public class 输入流工具 {
    InputStream stream;

    public 输入流工具(InputStream stream) {
        this.stream = stream;
    }

    public byte[] readLineBytes() throws IOException {
        byte[] bytes;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int cd;
        while ((cd = stream.read()) != -1) {
            if ((char) (cd) == '\n' || (char) (cd) == '\r') {
                outputStream.write(cd);
                bytes = outputStream.toByteArray();
                return bytes;
            }
            outputStream.write(cd);
        }
        bytes = outputStream.toByteArray();
        return bytes;
    }

    @Deprecated
    public static String 读取一行(InputStream is) {
        byte[] sj = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int cd;
        try {
            while ((cd = is.read(sj)) != -1) {
                for (int k = 0; k < cd; k++) {
                    outputStream.write(sj[k]);
                    if (sj[k] == '\n') {
                        outputStream.flush();
                        byte[] bytes = outputStream.toByteArray();
                        outputStream.reset();
                        return new String(bytes);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static byte[] sj = new byte[1024 * 1024 * 1];
    static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    static int cd;

    public static String 读取一行2(InputStream is) {
        outputStream.reset();
        try {
            while ((cd = is.read(sj)) != -1) {
                outputStream.write(sj, 0, cd);
            }
            outputStream.flush();
            byte[] bytes = outputStream.toByteArray();
            return new String(bytes, "gbk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String 读取一行3(InputStream is, Process process) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line, output = "";
        int exitCode = 0; // 等待命令执行完成
        try {
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
            exitCode = process.waitFor();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (exitCode != 0) {
            // 处理命令执行失败的情况
            System.out.println("Command execution failed with exit code: " + exitCode);
        }
        return output;
    }
}
