package phase07;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 7 - 实战项目: 电商系统设计
 * 
 * 🎯 项目目标:
 * 1. 综合运用多种设计模式
 * 2. 构建一个简化的电商订单系统
 * 3. 体验设计模式在实际项目中的应用
 */
public class EcommerceSystem {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   🛒 电商订单系统 - 设计模式实战");
        System.out.println("=".repeat(60));

        // 1. 创建商品 (建造者模式)
        System.out.println("\n【1. 创建商品 - 建造者模式】");

        Product laptop = new Product.Builder("MacBook Pro", 12999.0)
                .category("电子产品")
                .description("Apple M2 芯片")
                .stock(100)
                .build();

        Product phone = new Product.Builder("iPhone 15", 7999.0)
                .category("电子产品")
                .stock(200)
                .build();

        System.out.println("创建商品: " + laptop);
        System.out.println("创建商品: " + phone);

        // 2. 获取购物车 (单例模式)
        System.out.println("\n【2. 购物车 - 单例模式】");

        ShoppingCart cart = ShoppingCart.getInstance();
        cart.addItem(laptop, 1);
        cart.addItem(phone, 2);

        System.out.println("购物车内容:");
        cart.showItems();
        System.out.println("总计: ¥" + cart.getTotal());

        // 3. 应用促销策略 (策略模式)
        System.out.println("\n【3. 促销策略 - 策略模式】");

        PromotionContext promotion = new PromotionContext();

        double originalTotal = cart.getTotal();
        System.out.println("原价: ¥" + originalTotal);

        // 满减策略
        promotion.setStrategy(new FullReductionPromotion(5000, 500));
        double reduced = promotion.calculate(originalTotal);
        System.out.println("满5000减500后: ¥" + reduced);

        // 折扣策略
        promotion.setStrategy(new DiscountPromotion(0.9));
        double discounted = promotion.calculate(originalTotal);
        System.out.println("9折后: ¥" + discounted);

        // 4. 创建订单 (工厂模式)
        System.out.println("\n【4. 创建订单 - 工厂模式】");

        Order order = OrderFactory.createOrder("VIP", cart, discounted);
        System.out.println("创建订单: " + order);

        // 5. 订单状态通知 (观察者模式)
        System.out.println("\n【5. 订单状态通知 - 观察者模式】");

        order.addObserver(new SmsNotifier("13800138000"));
        order.addObserver(new EmailNotifier("user@example.com"));
        order.addObserver(new AppPushNotifier());

        order.updateStatus(OrderStatus.PAID);
        System.out.println();
        order.updateStatus(OrderStatus.SHIPPED);

        // 6. 支付处理 (模板方法 + 策略)
        System.out.println("\n【6. 支付处理 - 模板方法模式】");

        PaymentProcessor alipayProcessor = new AlipayProcessor();
        alipayProcessor.processPayment(order);

        // 7. 订单处理流程 (责任链模式)
        System.out.println("\n【7. 订单处理流程 - 责任链模式】");

        OrderHandler inventoryHandler = new InventoryHandler();
        OrderHandler paymentHandler = new PaymentCheckHandler();
        OrderHandler shippingHandler = new ShippingHandler();

        inventoryHandler.setNext(paymentHandler);
        paymentHandler.setNext(shippingHandler);

        inventoryHandler.handle(order);

        // 8. 订单日志装饰 (装饰器模式)
        System.out.println("\n【8. 日志增强 - 装饰器模式】");

        OrderService baseService = new BasicOrderService();
        OrderService loggingService = new LoggingOrderDecorator(baseService);
        OrderService timingService = new TimingOrderDecorator(loggingService);

        timingService.processOrder(order);

