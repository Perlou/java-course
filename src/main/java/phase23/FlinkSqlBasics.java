package phase23;

/**
 * Flink SQL 基础
 * 
 * Flink SQL 是 Flink 的高级 API，提供声明式的流批统一查询能力。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkSqlBasics {

    /**
     * ========================================
     * 第一部分：Flink SQL 概述
     * ========================================
     */
    public static void explainFlinkSql() {
        System.out.println("=== Flink SQL 概述 ===");
        System.out.println();

        System.out.println("【Flink SQL 优势】");
        System.out.println("  • 声明式: 描述\"要什么\"，而非\"怎么做\"");
        System.out.println("  • 统一: 同一 SQL 处理流和批");
        System.out.println("  • 优化: Calcite 查询优化器");
        System.out.println("  • 易用: SQL 是通用技能");
        System.out.println();

        System.out.println("【Table API vs SQL】");
        System.out.println("  Table API: Scala/Java 链式调用风格");
        System.out.println("  SQL: 标准 SQL 语法");
        System.out.println("  两者可混合使用，底层共享优化器");
    }

    /**
     * ========================================
     * 第二部分：环境配置
     * ========================================
     */
    public static void explainEnvironment() {
        System.out.println("=== 环境配置 ===");
        System.out.println();

        System.out.println("【创建 TableEnvironment】");
        System.out.println("```java");
        System.out.println("// 流处理环境 (推荐)");
        System.out.println("StreamExecutionEnvironment env = ");
        System.out.println("    StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println("StreamTableEnvironment tableEnv = ");
        System.out.println("    StreamTableEnvironment.create(env);");
        System.out.println();
        System.out.println("// 纯 SQL 环境");
        System.out.println("TableEnvironment tableEnv = TableEnvironment.create(");
        System.out.println("    EnvironmentSettings.newInstance()");
        System.out.println("        .inStreamingMode()  // 或 .inBatchMode()");
        System.out.println("        .build());");
        System.out.println("```");
        System.out.println();

        System.out.println("【配置选项】");
        System.out.println("```java");
        System.out.println("// 设置状态 TTL");
        System.out.println("tableEnv.getConfig().set(\"table.exec.state.ttl\", \"1 h\");");
        System.out.println();
        System.out.println("// 设置时区");
        System.out.println("tableEnv.getConfig().set(\"table.local-time-zone\", \"Asia/Shanghai\");");
        System.out.println();
        System.out.println("// 设置并行度");
        System.out.println("tableEnv.getConfig().set(\"parallelism.default\", \"4\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：DDL 定义表
     * ========================================
     */
    public static void explainDDL() {
        System.out.println("=== DDL 定义表 ===");
        System.out.println();

        System.out.println("【创建表 - Kafka Source】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE orders (");
        System.out.println("    order_id STRING,");
        System.out.println("    user_id STRING,");
        System.out.println("    product_id STRING,");
        System.out.println("    amount DECIMAL(10, 2),");
        System.out.println("    order_time TIMESTAMP(3),");
        System.out.println("    -- 定义水印");
        System.out.println("    WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'kafka',");
        System.out.println("    'topic' = 'orders',");
        System.out.println("    'properties.bootstrap.servers' = 'kafka:9092',");
        System.out.println("    'properties.group.id' = 'flink-sql-group',");
        System.out.println("    'scan.startup.mode' = 'latest-offset',");
        System.out.println("    'format' = 'json'");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【创建表 - JDBC Sink】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE order_stats (");
        System.out.println("    window_start TIMESTAMP(3),");
        System.out.println("    window_end TIMESTAMP(3),");
        System.out.println("    total_amount DECIMAL(10, 2),");
        System.out.println("    order_count BIGINT,");
        System.out.println("    PRIMARY KEY (window_start, window_end) NOT ENFORCED");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'jdbc',");
        System.out.println("    'url' = 'jdbc:mysql://localhost:3306/analytics',");
        System.out.println("    'table-name' = 'order_stats',");
        System.out.println("    'username' = 'root',");
        System.out.println("    'password' = 'password'");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【从 DataStream 创建表】");
        System.out.println("```java");
        System.out.println("DataStream<Order> orderStream = ...;");
        System.out.println();
        System.out.println("// 方式1: 自动推断 Schema");
        System.out.println("Table orderTable = tableEnv.fromDataStream(orderStream);");
        System.out.println();
        System.out.println("// 方式2: 指定列和水印");
        System.out.println("Table orderTable = tableEnv.fromDataStream(");
        System.out.println("    orderStream,");
        System.out.println("    Schema.newBuilder()");
        System.out.println("        .column(\"orderId\", DataTypes.STRING())");
        System.out.println("        .column(\"amount\", DataTypes.DECIMAL(10, 2))");
        System.out.println("        .columnByExpression(\"rowtime\", \"TO_TIMESTAMP(orderTime)\")");
        System.out.println("        .watermark(\"rowtime\", \"rowtime - INTERVAL '5' SECOND\")");
        System.out.println("        .build());");
        System.out.println();
        System.out.println("// 注册为临时视图");
        System.out.println("tableEnv.createTemporaryView(\"orders\", orderTable);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：DML 查询
     * ========================================
     */
    public static void explainDML() {
        System.out.println("=== DML 查询 ===");
        System.out.println();

        System.out.println("【基本查询】");
        System.out.println("```sql");
        System.out.println("-- 简单过滤");
        System.out.println("SELECT order_id, amount ");
        System.out.println("FROM orders ");
        System.out.println("WHERE amount > 100;");
        System.out.println();
        System.out.println("-- 聚合");
        System.out.println("SELECT user_id, SUM(amount) as total");
        System.out.println("FROM orders");
        System.out.println("GROUP BY user_id;");
        System.out.println("```");
        System.out.println();

        System.out.println("【窗口聚合 - 滚动窗口】");
        System.out.println("```sql");
        System.out.println("-- 每10分钟统计一次");
        System.out.println("SELECT ");
        System.out.println("    TUMBLE_START(order_time, INTERVAL '10' MINUTE) as window_start,");
        System.out.println("    TUMBLE_END(order_time, INTERVAL '10' MINUTE) as window_end,");
        System.out.println("    SUM(amount) as total_amount,");
        System.out.println("    COUNT(*) as order_count");
        System.out.println("FROM orders");
        System.out.println("GROUP BY TUMBLE(order_time, INTERVAL '10' MINUTE);");
        System.out.println("```");
        System.out.println();

        System.out.println("【窗口聚合 - 滑动窗口】");
        System.out.println("```sql");
        System.out.println("-- 每5分钟计算过去10分钟的统计");
        System.out.println("SELECT ");
        System.out.println("    HOP_START(order_time, INTERVAL '5' MINUTE, INTERVAL '10' MINUTE) as ws,");
        System.out.println("    HOP_END(order_time, INTERVAL '5' MINUTE, INTERVAL '10' MINUTE) as we,");
        System.out.println("    SUM(amount) as total");
        System.out.println("FROM orders");
        System.out.println("GROUP BY HOP(order_time, INTERVAL '5' MINUTE, INTERVAL '10' MINUTE);");
        System.out.println("```");
        System.out.println();

        System.out.println("【窗口 TVF (Flink 1.13+, 推荐)】");
        System.out.println("```sql");
        System.out.println("-- 使用 Table-Valued Function");
        System.out.println("SELECT ");
        System.out.println("    window_start, window_end,");
        System.out.println("    SUM(amount) as total");
        System.out.println("FROM TABLE(");
        System.out.println("    TUMBLE(TABLE orders, DESCRIPTOR(order_time), INTERVAL '10' MINUTE)");
        System.out.println(")");
        System.out.println("GROUP BY window_start, window_end;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：Join 操作
     * ========================================
     */
    public static void explainJoins() {
        System.out.println("=== Join 操作 ===");
        System.out.println();

        System.out.println("【Regular Join - 双流 Join】");
        System.out.println("  注意: 会保留两边全部状态，需要配置 TTL");
        System.out.println("```sql");
        System.out.println("SELECT o.order_id, o.amount, u.user_name");
        System.out.println("FROM orders o");
        System.out.println("JOIN users u ON o.user_id = u.user_id;");
        System.out.println("```");
        System.out.println();

        System.out.println("【Interval Join - 时间范围 Join】");
        System.out.println("  只 Join 时间范围内的数据，状态自动清理");
        System.out.println("```sql");
        System.out.println("SELECT o.order_id, p.pay_id");
        System.out.println("FROM orders o, payments p");
        System.out.println("WHERE o.order_id = p.order_id");
        System.out.println("  AND p.pay_time BETWEEN o.order_time ");
        System.out.println("                     AND o.order_time + INTERVAL '30' MINUTE;");
        System.out.println("```");
        System.out.println();

        System.out.println("【Temporal Join - 时态表 Join】");
        System.out.println("  用历史版本的维表数据");
        System.out.println("```sql");
        System.out.println("SELECT o.order_id, o.amount, r.rate");
        System.out.println("FROM orders o");
        System.out.println("JOIN exchange_rates FOR SYSTEM_TIME AS OF o.order_time AS r");
        System.out.println("ON o.currency = r.currency;");
        System.out.println("```");
        System.out.println();

        System.out.println("【Lookup Join - 维表关联】");
        System.out.println("  实时查询外部数据库");
        System.out.println("```sql");
        System.out.println("-- 先定义维表");
        System.out.println("CREATE TABLE products (");
        System.out.println("    product_id STRING,");
        System.out.println("    product_name STRING,");
        System.out.println("    price DECIMAL(10, 2),");
        System.out.println("    PRIMARY KEY (product_id) NOT ENFORCED");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'jdbc',");
        System.out.println("    'url' = 'jdbc:mysql://localhost:3306/shop',");
        System.out.println("    'table-name' = 'products',");
        System.out.println("    'lookup.cache.max-rows' = '5000',");
        System.out.println("    'lookup.cache.ttl' = '10 min'");
        System.out.println(");");
        System.out.println();
        System.out.println("-- Lookup Join");
        System.out.println("SELECT o.*, p.product_name, p.price");
        System.out.println("FROM orders o");
        System.out.println("LEFT JOIN products FOR SYSTEM_TIME AS OF o.proc_time AS p");
        System.out.println("ON o.product_id = p.product_id;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：自定义函数
     * ========================================
     */
    public static void explainUdf() {
        System.out.println("=== 自定义函数 (UDF) ===");
        System.out.println();

        System.out.println("【Scalar Function - 标量函数】");
        System.out.println("  一对一映射");
        System.out.println("```java");
        System.out.println("public class MyHash extends ScalarFunction {");
        System.out.println("    public String eval(String value) {");
        System.out.println("        return DigestUtils.md5Hex(value);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("// 注册");
        System.out.println("tableEnv.createTemporaryFunction(\"my_hash\", MyHash.class);");
        System.out.println();
        System.out.println("-- 使用");
        System.out.println("SELECT my_hash(user_id) FROM orders;");
        System.out.println("```");
        System.out.println();

        System.out.println("【Table Function - 表函数】");
        System.out.println("  一对多映射");
        System.out.println("```java");
        System.out.println("@FunctionHint(output = @DataTypeHint(\"ROW<word STRING>\"))");
        System.out.println("public class SplitFunction extends TableFunction<Row> {");
        System.out.println("    public void eval(String str) {");
        System.out.println("        for (String word : str.split(\" \")) {");
        System.out.println("            collect(Row.of(word));");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("-- 使用 (LATERAL TABLE)");
        System.out.println("SELECT o.order_id, word");
        System.out.println("FROM orders o");
        System.out.println("CROSS JOIN LATERAL TABLE(split_words(o.description)) AS T(word);");
        System.out.println("```");
        System.out.println();

        System.out.println("【Aggregate Function - 聚合函数】");
        System.out.println("  多对一聚合");
        System.out.println("```java");
        System.out.println("public class WeightedAvg extends AggregateFunction<Double, WeightedAvg.Acc> {");
        System.out.println("    ");
        System.out.println("    public static class Acc {");
        System.out.println("        public double sum = 0;");
        System.out.println("        public int count = 0;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Acc createAccumulator() { return new Acc(); }");
        System.out.println("    ");
        System.out.println("    public void accumulate(Acc acc, Double value, Integer weight) {");
        System.out.println("        acc.sum += value * weight;");
        System.out.println("        acc.count += weight;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Double getValue(Acc acc) {");
        System.out.println("        return acc.count == 0 ? null : acc.sum / acc.count;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第七部分：动态表与流表转换
     * ========================================
     */
    public static void explainDynamicTables() {
        System.out.println("=== 动态表与流表转换 ===");
        System.out.println();

        System.out.println("【动态表概念】");
        System.out.println("  流式 SQL 中，表是不断变化的\"动态表\"");
        System.out.println("  • 插入: 追加新行");
        System.out.println("  • 更新: 修改已有行（需要主键）");
        System.out.println("  • 删除: 移除行");
        System.out.println();

        System.out.println("【Changelog 模式】");
        System.out.println("  • Append-only: 只追加，如日志");
        System.out.println("  • Upsert: 按主键更新或插入");
        System.out.println("  • Retract: 支持删除（撤回）");
        System.out.println();

        System.out.println("【Table 转 DataStream】");
        System.out.println("```java");
        System.out.println("Table resultTable = tableEnv.sqlQuery(\"SELECT ...\");");
        System.out.println();
        System.out.println("// Append-only 表");
        System.out.println("DataStream<Row> appendStream = tableEnv.toDataStream(resultTable);");
        System.out.println();
        System.out.println("// 带更新/删除的表");
        System.out.println("DataStream<Row> changelogStream = tableEnv.toChangelogStream(resultTable);");
        System.out.println();
        System.out.println("// 处理 RowKind");
        System.out.println("changelogStream.map(row -> {");
        System.out.println("    RowKind kind = row.getKind();  // INSERT, UPDATE_BEFORE, UPDATE_AFTER, DELETE");
        System.out.println("    // ...");
        System.out.println("});");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink SQL                             ║");
        System.out.println("║          声明式流批统一处理                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainFlinkSql();
        System.out.println();

        explainEnvironment();
        System.out.println();

        explainDDL();
        System.out.println();

        explainDML();
        System.out.println();

        explainJoins();
        System.out.println();

        explainUdf();
        System.out.println();

        explainDynamicTables();
    }
}
