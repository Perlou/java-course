package phase07;

/**
 * Phase 7 - Lesson 1: SOLID 原则
 * 
 * 🎯 学习目标:
 * 1. 理解面向对象设计的五大原则
 * 2. 掌握每个原则的应用场景
 * 3. 学会用 SOLID 原则评估代码设计
 */
public class SolidPrinciples {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 1: SOLID 原则");
        System.out.println("=".repeat(60));

        // SOLID 概述
        System.out.println("\n【SOLID 原则概述】");
        System.out.println("""
                SOLID 是面向对象设计的五大基本原则:

                S - Single Responsibility Principle (单一职责原则)
                O - Open/Closed Principle (开闭原则)
                L - Liskov Substitution Principle (里氏替换原则)
                I - Interface Segregation Principle (接口隔离原则)
                D - Dependency Inversion Principle (依赖倒置原则)

                遵循 SOLID 原则可以:
                - 提高代码可维护性
                - 增强代码可扩展性
                - 降低代码耦合度
                - 便于测试
                """);

        // 1. 单一职责原则
        System.out.println("=".repeat(60));
        System.out.println("【1. 单一职责原则 (SRP)】");
        System.out.println("""
                定义: 一个类应该只有一个引起它变化的原因

                通俗理解: 一个类只做一件事
                """);

        System.out.println("❌ 违反 SRP:");
        System.out.println("""
                class UserService {
                    void createUser() { }
                    void deleteUser() { }
                    void sendEmail() { }      // 不应该在这里
                    void generateReport() { } // 不应该在这里
                }
                """);

        System.out.println("✅ 遵循 SRP:");
        System.out.println("""
                class UserService {
                    void createUser() { }
                    void deleteUser() { }
                }

                class EmailService {
                    void sendEmail() { }
                }

                class ReportService {
                    void generateReport() { }
                }
                """);

        // 2. 开闭原则
        System.out.println("=".repeat(60));
        System.out.println("【2. 开闭原则 (OCP)】");
        System.out.println("""
                定义: 对扩展开放，对修改关闭

                通俗理解: 添加新功能时，应该扩展代码而不是修改已有代码
                """);

        System.out.println("❌ 违反 OCP:");
        System.out.println("""
                class PriceCalculator {
                    double calculate(String type, double price) {
                        if (type.equals("normal")) {
                            return price;
                        } else if (type.equals("vip")) {
                            return price * 0.9;
                        } else if (type.equals("svip")) {  // 每次新增类型都要修改
                            return price * 0.8;
                        }
                        return price;
                    }
                }
                """);

        System.out.println("✅ 遵循 OCP:");
        System.out.println("""
                interface PricingStrategy {
                    double calculate(double price);
                }

                class NormalPricing implements PricingStrategy {
                    public double calculate(double price) { return price; }
                }

                class VipPricing implements PricingStrategy {
                    public double calculate(double price) { return price * 0.9; }
                }

                // 新增类型只需添加新类，无需修改已有代码
                class SvipPricing implements PricingStrategy {
                    public double calculate(double price) { return price * 0.8; }
                }
                """);

        // 演示开闭原则
        PricingStrategy normalStrategy = new NormalPricingStrategy();
        PricingStrategy vipStrategy = new VipPricingStrategy();

        double price = 100.0;
        System.out.println("原价: " + price);
        System.out.println("普通用户: " + normalStrategy.calculate(price));
        System.out.println("VIP用户: " + vipStrategy.calculate(price));

        // 3. 里氏替换原则
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【3. 里氏替换原则 (LSP)】");
        System.out.println("""
                定义: 子类必须能够替换其父类

                通俗理解: 子类可以扩展父类的功能，但不能改变父类原有的功能

                规则:
                - 子类可以实现父类的抽象方法
                - 子类可以增加自己的方法
                - 子类重写父类方法时，前置条件要更宽松
                - 子类重写父类方法时，后置条件要更严格
                """);

        System.out.println("❌ 违反 LSP (经典案例: 正方形继承长方形):");
        System.out.println("""
                class Rectangle {
                    int width, height;
                    void setWidth(int w) { width = w; }
                    void setHeight(int h) { height = h; }
                    int area() { return width * height; }
                }

                class Square extends Rectangle {
                    void setWidth(int w) { width = w; height = w; }  // 违反!
                    void setHeight(int h) { width = h; height = h; } // 违反!
                }

                // 期望: rect.setWidth(4); rect.setHeight(5); area = 20
                // 实际: square.setWidth(4); square.setHeight(5); area = 25
                """);

        System.out.println("✅ 遵循 LSP:");
        System.out.println("""
                interface Shape {
                    int area();
                }

                class Rectangle implements Shape { ... }
                class Square implements Shape { ... }
                """);

