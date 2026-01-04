package phase24;

/**
 * 维度建模
 * 
 * 维度建模是数据仓库设计的核心方法论，由 Ralph Kimball 提出。
 * 围绕业务过程设计事实表和维度表。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class DwModeling {

    /**
     * ========================================
     * 第一部分：维度建模概述
     * ========================================
     */
    public static void explainDimensionalModeling() {
        System.out.println("=== 维度建模概述 ===");
        System.out.println();

        System.out.println("【什么是维度建模】");
        System.out.println("  一种面向分析的数据建模方法");
        System.out.println("  围绕业务过程组织数据");
        System.out.println("  强调可读性和查询性能");
        System.out.println();

        System.out.println("【维度建模 vs E-R 建模】");
        System.out.println("┌──────────────┬───────────────────┬───────────────────┐");
        System.out.println("│              │    E-R 建模       │    维度建模       │");
        System.out.println("├──────────────┼───────────────────┼───────────────────┤");
        System.out.println("│ 设计目标     │ 消除数据冗余      │ 优化查询性能      │");
        System.out.println("│ 范式要求     │ 3NF/BCNF          │ 反范式            │");
        System.out.println("│ 表关系       │ 多对多复杂        │ 星型/雪花简洁     │");
        System.out.println("│ 适用场景     │ OLTP 事务处理     │ OLAP 分析         │");
        System.out.println("│ 查询复杂度   │ 多表关联复杂      │ 简单直观          │");
        System.out.println("└──────────────┴───────────────────┴───────────────────┘");
        System.out.println();

        System.out.println("【Kimball 四步法】");
        System.out.println("  1. 选择业务过程 (Business Process)");
        System.out.println("  2. 声明粒度 (Grain)");
        System.out.println("  3. 确认维度 (Dimensions)");
        System.out.println("  4. 确认事实 (Facts)");
    }

    /**
     * ========================================
     * 第二部分：事实表
     * ========================================
     */
    public static void explainFactTable() {
        System.out.println("=== 事实表 (Fact Table) ===");
        System.out.println();

        System.out.println("【事实表定义】");
        System.out.println("  存储业务过程的度量值（数值型数据）");
        System.out.println("  一行代表一个业务事件");
        System.out.println("  包含外键引用维度表");
        System.out.println();

        System.out.println("【事实表类型】");
        System.out.println();

        System.out.println("1. 事务事实表 (Transaction Fact)");
        System.out.println("   • 记录单次业务事件");
        System.out.println("   • 粒度最细，数据量最大");
        System.out.println("   • 示例：订单明细表");
        System.out.println("```sql");
        System.out.println("CREATE TABLE fact_order (");
        System.out.println("    order_id       BIGINT,");
        System.out.println("    order_time     TIMESTAMP,");
        System.out.println("    user_key       BIGINT,     -- 维度外键");
        System.out.println("    product_key    BIGINT,     -- 维度外键");
        System.out.println("    store_key      BIGINT,     -- 维度外键");
        System.out.println("    order_amount   DECIMAL,    -- 事实/度量");
        System.out.println("    quantity       INT         -- 事实/度量");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("2. 周期快照事实表 (Periodic Snapshot)");
        System.out.println("   • 定期记录状态快照");
        System.out.println("   • 每个时间周期一行");
        System.out.println("   • 示例：每日库存快照");
        System.out.println("```sql");
        System.out.println("CREATE TABLE fact_inventory_daily (");
        System.out.println("    snapshot_date  DATE,");
        System.out.println("    product_key    BIGINT,");
        System.out.println("    warehouse_key  BIGINT,");
        System.out.println("    quantity_on_hand INT,");
        System.out.println("    quantity_sold    INT");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("3. 累积快照事实表 (Accumulating Snapshot)");
        System.out.println("   • 记录业务流程的里程碑");
        System.out.println("   • 随流程推进更新");
        System.out.println("   • 示例：订单履约表");
        System.out.println("```sql");
        System.out.println("CREATE TABLE fact_order_fulfillment (");
        System.out.println("    order_key         BIGINT,");
        System.out.println("    order_date        DATE,     -- 下单时间");
        System.out.println("    payment_date      DATE,     -- 支付时间");
        System.out.println("    ship_date         DATE,     -- 发货时间");
        System.out.println("    delivery_date     DATE,     -- 签收时间");
        System.out.println("    days_to_payment   INT,      -- 计算指标");
        System.out.println("    days_to_delivery  INT");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：维度表
     * ========================================
     */
    public static void explainDimensionTable() {
        System.out.println("=== 维度表 (Dimension Table) ===");
        System.out.println();

        System.out.println("【维度表定义】");
        System.out.println("  描述业务过程的上下文");
        System.out.println("  包含文本属性，支持过滤和分组");
        System.out.println("  通常行数少、列数多");
        System.out.println();

        System.out.println("【常见维度】");
        System.out.println("  • 时间维度 (dim_date)");
        System.out.println("  • 地区维度 (dim_location)");
        System.out.println("  • 用户维度 (dim_user)");
        System.out.println("  • 产品维度 (dim_product)");
        System.out.println("  • 渠道维度 (dim_channel)");
        System.out.println();

        System.out.println("【时间维度示例】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE dim_date (");
        System.out.println("    date_key       INT PRIMARY KEY,  -- 20240101 格式");
        System.out.println("    full_date      DATE,");
        System.out.println("    year           INT,");
        System.out.println("    quarter        INT,");
        System.out.println("    month          INT,");
        System.out.println("    month_name     VARCHAR(20),");
        System.out.println("    week_of_year   INT,");
        System.out.println("    day_of_week    INT,");
        System.out.println("    day_name       VARCHAR(20),");
        System.out.println("    is_weekend     BOOLEAN,");
        System.out.println("    is_holiday     BOOLEAN,");
        System.out.println("    fiscal_year    INT,");
        System.out.println("    fiscal_quarter INT");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【产品维度示例】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE dim_product (");
        System.out.println("    product_key     BIGINT PRIMARY KEY,");
        System.out.println("    product_id      VARCHAR(50),  -- 自然键");
        System.out.println("    product_name    VARCHAR(200),");
        System.out.println("    category_l1     VARCHAR(50),  -- 一级分类");
        System.out.println("    category_l2     VARCHAR(50),  -- 二级分类");
        System.out.println("    category_l3     VARCHAR(50),  -- 三级分类");
        System.out.println("    brand           VARCHAR(100),");
        System.out.println("    supplier        VARCHAR(100),");
        System.out.println("    unit_price      DECIMAL(10,2),");
        System.out.println("    effective_date  DATE,");
        System.out.println("    expiration_date DATE");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：星型模型与雪花模型
     * ========================================
     */
    public static void explainStarAndSnowflake() {
        System.out.println("=== 星型模型与雪花模型 ===");
        System.out.println();

        System.out.println("【星型模型 (Star Schema)】");
        System.out.println("  事实表在中心，维度表环绕");
        System.out.println("  维度表不再规范化");
        System.out.println();
        System.out.println("```");
        System.out.println("              dim_date");
        System.out.println("                  │");
        System.out.println("  dim_product ─── fact_sales ─── dim_store");
        System.out.println("                  │");
        System.out.println("              dim_customer");
        System.out.println("```");
        System.out.println();
        System.out.println("  优点：查询简单、性能好");
        System.out.println("  缺点：维度表有数据冗余");
        System.out.println();

        System.out.println("【雪花模型 (Snowflake Schema)】");
        System.out.println("  维度表进一步规范化");
        System.out.println("  维度表可以有子维度表");
        System.out.println();
        System.out.println("```");
        System.out.println("              dim_date");
        System.out.println("                  │");
        System.out.println("  dim_category    │");
        System.out.println("       │          │");
        System.out.println("  dim_product ─── fact_sales ─── dim_store ─── dim_city");
        System.out.println("                  │                              │");
        System.out.println("              dim_customer                   dim_region");
        System.out.println("```");
        System.out.println();
        System.out.println("  优点：节省存储空间");
        System.out.println("  缺点：查询复杂、性能略差");
        System.out.println();

        System.out.println("【选择建议】");
        System.out.println("  • 推荐使用星型模型（简单高效）");
        System.out.println("  • 维度表很大且变化频繁时考虑雪花");
        System.out.println("  • 现代列式存储已减少雪花的存储优势");
    }

    /**
     * ========================================
     * 第五部分：代理键与自然键
     * ========================================
     */
    public static void explainKeys() {
        System.out.println("=== 代理键与自然键 ===");
        System.out.println();

        System.out.println("【自然键 (Natural Key)】");
        System.out.println("  • 业务系统的原始标识符");
        System.out.println("  • 如：身份证号、订单号、产品SKU");
        System.out.println();

        System.out.println("【代理键 (Surrogate Key)】");
        System.out.println("  • 数仓生成的无业务含义的标识符");
        System.out.println("  • 通常是自增整数");
        System.out.println();

        System.out.println("【为什么使用代理键】");
        System.out.println("  1. 自然键可能变化（如产品重编码）");
        System.out.println("  2. 自然键可能复合（多列组成）");
        System.out.println("  3. 自然键可能很长（影响 Join 性能）");
        System.out.println("  4. 支持缓慢变化维度（SCD Type 2）");
        System.out.println();

        System.out.println("【示例】");
        System.out.println("```sql");
        System.out.println("-- 维度表使用代理键");
        System.out.println("CREATE TABLE dim_product (");
        System.out.println("    product_key   BIGINT PRIMARY KEY,  -- 代理键");
        System.out.println("    product_id    VARCHAR(50),          -- 自然键");
        System.out.println("    product_name  VARCHAR(200),");
        System.out.println("    ...");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 事实表引用代理键");
        System.out.println("CREATE TABLE fact_sales (");
        System.out.println("    product_key   BIGINT REFERENCES dim_product(product_key),");
        System.out.println("    ...");
        System.out.println(");");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: 维度建模                              ║");
        System.out.println("║          数据仓库设计的核心方法论                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainDimensionalModeling();
        System.out.println();

        explainFactTable();
        System.out.println();

        explainDimensionTable();
        System.out.println();

        explainStarAndSnowflake();
        System.out.println();

        explainKeys();
    }
}
