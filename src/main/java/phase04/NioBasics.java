package phase04;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Phase 4 - Lesson 4: NIO 基础 - Buffer 与 Channel
 * 
 * 🎯 学习目标:
 * 1. 理解 NIO 与传统 IO 的区别
 * 2. 掌握 Buffer 的核心概念和操作
 * 3. 学会使用 Channel 进行文件读写
 * 4. 了解内存映射文件
 */
public class NioBasics {

    private static final String TEST_DIR = "target/nio-test";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 4: NIO 基础 - Buffer 与 Channel");
        System.out.println("=".repeat(60));

        new java.io.File(TEST_DIR).mkdirs();

        // 1. IO vs NIO
        System.out.println("\n【1. IO vs NIO】");
        System.out.println("""
                | 特性     | 传统 IO             | NIO                  |
                |----------|---------------------|----------------------|
                | 面向     | 流 (Stream)         | 缓冲区 (Buffer)      |
                | 阻塞     | 阻塞 IO             | 非阻塞 IO            |
                | 选择器   | 无                  | Selector 多路复用    |
                | 模型     | 一个线程处理一个连接| 一个线程处理多个连接 |
                """);

        // 2. Buffer 核心概念
        System.out.println("【2. Buffer 核心概念】");
        System.out.println("""
                Buffer 是 NIO 的核心，是一块可读/可写的内存区域

                关键属性:
                - capacity: 容量，Buffer 能容纳的最大元素数
                - limit:    限制，第一个不能读/写的位置
                - position: 位置，下一个要读/写的位置
                - mark:     标记，可以通过 reset() 返回此位置

                0 <= mark <= position <= limit <= capacity
                """);

        // 3. ByteBuffer 基本操作
        System.out.println("【3. ByteBuffer 基本操作】");

        // 创建 Buffer
        ByteBuffer buffer = ByteBuffer.allocate(16); // 堆内存
        printBufferState("初始状态", buffer);

        // 写入数据
        buffer.put((byte) 'H');
        buffer.put((byte) 'e');
        buffer.put((byte) 'l');
        buffer.put((byte) 'l');
        buffer.put((byte) 'o');
        printBufferState("写入 'Hello' 后", buffer);

        // 切换到读模式
        buffer.flip();
        printBufferState("flip() 后 (准备读取)", buffer);

