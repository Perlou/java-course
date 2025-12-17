package phase04;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Phase 4 - Lesson 1: File 类与路径操作
 * 
 * 🎯 学习目标:
 * 1. 理解 File 类的作用
 * 2. 掌握文件和目录的基本操作
 * 3. 了解 Path 与 Paths (NIO)
 * 4. 学会遍历目录结构
 */
public class FileIODemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 1: File 类与路径操作");
        System.out.println("=".repeat(60));

        // 1. File 对象创建
        System.out.println("\n【1. File 对象创建】");

        // 创建 File 对象 (不会创建实际文件)
        File file1 = new File("test.txt");
        File file2 = new File("/Users/example/documents", "report.pdf");
        File file3 = new File(new File("/Users/example"), "config.json");

        System.out.println("file1 路径: " + file1.getPath());
        System.out.println("file2 路径: " + file2.getPath());
        System.out.println("file3 路径: " + file3.getPath());

        // 2. 获取文件信息
        System.out.println("\n【2. 获取文件信息】");

        File currentDir = new File(".");
        System.out.println("当前目录: " + currentDir.getAbsolutePath());

        try {
            System.out.println("规范路径: " + currentDir.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用项目中的实际文件演示
        File srcDir = new File("src/main/java/phase04");
        if (srcDir.exists()) {
            System.out.println("\n源码目录信息:");
            System.out.println("  存在: " + srcDir.exists());
            System.out.println("  是目录: " + srcDir.isDirectory());
            System.out.println("  是文件: " + srcDir.isFile());
            System.out.println("  可读: " + srcDir.canRead());
            System.out.println("  可写: " + srcDir.canWrite());
            System.out.println("  绝对路径: " + srcDir.getAbsolutePath());
        }

        // 3. 文件和目录操作
        System.out.println("\n【3. 文件和目录操作】");

        File testDir = new File("target/test-files");
        File testFile = new File(testDir, "sample.txt");

        try {
            // 创建目录 (包括父目录)
            if (testDir.mkdirs()) {
                System.out.println("创建目录: " + testDir.getPath());
            }

            // 创建文件
            if (testFile.createNewFile()) {
                System.out.println("创建文件: " + testFile.getPath());
            }

            // 文件信息
            System.out.println("文件大小: " + testFile.length() + " 字节");
            System.out.println("最后修改: " + new java.util.Date(testFile.lastModified()));

            // 重命名
            File renamedFile = new File(testDir, "renamed.txt");
            if (testFile.renameTo(renamedFile)) {
                System.out.println("重命名为: " + renamedFile.getName());
            }

            // 删除文件
            if (renamedFile.delete()) {
                System.out.println("删除文件: " + renamedFile.getName());
            }

        } catch (IOException e) {
            System.err.println("IO 错误: " + e.getMessage());
        }

        // 4. 目录遍历
        System.out.println("\n【4. 目录遍历】");

        File phase04Dir = new File("src/main/java/phase04");
        if (phase04Dir.exists() && phase04Dir.isDirectory()) {
            // list() - 返回文件名数组
            String[] fileNames = phase04Dir.list();
            System.out.println("list() 结果:");
            if (fileNames != null) {
                for (String name : fileNames) {
                    System.out.println("  " + name);
                }
            }

            // listFiles() - 返回 File 对象数组
            System.out.println("\nlistFiles() 结果:");
            File[] files = phase04Dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String type = f.isDirectory() ? "[DIR]" : "[FILE]";
                    System.out.printf("  %s %s (%d bytes)%n",
                            type, f.getName(), f.length());
                }
            }

            // 使用过滤器
            System.out.println("\n只列出 .java 文件:");
            File[] javaFiles = phase04Dir.listFiles((dir, name) -> name.endsWith(".java"));
            if (javaFiles != null) {
                for (File f : javaFiles) {
                    System.out.println("  " + f.getName());
                }
            }
        }

        // 5. 递归遍历目录
        System.out.println("\n【5. 递归遍历目录】");

        File srcRoot = new File("src/main/java");
        if (srcRoot.exists()) {
            System.out.println("递归列出前 10 个文件:");
            listFilesRecursively(srcRoot, 0, new int[] { 0 }, 10);
        }

        // 6. Path 与 Paths (NIO.2)
        System.out.println("\n【6. Path 与 Paths (NIO.2)】");

        // 创建 Path
        Path path1 = Paths.get("src/main/java/phase04");
        Path path2 = Path.of("src", "main", "java", "phase04"); // Java 11+

        System.out.println("Path 对象: " + path1);
        System.out.println("文件名: " + path1.getFileName());
        System.out.println("父路径: " + path1.getParent());
        System.out.println("根路径: " + path1.getRoot());
        System.out.println("路径元素数: " + path1.getNameCount());

        // 路径操作
        Path basePath = Paths.get("/Users/example/projects");
        Path relativePath = Paths.get("module/src/Main.java");
        Path resolvedPath = basePath.resolve(relativePath);
        System.out.println("\n路径解析: " + resolvedPath);

        // 相对路径
        Path path3 = Paths.get("/Users/example/projects/app");
        Path path4 = Paths.get("/Users/example/documents");
        Path relative = path3.relativize(path4);
        System.out.println("相对路径: " + relative);

        // 规范化路径
        Path messyPath = Paths.get("/Users/example/../example/./documents");
        System.out.println("规范化前: " + messyPath);
        System.out.println("规范化后: " + messyPath.normalize());

        // 7. Path 与 File 转换
        System.out.println("\n【7. Path 与 File 转换】");

        File file = new File("test.txt");
        Path path = file.toPath(); // File → Path
        File fileAgain = path.toFile(); // Path → File

        System.out.println("File → Path: " + path);
        System.out.println("Path → File: " + fileAgain);

        // 8. Files 工具类预览
        System.out.println("\n【8. Files 工具类预览】");

        Path readmePath = Paths.get("src/main/java/phase04/README.md");
        if (Files.exists(readmePath)) {
            try {
                System.out.println("README.md 信息:");
                System.out.println("  存在: " + Files.exists(readmePath));
                System.out.println("  是普通文件: " + Files.isRegularFile(readmePath));
                System.out.println("  可读: " + Files.isReadable(readmePath));
                System.out.println("  大小: " + Files.size(readmePath) + " 字节");
                System.out.println("  最后修改: " + Files.getLastModifiedTime(readmePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 File 类是旧式 IO，Path 是 NIO.2 的新方式");
        System.out.println("💡 推荐使用 Path + Files 进行文件操作");
        System.out.println("=".repeat(60));
    }

    /**
     * 递归遍历目录
     */
    private static void listFilesRecursively(File dir, int depth, int[] count, int max) {
        if (count[0] >= max)
            return;

        File[] files = dir.listFiles();
        if (files == null)
            return;

        // 先排序
        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareTo(b.getName());
        });

        for (File file : files) {
            if (count[0] >= max)
                return;

            String indent = "  ".repeat(depth);
            if (file.isDirectory()) {
                System.out.println(indent + "📁 " + file.getName() + "/");
                listFilesRecursively(file, depth + 1, count, max);
            } else {
                System.out.println(indent + "📄 " + file.getName());
                count[0]++;
            }
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. File 类:
 * - 代表文件或目录的抽象路径
 * - 提供文件属性查询、创建、删除、重命名等操作
 * - 不能直接读写文件内容
 * 
 * 2. 常用方法:
 * - 创建: createNewFile(), mkdir(), mkdirs()
 * - 删除: delete()
 * - 判断: exists(), isFile(), isDirectory(), canRead(), canWrite()
 * - 获取: getName(), getPath(), getAbsolutePath(), length()
 * - 遍历: list(), listFiles()
 * 
 * 3. Path 接口 (NIO.2):
 * - Java 7 引入，现代文件路径表示
 * - Paths.get() 或 Path.of() 创建
 * - 提供路径解析、规范化等操作
 * 
 * 4. 选择建议:
 * - 新代码优先使用 Path + Files
 * - File 类更适合兼容旧代码
 * 
 * 🏃 练习:
 * 1. 统计某个目录下所有 Java 文件的总行数
 * 2. 查找目录中最大的 5 个文件
 * 3. 复制目录结构 (不包括文件)
 */
