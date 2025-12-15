# Phase 8: Spring 框架

> **目标**：深入理解 Spring 核心原理  
> **预计时长**：2 周  
> **前置条件**：Phase 7 完成

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 IoC 和 DI 原理
2. 掌握 Spring Bean 生命周期
3. 理解 AOP 面向切面编程
4. 熟悉 Spring MVC 开发

---

## 📚 核心概念

### IoC 容器

- 控制反转：对象创建交给容器
- 依赖注入：容器自动注入依赖

### AOP

- 切面（Aspect）
- 切点（Pointcut）
- 通知（Advice）

---

## 📁 文件列表

| 文件                       | 描述            | 状态 |
| -------------------------- | --------------- | ---- |
| `IocBasics.java`           | Bean 定义与获取 | ⏳   |
| `BeanLifecycle.java`       | Bean 生命周期   | ⏳   |
| `BeanScope.java`           | Bean 作用域     | ⏳   |
| `DependencyInjection.java` | 依赖注入方式    | ⏳   |
| `AnnotationConfig.java`    | 注解配置        | ⏳   |
| `AopBasics.java`           | 切面编程基础    | ⏳   |
| `AspectDemo.java`          | 切点与通知      | ⏳   |
| `TransactionDemo.java`     | 声明式事务      | ⏳   |

---

## ✅ 完成检查

- [ ] 理解 Spring IoC 原理
- [ ] 能够使用 AOP 实现横切关注点
- [ ] 完成手写简易 Spring IoC 项目
