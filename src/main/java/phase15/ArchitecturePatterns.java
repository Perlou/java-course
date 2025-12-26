package phase15;

/**
 * Phase 15: 架构模式
 * 
 * 本课程涵盖：
 * 1. 分层架构
 * 2. 六边形架构
 * 3. 事件驱动架构
 * 4. 架构演进
 */
public class ArchitecturePatterns {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 15: 架构模式");
        System.out.println("=".repeat(60));

        layeredArchitecture();
        hexagonalArchitecture();
        eventDrivenArchitecture();
        architectureEvolution();
    }

    private static void layeredArchitecture() {
        System.out.println("\n【1. 分层架构】");
        System.out.println("-".repeat(50));

        String layered = """

                ═══════════════════════════════════════════════════════════
                                  经典三层/四层架构
                ═══════════════════════════════════════════════════════════

                ┌─────────────────────────────────────────────────────────┐
                │                    表现层 (Presentation)                 │
                │              Controller, View, REST API                 │
                ├─────────────────────────────────────────────────────────┤
                │                    业务层 (Business)                     │
                │                 Service, Domain Logic                   │
                ├─────────────────────────────────────────────────────────┤
                │                    持久层 (Persistence)                  │
                │                 Repository, DAO, Mapper                 │
                ├─────────────────────────────────────────────────────────┤
                │                    数据层 (Database)                     │
                │                   MySQL, Redis, MQ                      │
                └─────────────────────────────────────────────────────────┘

                依赖方向：上层依赖下层（Controller → Service → Repository）


                ═══════════════════════════════════════════════════════════
                                  代码结构示例
                ═══════════════════════════════════════════════════════════

                src/main/java/com/example/
                ├── controller/           # 表现层
                │   └── OrderController.java
                ├── service/              # 业务层
                │   ├── OrderService.java (接口)
                │   └── impl/
                │       └── OrderServiceImpl.java
                ├── repository/           # 持久层
                │   └── OrderRepository.java
                ├── entity/               # 实体
                │   └── Order.java
                └── dto/                  # 数据传输对象
                    └── OrderDTO.java


                ═══════════════════════════════════════════════════════════
                                  优缺点
                ═══════════════════════════════════════════════════════════

                ✅ 优点：
                • 结构简单，易于理解
                • 职责分明，分工明确
                • 适合中小型项目

                ❌ 缺点：
                • 业务逻辑容易泄露到其他层
                • 过度依赖数据库模型
                • 难以进行单元测试
                • 倾向于过程式编程
                """;
        System.out.println(layered);
    }

    private static void hexagonalArchitecture() {
        System.out.println("\n【2. 六边形架构（端口与适配器）】");
        System.out.println("-".repeat(50));

        String hexagonal = """

                ═══════════════════════════════════════════════════════════
                                  六边形架构
                ═══════════════════════════════════════════════════════════

                         适配器                              适配器
                    ┌───────────┐                        ┌───────────┐
                    │   REST    │                        │   MySQL   │
                    │   API     │                        │  Adapter  │
                    └─────┬─────┘                        └─────┬─────┘
                          │                                    │
                          ↓                                    ↓
                    ┌───────────┐    ┌──────────────┐    ┌───────────┐
                    │  入站端口  │ →  │              │ →  │  出站端口  │
                    │ (接口)     │    │  领域核心    │    │  (接口)   │
                    └───────────┘    │  Domain      │    └───────────┘
                          ↑          │  Core        │          ↑
                          │          └──────────────┘          │
                    ┌─────┴─────┐                        ┌─────┴─────┐
                    │   gRPC    │                        │   Redis   │
                    │  Adapter  │                        │  Adapter  │
                    └───────────┘                        └───────────┘


                ═══════════════════════════════════════════════════════════
                                  核心概念
                ═══════════════════════════════════════════════════════════

                领域核心 (Domain Core)
                ─────────────────────────────────────────────────────────
                • 业务逻辑的核心
                • 不依赖任何外部技术
                • 纯 Java 代码，无框架依赖

                端口 (Port)
                ─────────────────────────────────────────────────────────
                • 定义领域与外部交互的接口
                • 入站端口：外部调用领域（Use Case）
                • 出站端口：领域调用外部（Repository）

                适配器 (Adapter)
                ─────────────────────────────────────────────────────────
                • 端口的具体实现
                • 入站适配器：REST Controller, gRPC
                • 出站适配器：JPA Repository, Redis Client


                ═══════════════════════════════════════════════════════════
                                  代码结构示例
                ═══════════════════════════════════════════════════════════

                src/main/java/com/example/
                ├── domain/                    # 领域核心（不依赖外部）
                │   ├── model/
                │   │   ├── Order.java         # 聚合根
                │   │   └── OrderItem.java     # 实体
                │   ├── port/
                │   │   ├── in/                # 入站端口
                │   │   │   └── CreateOrderUseCase.java
                │   │   └── out/               # 出站端口
                │   │       └── OrderRepository.java
                │   └── service/
                │       └── OrderService.java  # 领域服务
                │
                ├── application/               # 应用层
                │   └── CreateOrderUseCaseImpl.java
                │
                └── adapter/                   # 适配器
                    ├── in/                    # 入站适配器
                    │   └── web/
                    │       └── OrderController.java
                    └── out/                   # 出站适配器
                        └── persistence/
                            └── OrderJpaAdapter.java


                ═══════════════════════════════════════════════════════════
                                  关键代码示例
                ═══════════════════════════════════════════════════════════

                // 出站端口（领域定义）
                public interface OrderRepository {
                    void save(Order order);
                    Optional<Order> findById(OrderId id);
                }

                // 出站适配器（基础设施实现）
                @Repository
                public class OrderJpaAdapter implements OrderRepository {
                    private final OrderJpaRepository jpaRepository;

                    @Override
                    public void save(Order order) {
                        jpaRepository.save(toEntity(order));
                    }
                }

                // 领域服务（纯业务逻辑）
                public class OrderService {
                    private final OrderRepository repository;  // 依赖接口

                    public void createOrder(Order order) {
                        order.validate();
                        repository.save(order);
                    }
                }
                """;
        System.out.println(hexagonal);
    }

    private static void eventDrivenArchitecture() {
        System.out.println("\n【3. 事件驱动架构】");
        System.out.println("-".repeat(50));

        String eda = """

                ═══════════════════════════════════════════════════════════
                                  事件驱动架构 (EDA)
                ═══════════════════════════════════════════════════════════

                    生产者              事件总线              消费者

                  ┌─────────┐       ┌───────────────┐       ┌─────────┐
                  │ 订单服务 │ ────→ │               │ ────→ │ 库存服务 │
                  └─────────┘       │   Event Bus   │       └─────────┘
                                    │  (Kafka/RMQ)  │
                  ┌─────────┐       │               │       ┌─────────┐
                  │ 支付服务 │ ────→ │               │ ────→ │ 通知服务 │
                  └─────────┘       └───────────────┘       └─────────┘


                ═══════════════════════════════════════════════════════════
                                  事件类型
                ═══════════════════════════════════════════════════════════

                领域事件 (Domain Event)
                ─────────────────────────────────────────────────────────
                • 领域内发生的业务事件
                • OrderCreated, PaymentCompleted, ItemShipped
                • 表示已经发生的事实（过去式命名）

                集成事件 (Integration Event)
                ─────────────────────────────────────────────────────────
                • 跨服务/限界上下文的事件
                • 用于服务间通信
                • 需要序列化和版本兼容


                ═══════════════════════════════════════════════════════════
                                  事件定义示例
                ═══════════════════════════════════════════════════════════

                // 领域事件基类
                public abstract class DomainEvent {
                    private final String eventId;
                    private final Instant occurredOn;

                    protected DomainEvent() {
                        this.eventId = UUID.randomUUID().toString();
                        this.occurredOn = Instant.now();
                    }
                }

                // 具体事件
                public class OrderCreatedEvent extends DomainEvent {
                    private final String orderId;
                    private final String customerId;
                    private final BigDecimal totalAmount;
                    private final List<OrderItem> items;

                    // 构造函数、getter...
                }


                ═══════════════════════════════════════════════════════════
                                  事件处理模式
                ═══════════════════════════════════════════════════════════

                1. 发布-订阅 (Pub/Sub)
                ─────────────────────────────────────────────────────────
                • 一个事件可被多个消费者处理
                • 松耦合

                2. 事件溯源 (Event Sourcing)
                ─────────────────────────────────────────────────────────
                • 状态通过事件序列重建
                • 完整历史记录

                3. Saga 模式
                ─────────────────────────────────────────────────────────
                • 分布式事务协调
                • 补偿事务


                ═══════════════════════════════════════════════════════════
                                  优缺点
                ═══════════════════════════════════════════════════════════

                ✅ 优点：
                • 松耦合：服务间不直接依赖
                • 可扩展：独立扩展消费者
                • 异步处理：高吞吐量
                • 弹性：消费者失败不影响生产者

                ❌ 缺点：
                • 最终一致性：不是实时同步
                • 调试困难：追踪事件链
                • 事件顺序：需要特殊处理
                • 复杂度：需要额外基础设施
                """;
        System.out.println(eda);
    }

    private static void architectureEvolution() {
        System.out.println("\n【4. 架构演进】");
        System.out.println("-".repeat(50));

        String evolution = """

                ═══════════════════════════════════════════════════════════
                                  架构演进路径
                ═══════════════════════════════════════════════════════════

                阶段1: 单体架构
                ─────────────────────────────────────────────────────────
                • 适合：初创项目、MVP
                • 特点：简单、快速开发、易部署
                • 挑战：代码膨胀、扩展困难

                阶段2: 模块化单体
                ─────────────────────────────────────────────────────────
                • 适合：中型项目
                • 特点：模块化设计、清晰边界
                • 为微服务做准备

                阶段3: 微服务架构
                ─────────────────────────────────────────────────────────
                • 适合：大型项目、多团队
                • 特点：独立部署、技术异构
                • 挑战：分布式复杂度


                ═══════════════════════════════════════════════════════════
                                  架构选型建议
                ═══════════════════════════════════════════════════════════

                小型项目 (< 5人团队)
                ─────────────────────────────────────────────────────────
                • 推荐：分层架构 / 单体
                • 快速迭代
                • 避免过度设计

                中型项目 (5-20人团队)
                ─────────────────────────────────────────────────────────
                • 推荐：模块化单体 / 六边形架构
                • 清晰边界
                • 预留拆分可能

                大型项目 (> 20人团队)
                ─────────────────────────────────────────────────────────
                • 推荐：微服务 + 事件驱动
                • 多团队并行开发
                • 注意服务治理


                💡 核心原则：从简单开始，按需演进
                """;
        System.out.println(evolution);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 架构模式课程完成！");
        System.out.println("下一课: DomainDrivenDesign.java");
        System.out.println("=".repeat(60));
    }
}
