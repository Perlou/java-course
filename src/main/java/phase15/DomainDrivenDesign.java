package phase15;

/**
 * Phase 15: 领域驱动设计 (DDD)
 * 
 * 本课程涵盖：
 * 1. DDD 战略设计
 * 2. DDD 战术设计
 * 3. 实体与值对象
 * 4. 聚合设计
 */
public class DomainDrivenDesign {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 15: 领域驱动设计 (DDD)");
        System.out.println("=".repeat(60));

        dddOverview();
        strategicDesign();
        tacticalDesign();
        aggregateDesign();
        dddInPractice();
    }

    private static void dddOverview() {
        System.out.println("\n【1. DDD 概述】");
        System.out.println("-".repeat(50));

        String overview = """

                ═══════════════════════════════════════════════════════════
                                  什么是 DDD？
                ═══════════════════════════════════════════════════════════

                领域驱动设计 (Domain-Driven Design) 是一种软件开发方法论，
                核心思想是将业务领域的复杂性转化为软件模型。

                核心理念：
                • 业务专家与开发人员紧密合作
                • 使用统一语言 (Ubiquitous Language)
                • 领域模型驱动设计和开发


                ═══════════════════════════════════════════════════════════
                                  DDD 两大部分
                ═══════════════════════════════════════════════════════════

                战略设计 (Strategic Design)
                ─────────────────────────────────────────────────────────
                • 关注：系统整体结构
                • 核心概念：限界上下文、上下文映射、子域
                • 解决：如何划分系统边界

                战术设计 (Tactical Design)
                ─────────────────────────────────────────────────────────
                • 关注：领域模型内部设计
                • 核心概念：实体、值对象、聚合、领域服务
                • 解决：如何实现领域模型


                ═══════════════════════════════════════════════════════════
                                  统一语言
                ═══════════════════════════════════════════════════════════

                统一语言 (Ubiquitous Language)：
                • 业务专家和开发人员共同使用的语言
                • 在代码、文档、对话中一致使用
                • 避免翻译带来的信息丢失

                ❌ 错误示例：
                • 业务说"客户"，代码写"User"
                • 业务说"下单"，代码写"createRecord"

                ✅ 正确示例：
                • 业务说"会员"，代码写"Member"
                • 业务说"结算购物车"，代码写"checkout()"
                """;
        System.out.println(overview);
    }

    private static void strategicDesign() {
        System.out.println("\n【2. 战略设计】");
        System.out.println("-".repeat(50));

        String strategic = """

                ═══════════════════════════════════════════════════════════
                                  限界上下文 (Bounded Context)
                ═══════════════════════════════════════════════════════════

                限界上下文是一个明确的边界，在这个边界内，
                领域模型和统一语言保持一致。

                电商系统示例：

                ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
                │    订单上下文    │  │    商品上下文    │  │    用户上下文    │
                │                 │  │                 │  │                 │
                │  Order          │  │  Product        │  │  User           │
                │  OrderItem      │  │  SKU            │  │  Address        │
                │  Payment        │  │  Category       │  │  Account        │
                │                 │  │                 │  │                 │
                │  "Order 指订单" │  │  "Order 指排序" │  │  "Member 指会员"│
                └─────────────────┘  └─────────────────┘  └─────────────────┘

                ⚠️ 同一个词在不同上下文可能有不同含义！


                ═══════════════════════════════════════════════════════════
                                  子域 (Subdomain)
                ═══════════════════════════════════════════════════════════

                核心域 (Core Domain)
                ─────────────────────────────────────────────────────────
                • 业务的核心竞争力
                • 投入最多资源
                • 例：电商的商品推荐算法

                支撑域 (Supporting Subdomain)
                ─────────────────────────────────────────────────────────
                • 支撑核心业务
                • 可以外包或购买
                • 例：电商的物流跟踪

                通用域 (Generic Subdomain)
                ─────────────────────────────────────────────────────────
                • 非业务特定的通用功能
                • 使用现成方案
                • 例：用户认证、通知服务


                ═══════════════════════════════════════════════════════════
                                  上下文映射 (Context Map)
                ═══════════════════════════════════════════════════════════

                上下文之间的关系：

                共享内核 (Shared Kernel)
                • 两个上下文共享一部分模型
                • 需要紧密协调

                客户-供应商 (Customer-Supplier)
                • 上游提供接口，下游消费
                • 下游可以影响上游开发

                防腐层 (Anti-Corruption Layer, ACL)
                • 隔离外部系统的影响
                • 翻译外部模型为内部模型

                开放主机服务 (Open Host Service)
                • 提供公共协议供多方使用
                • REST API, gRPC 等
                """;
        System.out.println(strategic);
    }

    private static void tacticalDesign() {
        System.out.println("\n【3. 战术设计】");
        System.out.println("-".repeat(50));

        String tactical = """

                ═══════════════════════════════════════════════════════════
                                  实体 (Entity)
                ═══════════════════════════════════════════════════════════

                特征：
                • 有唯一标识 (ID)
                • 生命周期内可变
                • 通过 ID 判断相等性

                // 实体示例
                public class Order {
                    private final OrderId id;        // 唯一标识
                    private OrderStatus status;       // 可变状态
                    private List<OrderItem> items;

                    @Override
                    public boolean equals(Object o) {
                        if (o instanceof Order other) {
                            return this.id.equals(other.id);  // 按 ID 判等
                        }
                        return false;
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  值对象 (Value Object)
                ═══════════════════════════════════════════════════════════

                特征：
                • 无唯一标识
                • 不可变 (Immutable)
                • 通过属性值判断相等性

                // 值对象示例
                public record Money(BigDecimal amount, Currency currency) {

                    public Money add(Money other) {
                        if (!this.currency.equals(other.currency)) {
                            throw new IllegalArgumentException("货币不一致");
                        }
                        return new Money(this.amount.add(other.amount), currency);
                    }
                }

                public record Address(
                    String province,
                    String city,
                    String district,
                    String street
                ) {
                    public String fullAddress() {
                        return province + city + district + street;
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  领域服务 (Domain Service)
                ═══════════════════════════════════════════════════════════

                使用场景：
                • 业务逻辑不属于任何实体
                • 涉及多个实体的操作
                • 无状态

                // 领域服务示例
                public class TransferService {

                    public void transfer(Account from, Account to, Money amount) {
                        // 涉及两个账户，不适合放在任一实体中
                        from.withdraw(amount);
                        to.deposit(amount);
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  领域事件 (Domain Event)
                ═══════════════════════════════════════════════════════════

                特征：
                • 表示领域中发生的事件
                • 过去式命名（已经发生）
                • 不可变

                // 领域事件示例
                public class OrderCreatedEvent {
                    private final String orderId;
                    private final String customerId;
                    private final Instant occurredOn;
                    private final List<ItemInfo> items;

                    // 构造函数、getter...
                }
                """;
        System.out.println(tactical);
    }

    private static void aggregateDesign() {
        System.out.println("\n【4. 聚合设计】");
        System.out.println("-".repeat(50));

        String aggregate = """

                ═══════════════════════════════════════════════════════════
                                  聚合 (Aggregate)
                ═══════════════════════════════════════════════════════════

                聚合是一组相关对象的集合，作为数据修改的单元。

                ┌─────────────────────────────────────────────────────────┐
                │                      Order (聚合根)                      │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │                                                 │   │
                │  │  orderId                                        │   │
                │  │  status                                         │   │
                │  │  ┌─────────────┐  ┌─────────────┐              │   │
                │  │  │ OrderItem 1 │  │ OrderItem 2 │  ...        │   │
                │  │  └─────────────┘  └─────────────┘              │   │
                │  │                                                 │   │
                │  │  totalAmount (值对象)                           │   │
                │  │  shippingAddress (值对象)                       │   │
                │  │                                                 │   │
                │  └─────────────────────────────────────────────────┘   │
                │                                                         │
                │  外部只能通过 Order（聚合根）访问内部对象                │
                └─────────────────────────────────────────────────────────┘


                ═══════════════════════════════════════════════════════════
                                  聚合设计原则
                ═══════════════════════════════════════════════════════════

                1. 聚合根是唯一入口
                ─────────────────────────────────────────────────────────
                • 外部只能引用聚合根
                • 不能直接访问内部实体

                ❌ orderItem.setQuantity(5);
                ✅ order.updateItemQuantity(itemId, 5);

                2. 聚合内保证一致性
                ─────────────────────────────────────────────────────────
                • 聚合内的修改是原子的
                • 一个事务只修改一个聚合

                3. 聚合间通过 ID 引用
                ─────────────────────────────────────────────────────────
                • 不持有其他聚合的对象引用
                • 通过 ID 引用

                ❌ private Order order;
                ✅ private OrderId orderId;

                4. 保持聚合小巧
                ─────────────────────────────────────────────────────────
                • 只包含真正需要一起变化的对象
                • 大聚合影响性能和并发


                ═══════════════════════════════════════════════════════════
                                  聚合根代码示例
                ═══════════════════════════════════════════════════════════

                public class Order {
                    private final OrderId id;
                    private OrderStatus status;
                    private final CustomerId customerId;  // 引用其他聚合的 ID
                    private List<OrderItem> items;
                    private Money totalAmount;

                    // 工厂方法
                    public static Order create(CustomerId customerId, List<OrderItem> items) {
                        Order order = new Order(OrderId.generate(), customerId);
                        items.forEach(order::addItem);
                        order.status = OrderStatus.CREATED;
                        order.registerEvent(new OrderCreatedEvent(order));
                        return order;
                    }

                    // 业务方法
                    public void addItem(ProductId productId, int quantity, Money price) {
                        if (status != OrderStatus.CREATED) {
                            throw new IllegalStateException("订单已确认，无法添加商品");
                        }
                        this.items.add(new OrderItem(productId, quantity, price));
                        this.recalculateTotal();
                    }

                    public void confirm() {
                        if (items.isEmpty()) {
                            throw new IllegalStateException("订单不能为空");
                        }
                        this.status = OrderStatus.CONFIRMED;
                        this.registerEvent(new OrderConfirmedEvent(this));
                    }

                    private void recalculateTotal() {
                        this.totalAmount = items.stream()
                            .map(OrderItem::subtotal)
                            .reduce(Money.ZERO, Money::add);
                    }
                }
                """;
        System.out.println(aggregate);
    }

    private static void dddInPractice() {
        System.out.println("\n【5. DDD 实践建议】");
        System.out.println("-".repeat(50));

        String practice = """

                ═══════════════════════════════════════════════════════════
                                  何时使用 DDD？
                ═══════════════════════════════════════════════════════════

                ✅ 适合使用 DDD：
                • 业务复杂度高
                • 需要长期维护
                • 团队有领域专家

                ❌ 不适合使用 DDD：
                • CRUD 为主的简单应用
                • 短期项目
                • 技术驱动而非业务驱动


                ═══════════════════════════════════════════════════════════
                                  实践建议
                ═══════════════════════════════════════════════════════════

                1. 从战略设计开始
                ─────────────────────────────────────────────────────────
                • 先识别限界上下文
                • 再进行战术设计

                2. 与业务专家紧密合作
                ─────────────────────────────────────────────────────────
                • 建立统一语言
                • 持续完善领域模型

                3. 避免贫血模型
                ─────────────────────────────────────────────────────────
                • 业务逻辑放在领域对象中
                • 不要只有 getter/setter

                4. 渐进式采用
                ─────────────────────────────────────────────────────────
                • 不必一次性全部应用
                • 在核心域使用完整 DDD
                • 支撑域可以简化
                """;
        System.out.println(practice);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ DDD 领域驱动设计课程完成！");
        System.out.println("下一课: CqrsEventSourcing.java");
        System.out.println("=".repeat(60));
    }
}
