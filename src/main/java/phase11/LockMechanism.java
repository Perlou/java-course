package phase11;

/**
 * Phase 11 - Lesson 3: MySQL 锁机制
 * 
 * 🎯 学习目标:
 * 1. 理解各种锁类型
 * 2. 掌握行锁、表锁的使用
 * 3. 了解死锁问题和解决方案
 */
public class LockMechanism {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 3: MySQL 锁机制");
        System.out.println("=".repeat(60));

        // 1. 锁的分类
        System.out.println("\n【1. 锁的分类】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                    MySQL 锁分类                         │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  按锁粒度:                                              │
                │  ├── 表锁 (Table Lock) - MyISAM                        │
                │  ├── 页锁 (Page Lock) - BDB                            │
                │  └── 行锁 (Row Lock) - InnoDB                          │
                │                                                         │
                │  按锁类型:                                              │
                │  ├── 共享锁 (S Lock / 读锁)                            │
                │  └── 排他锁 (X Lock / 写锁)                            │
                │                                                         │
                │  按锁思想:                                              │
                │  ├── 悲观锁 (数据库锁)                                  │
                │  └── 乐观锁 (版本号/CAS)                                │
                │                                                         │
                │  InnoDB 特有:                                           │
                │  ├── 间隙锁 (Gap Lock)                                  │
                │  ├── 临键锁 (Next-Key Lock)                            │
                │  └── 意向锁 (Intention Lock)                            │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 表锁 vs 行锁
        System.out.println("=".repeat(60));
        System.out.println("【2. 表锁 vs 行锁】");
        System.out.println("""
                表锁 (Table Lock):
                ┌─────────────────────────────────────────────────────────┐
                │  特点: 锁整张表，开销小，加锁快                         │
                │  缺点: 并发度低，冲突严重                               │
                │  场景: ALTER TABLE, MyISAM 存储引擎                     │
                │                                                         │
                │  LOCK TABLES users READ;   -- 读锁                      │
                │  LOCK TABLES users WRITE;  -- 写锁                      │
                │  UNLOCK TABLES;            -- 释放                      │
                └─────────────────────────────────────────────────────────┘

                行锁 (Row Lock):
                ┌─────────────────────────────────────────────────────────┐
                │  特点: 锁定单行，开销大，加锁慢                         │
                │  优点: 并发度高，冲突少                                 │
                │  场景: InnoDB 存储引擎，UPDATE/DELETE                   │
                │                                                         │
                │  ⚠️ 行锁必须有索引，没有索引会升级为表锁               │
                └─────────────────────────────────────────────────────────┘

                行锁升级为表锁:
                -- 如果 name 列没有索引，会锁全表！
                UPDATE users SET status = 1 WHERE name = '张三';

                -- 有索引，只锁定匹配的行
                UPDATE users SET status = 1 WHERE id = 1;
                """);

        // 3. 共享锁和排他锁
        System.out.println("=".repeat(60));
        System.out.println("【3. 共享锁和排他锁】");
        System.out.println("""
                兼容矩阵:
                ┌──────────┬──────────┬──────────┐
                │          │  S Lock  │  X Lock  │
                ├──────────┼──────────┼──────────┤
                │  S Lock  │    ✅    │    ❌    │
                │  X Lock  │    ❌    │    ❌    │
                └──────────┴──────────┴──────────┘

                共享锁 (S Lock / 读锁):
                SELECT * FROM users WHERE id = 1 LOCK IN SHARE MODE;
                SELECT * FROM users WHERE id = 1 FOR SHARE; -- MySQL 8.0+

                排他锁 (X Lock / 写锁):
                SELECT * FROM users WHERE id = 1 FOR UPDATE;
                UPDATE users SET name = '张三' WHERE id = 1;  -- 自动加锁
                DELETE FROM users WHERE id = 1;               -- 自动加锁

                应用场景:

                -- 库存扣减 (需要排他锁)
                BEGIN;
                SELECT stock FROM products WHERE id = 1 FOR UPDATE;  -- 加锁
                UPDATE products SET stock = stock - 1 WHERE id = 1;
                COMMIT;

                -- 读取报表 (共享锁，允许其他读取)
                BEGIN;
                SELECT SUM(amount) FROM orders LOCK IN SHARE MODE;
                COMMIT;
                """);

        // 4. 间隙锁和临键锁
        System.out.println("=".repeat(60));
        System.out.println("【4. 间隙锁和临键锁】");
        System.out.println("""
                间隙锁 (Gap Lock) - 锁定记录间的间隙，防止幻读

                假设表中有 id: 1, 5, 10

                ┌────┬────────────────────────────────────────────────────┐
                │(-∞,1)│ id=1 │(1,5)│ id=5 │(5,10)│ id=10 │(10,+∞)      │
                │ gap  │ rec  │ gap │ rec  │ gap  │  rec  │  gap         │
                └────┴────────────────────────────────────────────────────┘

                SELECT * FROM t WHERE id > 5 AND id < 10 FOR UPDATE;
                锁定间隙 (5, 10)，阻止在此范围内插入新记录

                临键锁 (Next-Key Lock) = 行锁 + 间隙锁
                ┌─────────────────────────────────────────────────────────┐
                │  MySQL InnoDB 默认使用 Next-Key Lock                    │
                │  锁定: 行本身 + 行前面的间隙                            │
                │                                                         │
                │  SELECT * FROM t WHERE id = 5 FOR UPDATE;              │
                │  锁定: id=5 这行 + 间隙 (1, 5)                          │
                └─────────────────────────────────────────────────────────┘

                加锁规则 (了解):
                1. 唯一索引等值查询，命中时退化为行锁
                2. 唯一索引等值查询，未命中时锁间隙
                3. 非唯一索引查询，使用 Next-Key Lock
                """);

        // 5. 悲观锁 vs 乐观锁
        System.out.println("=".repeat(60));
        System.out.println("【5. 悲观锁 vs 乐观锁】");
        System.out.println("""
                悲观锁:
                ┌─────────────────────────────────────────────────────────┐
                │  假设会发生冲突，先加锁再操作                           │
                │                                                         │
                │  BEGIN;                                                 │
                │  SELECT * FROM products WHERE id = 1 FOR UPDATE;       │
                │  -- 此时其他事务无法修改 id=1 的行                      │
                │  UPDATE products SET stock = stock - 1 WHERE id = 1;   │
                │  COMMIT;                                                │
                │                                                         │
                │  优点: 安全，不会丢失更新                               │
                │  缺点: 性能低，可能死锁                                 │
                └─────────────────────────────────────────────────────────┘

                乐观锁:
                ┌─────────────────────────────────────────────────────────┐
                │  假设不会冲突，更新时检查版本号                         │
                │                                                         │
                │  1. 查询带版本号                                        │
                │  SELECT id, stock, version FROM products WHERE id = 1; │
                │  -- stock = 100, version = 1                           │
                │                                                         │
                │  2. 更新时检查版本号                                    │
                │  UPDATE products                                        │
                │  SET stock = 99, version = version + 1                 │
                │  WHERE id = 1 AND version = 1;                         │
                │                                                         │
                │  3. 如果 affected_rows = 0，说明被其他事务修改过        │
                │                                                         │
                │  优点: 性能高，无锁                                     │
                │  缺点: 高并发时失败率高，需要重试                       │
                └─────────────────────────────────────────────────────────┘

                JPA 乐观锁:
                @Entity
                public class Product {
                    @Id
                    private Long id;

                    @Version  // 乐观锁版本号
                    private Integer version;
                }

                MyBatis-Plus 乐观锁:
                @TableField(fill = FieldFill.INSERT)
                @Version
                private Integer version;
                """);

        // 6. 死锁
        System.out.println("=".repeat(60));
        System.out.println("【6. 死锁问题】");
        System.out.println("""
                死锁场景:
                ┌─────────────────────────────────────────────────────────┐
                │  事务 A:                    事务 B:                     │
                │  BEGIN;                     BEGIN;                      │
                │  UPDATE t SET x=1           UPDATE t SET x=1            │
                │  WHERE id=1;                WHERE id=2;                 │
                │  -- 锁定 id=1               -- 锁定 id=2               │
                │                                                         │
                │  UPDATE t SET x=1           UPDATE t SET x=1            │
                │  WHERE id=2;                WHERE id=1;                 │
                │  -- 等待 id=2               -- 等待 id=1               │
                │                                                         │
                │  ❌ 死锁！互相等待                                      │
                └─────────────────────────────────────────────────────────┘

                MySQL 死锁处理:
                1. 死锁检测: 自动检测死锁
                2. 死锁处理: 回滚代价较小的事务

                查看死锁信息:
                SHOW ENGINE INNODB STATUS\\G

                避免死锁:
                1. 按固定顺序访问资源
                   -- 总是按 id 从小到大的顺序锁定
                   UPDATE t SET x=1 WHERE id IN (1, 2) ORDER BY id;

                2. 减少锁持有时间
                   -- 先查询，再加锁
                   SELECT * FROM t WHERE ...;  -- 无锁查询
                   UPDATE t SET x=1 WHERE id=1 FOR UPDATE;

                3. 使用低隔离级别
                   -- RC 级别没有间隙锁，死锁概率低

                4. 小事务
                   -- 事务越小，锁持有时间越短
                """);

        // 7. 锁优化建议
        System.out.println("=".repeat(60));
        System.out.println("【7. 锁优化建议】");
        System.out.println("""
                ✅ 最佳实践:

                1. 尽量使用索引
                   -- 避免行锁升级为表锁
                   UPDATE users SET status = 1 WHERE id = 1;  -- 有索引

                2. 合理选择锁类型
                   -- 读多写少: 乐观锁
                   -- 写多读少: 悲观锁
                   -- 高并发扣减: FOR UPDATE

                3. 控制事务大小
                   -- 事务越小，锁持有时间越短
                   BEGIN;
                   -- 只包含必要的操作
                   UPDATE ...;
                   COMMIT;

                4. 避免长事务
                   -- 长事务会持有锁很久，影响并发
                   -- 大批量操作分批处理

                5. 合理设置超时
                   -- innodb_lock_wait_timeout = 50 (默认)
                   SET innodb_lock_wait_timeout = 10;

                6. 监控锁情况
                   -- 查看当前锁
                   SELECT * FROM performance_schema.data_locks;
                   SELECT * FROM information_schema.INNODB_TRX;
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 InnoDB 默认行锁，需要索引支持");
        System.out.println("💡 读用共享锁，写用排他锁");
        System.out.println("💡 高并发场景选择乐观锁或悲观锁");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 锁分类:
 * - 表锁、行锁
 * - 共享锁、排他锁
 * - 间隙锁、临键锁
 * 
 * 2. 悲观锁 vs 乐观锁:
 * - 悲观锁: FOR UPDATE
 * - 乐观锁: version 字段
 * 
 * 3. 死锁:
 * - 原因: 循环等待
 * - 解决: 固定顺序、小事务
 * 
 * 4. 优化:
 * - 使用索引
 * - 控制事务大小
 * - 合理选择锁类型
 */
