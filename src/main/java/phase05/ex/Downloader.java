package phase05.ex;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Downloader {

    private static final String DOWNLOAD_DIR = "target/downloads";
    private static final int THREAD_COUNT = 4;
    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   📥 多线程下载器 v1.0");
        System.out.println("=".repeat(60));

        new File(DOWNLOAD_DIR).mkdir();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> singleDownload(scanner);
                case "2" -> multiThreadDownload(scanner);
                case "3" -> batchDownload(scanner);
                case "4" -> showDownloads();
                case "0" -> {
                    System.out.println("👋 再见!");
                    return;
                }
                default -> System.out.println("无效选项");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("1. 单线程下载");
        System.out.println("2. 多线程下载 (分块)");
        System.out.println("3. 批量下载");
        System.out.println("4. 查看已下载文件");
        System.out.println("0. 退出");
        System.out.print("请选择 > ");
    }

    /**
     * 单线程下载
     */
    private static void singleDownload(Scanner scanner) {
        System.out.print("输入 URL:");
        String urlStr = scanner.nextLine().trim();

        if (urlStr.isEmpty()) {
            // 使用示例 URL
            urlStr = "https://www.example.com";
        }

        try {
            URL url = new URL(urlStr);
            String fileName = getFileName(url);
            Path outputPath = Paths.get(DOWNLOAD_DIR, fileName);

            System.out.println("下载: " + fileName);
            long startTime = System.currentTimeMillis();

            try (InputStream in = url.openStream();
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long totalBytes = 0;
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                    System.out.print("\r已下载: " + formatSize(totalBytes));
                }

                long duration = System.currentTimeMillis() - startTime;
                System.out.println("\n✅ 下载完成!");
                System.out.println("大小: " + formatSize(totalBytes));
                System.out.println("耗时: " + duration + " ms");
                System.out.println("保存: " + outputPath);

            }
        } catch (Exception e) {
            System.out.println("❌ 下载失败: " + e.getMessage());
        }
    }

    /**
     * 多线程分块下载
     */
    private static void multiThreadDownload(Scanner scanner) {
        System.out.print("输入 URL: ");
        String urlStr = scanner.nextLine().trim();

        if (urlStr.isEmpty()) {
            System.out.println("使用模拟下载演示...");
            simulateMultiThreadDownload();
            return;
        }

        try {
            URL url = new URL(urlStr);
            String fileName = getFileName(url);

            // 获取文件大小
            var connection = url.openConnection();
            long fileSize = connection.getContentLengthLong();
            connection.getInputStream().close();

            if (fileSize <= 0) {
                System.out.println("无法获取文件大小，使用单线程下载");
                singleDownload(scanner);
                return;
            }

            System.out.println("文件大小: " + formatSize(fileSize));
            System.out.print("使用 " + THREAD_COUNT + " 个线程下载");

            // 分块
            long chunkSize = fileSize / THREAD_COUNT;
            Path outpuPath = Paths.get(DOWNLOAD_DIR, fileName);

            // 创建临时文件
            RandomAccessFile raf = new RandomAccessFile(outpuPath.toFile(), "rw");
            raf.setLength(fileSize);
            raf.close();

            // 进度追踪
            AtomicLong totalDownloaded = new AtomicLong(0);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

            long startTime = System.currentTimeMillis();

            // 启动下载线程
            for (int i = 0; i < THREAD_COUNT; i++) {
                long start = i * chunkSize;
                long end = (i == THREAD_COUNT - 1) ? fileSize - 1 : (i + 1) * chunkSize - 1;

                final int threadId = i;

                new Thread(() -> {
                    try {
                        downloadChunk(urlStr, outpuPath.toString(), start, end, threadId, totalDownloaded);
                    } catch (Exception e) {
                        System.out.println("线程 " + threadId + " 失败: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }, "Downloader-" + i).start();
            }

            // 显示进度
            Thread progressThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted() && totalDownloaded.get() < fileSize) {
                    double percent = totalDownloaded.get() * 100.0 / fileSize;
                    System.out.printf("\r进度: %.1f%% (%s / %s)",
                            percent, formatSize(totalDownloaded.get()), formatSize(fileSize));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            progressThread.start();
            latch.await();
            progressThread.interrupt();

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n✅ 下载完成!");
            System.out.println("耗时: " + duration + " ms");
            System.out.println("速度: " + formatSize(fileSize * 1000 / duration) + "/s");

        } catch (Exception e) {
            System.out.println("❌ 下载失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件的一个块
     */
    private static void downloadChunk(String urlStr, String outputFile,
            long start, long end, int threadId, AtomicLong totalDownloaded) throws Exception {
        URL url = new URL(urlStr);
        var connection = url.openConnection();
        connection.setRequestProperty("Range", "bytes=" + start + "-" + end);

        try (InputStream in = connection.getInputStream();
                RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
            raf.seek(start);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                raf.write(buffer, 0, bytesRead);
                totalDownloaded.addAndGet(bytesRead);
            }
        }
    }

    /**
     * 模拟多线程下载
     */
    private static void simulateMultiThreadDownload() {
        long fileSize = 100_000_000; // 100MB

        System.out.println("模拟下载 " + formatSize(fileSize) + " 文件");
        System.out.println("使用 " + THREAD_COUNT + " 个线程");

        AtomicLong totalDownloaded = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long chunkSize = fileSize / THREAD_COUNT;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            long toDownload = (i == THREAD_COUNT - 1) ? fileSize - (chunkSize * (THREAD_COUNT - 1)) : chunkSize;

            final int threadId = i;
            new Thread(() -> {
                long downloaded = 0;
                Random random = new Random();

                while (downloaded < toDownload) {
                    // 模拟下载
                    int chunk = Math.min(random.nextInt(1000000) + 500000,
                            (int) (toDownload - downloaded));
                    downloaded += chunk;
                    totalDownloaded.addAndGet(chunk);

                    try {
                        Thread.sleep(50); // 模拟网络延迟
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                System.out.println("线程 " + threadId + " 完成");
                latch.countDown();
            }, "Sim-" + i).start();
        }

        // 进度显示
        Thread progressThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() &&
                    totalDownloaded.get() < fileSize) {
                double percent = totalDownloaded.get() * 100.0 / fileSize;
                int bars = (int) (percent / 2);
                String progress = "█".repeat(bars) + "░".repeat(50 - bars);
                System.out.printf("\r[%s] %.1f%%", progress, percent);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        progressThread.start();

        try {
            latch.await();
            progressThread.interrupt();

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n✅ 模拟下载完成!");
            System.out.println("耗时: " + duration + " ms");
            System.out.println("速度: " + formatSize(fileSize * 1000 / duration) + "/s");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量下载
     */
    private static void batchDownload(Scanner scanner) {
        System.out.println("输入 URL 列表 (每行一个，空行结束):");

        List<String> urls = new ArrayList<>();
        String line;
        while (!(line = scanner.nextLine().trim()).isEmpty()) {
            urls.add(line);
        }

        if (urls.isEmpty()) {
            System.out.println("使用示例 URL 列表");
            urls = List.of(
                    "https://www.example.com",
                    "https://www.google.com",
                    "https://www.github.com");
        }

        System.out.println("开始批量下载 " + urls.size() + " 个文件...");

        ExecutorService pool = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(urls.size());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (String urlStr : urls) {
            pool.execute(() -> {
                try {
                    URL url = new URL(urlStr);
                    String fileName = getFileName(url);
                    Path outpuPath = Paths.get(DOWNLOAD_DIR, fileName);

                    try (InputStream in = url.openStream(); OutputStream out = Files.newOutputStream(outpuPath)) {
                        in.transferTo(out);
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("❌ " + urlStr + " - " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("\n批量下载完成!");
        System.out.println("成功: " + successCount.get());
        System.out.println("失败: " + failCount.get());
        System.out.println("耗时: " + duration + " ms");
    }

    /**
     * 显示已下载的文件
     */
    private static void showDownloads() {
        File dir = new File(DOWNLOAD_DIR);
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("没有已下载的文件");
            return;

        }

        System.out.println("\n已下载文件:");
        System.out.printf("%-40s %12s%n", "文件名", "大小");
        System.out.println("-".repeat(55));

        long totalSize = 0;
        for (File file : files) {
            System.out.printf("%-40s %12s%n",
                    truncate(file.getName(), 40), formatSize(file.length()));
            totalSize += file.length();
        }

        System.out.println("-".repeat(55));
        System.out.printf("共 %d 个文件，总大小: %s%n", files.length, formatSize(totalSize));
    }

    // ==================== 工具方法 ====================

    private static String getFileName(URL url) {
        String path = url.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.isEmpty()) {
            name = url.getHost() + ".html";
        }
        return name;
    }

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

}
