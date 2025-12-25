package phase11;

/**
 * Phase 11 - Lesson 9: SQL 优化实战
 * 
 * 🎯 学习目标:
 * 1. 掌握 EXPLAIN 执行计划分析
 * 2. 学会慢查询分析和优化
 * 3. 了解常见 SQL 优化技巧
 */
public class SqlOptimization {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 9: SQL 优化实战");
        System.out.println("=".repeat(60));

        // 1. 慢查询日志
        System.out.println("\n【1. 慢查询日志】");
        System.out.println("""
                开启慢查询日志:

                -- 查看是否开启
                SHOW VARIABLES LIKE 'slow_query_log';

                -- 开启慢查询日志
                SET GLOBAL slow_query_log = 'ON';

                -- 设置阈值 (超过2秒记录)
                SET GLOBAL long_query_time = 2;

                -- 查看日志位置
                SHOW VARIABLES LIKE 'slow_query_log_file';

                配置文件 (my.cnf):
                [mysqld]
                slow_query_log = 1
                slow_query_log_file = /var/log/mysql/slow.log
                long_query_time = 2
                log_queries_not_using_indexes = 1

                分析慢查询日志:
                # 使用 mysqldumpslow 工具
                mysqldumpslow -s t -t 10 /var/log/mysql/slow.log

                参数说明:
                -s t: 按查询时间排序
                -t 10: 显示前10条
                -s c: 按执行次数排序
                """);

        // 2. EXPLAIN 执行计划
        System.out.println("=".repeat(60));
        System.out.println("【2. EXPLAIN 执行计划】");
        System.out.println("""
                基本用法:
                EXPLAIN SELECT * FROM users WHERE id = 1;

                结果字段解析:
                ┌──────────────┬────────────────────────────────────────┐
                │    字段      │             说明                       │
                ├──────────────┼────────────────────────────────────────┤
                │ id           │ 查询序号，越大越先执行                 │
                │ select_type  │ 查询类型 (SIMPLE、PRIMARY、SUBQUERY)   │
                │ table        │ 表名                                   │
                │ type         │ 访问类型 ⭐ 最重要                     │
                │ possible_keys│ 可能使用的索引                         │
                │ key          │ 实际使用的索引                         │
                │ key_len      │ 索引使用长度                           │
                │ rows         │ 预估扫描行数                           │
                │ filtered     │ 过滤比例 %                             │
                │ Extra        │ 额外信息                               │
                └──────────────┴────────────────────────────────────────┘

                type 访问类型 (性能从好到差):
                ┌────────────┬───────────────────────────────────────────┐
                │  类型      │             说明                          │
                ├────────────┼───────────────────────────────────────────┤
                │ system     │ 表只有一行                                │
                │ const      │ 主键或唯一索引等值查询，最多一行         │
                │ eq_ref     │ 主键或唯一索引连接查询，每行一条         │
                │ ref        │ 非唯一索引等值查询                       │
                │ range      │ 索引范围查询 (>, <, BETWEEN, IN)         │
                │ index      │ 索引全扫描                               │
                │ ALL        │ 全表扫描 ❌ 需要优化                     │
                └────────────┴───────────────────────────────────────────┘

                ⭐ 目标: type 至少达到 range 级别
                """);

        // 3. Extra 字段解读
        System.out.println("=".repeat(60));
        System.out.println("【3. Extra 字段解读】");
        System.out.println("""
                ┌─────────────────────────┬──────────────────────────────┐
                │         Extra值          │           说明              │
                ├─────────────────────────┼──────────────────────────────┤
                │ Using index             │ ✅ 覆盖索引，无需回表       │
                │ Using where             │ 存储引擎检索后再过滤        │
                │ Using index condition   │ ✅ 索引下推 ICP             │
                │ Using temporary         │ ⚠️ 使用临时表               │
                │ Using filesort          │ ⚠️ 额外排序                 │
                │ Select tables optimized │ MAX/MIN 优化               │
                └─────────────────────────┴──────────────────────────────┘

                Using filesort 示例:

                -- 有索引 idx_name
                EXPLAIN SELECT * FROM users ORDER BY age;  -- Using filesort
                EXPLAIN SELECT * FROM users ORDER BY name; -- 无 filesort

                Using temporary 示例:

                -- GROUP BY 字段没有索引
                EXPLAIN SELECT city, COUNT(*) FROM users GROUP BY city;
                -- Using temporary; Using filesort

                优化:
                CREATE INDEX idx_city ON users(city);
                """);

        // 4. 常见优化技巧
        System.out.println("=".repeat(60));
        System.out.println("【4. 常见 SQL 优化技巧】");
        System.out.println("""
                1. 避免 SELECT *

                -- 不好
                SELECT * FROM users WHERE id = 1;
                -- 好
                SELECT id, username, email FROM users WHERE id = 1;

                2. 使用覆盖索引

                -- 索引 (name, age)
                -- 只查询索引包含的列，无需回表
                SELECT name, age FROM users WHERE name = '张三';

                3. 避免在索引列上使用函数

                -- 不好 (索引失效)
                SELECT * FROM users WHERE YEAR(created_at) = 2024;
                -- 好
                SELECT * FROM users
                WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01';

                4. 小表驱动大表

                -- 如果 departments 是小表，users 是大表
                -- IN 适合外表小
                SELECT * FROM users WHERE dept_id IN (SELECT id FROM departments);

                -- EXISTS 适合外表大
                SELECT * FROM users u
                WHERE EXISTS (SELECT 1 FROM departments d WHERE d.id = u.dept_id);

                5. 避免 OR (用 UNION)

                -- 不好 (可能不走索引)
                SELECT * FROM users WHERE name = '张三' OR age = 25;

                -- 好
                SELECT * FROM users WHERE name = '张三'
                UNION ALL
                SELECT * FROM users WHERE age = 25;

                6. 分页优化

                -- 不好 (OFFSET 大时很慢)
                SELECT * FROM users ORDER BY id LIMIT 1000000, 10;

                -- 好 (延迟关联)
                SELECT u.* FROM users u
                INNER JOIN (SELECT id FROM users ORDER BY id LIMIT 1000000, 10) t
                ON u.id = t.id;

                -- 更好 (游标分页)
                SELECT * FROM users WHERE id > 1000000 ORDER BY id LIMIT 10;
                """);

