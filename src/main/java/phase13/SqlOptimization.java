package phase13;

/**
 * Phase 13: SQL 性能优化
 * 
 * 本课程涵盖：
 * 1. 执行计划分析
 * 2. 索引优化
 * 3. 慢查询优化
 * 4. 分页优化
 */
public class SqlOptimization {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: SQL 性能优化");
        System.out.println("=".repeat(60));

        explainAnalysis();
        indexOptimization();
        slowQueryOptimization();
        paginationOptimization();
        sqlBestPractices();
    }

    private static void explainAnalysis() {
        System.out.println("\n【1. EXPLAIN 执行计划分析】");
        System.out.println("-".repeat(50));

        String explain = """

                ═══════════════════════════════════════════════════════════
                                 EXPLAIN 基本用法
                ═══════════════════════════════════════════════════════════

                EXPLAIN SELECT * FROM orders WHERE user_id = 100;

                ┌────┬─────────────┬────────┬──────┬─────────┬──────┬──────────┐
                │ id │ select_type │ table  │ type │ key     │ rows │ Extra    │
                ├────┼─────────────┼────────┼──────┼─────────┼──────┼──────────┤
                │ 1  │ SIMPLE      │ orders │ ref  │ idx_uid │ 50   │ Using .. │
                └────┴─────────────┴────────┴──────┴─────────┴──────┴──────────┘


                ═══════════════════════════════════════════════════════════
                                 type 字段详解（从好到差）
                ═══════════════════════════════════════════════════════════

                system  │ 表只有一行数据
                const   │ 使用主键或唯一索引查找，最多一行
                eq_ref  │ JOIN 使用主键/唯一索引，每次只匹配一行
                ref     │ 使用非唯一索引查找 ✅ 常见好情况
                range   │ 索引范围扫描 (>, <, BETWEEN, IN)
                index   │ 全索引扫描 ⚠️ 需要关注
                ALL     │ 全表扫描 ❌ 必须优化！

                ⭐ 目标：避免 ALL，争取 ref 或更好


                ═══════════════════════════════════════════════════════════
                                 Extra 字段关注点
                ═══════════════════════════════════════════════════════════

                ✅ Using index          - 覆盖索引，效率最高
                ✅ Using where          - 使用 WHERE 条件过滤
                ⚠️ Using filesort       - 需要额外排序，尽量避免
                ⚠️ Using temporary      - 使用临时表，尽量避免
                ❌ Using join buffer    - JOIN 没有使用索引
                """;
        System.out.println(explain);
    }

    private static void indexOptimization() {
        System.out.println("\n【2. 索引优化原则】");
        System.out.println("-".repeat(50));

        String index = """

                ═══════════════════════════════════════════════════════════
                                  最左前缀原则
                ═══════════════════════════════════════════════════════════

                联合索引: INDEX idx_abc (a, b, c)

                ✅ 可以使用索引:
                WHERE a = 1
                WHERE a = 1 AND b = 2
                WHERE a = 1 AND b = 2 AND c = 3
                WHERE a = 1 AND b > 2

                ❌ 无法使用索引:
                WHERE b = 2                  -- 缺少最左列 a
                WHERE a = 1 AND c = 3        -- 跳过了列 b
                WHERE b = 2 AND c = 3        -- 缺少最左列 a

                ⚠️ 范围条件后的列无法使用索引:
                WHERE a = 1 AND b > 2 AND c = 3
                               ↑ 范围    ↑ c 无法使用索引


                ═══════════════════════════════════════════════════════════
                                  索引失效场景
                ═══════════════════════════════════════════════════════════

                1. 对索引列使用函数
                ─────────────────────────────────────────────────────────
                ❌ WHERE DATE(create_time) = '2024-01-01'
                ✅ WHERE create_time >= '2024-01-01'
                     AND create_time < '2024-01-02'

                2. 隐式类型转换
                ─────────────────────────────────────────────────────────
                ❌ WHERE phone = 13800138000   -- phone 是 VARCHAR
                ✅ WHERE phone = '13800138000'

                3. LIKE 以通配符开头
                ─────────────────────────────────────────────────────────
                ❌ WHERE name LIKE '%张'
                ✅ WHERE name LIKE '张%'

                4. OR 条件（部分情况）
                ─────────────────────────────────────────────────────────
                ❌ WHERE id = 1 OR name = '张三'  -- name 无索引
                ✅ 确保 OR 两边的列都有索引

                5. NOT IN / NOT EXISTS / !=
                ─────────────────────────────────────────────────────────
                ❌ WHERE status != 1    -- 可能不走索引
                ✅ 使用 IN 列出需要的值


                ═══════════════════════════════════════════════════════════
                                  覆盖索引
                ═══════════════════════════════════════════════════════════

                查询的所有列都在索引中，不需要回表查询

                INDEX idx_uid_status (user_id, status)

                ✅ 覆盖索引:
                SELECT user_id, status FROM orders WHERE user_id = 100

                ❌ 需要回表:
                SELECT * FROM orders WHERE user_id = 100

                💡 查看执行计划 Extra 列是否显示 "Using index"
                """;
        System.out.println(index);
    }

    private static void slowQueryOptimization() {
        System.out.println("\n【3. 慢查询优化】");
        System.out.println("-".repeat(50));

        String slowQuery = """

                ═══════════════════════════════════════════════════════════
                                 开启慢查询日志
                ═══════════════════════════════════════════════════════════

                -- MySQL 配置
                SET GLOBAL slow_query_log = 'ON';
                SET GLOBAL long_query_time = 1;  -- 超过1秒记录
                SET GLOBAL slow_query_log_file = '/var/log/mysql/slow.log';

                -- 查看状态
                SHOW VARIABLES LIKE 'slow_query%';
                SHOW VARIABLES LIKE 'long_query_time';


                ═══════════════════════════════════════════════════════════
                                 慢查询分析工具
                ═══════════════════════════════════════════════════════════

                # mysqldumpslow（MySQL 自带）
                mysqldumpslow -s t -t 10 /var/log/mysql/slow.log

                参数说明：
                -s t    按查询时间排序
                -t 10   显示前10条

                # pt-query-digest（更强大）
                pt-query-digest /var/log/mysql/slow.log


                ═══════════════════════════════════════════════════════════
                                 常见慢查询原因
                ═══════════════════════════════════════════════════════════

                1. 没有使用索引（type = ALL）
                   → 添加合适的索引

                2. 返回数据量过大
                   → 添加 LIMIT，分页查询

                3. JOIN 表过多
                   → 减少 JOIN，考虑冗余

                4. 子查询效率低
                   → 改写为 JOIN

                5. ORDER BY 没有利用索引
                   → 调整索引覆盖排序字段
                """;
        System.out.println(slowQuery);
    }

    private static void paginationOptimization() {
        System.out.println("\n【4. 分页优化】");
        System.out.println("-".repeat(50));

        String pagination = """

                ═══════════════════════════════════════════════════════════
                                 深分页问题
                ═══════════════════════════════════════════════════════════

                ❌ 问题 SQL（偏移量大时很慢）：
                SELECT * FROM orders LIMIT 1000000, 10;

                原因：MySQL 需要扫描 1000010 行，丢弃前 1000000 行


                ═══════════════════════════════════════════════════════════
                                 优化方案
                ═══════════════════════════════════════════════════════════

                方案1: 使用 ID 偏移（推荐）
                ─────────────────────────────────────────────────────────
                -- 记住上一页最后一条的 ID
                SELECT * FROM orders
                WHERE id > 1000000  -- 上一页最后一条 ID
                ORDER BY id
                LIMIT 10;

                ✅ 利用主键索引，效率高


                方案2: 延迟关联
                ─────────────────────────────────────────────────────────
                SELECT o.* FROM orders o
                INNER JOIN (
                    SELECT id FROM orders
                    ORDER BY id
                    LIMIT 1000000, 10
                ) tmp ON o.id = tmp.id;

                ✅ 子查询只查 ID（覆盖索引），再回表取数据


                方案3: 业务限制
                ─────────────────────────────────────────────────────────
                • 限制用户最多翻到第 100 页
                • 使用搜索代替深翻页
                • 只提供"上一页/下一页"，不提供跳页
                """;
        System.out.println(pagination);
    }

    private static void sqlBestPractices() {
        System.out.println("\n【5. SQL 优化最佳实践】");
        System.out.println("-".repeat(50));

        String practices = """

                ═══════════════════════════════════════════════════════════
                                 SQL 优化检查清单
                ═══════════════════════════════════════════════════════════

                □ 避免 SELECT *，只查需要的列
                □ 使用 EXPLAIN 检查执行计划
                □ 确保 WHERE 条件使用索引
                □ 避免在索引列上使用函数
                □ 注意隐式类型转换
                □ 大批量操作要分批执行
                □ 使用 LIMIT 限制返回行数
                □ JOIN 的关联字段要有索引
                □ 避免 N+1 查询问题
                □ 考虑使用覆盖索引


                ═══════════════════════════════════════════════════════════
                                 N+1 问题
                ═══════════════════════════════════════════════════════════

                ❌ 错误示例:
                List<Order> orders = orderDao.findAll();  // 1 条 SQL
                for (Order order : orders) {
                    User user = userDao.findById(order.getUserId()); // N 条 SQL
                }

                ✅ 正确做法:
                -- 使用 JOIN
                SELECT o.*, u.name FROM orders o
                LEFT JOIN users u ON o.user_id = u.id;

                -- 或批量查询
                SELECT * FROM users WHERE id IN (1, 2, 3, ...);


                ═══════════════════════════════════════════════════════════
                                 索引设计建议
                ═══════════════════════════════════════════════════════════

                1. 索引列选择：
                   • 高频查询的 WHERE 条件列
                   • JOIN 的关联列
                   • ORDER BY / GROUP BY 的列

                2. 索引数量控制：
                   • 单表索引数量 <= 5
                   • 联合索引字段数 <= 5

                3. 索引顺序：
                   • 等值查询列放前面
                   • 范围查询列放后面
                   • 区分度高的列放前面
                """;
        System.out.println(practices);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ SQL 优化课程完成！下一课: CacheStrategy.java");
        System.out.println("=".repeat(60));
    }
}
