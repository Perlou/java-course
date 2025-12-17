package phase07;

/**
 * Phase 7 - Lesson 3: 工厂模式
 * 
 * 🎯 学习目标:
 * 1. 理解简单工厂模式
 * 2. 掌握工厂方法模式
 * 3. 了解抽象工厂模式
 * 4. 学会选择合适的工厂模式
 */
public class FactoryDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 3: 工厂模式");
        System.out.println("=".repeat(60));

        // 工厂模式概述
        System.out.println("\n【工厂模式概述】");
        System.out.println("""
                意图: 定义创建对象的接口，让子类决定实例化哪个类

                优点:
                - 解耦: 客户端不直接创建对象
                - 扩展: 添加新产品无需修改已有代码
                - 封装: 隐藏创建的复杂性

                三种工厂模式:
                1. 简单工厂: 静态方法创建对象
                2. 工厂方法: 每个产品一个工厂
                3. 抽象工厂: 创建产品族
                """);

        // 1. 简单工厂
        System.out.println("=".repeat(60));
        System.out.println("【1. 简单工厂模式 (Simple Factory)】");
        System.out.println("""
                特点: 一个工厂类，通过参数决定创建哪种产品
                优点: 简单直观
                缺点: 违反开闭原则，新增产品需修改工厂

                适用: 产品类型较少且稳定
                """);

        // 演示简单工厂
        FactoryProduct productA = SimpleFactory.createProduct("A");
        FactoryProduct productB = SimpleFactory.createProduct("B");

        productA.use();
        productB.use();

        // 2. 工厂方法
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【2. 工厂方法模式 (Factory Method)】");
        System.out.println("""
                特点: 每种产品对应一个工厂
                优点: 符合开闭原则，新增产品只需添加工厂
                缺点: 类的数量增加

                适用: 产品种类可能扩展
                """);

        System.out.println("""
                结构:
                ┌──────────────┐      ┌──────────────┐
                │ ProductFactory │◁━━━━│ ConcreteFactory │
                │ +create()     │      │ +create()      │
                └───────┬───────┘      └───────────────┘
                        │
                        ▽
                ┌──────────────┐      ┌──────────────┐
                │  Product     │◁━━━━│ ConcreteProduct │
                └──────────────┘      └───────────────┘
                """);

        // 演示工厂方法
        FactoryProductFactory laptopFactory = new LaptopProductFactory();
        FactoryProductFactory phoneFactory = new PhoneProductFactory();

        FactoryProduct laptop = laptopFactory.create();
        FactoryProduct phone = phoneFactory.create();

        laptop.use();
        phone.use();

        // 3. 抽象工厂
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【3. 抽象工厂模式 (Abstract Factory)】");
        System.out.println("""
                特点: 创建一系列相关的产品 (产品族)
                优点: 保证产品的一致性
                缺点: 不易新增产品类型

                适用: 需要创建多个相关产品

                例如: UI 组件库
                - Windows 风格: WindowsButton, WindowsTextField
                - Mac 风格: MacButton, MacTextField
                """);

        System.out.println("""
                产品族概念:

                            产品等级(类型)
                            Button    TextField
                产品族 ┌───────────────────────┐
                Windows │ WinButton  WinTextField │
                Mac     │ MacButton  MacTextField │
                        └───────────────────────┘
                """);

        // 演示抽象工厂
        GUIFactory windowsFactory = new WindowsFactory();
        GUIFactory macFactory = new MacFactory();

        System.out.println("\nWindows 风格:");
        Button winBtn = windowsFactory.createButton();
        TextField winTf = windowsFactory.createTextField();
        winBtn.render();
        winTf.render();

        System.out.println("\nMac 风格:");
        Button macBtn = macFactory.createButton();
        TextField macTf = macFactory.createTextField();
        macBtn.render();
        macTf.render();

        // 4. 实际应用示例
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【4. 实际应用示例】");
        System.out.println("""
                Java 中的工厂模式:

                1. Calendar.getInstance()
                   // 根据地区返回不同的日历实现

                2. NumberFormat.getInstance()
                   // 根据地区返回不同的数字格式化器

                3. DocumentBuilderFactory.newInstance()
                   // XML 解析器工厂

                4. DriverManager.getConnection()
                   // JDBC 连接工厂

                5. Spring BeanFactory
                   // IoC 容器就是一个大工厂
                """);

        // 5. 三种工厂对比
        System.out.println("=".repeat(60));
        System.out.println("【5. 三种工厂模式对比】");
        System.out.println("""
                ┌────────────┬─────────────┬─────────────┬─────────────┐
                │   模式     │  简单工厂    │  工厂方法   │  抽象工厂   │
                ├────────────┼─────────────┼─────────────┼─────────────┤
                │ 工厂类数量 │  1个        │  多个       │  多个       │
                │ 产品类型   │  单一       │  单一       │  产品族     │
                │ 开闭原则   │  违反       │  遵守       │  半遵守     │
                │ 复杂度     │  低         │  中         │  高         │
                │ 使用场景   │  类型少     │  类型可扩展 │  产品族     │
                └────────────┴─────────────┴─────────────┴─────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 简单工厂适合产品少且稳定的场景");
        System.out.println("💡 工厂方法适合单一产品的扩展");
        System.out.println("💡 抽象工厂适合创建产品族");
        System.out.println("=".repeat(60));
    }
}

// ==================== 产品接口 ====================

interface FactoryProduct {
    void use();
}

// ==================== 1. 简单工厂 ====================

class FactoryProductA implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用产品 A");
    }
}

class FactoryProductB implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用产品 B");
    }
}

class SimpleFactory {
    public static FactoryProduct createProduct(String type) {
        return switch (type) {
            case "A" -> new FactoryProductA();
            case "B" -> new FactoryProductB();
            default -> throw new IllegalArgumentException("未知产品类型");
        };
    }
}

// ==================== 2. 工厂方法 ====================

interface FactoryProductFactory {
    FactoryProduct create();
}

class LaptopProduct implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用笔记本电脑");
    }
}

class PhoneProduct implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用手机");
    }
}

class LaptopProductFactory implements FactoryProductFactory {
    @Override
    public FactoryProduct create() {
        System.out.println("创建笔记本电脑");
        return new LaptopProduct();
    }
}

class PhoneProductFactory implements FactoryProductFactory {
    @Override
    public FactoryProduct create() {
        System.out.println("创建手机");
        return new PhoneProduct();
    }
}

// ==================== 3. 抽象工厂 ====================

// 产品接口
interface Button {
    void render();
}

interface TextField {
    void render();
}

// Windows 风格产品
class WindowsButton implements Button {
    @Override
    public void render() {
        System.out.println("  渲染 Windows 按钮 [====]");
    }
}

class WindowsTextField implements TextField {
    @Override
    public void render() {
        System.out.println("  渲染 Windows 文本框 |_______|");
    }
}

// Mac 风格产品
class MacButton implements Button {
    @Override
    public void render() {
        System.out.println("  渲染 Mac 按钮 (====)");
    }
}

class MacTextField implements TextField {
    @Override
    public void render() {
        System.out.println("  渲染 Mac 文本框 [_______]");
    }
}

// 抽象工厂接口
interface GUIFactory {
    Button createButton();

    TextField createTextField();
}

// 具体工厂
class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public TextField createTextField() {
        return new WindowsTextField();
    }
}

class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public TextField createTextField() {
        return new MacTextField();
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 简单工厂: 一个静态方法，根据参数创建对象
 * 2. 工厂方法: 定义创建接口，子类决定实例化
 * 3. 抽象工厂: 创建一系列相关对象的接口
 * 
 * 使用场景:
 * - 对象创建逻辑复杂
 * - 需要解耦客户端和具体类
 * - 可能需要扩展产品类型
 * 
 * 🏃 练习:
 * 1. 实现一个数据库连接工厂
 * 2. 为支付系统设计工厂模式
 * 3. 分析 Spring BeanFactory 的设计
 */
