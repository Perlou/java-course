package phase04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Phase 4 - Lesson 3: 字符流
 * 
 * 🎯 学习目标:
 * 1. 理解字符流与字节流的区别
 * 2. 掌握 Reader 和 Writer 体系
 * 3. 学会正确处理字符编码
 * 4. 熟练使用 BufferedReader 和 BufferedWriter
 */
public class CharStreamDemo {

    private static final String TEST_DIR = "target/io-test";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 3: 字符流");
        System.out.println("=".repeat(60));

        new File(TEST_DIR).mkdirs();

        // 1. 字符流体系
        System.out.println("\n【1. 字符流体系】");
        System.out.println("""
                Reader (抽象类)
                ├── FileReader           文件字符输入
                ├── InputStreamReader    字节流转字符流 (桥接)
                ├── BufferedReader       缓冲字符输入
                ├── StringReader         字符串输入
                └── CharArrayReader      字符数组输入

                Writer (抽象类)
                ├── FileWriter           文件字符输出
                ├── OutputStreamWriter   字符流转字节流 (桥接)
                ├── BufferedWriter       缓冲字符输出
                ├── StringWriter         字符串输出
                ├── CharArrayWriter      字符数组输出
                └── PrintWriter          格式化输出
                """);

        // 2. 字节流 vs 字符流
        System.out.println("【2. 字节流 vs 字符流】");
        System.out.println("""
                | 特性     | 字节流            | 字符流           |
                |----------|-------------------|------------------|
                | 处理单位 | 字节 (byte)       | 字符 (char)      |
                | 基类     | InputStream/Output| Reader/Writer    |
                | 适用场景 | 二进制文件        | 文本文件         |
                | 编码处理 | 需要手动处理      | 自动处理         |
                """);

        // 3. FileWriter - 写入文本
        System.out.println("【3. FileWriter - 写入文本】");

        String textFile = TEST_DIR + "/text.txt";

