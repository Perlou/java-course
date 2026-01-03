package phase22;

/**
 * Spark 基础与架构
 * 
 * Apache Spark 是一个快速、通用的大数据处理引擎，
 * 提供了比 MapReduce 快 10-100 倍的内存计算能力。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkBasics {

    public static void main(String[] args) {
        System.out.println("=== Spark 基础与架构 ===\n");

        sparkOverview();
        sparkArchitecture();
        sparkVsMapReduce();
        rddConcept();
        sparkDeployModes();
        sparkSubmit();
    }

    private static void sparkOverview() {
        System.out.println("【Spark 概述】\n");

        System.out.println("Apache Spark 是什么？");
        System.out.println("  • 快速通用的大数据处理引擎");
        System.out.println("  • 支持批处理、流处理、机器学习、图计算");
        System.out.println("  • 基于内存计算，比 MapReduce 快 10-100 倍");
        System.out.println("  • 使用 Scala 开发，支持 Java、Python、R");
        System.out.println();

        System.out.println("Spark 生态系统：");
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.println("  │                  Spark SQL                      │");
        System.out.println("  │  (结构化数据处理, DataFrame/Dataset API)         │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │   Spark        │   MLlib      │   GraphX       │");
        System.out.println("  │   Streaming    │   (机器学习)  │   (图计算)     │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │                  Spark Core (RDD)               │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │    Standalone  │   YARN   │   Mesos   │   K8s  │");
        System.out.println("  └─────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void sparkArchitecture() {
        System.out.println("【Spark 架构】\n");

        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│                    Spark Application                    │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│                                                         │");
        System.out.println("│  ┌─────────────────┐                                    │");
        System.out.println("│  │     Driver      │  (驱动程序)                        │");
        System.out.println("│  │  ┌───────────┐  │                                    │");
        System.out.println("│  │  │SparkContext│ │  - 创建 SparkContext               │");
        System.out.println("│  │  └───────────┘  │  - 划分 Job/Stage/Task             │");
        System.out.println("│  │  ┌───────────┐  │  - 调度任务到 Executor             │");
        System.out.println("│  │  │  DAG      │  │                                    │");
        System.out.println("│  │  │ Scheduler │  │                                    │");
        System.out.println("│  │  └───────────┘  │                                    │");
        System.out.println("│  └────────┬────────┘                                    │");
        System.out.println("│           │                                             │");
        System.out.println("│    ┌──────┴──────┬───────────────┐                      │");
        System.out.println("│    ▼             ▼               ▼                      │");
        System.out.println("│  ┌──────┐     ┌──────┐       ┌──────┐                   │");
        System.out.println("│  │Executor│   │Executor│     │Executor│  (执行器)       │");
        System.out.println("│  │┌────┐ │   │┌────┐ │     │┌────┐ │                   │");
        System.out.println("│  ││Task│ │   ││Task│ │     ││Task│ │                   │");
        System.out.println("│  │└────┘ │   │└────┘ │     │└────┘ │                   │");
        System.out.println("│  │┌────┐ │   │┌────┐ │     │┌────┐ │                   │");
        System.out.println("│  ││Cache│ │  ││Cache│ │    ││Cache│ │                   │");
        System.out.println("│  │└────┘ │   │└────┘ │     │└────┘ │                   │");
        System.out.println("│  └──────┘     └──────┘       └──────┘                   │");
        System.out.println("│                                                         │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("核心组件：");
        System.out.println("  • Driver: 运行 main() 方法，创建 SparkContext");
        System.out.println("  • Executor: 在 Worker 节点运行，执行具体任务");
        System.out.println("  • Cluster Manager: 资源管理（Standalone/YARN/Mesos/K8s）");
        System.out.println("  • Task: 最小执行单元，一个 Partition 对应一个 Task");
        System.out.println();
    }

    private static void sparkVsMapReduce() {
        System.out.println("【Spark vs MapReduce】\n");

        System.out.println("┌─────────────┬─────────────────────┬─────────────────────┐");
        System.out.println("│   对比项     │     MapReduce       │       Spark         │");
        System.out.println("├─────────────┼─────────────────────┼─────────────────────┤");
        System.out.println("│  计算模型    │  Map + Reduce       │  DAG（有向无环图）   │");
        System.out.println("│  数据存储    │  磁盘               │  内存 + 磁盘        │");
        System.out.println("│  迭代计算    │  多次读写磁盘       │  内存缓存复用       │");
        System.out.println("│  编程模型    │  只有 MR 两个阶段   │  丰富的算子         │");
        System.out.println("│  延迟        │  分钟级             │  秒级               │");
        System.out.println("│  容错        │  数据复制           │  血统(Lineage)      │");
        System.out.println("│  适用场景    │  批处理             │  批处理+流处理+ML   │");
        System.out.println("└─────────────┴─────────────────────┴─────────────────────┘");
        System.out.println();

        System.out.println("Spark 快的原因：");
        System.out.println("  1. 内存计算：中间结果缓存在内存中");
        System.out.println("  2. DAG 优化：减少不必要的 Shuffle");
        System.out.println("  3. 延迟执行：优化执行计划");
        System.out.println("  4. 代码生成：Catalyst 生成优化的字节码");
        System.out.println();
    }

    private static void rddConcept() {
        System.out.println("【RDD 核心概念】\n");

        System.out.println("RDD (Resilient Distributed Dataset) 弹性分布式数据集：");
        System.out.println("  • Resilient：容错性，通过 Lineage（血统）恢复数据");
        System.out.println("  • Distributed：数据分布在多个节点");
        System.out.println("  • Dataset：数据集合，可以是任何类型");
        System.out.println();

        System.out.println("RDD 五大特性：");
        System.out.println("  1. 分区列表 (Partitions)：数据被划分为多个分区");
        System.out.println("  2. 计算函数：每个分区有一个计算函数");
        System.out.println("  3. 依赖关系：记录父 RDD 的依赖");
        System.out.println("  4. 分区器 (Partitioner)：可选，用于 K-V 数据");
        System.out.println("  5. 首选位置：可选，数据本地性优化");
        System.out.println();

        System.out.println("RDD 依赖类型：");
        System.out.println("  • 窄依赖 (Narrow)：父 RDD 的每个分区最多被一个子分区使用");
        System.out.println("    例：map, filter, union");
        System.out.println("  • 宽依赖 (Wide)：父 RDD 的分区被多个子分区使用（需要 Shuffle）");
        System.out.println("    例：groupByKey, reduceByKey, join");
        System.out.println();
    }

    private static void sparkDeployModes() {
        System.out.println("【Spark 部署模式】\n");

        System.out.println("1. Local 模式");
        System.out.println("   • 本地单机运行，用于开发测试");
        System.out.println("   • spark-submit --master local[*]");
        System.out.println();

        System.out.println("2. Standalone 模式");
        System.out.println("   • Spark 自带的集群管理器");
        System.out.println("   • spark-submit --master spark://host:7077");
        System.out.println();

        System.out.println("3. YARN 模式（推荐生产环境）");
        System.out.println("   • 使用 Hadoop YARN 管理资源");
        System.out.println("   • spark-submit --master yarn");
        System.out.println("   • Client 模式：Driver 在本地");
        System.out.println("   • Cluster 模式：Driver 在集群中");
        System.out.println();

        System.out.println("4. Kubernetes 模式");
        System.out.println("   • 使用 K8s 管理容器化 Spark");
        System.out.println("   • spark-submit --master k8s://https://...");
        System.out.println();
    }

    private static void sparkSubmit() {
        System.out.println("【spark-submit 命令】\n");

        System.out.println("基本语法：");
        System.out.println("  spark-submit \\");
        System.out.println("    --class <main-class> \\");
        System.out.println("    --master <master-url> \\");
        System.out.println("    --deploy-mode <deploy-mode> \\");
        System.out.println("    --conf <key>=<value> \\");
        System.out.println("    <application-jar> \\");
        System.out.println("    [application-arguments]");
        System.out.println();

        System.out.println("常用参数：");
        System.out.println("  --driver-memory 4g      # Driver 内存");
        System.out.println("  --executor-memory 8g    # Executor 内存");
        System.out.println("  --executor-cores 4      # Executor CPU 核数");
        System.out.println("  --num-executors 10      # Executor 数量");
        System.out.println("  --queue default         # YARN 队列");
        System.out.println();

        System.out.println("示例：");
        System.out.println("  spark-submit \\");
        System.out.println("    --class com.example.WordCount \\");
        System.out.println("    --master yarn \\");
        System.out.println("    --deploy-mode cluster \\");
        System.out.println("    --executor-memory 4g \\");
        System.out.println("    --num-executors 10 \\");
        System.out.println("    myapp.jar input output");
    }
}