        // 设计模式总结
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【本项目使用的设计模式】");
        System.out.println("""
                ┌──────────────────┬────────────────────────────────┐
                │     模式          │        应用场景                │
                ├──────────────────┼────────────────────────────────┤
                │  建造者模式       │  复杂商品对象的创建            │
                │  单例模式         │  购物车实例管理                │
                │  策略模式         │  多种促销策略                  │
                │  工厂模式         │  不同类型订单创建              │
                │  观察者模式       │  订单状态变更通知              │
                │  模板方法模式     │  支付流程                      │
                │  责任链模式       │  订单处理流程                  │
                │  装饰器模式       │  服务功能增强                  │
                └──────────────────┴────────────────────────────────┘
                """);
        System.out.println("=".repeat(60));
    }
}

// ==================== 商品 - 建造者模式 ====================

class Product {
    private final String name;
    private final double price;
    private final String category;
    private final String description;
    private final int stock;

    private Product(Builder builder) {
        this.name = builder.name;
        this.price = builder.price;
        this.category = builder.category;
        this.description = builder.description;
        this.stock = builder.stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (¥%.2f)", name, price);
    }

    public static class Builder {
        private final String name;
        private final double price;
        private String category = "默认";
        private String description = "";
        private int stock = 0;

        public Builder(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder description(String desc) {
            this.description = desc;
            return this;
        }

        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}

// ==================== 购物车 - 单例模式 ====================

class ShoppingCart {
    private static final ShoppingCart INSTANCE = new ShoppingCart();
    private final Map<Product, Integer> items = new LinkedHashMap<>();

    private ShoppingCart() {
    }

    public static ShoppingCart getInstance() {
        return INSTANCE;
    }

    public void addItem(Product product, int quantity) {
        items.merge(product, quantity, Integer::sum);
    }

    public void showItems() {
        items.forEach((product, qty) -> System.out.println("  " + product + " x " + qty));
    }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    public Map<Product, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }
}

// ==================== 促销策略 - 策略模式 ====================

interface PromotionStrategy {
    double calculate(double price);
}

class FullReductionPromotion implements PromotionStrategy {
    private final double threshold;
    private final double reduction;

    public FullReductionPromotion(double threshold, double reduction) {
        this.threshold = threshold;
        this.reduction = reduction;
    }

    @Override
    public double calculate(double price) {
        return price >= threshold ? price - reduction : price;
    }
}

class DiscountPromotion implements PromotionStrategy {
    private final double discount;

    public DiscountPromotion(double discount) {
        this.discount = discount;
    }

    @Override
    public double calculate(double price) {
        return price * discount;
    }
}

class PromotionContext {
    private PromotionStrategy strategy;

    public void setStrategy(PromotionStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(double price) {
        return strategy.calculate(price);
    }
}

// ==================== 订单 - 工厂模式 + 观察者模式 ====================

enum OrderStatus {
    CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
}

interface OrderObserver {
    void onStatusChange(Order order, OrderStatus newStatus);
}

class Order {
    private final String orderId;
    private final String type;
    private final double amount;
    private OrderStatus status;
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(String orderId, String type, double amount) {
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
        this.status = OrderStatus.CREATED;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        notifyObservers();
    }

    private void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.onStatusChange(this, status);
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("Order[%s, type=%s, amount=¥%.2f, status=%s]",
                orderId, type, amount, status);
    }
}

class OrderFactory {
    private static int counter = 1000;

    public static Order createOrder(String type, ShoppingCart cart, double amount) {
        String orderId = "ORD" + (++counter);
        return new Order(orderId, type, amount);
    }
}

// 观察者实现
class SmsNotifier implements OrderObserver {
    private final String phone;

    public SmsNotifier(String phone) {
        this.phone = phone;
    }

    @Override
    public void onStatusChange(Order order, OrderStatus newStatus) {
        System.out.println("  [短信] 发送到 " + phone + ": 订单" + order.getOrderId() + " 状态变更为 " + newStatus);
    }
}

class EmailNotifier implements OrderObserver {
    private final String email;

    public EmailNotifier(String email) {
        this.email = email;
    }

