package phase24;

/**
 * Hive 基础
 * 
 * Hive 是 Hadoop 生态中的数据仓库工具，
 * 提供 SQL 接口查询 HDFS 数据。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class HiveBasics {

    /**
     * ========================================
     * 第一部分：Hive 架构
     * ========================================
     */
    public static void explainArchitecture() {
        System.out.println("=== Hive 架构 ===");
        System.out.println();

        System.out.println("【Hive 是什么】");
        System.out.println("  • 基于 Hadoop 的数据仓库工具");
        System.out.println("  • 将 SQL 转换为 MapReduce/Tez/Spark 作业");
        System.out.println("  • 数据存储在 HDFS");
        System.out.println("  • 适合批处理和 OLAP 分析");
        System.out.println();

        System.out.println("【架构组件】");
        System.out.println("```");
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│                      用户接口                            │");
        System.out.println("│              CLI / JDBC / Beeline                       │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                    HiveServer2                           │");
        System.out.println("│              提供客户端连接服务                          │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                    Driver                                │");
        System.out.println("│   ┌──────────┬──────────┬──────────┬──────────┐         │");
        System.out.println("│   │ Parser   │ Planner  │ Optimizer│ Executor │         │");
        System.out.println("│   │ SQL解析  │ 逻辑计划 │ 优化器   │ 执行器   │         │");
        System.out.println("│   └──────────┴──────────┴──────────┴──────────┘         │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                   MetaStore                              │");
        System.out.println("│        元数据存储（MySQL/PostgreSQL）                    │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│              执行引擎 (Execution Engine)                 │");
        System.out.println("│           MR / Tez / Spark / LLAP                       │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                   HDFS / OSS                             │");
        System.out.println("│                  数据存储层                              │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：数据类型与表
     * ========================================
     */
    public static void explainDataTypes() {
        System.out.println("=== Hive 数据类型 ===");
        System.out.println();

        System.out.println("【原生类型】");
        System.out.println("┌──────────────┬───────────────────────────────────┐");
        System.out.println("│ 类型         │ 说明                              │");
        System.out.println("├──────────────┼───────────────────────────────────┤");
        System.out.println("│ TINYINT      │ 1字节整数                         │");
        System.out.println("│ SMALLINT     │ 2字节整数                         │");
        System.out.println("│ INT          │ 4字节整数                         │");
        System.out.println("│ BIGINT       │ 8字节整数                         │");
        System.out.println("│ FLOAT        │ 单精度浮点                        │");
        System.out.println("│ DOUBLE       │ 双精度浮点                        │");
        System.out.println("│ DECIMAL      │ 任意精度数值                      │");
        System.out.println("│ STRING       │ 字符串                            │");
        System.out.println("│ VARCHAR      │ 变长字符串                        │");
        System.out.println("│ BOOLEAN      │ 布尔值                            │");
        System.out.println("│ TIMESTAMP    │ 时间戳                            │");
        System.out.println("│ DATE         │ 日期                              │");
        System.out.println("└──────────────┴───────────────────────────────────┘");
        System.out.println();

        System.out.println("【复杂类型】");
        System.out.println("  • ARRAY<type>       数组");
        System.out.println("  • MAP<key, value>   键值对");
        System.out.println("  • STRUCT<col:type>  结构体");
        System.out.println();

        System.out.println("【建表语句】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE IF NOT EXISTS orders (");
        System.out.println("    order_id      BIGINT,");
        System.out.println("    user_id       BIGINT,");
        System.out.println("    product_info  ARRAY<STRING>,");
        System.out.println("    ext_props     MAP<STRING, STRING>,");
        System.out.println("    address       STRUCT<city:STRING, street:STRING>,");
        System.out.println("    create_time   TIMESTAMP");
        System.out.println(")");
        System.out.println("PARTITIONED BY (dt STRING)");
        System.out.println("STORED AS ORC;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：内部表与外部表
     * ========================================
     */
    public static void explainTableTypes() {
        System.out.println("=== 内部表与外部表 ===");
        System.out.println();

        System.out.println("【内部表 (Managed Table)】");
        System.out.println("  • Hive 管理数据和元数据");
        System.out.println("  • DROP TABLE 会删除数据");
        System.out.println("  • 数据存储在 Hive warehouse 目录");
        System.out.println();
        System.out.println("```sql");
        System.out.println("CREATE TABLE internal_table (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING");
        System.out.println(") STORED AS ORC;");
        System.out.println("```");
        System.out.println();

        System.out.println("【外部表 (External Table)】");
        System.out.println("  • Hive 只管理元数据");
        System.out.println("  • DROP TABLE 不删除数据");
        System.out.println("  • 数据可以存储在任意位置");
        System.out.println("  • 推荐用于 ODS 层");
        System.out.println();
        System.out.println("```sql");
        System.out.println("CREATE EXTERNAL TABLE external_table (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING");
        System.out.println(")");
        System.out.println("STORED AS PARQUET");
        System.out.println("LOCATION '/data/external_table';");
        System.out.println("```");
        System.out.println();

        System.out.println("【选择建议】");
        System.out.println("  • ODS 层：外部表（保护原始数据）");
        System.out.println("  • DWD/DWS/ADS：内部表（Hive 管理）");
    }

    /**
     * ========================================
     * 第四部分：分区与分桶
     * ========================================
     */
    public static void explainPartitionBucket() {
        System.out.println("=== 分区与分桶 ===");
        System.out.println();

        System.out.println("【分区 (Partition)】");
        System.out.println("  按字段将数据存储到不同目录");
        System.out.println("  减少查询扫描的数据量");
        System.out.println();
        System.out.println("```sql");
        System.out.println("-- 创建分区表");
        System.out.println("CREATE TABLE orders (");
        System.out.println("    order_id BIGINT,");
        System.out.println("    amount   DECIMAL(10,2)");
        System.out.println(")");
        System.out.println("PARTITIONED BY (dt STRING, hour STRING)");
        System.out.println("STORED AS ORC;");
        System.out.println();
        System.out.println("-- 添加分区");
        System.out.println("ALTER TABLE orders ADD PARTITION (dt='2024-01-01', hour='10');");
        System.out.println();
        System.out.println("-- 查询特定分区");
        System.out.println("SELECT * FROM orders WHERE dt = '2024-01-01';");
        System.out.println();
        System.out.println("-- 目录结构");
        System.out.println("-- /warehouse/orders/dt=2024-01-01/hour=10/");
        System.out.println("-- /warehouse/orders/dt=2024-01-01/hour=11/");
        System.out.println("```");
        System.out.println();

        System.out.println("【分桶 (Bucket)】");
        System.out.println("  按字段 Hash 分成固定数量的文件");
        System.out.println("  优化 Join 和采样查询");
        System.out.println();
        System.out.println("```sql");
        System.out.println("-- 创建分桶表");
        System.out.println("CREATE TABLE orders_bucket (");
        System.out.println("    order_id BIGINT,");
        System.out.println("    user_id  BIGINT,");
        System.out.println("    amount   DECIMAL(10,2)");
        System.out.println(")");
        System.out.println("CLUSTERED BY (user_id) INTO 32 BUCKETS");
        System.out.println("STORED AS ORC;");
        System.out.println();
        System.out.println("-- 分桶 Join 优化（Bucket Map Join）");
        System.out.println("SET hive.optimize.bucketmapjoin = true;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：存储格式
     * ========================================
     */
    public static void explainFileFormats() {
        System.out.println("=== 存储格式 ===");
        System.out.println();

        System.out.println("【常见格式对比】");
        System.out.println("┌──────────┬───────────┬───────────┬───────────┬───────────┐");
        System.out.println("│ 格式     │ 行/列存储 │ 压缩      │ 可拆分    │ 适用场景  │");
        System.out.println("├──────────┼───────────┼───────────┼───────────┼───────────┤");
        System.out.println("│ TextFile │ 行        │ 不支持    │ 是        │ 简单场景  │");
        System.out.println("│ SequenceF│ 行        │ 支持      │ 是        │ MR 中间   │");
        System.out.println("│ ORC      │ 列        │ 高压缩    │ 是        │ Hive 推荐 │");
        System.out.println("│ Parquet  │ 列        │ 高压缩    │ 是        │ 跨引擎    │");
        System.out.println("│ Avro     │ 行        │ 支持      │ 是        │ Schema 演 │");
        System.out.println("└──────────┴───────────┴───────────┴───────────┴───────────┘");
        System.out.println();

        System.out.println("【ORC 格式特点】");
        System.out.println("  • Hive 原生优化格式");
        System.out.println("  • 支持 ACID 事务");
        System.out.println("  • 内置索引（Min/Max/Bloom）");
        System.out.println("  • 支持列裁剪和谓词下推");
        System.out.println();

        System.out.println("【建议】");
        System.out.println("  • 数仓推荐使用 ORC 或 Parquet");
        System.out.println("  • 需要跨引擎（Spark/Presto）时用 Parquet");
        System.out.println("  • 纯 Hive 环境用 ORC");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: Hive 基础                             ║");
        System.out.println("║          Hadoop 数据仓库工具                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainArchitecture();
        System.out.println();

        explainDataTypes();
        System.out.println();

        explainTableTypes();
        System.out.println();

        explainPartitionBucket();
        System.out.println();

        explainFileFormats();
    }
}
