package phase02;

/**
 * Phase 2 - Lesson 3: 封装
 * 
 * 🎯 学习目标:
 * 1. 理解封装的概念和意义
 * 2. 掌握访问修饰符
 * 3. 学会使用 getter/setter
 * 4. 理解不可变对象
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 3: 封装 ===\n");

        // ==================== 1. 什么是封装 ====================
        System.out.println("【1. 什么是封装】");
        System.out.println("封装 (Encapsulation) 是 OOP 的核心特性之一");
        System.out.println("核心思想: 隐藏实现细节，暴露安全接口");
        System.out.println();
        System.out.println("封装的好处:");
        System.out.println("  1. 保护数据，防止非法访问");
        System.out.println("  2. 隐藏复杂性，简化使用");
        System.out.println("  3. 便于修改内部实现");
        System.out.println("  4. 增强代码可维护性");

        // ==================== 2. 访问修饰符 ====================
        System.out.println("\n【2. 访问修饰符】");
        System.out.println("┌───────────────┬────────┬────────┬────────────┬──────────┐");
        System.out.println("│   修饰符      │ 同类   │ 同包   │ 子类(不同包) │ 不同包   │");
        System.out.println("├───────────────┼────────┼────────┼────────────┼──────────┤");
        System.out.println("│ public        │   ✓    │   ✓    │     ✓      │    ✓     │");
        System.out.println("│ protected     │   ✓    │   ✓    │     ✓      │    ✗     │");
        System.out.println("│ default(默认)  │   ✓    │   ✓    │     ✗      │    ✗     │");
        System.out.println("│ private       │   ✓    │   ✗    │     ✗      │    ✗     │");
        System.out.println("└───────────────┴────────┴────────┴────────────┴──────────┘");

        // ==================== 3. 不好的设计 (无封装) ====================
        System.out.println("\n【3. 不好的设计 (无封装)】");

        BadBankAccount badAccount = new BadBankAccount();
        badAccount.balance = -1000; // 可以设置非法值！
        System.out.println("不好的设计允许设置负余额: " + badAccount.balance);

        // ==================== 4. 好的设计 (有封装) ====================
        System.out.println("\n【4. 好的设计 (有封装)】");

        BankAccount account = new BankAccount("张三", 1000);
        System.out.println("初始状态: " + account);

        account.deposit(500);
        System.out.println("存款500后: " + account);

        boolean success = account.withdraw(200);
        System.out.println("取款200" + (success ? "成功" : "失败") + ": " + account);

        success = account.withdraw(5000); // 余额不足
        System.out.println("取款5000" + (success ? "成功" : "失败") + ": " + account);

        // account.balance = -1000; // 编译错误！无法直接访问 private 成员

        // ==================== 5. Getter 和 Setter ====================
        System.out.println("\n【5. Getter 和 Setter】");
        System.out.println("Getter: 获取私有成员变量的值");
        System.out.println("Setter: 设置私有成员变量的值 (可以添加验证逻辑)");

        UserProfile user = new UserProfile();
        user.setName("张三");
        user.setAge(25);
        user.setEmail("zhangsan@example.com");

        System.out.println("姓名: " + user.getName());
        System.out.println("年龄: " + user.getAge());
        System.out.println("邮箱: " + user.getEmail());

        // 尝试设置非法值
        user.setAge(-5); // 会被拒绝
        user.setEmail("invalid-email"); // 会被拒绝
        System.out.println("设置非法值后:");
        System.out.println("年龄: " + user.getAge()); // 仍然是 25
        System.out.println("邮箱: " + user.getEmail()); // 仍然是原来的

        // ==================== 6. 只读属性 ====================
        System.out.println("\n【6. 只读属性】");
        System.out.println("只提供 getter，不提供 setter");

        Circle circle = new Circle(5.0);
        System.out.println("圆的半径: " + circle.getRadius());
        System.out.println("圆的面积: " + circle.getArea());
        System.out.println("圆的周长: " + circle.getCircumference());
        // circle.setRadius(10); // 编译错误！没有 setter

        // ==================== 7. 不可变对象 ====================
        System.out.println("\n【7. 不可变对象 (Immutable Object)】");
        System.out.println("一旦创建，状态就不能改变的对象");
        System.out.println("例如: String, Integer, LocalDate");

        ImmutablePerson person = new ImmutablePerson("李四", 30);
        System.out.println("不可变对象: " + person);
        // person.setName("王五"); // 编译错误！没有 setter

        // ==================== 8. 封装最佳实践 ====================
        System.out.println("\n【8. 封装最佳实践】");
        System.out.println("✓ 成员变量应该是 private");
        System.out.println("✓ 通过 public 方法提供访问");
        System.out.println("✓ 在 setter 中验证数据");
        System.out.println("✓ 考虑使用不可变对象");
        System.out.println("✓ 最小化可访问性原则");
        System.out.println("✓ 返回内部数组/集合时要返回副本");

        System.out.println("\n✅ Phase 2 - Lesson 3 完成！");
    }
}

// ==================== 辅助类定义 ====================

/**
 * 不好的设计 - 没有封装
 */
class BadBankAccount {
    public double balance; // 公开的成员变量，任何人都可以直接修改
}

/**
 * 好的设计 - 有封装的银行账户
 */
class BankAccount {
    // 私有成员变量
    private String owner;
    private double balance;
    private static int accountCounter = 0;
    private final String accountNumber;

    // 构造方法
    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = Math.max(0, initialBalance); // 确保初始余额非负
        this.accountNumber = generateAccountNumber();
    }

    // 私有辅助方法
    private static String generateAccountNumber() {
        return "ACC" + String.format("%06d", ++accountCounter);
    }

    // 公共方法 - 存款
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    // 公共方法 - 取款
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    // Getter 方法
    public double getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return accountNumber + " [" + owner + "] 余额: ¥" + balance;
    }
}

/**
 * 用户资料类 - 演示 Getter/Setter 和数据验证
 */
class UserProfile {
    private String name;
    private int age;
    private String email;

    // Getter 方法
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    // Setter 方法 (带验证)
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        } else {
            System.out.println("警告: 非法年龄值 " + age + ", 已拒绝");
        }
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        } else {
            System.out.println("警告: 非法邮箱格式 " + email + ", 已拒绝");
        }
    }
}

/**
 * Circle 类 - 只读属性示例
 */
class Circle {
    private final double radius; // final 确保不可变

    public Circle(double radius) {
        this.radius = Math.max(0, radius);
    }

    // 只有 getter，没有 setter
    public double getRadius() {
        return radius;
    }

    // 计算属性
    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getCircumference() {
        return 2 * Math.PI * radius;
    }
}

/**
 * 不可变的 Person 类
 */
final class ImmutablePerson {
    private final String name;
    private final int age;

    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 只有 getter
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 如果需要"修改"，返回新对象
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge);
    }

    @Override
    public String toString() {
        return "ImmutablePerson{name='" + name + "', age=" + age + "}";
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 封装: 隐藏实现细节，暴露安全接口
 * 2. 访问修饰符: public > protected > default > private
 * 3. private 成员只能在类内部访问
 * 4. Getter/Setter 是访问私有成员的标准方式
 * 5. Setter 可以添加数据验证逻辑
 * 6. 只读属性: 只提供 getter
 * 7. 不可变对象: final 类 + final 字段 + 只有 getter
 * 
 * 🏃 练习:
 * 1. 创建一个 PasswordValidator 类，封装密码验证逻辑
 * 2. 设计一个 Temperature 类，支持摄氏度和华氏度转换
 * 3. 实现一个不可变的 Money 类
 */