    @Override
    public void onStatusChange(Order order, OrderStatus newStatus) {
        System.out.println("  [邮件] 发送到 " + email + ": 订单状态更新");
    }
}

class AppPushNotifier implements OrderObserver {
    @Override
    public void onStatusChange(Order order, OrderStatus newStatus) {
        System.out.println("  [App推送] 您的订单 " + order.getOrderId() + " " + getStatusMessage(newStatus));
    }

    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PAID -> "已支付成功";
            case SHIPPED -> "已发货";
            case DELIVERED -> "已送达";
            default -> "状态更新";
        };
    }
}

// ==================== 支付处理 - 模板方法模式 ====================

abstract class PaymentProcessor {
    public final void processPayment(Order order) {
        validate(order);
        connect();
        pay(order);
        sendReceipt(order);
    }

    protected void validate(Order order) {
        System.out.println("  验证订单...");
    }

    protected abstract void connect();

    protected abstract void pay(Order order);

    protected void sendReceipt(Order order) {
        System.out.println("  发送支付凭证");
    }
}

class AlipayProcessor extends PaymentProcessor {
    @Override
    protected void connect() {
        System.out.println("  连接支付宝...");
    }

    @Override
    protected void pay(Order order) {
        System.out.println("  支付宝支付 ¥" + order.getAmount());
    }
}

// ==================== 订单处理 - 责任链模式 ====================

abstract class OrderHandler {
    protected OrderHandler next;

    public void setNext(OrderHandler next) {
        this.next = next;
    }

    public abstract void handle(Order order);
}

class InventoryHandler extends OrderHandler {
    @Override
    public void handle(Order order) {
        System.out.println("  [库存检查] 商品库存充足 ✓");
        if (next != null)
            next.handle(order);
    }
}

class PaymentCheckHandler extends OrderHandler {
    @Override
    public void handle(Order order) {
        System.out.println("  [支付验证] 支付状态确认 ✓");
        if (next != null)
            next.handle(order);
    }
}

class ShippingHandler extends OrderHandler {
    @Override
    public void handle(Order order) {
        System.out.println("  [物流处理] 已安排发货 ✓");
        if (next != null)
            next.handle(order);
    }
}

// ==================== 订单服务 - 装饰器模式 ====================

interface OrderService {
    void processOrder(Order order);
}

class BasicOrderService implements OrderService {
    @Override
    public void processOrder(Order order) {
        System.out.println("  处理订单: " + order.getOrderId());
    }
}

abstract class OrderServiceDecorator implements OrderService {
    protected final OrderService delegate;

    public OrderServiceDecorator(OrderService delegate) {
        this.delegate = delegate;
    }
}

class LoggingOrderDecorator extends OrderServiceDecorator {
    public LoggingOrderDecorator(OrderService delegate) {
        super(delegate);
    }

    @Override
    public void processOrder(Order order) {
        System.out.println("  [日志] 开始处理订单 " + order.getOrderId());
        delegate.processOrder(order);
        System.out.println("  [日志] 订单处理完成");
    }
}

class TimingOrderDecorator extends OrderServiceDecorator {
    public TimingOrderDecorator(OrderService delegate) {
        super(delegate);
    }

    @Override
    public void processOrder(Order order) {
        long start = System.currentTimeMillis();
        delegate.processOrder(order);
        long duration = System.currentTimeMillis() - start;
        System.out.println("  [计时] 耗时: " + duration + "ms");
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 8 种设计模式:
 * 
 * 创建型:
 * 1. 建造者模式 - 商品创建
 * 2. 单例模式 - 购物车
 * 3. 工厂模式 - 订单创建
 * 
 * 结构型:
 * 4. 装饰器模式 - 服务增强
 * 
 * 行为型:
 * 5. 策略模式 - 促销策略
 * 6. 观察者模式 - 状态通知
 * 7. 模板方法模式 - 支付流程
 * 8. 责任链模式 - 订单处理
 * 
 * 🎯 扩展任务:
 * 1. 添加代理模式实现缓存
 * 2. 用适配器模式集成第三方支付
 * 3. 添加状态模式管理订单状态
 */
