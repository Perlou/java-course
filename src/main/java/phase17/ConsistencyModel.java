package phase17;

/**
 * 一致性模型对比 (Consistency Models)
 * 
 * 分布式系统中的一致性级别从强到弱:
 * 线性一致性 > 顺序一致性 > 因果一致性 > 最终一致性
 */
public class ConsistencyModel {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("         Phase 17: 一致性模型对比 (Consistency Models)       ");
        System.out.println("============================================================");

        System.out.println("\n[1] 一致性级别概览\n");
        showConsistencyLevels();

        System.out.println("\n[2] 强一致性详解\n");
        explainStrongConsistency();

        System.out.println("\n[3] 最终一致性详解\n");
        explainEventualConsistency();

        System.out.println("\n[4] 业务场景选择\n");
        showBusinessScenarios();
    }

    private static void showConsistencyLevels() {
        System.out.println("一致性级别 (从强到弱):");
        System.out.println();
        System.out.println("+------------------+----------------------------------------+");
        System.out.println("|     级别          |              描述                      |");
        System.out.println("+------------------+----------------------------------------+");
        System.out.println("| 线性一致性        | 写完成后所有读都能看到最新值             |");
        System.out.println("| (Linearizable)   | 最强，像单机一样                        |");
        System.out.println("+------------------+----------------------------------------+");
        System.out.println("| 顺序一致性        | 所有操作按某个全局顺序执行               |");
        System.out.println("| (Sequential)     | 不要求实时性                            |");
        System.out.println("+------------------+----------------------------------------+");
        System.out.println("| 因果一致性        | 有因果关系的操作保持顺序                 |");
        System.out.println("| (Causal)         | A 导致 B，则 B 一定在 A 之后            |");
        System.out.println("+------------------+----------------------------------------+");
        System.out.println("| 最终一致性        | 最终所有节点数据一致                     |");
        System.out.println("| (Eventual)       | 允许短暂不一致                          |");
        System.out.println("+------------------+----------------------------------------+");
    }

    private static void explainStrongConsistency() {
        System.out.println("线性一致性 (Linearizability):");
        System.out.println();
        System.out.println("时间线:");
        System.out.println("  Client A: |--write(x=1)--|");
        System.out.println("  Client B:                    |--read(x)--|");
        System.out.println("                                    |");
        System.out.println("                                    v");
        System.out.println("                              必须返回 1");
        System.out.println();
        System.out.println("实现方式:");
        System.out.println("  1. 单 Leader 同步复制 (延迟高)");
        System.out.println("  2. Raft/Paxos 共识 (多数确认)");
        System.out.println("  3. 2PC 分布式事务");
        System.out.println();
        System.out.println("代价:");
        System.out.println("  - 延迟增加 (需等待多数节点响应)");
        System.out.println("  - 可用性降低 (节点故障影响写入)");
        System.out.println("  - 性能限制 (吞吐量受限于最慢节点)");
    }

    private static void explainEventualConsistency() {
        System.out.println("最终一致性 (Eventual Consistency):");
        System.out.println();
        System.out.println("时间线:");
        System.out.println("  t=0: write(x=1) 写入节点A");
        System.out.println("  t=1: read(x) 从节点B -> 可能返回旧值或空");
        System.out.println("  t=2: 节点A异步复制到节点B");
        System.out.println("  t=3: read(x) 从节点B -> 返回 1 (最终一致)");
        System.out.println();
        System.out.println("变体:");
        System.out.println("  1. Read Your Writes (读己之写)");
        System.out.println("     - 用户能读到自己刚写入的数据");
        System.out.println("     - 实现: Session 粘性或版本标记");
        System.out.println();
        System.out.println("  2. Monotonic Reads (单调读)");
        System.out.println("     - 不会读到比之前更旧的数据");
        System.out.println("     - 实现: 读固定副本或版本检查");
        System.out.println();
        System.out.println("  3. Causal Consistency (因果一致)");
        System.out.println("     - 因果相关的写入按顺序可见");
        System.out.println("     - 实现: 向量时钟 (Vector Clock)");
    }

    private static void showBusinessScenarios() {
        System.out.println("业务场景与一致性选择:");
        System.out.println();
        System.out.println("+------------------+----------------+------------------------+");
        System.out.println("|     业务场景      |   一致性级别    |         原因          |");
        System.out.println("+------------------+----------------+------------------------+");
        System.out.println("| 银行转账          | 强一致性        | 不能出现钱凭空消失/增加 |");
        System.out.println("| 库存扣减          | 强一致性        | 避免超卖              |");
        System.out.println("+------------------+----------------+------------------------+");
        System.out.println("| 订单状态          | 因果一致        | 用户能看到自己操作结果 |");
        System.out.println("| 聊天消息          | 因果一致        | 保证消息顺序          |");
        System.out.println("+------------------+----------------+------------------------+");
        System.out.println("| 商品浏览          | 最终一致        | 短暂不一致可接受      |");
        System.out.println("| 用户动态          | 最终一致        | 不影响核心业务        |");
        System.out.println("| 日志收集          | 最终一致        | 高吞吐优先            |");
        System.out.println("+------------------+----------------+------------------------+");
        System.out.println();
        System.out.println("设计原则:");
        System.out.println("  1. 核心链路用强一致，非核心用最终一致");
        System.out.println("  2. 金钱相关必须强一致");
        System.out.println("  3. 可以接受延迟的用最终一致提升性能");
    }
}
