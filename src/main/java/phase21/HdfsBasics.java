package phase21;

/**
 * HDFS 分布式文件系统基础
 * 
 * HDFS（Hadoop Distributed File System）是 Hadoop 的核心组件之一，
 * 专为存储超大文件设计的分布式文件系统。
 * 
 * 核心架构：
 * - NameNode（主节点）：管理文件系统元数据（文件目录树、块映射）
 * - DataNode（从节点）：存储实际数据块
 * - Secondary NameNode：辅助 NameNode 合并 EditLog
 * 
 * 设计特点：
 * - 适合大文件存储（GB~PB级别）
 * - 流式数据访问（一次写入，多次读取）
 * - 运行在廉价硬件上
 * - 不适合低延迟访问和小文件存储
 * 
 * 注意：运行此代码需要配置 Hadoop 环境或连接到 Hadoop 集群
 * 
 * @author Java Course
 * @since Phase 21
 */
public class HdfsBasics {

    // HDFS 默认配置
    private static final String HDFS_URI = "hdfs://localhost:9000";
    private static final String HDFS_USER = "hadoop";

    public static void main(String[] args) {
        System.out.println("=== HDFS 分布式文件系统基础 ===\n");

        // 1. HDFS 架构详解
        explainArchitecture();

        // 2. HDFS 读写流程
        explainReadWriteFlow();

        // 3. 数据块与副本
        explainBlockReplication();

        // 4. HDFS Shell 命令
        hdfsShellCommands();

        // 5. Java API 操作示例（伪代码）
        javaApiDemo();
    }