        // 5. JOIN 优化
        System.out.println("=".repeat(60));
        System.out.println("【5. JOIN 优化】");
        System.out.println("""
                JOIN 算法:
                ┌───────────────────┬─────────────────────────────────────┐
                │      算法         │             说明                     │
                ├───────────────────┼─────────────────────────────────────┤
                │ Nested Loop Join  │ 嵌套循环，驱动表每行遍历被驱动表   │
                │ Block NL Join     │ 块嵌套循环，批量处理               │
                │ Index NL Join     │ 索引嵌套循环，被驱动表有索引       │
                │ Hash Join         │ 哈希连接，MySQL 8.0.18+            │
                └───────────────────┴─────────────────────────────────────┘

                优化建议:

                1. 被驱动表的关联字段建索引
                SELECT * FROM orders o
                JOIN users u ON o.user_id = u.id;  -- u.id 有主键索引

                2. 小表作为驱动表
                MySQL 优化器通常会自动选择

                3. 避免 JOIN 过多表
                一般不超过 3-4 个表

                4. 使用 STRAIGHT_JOIN 强制顺序 (慎用)
                SELECT STRAIGHT_JOIN * FROM small_table s
                JOIN large_table l ON s.id = l.small_id;
                """);

        // 6. 批量操作优化
        System.out.println("=".repeat(60));
        System.out.println("【6. 批量操作优化】");
        System.out.println("""
                批量插入:

                -- 不好 (多次网络往返)
                INSERT INTO users (name) VALUES ('张三');
                INSERT INTO users (name) VALUES ('李四');
                INSERT INTO users (name) VALUES ('王五');

                -- 好 (一次网络往返)
                INSERT INTO users (name) VALUES
                    ('张三'), ('李四'), ('王五');

                -- 更好 (分批，每批 500-1000 条)
                INSERT INTO users (name) VALUES ('user1'), ..., ('user500');
                INSERT INTO users (name) VALUES ('user501'), ..., ('user1000');

                批量更新:

                -- 不好
                UPDATE users SET status = 1 WHERE id = 1;
                UPDATE users SET status = 1 WHERE id = 2;

                -- 好
                UPDATE users SET status = 1 WHERE id IN (1, 2, 3, ...);

                -- 或者使用 CASE
                UPDATE users SET status = CASE
                    WHEN id = 1 THEN 2
                    WHEN id = 2 THEN 3
                    ELSE status
                END
                WHERE id IN (1, 2);

                批量删除:

                -- 不好 (锁表时间长)
                DELETE FROM logs WHERE created_at < '2024-01-01';

                -- 好 (分批删除)
                DELETE FROM logs WHERE created_at < '2024-01-01' LIMIT 1000;
                -- 循环直到删除完
                """);

        // 7. 实战优化案例
        System.out.println("=".repeat(60));
        System.out.println("【7. 实战优化案例】");
        System.out.println("""
                案例: 订单统计查询慢

                原始 SQL (5秒):
                SELECT u.username, COUNT(o.id) as order_count, SUM(o.amount) as total
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                WHERE o.created_at >= '2024-01-01'
                GROUP BY u.id
                ORDER BY total DESC
                LIMIT 10;

                问题分析:
                EXPLAIN 显示:
                - orders 表 type = ALL (全表扫描)
                - Extra: Using temporary; Using filesort

                优化步骤:

                1. 添加索引
                CREATE INDEX idx_user_created ON orders(user_id, created_at);

                2. 改写 SQL
                SELECT u.username, t.order_count, t.total
                FROM (
                    SELECT user_id, COUNT(*) as order_count, SUM(amount) as total
                    FROM orders
                    WHERE created_at >= '2024-01-01'
                    GROUP BY user_id
                    ORDER BY total DESC
                    LIMIT 10
                ) t
                JOIN users u ON t.user_id = u.id;

                优化后 (0.1秒):
                - 先在 orders 表聚合，利用索引
                - 只关联 TOP 10 的用户
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 EXPLAIN 是 SQL 优化的第一步");
        System.out.println("💡 type 至少要达到 range 级别");
        System.out.println("💡 避免 Using filesort 和 Using temporary");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 慢查询:
 * - 开启慢查询日志
 * - mysqldumpslow 分析
 * 
 * 2. EXPLAIN:
 * - type 访问类型
 * - key 实际索引
 * - Extra 额外信息
 * 
 * 3. 优化技巧:
 * - 避免 SELECT *
 * - 覆盖索引
 * - 分页优化
 * - 批量操作
 * 
 * 4. JOIN:
 * - 小表驱动大表
 * - 关联字段建索引
 */
