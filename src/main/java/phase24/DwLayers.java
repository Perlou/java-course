package phase24;

/**
 * 数仓分层
 * 
 * 数仓分层是企业级数据仓库的标准架构，将数据处理分为多个层次，
 * 各层职责清晰，便于管理和复用。
 * 
 * @author Java Course
 * @since Phase 24
 */
public class DwLayers {

    /**
     * ========================================
     * 第一部分：分层架构概述
     * ========================================
     */
    public static void explainLayeredArchitecture() {
        System.out.println("=== 数仓分层架构 ===");
        System.out.println();

        System.out.println("【为什么要分层】");
        System.out.println("  1. 清晰的数据流转路径");
        System.out.println("  2. 降低数据加工复杂度");
        System.out.println("  3. 便于数据复用");
        System.out.println("  4. 隔离原始数据与加工数据");
        System.out.println("  5. 方便问题定位与数据回溯");
        System.out.println();

        System.out.println("【标准四层架构】");
        System.out.println("```");
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    应用层 (ADS)                            │");
        System.out.println("│        Application Data Service / Data Mart                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                    服务层 (DWS)                            │");
        System.out.println("│              Data Warehouse Service                        │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                    明细层 (DWD)                            │");
        System.out.println("│              Data Warehouse Detail                         │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                    原始层 (ODS)                            │");
        System.out.println("│             Operational Data Store                         │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("                          ↑");
        System.out.println("              ┌──────────┴──────────┐");
        System.out.println("              │    数据源 (Source)   │");
        System.out.println("              │  DB, Log, File, API │");
        System.out.println("              └─────────────────────┘");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：ODS 原始数据层
     * ========================================
     */
    public static void explainOdsLayer() {
        System.out.println("=== ODS - 原始数据层 ===");
        System.out.println();

        System.out.println("【Operational Data Store】");
        System.out.println("  原始数据的落地层，保持源数据原貌");
        System.out.println();

        System.out.println("【特点】");
        System.out.println("  • 数据不做任何加工");
        System.out.println("  • 保留原始字段和结构");
        System.out.println("  • 增量或全量同步");
        System.out.println("  • 保留历史数据，支持回溯");
        System.out.println();

        System.out.println("【命名规范】");
        System.out.println("  ods_{来源}_{业务表名}");
        System.out.println("  示例：ods_mysql_orders, ods_kafka_user_behavior");
        System.out.println();

        System.out.println("【表结构示例】");
        System.out.println("```sql");
        System.out.println("-- ODS 层订单表");
        System.out.println("CREATE TABLE ods_mysql_orders (");
        System.out.println("    -- 原始字段，保持不变");
        System.out.println("    id              BIGINT,");
        System.out.println("    order_no        VARCHAR(50),");
        System.out.println("    user_id         BIGINT,");
        System.out.println("    product_id      BIGINT,");
        System.out.println("    amount          DECIMAL(10,2),");
        System.out.println("    status          INT,");
        System.out.println("    create_time     TIMESTAMP,");
        System.out.println("    update_time     TIMESTAMP,");
        System.out.println("    ");
        System.out.println("    -- 元数据字段");
        System.out.println("    dt              STRING,        -- 分区字段（日期）");
        System.out.println("    etl_time        TIMESTAMP,     -- ETL 时间");
        System.out.println("    source_table    STRING         -- 来源表");
        System.out.println(")");
        System.out.println("PARTITIONED BY (dt STRING)");
        System.out.println("STORED AS ORC;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：DWD 明细数据层
     * ========================================
     */
    public static void explainDwdLayer() {
        System.out.println("=== DWD - 明细数据层 ===");
        System.out.println();

        System.out.println("【Data Warehouse Detail】");
        System.out.println("  清洗、规范化后的明细数据");
        System.out.println();

        System.out.println("【处理内容】");
        System.out.println("  • 数据清洗（去重、空值、异常值）");
        System.out.println("  • 数据标准化（编码统一、格式统一）");
        System.out.println("  • 维度退化（关联维度表）");
        System.out.println("  • 数据类型转换");
        System.out.println();

        System.out.println("【命名规范】");
        System.out.println("  dwd_{业务域}_{数据域}_{粒度}");
        System.out.println("  示例：dwd_trade_order_detail, dwd_user_behavior_event");
        System.out.println();

        System.out.println("【表结构示例】");
        System.out.println("```sql");
        System.out.println("-- DWD 层订单明细表");
        System.out.println("CREATE TABLE dwd_trade_order_detail (");
        System.out.println("    -- 业务字段");
        System.out.println("    order_id           BIGINT,");
        System.out.println("    order_no           STRING,");
        System.out.println("    ");
        System.out.println("    -- 维度退化（直接关联维度信息）");
        System.out.println("    user_id            BIGINT,");
        System.out.println("    user_name          STRING,");
        System.out.println("    user_level         STRING,");
        System.out.println("    ");
        System.out.println("    product_id         BIGINT,");
        System.out.println("    product_name       STRING,");
        System.out.println("    category_id        BIGINT,");
        System.out.println("    category_name      STRING,");
        System.out.println("    brand_name         STRING,");
        System.out.println("    ");
        System.out.println("    -- 度量字段");
        System.out.println("    order_amount       DECIMAL(10,2),");
        System.out.println("    discount_amount    DECIMAL(10,2),");
        System.out.println("    pay_amount         DECIMAL(10,2),");
        System.out.println("    quantity           INT,");
        System.out.println("    ");
        System.out.println("    -- 标准化后的状态");
        System.out.println("    order_status       STRING,  -- 标准化编码");
        System.out.println("    order_status_name  STRING,  -- 状态名称");
        System.out.println("    ");
        System.out.println("    -- 时间字段");
        System.out.println("    order_time         TIMESTAMP,");
        System.out.println("    pay_time           TIMESTAMP,");
        System.out.println("    ");
        System.out.println("    dt                 STRING");
        System.out.println(")");
        System.out.println("PARTITIONED BY (dt STRING)");
        System.out.println("STORED AS ORC;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：DWS 服务数据层
     * ========================================
     */
    public static void explainDwsLayer() {
        System.out.println("=== DWS - 服务数据层 ===");
        System.out.println();

        System.out.println("【Data Warehouse Service】");
        System.out.println("  轻度汇总的宽表，面向主题域");
        System.out.println();

        System.out.println("【特点】");
        System.out.println("  • 按主题域组织（用户、交易、商品等）");
        System.out.println("  • 轻度聚合（日/小时粒度）");
        System.out.println("  • 宽表设计，字段丰富");
        System.out.println("  • 便于上层应用快速查询");
        System.out.println();

        System.out.println("【命名规范】");
        System.out.println("  dws_{业务域}_{主题}_{粒度}_{周期}");
        System.out.println("  示例：dws_trade_user_order_1d, dws_user_behavior_1h");
        System.out.println();

        System.out.println("【表结构示例】");
        System.out.println("```sql");
        System.out.println("-- DWS 层用户交易日汇总表");
        System.out.println("CREATE TABLE dws_trade_user_order_1d (");
        System.out.println("    -- 维度");
        System.out.println("    user_id              BIGINT,");
        System.out.println("    user_name            STRING,");
        System.out.println("    user_level           STRING,");
        System.out.println("    ");
        System.out.println("    -- 当日指标");
        System.out.println("    order_count_1d       BIGINT,    -- 当日订单数");
        System.out.println("    order_amount_1d      DECIMAL,   -- 当日订单金额");
        System.out.println("    pay_count_1d         BIGINT,    -- 当日支付订单数");
        System.out.println("    pay_amount_1d        DECIMAL,   -- 当日支付金额");
        System.out.println("    ");
        System.out.println("    -- 累计指标");
        System.out.println("    order_count_td       BIGINT,    -- 累计订单数");
        System.out.println("    order_amount_td      DECIMAL,   -- 累计订单金额");
        System.out.println("    first_order_time     TIMESTAMP, -- 首单时间");
        System.out.println("    last_order_time      TIMESTAMP, -- 末单时间");
        System.out.println("    ");
        System.out.println("    dt                   STRING");
        System.out.println(")");
        System.out.println("PARTITIONED BY (dt STRING)");
        System.out.println("STORED AS ORC;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：ADS 应用数据层
     * ========================================
     */
    public static void explainAdsLayer() {
        System.out.println("=== ADS - 应用数据层 ===");
        System.out.println();

        System.out.println("【Application Data Service / Data Mart】");
        System.out.println("  面向具体应用场景的数据");
        System.out.println();

        System.out.println("【特点】");
        System.out.println("  • 面向具体业务应用");
        System.out.println("  • 高度汇总");
        System.out.println("  • 直接供报表/接口使用");
        System.out.println("  • 可能导出到 MySQL、ES 等");
        System.out.println();

        System.out.println("【命名规范】");
        System.out.println("  ads_{应用}_{主题}");
        System.out.println("  示例：ads_report_sales_summary, ads_dashboard_user_portrait");
        System.out.println();

        System.out.println("【表结构示例】");
        System.out.println("```sql");
        System.out.println("-- ADS 层销售报表");
        System.out.println("CREATE TABLE ads_report_sales_summary (");
        System.out.println("    stat_date            DATE,");
        System.out.println("    ");
        System.out.println("    -- GMV 相关");
        System.out.println("    gmv                  DECIMAL,");
        System.out.println("    gmv_wow              DECIMAL,   -- 周同比");
        System.out.println("    gmv_mom              DECIMAL,   -- 月环比");
        System.out.println("    ");
        System.out.println("    -- 订单相关");
        System.out.println("    order_count          BIGINT,");
        System.out.println("    pay_order_count      BIGINT,");
        System.out.println("    pay_rate             DECIMAL,   -- 支付转化率");
        System.out.println("    ");
        System.out.println("    -- 用户相关");
        System.out.println("    uv                   BIGINT,    -- 访客数");
        System.out.println("    new_user_count       BIGINT,    -- 新用户数");
        System.out.println("    pay_user_count       BIGINT,    -- 付费用户数");
        System.out.println("    arpu                 DECIMAL    -- ARPU");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：维度表层 (DIM)
     * ========================================
     */
    public static void explainDimLayer() {
        System.out.println("=== DIM - 维度表层 ===");
        System.out.println();

        System.out.println("【公共维度表】");
        System.out.println("  独立于分层架构，供各层公用");
        System.out.println();

        System.out.println("【命名规范】");
        System.out.println("  dim_{维度主题}");
        System.out.println("  示例：dim_user, dim_product, dim_date, dim_region");
        System.out.println();

        System.out.println("【维度类型】");
        System.out.println("  • 高基数维度：用户、商品（行数多）");
        System.out.println("  • 低基数维度：地区、渠道、状态（行数少）");
        System.out.println("  • 日期维度：预生成的时间维度表");
        System.out.println();

        System.out.println("【数据流转】");
        System.out.println("```");
        System.out.println("源系统 → ODS → DIM（维度表）");
        System.out.println("                   ↓");
        System.out.println("         DWD（关联维度） → DWS → ADS");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 24: 数仓分层                              ║");
        System.out.println("║          企业级数仓标准架构                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainLayeredArchitecture();
        System.out.println();

        explainOdsLayer();
        System.out.println();

        explainDwdLayer();
        System.out.println();

        explainDwsLayer();
        System.out.println();

        explainAdsLayer();
        System.out.println();

        explainDimLayer();
    }
}
