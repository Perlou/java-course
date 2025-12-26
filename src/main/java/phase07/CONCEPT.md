# Phase 7: 设计模式 - 核心概念

> 设计模式是前人解决常见问题的经验总结

---

## 🎯 设计模式全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        设计模式分类                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  创建型模式 (Creational)                                            │
│  ├── 单例模式 (Singleton) - 全局唯一实例                           │
│  ├── 工厂方法 (Factory Method) - 延迟实例化                        │
│  ├── 抽象工厂 (Abstract Factory) - 产品族创建                      │
│  ├── 建造者 (Builder) - 复杂对象构建                               │
│  └── 原型 (Prototype) - 克隆对象                                   │
│                                                                     │
│  结构型模式 (Structural)                                            │
│  ├── 适配器 (Adapter) - 接口转换                                   │
│  ├── 装饰器 (Decorator) - 动态扩展功能                             │
│  ├── 代理 (Proxy) - 控制访问                                       │
│  ├── 外观 (Facade) - 简化接口                                      │
│  ├── 桥接 (Bridge) - 抽象与实现分离                                │
│  ├── 组合 (Composite) - 树形结构                                   │
│  └── 享元 (Flyweight) - 共享对象                                   │
│                                                                     │
│  行为型模式 (Behavioral)                                            │
│  ├── 策略 (Strategy) - 算法族可替换                                │
│  ├── 观察者 (Observer) - 事件通知                                  │
│  ├── 模板方法 (Template Method) - 算法骨架                         │
│  ├── 责任链 (Chain of Responsibility) - 请求传递                   │
│  ├── 命令 (Command) - 请求封装                                     │
│  ├── 状态 (State) - 状态驱动行为                                   │
│  ├── 迭代器 (Iterator) - 遍历集合                                  │
│  └── 中介者 (Mediator) - 解耦交互                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📐 SOLID 原则

```
┌─────────────────────────────────────────────────────────────────────┐
│                        SOLID 原则                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  S - Single Responsibility (单一职责)                               │
│  ─────────────────────────────────────────────────────────────     │
│  一个类应该只有一个引起它变化的原因                                  │
│                                                                     │
│  O - Open/Closed (开闭原则)                                        │
│  ─────────────────────────────────────────────────────────────     │
│  对扩展开放，对修改关闭                                             │
│                                                                     │
│  L - Liskov Substitution (里氏替换)                                │
│  ─────────────────────────────────────────────────────────────     │
│  子类可以替换父类出现的任何地方                                     │
│                                                                     │
│  I - Interface Segregation (接口隔离)                              │
│  ─────────────────────────────────────────────────────────────     │
│  不应该强迫客户端依赖它不需要的接口                                  │
│                                                                     │
│  D - Dependency Inversion (依赖倒置)                               │
│  ─────────────────────────────────────────────────────────────     │
│  高层模块不应该依赖低层模块，都应该依赖抽象                          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ 创建型模式

### 单例模式

```java
// 懒汉式 - 双重检查锁
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// 枚举单例 (推荐)
public enum Singleton {
    INSTANCE;

    public void doSomething() { }
}
```

### 工厂模式

```java
// 简单工厂
public class ShapeFactory {
    public static Shape create(String type) {
        return switch (type) {
            case "circle" -> new Circle();
            case "rectangle" -> new Rectangle();
            default -> throw new IllegalArgumentException();
        };
    }
}

// 工厂方法
public interface ShapeFactory {
    Shape create();
}
public class CircleFactory implements ShapeFactory {
    public Shape create() { return new Circle(); }
}
```

### 建造者模式

```java
User user = User.builder()
    .name("张三")
    .age(25)
    .email("zhangsan@example.com")
    .build();
```

---

## 🔧 结构型模式

### 适配器模式

```
┌─────────────────────────────────────────────────────────────────────┐
│                        适配器模式                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  将一个类的接口转换成客户期望的另一个接口                            │
│                                                                     │
│  ┌────────┐      ┌─────────┐      ┌────────┐                       │
│  │ Client │ ───→ │ Adapter │ ───→ │ Adaptee│                       │
│  └────────┘      └─────────┘      └────────┘                       │
│       ↓               ↓                ↓                           │
│   期望的接口      接口转换器       现有的接口                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 装饰器模式

```java
// Java IO 是装饰器模式的典型应用
InputStream in = new BufferedInputStream(
    new FileInputStream("file.txt")
);

// 装饰器可以层层嵌套
Component component = new ConcreteComponent();
component = new DecoratorA(component);
component = new DecoratorB(component);
```

### 代理模式

```java
// 静态代理
public class UserServiceProxy implements UserService {
    private final UserService target;

    @Override
    public void save(User user) {
        log.info("开始保存用户");
        target.save(user);
        log.info("保存用户完成");
    }
}

// 动态代理 (JDK / CGLIB)
// Spring AOP 就是基于动态代理实现
```

---

## 🎭 行为型模式

### 策略模式

```java
// 定义策略接口
public interface PaymentStrategy {
    void pay(BigDecimal amount);
}

// 具体策略
public class AlipayStrategy implements PaymentStrategy {
    public void pay(BigDecimal amount) {
        System.out.println("支付宝支付: " + amount);
    }
}

// 使用
PaymentStrategy strategy = new AlipayStrategy();
strategy.pay(new BigDecimal("100"));
```

### 观察者模式

```java
// 被观察者
public class EventPublisher {
    private List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void publish(Event event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}

// 观察者
public interface EventListener {
    void onEvent(Event event);
}
```

### 模板方法模式

```java
public abstract class AbstractTemplate {
    // 模板方法，定义算法骨架
    public final void execute() {
        step1();
        step2();  // 抽象方法，子类实现
        step3();
    }

    private void step1() { /* 固定步骤 */ }
    protected abstract void step2();  // 可变步骤
    private void step3() { /* 固定步骤 */ }
}
```

### 责任链模式

```java
public abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public void handle(Request request) {
        if (canHandle(request)) {
            doHandle(request);
        } else if (next != null) {
            next.handle(request);
        }
    }

    protected abstract boolean canHandle(Request request);
    protected abstract void doHandle(Request request);
}
```

---

## 🎯 模式选择指南

| 需求         | 推荐模式 |
| ------------ | -------- |
| 全局唯一实例 | 单例     |
| 对象创建解耦 | 工厂     |
| 复杂对象构建 | 建造者   |
| 接口不兼容   | 适配器   |
| 动态扩展功能 | 装饰器   |
| 控制对象访问 | 代理     |
| 算法可替换   | 策略     |
| 事件通知     | 观察者   |
| 算法骨架固定 | 模板方法 |
| 请求逐级处理 | 责任链   |

---

## 📖 学习要点

```
✅ 理解 SOLID 设计原则
✅ 掌握单例模式的多种实现
✅ 理解工厂模式的应用场景
✅ 掌握装饰器和代理的区别
✅ 熟练应用策略和模板方法模式
```

---

> 接下来学习 Spring 框架：[Phase 8 README](../phase08/README.md)