    /**
     * HDFS 架构详解
     * 
     * Master-Slave 架构：
     * ┌─────────────────────────────────────────────────────────┐
     * │ NameNode │
     * │ ┌─────────────────────────────────────────────────┐ │
     * │ │ 元数据管理： │ │
     * │ │ - 文件系统命名空间（目录树） │ │
     * │ │ - 文件 -> 块的映射关系 │ │
     * │ │ - 块 -> DataNode 的映射关系 │ │
     * │ └─────────────────────────────────────────────────┘ │
     * └─────────────────────────────────────────────────────────┘
     * │
     * ┌───────────────────┼───────────────────┐
     * ▼ ▼ ▼
     * ┌───────────┐ ┌───────────┐ ┌───────────┐
     * │ DataNode1 │ │ DataNode2 │ │ DataNode3 │
     * │ ┌───────┐ │ │ ┌───────┐ │ │ ┌───────┐ │
     * │ │Block 1│ │ │ │Block 1│ │ │ │Block 2│ │
     * │ │Block 2│ │ │ │Block 3│ │ │ │Block 3│ │
     * │ └───────┘ │ │ └───────┘ │ │ └───────┘ │
     * └───────────┘ └───────────┘ └───────────┘
     */
    private static void explainArchitecture() {
        System.out.println("【HDFS 架构】\n");

        System.out.println("核心组件：");
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│  NameNode (主节点 - Master)                            │");
        System.out.println("│  - 管理文件系统的命名空间（目录树结构）                 │");
        System.out.println("│  - 维护文件到数据块的映射关系                          │");
        System.out.println("│  - 记录每个数据块所在的 DataNode 位置                  │");
        System.out.println("│  - 内存中存储元数据，持久化到 fsimage + editlog        │");
        System.out.println("├────────────────────────────────────────────────────────┤");
        System.out.println("│  DataNode (从节点 - Slave)                             │");
        System.out.println("│  - 存储实际的数据块（Block）                           │");
        System.out.println("│  - 定期向 NameNode 发送心跳和块报告                    │");
        System.out.println("│  - 执行数据块的创建、删除和复制操作                    │");
        System.out.println("├────────────────────────────────────────────────────────┤");
        System.out.println("│  Secondary NameNode                                    │");
        System.out.println("│  - 定期合并 fsimage 和 editlog                         │");
        System.out.println("│  - 减轻 NameNode 的压力                                │");
        System.out.println("│  - 注意：它不是 NameNode 的热备份！                    │");
        System.out.println("└────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * HDFS 读写流程
     */
    private static void explainReadWriteFlow() {
        System.out.println("【HDFS 读写流程】\n");

        System.out.println("=== 写入流程 ===");
        System.out.println("  1. Client 向 NameNode 请求上传文件");
        System.out.println("  2. NameNode 检查权限，返回可用的 DataNode 列表");
        System.out.println("  3. Client 将文件切分为 Block（默认 128MB）");
        System.out.println("  4. Client 与第一个 DataNode 建立连接，写入第一个 Block");
        System.out.println("  5. DataNode 形成 Pipeline，将 Block 复制到其他节点");
        System.out.println("  6. 所有副本写入完成后，返回 ACK 确认");
        System.out.println();

        System.out.println("=== 读取流程 ===");
        System.out.println("  1. Client 向 NameNode 请求读取文件");
        System.out.println("  2. NameNode 返回文件所有 Block 的位置信息");
        System.out.println("  3. Client 选择最近的 DataNode 读取 Block");
        System.out.println("  4. 依次读取所有 Block，合并为完整文件");
        System.out.println("  5. 如果 DataNode 故障，自动切换到其他副本");
        System.out.println();
    }

    /**
     * 数据块与副本机制
     */
    private static void explainBlockReplication() {
        System.out.println("【数据块与副本】\n");

        System.out.println("Block（数据块）：");
        System.out.println("  - 默认大小：128 MB（Hadoop 2.x+）");
        System.out.println("  - 大文件切分为多个 Block 存储");
        System.out.println("  - 每个 Block 独立存储和复制");
        System.out.println();

        System.out.println("Replication（副本机制）：");
        System.out.println("  - 默认副本数：3");
        System.out.println("  - 副本放置策略：");
        System.out.println("    * 第 1 个副本：写入客户端所在节点（或随机节点）");
        System.out.println("    * 第 2 个副本：不同机架的某个节点");
        System.out.println("    * 第 3 个副本：与第 2 个副本同机架的不同节点");
        System.out.println();

        System.out.println("为什么 Block 大小是 128 MB？");
        System.out.println("  - 减少 NameNode 内存压力（元数据量减少）");
        System.out.println("  - 减少寻道时间占比（大块顺序读写更高效）");
        System.out.println("  - 平衡 Map 任务的启动时间与处理时间");
        System.out.println();
    }

    /**
     * HDFS Shell 命令
     */
    private static void hdfsShellCommands() {
        System.out.println("【HDFS Shell 常用命令】\n");

        String[][] commands = {
                // 文件操作
                { "hdfs dfs -ls /", "列出根目录下的文件" },
                { "hdfs dfs -mkdir /user/data", "创建目录" },
                { "hdfs dfs -put local.txt /user/data/", "上传本地文件到 HDFS" },
                { "hdfs dfs -get /user/data/file.txt ./", "从 HDFS 下载文件" },
                { "hdfs dfs -cat /user/data/file.txt", "查看文件内容" },
                { "hdfs dfs -rm /user/data/file.txt", "删除文件" },
                { "hdfs dfs -rm -r /user/data/", "递归删除目录" },
                { "hdfs dfs -cp /src /dst", "复制文件" },
                { "hdfs dfs -mv /src /dst", "移动/重命名文件" },
                { "hdfs dfs -du -s -h /user/data/", "查看目录大小" },
                // 管理命令
                { "hdfs dfsadmin -report", "查看集群状态报告" },
                { "hdfs dfsadmin -safemode get", "查看安全模式状态" },
                { "hdfs fsck /user/data/ -files -blocks", "检查文件系统健康状态" }
        };

        System.out.println("文件操作命令：");
        for (int i = 0; i < 10; i++) {
            System.out.printf("  %-45s # %s%n", commands[i][0], commands[i][1]);
        }
        System.out.println();

        System.out.println("管理命令：");
        for (int i = 10; i < commands.length; i++) {
            System.out.printf("  %-45s # %s%n", commands[i][0], commands[i][1]);
        }
        System.out.println();
    }

    /**
     * Java API 操作示例
     * 
     * 注意：这是演示代码，需要配置 Hadoop 环境才能运行
     */
    private static void javaApiDemo() {
        System.out.println("【Java API 操作 HDFS】\n");

        System.out.println("// 1. 获取 FileSystem 实例");
        System.out.println("Configuration conf = new Configuration();");
        System.out.println("conf.set(\"fs.defaultFS\", \"hdfs://namenode:9000\");");
        System.out.println("FileSystem fs = FileSystem.get(conf);");
        System.out.println();

        System.out.println("// 2. 创建目录");
        System.out.println("Path path = new Path(\"/user/data\");");
        System.out.println("fs.mkdirs(path);");
        System.out.println();

        System.out.println("// 3. 上传文件");
        System.out.println("Path src = new Path(\"/local/file.txt\");");
        System.out.println("Path dst = new Path(\"/user/data/file.txt\");");
        System.out.println("fs.copyFromLocalFile(src, dst);");
        System.out.println();

        System.out.println("// 4. 读取文件");
        System.out.println("FSDataInputStream in = fs.open(new Path(\"/user/data/file.txt\"));");
        System.out.println("BufferedReader reader = new BufferedReader(new InputStreamReader(in));");
        System.out.println("String line;");
        System.out.println("while ((line = reader.readLine()) != null) {");
        System.out.println("    System.out.println(line);");
        System.out.println("}");
        System.out.println("reader.close();");
        System.out.println();

        System.out.println("// 5. 写入文件");
        System.out.println("FSDataOutputStream out = fs.create(new Path(\"/user/data/output.txt\"));");
        System.out.println("out.writeBytes(\"Hello HDFS!\");");
        System.out.println("out.close();");
        System.out.println();

        System.out.println("// 6. 遍历目录");
        System.out.println("FileStatus[] files = fs.listStatus(new Path(\"/user/data\"));");
        System.out.println("for (FileStatus file : files) {");
        System.out.println("    System.out.println(file.getPath().getName());");
        System.out.println("}");
        System.out.println();

        System.out.println("// 7. 关闭 FileSystem");
        System.out.println("fs.close();");
    }

    /**
     * 实际可运行的本地模拟示例
     * 模拟 HDFS 的基本文件操作概念
     */
    public static void localSimulation() {
        System.out.println("\n【本地模拟 HDFS 概念】\n");

        // 模拟文件分块
        String content = "这是一个模拟大文件的内容，实际在HDFS中会被切分成128MB的块存储在不同的DataNode上。";
        int blockSize = 20; // 模拟块大小为20字符

        System.out.println("原始内容: " + content);
        System.out.println("块大小: " + blockSize + " 字符");
        System.out.println("分块结果:");

        int blockNum = 0;
        for (int i = 0; i < content.length(); i += blockSize) {
            int end = Math.min(i + blockSize, content.length());
            String block = content.substring(i, end);
            System.out.printf("  Block %d: [%s]%n", blockNum++, block);
        }

        System.out.println("\n模拟副本分布:");
        System.out.println("  Block 0: DataNode1(机架1), DataNode3(机架2), DataNode4(机架2)");
        System.out.println("  Block 1: DataNode2(机架1), DataNode4(机架2), DataNode5(机架2)");
        System.out.println("  Block 2: DataNode1(机架1), DataNode5(机架2), DataNode6(机架2)");
    }
}
