# Phase 7 学习总结: 设计模式 ✅

> **完成日期**: 2025 年 12 月 16 日  
> **学习时长**: 约 2 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. SOLID 原则 ✅

| 原则 | 名称     | 核心思想               | 实践要点           |
| ---- | -------- | ---------------------- | ------------------ |
| S    | 单一职责 | 一个类只做一件事       | 类的变化原因唯一   |
| O    | 开闭原则 | 对扩展开放，对修改关闭 | 用多态替代条件判断 |
| L    | 里氏替换 | 子类可替换父类         | 继承不改变原有行为 |
| I    | 接口隔离 | 接口要小而专           | 避免大而全的接口   |
| D    | 依赖倒置 | 依赖抽象而非具体       | 面向接口编程       |

### 2. 创建型模式 ✅

**单例模式**:

```java
// 枚举单例 - 最推荐
enum Singleton {
    INSTANCE;
    public void doSomething() { }
}

// 静态内部类 - 推荐
class StaticInnerSingleton {
    private static class Holder {
        static final StaticInnerSingleton INSTANCE = new StaticInnerSingleton();
    }
    public static StaticInnerSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

**工厂模式**:
| 类型 | 特点 | 适用场景 |
|------|------|----------|
| 简单工厂 | 一个工厂，参数控制 | 产品少且固定 |
| 工厂方法 | 每产品一工厂 | 产品可扩展 |
| 抽象工厂 | 创建产品族 | 多个相关产品 |

**建造者模式**:

```java
Computer pc = new Computer.Builder("i7", "16GB")
    .storage("512GB")
    .gpu("RTX 3080")
    .build();
```

### 3. 结构型模式 ✅

**代理模式**:

```java
// JDK 动态代理
UserService proxy = (UserService) Proxy.newProxyInstance(
    UserService.class.getClassLoader(),
    new Class<?>[] { UserService.class },
    (proxy, method, args) -> {
        System.out.println("前置处理");
        Object result = method.invoke(target, args);
        System.out.println("后置处理");
        return result;
    }
);
```

| 代理类型     | 实现方式    | 要求            |
| ------------ | ----------- | --------------- |
| JDK 动态代理 | 反射 + 接口 | 必须有接口      |
| CGLIB        | 继承目标类  | 不能是 final 类 |

**装饰器 vs 适配器**:
| 模式 | 目的 | 接口关系 |
|------|------|----------|
| 装饰器 | 增强功能 | 相同接口 |
| 适配器 | 转换接口 | 不同接口 |

### 4. 行为型模式 ✅

**策略模式**: 封装可互换的算法

```java
interface PaymentStrategy {
    void pay(double amount);
}
// 支付宝、微信、信用卡...不同策略
```

**观察者模式**: 一对多通知

```java
interface Observer {
    void update(Object data);
}
// 订阅-发布机制
```

**模板方法模式**: 定义骨架，子类实现细节

```java
abstract class Template {
    public final void process() {
        step1();
        step2();  // 抽象方法
        step3();  // 钩子方法
    }
}
```

**责任链模式**: 请求沿链传递

```java
Handler h1 = new Handler1();
Handler h2 = new Handler2();
h1.setNext(h2);
h1.handle(request);
```

---

## 🎯 实战项目: 电商订单系统

成功完成了 `EcommerceSystem.java` 项目，综合运用 8 种设计模式：

| 模式         | 应用场景                     |
| ------------ | ---------------------------- |
| 建造者模式   | 商品对象创建                 |
| 单例模式     | 购物车管理                   |
| 策略模式     | 促销策略 (满减/折扣)         |
| 工厂模式     | 订单创建                     |
| 观察者模式   | 订单状态通知 (短信/邮件/APP) |
| 模板方法模式 | 支付流程                     |
| 责任链模式   | 订单处理流水线               |
| 装饰器模式   | 服务日志增强                 |

---

## 📊 学习文件清单

| #   | 文件                        | 状态 | 核心模式            |
| --- | --------------------------- | ---- | ------------------- |
| 1   | `SolidPrinciples.java`      | ✅   | SRP/OCP/LSP/ISP/DIP |
| 2   | `SingletonDemo.java`        | ✅   | 单例 (6 种实现)     |
| 3   | `FactoryDemo.java`          | ✅   | 工厂 (3 种变体)     |
| 4   | `BuilderPrototypeDemo.java` | ✅   | 建造者 + 原型       |
| 5   | `ProxyDemo.java`            | ✅   | 代理 (静态/动态)    |
| 6   | `DecoratorAdapterDemo.java` | ✅   | 装饰器 + 适配器     |
| 7   | `StrategyObserverDemo.java` | ✅   | 策略 + 观察者       |
| 8   | `TemplateChainDemo.java`    | ✅   | 模板方法 + 责任链   |
| 9   | `EcommerceSystem.java`      | ✅   | **综合项目**        |

---

## 💡 重点心得

### 模式选择指南

```
需求场景                    推荐模式
─────────────────────────────────────────
对象创建复杂，多可选参数   → 建造者模式
全局唯一实例               → 单例模式 (枚举)
根据类型创建对象           → 工厂模式
运行时切换算法             → 策略模式
一对多事件通知             → 观察者模式
增强功能但不改接口         → 装饰器模式
接口不兼容转换             → 适配器模式
控制对象访问               → 代理模式
固定流程不同细节           → 模板方法模式
请求需多个处理者           → 责任链模式
```

### 最佳实践

1. **不要为了模式而模式** - 简单问题用简单方案
2. **优先组合而非继承** - 策略/装饰器/代理都是组合
3. **面向接口编程** - 依赖抽象，便于扩展
4. **识别变化点** - 模式的本质是封装变化

### Java/框架中的设计模式

| 模式     | Java/框架示例                       |
| -------- | ----------------------------------- |
| 单例     | Runtime, Spring Bean                |
| 工厂     | Calendar.getInstance(), BeanFactory |
| 建造者   | StringBuilder, Stream API           |
| 代理     | Spring AOP, MyBatis Mapper          |
| 装饰器   | Java IO (BufferedInputStream)       |
| 适配器   | Arrays.asList(), InputStreamReader  |
| 策略     | Comparator, ThreadPoolExecutor      |
| 观察者   | Spring Event, Swing Listener        |
| 模板方法 | HttpServlet, AbstractList           |
| 责任链   | Servlet Filter, Spring Interceptor  |

---

## 🚀 下一步计划

Phase 7 已完成！准备进入 **[Phase 8: Spring 框架](../phase08/README.md)**

Phase 8 将学习:

- IoC 容器与依赖注入
- AOP 面向切面编程
- Bean 生命周期
- Spring MVC
- 手写简易 Spring IoC

---

> 📝 _本总结由学习过程自动生成于 2025-12-16_
