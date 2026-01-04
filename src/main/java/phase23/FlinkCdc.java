package phase23;

/**
 * Flink CDC - 变更数据捕获
 * 
 * CDC (Change Data Capture) 用于捕获数据库的变更事件，
 * 实现数据库到流处理系统的实时同步。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkCdc {

    /**
     * ========================================
     * 第一部分：CDC 概述
     * ========================================
     */
    public static void explainCdcConcept() {
        System.out.println("=== CDC 概述 ===");
        System.out.println();

        System.out.println("【什么是 CDC】");
        System.out.println("  Change Data Capture 变更数据捕获");
        System.out.println("  捕获数据库的 INSERT、UPDATE、DELETE 操作");
        System.out.println("  将变更作为事件流传输");
        System.out.println();

        System.out.println("【CDC 方式对比】");
        System.out.println("┌────────────────┬────────────────────┬──────────────────┐");
        System.out.println("│     方式       │       优点         │       缺点       │");
        System.out.println("├────────────────┼────────────────────┼──────────────────┤");
        System.out.println("│ 时间戳轮询     │ 简单               │ 无法捕获删除     │");
        System.out.println("│ 触发器         │ 实时，全类型       │ 侵入性高         │");
        System.out.println("│ 日志解析       │ 非侵入，高效       │ 实现复杂         │");
        System.out.println("└────────────────┴────────────────────┴──────────────────┘");
        System.out.println();

        System.out.println("【Flink CDC 的实现】");
        System.out.println("  基于数据库日志解析（Binlog/WAL）");
        System.out.println("  • MySQL: 解析 Binlog");
        System.out.println("  • PostgreSQL: 解析 WAL");
        System.out.println("  • MongoDB: 监听 Change Stream");
        System.out.println("  内部使用 Debezium 引擎");
    }

    /**
     * ========================================
     * 第二部分：Flink CDC 架构
     * ========================================
     */
    public static void explainArchitecture() {
        System.out.println("=== Flink CDC 架构 ===");
        System.out.println();

        System.out.println("【架构示意】");
        System.out.println("```");
        System.out.println("┌──────────────┐");
        System.out.println("│   MySQL      │");
        System.out.println("│  (Binlog)    │");
        System.out.println("└──────┬───────┘");
        System.out.println("       │ Binlog 事件");
        System.out.println("       ▼");
        System.out.println("┌──────────────┐     ┌──────────────┐");
        System.out.println("│ Flink CDC    │────►│    Flink     │");
        System.out.println("│   Source     │     │  DataStream  │");
        System.out.println("└──────────────┘     └──────┬───────┘");
        System.out.println("                           │");
        System.out.println("       ┌───────────────────┼───────────────────┐");
        System.out.println("       ▼                   ▼                   ▼");
        System.out.println("┌────────────┐      ┌────────────┐      ┌────────────┐");
        System.out.println("│   Kafka    │      │    ES      │      │   Doris    │");
        System.out.println("└────────────┘      └────────────┘      └────────────┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【Flink CDC 版本演进】");
        System.out.println("  2.0: 引入增量快照读取，无锁读取");
        System.out.println("  2.1: 支持并行读取");
        System.out.println("  2.2: 支持 Schema Evolution");
        System.out.println("  2.3: 支持更多数据库");
        System.out.println("  3.0: 重构 API，增强稳定性");
    }

    /**
     * ========================================
     * 第三部分：MySQL CDC 使用
     * ========================================
     */
    public static void explainMysqlCdc() {
        System.out.println("=== MySQL CDC 使用 ===");
        System.out.println();

        System.out.println("【1. MySQL 配置准备】");
        System.out.println("```sql");
        System.out.println("-- 开启 Binlog");
        System.out.println("-- my.cnf 配置");
        System.out.println("-- server-id = 1");
        System.out.println("-- log_bin = mysql-bin");
        System.out.println("-- binlog_format = ROW");
        System.out.println("-- binlog_row_image = FULL");
        System.out.println();
        System.out.println("-- 创建 CDC 用户");
        System.out.println("CREATE USER 'flink'@'%' IDENTIFIED BY 'flink123';");
        System.out.println(
                "GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'flink'@'%';");
        System.out.println("FLUSH PRIVILEGES;");
        System.out.println("```");
        System.out.println();

        System.out.println("【2. Maven 依赖】");
        System.out.println("```xml");
        System.out.println("<dependency>");
        System.out.println("    <groupId>com.ververica</groupId>");
        System.out.println("    <artifactId>flink-sql-connector-mysql-cdc</artifactId>");
        System.out.println("    <version>3.0.0</version>");
        System.out.println("</dependency>");
        System.out.println("```");
        System.out.println();

        System.out.println("【3. Flink SQL 方式】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE orders (");
        System.out.println("    order_id INT,");
        System.out.println("    order_date TIMESTAMP(3),");
        System.out.println("    customer_name STRING,");
        System.out.println("    price DECIMAL(10, 2),");
        System.out.println("    product_id INT,");
        System.out.println("    order_status STRING,");
        System.out.println("    PRIMARY KEY (order_id) NOT ENFORCED");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'mysql-cdc',");
        System.out.println("    'hostname' = 'localhost',");
        System.out.println("    'port' = '3306',");
        System.out.println("    'username' = 'flink',");
        System.out.println("    'password' = 'flink123',");
        System.out.println("    'database-name' = 'mydb',");
        System.out.println("    'table-name' = 'orders'");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 查询实时变更");
        System.out.println("SELECT * FROM orders;");
        System.out.println("```");
        System.out.println();

        System.out.println("【4. DataStream API 方式】");
        System.out.println("```java");
        System.out.println("MySqlSource<String> source = MySqlSource.<String>builder()");
        System.out.println("    .hostname(\"localhost\")");
        System.out.println("    .port(3306)");
        System.out.println("    .databaseList(\"mydb\")");
        System.out.println("    .tableList(\"mydb.orders\")");
        System.out.println("    .username(\"flink\")");
        System.out.println("    .password(\"flink123\")");
        System.out.println("    .deserializer(new JsonDebeziumDeserializationSchema())");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("DataStream<String> stream = env.fromSource(");
        System.out.println("    source, WatermarkStrategy.noWatermarks(), \"MySQL CDC Source\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：增量快照读取
     * ========================================
     */
    public static void explainIncrementalSnapshot() {
        System.out.println("=== 增量快照读取 ===");
        System.out.println();

        System.out.println("【全量 + 增量同步机制】");
        System.out.println("```");
        System.out.println("阶段1: 全量快照读取（无锁）");
        System.out.println("  ┌──────────────────────────────┐");
        System.out.println("  │  表数据按 Chunk 分片并行读取  │");
        System.out.println("  │  [Chunk1][Chunk2][Chunk3]... │");
        System.out.println("  └──────────────────────────────┘");
        System.out.println();
        System.out.println("阶段2: 增量 Binlog 读取");
        System.out.println("  ┌──────────────────────────────┐");
        System.out.println("  │  从快照结束点读取 Binlog      │");
        System.out.println("  │  INSERT/UPDATE/DELETE 事件   │");
        System.out.println("  └──────────────────────────────┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【Chunk 分片策略】");
        System.out.println("```java");
        System.out.println("MySqlSource.<String>builder()");
        System.out.println("    .splitSize(8096)           // 每个 Chunk 的行数");
        System.out.println("    .fetchSize(1024)           // 每次 fetch 的行数");
        System.out.println("    .splitMetaGroupSize(1000)  // 元数据分组大小");
        System.out.println("    .build();");
        System.out.println("```");
        System.out.println();

        System.out.println("【优势】");
        System.out.println("  • 无锁读取：不阻塞业务写入");
        System.out.println("  • 并行读取：多任务并行读取不同 Chunk");
        System.out.println("  • 断点续传：支持从故障点恢复");
        System.out.println("  • 精确一次：Checkpoint 保证");
    }

    /**
     * ========================================
     * 第五部分：数据去重与 Upsert
     * ========================================
     */
    public static void explainDeduplication() {
        System.out.println("=== 数据去重与 Upsert ===");
        System.out.println();

        System.out.println("【CDC 消息格式】");
        System.out.println("  每条 CDC 消息包含:");
        System.out.println("  • op: 操作类型 (c=create, u=update, d=delete, r=read)");
        System.out.println("  • before: 变更前的数据");
        System.out.println("  • after: 变更后的数据");
        System.out.println();

        System.out.println("【Flink SQL 自动处理】");
        System.out.println("  Flink SQL 会自动将 CDC 格式转为 Changelog");
        System.out.println("```sql");
        System.out.println("-- CDC 源表");
        System.out.println("CREATE TABLE src_orders (...) WITH ('connector' = 'mysql-cdc', ...);");
        System.out.println();
        System.out.println("-- 输出到支持 Upsert 的 Sink");
        System.out.println("CREATE TABLE sink_orders (");
        System.out.println("    order_id INT,");
        System.out.println("    ...,");
        System.out.println("    PRIMARY KEY (order_id) NOT ENFORCED  -- 重要!");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'upsert-kafka',");
        System.out.println("    ...);");
        System.out.println();
        System.out.println("INSERT INTO sink_orders SELECT * FROM src_orders;");
        System.out.println("```");
        System.out.println();

        System.out.println("【DataStream 处理方式】");
        System.out.println("```java");
        System.out.println("// 解析 Debezium JSON");
        System.out.println("stream.map(json -> {");
        System.out.println("    JSONObject obj = JSON.parseObject(json);");
        System.out.println("    String op = obj.getString(\"op\");");
        System.out.println("    JSONObject data;");
        System.out.println("    if (\"d\".equals(op)) {");
        System.out.println("        data = obj.getJSONObject(\"before\"); // 删除用 before");
        System.out.println("    } else {");
        System.out.println("        data = obj.getJSONObject(\"after\");  // 其他用 after");
        System.out.println("    }");
        System.out.println("    return new Order(op, data.getInteger(\"order_id\"), ...);");
        System.out.println("});");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：整库同步
     * ========================================
     */
    public static void explainWholeDatabaseSync() {
        System.out.println("=== 整库同步 ===");
        System.out.println();

        System.out.println("【整库同步场景】");
        System.out.println("  • 数据仓库同步");
        System.out.println("  • 数据库迁移");
        System.out.println("  • 异构数据库同步");
        System.out.println();

        System.out.println("【多表同步】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE all_tables (");
        System.out.println("    database_name STRING METADATA VIRTUAL,");
        System.out.println("    table_name STRING METADATA VIRTUAL,");
        System.out.println("    order_id INT,");
        System.out.println("    ...,");
        System.out.println("    PRIMARY KEY (database_name, table_name, order_id) NOT ENFORCED");
        System.out.println(") WITH (");
        System.out.println("    'connector' = 'mysql-cdc',");
        System.out.println("    'hostname' = 'localhost',");
        System.out.println("    'port' = '3306',");
        System.out.println("    'username' = 'flink',");
        System.out.println("    'password' = 'flink123',");
        System.out.println("    'database-name' = 'shop_.*',     -- 正则匹配多库");
        System.out.println("    'table-name' = 'order_.*'        -- 正则匹配多表");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【使用 YAML 配置整库同步】");
        System.out.println("```yaml");
        System.out.println("# flink-cdc.yaml");
        System.out.println("source:");
        System.out.println("  type: mysql");
        System.out.println("  hostname: localhost");
        System.out.println("  port: 3306");
        System.out.println("  username: flink");
        System.out.println("  password: flink123");
        System.out.println("  tables: mydb.orders, mydb.customers, mydb.products");
        System.out.println();
        System.out.println("sink:");
        System.out.println("  type: doris");
        System.out.println("  fenodes: 127.0.0.1:8030");
        System.out.println("  username: root");
        System.out.println("  password: \"\"");
        System.out.println("  ");
        System.out.println("pipeline:");
        System.out.println("  name: MySQL to Doris Pipeline");
        System.out.println("  parallelism: 4");
        System.out.println("```");
        System.out.println();
        System.out.println("```bash");
        System.out.println("# 使用 flink-cdc CLI 启动");
        System.out.println("./bin/flink-cdc.sh flink-cdc.yaml");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第七部分：最佳实践
     * ========================================
     */
    public static void explainBestPractices() {
        System.out.println("=== 最佳实践 ===");
        System.out.println();

        System.out.println("【1. 性能优化】");
        System.out.println("  • 合理设置 splitSize，避免单 Chunk 过大");
        System.out.println("  • 启用并行读取，提高全量同步速度");
        System.out.println("  • 关注 Binlog 延迟指标");
        System.out.println();

        System.out.println("【2. 容错配置】");
        System.out.println("  • 启用 Checkpoint");
        System.out.println("  • 配置合理的重启策略");
        System.out.println("  • 保留 Binlog/WAL 足够长时间");
        System.out.println();

        System.out.println("【3. 监控指标】");
        System.out.println("  • sourceIdleTime: 源空闲时间");
        System.out.println("  • currentEmitEventTimeLag: 事件延迟");
        System.out.println("  • numRecordsIn: 输入记录数");
        System.out.println();

        System.out.println("【4. 常见问题】");
        System.out.println();
        System.out.println("  问题: Binlog 已被清理，无法恢复");
        System.out.println("  解决: 延长 Binlog 保留时间或重新全量同步");
        System.out.println("```sql");
        System.out.println("SET GLOBAL expire_logs_days = 7;");
        System.out.println("```");
        System.out.println();
        System.out.println("  问题: 大表全量同步慢");
        System.out.println("  解决: 增加并行度，调小 splitSize");
        System.out.println("```java");
        System.out.println(".splitSize(4096)");
        System.out.println(".parallelism(8)");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink CDC                             ║");
        System.out.println("║          实时数据同步                                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainCdcConcept();
        System.out.println();

        explainArchitecture();
        System.out.println();

        explainMysqlCdc();
        System.out.println();

        explainIncrementalSnapshot();
        System.out.println();

        explainDeduplication();
        System.out.println();

        explainWholeDatabaseSync();
        System.out.println();

        explainBestPractices();
    }
}
