package phase21;

/**
 * 大数据存储格式
 * 
 * 在大数据领域，选择合适的数据存储格式对于查询性能、存储效率至关重要。
 * 本课件介绍三种主流的大数据格式：Parquet、ORC、Avro。
 * 
 * 核心概念：
 * - 行式存储：数据按行存储，适合写入和全行查询
 * - 列式存储：数据按列存储，适合分析查询（只读取需要的列）
 * - 混合格式：结合行列存储的优势
 * 
 * @author Java Course
 * @since Phase 21
 */
public class DataFormats {

    public static void main(String[] args) {
        System.out.println("=== 大数据存储格式 ===\n");

        // 1. 行式存储 vs 列式存储
        rowVsColumn();

        // 2. Parquet 格式
        parquetFormat();

        // 3. ORC 格式
        orcFormat();

        // 4. Avro 格式
        avroFormat();

        // 5. 格式对比与选型
        formatComparison();

        // 6. 实战示例
        practicalExamples();
    }

    /**
     * 行式存储 vs 列式存储
     */
    private static void rowVsColumn() {
        System.out.println("【行式存储 vs 列式存储】\n");

        System.out.println("示例数据：");
        System.out.println("  +----+--------+-----+--------+");
        System.out.println("  | ID |  Name  | Age | Salary |");
        System.out.println("  +----+--------+-----+--------+");
        System.out.println("  |  1 | Alice  |  28 |  50000 |");
        System.out.println("  |  2 | Bob    |  32 |  60000 |");
        System.out.println("  |  3 | Carol  |  26 |  45000 |");
        System.out.println("  +----+--------+-----+--------+");
        System.out.println();

        System.out.println("=== 行式存储（Row-based） ===");
        System.out.println("  存储方式: [1,Alice,28,50000] [2,Bob,32,60000] [3,Carol,26,45000]");
        System.out.println("  优点:");
        System.out.println("    • 写入效率高（追加写入）");
        System.out.println("    • 适合事务处理（OLTP）");
        System.out.println("    • 获取整行数据效率高");
        System.out.println("  缺点:");
        System.out.println("    • 只查询部分列时效率低（需要读取整行）");
        System.out.println("    • 压缩率较低（同一列数据分散存储）");
        System.out.println("  代表: MySQL、PostgreSQL、JSON、CSV");
        System.out.println();

        System.out.println("=== 列式存储（Column-based） ===");
        System.out.println("  存储方式: [1,2,3] [Alice,Bob,Carol] [28,32,26] [50000,60000,45000]");
        System.out.println("  优点:");
        System.out.println("    • 查询部分列效率极高（只读取需要的列）");
        System.out.println("    • 压缩率高（同类型数据集中存储）");
        System.out.println("    • 适合分析查询（OLAP）");
        System.out.println("  缺点:");
        System.out.println("    • 写入相对复杂（需要拆分到多列）");
        System.out.println("    • 获取整行数据需要合并多列");
        System.out.println("  代表: Parquet、ORC、ClickHouse");
        System.out.println();
    }

