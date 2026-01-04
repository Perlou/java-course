package phase24;

/**
 * Delta Lake
 * 
 * Delta Lake 是 Databricks 开源的存储层，
 * 为数据湖带来 ACID 事务和可靠性。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class DeltaLake {

    /**
     * ========================================
     * 第一部分：Delta Lake 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== Delta Lake 概述 ===");
        System.out.println();

        System.out.println("【什么是 Delta Lake】");
        System.out.println("  • 开源存储层（非存储系统）");
        System.out.println("  • 基于 Parquet + 事务日志");
        System.out.println("  • 为数据湖添加可靠性保障");
        System.out.println("  • 由 Databricks 开源");
        System.out.println();

        System.out.println("【核心能力】");
        System.out.println("  • ACID 事务");
        System.out.println("  • 可伸缩的元数据处理");
        System.out.println("  • 时间旅行（Time Travel）");
        System.out.println("  • 统一批流处理");
        System.out.println("  • Schema 演进与强制");
        System.out.println("  • 审计历史");
        System.out.println("  • DML 支持（UPDATE/DELETE/MERGE）");
    }

    /**
     * ========================================
     * 第二部分：Delta Lake 原理
     * ========================================
     */
    public static void explainInternals() {
        System.out.println("=== Delta Lake 原理 ===");
        System.out.println();

        System.out.println("【存储结构】");
        System.out.println("```");
        System.out.println("delta_table/");
        System.out.println("├── _delta_log/               # 事务日志目录");
        System.out.println("│   ├── 00000000000000000000.json");
        System.out.println("│   ├── 00000000000000000001.json");
        System.out.println("│   ├── 00000000000000000010.checkpoint.parquet");
        System.out.println("│   └── _last_checkpoint");
        System.out.println("├── part-00000-xxx.parquet    # 数据文件");
        System.out.println("├── part-00001-xxx.parquet");
        System.out.println("└── part-00002-xxx.parquet");
        System.out.println("```");
        System.out.println();

        System.out.println("【事务日志】");
        System.out.println("  • JSON 格式记录每次操作");
        System.out.println("  • 记录 add/remove 的文件");
        System.out.println("  • 每 10 次提交生成 checkpoint");
        System.out.println("  • checkpoint 是 Parquet 格式的快照");
        System.out.println();

        System.out.println("【乐观并发控制】");
        System.out.println("  1. 读取当前版本");
        System.out.println("  2. 执行写入操作");
        System.out.println("  3. 尝试提交新版本");
        System.out.println("  4. 冲突时重试");
    }

    /**
     * ========================================
     * 第三部分：Delta Lake 操作
     * ========================================
     */
    public static void explainOperations() {
        System.out.println("=== Delta Lake 操作 ===");
        System.out.println();

        System.out.println("【创建 Delta 表】");
        System.out.println("```python");
        System.out.println("# PySpark 创建 Delta 表");
        System.out.println("df.write.format('delta').save('/path/to/delta_table')");
        System.out.println();
        System.out.println("# SQL 创建");
        System.out.println("CREATE TABLE delta_table (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING");
        System.out.println(") USING DELTA");
        System.out.println("LOCATION '/path/to/delta_table';");
        System.out.println("```");
        System.out.println();

        System.out.println("【UPDATE 操作】");
        System.out.println("```sql");
        System.out.println("UPDATE delta_table");
        System.out.println("SET name = 'new_name'");
        System.out.println("WHERE id = 1;");
        System.out.println("```");
        System.out.println();

        System.out.println("【DELETE 操作】");
        System.out.println("```sql");
        System.out.println("DELETE FROM delta_table WHERE id = 1;");
        System.out.println("```");
        System.out.println();

        System.out.println("【MERGE (Upsert) 操作】");
        System.out.println("```sql");
        System.out.println("MERGE INTO target_table t");
        System.out.println("USING source_table s");
        System.out.println("ON t.id = s.id");
        System.out.println("WHEN MATCHED THEN UPDATE SET *");
        System.out.println("WHEN NOT MATCHED THEN INSERT *;");
        System.out.println("```");
        System.out.println();

        System.out.println("【时间旅行】");
        System.out.println("```sql");
        System.out.println("-- 按版本查询");
        System.out.println("SELECT * FROM delta_table VERSION AS OF 5;");
        System.out.println();
        System.out.println("-- 按时间戳查询");
        System.out.println("SELECT * FROM delta_table TIMESTAMP AS OF '2024-01-01 00:00:00';");
        System.out.println();
        System.out.println("-- 查看历史");
        System.out.println("DESCRIBE HISTORY delta_table;");
        System.out.println();
        System.out.println("-- 回滚到指定版本");
        System.out.println("RESTORE TABLE delta_table TO VERSION AS OF 5;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Delta Lake 优化
     * ========================================
     */
    public static void explainOptimization() {
        System.out.println("=== Delta Lake 优化 ===");
        System.out.println();

        System.out.println("【OPTIMIZE 小文件合并】");
        System.out.println("```sql");
        System.out.println("-- 合并小文件");
        System.out.println("OPTIMIZE delta_table;");
        System.out.println();
        System.out.println("-- 按列优化（Z-Ordering）");
        System.out.println("OPTIMIZE delta_table ZORDER BY (user_id, date);");
        System.out.println("```");
        System.out.println();

        System.out.println("【VACUUM 清理历史文件】");
        System.out.println("```sql");
        System.out.println("-- 清理 7 天前的文件（默认保留 7 天）");
        System.out.println("VACUUM delta_table;");
        System.out.println();
        System.out.println("-- 清理所有历史（需禁用安全检查）");
        System.out.println("SET spark.databricks.delta.retentionDurationCheck.enabled = false;");
        System.out.println("VACUUM delta_table RETAIN 0 HOURS;");
        System.out.println("```");
        System.out.println();

        System.out.println("【Auto Optimize】");
        System.out.println("```sql");
        System.out.println("-- 表级别开启自动优化");
        System.out.println("ALTER TABLE delta_table SET TBLPROPERTIES (");
        System.out.println("    'delta.autoOptimize.optimizeWrite' = 'true',");
        System.out.println("    'delta.autoOptimize.autoCompact' = 'true'");
        System.out.println(");");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: Delta Lake                            ║");
        System.out.println("║          可靠的数据湖存储层                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainInternals();
        System.out.println();

        explainOperations();
        System.out.println();

        explainOptimization();
    }
}
