package phase04;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Phase 4 - Lesson 2: 字节流
 * 
 * 🎯 学习目标:
 * 1. 理解字节流的概念和体系
 * 2. 掌握 InputStream 和 OutputStream
 * 3. 学会使用缓冲流提高性能
 * 4. 理解 try-with-resources 自动关闭
 */
public class ByteStreamDemo {

    // 测试目录
    private static final String TEST_DIR = "target/io-test";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 2: 字节流");
        System.out.println("=".repeat(60));

        // 确保测试目录存在
        new File(TEST_DIR).mkdirs();

        // 1. 字节流体系
        System.out.println("\n【1. 字节流体系】");
        System.out.println("""
                InputStream (抽象类)
                ├── FileInputStream      文件输入
                ├── ByteArrayInputStream 字节数组输入
                ├── BufferedInputStream  缓冲输入 (装饰器)
                ├── DataInputStream      基本类型输入 (装饰器)
                └── ObjectInputStream    对象输入 (装饰器)

                OutputStream (抽象类)
                ├── FileOutputStream      文件输出
                ├── ByteArrayOutputStream 字节数组输出
                ├── BufferedOutputStream  缓冲输出 (装饰器)
                ├── DataOutputStream      基本类型输出 (装饰器)
                └── ObjectOutputStream    对象输出 (装饰器)
                """);

        // 2. FileOutputStream - 写入字节
        System.out.println("【2. FileOutputStream - 写入字节】");

        String filePath = TEST_DIR + "/bytes.dat";

