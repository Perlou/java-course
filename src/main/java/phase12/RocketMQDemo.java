package phase12;

/**
 * Phase 12 - Lesson 4: RocketMQ 消息队列
 * 
 * 🎯 学习目标:
 * 1. 理解消息队列的作用
 * 2. 掌握 RocketMQ 核心概念
 * 3. 学会生产者和消费者开发
 */
public class RocketMQDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 4: RocketMQ 消息队列");
        System.out.println("=".repeat(60));

        // 1. 消息队列作用
        System.out.println("\n【1. 消息队列的作用】");
        System.out.println("""
                消息队列 = 异步通信中间件

                ┌─────────────────────────────────────────────────────────┐
                │  1. 异步解耦                                            │
                │  ┌─────────┐     ┌─────────┐     ┌─────────┐          │
                │  │ 订单服务│ ──▶ │   MQ    │ ──▶ │ 库存服务│          │
                │  └─────────┘     └─────────┘     └─────────┘          │
                │                        │                                │
                │                        ▼                                │
                │                  ┌─────────┐                           │
                │                  │积分服务 │                           │
                │                  └─────────┘                           │
                │  订单创建后不需要等待库存、积分处理完成                 │
                │                                                         │
                │  2. 削峰填谷                                            │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │ 请求 ─────▶ MQ 缓冲 ─────▶ 匀速消费 ─────▶ DB │   │
                │  │ 10万/s       队列          1000/s         处理  │   │
                │  └─────────────────────────────────────────────────┘   │
                │                                                         │
                │  3. 数据同步                                            │
                │  主数据库变更 ──▶ MQ ──▶ 缓存/ES/其他系统              │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                常见消息队列对比:
                ┌──────────────────┬──────────────────────────────────────┐
                │       MQ         │             特点                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ RocketMQ         │ 阿里开源，功能丰富，适合电商         │
                │ Kafka            │ 高吞吐，适合日志/大数据              │
                │ RabbitMQ         │ Erlang，功能全面，中小项目           │
                │ ActiveMQ         │ 老牌，性能一般                       │
                │ Pulsar           │ 新一代，存储计算分离                 │
                └──────────────────┴──────────────────────────────────────┘
                """);

        // 2. RocketMQ 架构
        System.out.println("=".repeat(60));
        System.out.println("【2. RocketMQ 架构】");
        System.out.println("""
                核心组件:

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  Producer ─────▶ Broker ◀───── Consumer                │
                │  (生产者)         (消息存储)     (消费者)               │
                │      │               │               │                  │
                │      │               │               │                  │
                │      └───────────────┼───────────────┘                  │
                │                      │                                  │
                │                 NameServer                              │
                │                 (注册中心)                               │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                核心概念:
                ┌──────────────────┬──────────────────────────────────────┐
                │       概念       │             说明                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ Topic            │ 消息主题，消息的逻辑分类             │
                │ Tag              │ 消息标签，用于过滤                   │
                │ Message Queue    │ 消息队列，Topic 下的分区             │
                │ Producer Group   │ 生产者组                             │
                │ Consumer Group   │ 消费者组，同组竞争消费               │
                │ Offset           │ 消费进度                             │
                └──────────────────┴──────────────────────────────────────┘

                消费模式:
                - 集群模式 (默认): 同一消费者组内竞争消费
                - 广播模式: 每个消费者都收到全部消息
                """);

        // 3. Spring Boot 集成
        System.out.println("=".repeat(60));
        System.out.println("【3. Spring Boot 集成 RocketMQ】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>org.apache.rocketmq</groupId>
                    <artifactId>rocketmq-spring-boot-starter</artifactId>
                    <version>2.2.3</version>
                </dependency>

                2. 配置

                rocketmq:
                  name-server: localhost:9876
                  producer:
                    group: order-producer-group
                    send-message-timeout: 3000
                    retry-times-when-send-failed: 2

                3. 发送消息

                @Service
                @RequiredArgsConstructor
                public class OrderMessageProducer {

                    private final RocketMQTemplate rocketMQTemplate;

                    // 同步发送
                    public void sendSync(OrderMessage message) {
                        SendResult result = rocketMQTemplate.syncSend(
                            "order-topic", message);
                        System.out.println("发送结果: " + result.getSendStatus());
                    }

                    // 异步发送
                    public void sendAsync(OrderMessage message) {
                        rocketMQTemplate.asyncSend("order-topic", message,
                            new SendCallback() {
                                @Override
                                public void onSuccess(SendResult result) {
                                    System.out.println("发送成功");
                                }

                                @Override
                                public void onException(Throwable e) {
                                    System.out.println("发送失败: " + e.getMessage());
                                }
                            });
                    }

                    // 单向发送 (不等待响应)
                    public void sendOneWay(OrderMessage message) {
                        rocketMQTemplate.sendOneWay("order-topic", message);
                    }
                }

                4. 消费消息

                @Service
                @RocketMQMessageListener(
                    topic = "order-topic",
                    consumerGroup = "order-consumer-group"
                )
                public class OrderMessageConsumer implements RocketMQListener<OrderMessage> {

                    @Override
                    public void onMessage(OrderMessage message) {
                        System.out.println("收到消息: " + message);
                        // 处理业务逻辑
                        processOrder(message);
                    }
                }
                """);

        // 4. 消息类型
        System.out.println("=".repeat(60));
        System.out.println("【4. 消息类型】");
        System.out.println("""
                1. 普通消息

                rocketMQTemplate.syncSend("topic", message);

                2. 延迟消息 (定时)

                // 延迟级别: 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                Message<OrderMessage> msg = MessageBuilder
                    .withPayload(message)
                    .build();
                rocketMQTemplate.syncSend("topic", msg, 3000, 3);  // level=3, 即10s后投递

                // 应用场景: 订单超时取消

                3. 顺序消息

                // 发送时指定队列选择器，相同 key 的消息发到同一队列
                rocketMQTemplate.syncSendOrderly("topic", message, "orderId:" + orderId);

                // 消费时设置消费模式
                @RocketMQMessageListener(
                    topic = "order-topic",
                    consumerGroup = "order-consumer-group",
                    consumeMode = ConsumeMode.ORDERLY  // 顺序消费
                )

                4. 事务消息

                ┌─────────────────────────────────────────────────────────┐
                │  1. 发送半消息 (Half Message)                           │
                │  2. 执行本地事务                                        │
                │  3. 根据事务结果提交/回滚消息                           │
                │                                                         │
                │  Producer         Broker        Consumer               │
                │     │               │               │                  │
                │     │──Half Msg────▶│               │                  │
                │     │◀──ACK────────│               │                  │
                │     │               │               │                  │
                │     │──Commit/     │               │                  │
                │     │  Rollback ──▶│               │                  │
                │     │               │──Deliver────▶│ (提交才投递)     │
                └─────────────────────────────────────────────────────────┘

                // 事务消息监听器
                @RocketMQTransactionListener
                public class OrderTransactionListener
                        implements RocketMQLocalTransactionListener {

                    @Override
                    public RocketMQLocalTransactionState executeLocalTransaction(
                            Message msg, Object arg) {
                        try {
                            // 执行本地事务
                            orderService.createOrder(order);
                            return RocketMQLocalTransactionState.COMMIT;
                        } catch (Exception e) {
                            return RocketMQLocalTransactionState.ROLLBACK;
                        }
                    }

                    @Override
                    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
                        // 回查本地事务状态
                        if (orderService.existsOrder(orderId)) {
                            return RocketMQLocalTransactionState.COMMIT;
                        }
                        return RocketMQLocalTransactionState.ROLLBACK;
                    }
                }
                """);

        // 5. 消息过滤
        System.out.println("=".repeat(60));
        System.out.println("【5. 消息过滤】");
        System.out.println("""
                1. Tag 过滤

                // 发送时指定 Tag
                rocketMQTemplate.syncSend("order-topic:CREATE", createMessage);
                rocketMQTemplate.syncSend("order-topic:PAY", payMessage);
                rocketMQTemplate.syncSend("order-topic:CANCEL", cancelMessage);

                // 消费时过滤
                @RocketMQMessageListener(
                    topic = "order-topic",
                    selectorExpression = "CREATE || PAY",  // 只消费 CREATE 和 PAY
                    consumerGroup = "order-consumer-group"
                )

                2. SQL 过滤 (需要 Broker 开启)

                // 发送时设置属性
                Message<OrderMessage> msg = MessageBuilder
                    .withPayload(message)
                    .setHeader("amount", 100)
                    .setHeader("region", "shanghai")
                    .build();

                // 消费时 SQL 过滤
                @RocketMQMessageListener(
                    topic = "order-topic",
                    selectorType = SelectorType.SQL92,
                    selectorExpression = "amount > 50 AND region = 'shanghai'"
                )
                """);

        // 6. 最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【6. RocketMQ 最佳实践】");
        System.out.println("""
                1. 消息幂等

                @Override
                public void onMessage(OrderMessage message) {
                    // 使用唯一键判断是否已处理
                    String key = "msg:" + message.getMsgId();
                    Boolean isNew = redis.setIfAbsent(key, "1", 24, TimeUnit.HOURS);
                    if (!Boolean.TRUE.equals(isNew)) {
                        return;  // 已处理过
                    }

                    try {
                        processOrder(message);
                    } catch (Exception e) {
                        redis.delete(key);  // 处理失败删除标记
                        throw e;
                    }
                }

                2. 消息重试

                // 默认重试 16 次
                // 消费失败返回 RECONSUME_LATER 触发重试

                3. 死信队列 (DLQ)

                // 重试次数用尽后进入死信队列
                // 死信 Topic: %DLQ%消费者组名
                // 需要单独消费处理

                4. 消息积压处理
                - 增加消费者实例
                - 提高消费线程数
                - 批量消费

                5. 消息大小
                - 单条消息不超过 4MB
                - 大消息考虑拆分或存储到 OSS

                6. Topic 设计
                - 一类业务一个 Topic
                - 用 Tag 区分子类型
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 RocketMQ 支持普通/延迟/顺序/事务消息");
        System.out.println("💡 消费者必须保证幂等性");
        System.out.println("💡 事务消息用于分布式事务场景");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 消息队列作用:
 * - 异步解耦
 * - 削峰填谷
 * - 数据同步
 * 
 * 2. RocketMQ 概念:
 * - Topic/Tag
 * - Producer/Consumer Group
 * - Message Queue
 * 
 * 3. 消息类型:
 * - 普通、延迟、顺序、事务
 * 
 * 4. 最佳实践:
 * - 幂等消费
 * - 重试机制
 * - 死信队列
 */
