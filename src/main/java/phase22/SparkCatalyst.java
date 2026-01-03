package phase22;

/**
 * Spark Catalyst 优化器原理
 * 
 * Catalyst 是 SparkSQL 的查询优化器，
 * 负责将用户的查询转换为高效的执行计划。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkCatalyst {

    public static void main(String[] args) {
        System.out.println("=== Spark Catalyst 优化器原理 ===\n");

        catalystOverview();
        optimizationPhases();
        logicalOptimization();
        physicalOptimization();
        codeGeneration();
        explainPlan();
    }

    private static void catalystOverview() {
        System.out.println("【Catalyst 概述】\n");

        System.out.println("Catalyst 是什么？");
        System.out.println("  • SparkSQL 的核心查询优化框架");
        System.out.println("  • 基于函数式编程和规则的优化器");
        System.out.println("  • 自动优化 DataFrame/Dataset/SQL 查询");
        System.out.println();

        System.out.println("Catalyst 执行流程：");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   SQL / DataFrame / Dataset                            │");
        System.out.println("  └──────────────────────┬────────────────────────────────┘");
        System.out.println("                         ▼");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   1. 解析 (Parsing)                                     │");
        System.out.println("  │      SQL -> 未解析的逻辑计划 (Unresolved Logical Plan)  │");
        System.out.println("  └──────────────────────┬────────────────────────────────┘");
        System.out.println("                         ▼");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   2. 分析 (Analysis)                                    │");
        System.out.println("  │      绑定元数据 -> 已解析的逻辑计划 (Analyzed Plan)      │");
        System.out.println("  └──────────────────────┬────────────────────────────────┘");
        System.out.println("                         ▼");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   3. 逻辑优化 (Logical Optimization)                    │");
        System.out.println("  │      应用优化规则 -> 优化的逻辑计划 (Optimized Plan)     │");
        System.out.println("  └──────────────────────┬────────────────────────────────┘");
        System.out.println("                         ▼");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   4. 物理规划 (Physical Planning)                       │");
        System.out.println("  │      选择最优策略 -> 物理计划 (Physical Plan)            │");
        System.out.println("  └──────────────────────┬────────────────────────────────┘");
        System.out.println("                         ▼");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │   5. 代码生成 (Code Generation)                         │");
        System.out.println("  │      Whole-Stage CodeGen -> 优化的 Java 字节码          │");
        System.out.println("  └─────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void optimizationPhases() {
        System.out.println("【优化阶段详解】\n");

        System.out.println("1. 解析阶段 (Parsing)");
        System.out.println("   • 将 SQL 字符串解析为抽象语法树 (AST)");
        System.out.println("   • 生成未绑定的逻辑计划");
        System.out.println("   • 此时不验证表名、列名是否存在");
        System.out.println();

        System.out.println("2. 分析阶段 (Analysis)");
        System.out.println("   • 使用 Catalog 绑定表名、列名");
        System.out.println("   • 解析列的数据类型");
        System.out.println("   • 验证语义正确性");
        System.out.println("   • 解析函数调用");
        System.out.println();

        System.out.println("3. 逻辑优化阶段 (Logical Optimization)");
        System.out.println("   • 应用规则优化逻辑计划");
        System.out.println("   • 与具体执行策略无关");
        System.out.println("   • 例：谓词下推、常量折叠、列剪裁");
        System.out.println();

        System.out.println("4. 物理规划阶段 (Physical Planning)");
        System.out.println("   • 将逻辑计划转换为物理计划");
        System.out.println("   • 选择具体的算法实现");
        System.out.println("   • 基于成本模型选择最优计划");
        System.out.println("   • 例：选择 BroadcastHashJoin 还是 SortMergeJoin");
        System.out.println();

        System.out.println("5. 代码生成阶段 (Code Generation)");
        System.out.println("   • 生成优化的 Java 字节码");
        System.out.println("   • Whole-Stage CodeGen 减少虚函数调用");
        System.out.println("   • 提升 CPU 效率");
        System.out.println();
    }

    private static void logicalOptimization() {
        System.out.println("【逻辑优化规则】\n");

        System.out.println("1. 谓词下推 (Predicate Pushdown)");
        System.out.println("   将过滤条件尽可能下推到数据源");
        System.out.println("   优化前: scan -> join -> filter");
        System.out.println("   优化后: scan(with filter) -> join");
        System.out.println();

        System.out.println("2. 列剪裁 (Column Pruning)");
        System.out.println("   只读取需要的列，减少 IO");
        System.out.println("   SELECT name FROM users  // 只读取 name 列");
        System.out.println();

        System.out.println("3. 常量折叠 (Constant Folding)");
        System.out.println("   编译时计算常量表达式");
        System.out.println("   1 + 2 + 3 -> 6");
        System.out.println();

        System.out.println("4. 表达式简化 (Expression Simplification)");
        System.out.println("   x * 1 -> x");
        System.out.println("   x + 0 -> x");
        System.out.println("   true AND x -> x");
        System.out.println();

        System.out.println("5. Join 重排序 (Join Reordering)");
        System.out.println("   根据表大小调整 Join 顺序");
        System.out.println("   小表放在 Join 的左边或右边");
        System.out.println();

        System.out.println("6. 子查询展开 (Subquery Flattening)");
        System.out.println("   将子查询转换为 Join");
        System.out.println();
    }

    private static void physicalOptimization() {
        System.out.println("【物理优化策略】\n");

        System.out.println("Join 策略选择：");
        System.out.println();

        System.out.println("1. BroadcastHashJoin");
        System.out.println("   • 小表广播到所有节点");
        System.out.println("   • 适用：小表 < spark.sql.autoBroadcastJoinThreshold (10MB)");
        System.out.println("   • 优点：无 Shuffle");
        System.out.println();

        System.out.println("2. ShuffleHashJoin");
        System.out.println("   • 两表都按 Join Key Shuffle");
        System.out.println("   • 然后构建 Hash 表进行 Join");
        System.out.println("   • 适用：一表较小可以放入内存");
        System.out.println();

        System.out.println("3. SortMergeJoin");
        System.out.println("   • 两表都按 Join Key 排序");
        System.out.println("   • 然后归并连接");
        System.out.println("   • 适用：大表与大表 Join");
        System.out.println("   • 默认策略");
        System.out.println();

        System.out.println("4. BroadcastNestedLoopJoin");
        System.out.println("   • 笛卡尔积 + 过滤");
        System.out.println("   • 适用：非等值 Join");
        System.out.println();

        System.out.println("聚合策略选择：");
        System.out.println("  • HashAggregate：基于 Hash 表聚合");
        System.out.println("  • SortAggregate：排序后聚合");
        System.out.println();
    }

    private static void codeGeneration() {
        System.out.println("【代码生成 (Tungsten)】\n");

        System.out.println("Whole-Stage Code Generation：");
        System.out.println("  • 将多个算子编译为单个 Java 方法");
        System.out.println("  • 减少虚函数调用开销");
        System.out.println("  • 利用 CPU 寄存器和流水线");
        System.out.println();

        System.out.println("优化效果：");
        System.out.println("  传统方式: next() -> next() -> next() (多次虚函数调用)");
        System.out.println("  CodeGen:  生成一个紧凑的 while 循环");
        System.out.println();

        System.out.println("查看生成的代码：");
        System.out.println("  df.queryExecution().debug().codegen()");
        System.out.println();
    }

    private static void explainPlan() {
        System.out.println("【查看执行计划】\n");

        System.out.println("使用 explain() 查看执行计划：");
        System.out.println();
        System.out.println("  df.explain()           // 简单物理计划");
        System.out.println("  df.explain(true)       // 完整计划");
        System.out.println("  df.explain(\"simple\")   // 物理计划");
        System.out.println("  df.explain(\"extended\") // 逻辑+物理计划");
        System.out.println("  df.explain(\"codegen\")  // 生成的代码");
        System.out.println("  df.explain(\"cost\")     // 包含成本信息");
        System.out.println("  df.explain(\"formatted\")// 格式化输出");
        System.out.println();

        System.out.println("执行计划示例：");
        System.out.println("  == Physical Plan ==");
        System.out.println("  *(2) HashAggregate(keys=[city], functions=[count(1)])");
        System.out.println("  +- Exchange hashpartitioning(city, 200)");
        System.out.println("     +- *(1) HashAggregate(keys=[city], functions=[partial_count(1)])");
        System.out.println("        +- *(1) Filter (age > 18)");
        System.out.println("           +- *(1) FileScan parquet [city,age]");
        System.out.println();

        System.out.println("符号说明：");
        System.out.println("  * 表示 Whole-Stage CodeGen");
        System.out.println("  +- 表示子节点");
        System.out.println("  Exchange 表示 Shuffle");
        System.out.println("  BroadcastExchange 表示广播");
    }
}
