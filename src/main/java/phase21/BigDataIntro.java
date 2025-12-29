package phase21;

/**
 * 大数据概念入门
 * 
 * 大数据（Big Data）是指无法在一定时间范围内用常规软件工具进行捕捉、管理和处理的数据集合，
 * 需要新处理模式才能具有更强的决策力、洞察发现力和流程优化能力的海量、高增长率和多样化的信息资产。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class BigDataIntro {

    public static void main(String[] args) {
        System.out.println("=== 大数据概念入门 ===\n");

        // 1. 大数据 5V 特性
        explainFiveV();

        // 2. 大数据技术栈概览
        techStackOverview();

        // 3. 批处理 vs 流处理
        batchVsStream();

        // 4. 大数据应用场景
        applicationScenarios();
    }

    /**
     * 大数据 5V 特性
     * 
     * Volume（大量）：数据量巨大，从 TB 到 PB 甚至 EB 级别
     * Velocity（高速）：数据产生和处理速度快，要求实时或近实时处理
     * Variety（多样）：数据类型多样，包括结构化、半结构化、非结构化数据
     * Value（价值）：数据价值密度低，需要从海量数据中挖掘有价值信息
     * Veracity（真实）：数据质量参差不齐，需要确保数据的准确性和可信度
     */
    private static void explainFiveV() {
        System.out.println("【大数据 5V 特性】\n");

        String[][] fiveV = {
                { "Volume（大量）", "数据量巨大", "从 TB 到 PB 级别的数据存储和处理" },
                { "Velocity（高速）", "数据产生速度快", "实时流处理、毫秒级响应" },
                { "Variety（多样）", "数据类型多样", "结构化(SQL)、半结构化(JSON)、非结构化(图片视频)" },
                { "Value（价值）", "价值密度低", "从海量数据中挖掘有价值的洞察" },
                { "Veracity（真实）", "数据质量不一", "数据清洗、验证、去重" }
        };

        for (String[] v : fiveV) {
            System.out.printf("  %-20s | %-15s | %s%n", v[0], v[1], v[2]);
        }
        System.out.println();
    }

    /**
     * 大数据技术栈概览
     * 
     * 数据采集层：Flume、Kafka、Sqoop
     * 数据存储层：HDFS、HBase、Hive
     * 数据计算层：MapReduce、Spark、Flink
     * 数据分析层：Presto、Kylin、ClickHouse
     */
    private static void techStackOverview() {
        System.out.println("【大数据技术栈】\n");

        String[][] stack = {
                { "数据采集", "Flume", "日志采集", "实时数据流采集" },
                { "数据采集", "Kafka", "消息队列", "高吞吐分布式消息系统" },
                { "数据采集", "Sqoop", "数据迁移", "RDBMS 与 Hadoop 数据互导" },
                { "数据存储", "HDFS", "分布式文件系统", "PB 级数据存储" },
                { "数据存储", "HBase", "列式数据库", "海量数据实时读写" },
                { "数据计算", "MapReduce", "批处理", "离线大规模数据处理" },
                { "数据计算", "Spark", "内存计算", "比 MapReduce 快 10-100 倍" },
                { "数据计算", "Flink", "流批一体", "真正的实时流处理" },
                { "数据仓库", "Hive", "SQL on Hadoop", "数据仓库查询" },
                { "OLAP", "ClickHouse", "列式存储", "亚秒级 OLAP 查询" }
        };

        String currentLayer = "";
        for (String[] tech : stack) {
            if (!tech[0].equals(currentLayer)) {
                currentLayer = tech[0];
                System.out.printf("  [%s]%n", currentLayer);
            }
            System.out.printf("    %-12s: %-15s - %s%n", tech[1], tech[2], tech[3]);
        }
        System.out.println();
    }

    /**
     * 批处理 vs 流处理
     * 
     * 批处理（Batch Processing）：
     * - 处理有界数据集（Bounded Data）
     * - 高吞吐量，高延迟
     * - 典型场景：日终报表、历史数据分析
     * - 代表技术：MapReduce、Spark Batch
     * 
     * 流处理（Stream Processing）：
     * - 处理无界数据流（Unbounded Data）
     * - 低延迟，实时响应
     * - 典型场景：实时监控、风控系统
     * - 代表技术：Flink、Kafka Streams
     */
    private static void batchVsStream() {
        System.out.println("【批处理 vs 流处理】\n");

        System.out.println("  ┌───────────────────────────────────────────────┐");
        System.out.println("  │              批处理（Batch）                   │");
        System.out.println("  ├───────────────────────────────────────────────┤");
        System.out.println("  │  数据特征: 有界数据集（文件、数据库快照）      │");
        System.out.println("  │  处理延迟: 分钟级 ~ 小时级                     │");
        System.out.println("  │  典型场景: 日终报表、ETL、历史分析             │");
        System.out.println("  │  代表技术: MapReduce、Spark Batch              │");
        System.out.println("  └───────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  ┌───────────────────────────────────────────────┐");
        System.out.println("  │              流处理（Stream）                  │");
        System.out.println("  ├───────────────────────────────────────────────┤");
        System.out.println("  │  数据特征: 无界数据流（持续产生的事件）        │");
        System.out.println("  │  处理延迟: 毫秒级 ~ 秒级                       │");
        System.out.println("  │  典型场景: 实时监控、风控、推荐系统            │");
        System.out.println("  │  代表技术: Flink、Kafka Streams                │");
        System.out.println("  └───────────────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * 大数据应用场景
     */
    private static void applicationScenarios() {
        System.out.println("【大数据典型应用场景】\n");

        String[][] scenarios = {
                { "电商", "用户画像、商品推荐、库存预测、价格优化" },
                { "金融", "风险控制、反欺诈、信用评估、量化交易" },
                { "物流", "路径优化、需求预测、智能调度、仓储管理" },
                { "医疗", "疾病预测、影像分析、药物研发、精准医疗" },
                { "社交", "内容推荐、社交网络分析、舆情监控、广告投放" },
                { "IoT", "设备监控、预测性维护、能耗优化、智能家居" }
        };

        for (String[] scenario : scenarios) {
            System.out.printf("  [%s] %s%n", scenario[0], scenario[1]);
        }
        System.out.println();

        System.out.println("=== 总结 ===");
        System.out.println("大数据技术的核心价值：");
        System.out.println("  1. 存储：解决海量数据的存储问题（HDFS、数据湖）");
        System.out.println("  2. 计算：解决海量数据的计算问题（MapReduce、Spark、Flink）");
        System.out.println("  3. 分析：从数据中挖掘商业价值（数据仓库、机器学习）");
    }
}
