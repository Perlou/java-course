package phase04;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Phase 4 - 实战项目: 文件批量处理工具
 * 
 * 🎯 项目目标:
 * 1. 综合运用 IO 和 NIO 知识
 * 2. 实现实用的文件处理功能
 * 3. 练习异常处理和用户交互
 */
public class FileTool {

    private static final Scanner scanner = new Scanner(System.in);
    private static Path currentDir = Paths.get(".").toAbsolutePath().normalize();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   📁 文件批量处理工具 v1.0");
        System.out.println("=".repeat(60));
        System.out.println("当前目录: " + currentDir);

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listFiles();
                case "2" -> searchFiles();
                case "3" -> countLines();
                case "4" -> findLargestFiles();
                case "5" -> findDuplicateFiles();
                case "6" -> batchRename();
                case "7" -> mergeTxtFiles();
                case "8" -> changeDirectory();
                case "9" -> fileInfo();
                case "0" -> running = false;
                default -> System.out.println("无效选项，请重新选择");
            }
        }

        System.out.println("\n👋 再见!");
    }

    private static void showMenu() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("📍 当前目录: " + currentDir.getFileName());
        System.out.println("-".repeat(40));
        System.out.println("1. 列出文件");
        System.out.println("2. 搜索文件");
        System.out.println("3. 统计代码行数");
        System.out.println("4. 查找最大文件");
        System.out.println("5. 查找重复文件");
        System.out.println("6. 批量重命名");
        System.out.println("7. 合并文本文件");
        System.out.println("8. 切换目录");
        System.out.println("9. 文件信息");
        System.out.println("0. 退出");
        System.out.print("请选择 > ");
    }

    /**
     * 1. 列出当前目录文件
     */
    private static void listFiles() {
        System.out.print("列出深度 (1=直接子项, 0=全部): ");
        int depth = Integer.parseInt(scanner.nextLine().trim());
        if (depth == 0)
            depth = Integer.MAX_VALUE;

        try (Stream<Path> stream = Files.walk(currentDir, depth)) {
            List<FileInfo> files = stream
                    .filter(p -> !p.equals(currentDir))
                    .map(p -> {
                        try {
                            boolean isDir = Files.isDirectory(p);
                            long size = isDir ? 0 : Files.size(p);
                            return new FileInfo(currentDir.relativize(p), isDir, size);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> {
                        if (a.isDir != b.isDir)
                            return a.isDir ? -1 : 1;
                        return a.path.compareTo(b.path);
                    })
                    .toList();

            System.out.println("\n共 " + files.size() + " 个项目:");
            for (FileInfo f : files) {
                String icon = f.isDir ? "📁" : "📄";
                String size = f.isDir ? "" : formatSize(f.size);
                System.out.printf("  %s %-40s %s%n", icon, f.path, size);
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 2. 搜索文件
     */
    private static void searchFiles() {
        System.out.print("搜索文件名 (支持 * 通配符): ");
        String pattern = scanner.nextLine().trim();

        String glob = pattern.contains("*") ? pattern : "*" + pattern + "*";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        try (Stream<Path> stream = Files.walk(currentDir)) {
            List<Path> results = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .toList();

            System.out.println("\n找到 " + results.size() + " 个文件:");
            for (Path p : results) {
                System.out.println("  " + currentDir.relativize(p));
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 3. 统计代码行数
     */
    private static void countLines() {
        System.out.print("文件扩展名 (如 java, txt): ");
        String ext = scanner.nextLine().trim();
        String pattern = "*." + ext;

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        try (Stream<Path> stream = Files.walk(currentDir)) {
            var stats = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .map(p -> {
                        try {
                            long lines = Files.lines(p, StandardCharsets.UTF_8).count();
                            long codeLines = Files.lines(p, StandardCharsets.UTF_8)
                                    .filter(line -> !line.trim().isEmpty())
                                    .filter(line -> !line.trim().startsWith("//"))
                                    .filter(line -> !line.trim().startsWith("/*"))
                                    .filter(line -> !line.trim().startsWith("*"))
                                    .count();
                            return new LineStats(currentDir.relativize(p), lines, codeLines);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> Long.compare(b.codeLines, a.codeLines))
                    .toList();

            long totalLines = stats.stream().mapToLong(s -> s.lines).sum();
            long totalCode = stats.stream().mapToLong(s -> s.codeLines).sum();

            System.out.println("\n代码统计 (*." + ext + "):");
            System.out.printf("%-40s %8s %8s%n", "文件", "总行数", "代码行");
            System.out.println("-".repeat(60));

            for (LineStats s : stats) {
                System.out.printf("%-40s %8d %8d%n",
                        truncate(s.path.toString(), 40), s.lines, s.codeLines);
            }

            System.out.println("-".repeat(60));
            System.out.printf("%-40s %8d %8d%n", "合计 (" + stats.size() + " 个文件)",
                    totalLines, totalCode);

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 4. 查找最大文件
     */
    private static void findLargestFiles() {
        System.out.print("显示前几个 (默认10): ");
        String input = scanner.nextLine().trim();
        int limit = input.isEmpty() ? 10 : Integer.parseInt(input);

        try (Stream<Path> stream = Files.walk(currentDir)) {
            List<FileInfo> largest = stream
                    .filter(Files::isRegularFile)
                    .map(p -> {
                        try {
                            return new FileInfo(currentDir.relativize(p), false, Files.size(p));
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> Long.compare(b.size, a.size))
                    .limit(limit)
                    .toList();

            System.out.println("\n最大的 " + limit + " 个文件:");
            System.out.printf("%-50s %12s%n", "文件", "大小");
            System.out.println("-".repeat(65));

            for (int i = 0; i < largest.size(); i++) {
                FileInfo f = largest.get(i);
                System.out.printf("%2d. %-47s %12s%n",
                        i + 1, truncate(f.path.toString(), 47), formatSize(f.size));
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 5. 查找重复文件 (基于大小)
     */
    private static void findDuplicateFiles() {
        System.out.println("正在扫描...");

        try (Stream<Path> stream = Files.walk(currentDir)) {
            Map<Long, List<Path>> bySize = new HashMap<>();

            stream.filter(Files::isRegularFile)
                    .forEach(p -> {
                        try {
                            long size = Files.size(p);
                            if (size > 0) {
                                bySize.computeIfAbsent(size, k -> new ArrayList<>()).add(p);
                            }
                        } catch (IOException ignored) {
                        }
                    });

            List<Map.Entry<Long, List<Path>>> duplicates = bySize.entrySet().stream()
                    .filter(e -> e.getValue().size() > 1)
                    .sorted((a, b) -> Long.compare(b.getKey(), a.getKey()))
                    .toList();

            if (duplicates.isEmpty()) {
                System.out.println("未发现重复文件（基于文件大小）");
            } else {
                System.out.println("\n发现 " + duplicates.size() + " 组可能重复的文件:");
                for (var entry : duplicates) {
                    System.out.println("\n大小: " + formatSize(entry.getKey()));
                    for (Path p : entry.getValue()) {
                        System.out.println("  " + currentDir.relativize(p));
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 6. 批量重命名
     */
    private static void batchRename() {
        System.out.print("文件模式 (如 *.txt): ");
        String pattern = scanner.nextLine().trim();
        System.out.print("替换规则 - 查找: ");
        String find = scanner.nextLine();
        System.out.print("替换规则 - 替换为: ");
        String replace = scanner.nextLine();

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        try (Stream<Path> stream = Files.list(currentDir)) {
            List<Path> toRename = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .filter(p -> p.getFileName().toString().contains(find))
                    .toList();

            if (toRename.isEmpty()) {
                System.out.println("没有匹配的文件");
                return;
            }

            System.out.println("\n将重命名以下文件:");
            for (Path p : toRename) {
                String oldName = p.getFileName().toString();
                String newName = oldName.replace(find, replace);
                System.out.println("  " + oldName + " -> " + newName);
            }

            System.out.print("\n确认执行? (y/n): ");
            if (!"y".equalsIgnoreCase(scanner.nextLine().trim())) {
                System.out.println("已取消");
                return;
            }

            int count = 0;
            for (Path p : toRename) {
                String newName = p.getFileName().toString().replace(find, replace);
                Path newPath = p.resolveSibling(newName);
                Files.move(p, newPath, StandardCopyOption.REPLACE_EXISTING);
                count++;
            }
            System.out.println("成功重命名 " + count + " 个文件");

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 7. 合并文本文件
     */
    private static void mergeTxtFiles() {
        System.out.print("文件模式 (如 *.txt): ");
        String pattern = scanner.nextLine().trim();
        System.out.print("输出文件名: ");
        String outputName = scanner.nextLine().trim();

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        Path outputPath = currentDir.resolve(outputName);

        try (Stream<Path> stream = Files.list(currentDir)) {
            List<Path> toMerge = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .filter(p -> !p.equals(outputPath))
                    .sorted()
                    .toList();

            if (toMerge.isEmpty()) {
                System.out.println("没有匹配的文件");
                return;
            }

            System.out.println("将合并 " + toMerge.size() + " 个文件...");

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                for (Path p : toMerge) {
                    writer.write("// ===== " + p.getFileName() + " =====\n");
                    Files.lines(p, StandardCharsets.UTF_8).forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                    writer.newLine();
                }
            }

            System.out.println("合并完成: " + outputPath.getFileName());
            System.out.println("大小: " + formatSize(Files.size(outputPath)));

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    /**
     * 8. 切换目录
     */
    private static void changeDirectory() {
        System.out.println("当前: " + currentDir);
        System.out.print("新目录 (输入..返回上级, 或输入路径): ");
        String input = scanner.nextLine().trim();

        Path newDir;
        if (input.equals("..")) {
            newDir = currentDir.getParent();
        } else {
            newDir = Paths.get(input);
            if (!newDir.isAbsolute()) {
                newDir = currentDir.resolve(input);
            }
        }

        newDir = newDir.normalize();

        if (Files.isDirectory(newDir)) {
            currentDir = newDir;
            System.out.println("已切换到: " + currentDir);
        } else {
            System.out.println("目录不存在: " + newDir);
        }
    }

    /**
     * 9. 文件详细信息
     */
    private static void fileInfo() {
        System.out.print("文件名: ");
        String name = scanner.nextLine().trim();
        Path file = currentDir.resolve(name);

        if (!Files.exists(file)) {
            System.out.println("文件不存在");
            return;
        }

        try {
            var attrs = Files.readAttributes(file, "*");

            System.out.println("\n文件信息: " + file.getFileName());
            System.out.println("-".repeat(40));
            System.out.println("完整路径: " + file.toAbsolutePath());
            System.out.println("类型: " + (Files.isDirectory(file) ? "目录" : "文件"));
            System.out.println("大小: " + formatSize(Files.size(file)));
            System.out.println("创建时间: " + attrs.get("creationTime"));
            System.out.println("修改时间: " + attrs.get("lastModifiedTime"));
            System.out.println("访问时间: " + attrs.get("lastAccessTime"));
            System.out.println("可读: " + Files.isReadable(file));
            System.out.println("可写: " + Files.isWritable(file));
            System.out.println("隐藏: " + Files.isHidden(file));

            if (Files.isRegularFile(file)) {
                String type = Files.probeContentType(file);
                System.out.println("MIME类型: " + (type != null ? type : "未知"));
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private static String formatSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024)
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static String truncate(String s, int maxLen) {
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    // ==================== 辅助记录类 ====================

    record FileInfo(Path path, boolean isDir, long size) {
    }

    record LineStats(Path path, long lines, long codeLines) {
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 Phase 4 的核心知识:
 * - Files.walk/list: 目录遍历
 * - PathMatcher: 文件匹配
 * - Files.lines: 流式读取
 * - Files.readAttributes: 文件属性
 * - BufferedWriter: 文件写入
 * - Stream API: 数据处理
 * 
 * 🎯 扩展任务:
 * 1. 添加文件内容搜索 (grep 功能)
 * 2. 添加目录大小统计
 * 3. 添加文件比较功能
 * 4. 添加压缩/解压功能
 */
