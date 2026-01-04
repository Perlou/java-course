package phase24;

/**
 * 缓慢变化维度 (SCD - Slowly Changing Dimension)
 * 
 * 缓慢变化维度是数仓中处理维度属性历史变化的技术，
 * 保留或覆盖维度的历史状态。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class SlowlyChangingDim {

    /**
     * ========================================
     * 第一部分：SCD 概述
     * ========================================
     */
    public static void explainScdOverview() {
        System.out.println("=== 缓慢变化维度概述 ===");
        System.out.println();

        System.out.println("【什么是 SCD】");
        System.out.println("  维度表的属性会随时间变化");
        System.out.println("  如：用户地址变更、产品价格调整");
        System.out.println("  SCD 定义了如何处理这些变化");
        System.out.println();

        System.out.println("【SCD 类型】");
        System.out.println("┌──────────┬────────────────────────────────────────┐");
        System.out.println("│ 类型     │ 策略                                   │");
        System.out.println("├──────────┼────────────────────────────────────────┤");
        System.out.println("│ Type 0   │ 保留原始值，不做任何变更               │");
        System.out.println("│ Type 1   │ 直接覆盖，不保留历史                   │");
        System.out.println("│ Type 2   │ 新增行，保留完整历史                   │");
        System.out.println("│ Type 3   │ 新增列，保留有限历史（当前+前一版本）│");
        System.out.println("│ Type 4   │ 历史表分离                            │");
        System.out.println("│ Type 6   │ Type 1+2+3 混合                       │");
        System.out.println("└──────────┴────────────────────────────────────────┘");
    }

    /**
     * ========================================
     * 第二部分：SCD Type 1 - 覆盖
     * ========================================
     */
    public static void explainScdType1() {
        System.out.println("=== SCD Type 1 - 直接覆盖 ===");
        System.out.println();

        System.out.println("【策略】");
        System.out.println("  直接用新值覆盖旧值");
        System.out.println("  不保留任何历史");
        System.out.println();

        System.out.println("【适用场景】");
        System.out.println("  • 数据修正（错误数据更正）");
        System.out.println("  • 不需要历史分析的属性");
        System.out.println("  • 如：用户昵称变更");
        System.out.println();

        System.out.println("【示例】");
        System.out.println("```sql");
        System.out.println("-- 用户地址变更：北京 → 上海");
        System.out.println();
        System.out.println("-- 变更前");
        System.out.println("-- user_id | name   | city");
        System.out.println("-- 1001    | 张三   | 北京");
        System.out.println();
        System.out.println("-- Type 1: 直接 UPDATE");
        System.out.println("UPDATE dim_user SET city = '上海' WHERE user_id = 1001;");
        System.out.println();
        System.out.println("-- 变更后");
        System.out.println("-- user_id | name   | city");
        System.out.println("-- 1001    | 张三   | 上海  ← 历史丢失");
        System.out.println("```");
        System.out.println();

        System.out.println("【优点】简单、节省空间");
        System.out.println("【缺点】无法追溯历史");
    }

    /**
     * ========================================
     * 第三部分：SCD Type 2 - 新增行
     * ========================================
     */
    public static void explainScdType2() {
        System.out.println("=== SCD Type 2 - 新增行保留历史 ===");
        System.out.println();

        System.out.println("【策略】");
        System.out.println("  属性变化时新增一行");
        System.out.println("  使用代理键区分不同版本");
        System.out.println("  标记有效期");
        System.out.println();

        System.out.println("【关键字段】");
        System.out.println("  • surrogate_key: 代理键");
        System.out.println("  • natural_key: 自然键（业务主键）");
        System.out.println("  • effective_date: 生效日期");
        System.out.println("  • expiration_date: 失效日期");
        System.out.println("  • is_current: 是否当前有效");
        System.out.println();

        System.out.println("【示例】");
        System.out.println("```sql");
        System.out.println("-- Type 2 维度表结构");
        System.out.println("CREATE TABLE dim_user (");
        System.out.println("    user_key         BIGINT,      -- 代理键");
        System.out.println("    user_id          BIGINT,      -- 自然键");
        System.out.println("    name             STRING,");
        System.out.println("    city             STRING,");
        System.out.println("    effective_date   DATE,        -- 生效日期");
        System.out.println("    expiration_date  DATE,        -- 失效日期");
        System.out.println("    is_current       BOOLEAN      -- 是否当前");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 变更前数据");
        System.out.println("-- user_key | user_id | name | city | eff_date   | exp_date   | is_current");
        System.out.println("-- 1        | 1001    | 张三 | 北京 | 2024-01-01 | 9999-12-31 | true");
        System.out.println();
        System.out.println("-- 用户 1001 于 2024-06-01 从北京迁移到上海");
        System.out.println();
        System.out.println("-- Step 1: 关闭旧记录");
        System.out.println("UPDATE dim_user ");
        System.out.println("SET expiration_date = '2024-05-31', is_current = false");
        System.out.println("WHERE user_id = 1001 AND is_current = true;");
        System.out.println();
        System.out.println("-- Step 2: 新增记录");
        System.out.println("INSERT INTO dim_user VALUES");
        System.out.println("(2, 1001, '张三', '上海', '2024-06-01', '9999-12-31', true);");
        System.out.println();
        System.out.println("-- 变更后数据");
        System.out.println("-- user_key | user_id | name | city | eff_date   | exp_date   | is_current");
        System.out.println("-- 1        | 1001    | 张三 | 北京 | 2024-01-01 | 2024-05-31 | false");
        System.out.println("-- 2        | 1001    | 张三 | 上海 | 2024-06-01 | 9999-12-31 | true");
        System.out.println("```");
        System.out.println();

        System.out.println("【查询示例】");
        System.out.println("```sql");
        System.out.println("-- 获取当前有效维度");
        System.out.println("SELECT * FROM dim_user WHERE is_current = true;");
        System.out.println();
        System.out.println("-- 获取某时间点的维度快照");
        System.out.println("SELECT * FROM dim_user ");
        System.out.println("WHERE '2024-03-15' BETWEEN effective_date AND expiration_date;");
        System.out.println("```");
        System.out.println();

        System.out.println("【优点】完整保留历史，支持时间旅行分析");
        System.out.println("【缺点】数据量增大，查询需注意过滤");
    }

    /**
     * ========================================
     * 第四部分：SCD Type 3 - 新增列
     * ========================================
     */
    public static void explainScdType3() {
        System.out.println("=== SCD Type 3 - 新增列 ===");
        System.out.println();

        System.out.println("【策略】");
        System.out.println("  增加列存储前一个版本");
        System.out.println("  只保留当前和前一个版本");
        System.out.println();

        System.out.println("【示例】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE dim_user (");
        System.out.println("    user_id        BIGINT,");
        System.out.println("    name           STRING,");
        System.out.println("    current_city   STRING,    -- 当前城市");
        System.out.println("    previous_city  STRING,    -- 前一个城市");
        System.out.println("    city_changed   DATE       -- 变更日期");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 变更前");
        System.out.println("-- user_id | name | current_city | previous_city | city_changed");
        System.out.println("-- 1001    | 张三 | 北京         | NULL          | NULL");
        System.out.println();
        System.out.println("-- 变更后");
        System.out.println("-- user_id | name | current_city | previous_city | city_changed");
        System.out.println("-- 1001    | 张三 | 上海         | 北京          | 2024-06-01");
        System.out.println("```");
        System.out.println();

        System.out.println("【优点】结构简单，行数不增加");
        System.out.println("【缺点】只能保留一次历史");
    }

    /**
     * ========================================
     * 第五部分：SCD 实践建议
     * ========================================
     */
    public static void explainScdPractice() {
        System.out.println("=== SCD 实践建议 ===");
        System.out.println();

        System.out.println("【如何选择】");
        System.out.println("┌────────────────────────┬─────────────────────────────┐");
        System.out.println("│ 场景                   │ 推荐类型                    │");
        System.out.println("├────────────────────────┼─────────────────────────────┤");
        System.out.println("│ 数据修正              │ Type 1                      │");
        System.out.println("│ 需要完整历史           │ Type 2                      │");
        System.out.println("│ 只关心当前和前一版本   │ Type 3                      │");
        System.out.println("│ 混合场景               │ 同表不同字段用不同策略      │");
        System.out.println("└────────────────────────┴─────────────────────────────┘");
        System.out.println();

        System.out.println("【Type 2 最佳实践】");
        System.out.println("  1. 使用 9999-12-31 作为当前记录的失效日期");
        System.out.println("  2. 添加 is_current 标志简化查询");
        System.out.println("  3. 生效日期精确到秒（处理同日多次变更）");
        System.out.println("  4. 事实表关联使用代理键而非自然键");
        System.out.println();

        System.out.println("【拉链表】");
        System.out.println("  Type 2 在 Hive 中常称为\"拉链表\"");
        System.out.println("  可以高效存储历史变化数据");
        System.out.println("```sql");
        System.out.println("-- 拉链表查询特定日期快照");
        System.out.println("SELECT * FROM dim_user_zipper");
        System.out.println("WHERE start_date <= '2024-03-15' ");
        System.out.println("  AND end_date > '2024-03-15';");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: 缓慢变化维度                          ║");
        System.out.println("║          处理维度历史变化的技术                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainScdOverview();
        System.out.println();

        explainScdType1();
        System.out.println();

        explainScdType2();
        System.out.println();

        explainScdType3();
        System.out.println();

        explainScdPractice();
    }
}