        // 4. 接口隔离原则
        System.out.println("=".repeat(60));
        System.out.println("【4. 接口隔离原则 (ISP)】");
        System.out.println("""
                定义: 客户端不应该被迫依赖它不使用的接口

                通俗理解: 接口要小而专，不要大而全
                """);

        System.out.println("❌ 违反 ISP:");
        System.out.println("""
                interface Worker {
                    void work();
                    void eat();
                    void sleep();
                }

                class Robot implements Worker {
                    void work() { }
                    void eat() { }   // 机器人不需要!
                    void sleep() { } // 机器人不需要!
                }
                """);

        System.out.println("✅ 遵循 ISP:");
        System.out.println("""
                interface Workable { void work(); }
                interface Eatable { void eat(); }
                interface Sleepable { void sleep(); }

                class Human implements Workable, Eatable, Sleepable { ... }
                class Robot implements Workable { ... }  // 只实现需要的
                """);

        // 5. 依赖倒置原则
        System.out.println("=".repeat(60));
        System.out.println("【5. 依赖倒置原则 (DIP)】");
        System.out.println("""
                定义:
                - 高层模块不应该依赖低层模块，两者都应该依赖抽象
                - 抽象不应该依赖细节，细节应该依赖抽象

                通俗理解: 面向接口编程，而不是面向实现编程
                """);

        System.out.println("❌ 违反 DIP:");
        System.out.println("""
                class MySQLDatabase {
                    void save(String data) { }
                }

                class UserRepository {
                    private MySQLDatabase db = new MySQLDatabase();  // 直接依赖具体实现
                    void save(User user) {
                        db.save(user.toString());
                    }
                }
                // 更换数据库需要修改 UserRepository
                """);

        System.out.println("✅ 遵循 DIP:");
        System.out.println("""
                interface Database {
                    void save(String data);
                }

                class MySQLDatabase implements Database { ... }
                class MongoDatabase implements Database { ... }

                class UserRepository {
                    private Database db;  // 依赖抽象

                    UserRepository(Database db) {  // 依赖注入
                        this.db = db;
                    }
                }
                // 更换数据库只需注入不同实现
                """);

        // 演示依赖倒置
        Database mysql = new MySQLDatabaseImpl();
        Database mongo = new MongoDatabaseImpl();

        UserRepositoryDemo repo1 = new UserRepositoryDemo(mysql);
        UserRepositoryDemo repo2 = new UserRepositoryDemo(mongo);

        repo1.saveUser("Alice");
        repo2.saveUser("Bob");

        // 总结
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【SOLID 总结】");
        System.out.println("""
                ┌─────┬──────────────────┬─────────────────────────────┐
                │ 原则 │ 核心思想         │ 实践要点                    │
                ├─────┼──────────────────┼─────────────────────────────┤
                │ SRP │ 单一职责         │ 一个类只做一件事            │
                │ OCP │ 开闭原则         │ 扩展开放，修改关闭          │
                │ LSP │ 里氏替换         │ 子类可替换父类              │
                │ ISP │ 接口隔离         │ 接口小而专                  │
                │ DIP │ 依赖倒置         │ 依赖抽象，不依赖具体        │
                └─────┴──────────────────┴─────────────────────────────┘
                """);

        System.out.println("💡 SOLID 原则是设计模式的理论基础");
        System.out.println("💡 不必教条式遵守，根据实际情况权衡");
        System.out.println("=".repeat(60));
    }
}

// ==================== 开闭原则演示 ====================

interface PricingStrategy {
    double calculate(double price);
}

class NormalPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(double price) {
        return price;
    }
}

class VipPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(double price) {
        return price * 0.9;
    }
}

// ==================== 依赖倒置演示 ====================

interface Database {
    void save(String data);
}

class MySQLDatabaseImpl implements Database {
    @Override
    public void save(String data) {
        System.out.println("MySQL 保存: " + data);
    }
}

class MongoDatabaseImpl implements Database {
    @Override
    public void save(String data) {
        System.out.println("MongoDB 保存: " + data);
    }
}

class UserRepositoryDemo {
    private final Database db;

    public UserRepositoryDemo(Database db) {
        this.db = db;
    }

    public void saveUser(String username) {
        db.save("User: " + username);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. SRP: 一个类一个职责，变化原因唯一
 * 2. OCP: 通过扩展添加功能，不修改已有代码
 * 3. LSP: 子类必须能替换父类
 * 4. ISP: 接口要小，客户端不依赖不需要的方法
 * 5. DIP: 依赖抽象而非具体实现
 * 
 * 🏃 练习:
 * 1. 找一段违反 SRP 的代码并重构
 * 2. 用开闭原则重构一个 if-else 分支逻辑
 * 3. 分析 Java 标准库中哪些类遵循了 SOLID
 */
