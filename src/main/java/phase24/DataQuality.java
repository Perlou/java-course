package phase24;

/**
 * 数据质量
 * 
 * 数据质量是数据治理的核心，确保数据的可用性和可靠性。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class DataQuality {

    /**
     * ========================================
     * 第一部分：数据质量概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== 数据质量概述 ===");
        System.out.println();

        System.out.println("【什么是数据质量】");
        System.out.println("  数据满足业务需求的程度");
        System.out.println("  包括数据的准确性、完整性、一致性等");
        System.out.println();

        System.out.println("【为什么重要】");
        System.out.println("  • 数据决策的基础");
        System.out.println("  • 避免\"垃圾进，垃圾出\"");
        System.out.println("  • 降低数据使用成本");
        System.out.println("  • 满足合规要求");
    }

    /**
     * ========================================
     * 第二部分：数据质量六维度
     * ========================================
     */
    public static void explainDimensions() {
        System.out.println("=== 数据质量六维度 ===");
        System.out.println();

        System.out.println("┌──────────────┬───────────────────────────────────────────┐");
        System.out.println("│ 维度         │ 说明                                       │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 完整性       │ 数据是否缺失                              │");
        System.out.println("│ Completeness │ 检查：NULL值、空字符串、必填字段           │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 准确性       │ 数据是否正确                              │");
        System.out.println("│ Accuracy     │ 检查：与源数据比对、业务规则验证           │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 一致性       │ 多源数据是否一致                           │");
        System.out.println("│ Consistency  │ 检查：跨表/跨系统数据比对                  │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 时效性       │ 数据是否及时                              │");
        System.out.println("│ Timeliness   │ 检查：数据延迟、更新频率                   │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 唯一性       │ 是否有重复数据                            │");
        System.out.println("│ Uniqueness   │ 检查：主键重复、业务键重复                 │");
        System.out.println("├──────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 有效性       │ 数据是否符合业务规则                       │");
        System.out.println("│ Validity     │ 检查：枚举值、格式、范围                   │");
        System.out.println("└──────────────┴───────────────────────────────────────────┘");
    }

    /**
     * ========================================
     * 第三部分：质量规则示例
     * ========================================
     */
    public static void explainRules() {
        System.out.println("=== 数据质量规则示例 ===");
        System.out.println();

        System.out.println("【完整性规则】");
        System.out.println("```sql");
        System.out.println("-- 检查必填字段");
        System.out.println("SELECT COUNT(*) AS null_count");
        System.out.println("FROM orders WHERE user_id IS NULL;");
        System.out.println();
        System.out.println("-- 完整性比率");
        System.out.println("SELECT ");
        System.out.println("    COUNT(CASE WHEN user_id IS NOT NULL THEN 1 END) * 100.0 / COUNT(*)");
        System.out.println("    AS completeness_rate");
        System.out.println("FROM orders;");
        System.out.println("```");
        System.out.println();

        System.out.println("【唯一性规则】");
        System.out.println("```sql");
        System.out.println("-- 检查主键重复");
        System.out.println("SELECT order_id, COUNT(*) AS cnt");
        System.out.println("FROM orders");
        System.out.println("GROUP BY order_id");
        System.out.println("HAVING COUNT(*) > 1;");
        System.out.println("```");
        System.out.println();

        System.out.println("【有效性规则】");
        System.out.println("```sql");
        System.out.println("-- 检查枚举值");
        System.out.println("SELECT * FROM orders");
        System.out.println("WHERE status NOT IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED');");
        System.out.println();
        System.out.println("-- 检查数值范围");
        System.out.println("SELECT * FROM orders WHERE amount < 0;");
        System.out.println();
        System.out.println("-- 检查格式（正则）");
        System.out.println("SELECT * FROM users WHERE email NOT RLIKE '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+$';");
        System.out.println("```");
        System.out.println();

        System.out.println("【一致性规则】");
        System.out.println("```sql");
        System.out.println("-- 跨表一致性");
        System.out.println("SELECT a.total_amount, b.sum_amount");
        System.out.println("FROM order_summary a");
        System.out.println("JOIN (SELECT SUM(amount) AS sum_amount FROM order_details) b");
        System.out.println("WHERE a.total_amount != b.sum_amount;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：数据质量工具
     * ========================================
     */
    public static void explainTools() {
        System.out.println("=== 数据质量工具 ===");
        System.out.println();

        System.out.println("【开源工具】");
        System.out.println("  • Great Expectations: Python 质量框架");
        System.out.println("  • Deequ: Spark 上的质量库");
        System.out.println("  • Apache Griffin: 大数据质量平台");
        System.out.println("  • Soda: SQL 质量检查");
        System.out.println();

        System.out.println("【Great Expectations 示例】");
        System.out.println("```python");
        System.out.println("import great_expectations as ge");
        System.out.println();
        System.out.println("df_ge = ge.from_pandas(df)");
        System.out.println();
        System.out.println("# 定义期望");
        System.out.println("df_ge.expect_column_values_to_not_be_null('user_id')");
        System.out.println("df_ge.expect_column_values_to_be_unique('order_id')");
        System.out.println("df_ge.expect_column_values_to_be_in_set('status', ['A', 'B', 'C'])");
        System.out.println();
        System.out.println("# 验证");
        System.out.println("results = df_ge.validate()");
        System.out.println("```");
        System.out.println();

        System.out.println("【Deequ 示例 (Spark)】");
        System.out.println("```scala");
        System.out.println("import com.amazon.deequ.VerificationSuite");
        System.out.println("import com.amazon.deequ.checks.{Check, CheckLevel}");
        System.out.println();
        System.out.println("val verificationResult = VerificationSuite()");
        System.out.println("  .onData(df)");
        System.out.println("  .addCheck(");
        System.out.println("    Check(CheckLevel.Error, \"Data Quality\")");
        System.out.println("      .isComplete(\"user_id\")");
        System.out.println("      .isUnique(\"order_id\")");
        System.out.println("      .isNonNegative(\"amount\")");
        System.out.println("  )");
        System.out.println("  .run()");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：数据质量体系建设
     * ========================================
     */
    public static void explainImplementation() {
        System.out.println("=== 数据质量体系建设 ===");
        System.out.println();

        System.out.println("【建设步骤】");
        System.out.println("  1. 定义质量标准和规则");
        System.out.println("  2. 实现自动化检测");
        System.out.println("  3. 建立监控告警");
        System.out.println("  4. 质量报告与仪表盘");
        System.out.println("  5. 问题修复与追溯");
        System.out.println();

        System.out.println("【质量指标】");
        System.out.println("  • DQI (Data Quality Index): 综合质量分数");
        System.out.println("  • 各维度通过率");
        System.out.println("  • 规则执行数量");
        System.out.println("  • 问题修复率");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: 数据质量                              ║");
        System.out.println("║          数据治理的核心                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainDimensions();
        System.out.println();

        explainRules();
        System.out.println();

        explainTools();
        System.out.println();

        explainImplementation();
    }
}
