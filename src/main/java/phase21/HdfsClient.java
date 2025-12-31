package phase21;

import java.util.HashMap;
import java.util.Map;

/**
 * Java HDFS 客户端操作详解
 * 
 * 本课件展示如何使用 Java API 操作 HDFS 文件系统。
 * 运行需要真实的 Hadoop 环境，这里提供完整的代码示例和模拟演示。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class HdfsClient {

    public static void main(String[] args) {
        System.out.println("=== Java HDFS 客户端操作 ===\n");
        apiOverview();
        connectToHdfs();
        fileOperations();
        directoryOperations();
        localSimulation();
    }

    private static void apiOverview() {
        System.out.println("【HDFS Java API 概览】\n");
        System.out.println("核心类介绍：");
        System.out.println("  Configuration    - Hadoop 配置类");
        System.out.println("  FileSystem       - 文件系统抽象（核心类）");
        System.out.println("  Path             - 文件/目录路径");
        System.out.println("  FileStatus       - 文件状态信息");
        System.out.println("  FSDataInputStream  - HDFS 输入流");
        System.out.println("  FSDataOutputStream - HDFS 输出流");
        System.out.println();
    }

    private static void connectToHdfs() {
        System.out.println("【连接 HDFS】\n");
        System.out.println("Configuration conf = new Configuration();");
        System.out.println("conf.set(\"fs.defaultFS\", \"hdfs://namenode:9000\");");
        System.out.println("FileSystem fs = FileSystem.get(conf);");
        System.out.println();
    }

    private static void fileOperations() {
        System.out.println("【文件操作示例】\n");
        System.out.println("// 上传文件");
        System.out.println("fs.copyFromLocalFile(new Path(\"/local\"), new Path(\"/hdfs\"));");
        System.out.println();
        System.out.println("// 下载文件");
        System.out.println("fs.copyToLocalFile(new Path(\"/hdfs\"), new Path(\"/local\"));");
        System.out.println();
        System.out.println("// 读取文件");
        System.out.println("FSDataInputStream in = fs.open(new Path(\"/hdfs/file\"));");
        System.out.println();
        System.out.println("// 写入文件");
        System.out.println("FSDataOutputStream out = fs.create(new Path(\"/hdfs/out\"));");
        System.out.println("out.writeBytes(\"Hello HDFS!\");");
        System.out.println();
    }

    private static void directoryOperations() {
        System.out.println("【目录操作】\n");
        System.out.println("// 创建目录");
        System.out.println("fs.mkdirs(new Path(\"/user/data\"));");
        System.out.println();
        System.out.println("// 列出目录");
        System.out.println("FileStatus[] files = fs.listStatus(new Path(\"/user\"));");
        System.out.println();
        System.out.println("// 检查存在");
        System.out.println("boolean exists = fs.exists(new Path(\"/user\"));");
        System.out.println();
    }

    private static void localSimulation() {
        System.out.println("【本地模拟演示】\n");
        Map<String, String> files = new HashMap<>();
        files.put("/user/data/test.txt", "Hello HDFS!");

        System.out.println(">>> 模拟上传文件");
        System.out.println("  文件: /user/data/test.txt");
        System.out.println("  内容: " + files.get("/user/data/test.txt"));

        System.out.println("\n>>> 模拟文件分块");
        String content = "这是模拟大文件内容，会被分成多个块存储在不同DataNode上";
        int blockSize = 15;
        for (int i = 0, block = 0; i < content.length(); i += blockSize, block++) {
            System.out.printf("  Block %d: [%s]%n", block,
                    content.substring(i, Math.min(i + blockSize, content.length())));
        }
    }
}
