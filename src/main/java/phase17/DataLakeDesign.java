package phase17;

/**
 * 数据湖架构设计 (Data Lake Design)
 * 
 * 数据湖: 存储原始数据，Schema-on-Read
 * 数据仓库: 存储处理后数据，Schema-on-Write
 * 湖仓一体: 结合两者优势
 */
public class DataLakeDesign {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("           Phase 17: 数据湖架构设计 (Data Lake)              ");
        System.out.println("============================================================");

        System.out.println("\n[1] 数据湖 vs 数据仓库\n");
        compareDataLakeAndWarehouse();

        System.out.println("\n[2] 数据湖分层架构\n");
        showDataLakeLayers();

        System.out.println("\n[3] 湖仓一体 (Lakehouse)\n");
        explainLakehouse();

        System.out.println("\n[4] 技术选型\n");
        showTechStack();
    }

    private static void compareDataLakeAndWarehouse() {
        System.out.println("数据湖 vs 数据仓库:");
        System.out.println();
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("|    维度       |      数据湖         |     数据仓库       |");
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("| 数据类型      | 结构化/半结构化/非结 | 结构化数据         |");
        System.out.println("|              | 构化,任意格式        |                   |");
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("| Schema       | Schema-on-Read     | Schema-on-Write    |");
        System.out.println("|              | 读取时定义结构      | 写入时定义结构      |");
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("| 数据处理      | ELT               | ETL                |");
        System.out.println("|              | 先加载后转换        | 先转换后加载        |");
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("| 适用场景      | 探索性分析、ML     | BI报表、固定分析    |");
        System.out.println("+--------------+--------------------+--------------------+");
        System.out.println("| 存储成本      | 低 (对象存储)      | 高 (专用存储)       |");
        System.out.println("+--------------+--------------------+--------------------+");
    }

    private static void showDataLakeLayers() {
        System.out.println("数据湖分层架构 (Medallion Architecture):");
        System.out.println();
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |                   数据应用层                      |");
        System.out.println("  |    BI报表  |  数据产品  |  机器学习  |  API服务   |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("                            |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |              Gold Layer (金层)                   |");
        System.out.println("  |  - 业务聚合数据                                   |");
        System.out.println("  |  - 预计算的指标                                   |");
        System.out.println("  |  - 直接支撑业务                                   |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("                            |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |             Silver Layer (银层)                  |");
        System.out.println("  |  - 清洗后的数据                                   |");
        System.out.println("  |  - 标准化的字段                                   |");
        System.out.println("  |  - 去重、合并后的数据                              |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("                            |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |             Bronze Layer (铜层)                  |");
        System.out.println("  |  - 原始数据                                       |");
        System.out.println("  |  - 保留完整性                                     |");
        System.out.println("  |  - 用于数据追溯                                   |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("                            |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |                   数据源层                        |");
        System.out.println("  |   业务数据库  |  日志  |  IoT设备  |  第三方API   |");
        System.out.println("  +--------------------------------------------------+");
    }

    private static void explainLakehouse() {
        System.out.println("湖仓一体 (Lakehouse) 架构:");
        System.out.println();
        System.out.println("核心理念: 在数据湖上实现数据仓库的能力");
        System.out.println();
        System.out.println("  传统架构:");
        System.out.println("  数据源 --> 数据湖 --> ETL --> 数据仓库 --> BI");
        System.out.println("            (原始存储)          (分析引擎)");
        System.out.println();
        System.out.println("  湖仓一体:");
        System.out.println("  数据源 --> Lakehouse --> BI + ML + 实时分析");
        System.out.println("            (统一存储+计算)");
        System.out.println();
        System.out.println("关键技术:");
        System.out.println("  1. Delta Lake (Databricks)");
        System.out.println("     - ACID 事务支持");
        System.out.println("     - Schema 演进");
        System.out.println("     - 时间旅行 (Time Travel)");
        System.out.println();
        System.out.println("  2. Apache Iceberg");
        System.out.println("     - 开放表格式");
        System.out.println("     - 隐藏分区");
        System.out.println("     - 元数据管理");
        System.out.println();
        System.out.println("  3. Apache Hudi");
        System.out.println("     - 增量处理");
        System.out.println("     - Upsert 支持");
        System.out.println("     - 流批一体");
    }

    private static void showTechStack() {
        System.out.println("数据湖技术选型:");
        System.out.println();
        System.out.println("+------------+------------------------------------------+");
        System.out.println("|    层次     |              技术选项                    |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 存储层      | HDFS, S3, Azure Blob, MinIO            |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 表格式      | Delta Lake, Iceberg, Hudi               |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 计算引擎    | Spark, Flink, Presto, Trino            |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 元数据管理  | Hive Metastore, AWS Glue               |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 数据治理    | Apache Atlas, DataHub                   |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println("| 编排调度    | Airflow, Dagster, Prefect               |");
        System.out.println("+------------+------------------------------------------+");
        System.out.println();
        System.out.println("选型建议:");
        System.out.println("  - 云上优先考虑托管服务 (Databricks, Snowflake)");
        System.out.println("  - 自建考虑 Spark + Iceberg + MinIO");
        System.out.println("  - 流批一体选 Flink + Hudi");
    }
}
