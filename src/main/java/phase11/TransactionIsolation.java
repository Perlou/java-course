package phase11;

/**
 * Phase 11 - Lesson 2: 事务隔离级别
 * 
 * 🎯 学习目标:
 * 1. 理解事务 ACID 特性
 * 2. 掌握四种隔离级别
 * 3. 了解并发问题和解决方案
 */
public class TransactionIsolation {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 2: 事务隔离级别");
        System.out.println("=".repeat(60));

        // 1. ACID 特性
        System.out.println("\n【1. 事务 ACID 特性】");
        System.out.println("""
                事务 = 一组操作的逻辑单元，要么全部成功，要么全部失败

                ┌─────────────────────────────────────────────────────────┐
                │                    ACID 特性                            │
                ├────────────────┬────────────────────────────────────────┤
                │ A - Atomicity  │ 原子性: 全部成功或全部回滚            │
                │ C - Consistency│ 一致性: 事务前后数据状态一致          │
                │ I - Isolation  │ 隔离性: 事务间相互隔离               │
                │ D - Durability │ 持久性: 提交后永久保存               │
                └────────────────┴────────────────────────────────────────┘

                例: 转账操作

                BEGIN;
                UPDATE accounts SET balance = balance - 100 WHERE id = 1;
                UPDATE accounts SET balance = balance + 100 WHERE id = 2;
                COMMIT;

                原子性: 两条 UPDATE 要么都成功，要么都回滚
                一致性: 转账前后总金额不变
                隔离性: 其他事务看不到中间状态
                持久性: COMMIT 后即使宕机数据也不会丢失 (redo log)
                """);

        // 2. 并发问题
        System.out.println("=".repeat(60));
        System.out.println("【2. 并发问题】");
        System.out.println("""
                多个事务并发执行时可能出现的问题:

                1. 脏读 (Dirty Read)
                ┌─────────────────────────────────────────────────────────┐
                │  事务 A:                    事务 B:                     │
                │  BEGIN;                     BEGIN;                      │
                │  UPDATE balance=200;                                    │
                │                             SELECT balance; → 200 (脏)  │
                │  ROLLBACK;                                              │
                │                             提交，但基于错误数据        │
                └─────────────────────────────────────────────────────────┘
                读到其他事务未提交的数据

                2. 不可重复读 (Non-Repeatable Read)
                ┌─────────────────────────────────────────────────────────┐
                │  事务 A:                    事务 B:                     │
                │  BEGIN;                     BEGIN;                      │
                │  SELECT balance; → 100                                  │
                │                             UPDATE balance=200;         │
                │                             COMMIT;                     │
                │  SELECT balance; → 200                                  │
                │  同一事务两次读取结果不同                                │
                └─────────────────────────────────────────────────────────┘
                同一事务内多次读取同一数据，结果不一致

                3. 幻读 (Phantom Read)
                ┌─────────────────────────────────────────────────────────┐
                │  事务 A:                    事务 B:                     │
                │  BEGIN;                     BEGIN;                      │
                │  SELECT COUNT(*); → 10                                  │
                │                             INSERT INTO ...;            │
                │                             COMMIT;                     │
                │  SELECT COUNT(*); → 11                                  │
                │  多出了一行 "幻影" 数据                                  │
                └─────────────────────────────────────────────────────────┘
                同一查询多次执行，返回的行数不同
                """);

        // 3. 隔离级别
        System.out.println("=".repeat(60));
        System.out.println("【3. 四种隔离级别】");
        System.out.println("""
                ┌──────────────────────┬─────────┬─────────────┬─────────┐
                │       隔离级别        │  脏读   │ 不可重复读  │  幻读   │
                ├──────────────────────┼─────────┼─────────────┼─────────┤
                │ READ UNCOMMITTED     │   ❌    │     ❌      │   ❌    │
                │ 读未提交             │ 可能    │    可能     │  可能   │
                ├──────────────────────┼─────────┼─────────────┼─────────┤
                │ READ COMMITTED       │   ✅    │     ❌      │   ❌    │
                │ 读已提交 (Oracle默认)│ 避免    │    可能     │  可能   │
                ├──────────────────────┼─────────┼─────────────┼─────────┤
                │ REPEATABLE READ      │   ✅    │     ✅      │   ❌*   │
                │ 可重复读 (MySQL默认) │ 避免    │    避免     │ 部分避免│
                ├──────────────────────┼─────────┼─────────────┼─────────┤
                │ SERIALIZABLE         │   ✅    │     ✅      │   ✅    │
                │ 串行化               │ 避免    │    避免     │  避免   │
                └──────────────────────┴─────────┴─────────────┴─────────┘

                * MySQL InnoDB 的 RR 级别通过间隙锁 + MVCC 可避免大部分幻读

                隔离级别越高，并发性能越低:
                性能: RU > RC > RR > Serializable
                安全: RU < RC < RR < Serializable

                查看/设置隔离级别:
                -- 查看
                SELECT @@transaction_isolation;

                -- 设置 (当前会话)
                SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
                """);