    /**
     * Parquet 格式详解
     */
    private static void parquetFormat() {
        System.out.println("【Parquet 格式】\n");

        System.out.println("Apache Parquet 是一种开源的列式存储格式，");
        System.out.println("由 Twitter 和 Cloudera 共同开发。\n");

        System.out.println("核心特性：");
        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │  1. 列式存储: 高效的列投影，跳过不需要的列             │");
        System.out.println("  │  2. 嵌套结构: 支持复杂的嵌套数据结构                   │");
        System.out.println("  │  3. 高压缩比: 同类型数据压缩效率高                     │");
        System.out.println("  │  4. 谓词下推: 查询时跳过不满足条件的数据块             │");
        System.out.println("  │  5. 统计信息: 每个列块存储 min/max 值                  │");
        System.out.println("  └────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("文件结构：");
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.println("  │              Parquet File              │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  Row Group 1                           │");
        System.out.println("  │    ├── Column Chunk (ID)               │");
        System.out.println("  │    │     └── Page 1, Page 2, ...       │");
        System.out.println("  │    ├── Column Chunk (Name)             │");
        System.out.println("  │    └── Column Chunk (Age)              │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  Row Group 2                           │");
        System.out.println("  │    └── ...                             │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  Footer (Schema + Metadata)            │");
        System.out.println("  └────────────────────────────────────────┘");
        System.out.println();

        System.out.println("压缩算法支持：");
        System.out.println("  • Snappy  - 速度快，压缩率中等（默认）");
        System.out.println("  • Gzip    - 压缩率高，速度较慢");
        System.out.println("  • LZO     - 速度快，支持分片");
        System.out.println("  • Zstd    - 压缩率和速度平衡");
        System.out.println();
    }

    /**
     * ORC 格式详解
     */
    private static void orcFormat() {
        System.out.println("【ORC 格式】\n");

        System.out.println("ORC (Optimized Row Columnar) 是 Hive 的优化列式存储格式，");
        System.out.println("由 Hortonworks 开发。\n");

        System.out.println("核心特性：");
        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │  1. 高压缩比: 默认使用 ZLIB，压缩率高于 Parquet        │");
        System.out.println("  │  2. 索引支持: 内置布隆过滤器和行索引                    │");
        System.out.println("  │  3. ACID 支持: 支持事务和更新操作                       │");
        System.out.println("  │  4. 向量化: 支持 Hive 向量化执行                        │");
        System.out.println("  │  5. 类型丰富: 支持更多复杂数据类型                      │");
        System.out.println("  └────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("文件结构：");
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.println("  │               ORC File                 │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  Stripe 1 (默认 250MB)                 │");
        System.out.println("  │    ├── Index Data (索引数据)           │");
        System.out.println("  │    ├── Row Data (列数据)               │");
        System.out.println("  │    └── Stripe Footer (元数据)          │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  Stripe 2                              │");
        System.out.println("  │    └── ...                             │");
        System.out.println("  ├────────────────────────────────────────┤");
        System.out.println("  │  File Footer (Schema + Statistics)     │");
        System.out.println("  │  Postscript (压缩信息)                  │");
        System.out.println("  └────────────────────────────────────────┘");
        System.out.println();

        System.out.println("ORC 三级索引：");
        System.out.println("  1. File Level   - 整个文件的统计信息");
        System.out.println("  2. Stripe Level - 每个 Stripe 的统计信息");
        System.out.println("  3. Row Level    - 每 10000 行的统计信息");
        System.out.println();
    }

    /**
     * Avro 格式详解
     */
    private static void avroFormat() {
        System.out.println("【Avro 格式】\n");

        System.out.println("Apache Avro 是一种行式存储格式，");
        System.out.println("专为序列化和 RPC 设计，是 Hadoop 生态的核心组件。\n");

        System.out.println("核心特性：");
        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │  1. 行式存储: 适合写入和扫描整行数据                   │");
        System.out.println("  │  2. Schema 演进: 支持向前和向后兼容的 Schema 变更      │");
        System.out.println("  │  3. 自描述: 数据文件包含完整 Schema                    │");
        System.out.println("  │  4. 动态类型: 无需代码生成即可读写数据                 │");
        System.out.println("  │  5. 紧凑二进制: 高效的二进制编码格式                   │");
        System.out.println("  └────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("Avro Schema 示例 (JSON 格式)：");
        System.out.println("  {");
        System.out.println("    \"type\": \"record\",");
        System.out.println("    \"name\": \"User\",");
        System.out.println("    \"fields\": [");
        System.out.println("      {\"name\": \"id\", \"type\": \"long\"},");
        System.out.println("      {\"name\": \"name\", \"type\": \"string\"},");
        System.out.println("      {\"name\": \"email\", \"type\": [\"null\", \"string\"]},");
        System.out.println("      {\"name\": \"age\", \"type\": \"int\", \"default\": 0}");
        System.out.println("    ]");
        System.out.println("  }");
        System.out.println();

        System.out.println("Schema 演进规则：");
        System.out.println("  • 向后兼容: 新 Schema 能读取旧数据（添加带默认值的字段）");
        System.out.println("  • 向前兼容: 旧 Schema 能读取新数据（删除带默认值的字段）");
        System.out.println("  • 完全兼容: 同时满足向前和向后兼容");
        System.out.println();
    }

    /**
     * 格式对比与选型
     */
    private static void formatComparison() {
        System.out.println("【格式对比与选型】\n");

        System.out.println("┌─────────────┬──────────┬──────────┬──────────┬──────────────┐");
        System.out.println("│   特性      │ Parquet  │   ORC    │   Avro   │     CSV      │");
        System.out.println("├─────────────┼──────────┼──────────┼──────────┼──────────────┤");
        System.out.println("│  存储方式    │  列式    │   列式   │   行式   │     行式     │");
        System.out.println("│  压缩率      │   高     │   更高   │   中等   │     低       │");
        System.out.println("│  查询性能    │   高     │   高     │   中等   │     低       │");
        System.out.println("│  写入性能    │  中等    │   中等   │   高     │     高       │");
        System.out.println("│  Schema演进  │  支持    │   支持   │   完善   │     不支持   │");
        System.out.println("│  嵌套类型    │  支持    │   支持   │   支持   │     不支持   │");
        System.out.println("│  ACID       │  不支持  │   支持   │  不支持  │     不支持   │");
        System.out.println("│  生态系统    │  广泛    │   Hive   │  Kafka   │     通用     │");
        System.out.println("└─────────────┴──────────┴──────────┴──────────┴──────────────┘");
        System.out.println();

        System.out.println("选型建议：");
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │  场景                    │  推荐格式                    │");
        System.out.println("  ├─────────────────────────────────────────────────────────┤");
        System.out.println("  │  Spark/Presto 分析查询   │  Parquet                     │");
        System.out.println("  │  Hive 数据仓库           │  ORC                         │");
        System.out.println("  │  Kafka 消息传输          │  Avro                        │");
        System.out.println("  │  数据交换/导出           │  CSV / JSON                  │");
        System.out.println("  │  需要事务支持            │  ORC (Hive ACID)             │");
        System.out.println("  │  频繁 Schema 变更        │  Avro                        │");
        System.out.println("  │  通用列式存储            │  Parquet                     │");
        System.out.println("  └─────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * 实战示例
     */
    private static void practicalExamples() {
        System.out.println("【实战示例】\n");

        System.out.println("=== Spark 读写 Parquet ===");
        System.out.println("// 读取 Parquet 文件");
        System.out.println("Dataset<Row> df = spark.read().parquet(\"hdfs:///data/users.parquet\");");
        System.out.println();
        System.out.println("// 写入 Parquet 文件");
        System.out.println("df.write()");
        System.out.println("  .mode(SaveMode.Overwrite)");
        System.out.println("  .option(\"compression\", \"snappy\")");
        System.out.println("  .parquet(\"hdfs:///output/users.parquet\");");
        System.out.println();

        System.out.println("=== Hive 创建 ORC 表 ===");
        System.out.println("CREATE TABLE user_orc (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING,");
        System.out.println("    age INT");
        System.out.println(")");
        System.out.println("STORED AS ORC");
        System.out.println("TBLPROPERTIES ('orc.compress'='ZLIB');");
        System.out.println();

        System.out.println("=== Kafka 使用 Avro ===");
        System.out.println("// 使用 Schema Registry");
        System.out.println("props.put(\"key.serializer\", StringSerializer.class);");
        System.out.println("props.put(\"value.serializer\", KafkaAvroSerializer.class);");
        System.out.println("props.put(\"schema.registry.url\", \"http://localhost:8081\");");
        System.out.println();

        System.out.println("=== 总结 ===");
        System.out.println("  • 分析查询选列式（Parquet/ORC），写入密集选行式（Avro）");
        System.out.println("  • 压缩算法：Snappy（速度） vs Zlib/Gzip（压缩率）");
        System.out.println("  • Schema 演进：Avro 最完善，适合长期数据存储");
    }
}
