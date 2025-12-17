package phase07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Phase 7 - Lesson 7: 策略模式与观察者模式
 * 
 * 🎯 学习目标:
 * 1. 理解策略模式的应用
 * 2. 理解观察者模式的应用
 * 3. 学会组合使用模式
 */
public class StrategyObserverDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 7: 策略模式与观察者模式");
        System.out.println("=".repeat(60));

        // ==================== 策略模式 ====================

        System.out.println("\n【策略模式 (Strategy)】");
        System.out.println("""
                意图: 定义一系列算法，封装每个算法，使它们可以互换

                组成:
                - Context: 上下文，持有策略引用
                - Strategy: 策略接口
                - ConcreteStrategy: 具体策略实现

                优点:
                - 避免大量 if-else
                - 算法可独立变化
                - 运行时切换策略

                应用场景:
                - 支付方式选择
                - 排序算法
                - 促销活动
                """);

        // 演示策略模式 - 支付
        System.out.println("支付策略示例:");

        PaymentContext payment = new PaymentContext();

        payment.setStrategy(new AlipayStrategy());
        payment.pay(100);

        payment.setStrategy(new WechatPayStrategy());
        payment.pay(200);

        payment.setStrategy(new CreditCardStrategy("1234-5678-9012"));
        payment.pay(300);

        // 策略模式 - 排序
        System.out.println("\n排序策略示例:");

        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9);

        SortContext<Integer> sorter = new SortContext<>();

        sorter.setStrategy(new BubbleSortStrategy<>());
        System.out.println("冒泡排序: " + sorter.sort(new ArrayList<>(numbers)));

        sorter.setStrategy(new QuickSortStrategy<>());
        System.out.println("快速排序: " + sorter.sort(new ArrayList<>(numbers)));

        // ==================== 观察者模式 ====================

        System.out.println("\n" + "=".repeat(60));
        System.out.println("\n【观察者模式 (Observer)】");
        System.out.println("""
                意图: 定义对象间一对多的依赖，当一个对象改变状态时，
                      所有依赖它的对象都会收到通知并自动更新

                又称: 发布-订阅模式 (Pub-Sub)

                组成:
                - Subject: 主题/被观察者
                - Observer: 观察者

                应用场景:
                - 事件处理系统
                - GUI 事件监听
                - 消息通知
                - 数据变化监听
                """);

        // 演示观察者模式 - 股票价格
        System.out.println("股票价格监控示例:");

        StockPrice stock = new StockPrice("AAPL", 150.0);

        // 添加观察者
        stock.addObserver(new PhoneApp("手机App"));
        stock.addObserver(new EmailAlert("user@example.com"));
        stock.addObserver(new WebDashboard());

        // 价格变化，通知所有观察者
        stock.setPrice(155.0);

        System.out.println();
        stock.setPrice(145.0);

        // Java 中的观察者
        System.out.println("\nJava 中的观察者模式:");
        System.out.println("""
                1. Swing 事件监听
                   button.addActionListener(e -> { });

                2. PropertyChangeSupport
                   support.addPropertyChangeListener(listener);

                3. Java 9+ Flow API (响应式流)
                   Flow.Publisher / Flow.Subscriber

                4. Spring 事件机制
                   ApplicationEventPublisher
                   @EventListener
                """);

        // 对比策略和状态模式
        System.out.println("=".repeat(60));
        System.out.println("【策略模式 vs 状态模式】");
        System.out.println("""
                相似点:
                - 都使用组合替代继承
                - 都通过接口实现多态

                区别:
                ┌────────────┬─────────────────────┬─────────────────────┐
                │   方面     │     策略模式         │     状态模式         │
                ├────────────┼─────────────────────┼─────────────────────┤
                │  切换主体  │ 客户端决定          │ 状态对象自动切换     │
                │  目的      │ 算法可互换          │ 对象行为随状态变化   │
                │  关联性    │ 策略间相互独立      │ 状态间有转换关系     │
                └────────────┴─────────────────────┴─────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 策略模式: 封装可互换的算法");
        System.out.println("💡 观察者模式: 一对多的通知机制");
        System.out.println("💡 两者都是非常实用的行为型模式");
        System.out.println("=".repeat(60));
    }
}

// ==================== 策略模式 - 支付 ====================

interface PaymentStrategy {
    void pay(double amount);
}

class AlipayStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("  支付宝支付: ¥" + amount);
    }
}

class WechatPayStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("  微信支付: ¥" + amount);
    }
}

class CreditCardStrategy implements PaymentStrategy {
    private final String cardNumber;

    public CreditCardStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("  信用卡支付: ¥" + amount + " (卡号: " + cardNumber + ")");
    }
}

class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void pay(double amount) {
        if (strategy == null) {
            throw new IllegalStateException("未设置支付策略");
        }
        strategy.pay(amount);
    }
}

// ==================== 策略模式 - 排序 ====================

interface SortStrategy<T extends Comparable<T>> {
    List<T> sort(List<T> list);
}

class BubbleSortStrategy<T extends Comparable<T>> implements SortStrategy<T> {
    @Override
    public List<T> sort(List<T> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    T temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }
}

class QuickSortStrategy<T extends Comparable<T>> implements SortStrategy<T> {
    @Override
    public List<T> sort(List<T> list) {
        Collections.sort(list); // 简化实现
        return list;
    }
}

class SortContext<T extends Comparable<T>> {
    private SortStrategy<T> strategy;

    public void setStrategy(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public List<T> sort(List<T> list) {
        return strategy.sort(list);
    }
}

// ==================== 观察者模式 ====================

interface StockObserver {
    void update(String symbol, double price);
}

class StockPrice {
    private final String symbol;
    private double price;
    private final List<StockObserver> observers = new ArrayList<>();

    public StockPrice(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    public void setPrice(double newPrice) {
        double oldPrice = this.price;
        this.price = newPrice;
        System.out.println("股票 " + symbol + " 价格变化: $" + oldPrice + " → $" + newPrice);
        notifyObservers();
    }

    private void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(symbol, price);
        }
    }
}

class PhoneApp implements StockObserver {
    private final String appName;

    public PhoneApp(String appName) {
        this.appName = appName;
    }

    @Override
    public void update(String symbol, double price) {
        System.out.println("  [" + appName + "] 推送: " + symbol + " 当前价格 $" + price);
    }
}

class EmailAlert implements StockObserver {
    private final String email;

    public EmailAlert(String email) {
        this.email = email;
    }

    @Override
    public void update(String symbol, double price) {
        System.out.println("  [邮件] 发送到 " + email + ": " + symbol + " = $" + price);
    }
}

class WebDashboard implements StockObserver {
    @Override
    public void update(String symbol, double price) {
        System.out.println("  [网页仪表板] 更新显示: " + symbol + " = $" + price);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 策略模式:
 * - 封装可互换的算法
 * - 避免条件语句
 * - 运行时切换算法
 * 
 * 观察者模式:
 * - 一对多的通知
 * - 解耦主题和观察者
 * - 事件驱动架构基础
 * 
 * 🏃 练习:
 * 1. 实现一个折扣策略模式
 * 2. 实现一个天气通知的观察者模式
 * 3. 分析 Spring @EventListener 的实现
 */
