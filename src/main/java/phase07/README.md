# Phase 7: 设计模式

> **目标**：掌握常用设计模式与 SOLID 原则  
> **预计时长**：2 周  
> **前置条件**：Phase 6 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 SOLID 设计原则
2. 掌握创建型模式 (单例、工厂、建造者、原型)
3. 掌握结构型模式 (代理、装饰器、适配器)
4. 掌握行为型模式 (策略、观察者、模板方法、责任链)
5. 能够在实际项目中应用设计模式

---

## 📚 核心概念

### SOLID 原则

| 原则 | 名称     | 核心思想               |
| ---- | -------- | ---------------------- |
| S    | 单一职责 | 一个类只做一件事       |
| O    | 开闭原则 | 对扩展开放，对修改关闭 |
| L    | 里氏替换 | 子类可以替换父类       |
| I    | 接口隔离 | 接口要小而专           |
| D    | 依赖倒置 | 依赖抽象而非具体       |

### 设计模式分类

```
┌─────────────────────────────────────────────────────────┐
│                    23 种设计模式                         │
├───────────────┬───────────────┬─────────────────────────┤
│   创建型 (5)   │   结构型 (7)   │       行为型 (11)       │
├───────────────┼───────────────┼─────────────────────────┤
│  单例         │  代理         │  策略                   │
│  工厂方法     │  装饰器       │  观察者                 │
│  抽象工厂     │  适配器       │  模板方法               │
│  建造者       │  外观         │  责任链                 │
│  原型         │  桥接         │  状态                   │
│               │  组合         │  命令                   │
│               │  享元         │  迭代器/中介者等        │
└───────────────┴───────────────┴─────────────────────────┘
```

---

## 📁 文件列表

| #   | 文件                                                     | 描述            | 知识点                     |
| --- | -------------------------------------------------------- | --------------- | -------------------------- |
| 1   | [SolidPrinciples.java](./SolidPrinciples.java)           | SOLID 原则      | 五大设计原则               |
| 2   | [SingletonDemo.java](./SingletonDemo.java)               | 单例模式        | 饿汉/懒汉/DCL/枚举         |
| 3   | [FactoryDemo.java](./FactoryDemo.java)                   | 工厂模式        | 简单工厂/工厂方法/抽象工厂 |
| 4   | [BuilderPrototypeDemo.java](./BuilderPrototypeDemo.java) | 建造者/原型     | 链式调用/克隆              |
| 5   | [ProxyDemo.java](./ProxyDemo.java)                       | 代理模式        | 静态/JDK 动态/CGLIB        |
| 6   | [DecoratorAdapterDemo.java](./DecoratorAdapterDemo.java) | 装饰器/适配器   | IO 装饰器/接口转换         |
| 7   | [StrategyObserverDemo.java](./StrategyObserverDemo.java) | 策略/观察者     | 算法替换/事件通知          |
| 8   | [TemplateChainDemo.java](./TemplateChainDemo.java)       | 模板方法/责任链 | 骨架算法/链式处理          |
| 9   | [EcommerceSystem.java](./EcommerceSystem.java)           | 🎯 **实战项目** | 电商系统综合设计           |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行 SOLID 原则
mvn exec:java -Dexec.mainClass="phase07.SolidPrinciples"

# 运行单例模式
mvn exec:java -Dexec.mainClass="phase07.SingletonDemo"

# 运行工厂模式
mvn exec:java -Dexec.mainClass="phase07.FactoryDemo"

# 运行实战项目
mvn exec:java -Dexec.mainClass="phase07.EcommerceSystem"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: SolidPrinciples - 设计原则基础
2. **Day 3-4**: SingletonDemo + FactoryDemo - 创建型模式
3. **Day 5-6**: BuilderPrototypeDemo - 创建型模式续
4. **Day 7-8**: ProxyDemo - 代理模式 (重点)
5. **Day 9-10**: DecoratorAdapterDemo - 结构型模式
6. **Day 11-12**: StrategyObserverDemo + TemplateChainDemo - 行为型模式
7. **Day 13-14**: EcommerceSystem 项目实战

### 学习方法

1. 先理解问题场景，再看模式如何解决
2. 画 UML 类图加深理解
3. 找 Java 标准库/框架中的应用实例
4. 在自己的项目中尝试应用

---

## ✅ 完成检查

- [ ] 理解 SOLID 五大原则
- [ ] 掌握单例模式的多种实现
- [ ] 掌握工厂模式的三种变体
- [ ] 理解代理模式与 AOP 的关系
- [ ] 能够区分装饰器和适配器
- [ ] 理解策略模式如何替代 if-else
- [ ] 完成电商系统设计项目

---

## 🎯 实战项目: 电商订单系统

`EcommerceSystem.java` 是本阶段的综合项目，综合运用 8 种设计模式：

| 模式         | 应用场景     |
| ------------ | ------------ |
| 建造者模式   | 商品对象创建 |
| 单例模式     | 购物车管理   |
| 策略模式     | 促销策略     |
| 工厂模式     | 订单创建     |
| 观察者模式   | 订单状态通知 |
| 模板方法模式 | 支付流程     |
| 责任链模式   | 订单处理     |
| 装饰器模式   | 服务增强     |

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase07.EcommerceSystem"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 8: Spring 框架](../phase08/README.md)
