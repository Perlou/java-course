package phase04.xor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileEncryptor.java - 文件加密/解密处理器
 */
public class FileEncryptor {

    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区

    private final XORCipher cipher;
    private ProgressListener progressListener;

    // 进度监听器接口
    public interface ProgressListener {
        void onProgress(long bytesProcessed, long totalBytes, int percentage);

        void onComplete(String message);

        void onError(String error);
    }

    public FileEncryptor(String password) {
        this.cipher = new XORCipher(password);
    }

    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

    /**
     * 加密/解密文件
     */
    public void processFile(String inputPath, String outputPath) throws IOException {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        if (!inputFile.exists()) {
            throw new FileNotFoundException("输入文件不存在: " + inputPath);
        }

        long totalBytes = inputFile.length();
        long bytesProcessed = 0;

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long position = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                // 处理缓冲区中的每个字节
                byte[] processed = new byte[bytesRead];
                for (int i = 0; i < bytesRead; i++) {
                    processed[i] = cipher.processByte(buffer[i], (int) ((position + i) % Integer.MAX_VALUE));
                }
                position += bytesRead;

                out.write(processed, 0, bytesRead);
                bytesProcessed += bytesRead;

                // 报告进度
                if (progressListener != null) {
                    int percentage = (int) ((bytesProcessed * 100) / totalBytes);
                    progressListener.onProgress(bytesProcessed, totalBytes, percentage);
                }
            }

            if (progressListener != null) {
                progressListener.onComplete("处理完成！输出文件: " + outputPath);
            }

        } catch (IOException e) {
            if (progressListener != null) {
                progressListener.onError("处理失败: " + e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 加密文件（生成 .encrypted 后缀）
     */
    public void encryptFile(String inputPath) throws IOException {
        String outputPath = inputPath + ".encrypted";
        processFile(inputPath, outputPath);
    }

    /**
     * 解密文件（移除 .encrypted 后缀）
     */
    public void decryptFile(String inputPath) throws IOException {
        String outputPath;
        if (inputPath.endsWith(".encrypted")) {
            outputPath = inputPath.substring(0, inputPath.length() - 10);
        } else {
            outputPath = inputPath + ".decrypted";
        }
        processFile(inputPath, outputPath);
    }

    /**
     * 加密字节数组（用于小数据）
     */
    public byte[] encrypt(byte[] data) {
        return cipher.process(data);
    }

    /**
     * 解密字节数组
     */
    public byte[] decrypt(byte[] data) {
        return cipher.process(data); // XOR 是对称的
    }
}