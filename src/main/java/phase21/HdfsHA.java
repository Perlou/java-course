package phase21;

/**
 * HDFS 高可用（HA）配置
 * 
 * HDFS HA 解决了 NameNode 单点故障问题，通过 Active-Standby 架构
 * 和 ZooKeeper 实现自动故障转移。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class HdfsHA {

    public static void main(String[] args) {
        System.out.println("=== HDFS 高可用配置 ===\n");
        haArchitecture();
        haComponents();
        haConfiguration();
        failoverProcess();
        bestPractices();
    }

    private static void haArchitecture() {
        System.out.println("【HA 架构概述】\n");
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│                  HDFS HA 架构                    │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│     ┌──────────┐         ┌──────────┐          │");
        System.out.println("│     │ Active   │         │ Standby  │          │");
        System.out.println("│     │ NameNode │ <-----> │ NameNode │          │");
        System.out.println("│     └────┬─────┘         └────┬─────┘          │");
        System.out.println("│          │                    │                 │");
        System.out.println("│          └────────┬───────────┘                 │");
        System.out.println("│                   ▼                             │");
        System.out.println("│           ┌─────────────┐                       │");
        System.out.println("│           │ JournalNode │ (共享编辑日志)         │");
        System.out.println("│           │   Quorum    │                       │");
        System.out.println("│           └─────────────┘                       │");
        System.out.println("│                   │                             │");
        System.out.println("│           ┌───────┴────────┐                    │");
        System.out.println("│           │   ZooKeeper    │ (故障检测与选举)    │");
        System.out.println("│           └────────────────┘                    │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void haComponents() {
        System.out.println("【HA 核心组件】\n");

        System.out.println("1. Active NameNode");
        System.out.println("   - 处理所有客户端请求");
        System.out.println("   - 将编辑日志写入 JournalNode");
        System.out.println();

        System.out.println("2. Standby NameNode");
        System.out.println("   - 实时同步 Active 的编辑日志");
        System.out.println("   - 维护最新的元数据状态");
        System.out.println("   - 故障时可快速接管");
        System.out.println();

        System.out.println("3. JournalNode（QJM）");
        System.out.println("   - 存储共享的编辑日志");
        System.out.println("   - 通常部署 3-5 个节点");
        System.out.println("   - 基于 Paxos 协议保证一致性");
        System.out.println();

        System.out.println("4. ZooKeeper Failover Controller (ZKFC)");
        System.out.println("   - 监控 NameNode 健康状态");
        System.out.println("   - 参与 Leader 选举");
        System.out.println("   - 触发自动故障转移");
        System.out.println();
    }

    private static void haConfiguration() {
        System.out.println("【HA 配置示例】\n");

        System.out.println("=== hdfs-site.xml ===");
        System.out.println("<property>");
        System.out.println("  <name>dfs.nameservices</name>");
        System.out.println("  <value>mycluster</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>dfs.ha.namenodes.mycluster</name>");
        System.out.println("  <value>nn1,nn2</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>dfs.namenode.rpc-address.mycluster.nn1</name>");
        System.out.println("  <value>namenode1:8020</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>dfs.namenode.shared.edits.dir</name>");
        System.out.println("  <value>qjournal://jn1:8485;jn2:8485;jn3:8485/mycluster</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>dfs.ha.automatic-failover.enabled</name>");
        System.out.println("  <value>true</value>");
        System.out.println("</property>");
        System.out.println();
    }

    private static void failoverProcess() {
        System.out.println("【故障转移流程】\n");
        System.out.println("自动故障转移步骤：");
        System.out.println("  1. ZKFC 检测到 Active NameNode 故障");
        System.out.println("  2. ZKFC 在 ZooKeeper 删除故障节点的锁");
        System.out.println("  3. Standby 的 ZKFC 检测到锁被释放");
        System.out.println("  4. Standby ZKFC 获取锁，触发 Fencing");
        System.out.println("  5. 确保旧 Active 不再服务（防止脑裂）");
        System.out.println("  6. Standby 切换为 Active 状态");
        System.out.println("  7. 开始接收客户端请求");
        System.out.println();

        System.out.println("手动故障转移命令：");
        System.out.println("  hdfs haadmin -transitionToActive nn2");
        System.out.println("  hdfs haadmin -failover nn1 nn2");
        System.out.println();
    }

    private static void bestPractices() {
        System.out.println("【最佳实践】\n");
        System.out.println("1. JournalNode 部署奇数个（推荐 3 或 5）");
        System.out.println("2. 配置合理的 Fencing 方法防止脑裂");
        System.out.println("3. 定期备份 NameNode 元数据");
        System.out.println("4. 监控 ZKFC 和 JournalNode 状态");
        System.out.println("5. 测试故障转移流程");
    }
}
