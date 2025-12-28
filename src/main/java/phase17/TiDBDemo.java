package phase17;

/**
 * TiDB 分布式数据库演示
 * 
 * TiDB 是 PingCAP 开发的开源分布式 NewSQL 数据库
 * 特点: 水平扩展、强一致性、MySQL 兼容
 */
public class TiDBDemo {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("          Phase 17: TiDB 分布式数据库                        ");
        System.out.println("============================================================");

        System.out.println("\n[1] TiDB 架构概览\n");
        showArchitecture();

        System.out.println("\n[2] 核心组件详解\n");
        explainComponents();

        System.out.println("\n[3] 分布式事务实现\n");
        explainDistributedTransaction();

        System.out.println("\n[4] 使用示例\n");
        showUsageExample();
    }

    private static void showArchitecture() {
        System.out.println("TiDB 整体架构:");
        System.out.println();
        System.out.println("                  +-------------------+");
        System.out.println("                  |      应用层        |");
        System.out.println("                  +--------+----------+");
        System.out.println("                           |");
        System.out.println("                           | MySQL 协议");
        System.out.println("                           v");
        System.out.println("  +--------------------------------------------+");
        System.out.println("  |                 TiDB Server                |");
        System.out.println("  |  (无状态, SQL解析/优化, 可水平扩展)          |");
        System.out.println("  +--------------------------------------------+");
        System.out.println("                           |");
        System.out.println("            +--------------+--------------+");
        System.out.println("            |              |              |");
        System.out.println("            v              v              v");
        System.out.println("      +---------+   +---------+   +---------+");
        System.out.println("      |  TiKV   |   |  TiKV   |   |  TiKV   |");
        System.out.println("      | Region1 |   | Region2 |   | Region3 |");
        System.out.println("      +---------+   +---------+   +---------+");
        System.out.println("            |              |              |");
        System.out.println("            +--------------+--------------+");
        System.out.println("                           |");
        System.out.println("                           v");
        System.out.println("               +---------------------+");
        System.out.println("               |         PD          |");
        System.out.println("               | (Placement Driver)  |");
        System.out.println("               +---------------------+");
    }

    private static void explainComponents() {
        System.out.println("1. TiDB Server (SQL 层)");
        System.out.println("   - 解析 SQL，生成执行计划");
        System.out.println("   - 完全无状态，可随意扩缩容");
        System.out.println("   - 兼容 MySQL 协议");
        System.out.println();
        System.out.println("2. TiKV (存储层)");
        System.out.println("   - 分布式 KV 存储引擎");
        System.out.println("   - 基于 RocksDB (LSM-Tree)");
        System.out.println("   - Raft 协议保证数据一致性");
        System.out.println("   - Region: 数据分片单位 (默认 96MB)");
        System.out.println();
        System.out.println("3. PD (调度层)");
        System.out.println("   - 存储集群元数据");
        System.out.println("   - 分配全局唯一 ID 和事务时间戳");
        System.out.println("   - 调度 Region，实现负载均衡");
        System.out.println();
        System.out.println("4. TiFlash (可选，列存引擎)");
        System.out.println("   - 列式存储，加速 OLAP 查询");
        System.out.println("   - 与 TiKV 保持强一致");
        System.out.println("   - HTAP: 同时支持 OLTP 和 OLAP");
    }

    private static void explainDistributedTransaction() {
        System.out.println("TiDB 分布式事务 (基于 Percolator 模型):");
        System.out.println();
        System.out.println("两阶段提交 (2PC) + MVCC:");
        System.out.println();
        System.out.println("1. 事务开始");
        System.out.println("   - 从 PD 获取开始时间戳 (start_ts)");
        System.out.println();
        System.out.println("2. 预写阶段 (Prewrite)");
        System.out.println("   - 写入数据到各 Region (加锁)");
        System.out.println("   - 选择一个 Primary Key");
        System.out.println("   - 其他 Key 指向 Primary");
        System.out.println();
        System.out.println("3. 提交阶段 (Commit)");
        System.out.println("   - 从 PD 获取提交时间戳 (commit_ts)");
        System.out.println("   - 先提交 Primary Key");
        System.out.println("   - 异步提交其他 Key");
        System.out.println();
        System.out.println("隔离级别:");
        System.out.println("   - 默认: SI (Snapshot Isolation)");
        System.out.println("   - 可选: RC (Read Committed)");
        System.out.println();
        System.out.println("MVCC 实现:");
        System.out.println("   - 每个版本带时间戳");
        System.out.println("   - 读操作看到 start_ts 之前的数据");
        System.out.println("   - 不阻塞读写");
    }

    private static void showUsageExample() {
        System.out.println("Spring Boot + TiDB 配置:");
        System.out.println();
        System.out.println("```yaml");
        System.out.println("spring:");
        System.out.println("  datasource:");
        System.out.println("    url: jdbc:mysql://tidb-host:4000/mydb");
        System.out.println("    username: root");
        System.out.println("    password: ''");
        System.out.println("    driver-class-name: com.mysql.cj.jdbc.Driver");
        System.out.println("```");
        System.out.println();
        System.out.println("TiDB 特有 SQL:");
        System.out.println();
        System.out.println("-- 查看 Region 分布");
        System.out.println("SHOW TABLE mydb.mytable REGIONS;");
        System.out.println();
        System.out.println("-- 手动分裂 Region");
        System.out.println("SPLIT TABLE mytable BETWEEN (0) AND (1000000) REGIONS 10;");
        System.out.println();
        System.out.println("-- 查看热点 Region");
        System.out.println("SELECT * FROM INFORMATION_SCHEMA.TIDB_HOT_REGIONS;");
        System.out.println();
        System.out.println("适用场景:");
        System.out.println("  - MySQL 单机扛不住，需要水平扩展");
        System.out.println("  - 需要分布式事务的场景");
        System.out.println("  - HTAP 混合负载");
    }
}
