package phase12;

/**
 * Phase 12 - Lesson 6: 消息可靠性
 * 
 * 🎯 学习目标:
 * 1. 理解消息丢失的场景
 * 2. 掌握消息可靠性保证方案
 * 3. 学会幂等性和重试设计
 */
public class MessageReliability {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 6: 消息可靠性保证");
        System.out.println("=".repeat(60));

        // 1. 消息丢失场景
        System.out.println("\n【1. 消息丢失的三个环节】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │  Producer ──────▶ Broker ──────▶ Consumer              │
                │     ①               ②              ③                   │
                │                                                         │
                │  ① 生产者丢失:                                         │
                │     - 网络故障                                         │
                │     - 发送超时                                         │
                │     - 生产者宕机                                       │
                │                                                         │
                │  ② Broker 丢失:                                        │
                │     - 异步刷盘时宕机                                   │
                │     - 主从切换丢失                                     │
                │     - 磁盘损坏                                         │
                │                                                         │
                │  ③ 消费者丢失:                                         │
                │     - 自动确认后处理失败                               │
                │     - 消费者宕机                                       │
                │     - 处理异常                                         │
                │                                                         │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 生产者可靠性
        System.out.println("=".repeat(60));
        System.out.println("【2. 生产者可靠性保证】");
        System.out.println("""
                1. 同步发送 + 重试

                // RocketMQ
                SendResult result = producer.send(message);
                if (result.getSendStatus() != SendStatus.SEND_OK) {
                    // 重试或处理失败
                }

                // Kafka
                spring:
                  kafka:
                    producer:
                      acks: all          # 等待所有副本确认
                      retries: 3         # 重试次数
                      retry-backoff-ms: 1000

                2. 本地消息表

                ┌─────────────────────────────────────────────────────────┐
                │  1. 业务操作 + 消息入表 (同一事务)                      │
                │  2. 定时任务扫描未发送的消息                            │
                │  3. 发送消息并更新状态                                  │
                │  4. 发送成功删除记录                                    │
                │                                                         │
                │  ┌──────────┐     ┌──────────┐     ┌──────────┐       │
                │  │ 业务表   │     │ 消息表   │     │   MQ     │       │
                │  └──────────┘     └──────────┘     └──────────┘       │
                │       ↑               ↑                 ↑              │
                │       └───事务1───────┘                 │              │
                │                       └────定时任务─────┘              │
                └─────────────────────────────────────────────────────────┘

                @Transactional
                public void createOrder(Order order) {
                    // 1. 保存订单
                    orderMapper.insert(order);

                    // 2. 保存消息记录 (同一事务)
                    MessageRecord record = new MessageRecord();
                    record.setTopic("order-topic");
                    record.setContent(JSON.toJSONString(order));
                    record.setStatus(0);  // 待发送
                    messageRecordMapper.insert(record);
                }

                // 定时任务发送
                @Scheduled(fixedRate = 5000)
                public void sendPendingMessages() {
                    List<MessageRecord> records = messageRecordMapper.findPending();
                    for (MessageRecord record : records) {
                        try {
                            producer.send(record.getContent());
                            messageRecordMapper.updateStatus(record.getId(), 1);  // 已发送
                        } catch (Exception e) {
                            // 重试次数++，超过阈值报警
                        }
                    }
                }

                3. 事务消息 (RocketMQ)
                见 RocketMQDemo.java
                """);

        // 3. Broker 可靠性
        System.out.println("=".repeat(60));
        System.out.println("【3. Broker 可靠性保证】");
        System.out.println("""
                1. 同步刷盘

                RocketMQ:
                flushDiskType = SYNC_FLUSH  # 同步刷盘

                Kafka:
                flush.messages = 1  # 每条消息刷盘 (性能低)

                2. 多副本

                RocketMQ:
                - 主从同步: brokerRole = SYNC_MASTER

                Kafka:
                - min.insync.replicas = 2
                - acks = all

                3. 高可用集群

                ┌─────────────────────────────────────────────────────────┐
                │  RocketMQ:                                              │
                │  ┌─────────┐ ┌─────────┐                              │
                │  │ Master1 │ │ Master2 │                              │
                │  │ Slave1  │ │ Slave2  │                              │
                │  └─────────┘ └─────────┘                              │
                │                                                         │
                │  Kafka:                                                 │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │ Partition0: Broker1(L) Broker2(F) Broker3(F)   │   │
                │  │ Partition1: Broker2(L) Broker3(F) Broker1(F)   │   │
                │  │ Partition2: Broker3(L) Broker1(F) Broker2(F)   │   │
                │  └─────────────────────────────────────────────────┘   │
                └─────────────────────────────────────────────────────────┘
                """);

        // 4. 消费者可靠性
        System.out.println("=".repeat(60));
        System.out.println("【4. 消费者可靠性保证】");
        System.out.println("""
                1. 手动确认

                // RocketMQ - 返回状态控制
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    try {
                        process(msgs);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;  // 重试
                    }
                }

                // Kafka - 手动提交偏移量
                @KafkaListener(topics = "order-topic")
                public void consume(OrderEvent event, Acknowledgment ack) {
                    try {
                        process(event);
                        ack.acknowledge();  // 成功后确认
                    } catch (Exception e) {
                        // 不确认，会重新投递
                    }
                }

                2. 重试机制

                RocketMQ 重试策略:
                ┌──────────┬────────────────────────────────────────────┐
                │ 重试次数  │ 延迟时间                                   │
                ├──────────┼────────────────────────────────────────────┤
                │ 1        │ 10s                                        │
                │ 2        │ 30s                                        │
                │ 3        │ 1m                                         │
                │ ...      │ 逐渐增加                                   │
                │ 16       │ 2h (最大)                                  │
                └──────────┴────────────────────────────────────────────┘

                3. 死信队列

                消息重试次数用尽后进入死信队列:
                - RocketMQ: %DLQ%消费者组
                - Kafka: 需要自己实现

                // 监控死信队列，人工处理
                @RocketMQMessageListener(
                    topic = "%DLQ%order-consumer-group",
                    consumerGroup = "dlq-consumer"
                )
                public class DLQConsumer { ... }
                """);

        // 5. 幂等性设计
        System.out.println("=".repeat(60));
        System.out.println("【5. 消费幂等性】");
        System.out.println("""
                幂等性 = 多次执行结果相同

                为什么需要幂等:
                ┌─────────────────────────────────────────────────────────┐
                │  消息可能重复消费:                                      │
                │  - 生产者重试 (网络超时但已发送成功)                    │
                │  - 消费者处理成功但确认失败                             │
                │  - 消费者 Rebalance                                     │
                └─────────────────────────────────────────────────────────┘

                幂等方案:

                1. 唯一消息 ID + 去重表

                @Override
                public void onMessage(OrderEvent event) {
                    String msgId = event.getMsgId();

                    // 使用 Redis 去重
                    Boolean isNew = redis.setIfAbsent(
                        "msg:" + msgId, "1", 24, TimeUnit.HOURS);

                    if (!Boolean.TRUE.equals(isNew)) {
                        log.info("消息已处理: {}", msgId);
                        return;
                    }

                    try {
                        processOrder(event);
                    } catch (Exception e) {
                        redis.delete("msg:" + msgId);  // 处理失败删除标记
                        throw e;
                    }
                }

                2. 业务唯一键去重

                @Transactional
                public void createOrder(OrderEvent event) {
                    // 先查询订单是否存在
                    Order existing = orderMapper.selectByOrderNo(event.getOrderNo());
                    if (existing != null) {
                        log.info("订单已存在: {}", event.getOrderNo());
                        return;
                    }

                    // 创建订单
                    orderMapper.insert(order);
                }

                3. 数据库唯一索引

                CREATE UNIQUE INDEX uk_order_no ON orders(order_no);

                // 插入重复时会抛异常，捕获后忽略
                try {
                    orderMapper.insert(order);
                } catch (DuplicateKeyException e) {
                    log.info("订单已存在");
                }

                4. 状态机校验

                @Transactional
                public void payOrder(Long orderId) {
                    // 只有待支付状态才能支付
                    int rows = orderMapper.updateStatus(
                        orderId, OrderStatus.PAID, OrderStatus.PENDING);

                    if (rows == 0) {
                        log.info("订单状态不正确，忽略");
                        return;
                    }

                    // 继续处理...
                }
                """);

        // 6. 消息顺序性
        System.out.println("=".repeat(60));
        System.out.println("【6. 消息顺序性保证】");
        System.out.println("""
                场景: 订单状态变更必须按顺序: 创建 → 支付 → 发货 → 完成

                方案 1: 顺序消息 (RocketMQ)

                // 发送: 相同订单发到同一队列
                producer.send(msg, (mqs, msg1, arg) -> {
                    Long orderId = (Long) arg;
                    int index = (int) (orderId % mqs.size());
                    return mqs.get(index);
                }, orderId);

                // 消费: 顺序消费模式
                consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                    // 单线程顺序消费
                    return ConsumeOrderlyStatus.SUCCESS;
                });

                方案 2: 分区有序 (Kafka)

                // 相同 Key 发到同一分区
                kafkaTemplate.send("order-topic", orderId.toString(), event);

                方案 3: 应用层保证

                // 消费时校验顺序
                @Override
                public void onMessage(OrderEvent event) {
                    Order order = orderMapper.selectById(event.getOrderId());

                    // 校验状态顺序
                    if (!isValidTransition(order.getStatus(), event.getNewStatus())) {
                        // 状态不对，等待重试或人工处理
                        throw new BusinessException("状态顺序错误");
                    }

                    updateOrderStatus(event);
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 生产者: 同步发送 + 重试 + 本地消息表");
        System.out.println("💡 Broker: 同步刷盘 + 多副本");
        System.out.println("💡 消费者: 手动确认 + 幂等设计");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 消息丢失:
 * - 生产者、Broker、消费者
 * 
 * 2. 可靠性保证:
 * - 同步发送、重试
 * - 同步刷盘、多副本
 * - 手动确认
 * 
 * 3. 幂等性:
 * - 唯一 ID 去重
 * - 业务唯一键
 * - 状态机校验
 * 
 * 4. 顺序性:
 * - 相同 Key 同一队列
 * - 顺序消费模式
 */
