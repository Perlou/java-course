package phase12;

/**
 * Phase 12 - Lesson 7: Seata 分布式事务
 * 
 * 🎯 学习目标:
 * 1. 理解分布式事务问题
 * 2. 掌握 Seata 的使用
 * 3. 了解不同事务模式
 */
public class SeataDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 7: Seata 分布式事务");
        System.out.println("=".repeat(60));

        // 1. 分布式事务问题
        System.out.println("\n【1. 分布式事务问题】");
        System.out.println("""
                场景: 下单扣库存

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  ┌─────────┐     ┌─────────┐     ┌─────────┐          │
                │  │订单服务 │     │库存服务 │     │账户服务 │          │
                │  │ DB1     │     │ DB2     │     │ DB3     │          │
                │  └─────────┘     └─────────┘     └─────────┘          │
                │       │               │               │                │
                │  创建订单 ✓      扣减库存 ✓      扣减余额 ✗           │
                │                                                         │
                │  问题: 订单和库存已修改，余额扣减失败                   │
                │        如何回滚订单和库存？                             │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                本地事务无法解决:
                - 每个服务有独立数据库
                - 跨服务无法用 @Transactional

                分布式事务方案:
                ┌──────────────────┬──────────────────────────────────────┐
                │       方案       │             特点                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ 2PC/3PC          │ 强一致，性能差                       │
                │ TCC              │ 最终一致，代码侵入大                 │
                │ Saga             │ 最终一致，补偿操作                   │
                │ 本地消息表       │ 最终一致，基于 MQ                    │
                │ Seata AT         │ 强一致，无代码侵入 (推荐)            │
                └──────────────────┴──────────────────────────────────────┘
                """);

        // 2. Seata 简介
        System.out.println("=".repeat(60));
        System.out.println("【2. Seata 简介】");
        System.out.println("""
                Seata = Simple Extensible Autonomous Transaction Architecture

                ┌─────────────────────────────────────────────────────────┐
                │                    Seata 架构                           │
                │                                                         │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │                 TC (Transaction Coordinator)    │   │
                │  │                 事务协调器 (Seata Server)        │   │
                │  └─────────────────────────────────────────────────┘   │
                │                ▲                ▲                       │
                │                │                │                       │
                │        ┌───────┴────┐    ┌─────┴───────┐              │
                │        │            │    │             │              │
                │  ┌─────────┐  ┌─────────┐  ┌─────────┐               │
                │  │   TM    │  │   RM    │  │   RM    │               │
                │  │ 事务管理│  │ 资源管理│  │ 资源管理│               │
                │  │ 订单服务│  │ 库存服务│  │ 账户服务│               │
                │  └─────────┘  └─────────┘  └─────────┘               │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                核心组件:
                ┌──────────────────┬──────────────────────────────────────┐
                │       组件       │             说明                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ TC               │ 事务协调器，维护全局事务状态         │
                │ TM               │ 事务管理器，发起全局事务             │
                │ RM               │ 资源管理器，管理分支事务             │
                └──────────────────┴──────────────────────────────────────┘

                支持模式:
                - AT 模式 (自动补偿，推荐)
                - TCC 模式 (手动编码)
                - Saga 模式 (长事务)
                - XA 模式 (强一致)
                """);

        // 3. AT 模式
        System.out.println("=".repeat(60));
        System.out.println("【3. AT 模式详解】");
        System.out.println("""
                AT 模式 = 自动补偿的两阶段提交

                阶段一 (执行):
                ┌─────────────────────────────────────────────────────────┐
                │  1. 解析 SQL，记录 beforeImage (修改前数据)             │
                │  2. 执行业务 SQL                                        │
                │  3. 记录 afterImage (修改后数据)                        │
                │  4. 生成 undo_log 记录                                  │
                │  5. 提交本地事务                                        │
                └─────────────────────────────────────────────────────────┘

                阶段二 (提交/回滚):
                ┌─────────────────────────────────────────────────────────┐
                │  提交: 删除 undo_log 记录                               │
                │                                                         │
                │  回滚: 根据 undo_log 反向补偿                           │
                │        1. 用 beforeImage 检查数据是否被脏写             │
                │        2. 如果未被修改，恢复为 beforeImage              │
                │        3. 删除 undo_log                                 │
                └─────────────────────────────────────────────────────────┘

                undo_log 表结构:
                CREATE TABLE undo_log (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    branch_id BIGINT NOT NULL,
                    xid VARCHAR(100) NOT NULL,
                    context VARCHAR(128) NOT NULL,
                    rollback_info LONGBLOB NOT NULL,
                    log_status INT(11) NOT NULL,
                    log_created DATETIME NOT NULL,
                    log_modified DATETIME NOT NULL,
                    UNIQUE KEY ux_undo_log (xid, branch_id)
                );

                ⚠️ 每个微服务的数据库都需要创建 undo_log 表
                """);

        // 4. Spring Boot 集成
        System.out.println("=".repeat(60));
        System.out.println("【4. Spring Boot 集成 Seata】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
                </dependency>

                2. 配置

                seata:
                  enabled: true
                  tx-service-group: my_tx_group
                  service:
                    vgroup-mapping:
                      my_tx_group: default
                    grouplist:
                      default: 127.0.0.1:8091

                  # 使用 Nacos 作为注册中心
                  registry:
                    type: nacos
                    nacos:
                      server-addr: localhost:8848
                      namespace: ""
                      group: SEATA_GROUP

                  # 使用 Nacos 作为配置中心
                  config:
                    type: nacos
                    nacos:
                      server-addr: localhost:8848
                      namespace: ""
                      group: SEATA_GROUP

                3. 使用 @GlobalTransactional

                @Service
                @RequiredArgsConstructor
                public class OrderService {

                    private final OrderMapper orderMapper;
                    private final StorageClient storageClient;  // Feign
                    private final AccountClient accountClient;  // Feign

                    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
                    public void createOrder(OrderDTO dto) {
                        // 1. 创建订单
                        Order order = new Order();
                        order.setUserId(dto.getUserId());
                        order.setProductId(dto.getProductId());
                        order.setAmount(dto.getAmount());
                        orderMapper.insert(order);

                        // 2. 扣减库存 (远程调用)
                        storageClient.deduct(dto.getProductId(), dto.getQuantity());

                        // 3. 扣减账户余额 (远程调用)
                        accountClient.debit(dto.getUserId(), dto.getAmount());

                        // 任何一步失败，所有操作都会回滚
                    }
                }
                """);

        // 5. TCC 模式
        System.out.println("=".repeat(60));
        System.out.println("【5. TCC 模式】");
        System.out.println("""
                TCC = Try-Confirm-Cancel

                ┌─────────────────────────────────────────────────────────┐
                │  Try: 预留资源 (冻结库存)                               │
                │  Confirm: 确认操作 (扣减库存)                           │
                │  Cancel: 取消操作 (释放库存)                            │
                └─────────────────────────────────────────────────────────┘

                适用场景:
                - 需要强一致性
                - 数据库不支持 AT 模式

                实现示例:

                @LocalTCC
                public interface StorageService {

                    @TwoPhaseBusinessAction(name = "deductStorage",
                        commitMethod = "confirm", rollbackMethod = "cancel")
                    boolean tryDeduct(@BusinessActionContextParameter("productId") Long productId,
                                      @BusinessActionContextParameter("count") Integer count);

                    boolean confirm(BusinessActionContext context);

                    boolean cancel(BusinessActionContext context);
                }

                @Service
                public class StorageServiceImpl implements StorageService {

                    @Transactional
                    @Override
                    public boolean tryDeduct(Long productId, Integer count) {
                        // Try: 冻结库存
                        int rows = storageMapper.freeze(productId, count);
                        return rows > 0;
                    }

                    @Transactional
                    @Override
                    public boolean confirm(BusinessActionContext context) {
                        Long productId = context.getActionContext("productId");
                        Integer count = context.getActionContext("count");
                        // Confirm: 扣减冻结的库存
                        storageMapper.deductFrozen(productId, count);
                        return true;
                    }

                    @Transactional
                    @Override
                    public boolean cancel(BusinessActionContext context) {
                        Long productId = context.getActionContext("productId");
                        Integer count = context.getActionContext("count");
                        // Cancel: 释放冻结的库存
                        storageMapper.releaseFrozen(productId, count);
                        return true;
                    }
                }

                TCC 注意事项:
                - 需要处理空回滚 (Try 未执行就 Cancel)
                - 需要防悬挂 (Cancel 先于 Try 执行)
                - 需要幂等
                """);

        // 6. 最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【6. 分布式事务最佳实践】");
        System.out.println("""
                1. 优先避免分布式事务

                // 能用单库事务解决的不要用分布式事务
                // 考虑合并服务或调整边界

                2. 选择合适的模式

                ┌──────────────────┬──────────────────────────────────────┐
                │       场景       │             推荐模式                  │
                ├──────────────────┼──────────────────────────────────────┤
                │ 常规业务         │ AT 模式 (简单)                       │
                │ 高一致性要求     │ TCC / XA                             │
                │ 长事务           │ Saga                                 │
                │ 异步场景         │ 本地消息表 / 事务消息                │
                └──────────────────┴──────────────────────────────────────┘

                3. AT 模式优化

                // 避免热点数据
                // 尽量减少事务持有时间
                // 合理设置超时时间

                4. 降级方案

                @GlobalTransactional(rollbackFor = Exception.class)
                @SentinelResource(
                    value = "createOrder",
                    fallback = "createOrderFallback"
                )
                public void createOrder(OrderDTO dto) { ... }

                public void createOrderFallback(OrderDTO dto, Throwable t) {
                    // 降级处理: 放入队列稍后重试
                    messageProducer.sendRetryOrder(dto);
                }

                5. 监控告警

                // 监控全局事务成功率
                // 监控回滚次数
                // 监控事务超时
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Seata AT 模式无侵入，推荐使用");
        System.out.println("💡 每个服务数据库需要 undo_log 表");
        System.out.println("💡 优先考虑是否真的需要分布式事务");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 分布式事务问题:
 * - 跨服务数据一致性
 * 
 * 2. Seata 架构:
 * - TC/TM/RM
 * - AT/TCC/Saga/XA
 * 
 * 3. AT 模式:
 * - 自动补偿
 * - undo_log
 * 
 * 4. TCC 模式:
 * - Try/Confirm/Cancel
 * - 空回滚、防悬挂
 */
