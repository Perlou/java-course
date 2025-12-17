package phase04;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Phase 4 - Lesson 5: NIO.2 Files 工具类
 * 
 * 🎯 学习目标:
 * 1. 掌握 Files 工具类的常用方法
 * 2. 学会使用 Path 进行路径操作
 * 3. 理解 WatchService 文件监控
 * 4. 熟练使用 Files.walk 遍历目录
 */
public class NioFileDemo {

    private static final String TEST_DIR = "target/nio-files-test";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 5: NIO.2 Files 工具类");
        System.out.println("=".repeat(60));

        try {
            // 创建测试目录
            Path testDir = Paths.get(TEST_DIR);
            Files.createDirectories(testDir);

            // 1. 文件/目录创建
            System.out.println("\n【1. 文件/目录创建】");

            // 创建目录
            Path subDir = testDir.resolve("subdir/nested");
            Files.createDirectories(subDir);
            System.out.println("创建目录: " + subDir);

            // 创建文件
            Path newFile = testDir.resolve("test.txt");
            if (!Files.exists(newFile)) {
                Files.createFile(newFile);
                System.out.println("创建文件: " + newFile);
            }

            // 创建临时文件
            Path tempFile = Files.createTempFile(testDir, "temp_", ".txt");
            System.out.println("创建临时文件: " + tempFile.getFileName());

            // 创建临时目录
            Path tempDir = Files.createTempDirectory(testDir, "tempdir_");
            System.out.println("创建临时目录: " + tempDir.getFileName());

            // 2. 文件读写
            System.out.println("\n【2. 文件读写】");

            Path dataFile = testDir.resolve("data.txt");

            // 写入字符串
            Files.writeString(dataFile, "Hello, NIO Files!\n第二行内容\n第三行");
            System.out.println("writeString 完成");

            // 读取全部为字符串
            String content = Files.readString(dataFile);
            System.out.println("readString:\n" + content);

            // 写入行
            Path linesFile = testDir.resolve("lines.txt");
            List<String> lines = List.of("Line 1", "Line 2", "Line 3", "中文行");
            Files.write(linesFile, lines);
            System.out.println("\nwrite(List) 完成");

            // 读取所有行
            List<String> readLines = Files.readAllLines(linesFile, StandardCharsets.UTF_8);
            System.out.println("readAllLines: " + readLines);

            // 写入字节
            Path binaryFile = testDir.resolve("binary.dat");
            byte[] bytes = { 0x48, 0x65, 0x6C, 0x6C, 0x6F }; // "Hello"
            Files.write(binaryFile, bytes);

            // 读取字节
            byte[] readBytes = Files.readAllBytes(binaryFile);
            System.out.println("readAllBytes: " + new String(readBytes));

            // 3. 追加写入
            System.out.println("\n【3. 追加写入】");

            Files.writeString(dataFile, "\n追加内容1", StandardOpenOption.APPEND);
            Files.write(dataFile, List.of("追加内容2", "追加内容3"), StandardOpenOption.APPEND);

            System.out.println("追加后内容:");
            Files.lines(dataFile).forEach(line -> System.out.println("  " + line));

            // 4. 流式读取 (大文件友好)
            System.out.println("\n【4. 流式读取】");

            // 创建一个较大的文件
            Path bigFile = testDir.resolve("big.txt");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("Line ").append(i).append(": Some content here\n");
            }
            Files.writeString(bigFile, sb.toString());

            // 使用 lines() 流式处理
            try (Stream<String> stream = Files.lines(bigFile)) {
                long lineCount = stream.count();
                System.out.println("文件行数: " + lineCount);
            }

            // 统计包含特定内容的行
            try (Stream<String> stream = Files.lines(bigFile)) {
                long matchCount = stream.filter(line -> line.contains("500")).count();
                System.out.println("包含 '500' 的行数: " + matchCount);
            }

            // 5. 文件属性
            System.out.println("\n【5. 文件属性】");

            System.out.println("data.txt 属性:");
            System.out.println("  存在: " + Files.exists(dataFile));
            System.out.println("  不存在: " + Files.notExists(dataFile));
            System.out.println("  是普通文件: " + Files.isRegularFile(dataFile));
            System.out.println("  是目录: " + Files.isDirectory(dataFile));
            System.out.println("  可读: " + Files.isReadable(dataFile));
            System.out.println("  可写: " + Files.isWritable(dataFile));
            System.out.println("  可执行: " + Files.isExecutable(dataFile));
            System.out.println("  是隐藏文件: " + Files.isHidden(dataFile));
            System.out.println("  大小: " + Files.size(dataFile) + " 字节");
            System.out.println("  最后修改: " + Files.getLastModifiedTime(dataFile));

            // 获取详细属性
            BasicFileAttributes attrs = Files.readAttributes(dataFile, BasicFileAttributes.class);
            System.out.println("  创建时间: " + attrs.creationTime());
            System.out.println("  最后访问: " + attrs.lastAccessTime());

            // 修改属性
            Files.setLastModifiedTime(dataFile, FileTime.from(Instant.now()));

            // 6. 文件复制、移动、删除
            System.out.println("\n【6. 文件操作】");