        try (FileWriter fw = new FileWriter(textFile)) {
            // 写入字符串
            fw.write("Hello, 世界!\n");

            // 写入单个字符
            fw.write('A');
            fw.write('\n');

            // 写入字符数组
            char[] chars = { '你', '好', '，', 'J', 'a', 'v', 'a', '\n' };
            fw.write(chars);

            // 写入部分字符数组
            fw.write(chars, 0, 3); // 只写入 "你好，"

            System.out.println("写入文本文件: " + textFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4. FileReader - 读取文本
        System.out.println("\n【4. FileReader - 读取文本】");

        try (FileReader fr = new FileReader(textFile)) {
            // 逐字符读取
            System.out.print("逐字符读取: ");
            int ch;
            while ((ch = fr.read()) != -1) {
                System.out.print((char) ch);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 批量读取
        try (FileReader fr = new FileReader(textFile)) {
            char[] buffer = new char[1024];
            int charsRead = fr.read(buffer);

            System.out.println("批量读取 " + charsRead + " 字符");
            System.out.println("内容: " + new String(buffer, 0, charsRead));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. 字符编码
        System.out.println("\n【5. 字符编码】");

        System.out.println("系统默认编码: " + Charset.defaultCharset());
        System.out.println("常用编码:");
        System.out.println("  UTF-8:    " + StandardCharsets.UTF_8);
        System.out.println("  UTF-16:   " + StandardCharsets.UTF_16);
        System.out.println("  US-ASCII: " + StandardCharsets.US_ASCII);
        System.out.println("  ISO-8859-1: " + StandardCharsets.ISO_8859_1);

        // 6. InputStreamReader/OutputStreamWriter - 指定编码
        System.out.println("\n【6. 指定编码的字符流】");

        String utf8File = TEST_DIR + "/utf8.txt";
        String gbkFile = TEST_DIR + "/gbk.txt";

        // 使用 UTF-8 写入
        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(utf8File), StandardCharsets.UTF_8)) {
            osw.write("UTF-8 编码的中文内容\n");
            osw.write("日本語テスト\n");
            osw.write("한국어 테스트\n");
            System.out.println("UTF-8 写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用 GBK 写入
        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(gbkFile), Charset.forName("GBK"))) {
            osw.write("GBK 编码的中文内容\n");
            System.out.println("GBK 写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用正确的编码读取
        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(utf8File), StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int len = isr.read(buffer);
            System.out.println("UTF-8 读取: " + new String(buffer, 0, len));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 7. BufferedReader/BufferedWriter - 缓冲字符流
        System.out.println("\n【7. BufferedReader/BufferedWriter】");

        String bufferedFile = TEST_DIR + "/buffered.txt";

        // 缓冲写入
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(bufferedFile))) {
            for (int i = 1; i <= 100; i++) {
                bw.write("这是第 " + i + " 行内容");
                bw.newLine(); // 跨平台换行
            }
            System.out.println("缓冲写入 100 行完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 缓冲读取 - 按行读取 (最常用!)
        try (BufferedReader br = new BufferedReader(new FileReader(bufferedFile))) {
            System.out.println("按行读取 (前5行):");
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < 5) {
                System.out.println("  " + line);
                count++;
            }
            System.out.println("  ...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 8. PrintWriter - 格式化输出
        System.out.println("\n【8. PrintWriter - 格式化输出】");

        String printFile = TEST_DIR + "/print.txt";

        // 自动刷新的 PrintWriter
        try (PrintWriter pw = new PrintWriter(new FileWriter(printFile), true)) {
            // 类似 System.out 的方法
            pw.println("println 方法");
            pw.print("print 不换行");
            pw.println();
            pw.printf("格式化: 名字=%s, 年龄=%d, 分数=%.2f%n", "张三", 25, 88.5);

            // 打印各种类型
            pw.println(true);
            pw.println(3.14159);
            pw.println(new int[] { 1, 2, 3 }); // 打印数组引用

            System.out.println("PrintWriter 写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取并显示内容
        try (BufferedReader br = new BufferedReader(new FileReader(printFile))) {
            System.out.println("PrintWriter 输出内容:");
            br.lines().forEach(line -> System.out.println("  " + line));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 9. StringReader/StringWriter
        System.out.println("\n【9. StringReader/StringWriter】");

        String source = "Hello\nWorld\nJava";

        try (StringReader sr = new StringReader(source);
                BufferedReader br = new BufferedReader(sr)) {

            System.out.println("从字符串读取:");
            br.lines().forEach(line -> System.out.println("  " + line));

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.println("写入到字符串");
            pw.println("第二行");
            pw.printf("格式化 %d%n", 100);
        }
        System.out.println("StringWriter 结果: " + sw.toString());

        // 10. 读取 Java 源文件示例
        System.out.println("\n【10. 读取 Java 源文件】");

        String javaFile = "src/main/java/phase04/FileIODemo.java";
        if (new File(javaFile).exists()) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(javaFile), StandardCharsets.UTF_8))) {

                System.out.println(javaFile + " 前10行:");
                br.lines()
                        .limit(10)
                        .forEach(line -> System.out.println("  " + line));
                System.out.println("  ...");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 11. 统计文件行数和字符数
        System.out.println("\n【11. 统计文件信息】");

        if (new File(javaFile).exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(javaFile))) {
                long lineCount = 0;
                long charCount = 0;
                long wordCount = 0;

                String line;
                while ((line = br.readLine()) != null) {
                    lineCount++;
                    charCount += line.length();
                    wordCount += line.split("\\s+").length;
                }

                System.out.println("FileIODemo.java 统计:");
                System.out.println("  行数: " + lineCount);
                System.out.println("  字符数: " + charCount);
                System.out.println("  单词数: " + wordCount);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 字符流适合处理文本文件");
        System.out.println("💡 正确指定编码避免乱码");
        System.out.println("💡 BufferedReader.readLine() 是读取文本最常用的方式");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 字符流 vs 字节流:
 * - 字符流处理文本，字节流处理二进制
 * - 字符流内部使用字节流 + 编码转换
 * 
 * 2. 核心类:
 * - Reader/Writer: 抽象基类
 * - FileReader/FileWriter: 文件读写
 * - InputStreamReader/OutputStreamWriter: 桥接流，可指定编码
 * - BufferedReader/BufferedWriter: 缓冲流，提供 readLine()
 * - PrintWriter: 格式化输出
 * 
 * 3. 编码处理:
 * - FileReader/FileWriter 使用系统默认编码
 * - InputStreamReader/OutputStreamWriter 可指定编码
 * - 推荐始终明确指定 UTF-8
 * 
 * 4. 常用模式:
 * BufferedReader br = new BufferedReader(
 * new InputStreamReader(
 * new FileInputStream(file),
 * StandardCharsets.UTF_8));
 * 
 * 🏃 练习:
 * 1. 统计 Java 文件中的代码行数 (排除空行和注释)
 * 2. 实现一个简单的文本查找替换工具
 * 3. 合并多个文本文件并添加文件名分隔符
 */