        // 传统方式 (需要手动关闭)
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);

            // 写入单个字节
            fos.write(65); // 'A'
            fos.write(66); // 'B'
            fos.write(67); // 'C'

            // 写入字节数组
            byte[] data = { 68, 69, 70 }; // 'D', 'E', 'F'
            fos.write(data);

            // 写入部分数组
            byte[] more = { 71, 72, 73, 74, 75 }; // 'G' to 'K'
            fos.write(more, 1, 3); // 写入 'H', 'I', 'J'

            // 写入字符串的字节
            fos.write("\nHello, Bytes!".getBytes());

            fos.flush(); // 强制刷新缓冲区
            System.out.println("写入完成: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 手动关闭资源
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 3. FileInputStream - 读取字节
        System.out.println("\n【3. FileInputStream - 读取字节】");

        // try-with-resources (推荐)
        try (FileInputStream fis = new FileInputStream(filePath)) {
            System.out.println("文件大小: " + fis.available() + " 字节");

            // 逐字节读取
            System.out.print("逐字节读取: ");
            int b;
            while ((b = fis.read()) != -1) {
                if (b >= 32 && b < 127) {
                    System.out.print((char) b);
                } else {
                    System.out.print("[" + b + "]");
                }
            }
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 批量读取
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);

            System.out.println("批量读取 " + bytesRead + " 字节");
            System.out.println("内容: " + new String(buffer, 0, bytesRead));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4. 追加写入
        System.out.println("\n【4. 追加写入】");

        try (FileOutputStream fos2 = new FileOutputStream(filePath, true)) { // append = true
            fos2.write("\n追加的内容".getBytes());
            System.out.println("追加写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. BufferedInputStream/BufferedOutputStream - 缓冲流
        System.out.println("\n【5. 缓冲流】");

        String bufferedFile = TEST_DIR + "/buffered.dat";

        // 缓冲写入
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(bufferedFile), 8192)) { // 8KB 缓冲区

            for (int i = 0; i < 10000; i++) {
                bos.write(("Line " + i + "\n").getBytes());
            }
            // 缓冲流会在关闭时自动 flush
            System.out.println("缓冲写入 10000 行完成");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 缓冲读取
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(bufferedFile))) {

            byte[] buffer = new byte[1024];
            int totalBytes = 0;
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }
            System.out.println("缓冲读取总字节: " + totalBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 6. 性能对比
        System.out.println("\n【6. 性能对比: 无缓冲 vs 有缓冲】");

        String perfFile = TEST_DIR + "/perf.dat";
        int dataSize = 100_000;
        byte[] testData = new byte[dataSize];
        Arrays.fill(testData, (byte) 'X');

        // 无缓冲写入
        long start = System.nanoTime();
        try (FileOutputStream fos3 = new FileOutputStream(perfFile)) {
            for (byte b : testData) {
                fos3.write(b); // 逐字节写入 (低效)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long unbufferedWrite = System.nanoTime() - start;

        // 有缓冲写入
        start = System.nanoTime();
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(perfFile))) {
            for (byte b : testData) {
                bos.write(b); // 缓冲写入
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long bufferedWrite = System.nanoTime() - start;

        // 批量写入
        start = System.nanoTime();
        try (FileOutputStream fos4 = new FileOutputStream(perfFile)) {
            fos4.write(testData); // 一次写入整个数组
        } catch (IOException e) {
            e.printStackTrace();
        }
        long bulkWrite = System.nanoTime() - start;

        System.out.printf("无缓冲逐字节: %,d ns%n", unbufferedWrite);
        System.out.printf("有缓冲逐字节: %,d ns (%.1fx 提升)%n",
                bufferedWrite, (double) unbufferedWrite / bufferedWrite);
        System.out.printf("批量写入:     %,d ns (%.1fx 提升)%n",
                bulkWrite, (double) unbufferedWrite / bulkWrite);

        // 7. DataInputStream/DataOutputStream - 基本类型 IO
        System.out.println("\n【7. DataInputStream/DataOutputStream】");

        String dataFile = TEST_DIR + "/data.dat";

        // 写入基本类型
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(dataFile)))) {

            dos.writeBoolean(true);
            dos.writeByte(127);
            dos.writeChar('中');
            dos.writeShort(32767);
            dos.writeInt(Integer.MAX_VALUE);
            dos.writeLong(Long.MAX_VALUE);
            dos.writeFloat(3.14f);
            dos.writeDouble(Math.PI);
            dos.writeUTF("Hello, 世界!"); // 带长度的 UTF-8 字符串

            System.out.println("写入基本类型数据完成");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取基本类型 (顺序必须一致!)
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(dataFile)))) {

            System.out.println("boolean: " + dis.readBoolean());
            System.out.println("byte:    " + dis.readByte());
            System.out.println("char:    " + dis.readChar());
            System.out.println("short:   " + dis.readShort());
            System.out.println("int:     " + dis.readInt());
            System.out.println("long:    " + dis.readLong());
            System.out.println("float:   " + dis.readFloat());
            System.out.println("double:  " + dis.readDouble());
            System.out.println("UTF:     " + dis.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 8. ByteArrayInputStream/ByteArrayOutputStream
        System.out.println("\n【8. ByteArrayInputStream/ByteArrayOutputStream】");

        // 内存中的字节流，不涉及实际文件
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write("Hello ".getBytes());
            baos.write("Memory ".getBytes());
            baos.write("Stream!".getBytes());

            byte[] result = baos.toByteArray();
            System.out.println("内存输出流结果: " + new String(result));
            System.out.println("字节数: " + result.length);

            // 从字节数组读取
            try (ByteArrayInputStream bais = new ByteArrayInputStream(result)) {
                byte[] buffer = new byte[6];
                int len = bais.read(buffer);
                System.out.println("读取前6字节: " + new String(buffer, 0, len));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 9. 文件复制示例
        System.out.println("\n【9. 文件复制示例】");

        String srcFile = "src/main/java/phase04/README.md";
        String destFile = TEST_DIR + "/README_copy.md";

        if (new File(srcFile).exists()) {
            try {
                long copied = copyFile(srcFile, destFile);
                System.out.println("复制完成: " + copied + " 字节");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 字节流适合处理二进制数据 (图片、音频、视频等)");
        System.out.println("💡 使用缓冲流可以显著提高 IO 性能");
        System.out.println("💡 始终使用 try-with-resources 自动关闭资源");
        System.out.println("=".repeat(60));
    }

    /**
     * 使用缓冲流复制文件
     */
    private static long copyFile(String src, String dest) throws IOException {
        long totalBytes = 0;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {

            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
        }

        return totalBytes;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 字节流基类:
 * - InputStream: 所有字节输入流的父类
 * - OutputStream: 所有字节输出流的父类
 * 
 * 2. 核心方法:
 * - read(): 读取一个字节，返回 -1 表示结束
 * - read(byte[]): 批量读取，返回实际读取字节数
 * - write(int): 写入一个字节
 * - write(byte[]): 批量写入
 * - flush(): 刷新缓冲区
 * - close(): 关闭流
 * 
 * 3. 装饰器模式:
 * - 缓冲流包装节点流: new BufferedInputStream(new FileInputStream(...))
 * - 可以多层包装
 * 
 * 4. 最佳实践:
 * - 使用 try-with-resources 自动关闭
 * - 使用缓冲流提高性能
 * - 批量读写优于逐字节读写
 * 
 * 🏃 练习:
 * 1. 实现一个文件加密工具 (XOR 加密)
 * 2. 统计一个二进制文件中每个字节值出现的次数
 * 3. 合并多个文件为一个文件
 */
