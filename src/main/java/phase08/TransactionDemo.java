package phase08;

/**
 * Phase 8 - Lesson 5: 声明式事务
 * 
 * 🎯 学习目标:
 * 1. 理解事务的 ACID 特性
 * 2. 掌握 @Transactional 的使用
 * 3. 理解事务传播行为
 */
public class TransactionDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 5: 声明式事务");
        System.out.println("=".repeat(60));

        // 1. 事务基础
        System.out.println("\n【1. 事务 ACID 特性】");
        System.out.println("""
                ACID - 事务四大特性:

                ┌────────────────┬────────────────────────────────────┐
                │     特性        │              说明                  │
                ├────────────────┼────────────────────────────────────┤
                │ A - Atomicity   │ 原子性: 全部成功或全部回滚        │
                │ C - Consistency │ 一致性: 事务前后数据保持一致      │
                │ I - Isolation   │ 隔离性: 事务间相互隔离            │
                │ D - Durability  │ 持久性: 提交后数据永久保存        │
                └────────────────┴────────────────────────────────────┘

                经典案例 - 转账:
                1. A 账户 -100
                2. B 账户 +100

                原子性: 1和2要么都成功，要么都失败
                一致性: 转账后总金额不变
                """);

        // 2. @Transactional 使用
        System.out.println("=".repeat(60));
        System.out.println("【2. @Transactional 使用】");
        System.out.println("""
                基本使用:

                @Service
                public class OrderService {

                    @Autowired
                    private OrderRepository orderRepo;

                    @Autowired
                    private InventoryService inventoryService;

                    @Transactional
                    public void createOrder(Order order) {
                        // 1. 保存订单
                        orderRepo.save(order);

                        // 2. 扣减库存
                        inventoryService.deduct(order.getProductId(), order.getQuantity());

                        // 如果任何一步失败，整个事务回滚
                    }
                }

                @Transactional 属性:
                ┌──────────────────┬────────────────────────────────────┐
                │      属性         │              说明                  │
                ├──────────────────┼────────────────────────────────────┤
                │ propagation      │ 传播行为 (默认 REQUIRED)          │
                │ isolation        │ 隔离级别 (默认数据库默认)         │
                │ timeout          │ 超时时间 (秒)                     │
                │ readOnly         │ 只读事务 (优化查询)               │
                │ rollbackFor      │ 触发回滚的异常                    │
                │ noRollbackFor    │ 不触发回滚的异常                  │
                └──────────────────┴────────────────────────────────────┘
                """);

        // 3. 事务传播行为
        System.out.println("=".repeat(60));
        System.out.println("【3. 事务传播行为 (Propagation)】");
        System.out.println("""
                ┌────────────────────┬────────────────────────────────────┐
                │     传播行为        │              说明                  │
                ├────────────────────┼────────────────────────────────────┤
                │ REQUIRED (默认)    │ 有事务加入，没有新建               │
                │ REQUIRES_NEW       │ 总是新建事务，挂起当前事务         │
                │ SUPPORTS           │ 有事务加入，没有就无事务执行       │
                │ NOT_SUPPORTED      │ 无事务执行，挂起当前事务           │
                │ MANDATORY          │ 必须在事务中，否则异常             │
                │ NEVER              │ 不能在事务中，否则异常             │
                │ NESTED             │ 嵌套事务 (保存点)                  │
                └────────────────────┴────────────────────────────────────┘

                常用场景:

                REQUIRED (默认):
                - A 方法调用 B 方法
                - B 加入 A 的事务
                - A 或 B 任一失败，都回滚

                REQUIRES_NEW:
                - 日志记录
                - 即使主事务回滚，日志也要保存

                @Service
                public class AuditService {
                    @Transactional(propagation = Propagation.REQUIRES_NEW)
                    public void log(String action) {
                        // 独立事务，不受外部事务影响
                    }
                }
                """);

        // 4. 事务隔离级别
        System.out.println("=".repeat(60));
        System.out.println("【4. 事务隔离级别 (Isolation)】");
        System.out.println("""
                并发问题:
                ┌────────────────┬────────────────────────────────────┐
                │     问题        │              说明                  │
                ├────────────────┼────────────────────────────────────┤
                │ 脏读            │ 读到未提交的数据                  │
                │ 不可重复读      │ 同一事务内两次读取结果不同        │
                │ 幻读            │ 同一事务内两次查询记录数不同      │
                └────────────────┴────────────────────────────────────┘

                隔离级别:
                ┌───────────────────┬──────┬──────────┬──────┐
                │     隔离级别       │ 脏读 │ 不可重复读 │ 幻读 │
                ├───────────────────┼──────┼──────────┼──────┤
                │ READ_UNCOMMITTED  │  ✓   │    ✓     │  ✓   │
                │ READ_COMMITTED    │  ✗   │    ✓     │  ✓   │
                │ REPEATABLE_READ   │  ✗   │    ✗     │  ✓   │
                │ SERIALIZABLE      │  ✗   │    ✗     │  ✗   │
                └───────────────────┴──────┴──────────┴──────┘

                MySQL 默认: REPEATABLE_READ
                Oracle 默认: READ_COMMITTED

                @Transactional(isolation = Isolation.READ_COMMITTED)
                """);

        // 5. 事务失效场景
        System.out.println("=".repeat(60));
        System.out.println("【5. @Transactional 失效场景】");
        System.out.println("""
                ⚠️ 事务失效的常见场景:

                1. 非 public 方法 ❌
                   @Transactional
                   private void doSomething() { }  // 不生效!

                2. 同类自调用 ❌
                   public void methodA() {
                       methodB();  // 不走代理，事务不生效!
                   }
                   @Transactional
                   public void methodB() { }

                   解决: 注入自己或使用 AopContext

                3. 异常被捕获 ❌
                   @Transactional
                   public void save() {
                       try {
                           // 业务代码
                       } catch (Exception e) {
                           // 异常被吞，不回滚!
                       }
                   }

                   解决: 重新抛出或手动回滚

                4. 非 RuntimeException ❌
                   @Transactional
                   public void save() throws IOException {
                       throw new IOException();  // 不回滚!
                   }

                   解决: @Transactional(rollbackFor = Exception.class)

                5. 非 Spring 管理的类 ❌
                   new OrderService().save();  // 不是代理对象!
                """);

        // 6. 最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【6. 事务最佳实践】");
        System.out.println("""
                ✅ 推荐做法:

                1. 事务粒度要小
                   - 只在需要的方法上加事务
                   - 避免大事务

                2. 指定 rollbackFor
                   @Transactional(rollbackFor = Exception.class)

                3. 只读事务优化
                   @Transactional(readOnly = true)
                   public List<User> findAll() { }

                4. 合理设置超时
                   @Transactional(timeout = 30)

                5. 避免事务中调用远程服务
                   - 网络请求可能很慢
                   - 导致事务过长

                6. 分布式事务
                   - 本地消息表
                   - Seata 框架
                   - TCC 模式
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 @Transactional 基于 AOP 实现");
        System.out.println("💡 默认只对 RuntimeException 回滚");
        System.out.println("💡 注意自调用和非 public 方法失效");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. ACID 特性:
 * - 原子性、一致性、隔离性、持久性
 * 
 * 2. @Transactional:
 * - propagation: 传播行为
 * - isolation: 隔离级别
 * - rollbackFor: 回滚异常
 * - readOnly: 只读优化
 * 
 * 3. 传播行为:
 * - REQUIRED: 默认，加入或新建
 * - REQUIRES_NEW: 独立事务
 * 
 * 4. 失效场景:
 * - 非 public、自调用
 * - 异常被捕获、非 RuntimeException
 */