        // 读取数据
        System.out.print("读取数据: ");
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }
        System.out.println();
        printBufferState("读取完成后", buffer);

        // 4. Buffer 常用方法
        System.out.println("\n【4. Buffer 常用方法】");

        buffer.rewind(); // 重置 position 为 0，可以重新读取
        printBufferState("rewind() 后", buffer);

        buffer.clear(); // 重置为初始状态 (不清除数据)
        printBufferState("clear() 后", buffer);

        // compact - 压缩 (保留未读数据)
        buffer.put((byte) 'A');
        buffer.put((byte) 'B');
        buffer.put((byte) 'C');
        buffer.flip();
        buffer.get(); // 读取 'A'
        buffer.get(); // 读取 'B'
        buffer.compact(); // 保留 'C'，准备继续写入
        printBufferState("compact() 后 (保留 'C')", buffer);

        // 5. 其他类型的 Buffer
        System.out.println("\n【5. 其他类型的 Buffer】");

        // IntBuffer
        IntBuffer intBuffer = IntBuffer.allocate(10);
        intBuffer.put(100);
        intBuffer.put(200);
        intBuffer.put(300);
        intBuffer.flip();

        System.out.print("IntBuffer 内容: ");
        while (intBuffer.hasRemaining()) {
            System.out.print(intBuffer.get() + " ");
        }
        System.out.println();

        // wrap - 包装现有数组
        int[] array = { 1, 2, 3, 4, 5 };
        IntBuffer wrapped = IntBuffer.wrap(array);
        System.out.println("Wrapped IntBuffer 容量: " + wrapped.capacity());

        // 6. 直接缓冲区 vs 堆缓冲区
        System.out.println("\n【6. 直接缓冲区 vs 堆缓冲区】");

        ByteBuffer heapBuffer = ByteBuffer.allocate(1024); // 堆缓冲区
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024); // 直接缓冲区

        System.out.println("堆缓冲区:   isDirect = " + heapBuffer.isDirect());
        System.out.println("直接缓冲区: isDirect = " + directBuffer.isDirect());

        System.out.println("""

                堆缓冲区 (Heap Buffer):
                - 分配在 JVM 堆内存
                - 受 GC 管理
                - 读写需要复制到内核空间

                直接缓冲区 (Direct Buffer):
                - 分配在操作系统内存
                - 不受 GC 管理
                - 减少一次内存复制，IO 性能更好
                - 分配/释放开销较大
                """);

        // 7. FileChannel 基础
        System.out.println("【7. FileChannel 基础】");

        String filePath = TEST_DIR + "/channel.dat";

        // 写入文件
        try (FileChannel writeChannel = FileChannel.open(
                Path.of(filePath),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {

            ByteBuffer writeBuffer = ByteBuffer.allocate(64);
            writeBuffer.put("Hello, NIO Channel!\n".getBytes());
            writeBuffer.put("This is line 2\n".getBytes());
            writeBuffer.flip(); // 切换为读模式

            int bytesWritten = writeChannel.write(writeBuffer);
            System.out.println("写入 " + bytesWritten + " 字节到 " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取文件
        try (FileChannel readChannel = FileChannel.open(
                Path.of(filePath),
                StandardOpenOption.READ)) {

            ByteBuffer readBuffer = ByteBuffer.allocate(64);
            int bytesRead = readChannel.read(readBuffer);
            System.out.println("读取 " + bytesRead + " 字节");

            readBuffer.flip(); // 切换为读模式
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            System.out.println("内容:\n" + new String(bytes));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 8. 使用 RandomAccessFile 获取 FileChannel
        System.out.println("【8. RandomAccessFile 与 FileChannel】");

        String rafFile = TEST_DIR + "/random.dat";

        try (RandomAccessFile raf = new RandomAccessFile(rafFile, "rw");
                FileChannel channel = raf.getChannel()) {

            // 写入数据
            ByteBuffer buf = ByteBuffer.allocate(48);
            buf.putInt(100);
            buf.putLong(System.currentTimeMillis());
            buf.putDouble(3.14159);
            buf.flip();

            channel.write(buf);
            System.out.println("写入 int, long, double 到 " + rafFile);

            // 定位到文件开头读取
            channel.position(0);

            buf.clear();
            channel.read(buf);
            buf.flip();

            System.out.println("读取: int=" + buf.getInt() +
                    ", long=" + buf.getLong() +
                    ", double=" + buf.getDouble());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 9. 文件复制 (Channel 方式)
        System.out.println("\n【9. 文件复制 (Channel 方式)】");

        String src = TEST_DIR + "/channel.dat";
        String dest = TEST_DIR + "/channel_copy.dat";

        try (FileChannel srcChannel = FileChannel.open(Path.of(src), StandardOpenOption.READ);
                FileChannel destChannel = FileChannel.open(Path.of(dest),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            // 方式1: 使用 Buffer
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while (srcChannel.read(buf) != -1) {
                buf.flip();
                destChannel.write(buf);
                buf.clear();
            }

            System.out.println("Buffer 方式复制完成");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用 transferTo (更高效)
        try (FileChannel srcChannel = FileChannel.open(Path.of(src), StandardOpenOption.READ);
                FileChannel destChannel = FileChannel.open(Path.of(dest + "2"),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long transferred = srcChannel.transferTo(0, srcChannel.size(), destChannel);
            System.out.println("transferTo 复制 " + transferred + " 字节");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 10. 内存映射文件
        System.out.println("\n【10. 内存映射文件 (MappedByteBuffer)】");

        String mmapFile = TEST_DIR + "/mmap.dat";

        try (RandomAccessFile raf = new RandomAccessFile(mmapFile, "rw");
                FileChannel channel = raf.getChannel()) {

            // 创建内存映射
            MappedByteBuffer mbb = channel.map(
                    FileChannel.MapMode.READ_WRITE,
                    0,
                    1024); // 映射 1024 字节

            // 直接操作内存映射区域
            mbb.putInt(12345);
            mbb.putLong(System.currentTimeMillis());
            mbb.put("Memory Mapped File!".getBytes());

            System.out.println("写入内存映射完成 (自动同步到文件)");

            // 读取
            mbb.flip();
            System.out.println("读取: int=" + mbb.getInt());
            System.out.println("读取: long=" + mbb.getLong());

            byte[] strBytes = new byte[19];
            mbb.get(strBytes);
            System.out.println("读取: string=" + new String(strBytes));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("""

                内存映射文件优势:
                - 大文件操作非常高效
                - 操作系统负责数据同步
                - 适合频繁随机访问
                - 多进程可以共享
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Buffer 是 NIO 的核心，flip() 切换读写模式");
        System.out.println("💡 Channel 是双向的，可读可写");
        System.out.println("💡 直接缓冲区适合大量 IO，堆缓冲区适合临时使用");
        System.out.println("=".repeat(60));
    }

    /**
     * 打印 Buffer 状态
     */
    private static void printBufferState(String label, ByteBuffer buffer) {
        System.out.printf("%-20s: position=%d, limit=%d, capacity=%d%n",
                label, buffer.position(), buffer.limit(), buffer.capacity());
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. NIO 核心组件:
 * - Buffer: 数据容器
 * - Channel: 数据通道
 * - Selector: 多路复用 (用于网络 IO)
 * 
 * 2. Buffer 操作:
 * - allocate(): 创建缓冲区
 * - put()/get(): 写入/读取数据
 * - flip(): 切换为读模式
 * - clear(): 清空准备写入
 * - rewind(): 重新读取
 * - compact(): 保留未读数据
 * 
 * 3. Channel 操作:
 * - FileChannel.open(): 打开文件通道
 * - read()/write(): 读写数据
 * - transferTo()/transferFrom(): 高效复制
 * - map(): 内存映射
 * 
 * 4. 最佳实践:
 * - 大文件使用直接缓冲区或内存映射
 * - 小操作使用堆缓冲区
 * - 文件复制优先使用 transferTo
 * 
 * 🏃 练习:
 * 1. 使用 NIO 实现大文件分块读取
 * 2. 使用内存映射实现两个大文件的比较
 * 3. 实现一个简单的二进制日志写入器
 */
