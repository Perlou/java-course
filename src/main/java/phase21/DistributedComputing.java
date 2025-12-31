package phase21;

/**
 * 分布式计算基础原理
 * 
 * 分布式计算是指将大型计算任务分解成多个小任务，
 * 分配到多台计算机上并行执行，最后汇总结果的计算模式。
 * 
 * 核心思想：
 * - 分而治之：将大问题拆分成小问题
 * - 并行处理：多个节点同时处理不同部分
 * - 容错机制：节点故障时自动重试或恢复
 * 
 * @author Java Course
 * @since Phase 21
 */
public class DistributedComputing {

    public static void main(String[] args) {
        System.out.println("=== 分布式计算基础原理 ===\n");

        // 1. 分布式计算概述
        overview();

        // 2. 分布式计算的挑战
        challenges();

        // 3. 分布式计算模型
        computingModels();

        // 4. CAP 定理
        capTheorem();

        // 5. 一致性模型
        consistencyModels();

        // 6. 分布式计算框架对比
        frameworkComparison();
    }

    /**
     * 分布式计算概述
     */
    private static void overview() {
        System.out.println("【分布式计算概述】\n");

        System.out.println("什么是分布式计算？");
        System.out.println("  分布式计算是一种计算方法，它将一个需要大量计算能力才能解决的问题");
        System.out.println("  分解成许多小部分，然后把这些部分分配给多台计算机进行处理。");
        System.out.println();

        System.out.println("为什么需要分布式计算？");
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. 数据量爆炸: 单机无法存储和处理 PB 级数据                │");
        System.out.println("│  2. 计算复杂度: 复杂算法需要大量并行计算资源                │");
        System.out.println("│  3. 高可用性: 单点故障会导致服务不可用                      │");
        System.out.println("│  4. 可扩展性: 业务增长需要灵活扩展计算能力                  │");
        System.out.println("│  5. 成本效益: 多台普通机器比单台超级计算机更经济            │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("分布式计算 vs 并行计算：");
        System.out.println("  ┌─────────────────┬─────────────────────────────────────┐");
        System.out.println("  │    并行计算      │  同一台机器内多核/多处理器并行       │");
        System.out.println("  ├─────────────────┼─────────────────────────────────────┤");
        System.out.println("  │    分布式计算    │  多台机器通过网络协作计算            │");
        System.out.println("  └─────────────────┴─────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * 分布式计算的挑战
     */
    private static void challenges() {
        System.out.println("【分布式计算的挑战】\n");

        String[][] challenges = {
                { "网络问题", "网络延迟", "节点间通信存在毫秒级延迟" },
                { "网络问题", "网络分区", "网络故障导致节点间无法通信" },
                { "网络问题", "带宽限制", "大量数据传输受网络带宽限制" },
                { "", "", "" },
                { "故障处理", "节点故障", "任何节点都可能随时宕机" },
                { "故障处理", "部分失败", "某些操作成功，某些失败" },
                { "故障处理", "拜占庭故障", "节点可能返回错误或恶意结果" },
                { "", "", "" },
                { "数据一致性", "时钟同步", "不同节点的时钟存在偏差" },
                { "数据一致性", "数据复制", "多副本数据需要保持一致" },
                { "数据一致性", "并发控制", "多节点同时修改相同数据" },
        };

        String currentCategory = "";
        for (String[] challenge : challenges) {
            if (challenge[0].isEmpty()) {
                System.out.println();
                continue;
            }
            if (!challenge[0].equals(currentCategory)) {
                currentCategory = challenge[0];
                System.out.printf("  [%s]%n", currentCategory);
            }
            System.out.printf("    • %-10s - %s%n", challenge[1], challenge[2]);
        }
        System.out.println();
    }

    /**
     * 分布式计算模型
     */
    private static void computingModels() {
        System.out.println("【分布式计算模型】\n");

        System.out.println("1. MapReduce 模型");
        System.out.println("   ├── 适用场景: 批量数据处理、ETL、日志分析");
        System.out.println("   ├── 核心思想: Map（分解）+ Reduce（汇总）");
        System.out.println("   └── 代表框架: Hadoop MapReduce");
        System.out.println();

        System.out.println("2. BSP 模型（Bulk Synchronous Parallel）");
        System.out.println("   ├── 适用场景: 图计算、迭代算法");
        System.out.println("   ├── 核心思想: 超步(Superstep) = 计算 + 通信 + 同步");
        System.out.println("   └── 代表框架: Pregel、Giraph、Spark GraphX");
        System.out.println();

        System.out.println("3. 数据流模型（Dataflow）");
        System.out.println("   ├── 适用场景: 流式处理、实时计算");
        System.out.println("   ├── 核心思想: 数据在算子之间流动");
        System.out.println("   └── 代表框架: Flink、Storm、Spark Streaming");
        System.out.println();

        System.out.println("4. Actor 模型");
        System.out.println("   ├── 适用场景: 并发处理、分布式系统");
        System.out.println("   ├── 核心思想: 基于消息传递的并发模型");
        System.out.println("   └── 代表框架: Akka、Erlang/OTP");
        System.out.println();

        System.out.println("5. 参数服务器模型（Parameter Server）");
        System.out.println("   ├── 适用场景: 分布式机器学习");
        System.out.println("   ├── 核心思想: Worker 计算梯度，Server 维护参数");
        System.out.println("   └── 代表框架: PS-Lite、TensorFlow");
        System.out.println();
    }

    /**
     * CAP 定理
     */
    private static void capTheorem() {
        System.out.println("【CAP 定理】\n");

        System.out.println("CAP 定理指出，分布式系统不可能同时满足以下三个特性：");
        System.out.println();

        System.out.println("  ┌─────────────────────────────────────────────────────┐");
        System.out.println("  │                        C                            │");
        System.out.println("  │                  Consistency                        │");
        System.out.println("  │                    (一致性)                          │");
        System.out.println("  │                     /   \\                           │");
        System.out.println("  │                    /     \\                          │");
        System.out.println("  │                   /       \\                         │");
        System.out.println("  │                  /   CAP   \\                        │");
        System.out.println("  │                 /           \\                       │");
        System.out.println("  │                /             \\                      │");
        System.out.println("  │    Availability ─────────── Partition               │");
        System.out.println("  │      (可用性)               Tolerance                │");
        System.out.println("  │         A                  (分区容错)                │");
        System.out.println("  │                               P                     │");
        System.out.println("  └─────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("三个特性详解：");
        System.out.println("  • C (Consistency)   - 所有节点同一时刻看到相同的数据");
        System.out.println("  • A (Availability)  - 每个请求都能收到非错误响应（不保证最新数据）");
        System.out.println("  • P (Partition)     - 即使网络分区，系统仍能继续运行");
        System.out.println();

        System.out.println("CAP 选择与典型系统：");
        System.out.println("  ┌───────────┬────────────────────────────────────────┐");
        System.out.println("  │  CP 系统  │  ZooKeeper、etcd、Consul               │");
        System.out.println("  │           │  放弃可用性，保证强一致性               │");
        System.out.println("  ├───────────┼────────────────────────────────────────┤");
        System.out.println("  │  AP 系统  │  Cassandra、DynamoDB、CouchDB          │");
        System.out.println("  │           │  放弃强一致性，保证可用性               │");
        System.out.println("  ├───────────┼────────────────────────────────────────┤");
        System.out.println("  │  CA 系统  │  传统单机数据库（RDBMS）               │");
        System.out.println("  │           │  不考虑分区，实际分布式中不存在         │");
        System.out.println("  └───────────┴────────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * 一致性模型
     */
    private static void consistencyModels() {
        System.out.println("【一致性模型】\n");

        System.out.println("从强到弱的一致性模型：\n");

        String[][] models = {
                { "强一致性", "Linearizability", "所有操作都能看到最新写入的值" },
                { "顺序一致性", "Sequential", "所有进程看到相同的操作顺序" },
                { "因果一致性", "Causal", "有因果关系的操作保持顺序" },
                { "最终一致性", "Eventual", "最终所有副本会达到一致状态" },
                { "读己写", "Read Your Writes", "能读到自己写入的最新值" },
        };

        for (String[] model : models) {
            System.out.printf("  %-10s (%-20s)%n", model[0], model[1]);
            System.out.printf("    └── %s%n%n", model[2]);
        }

        System.out.println("BASE 理论（与 ACID 对应）：");
        System.out.println("  • BA (Basically Available) - 基本可用");
        System.out.println("  • S  (Soft State)          - 软状态，允许中间状态");
        System.out.println("  • E  (Eventually Consistent) - 最终一致性");
        System.out.println();
    }

    /**
     * 分布式计算框架对比
     */
    private static void frameworkComparison() {
        System.out.println("【分布式计算框架对比】\n");

        System.out.println("┌─────────────┬──────────────┬──────────────┬──────────────┐");
        System.out.println("│   框架       │    类型      │    延迟      │    适用场景   │");
        System.out.println("├─────────────┼──────────────┼──────────────┼──────────────┤");
        System.out.println("│  MapReduce  │    批处理    │   分钟~小时   │  离线ETL     │");
        System.out.println("│  Spark      │   批+微批    │   秒~分钟    │  交互式分析  │");
        System.out.println("│  Flink      │   流批一体   │   毫秒~秒    │  实时计算    │");
        System.out.println("│  Storm      │    纯流      │   毫秒       │  实时告警    │");
        System.out.println("│  Presto     │   交互查询   │   秒级       │  即席查询    │");
        System.out.println("└─────────────┴──────────────┴──────────────┴──────────────┘");
        System.out.println();

        System.out.println("框架选型建议：");
        System.out.println("  • 离线大规模ETL      → Spark / MapReduce");
        System.out.println("  • 实时流处理         → Flink");
        System.out.println("  • 交互式数据分析     → Spark SQL / Presto");
        System.out.println("  • 图计算             → Spark GraphX / Neo4j");
        System.out.println("  • 机器学习           → Spark MLlib / TensorFlow");
        System.out.println();

        System.out.println("=== 总结 ===");
        System.out.println("分布式计算的核心价值：");
        System.out.println("  1. 水平扩展：通过增加节点提升处理能力");
        System.out.println("  2. 高容错：任何节点故障不影响整体服务");
        System.out.println("  3. 数据本地化：计算向数据移动，减少网络传输");
    }
}
