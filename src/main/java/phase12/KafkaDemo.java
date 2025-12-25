package phase12;

/**
 * Phase 12 - Lesson 5: Kafka 消息队列
 * 
 * 🎯 学习目标:
 * 1. 理解 Kafka 核心概念
 * 2. 掌握 Kafka 生产者和消费者
 * 3. 了解 Kafka 与 RocketMQ 的区别
 */
public class KafkaDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 5: Kafka 消息队列");
        System.out.println("=".repeat(60));

        // 1. Kafka 简介
        System.out.println("\n【1. Kafka 简介】");
        System.out.println("""
                Kafka = 分布式流处理平台

                ┌─────────────────────────────────────────────────────────┐
                │                    Kafka 特点                           │
                ├─────────────────────────────────────────────────────────┤
                │  ✅ 高吞吐: 百万级消息/秒                               │
                │  ✅ 低延迟: 毫秒级                                      │
                │  ✅ 持久化: 消息存储在磁盘                              │
                │  ✅ 分布式: 水平扩展                                    │
                │  ✅ 流处理: Kafka Streams                               │
                └─────────────────────────────────────────────────────────┘

                适用场景:
                - 日志收集
                - 实时数据管道
                - 事件溯源
                - 流处理分析

                Kafka vs RocketMQ:
                ┌──────────────────┬────────────────┬────────────────────┐
                │       特性       │     Kafka      │     RocketMQ       │
                ├──────────────────┼────────────────┼────────────────────┤
                │ 吞吐量           │ 更高           │ 高                 │
                │ 延迟             │ 低             │ 更低               │
                │ 消息可靠性       │ 高             │ 更高               │
                │ 事务消息         │ 支持           │ 原生支持更好       │
                │ 消息顺序         │ 分区内有序     │ 支持全局有序       │
                │ 定时消息         │ 不原生支持     │ 原生支持           │
                │ 消息回溯         │ 支持           │ 支持               │
                │ 社区             │ 国际化活跃     │ 阿里维护           │
                └──────────────────┴────────────────┴────────────────────┘
                """);

        // 2. Kafka 架构
        System.out.println("=".repeat(60));
        System.out.println("【2. Kafka 架构】");
        System.out.println("""
                核心概念:

                ┌─────────────────────────────────────────────────────────┐
                │                      Kafka Cluster                       │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │                   Topic: orders                  │   │
                │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐        │   │
                │  │  │Partition0│ │Partition1│ │Partition2│        │   │
                │  │  │ [0,1,2] │ │ [0,1,2] │ │ [0,1,2] │        │   │
                │  │  │ Broker1  │ │ Broker2  │ │ Broker3  │        │   │
                │  │  └──────────┘ └──────────┘ └──────────┘        │   │
                │  └─────────────────────────────────────────────────┘   │
                │                         ▲                               │
                │         ┌───────────────┼───────────────┐              │
                │         │               │               │              │
                │    ┌─────────┐    ┌─────────┐    ┌─────────┐         │
                │    │Producer │    │Consumer1│    │Consumer2│         │
                │    └─────────┘    └─────────┘    └─────────┘         │
                │                   └───── Consumer Group ─────┘         │
                └─────────────────────────────────────────────────────────┘

                ┌──────────────────┬──────────────────────────────────────┐
                │       概念       │             说明                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ Broker           │ Kafka 服务节点                       │
                │ Topic            │ 消息的逻辑分类                       │
                │ Partition        │ 分区，Topic 的物理分片               │
                │ Replica          │ 副本，分区的备份                     │
                │ Leader/Follower  │ 主从副本，读写在 Leader              │
                │ Consumer Group   │ 消费者组，组内竞争消费               │
                │ Offset           │ 消息在分区内的偏移量                 │
                └──────────────────┴──────────────────────────────────────┘

                分区与消费者:
                - Partition 数 >= Consumer 数才能充分并行
                - 同一 Partition 只能被组内一个 Consumer 消费
                - 不同组可以重复消费
                """);

        // 3. Spring Boot 集成
        System.out.println("=".repeat(60));
        System.out.println("【3. Spring Boot 集成 Kafka】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>org.springframework.kafka</groupId>
                    <artifactId>spring-kafka</artifactId>
                </dependency>

                2. 配置

                spring:
                  kafka:
                    bootstrap-servers: localhost:9092
                    producer:
                      key-serializer: org.apache.kafka.common.serialization.StringSerializer
                      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
                      acks: all  # 等待所有副本确认
                      retries: 3
                    consumer:
                      group-id: my-group
                      auto-offset-reset: earliest  # 无偏移量时从头消费
                      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
                      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
                      properties:
                        spring.json.trusted.packages: "*"

                3. 生产者

                @Service
                @RequiredArgsConstructor
                public class OrderProducer {

                    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

                    public void send(OrderEvent event) {
                        // 发送消息
                        kafkaTemplate.send("order-topic", event.getOrderId(), event);
                    }

                    // 带回调
                    public void sendWithCallback(OrderEvent event) {
                        CompletableFuture<SendResult<String, OrderEvent>> future =
                            kafkaTemplate.send("order-topic", event.getOrderId(), event);

                        future.whenComplete((result, ex) -> {
                            if (ex == null) {
                                System.out.println("发送成功: " +
                                    result.getRecordMetadata().offset());
                            } else {
                                System.out.println("发送失败: " + ex.getMessage());
                            }
                        });
                    }

                    // 发送到指定分区
                    public void sendToPartition(OrderEvent event, int partition) {
                        kafkaTemplate.send("order-topic", partition, event.getOrderId(), event);
                    }
                }

                4. 消费者

                @Service
                public class OrderConsumer {

                    @KafkaListener(topics = "order-topic", groupId = "order-group")
                    public void consume(OrderEvent event) {
                        System.out.println("收到消息: " + event);
                        // 处理业务
                    }

                    // 获取元数据
                    @KafkaListener(topics = "order-topic", groupId = "order-group-2")
                    public void consumeWithMetadata(
                            @Payload OrderEvent event,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                            @Header(KafkaHeaders.OFFSET) long offset) {
                        System.out.println("分区: " + partition + ", 偏移量: " + offset);
                    }

                    // 批量消费
                    @KafkaListener(topics = "order-topic", groupId = "order-group-3",
                                   containerFactory = "batchFactory")
                    public void consumeBatch(List<OrderEvent> events) {
                        events.forEach(this::process);
                    }
                }
                """);

        // 4. 消息确认
        System.out.println("=".repeat(60));
        System.out.println("【4. 消息确认与偏移量】");
        System.out.println("""
                生产者确认 (acks):

                ┌────────────┬──────────────────────────────────────────┐
                │   acks     │             说明                          │
                ├────────────┼──────────────────────────────────────────┤
                │    0       │ 不等待确认，可能丢失                      │
                │    1       │ Leader 写入成功即确认                    │
                │   all/-1   │ 所有 ISR 副本写入成功                     │
                └────────────┴──────────────────────────────────────────┘

                消费者偏移量:

                自动提交 (默认):
                spring:
                  kafka:
                    consumer:
                      enable-auto-commit: true
                      auto-commit-interval: 5000  # 5秒

                手动提交:
                @KafkaListener(topics = "order-topic")
                public void consume(OrderEvent event, Acknowledgment ack) {
                    try {
                        process(event);
                        ack.acknowledge();  // 手动确认
                    } catch (Exception e) {
                        // 不确认，会重新消费
                    }
                }

                // 配置 AckMode
                spring:
                  kafka:
                    listener:
                      ack-mode: MANUAL_IMMEDIATE

                偏移量提交模式:
                ┌─────────────────────┬────────────────────────────────┐
                │      AckMode        │             说明                │
                ├─────────────────────┼────────────────────────────────┤
                │ RECORD              │ 每条消息后提交                  │
                │ BATCH               │ 批量消息后提交                  │
                │ MANUAL              │ 手动调用 ack，批处理后提交      │
                │ MANUAL_IMMEDIATE    │ 手动调用 ack，立即提交          │
                └─────────────────────┴────────────────────────────────┘
                """);

        // 5. 分区策略
        System.out.println("=".repeat(60));
        System.out.println("【5. 分区策略】");
        System.out.println("""
                生产者分区策略:

                1. 指定 Key: Hash(Key) % Partition数
                   相同 Key 总是发到同一分区 → 保证顺序

                2. 不指定 Key: 轮询 (Round Robin)

                3. 自定义分区器:
                public class OrderPartitioner implements Partitioner {
                    @Override
                    public int partition(String topic, Object key, byte[] keyBytes,
                                        Object value, byte[] valueBytes, Cluster cluster) {
                        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
                        String orderId = (String) key;
                        // 自定义逻辑
                        return Math.abs(orderId.hashCode()) % partitions.size();
                    }
                }

                // 配置
                spring:
                  kafka:
                    producer:
                      properties:
                        partitioner.class: com.example.OrderPartitioner

                消费者分区分配策略:
                ┌─────────────────┬───────────────────────────────────────┐
                │     策略        │             说明                       │
                ├─────────────────┼───────────────────────────────────────┤
                │ RangeAssignor   │ 按范围分配 (默认)                      │
                │ RoundRobin      │ 轮询分配                              │
                │ Sticky          │ 尽量保持原有分配                       │
                │ CooperativeSticky│ 协作式粘性分配                       │
                └─────────────────┴───────────────────────────────────────┘
                """);

        // 6. 顺序消费
        System.out.println("=".repeat(60));
        System.out.println("【6. 顺序消费保证】");
        System.out.println("""
                Kafka 只保证分区内有序

                实现全局有序:

                1. 单分区 (性能低)
                Topic 只设置 1 个分区

                2. 相同业务 Key 发到同一分区 (推荐)
                // 相同订单 ID 的消息发到同一分区
                kafkaTemplate.send("order-topic", orderId, event);

                3. 消费者单线程消费
                @Configuration
                public class KafkaConfig {
                    @Bean
                    public ConcurrentKafkaListenerContainerFactory<String, Object>
                            kafkaListenerContainerFactory() {
                        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                            new ConcurrentKafkaListenerContainerFactory<>();
                        factory.setConsumerFactory(consumerFactory());
                        factory.setConcurrency(1);  // 单线程
                        return factory;
                    }
                }

                顺序消费示例:
                ┌─────────────────────────────────────────────────────────┐
                │  订单操作: 创建 → 支付 → 发货 → 完成                    │
                │                                                         │
                │  Key = orderId                                          │
                │  Partition 0: [创建:001, 支付:001, 发货:001, 完成:001] │
                │  Partition 1: [创建:002, 支付:002, ...]                │
                │                                                         │
                │  同一订单的消息在同一分区，保证顺序                      │
                └─────────────────────────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Kafka 适合高吞吐日志收集和流处理");
        System.out.println("💡 分区是并行度单位，相同 Key 保证顺序");
        System.out.println("💡 acks=all 保证消息不丢失");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 核心概念:
 * - Topic/Partition
 * - Consumer Group
 * - Offset
 * 
 * 2. 消息确认:
 * - acks: 0/1/all
 * - 偏移量提交
 * 
 * 3. 分区策略:
 * - Key Hash
 * - 自定义分区器
 * 
 * 4. 顺序消费:
 * - 相同 Key 同一分区
 * - 分区内有序
 */
