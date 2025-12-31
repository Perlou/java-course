package phase21;

/**
 * YARN 资源调度策略
 * 
 * YARN 支持多种资源调度器，不同调度器适用于不同场景。
 * 主要包括：FIFO、Capacity Scheduler、Fair Scheduler。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class ResourceScheduler {

    public static void main(String[] args) {
        System.out.println("=== YARN 资源调度策略 ===\n");

        schedulerOverview();
        fifoScheduler();
        capacityScheduler();
        fairScheduler();
        comparison();
        configuration();
    }

    private static void schedulerOverview() {
        System.out.println("【调度器概述】\n");

        System.out.println("YARN 调度器负责将集群资源分配给各个应用程序。");
        System.out.println();
        System.out.println("三种调度器：");
        System.out.println("  ┌─────────────────┬────────────────────────────────┐");
        System.out.println("  │  FIFO Scheduler │  先进先出，适合单用户/测试环境   │");
        System.out.println("  │  Capacity       │  容量调度，适合多租户共享集群    │");
        System.out.println("  │  Fair           │  公平调度，资源公平分配          │");
        System.out.println("  └─────────────────┴────────────────────────────────┘");
        System.out.println();
    }

    private static void fifoScheduler() {
        System.out.println("【FIFO Scheduler】\n");

        System.out.println("特点：");
        System.out.println("  • 先提交的作业先获得资源");
        System.out.println("  • 简单易懂，无需配置");
        System.out.println("  • 不支持多队列");
        System.out.println();

        System.out.println("工作示意：");
        System.out.println("  Job1 [==========]           (100% 资源)");
        System.out.println("  Job2 [等待中...]");
        System.out.println("  Job3 [等待中...]");
        System.out.println();

        System.out.println("缺点：");
        System.out.println("  • 大作业会阻塞后续小作业");
        System.out.println("  • 不适合生产环境多用户场景");
        System.out.println();
    }

    private static void capacityScheduler() {
        System.out.println("【Capacity Scheduler 容量调度器】\n");

        System.out.println("Apache Hadoop 默认调度器，专为多租户设计。");
        System.out.println();

        System.out.println("核心特性：");
        System.out.println("  • 队列层次结构（树形）");
        System.out.println("  • 每个队列分配固定容量百分比");
        System.out.println("  • 队列内部使用 FIFO");
        System.out.println("  • 支持资源弹性：空闲资源可被其他队列使用");
        System.out.println();

        System.out.println("队列结构示例：");
        System.out.println("  root (100%)");
        System.out.println("   ├── prod (60%)     生产队列");
        System.out.println("   │    ├── online (40%)");
        System.out.println("   │    └── batch (20%)");
        System.out.println("   └── dev (40%)      开发队列");
        System.out.println("        ├── team-a (20%)");
        System.out.println("        └── team-b (20%)");
        System.out.println();

        System.out.println("配置示例 (capacity-scheduler.xml)：");
        System.out.println("  yarn.scheduler.capacity.root.queues=prod,dev");
        System.out.println("  yarn.scheduler.capacity.root.prod.capacity=60");
        System.out.println("  yarn.scheduler.capacity.root.dev.capacity=40");
        System.out.println("  yarn.scheduler.capacity.root.prod.maximum-capacity=80");
        System.out.println();
    }

    private static void fairScheduler() {
        System.out.println("【Fair Scheduler 公平调度器】\n");

        System.out.println("所有作业公平共享集群资源。");
        System.out.println();

        System.out.println("核心特性：");
        System.out.println("  • 动态资源分配，按需分配");
        System.out.println("  • 支持抢占：资源不足时回收多余资源");
        System.out.println("  • 多种调度策略（fair、fifo、drf）");
        System.out.println("  • 延迟调度优化数据本地性");
        System.out.println();

        System.out.println("工作示意：");
        System.out.println("  时刻1: Job1 [========] (100%)");
        System.out.println("  时刻2: Job1 [====] Job2 [====] (各 50%)");
        System.out.println("  时刻3: Job1 [==] Job2 [==] Job3 [==] (各 33%)");
        System.out.println();

        System.out.println("DRF (Dominant Resource Fairness)：");
        System.out.println("  考虑多维资源（CPU + 内存），按主导资源公平分配");
        System.out.println();

        System.out.println("配置示例 (fair-scheduler.xml)：");
        System.out.println("  <allocations>");
        System.out.println("    <queue name=\"root\">");
        System.out.println("      <queue name=\"prod\">");
        System.out.println("        <weight>3</weight>");
        System.out.println("        <minResources>4096mb,4vcores</minResources>");
        System.out.println("      </queue>");
        System.out.println("      <queue name=\"dev\">");
        System.out.println("        <weight>1</weight>");
        System.out.println("      </queue>");
        System.out.println("    </queue>");
        System.out.println("    <defaultQueueSchedulingPolicy>fair</defaultQueueSchedulingPolicy>");
        System.out.println("  </allocations>");
        System.out.println();
    }

    private static void comparison() {
        System.out.println("【调度器对比】\n");

        System.out.println("┌─────────────┬─────────────┬───────────────┬───────────────┐");
        System.out.println("│   特性       │    FIFO     │   Capacity    │     Fair      │");
        System.out.println("├─────────────┼─────────────┼───────────────┼───────────────┤");
        System.out.println("│ 多队列       │    不支持   │     支持      │     支持      │");
        System.out.println("│ 资源共享     │    不支持   │   队列间弹性   │   完全共享    │");
        System.out.println("│ 抢占         │    不支持   │     支持      │     支持      │");
        System.out.println("│ 适用场景     │   单用户    │   多租户      │   交互式      │");
        System.out.println("│ 复杂度       │    低       │     中        │     高        │");
        System.out.println("└─────────────┴─────────────┴───────────────┴───────────────┘");
        System.out.println();

        System.out.println("选型建议：");
        System.out.println("  • 测试/开发环境: FIFO");
        System.out.println("  • 多租户生产环境: Capacity Scheduler");
        System.out.println("  • 需要快速响应: Fair Scheduler");
        System.out.println();
    }

    private static void configuration() {
        System.out.println("【配置调度器】\n");

        System.out.println("yarn-site.xml 中配置：");
        System.out.println();
        System.out.println("<!-- 使用 Capacity Scheduler -->");
        System.out.println("<property>");
        System.out.println("  <name>yarn.resourcemanager.scheduler.class</name>");
        System.out.println("  <value>org.apache.hadoop.yarn.server.resourcemanager.");
        System.out.println("         scheduler.capacity.CapacityScheduler</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<!-- 使用 Fair Scheduler -->");
        System.out.println("<property>");
        System.out.println("  <name>yarn.resourcemanager.scheduler.class</name>");
        System.out.println("  <value>org.apache.hadoop.yarn.server.resourcemanager.");
        System.out.println("         scheduler.fair.FairScheduler</value>");
        System.out.println("</property>");
        System.out.println();

        System.out.println("提交作业到指定队列：");
        System.out.println("  hadoop jar app.jar -Dmapreduce.job.queuename=prod");
        System.out.println("  spark-submit --queue prod app.jar");
    }
}
