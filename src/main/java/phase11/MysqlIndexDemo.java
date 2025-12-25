package phase11;

/**
 * Phase 11 - Lesson 1: MySQL 索引原理
 * 
 * 🎯 学习目标:
 * 1. 理解 B+ 树索引结构
 * 2. 掌握各种索引类型
 * 3. 学会索引优化策略
 */
public class MysqlIndexDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 1: MySQL 索引原理");
        System.out.println("=".repeat(60));

        // 1. 什么是索引
        System.out.println("\n【1. 什么是索引】");
        System.out.println("""
                索引 = 数据库表的目录

                没有索引:
                ┌──────────────────────────────────────────────────────┐
                │  SELECT * FROM users WHERE id = 100                  │
                │                                                      │
                │  需要扫描全表 (Full Table Scan)                      │
                │  数据量: 100万 → 扫描: 100万行                       │
                │  时间复杂度: O(n)                                    │
                └──────────────────────────────────────────────────────┘

                有索引:
                ┌──────────────────────────────────────────────────────┐
                │  SELECT * FROM users WHERE id = 100                  │
                │                                                      │
                │  通过索引快速定位 (Index Seek)                       │
                │  数据量: 100万 → 扫描: ~20行 (树高度)                │
                │  时间复杂度: O(log n)                                │
                └──────────────────────────────────────────────────────┘

                索引本质: 排序 + 快速查找的数据结构
                """);

        // 2. B+ 树结构
        System.out.println("=".repeat(60));
        System.out.println("【2. B+ 树索引结构】");
        System.out.println("""
                为什么用 B+ 树而不是二叉树?

                1. 二叉树: 树太高，磁盘 IO 次数多
                2. B 树: 数据分布在所有节点
                3. B+ 树: 数据只在叶子节点，叶子节点链表连接

                B+ 树结构:
                ┌─────────────────────────────────────────────────────────┐
                │                        [15 | 28]                        │ ← 根节点
                │                       /    |    \\                       │   非叶子节点
                │                      /     |     \\                      │   只存索引
                │              [5|10]    [18|23]    [30|35]               │ ← 非叶子
                │              / | \\      / | \\      / | \\                │
                │             /  |  \\    /  |  \\    /  |  \\               │
                │            ↓   ↓   ↓  ↓   ↓   ↓  ↓   ↓   ↓              │
                │           [1-5][6-10][11-15]...[30-35][36-40]           │ ← 叶子节点
                │            ↔    ↔    ↔    ↔    ↔    ↔                   │   存数据
                │           双向链表连接，支持范围查询                    │   链表连接
                └─────────────────────────────────────────────────────────┘

                B+ 树优点:
                1. 树高度低 (3-4层可存千万数据)
                2. 非叶子节点不存数据，一页能存更多索引
                3. 叶子节点链表连接，范围查询高效
                4. 查询复杂度稳定 O(log n)
                """);

        // 3. 聚簇索引 vs 非聚簇索引
        System.out.println("=".repeat(60));
        System.out.println("【3. 聚簇索引 vs 非聚簇索引】");
        System.out.println("""
                InnoDB 存储引擎:

                1. 聚簇索引 (Clustered Index) - 主键索引
                ┌─────────────────────────────────────────────────────────┐
                │  主键 B+ 树                                             │
                │  ┌─────┐                                               │
                │  │  15 │ ← 非叶子节点存主键                            │
                │  └──┬──┘                                               │
                │     │                                                   │
                │  ┌──┴──┐                                               │
                │  │ 15  │ → [id=15, name='张三', age=25, ...]           │
                │  └─────┘   ← 叶子节点存完整行数据                       │
                └─────────────────────────────────────────────────────────┘

                2. 非聚簇索引 (Secondary Index) - 普通索引
                ┌─────────────────────────────────────────────────────────┐
                │  name 索引 B+ 树                                        │
                │  ┌─────┐                                               │
                │  │张三 │ ← 非叶子节点存索引列                          │
                │  └──┬──┘                                               │
                │     │                                                   │
                │  ┌──┴──┐                                               │
                │  │张三 │ → 主键值: 15                                  │
                │  └─────┘   ← 叶子节点存主键值（需要回表查询）           │
                └─────────────────────────────────────────────────────────┘

                回表查询:
                SELECT * FROM users WHERE name = '张三'
                1. 先通过 name 索引找到主键 id = 15
                2. 再通过主键索引找到完整行数据
                = 两次 B+ 树查询

                覆盖索引 (避免回表):
                SELECT id, name FROM users WHERE name = '张三'
                如果索引包含了需要的所有列，无需回表
                """);

        // 4. 索引类型
        System.out.println("=".repeat(60));
        System.out.println("【4. 索引类型】");
        System.out.println("""
                ┌──────────────────┬────────────────────────────────────┐
                │     索引类型      │             说明                   │
                ├──────────────────┼────────────────────────────────────┤
                │ 主键索引          │ PRIMARY KEY，唯一且非空            │
                │ 唯一索引          │ UNIQUE，值唯一可为 NULL            │
                │ 普通索引          │ INDEX，无限制                      │
                │ 组合索引          │ 多列联合索引                       │
                │ 前缀索引          │ 字符串前 N 位建索引                │
                │ 全文索引          │ FULLTEXT，全文搜索                 │
                └──────────────────┴────────────────────────────────────┘

                创建索引:

                -- 主键索引
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(50)
                );

                -- 唯一索引
                CREATE UNIQUE INDEX idx_email ON users(email);

                -- 普通索引
                CREATE INDEX idx_name ON users(name);

                -- 组合索引
                CREATE INDEX idx_name_age ON users(name, age);

                -- 前缀索引 (节省空间)
                CREATE INDEX idx_email_prefix ON users(email(10));
                """);

        // 5. 组合索引与最左前缀
        System.out.println("=".repeat(60));
        System.out.println("【5. 组合索引与最左前缀原则】");
        System.out.println("""
                组合索引: CREATE INDEX idx ON users(a, b, c)

                索引结构 (排序规则):
                ┌─────────────────────────────────────────────────────────┐
                │  先按 a 排序，a 相同按 b 排序，b 相同按 c 排序          │
                │                                                         │
                │  (1, 1, 1)                                             │
                │  (1, 1, 2)                                             │
                │  (1, 2, 1)                                             │
                │  (2, 1, 1)                                             │
                │  (2, 1, 2)                                             │
                │  ...                                                   │
                └─────────────────────────────────────────────────────────┘

                最左前缀原则:

                ✅ 走索引:
                WHERE a = 1
                WHERE a = 1 AND b = 2
                WHERE a = 1 AND b = 2 AND c = 3
                WHERE a = 1 AND c = 3  (只用到 a)

                ❌ 不走索引:
                WHERE b = 2
                WHERE c = 3
                WHERE b = 2 AND c = 3

                ⚠️ 范围查询后的列不走索引:
                WHERE a = 1 AND b > 2 AND c = 3
                只用到 a 和 b，c 不走索引

                索引下推 (Index Condition Pushdown):
                MySQL 5.6+ 可以在索引层过滤，减少回表
                """);

        // 6. 索引失效场景
        System.out.println("=".repeat(60));
        System.out.println("【6. 索引失效场景】");
        System.out.println("""
                ❌ 索引失效的常见情况:

                1. 对索引列使用函数或运算
                -- 失效
                WHERE YEAR(create_time) = 2024
                WHERE id + 1 = 100
                -- 优化
                WHERE create_time >= '2024-01-01' AND create_time < '2025-01-01'
                WHERE id = 99

                2. 隐式类型转换
                -- phone 是 VARCHAR，传入数字，失效
                WHERE phone = 13800138000
                -- 优化
                WHERE phone = '13800138000'

                3. LIKE 以 % 开头
                -- 失效
                WHERE name LIKE '%张三'
                -- 走索引
                WHERE name LIKE '张三%'

                4. OR 连接非索引列
                -- 如果 age 无索引，失效
                WHERE name = '张三' OR age = 25
                -- 优化: 用 UNION
                SELECT * FROM users WHERE name = '张三'
                UNION ALL
                SELECT * FROM users WHERE age = 25

                5. NOT IN, NOT EXISTS, <>, !=
                -- 可能不走索引 (优化器判断)
                WHERE status != 1
                -- 优化
                WHERE status IN (0, 2, 3)

                6. IS NULL, IS NOT NULL (取决于数据分布)
                """);

        // 7. 索引优化策略
        System.out.println("=".repeat(60));
        System.out.println("【7. 索引优化策略】");
        System.out.println("""
                ✅ 索引设计原则:

                1. 选择区分度高的列
                   区分度 = COUNT(DISTINCT column) / COUNT(*)
                   性别 (区分度低) < 手机号 (区分度高)

                2. 组合索引列的顺序
                   - 区分度高的在前
                   - 常用查询条件在前
                   - 范围查询的列在后

                3. 覆盖索引
                   SELECT a, b FROM t WHERE a = ? AND b = ?
                   索引 (a, b) 可以覆盖查询，无需回表

                4. 索引不是越多越好
                   - 占用磁盘空间
                   - 插入/更新/删除需要维护索引
                   - 一般单表索引不超过 5 个

                5. 长字符串用前缀索引
                   CREATE INDEX idx_email ON users(email(10));

                6. 使用 EXPLAIN 分析索引使用情况
                   EXPLAIN SELECT * FROM users WHERE name = '张三';

                EXPLAIN 关键字段:
                ┌──────────────┬────────────────────────────────────┐
                │    字段      │             说明                   │
                ├──────────────┼────────────────────────────────────┤
                │ type         │ 访问类型 (const > eq_ref > ref > range > index > ALL)
                │ key          │ 实际使用的索引                     │
                │ rows         │ 预估扫描行数                       │
                │ Extra        │ 额外信息 (Using index = 覆盖索引)  │
                └──────────────┴────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 B+ 树是 MySQL InnoDB 的核心索引结构");
        System.out.println("💡 聚簇索引存数据，非聚簇索引存主键");
        System.out.println("💡 注意最左前缀原则和索引失效场景");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 索引结构:
 * - B+ 树
 * - 非叶子节点存索引，叶子节点存数据
 * - 叶子节点链表连接
 * 
 * 2. 索引类型:
 * - 聚簇索引 (主键)
 * - 非聚簇索引 (普通索引)
 * - 回表查询
 * 
 * 3. 最左前缀:
 * - 组合索引从左到右使用
 * - 范围查询后的列不走索引
 * 
 * 4. 索引优化:
 * - 覆盖索引
 * - EXPLAIN 分析
 * - 避免索引失效
 */
