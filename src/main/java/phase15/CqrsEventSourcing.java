package phase15;

/**
 * Phase 15: CQRS 与事件溯源
 * 
 * 本课程涵盖：
 * 1. CQRS 模式
 * 2. 事件溯源
 * 3. 实现示例
 * 4. 适用场景
 */
public class CqrsEventSourcing {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 15: CQRS 与事件溯源");
        System.out.println("=".repeat(60));

        cqrsPattern();
        eventSourcing();
        implementationExample();
        whenToUse();
    }

    private static void cqrsPattern() {
        System.out.println("\n【1. CQRS 模式】");
        System.out.println("-".repeat(50));

        String cqrs = """

                ═══════════════════════════════════════════════════════════
                            CQRS - 命令查询职责分离
                ═══════════════════════════════════════════════════════════

                CQRS = Command Query Responsibility Segregation

                核心思想：将读操作和写操作分离成不同的模型

                              ┌─────────────────────────────────┐
                              │            客户端                │
                              └───────────┬─────────────────────┘
                                          │
                            ┌─────────────┴─────────────┐
                            ↓                           ↓
                    ┌───────────────┐           ┌───────────────┐
                    │   Command     │           │     Query     │
                    │   写操作       │           │    读操作      │
                    └───────┬───────┘           └───────┬───────┘
                            ↓                           ↓
                    ┌───────────────┐           ┌───────────────┐
                    │  Write Model  │ ────────→ │  Read Model   │
                    │   写模型       │   同步    │   读模型       │
                    │  (Domain)     │           │   (DTO)       │
                    └───────────────┘           └───────────────┘
                            ↓                           ↓
                    ┌───────────────┐           ┌───────────────┐
                    │  PostgreSQL   │           │    Redis      │
                    │   主库        │           │   读库/缓存    │
                    └───────────────┘           └───────────────┘


                ═══════════════════════════════════════════════════════════
                                  Command（命令）
                ═══════════════════════════════════════════════════════════

                • 表达意图的动作
                • 祈使句命名：CreateOrder, UpdateProfile
                • 可能失败
                • 改变系统状态

                // 命令示例
                public record CreateOrderCommand(
                    String customerId,
                    List<OrderItem> items,
                    Address shippingAddress
                ) {}

                // 命令处理器
                public class CreateOrderCommandHandler {
                    private final OrderRepository repository;

                    public void handle(CreateOrderCommand command) {
                        Order order = Order.create(
                            CustomerId.of(command.customerId()),
                            command.items()
                        );
                        repository.save(order);
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  Query（查询）
                ═══════════════════════════════════════════════════════════

                • 返回数据，不改变状态
                • 可以使用优化的读模型
                • 直接访问读库

                // 查询示例
                public record GetOrderQuery(String orderId) {}

                // 查询处理器
                public class GetOrderQueryHandler {
                    private final OrderReadRepository readRepository;

                    public OrderDTO handle(GetOrderQuery query) {
                        return readRepository.findById(query.orderId())
                            .orElseThrow(() -> new OrderNotFoundException());
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  读写模型同步
                ═══════════════════════════════════════════════════════════

                同步方式：

                1. 同步更新
                ─────────────────────────────────────────────────────────
                • 写入时同步更新读模型
                • 简单，但耦合

                2. 异步更新（推荐）
                ─────────────────────────────────────────────────────────
                • 通过事件异步更新读模型
                • 松耦合，最终一致

                Command → Write Model → Event → Read Model
                """;
        System.out.println(cqrs);
    }

    private static void eventSourcing() {
        System.out.println("\n【2. 事件溯源 (Event Sourcing)】");
        System.out.println("-".repeat(50));

        String es = """

                ═══════════════════════════════════════════════════════════
                                  什么是事件溯源？
                ═══════════════════════════════════════════════════════════

                传统方式：存储当前状态
                ─────────────────────────────────────────────────────────
                ┌────────────────────────────────────────────────────────┐
                │ Order: { id: 1, status: "PAID", total: 100 }           │
                └────────────────────────────────────────────────────────┘

                ⚠️ 问题：丢失历史变化过程


                事件溯源：存储事件序列
                ─────────────────────────────────────────────────────────
                ┌────────────────────────────────────────────────────────┐
                │ Event 1: OrderCreated { orderId: 1, items: [...] }     │
                │ Event 2: ItemAdded { orderId: 1, productId: 100 }      │
                │ Event 3: PaymentReceived { orderId: 1, amount: 100 }   │
                └────────────────────────────────────────────────────────┘

                ✅ 优点：保留完整历史


                ═══════════════════════════════════════════════════════════
                                  核心概念
                ═══════════════════════════════════════════════════════════

                事件存储 (Event Store)
                ─────────────────────────────────────────────────────────
                • 按顺序存储所有事件
                • 只追加，不修改，不删除
                • 每个聚合一个事件流

                状态重建 (State Reconstruction)
                ─────────────────────────────────────────────────────────
                • 回放所有事件 → 得到当前状态
                • Order order = events.replay();

                快照 (Snapshot)
                ─────────────────────────────────────────────────────────
                • 定期保存状态快照
                • 避免每次回放所有事件
                • 从最近快照 + 后续事件重建


                ═══════════════════════════════════════════════════════════
                                  事件溯源流程
                ═══════════════════════════════════════════════════════════

                写入流程：
                1. 加载聚合（回放事件）
                2. 执行命令，产生新事件
                3. 保存新事件到事件存储

                读取流程：
                1. 获取聚合的所有事件
                2. 依次应用事件
                3. 得到当前状态


                ═══════════════════════════════════════════════════════════
                                  与 CQRS 结合
                ═══════════════════════════════════════════════════════════

                                      事件发布
                ┌─────────────┐     ┌─────────┐     ┌─────────────┐
                │   Command   │ ──→ │  Event  │ ──→ │   Query     │
                │   Handler   │     │  Store  │     │   Handler   │
                └──────┬──────┘     └────┬────┘     └──────┬──────┘
                       ↓                  ↓                 ↓
                ┌─────────────┐     ┌─────────┐     ┌─────────────┐
                │   Domain    │     │  Event  │     │   Read      │
                │   Model     │     │  Stream │     │   Model     │
                └─────────────┘     └─────────┘     └─────────────┘

                事件存储既是写模型的存储，也是读模型的数据源
                """;
        System.out.println(es);
    }

    private static void implementationExample() {
        System.out.println("\n【3. 实现示例】");
        System.out.println("-".repeat(50));

        String example = """

                ═══════════════════════════════════════════════════════════
                                  事件基类
                ═══════════════════════════════════════════════════════════

                public abstract class DomainEvent {
                    private final String eventId;
                    private final Instant occurredOn;
                    private final long version;

                    protected DomainEvent(long version) {
                        this.eventId = UUID.randomUUID().toString();
                        this.occurredOn = Instant.now();
                        this.version = version;
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  具体事件
                ═══════════════════════════════════════════════════════════

                public class OrderCreatedEvent extends DomainEvent {
                    private final String orderId;
                    private final String customerId;
                    private final List<OrderItemData> items;
                }

                public class OrderConfirmedEvent extends DomainEvent {
                    private final String orderId;
                    private final Instant confirmedAt;
                }

                public class OrderCancelledEvent extends DomainEvent {
                    private final String orderId;
                    private final String reason;
                }


                ═══════════════════════════════════════════════════════════
                                  事件溯源聚合
                ═══════════════════════════════════════════════════════════

                public class Order {
                    private OrderId id;
                    private OrderStatus status;
                    private List<OrderItem> items;

                    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
                    private long version = 0;

                    // 从事件重建
                    public static Order fromEvents(List<DomainEvent> events) {
                        Order order = new Order();
                        events.forEach(order::apply);
                        return order;
                    }

                    // 命令方法
                    public void create(CustomerId customerId, List<OrderItem> items) {
                        // 业务验证
                        if (items.isEmpty()) {
                            throw new IllegalArgumentException("订单不能为空");
                        }

                        // 发布事件（不直接修改状态）
                        raise(new OrderCreatedEvent(
                            OrderId.generate(),
                            customerId,
                            items,
                            ++version
                        ));
                    }

                    public void confirm() {
                        if (status != OrderStatus.CREATED) {
                            throw new IllegalStateException("只有新建订单可以确认");
                        }
                        raise(new OrderConfirmedEvent(id, Instant.now(), ++version));
                    }

                    // 事件应用（修改状态）
                    private void apply(DomainEvent event) {
                        switch (event) {
                            case OrderCreatedEvent e -> {
                                this.id = OrderId.of(e.getOrderId());
                                this.items = e.getItems().stream()
                                    .map(OrderItem::fromData)
                                    .toList();
                                this.status = OrderStatus.CREATED;
                            }
                            case OrderConfirmedEvent e -> {
                                this.status = OrderStatus.CONFIRMED;
                            }
                            case OrderCancelledEvent e -> {
                                this.status = OrderStatus.CANCELLED;
                            }
                            default -> {}
                        }
                        this.version = event.getVersion();
                    }

                    private void raise(DomainEvent event) {
                        apply(event);
                        uncommittedEvents.add(event);
                    }

                    public List<DomainEvent> getUncommittedEvents() {
                        return Collections.unmodifiableList(uncommittedEvents);
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  事件存储
                ═══════════════════════════════════════════════════════════

                public interface EventStore {
                    void save(String aggregateId, List<DomainEvent> events, long expectedVersion);
                    List<DomainEvent> load(String aggregateId);
                }

                public class OrderRepository {
                    private final EventStore eventStore;

                    public Order findById(OrderId id) {
                        List<DomainEvent> events = eventStore.load(id.value());
                        if (events.isEmpty()) {
                            throw new OrderNotFoundException();
                        }
                        return Order.fromEvents(events);
                    }

                    public void save(Order order) {
                        eventStore.save(
                            order.getId().value(),
                            order.getUncommittedEvents(),
                            order.getVersion()
                        );
                    }
                }
                """;
        System.out.println(example);
    }

    private static void whenToUse() {
        System.out.println("\n【4. 适用场景】");
        System.out.println("-".repeat(50));

        String whenToUse = """

                ═══════════════════════════════════════════════════════════
                                  CQRS 适用场景
                ═══════════════════════════════════════════════════════════

                ✅ 适合使用：
                • 读写比例差异大
                • 读写模型差异大
                • 需要独立扩展读写
                • 复杂的业务逻辑

                ❌ 不适合使用：
                • 简单 CRUD 应用
                • 读写对实时性要求高
                • 团队经验不足


                ═══════════════════════════════════════════════════════════
                                  事件溯源适用场景
                ═══════════════════════════════════════════════════════════

                ✅ 适合使用：
                • 需要审计追踪
                • 需要时间旅行（查看历史状态）
                • 复杂的业务状态转换
                • 金融、医疗等强监管领域

                ❌ 不适合使用：
                • 对删除有强需求（GDPR）
                • 状态简单
                • 团队不熟悉


                ═══════════════════════════════════════════════════════════
                                  框架推荐
                ═══════════════════════════════════════════════════════════

                Java 生态：
                • Axon Framework - 完整的 CQRS/ES 框架
                • Eventuate - 微服务事件驱动框架

                事件存储：
                • EventStoreDB - 专用事件存储
                • PostgreSQL + 事件表
                • Kafka


                ═══════════════════════════════════════════════════════════
                                  最佳实践
                ═══════════════════════════════════════════════════════════

                1. 从简单开始
                • 不必一开始就用完整 CQRS + ES
                • 可以先只做读写分离

                2. 事件设计很重要
                • 事件一旦发布就无法修改
                • 仔细设计事件结构
                • 考虑版本兼容

                3. 处理最终一致性
                • UI 层要能处理数据延迟
                • 考虑乐观 UI 更新
                """;
        System.out.println(whenToUse);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ CQRS 与事件溯源课程完成！");
        System.out.println("Phase 15 全部完成！");
        System.out.println("=".repeat(60));
    }
}