            // 复制
            Path copyDest = testDir.resolve("data_copy.txt");
            Files.copy(dataFile, copyDest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("复制: " + dataFile.getFileName() + " -> " + copyDest.getFileName());

            // 移动/重命名
            Path moveDest = testDir.resolve("data_moved.txt");
            Files.move(copyDest, moveDest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("移动: " + copyDest.getFileName() + " -> " + moveDest.getFileName());

            // 删除
            Files.delete(moveDest);
            System.out.println("删除: " + moveDest.getFileName());

            // 安全删除 (不存在不抛异常)
            boolean deleted = Files.deleteIfExists(Paths.get(TEST_DIR + "/nonexistent.txt"));
            System.out.println("删除不存在的文件: " + deleted);

            // 7. 目录遍历
            System.out.println("\n【7. 目录遍历】");

            // 创建一些测试目录结构
            Files.createDirectories(testDir.resolve("a/b/c"));
            Files.createDirectories(testDir.resolve("d/e"));
            Files.writeString(testDir.resolve("a/file1.txt"), "content");
            Files.writeString(testDir.resolve("a/b/file2.java"), "content");
            Files.writeString(testDir.resolve("d/file3.txt"), "content");

            // list() - 列出直接子项
            System.out.println("list() 直接子项:");
            try (Stream<Path> stream = Files.list(testDir)) {
                stream.forEach(p -> System.out.println("  " + p.getFileName()));
            }

            // walk() - 递归遍历
            System.out.println("\nwalk() 递归遍历:");
            try (Stream<Path> stream = Files.walk(testDir)) {
                stream.forEach(p -> {
                    int depth = testDir.relativize(p).getNameCount();
                    String indent = "  ".repeat(depth);
                    String type = Files.isDirectory(p) ? "📁" : "📄";
                    System.out.println(indent + type + " " + p.getFileName());
                });
            }

            // walk() 指定深度
            System.out.println("\nwalk(maxDepth=1):");
            try (Stream<Path> stream = Files.walk(testDir, 1)) {
                stream.filter(p -> !p.equals(testDir))
                        .forEach(p -> System.out.println("  " + p.getFileName()));
            }

            // 8. 查找文件
            System.out.println("\n【8. 查找文件】");

            // find() - 带条件查找
            System.out.println("查找所有 .txt 文件:");
            try (Stream<Path> stream = Files.find(testDir, Integer.MAX_VALUE,
                    (path, fileAttrs) -> fileAttrs.isRegularFile()
                            && path.toString().endsWith(".txt"))) {
                stream.forEach(p -> System.out.println("  " + testDir.relativize(p)));
            }

            // 使用 PathMatcher
            System.out.println("\n使用 glob 模式匹配 *.java:");
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");
            try (Stream<Path> stream = Files.walk(testDir)) {
                stream.filter(p -> matcher.matches(p.getFileName()))
                        .forEach(p -> System.out.println("  " + testDir.relativize(p)));
            }

            // 9. Files.walkFileTree (更精细控制)
            System.out.println("\n【9. walkFileTree】");

            Files.walkFileTree(testDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println("进入目录: " + testDir.relativize(dir));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println("  访问文件: " + file.getFileName()
                            + " (" + attrs.size() + " bytes)");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    System.out.println("离开目录: " + testDir.relativize(dir));
                    return FileVisitResult.CONTINUE;
                }
            });

            // 10. 递归删除目录
            System.out.println("\n【10. 递归删除目录】");

            Path toDelete = testDir.resolve("a");
            if (Files.exists(toDelete)) {
                // 必须先删除内容
                try (Stream<Path> stream = Files.walk(toDelete)) {
                    stream.sorted(Comparator.reverseOrder())
                            .forEach(p -> {
                                try {
                                    Files.delete(p);
                                    System.out.println("  删除: " + testDir.relativize(p));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }

            // 11. 创建符号链接
            System.out.println("\n【11. 符号链接】");

            Path linkTarget = testDir.resolve("data.txt");
            Path symLink = testDir.resolve("data_link.txt");

            try {
                Files.deleteIfExists(symLink);
                Files.createSymbolicLink(symLink, linkTarget);
                System.out.println("创建符号链接: " + symLink.getFileName() + " -> " + linkTarget.getFileName());
                System.out.println("是符号链接: " + Files.isSymbolicLink(symLink));
                System.out.println("链接目标: " + Files.readSymbolicLink(symLink));
            } catch (UnsupportedOperationException e) {
                System.out.println("当前系统/权限不支持符号链接");
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("💡 Files 是 NIO.2 最常用的工具类");
            System.out.println("💡 readString/writeString 是 Java 11+ 的便捷方法");
            System.out.println("💡 使用 try-with-resources 处理返回 Stream 的方法");
            System.out.println("=".repeat(60));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 文件读写:
 * - readString/writeString: 字符串读写 (Java 11+)
 * - readAllLines/write: 行读写
 * - readAllBytes/write: 字节读写
 * - lines(): 流式读取 (大文件友好)
 * 
 * 2. 文件操作:
 * - createFile/createDirectory/createDirectories
 * - copy/move/delete/deleteIfExists
 * 
 * 3. 目录遍历:
 * - list(): 直接子项
 * - walk(): 递归遍历
 * - find(): 条件查找
 * - walkFileTree(): 精细控制
 * 
 * 4. 文件属性:
 * - exists/notExists, isDirectory/isRegularFile
 * - isReadable/isWritable/isExecutable
 * - size, getLastModifiedTime
 * - readAttributes
 * 
 * 🏃 练习:
 * 1. 统计一个目录下所有 Java 文件的总行数
 * 2. 查找目录中最大的 10 个文件
 * 3. 实现目录同步工具 (复制新增、删除多余)
 */
