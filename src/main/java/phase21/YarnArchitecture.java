package phase21;

/**
 * YARN 架构详解
 * 
 * YARN (Yet Another Resource Negotiator) 是 Hadoop 2.0 引入的资源管理平台，
 * 负责集群资源管理和作业调度。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class YarnArchitecture {

    public static void main(String[] args) {
        System.out.println("=== YARN 架构详解 ===\n");

        architecture();
        components();
        workflowDemo();
        applicationTypes();
        configuration();
    }

    private static void architecture() {
        System.out.println("【YARN 架构图】\n");

        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│                       Client                            │");
        System.out.println("│                         │                               │");
        System.out.println("│                         ▼                               │");
        System.out.println("│  ┌─────────────────────────────────────────────────┐   │");
        System.out.println("│  │              ResourceManager (RM)               │   │");
        System.out.println("│  │  ┌───────────────┐  ┌──────────────────────┐   │   │");
        System.out.println("│  │  │   Scheduler   │  │ ApplicationManager   │   │   │");
        System.out.println("│  │  └───────────────┘  └──────────────────────┘   │   │");
        System.out.println("│  └─────────────────────────────────────────────────┘   │");
        System.out.println("│                         │                               │");
        System.out.println("│         ┌───────────────┼───────────────┐               │");
        System.out.println("│         ▼               ▼               ▼               │");
        System.out.println("│  ┌───────────┐  ┌───────────┐  ┌───────────┐           │");
        System.out.println("│  │NodeManager│  │NodeManager│  │NodeManager│           │");
        System.out.println("│  │┌────┐┌───┐│  │┌────┐┌───┐│  │┌────┐┌───┐│           │");
        System.out.println("│  ││ AM ││ C ││  ││ C  ││ C ││  ││ C  ││ C ││           │");
        System.out.println("│  │└────┘└───┘│  │└────┘└───┘│  │└────┘└───┘│           │");
        System.out.println("│  └───────────┘  └───────────┘  └───────────┘           │");
        System.out.println("│                                                         │");
        System.out.println("│  AM = ApplicationMaster    C = Container               │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void components() {
        System.out.println("【核心组件】\n");

        System.out.println("1. ResourceManager (RM) - 全局资源管理器");
        System.out.println("   ├── Scheduler：资源调度（不关心应用状态）");
        System.out.println("   │   ├── 分配 Container 给应用");
        System.out.println("   │   └── 支持多种调度策略");
        System.out.println("   └── ApplicationManager：应用管理");
        System.out.println("       ├── 接收作业提交请求");
        System.out.println("       └── 管理 ApplicationMaster 生命周期");
        System.out.println();

        System.out.println("2. NodeManager (NM) - 节点管理器");
        System.out.println("   ├── 管理本节点的资源（CPU、内存）");
        System.out.println("   ├── 定期向 RM 汇报资源使用情况");
        System.out.println("   ├── 启动和监控 Container");
        System.out.println("   └── 处理来自 RM/AM 的请求");
        System.out.println();

        System.out.println("3. ApplicationMaster (AM) - 应用管理器");
        System.out.println("   ├── 每个应用一个 AM");
        System.out.println("   ├── 向 RM 申请资源");
        System.out.println("   ├── 与 NM 协调执行任务");
        System.out.println("   └── 监控任务执行状态");
        System.out.println();

        System.out.println("4. Container - 资源容器");
        System.out.println("   ├── 资源抽象（CPU + 内存）");
        System.out.println("   ├── 任务运行的沙箱环境");
        System.out.println("   └── 由 NM 创建和管理");
        System.out.println();
    }

    private static void workflowDemo() {
        System.out.println("【作业执行流程】\n");

        System.out.println("步骤 1: 客户端提交应用");
        System.out.println("  Client --> ResourceManager");
        System.out.println();

        System.out.println("步骤 2: RM 分配 Container 启动 AM");
        System.out.println("  RM --> NodeManager: 启动 ApplicationMaster");
        System.out.println();

        System.out.println("步骤 3: AM 向 RM 注册并申请资源");
        System.out.println("  AM --> RM: 需要 N 个 Container");
        System.out.println();

        System.out.println("步骤 4: RM 分配 Container 给 AM");
        System.out.println("  RM --> AM: 分配 Container 列表");
        System.out.println();

        System.out.println("步骤 5: AM 联系 NM 启动 Container");
        System.out.println("  AM --> NodeManager: 启动任务");
        System.out.println();

        System.out.println("步骤 6: 任务运行并汇报进度");
        System.out.println("  Container --> AM: 进度/状态");
        System.out.println();

        System.out.println("步骤 7: 应用完成，AM 注销");
        System.out.println("  AM --> RM: 应用完成");
        System.out.println();
    }

    private static void applicationTypes() {
        System.out.println("【支持的应用类型】\n");

        System.out.println("YARN 是通用资源管理平台，支持多种计算框架：");
        System.out.println();
        System.out.println("  ┌─────────────┬────────────────────────────────┐");
        System.out.println("  │   框架       │         说明                   │");
        System.out.println("  ├─────────────┼────────────────────────────────┤");
        System.out.println("  │  MapReduce  │  Hadoop 原生批处理框架         │");
        System.out.println("  │  Spark      │  内存计算框架                  │");
        System.out.println("  │  Flink      │  流批一体框架                  │");
        System.out.println("  │  Tez        │  DAG 计算框架                  │");
        System.out.println("  │  Storm      │  实时流处理框架                │");
        System.out.println("  └─────────────┴────────────────────────────────┘");
        System.out.println();
    }

    private static void configuration() {
        System.out.println("【核心配置】\n");

        System.out.println("=== yarn-site.xml ===");
        System.out.println("<property>");
        System.out.println("  <name>yarn.resourcemanager.hostname</name>");
        System.out.println("  <value>rm-host</value>");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>yarn.nodemanager.resource.memory-mb</name>");
        System.out.println("  <value>8192</value>  <!-- 节点可用内存 -->");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>yarn.nodemanager.resource.cpu-vcores</name>");
        System.out.println("  <value>4</value>  <!-- 节点可用 CPU 核数 -->");
        System.out.println("</property>");
        System.out.println();
        System.out.println("<property>");
        System.out.println("  <name>yarn.scheduler.minimum-allocation-mb</name>");
        System.out.println("  <value>1024</value>  <!-- Container 最小内存 -->");
        System.out.println("</property>");
        System.out.println();

        System.out.println("常用命令：");
        System.out.println("  yarn application -list           # 列出应用");
        System.out.println("  yarn application -kill <app-id>  # 杀死应用");
        System.out.println("  yarn node -list                  # 列出节点");
        System.out.println("  yarn logs -applicationId <id>    # 查看日志");
    }
}
