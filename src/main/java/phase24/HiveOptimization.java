package phase24;

/**
 * Hive 优化
 * 
 * Hive 性能优化的关键技术和最佳实践。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class HiveOptimization {

    /**
     * ========================================
     * 第一部分：执行引擎选择
     * ========================================
     */
    public static void explainExecutionEngines() {
        System.out.println("=== 执行引擎选择 ===");
        System.out.println();

        System.out.println("【执行引擎对比】");
        System.out.println("┌──────────┬───────────┬───────────┬───────────────────────┐");
        System.out.println("│ 引擎     │ 性能      │ 稳定性    │ 适用场景              │");
        System.out.println("├──────────┼───────────┼───────────┼───────────────────────┤");
        System.out.println("│ MR       │ 慢        │ 高        │ 历史遗留              │");
        System.out.println("│ Tez      │ 快 3-5x   │ 高        │ Hive 推荐             │");
        System.out.println("│ Spark    │ 快 5-10x  │ 中        │ 迭代/机器学习         │");
        System.out.println("│ LLAP     │ 亚秒级    │ 中        │ 交互式查询            │");
        System.out.println("└──────────┴───────────┴───────────┴───────────────────────┘");
        System.out.println();

        System.out.println("【配置示例】");
        System.out.println("```sql");
        System.out.println("-- 使用 Tez 引擎");
        System.out.println("SET hive.execution.engine=tez;");
        System.out.println();
        System.out.println("-- 使用 Spark 引擎");
        System.out.println("SET hive.execution.engine=spark;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：数据倾斜优化
     * ========================================
     */
    public static void explainSkewOptimization() {
        System.out.println("=== 数据倾斜优化 ===");
        System.out.println();

        System.out.println("【什么是数据倾斜】");
        System.out.println("  某些 Key 的数据量远大于其他 Key");
        System.out.println("  导致少数 Task 处理大量数据，拖慢整体");
        System.out.println();

        System.out.println("【解决方案】");
        System.out.println();

        System.out.println("1. 开启倾斜优化");
        System.out.println("```sql");
        System.out.println("SET hive.optimize.skewjoin=true;");
        System.out.println("SET hive.skewjoin.key=100000;  -- 阈值");
        System.out.println("```");
        System.out.println();

        System.out.println("2. 加盐打散");
        System.out.println("```sql");
        System.out.println("-- 大表加随机前缀");
        System.out.println("SELECT * FROM (");
        System.out.println("    SELECT *, CONCAT(key, '_', FLOOR(RAND() * 10)) AS salt_key");
        System.out.println("    FROM big_table");
        System.out.println(") a");
        System.out.println("JOIN (");
        System.out.println("    -- 小表膨胀（复制多份）");
        System.out.println("    SELECT *, CONCAT(key, '_', num) AS salt_key");
        System.out.println("    FROM small_table");
        System.out.println("    LATERAL VIEW EXPLODE(ARRAY(0,1,2,3,4,5,6,7,8,9)) t AS num");
        System.out.println(") b ON a.salt_key = b.salt_key;");
        System.out.println("```");
        System.out.println();

        System.out.println("3. Map Join（小表广播）");
        System.out.println("```sql");
        System.out.println("SET hive.auto.convert.join=true;");
        System.out.println("SET hive.mapjoin.smalltable.filesize=25000000;  -- 25MB");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Join 优化
     * ========================================
     */
    public static void explainJoinOptimization() {
        System.out.println("=== Join 优化 ===");
        System.out.println();

        System.out.println("【Join 类型】");
        System.out.println("  • Common Join: Reduce 端 Join");
        System.out.println("  • Map Join: 小表广播到 Map 端");
        System.out.println("  • Bucket Map Join: 分桶表 Join");
        System.out.println("  • SMB Join: 排序分桶 Join");
        System.out.println();

        System.out.println("【Map Join 配置】");
        System.out.println("```sql");
        System.out.println("-- 自动转换小表为 Map Join");
        System.out.println("SET hive.auto.convert.join=true;");
        System.out.println("SET hive.mapjoin.smalltable.filesize=25000000;");
        System.out.println();
        System.out.println("-- 手动指定 Map Join");
        System.out.println("SELECT /*+ MAPJOIN(small_table) */ *");
        System.out.println("FROM big_table a JOIN small_table b ON a.id = b.id;");
        System.out.println("```");
        System.out.println();

        System.out.println("【SMB Join（排序分桶 Join）】");
        System.out.println("```sql");
        System.out.println("-- 创建分桶排序表");
        System.out.println("CREATE TABLE bucket_table (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING");
        System.out.println(")");
        System.out.println("CLUSTERED BY (id) SORTED BY (id) INTO 32 BUCKETS;");
        System.out.println();
        System.out.println("-- 开启 SMB Join");
        System.out.println("SET hive.optimize.bucketmapjoin=true;");
        System.out.println("SET hive.optimize.bucketmapjoin.sortedmerge=true;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：压缩与存储优化
     * ========================================
     */
    public static void explainStorageOptimization() {
        System.out.println("=== 压缩与存储优化 ===");
        System.out.println();

        System.out.println("【压缩配置】");
        System.out.println("```sql");
        System.out.println("-- 开启中间结果压缩");
        System.out.println("SET hive.exec.compress.intermediate=true;");
        System.out.println("SET mapreduce.map.output.compress=true;");
        System.out.println("SET mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec;");
        System.out.println();
        System.out.println("-- 开启输出压缩");
        System.out.println("SET hive.exec.compress.output=true;");
        System.out.println("SET mapreduce.output.fileoutputformat.compress=true;");
        System.out.println(
                "SET mapreduce.output.fileoutputformat.compress.codec=org.apache.hadoop.io.compress.SnappyCodec;");
        System.out.println("```");
        System.out.println();

        System.out.println("【ORC 优化】");
        System.out.println("```sql");
        System.out.println("-- 创建 ORC 表");
        System.out.println("CREATE TABLE orc_table (");
        System.out.println("    id BIGINT,");
        System.out.println("    name STRING");
        System.out.println(")");
        System.out.println("STORED AS ORC");
        System.out.println("TBLPROPERTIES (");
        System.out.println("    'orc.compress'='SNAPPY',      -- 压缩算法");
        System.out.println("    'orc.bloom.filter.columns'='id',  -- 布隆过滤器");
        System.out.println("    'orc.create.index'='true'      -- 创建索引");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【小文件合并】");
        System.out.println("```sql");
        System.out.println("-- 合并小文件");
        System.out.println("SET hive.merge.mapfiles=true;");
        System.out.println("SET hive.merge.mapredfiles=true;");
        System.out.println("SET hive.merge.size.per.task=256000000;  -- 256MB");
        System.out.println("SET hive.merge.smallfiles.avgsize=16000000;  -- 16MB");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：SQL 优化技巧
     * ========================================
     */
    public static void explainSqlOptimization() {
        System.out.println("=== SQL 优化技巧 ===");
        System.out.println();

        System.out.println("【谓词下推】");
        System.out.println("```sql");
        System.out.println("-- 推荐：先过滤再 Join");
        System.out.println("SELECT * FROM");
        System.out.println("(SELECT * FROM orders WHERE dt = '2024-01-01') a");
        System.out.println("JOIN users b ON a.user_id = b.id;");
        System.out.println("```");
        System.out.println();

        System.out.println("【列裁剪】");
        System.out.println("```sql");
        System.out.println("-- 避免 SELECT *，只查需要的列");
        System.out.println("SELECT id, name FROM big_table;  -- 推荐");
        System.out.println("-- SELECT * FROM big_table;     -- 避免");
        System.out.println("```");
        System.out.println();

        System.out.println("【分区裁剪】");
        System.out.println("```sql");
        System.out.println("-- 使用分区字段过滤");
        System.out.println("SELECT * FROM orders WHERE dt = '2024-01-01';  -- 推荐");
        System.out.println("-- SELECT * FROM orders WHERE create_time = '2024-01-01';  -- 不推荐");
        System.out.println("```");
        System.out.println();

        System.out.println("【避免笛卡尔积】");
        System.out.println("```sql");
        System.out.println("-- 开启严格模式，禁止笛卡尔积");
        System.out.println("SET hive.mapred.mode=strict;");
        System.out.println("```");
        System.out.println();

        System.out.println("【EXPLAIN 分析】");
        System.out.println("```sql");
        System.out.println("-- 查看执行计划");
        System.out.println("EXPLAIN SELECT * FROM orders WHERE dt = '2024-01-01';");
        System.out.println();
        System.out.println("-- 查看详细执行计划");
        System.out.println("EXPLAIN EXTENDED SELECT ...;");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: Hive 优化                             ║");
        System.out.println("║          性能调优最佳实践                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainExecutionEngines();
        System.out.println();

        explainSkewOptimization();
        System.out.println();

        explainJoinOptimization();
        System.out.println();

        explainStorageOptimization();
        System.out.println();

        explainSqlOptimization();
    }
}
