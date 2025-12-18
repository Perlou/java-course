package phase04.xor;

import java.io.File;
import java.util.Scanner;

/**
 * Main.java - 命令行界面
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        if (args.length >= 3) {
            // 命令行模式
            handleCommandLineArgs(args);
        } else {
            // 交互模式
            runInteractiveMode();
        }
    }

    private static void printBanner() {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     🔐 XOR 文件加密工具 v1.0              ║");
        System.out.println("║     Java 实现                              ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void runInteractiveMode() {
        while (true) {
            System.out.println("请选择操作:");
            System.out.println("  [1] 加密文件");
            System.out.println("  [2] 解密文件");
            System.out.println("  [3] 加密文本");
            System.out.println("  [4] 退出");
            System.out.print("\n请输入选项 (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    processFile(true);
                    break;
                case "2":
                    processFile(false);
                    break;
                case "3":
                    processText();
                    break;
                case "4":
                    System.out.println("再见! 👋");
                    return;
                default:
                    System.out.println("❌ 无效选项，请重试\n");
            }
        }
    }

    private static void processFile(boolean isEncrypt) {
        String operation = isEncrypt ? "加密" : "解密";

        System.out.print("\n请输入文件路径: ");
        String inputPath = scanner.nextLine().trim();

        // 移除可能的引号
        inputPath = inputPath.replace("\"", "").replace("'", "");

        File file = new File(inputPath);
        if (!file.exists()) {
            System.out.println("❌ 文件不存在: " + inputPath + "\n");
            return;
        }

        System.out.print("请输入密钥: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("❌ 密钥不能为空!\n");
            return;
        }

        System.out.print("输出文件路径 (直接回车使用默认): ");
        String outputPath = scanner.nextLine().trim();

        if (outputPath.isEmpty()) {
            outputPath = isEncrypt ? inputPath + ".encrypted"
                    : (inputPath.endsWith(".encrypted") ? inputPath.substring(0, inputPath.length() - 10)
                            : inputPath + ".decrypted");
        }

        try {
            FileEncryptor encryptor = new FileEncryptor(password);

            // 设置进度监听
            encryptor.setProgressListener(new FileEncryptor.ProgressListener() {
                private int lastPercentage = -1;

                @Override
                public void onProgress(long bytesProcessed, long totalBytes, int percentage) {
                    if (percentage != lastPercentage && percentage % 10 == 0) {
                        printProgressBar(percentage);
                        lastPercentage = percentage;
                    }
                }

                @Override
                public void onComplete(String message) {
                    System.out.println("\n✅ " + message);
                }

                @Override
                public void onError(String error) {
                    System.out.println("\n❌ " + error);
                }
            });

            System.out.println("\n正在" + operation + "...");
            long startTime = System.currentTimeMillis();

            encryptor.processFile(inputPath, outputPath);

            long endTime = System.currentTimeMillis();
            System.out.printf("⏱ 耗时: %.2f 秒\n\n", (endTime - startTime) / 1000.0);

        } catch (Exception e) {
            System.out.println("❌ " + operation + "失败: " + e.getMessage() + "\n");
        }
    }

    private static void processText() {
        System.out.print("\n请输入要加密的文本: ");
        String text = scanner.nextLine();

        System.out.print("请输入密钥: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("❌ 密钥不能为空!\n");
            return;
        }

        FileEncryptor encryptor = new FileEncryptor(password);
        byte[] encrypted = encryptor.encrypt(text.getBytes());

        // 显示十六进制结果
        StringBuilder hex = new StringBuilder();
        for (byte b : encrypted) {
            hex.append(String.format("%02X ", b));
        }

        System.out.println("\n📝 原文: " + text);
        System.out.println("🔐 加密 (HEX): " + hex.toString().trim());
        System.out.println("🔓 解密验证: " + new String(encryptor.decrypt(encrypted)));
        System.out.println();
    }

    private static void printProgressBar(int percentage) {
        int filled = percentage / 5;
        StringBuilder bar = new StringBuilder("\r[");
        for (int i = 0; i < 20; i++) {
            if (i < filled)
                bar.append("█");
            else
                bar.append("░");
        }
        bar.append("] ").append(percentage).append("%");
        System.out.print(bar);
    }

    private static void handleCommandLineArgs(String[] args) {
        // java Main <encrypt|decrypt> <file> <password>
        String operation = args[0].toLowerCase();
        String filePath = args[1];
        String password = args[2];

        FileEncryptor encryptor = new FileEncryptor(password);

        try {
            if (operation.equals("encrypt") || operation.equals("e")) {
                encryptor.encryptFile(filePath);
                System.out.println("✅ 加密完成!");
            } else if (operation.equals("decrypt") || operation.equals("d")) {
                encryptor.decryptFile(filePath);
                System.out.println("✅ 解密完成!");
            } else {
                System.out.println("用法: java Main <encrypt|decrypt> <file> <password>");
            }
        } catch (Exception e) {
            System.out.println("❌ 操作失败: " + e.getMessage());
        }
    }
}