        // 4. MVCC
        System.out.println("=".repeat(60));
        System.out.println("【4. MVCC 多版本并发控制】");
        System.out.println("""
                MVCC = Multi-Version Concurrency Control

                原理: 为每行数据保存多个版本，读取时选择合适的版本

                InnoDB 隐藏列:
                ┌─────────────────────────────────────────────────────────┐
                │  id  │  name  │  DB_TRX_ID  │  DB_ROLL_PTR  │ DB_ROW_ID│
                │      │        │  事务ID     │  回滚指针      │  行ID    │
                └─────────────────────────────────────────────────────────┘

                Undo Log (版本链):
                ┌────────────┐    ┌────────────┐    ┌────────────┐
                │ 当前版本   │───▶│ 上一版本   │───▶│ 更早版本   │
                │ trx_id=103 │    │ trx_id=102 │    │ trx_id=101 │
                └────────────┘    └────────────┘    └────────────┘

                Read View (快照):
                ┌─────────────────────────────────────────────────────────┐
                │  m_ids: 活跃事务列表 [101, 102]                         │
                │  min_trx_id: 最小活跃事务 101                           │
                │  max_trx_id: 下一个分配的事务 104                       │
                │  creator_trx_id: 创建者事务 103                         │
                └─────────────────────────────────────────────────────────┘

                可见性判断:
                1. trx_id == creator_trx_id → 自己修改的，可见
                2. trx_id < min_trx_id → 事务已提交，可见
                3. trx_id >= max_trx_id → 事务在快照后开始，不可见
                4. trx_id in m_ids → 事务未提交，不可见

                RC vs RR 的区别:
                - RC: 每次 SELECT 都创建新的 Read View
                - RR: 只在第一次 SELECT 时创建 Read View
                """);

        // 5. 实际应用
        System.out.println("=".repeat(60));
        System.out.println("【5. 实际应用场景】");
        System.out.println("""
                场景 1: 秒杀库存扣减 (RR + 悲观锁)

                BEGIN;
                -- 加锁查询
                SELECT stock FROM products WHERE id = 1 FOR UPDATE;
                -- 扣减库存
                UPDATE products SET stock = stock - 1 WHERE id = 1 AND stock > 0;
                COMMIT;

                场景 2: 报表统计 (RR + 一致性快照)

                BEGIN;
                -- 同一事务内多次查询结果一致
                SELECT SUM(amount) FROM orders WHERE create_time >= '2024-01-01';
                -- ... 其他查询
                SELECT COUNT(*) FROM orders WHERE status = 'PAID';
                COMMIT;

                场景 3: 日志记录 (RC)

                -- 对一致性要求不高，用 RC 提高并发
                SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
                INSERT INTO logs (...) VALUES (...);

                场景 4: 账户余额 (序列化)

                -- 对数据一致性要求极高
                SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;
                BEGIN;
                SELECT balance FROM accounts WHERE id = 1;
                UPDATE accounts SET balance = balance - 100 WHERE id = 1;
                COMMIT;
                """);

        // 6. Spring 事务配置
        System.out.println("=".repeat(60));
        System.out.println("【6. Spring 事务配置】");
        System.out.println("""
                @Transactional 隔离级别:

                @Transactional(isolation = Isolation.READ_COMMITTED)
                public void updateBalance() {
                    // ...
                }

                隔离级别枚举:
                - Isolation.DEFAULT (数据库默认)
                - Isolation.READ_UNCOMMITTED
                - Isolation.READ_COMMITTED
                - Isolation.REPEATABLE_READ
                - Isolation.SERIALIZABLE

                只读优化:

                @Transactional(readOnly = true)
                public List<User> findAll() {
                    // 只读事务，优化性能
                    return userRepository.findAll();
                }

                超时设置:

                @Transactional(timeout = 30)  // 30秒超时
                public void longRunningTask() {
                    // ...
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 MySQL 默认隔离级别是 RR (可重复读)");
        System.out.println("💡 MVCC 实现了读写不阻塞，提高并发");
        System.out.println("💡 根据业务场景选择合适的隔离级别");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. ACID 特性:
 * - 原子性、一致性、隔离性、持久性
 * 
 * 2. 并发问题:
 * - 脏读、不可重复读、幻读
 * 
 * 3. 隔离级别:
 * - RU < RC < RR < Serializable
 * - MySQL 默认 RR
 * 
 * 4. MVCC:
 * - 版本链 + Read View
 * - 读写不阻塞
 */
