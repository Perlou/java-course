package phase02;

/**
 * Phase 2 - Lesson 12: Record 类 (Java 16+)
 * 
 * 🎯 学习目标:
 * 1. 理解 Record 的概念和用途
 * 2. 掌握 record 关键字的使用
 * 3. 了解 Record 的特性和限制
 * 4. 学会自定义 Record 行为
 */
public class RecordDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 12: Record 类 (Java 16+) ===\n");

        // ==================== 1. 什么是 Record ====================
        System.out.println("【1. 什么是 Record】");
        System.out.println("Record: Java 16 正式引入的不可变数据类");
        System.out.println("自动生成:");
        System.out.println("  - 私有 final 字段");
        System.out.println("  - 公共构造方法");
        System.out.println("  - getter 方法 (与字段同名)");
        System.out.println("  - equals()、hashCode()、toString()");

        // ==================== 2. 基本使用 ====================
        System.out.println("\n【2. 基本使用】");

        // 创建 Record 实例
        Point3D p1 = new Point3D(3, 4, 5);
        Point3D p2 = new Point3D(3, 4, 5);
        Point3D p3 = new Point3D(1, 2, 3);

        // 访问字段
        System.out.println("p1.x() = " + p1.x());
        System.out.println("p1.y() = " + p1.y());
        System.out.println("p1.z() = " + p1.z());

        // toString() 自动生成
        System.out.println("p1.toString() = " + p1);

        // equals() 自动生成（基于所有字段）
        System.out.println("p1.equals(p2) = " + p1.equals(p2));
        System.out.println("p1.equals(p3) = " + p1.equals(p3));

        // hashCode() 自动生成
        System.out.println("p1.hashCode() = " + p1.hashCode());
        System.out.println("p2.hashCode() = " + p2.hashCode());

        // ==================== 3. Record vs 传统类 ====================
        System.out.println("\n【3. Record vs 传统类】");
        System.out.println("传统方式（18行代码）:");
        System.out.println("  class Point { int x, y, z; // + 构造方法 + getter + equals + hashCode + toString }");
        System.out.println();
        System.out.println("Record 方式（1行代码）:");
        System.out.println("  record Point(int x, int y, int z) {}");

        // ==================== 4. 不可变性 ====================
        System.out.println("\n【4. 不可变性】");
        System.out.println("Record 的字段是 final 的，不能被修改");

        // p1.x = 10; // 编译错误！字段是 final 的
        System.out.println("// p1.x = 10;  ← 编译错误！");
        System.out.println("Record 是天然不可变的，非常适合作为 DTO");

        // ==================== 5. 自定义构造方法 ====================
        System.out.println("\n【5. 自定义构造方法】");

        // 使用紧凑构造方法验证
        try {
            Person3 invalidPerson = new Person3("", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("验证失败: " + e.getMessage());
        }

        Person3 validPerson = new Person3("张三", 25);
        System.out.println("有效的人: " + validPerson);

        // ==================== 6. 自定义方法 ====================
        System.out.println("\n【6. 自定义方法】");

        Rectangle3 rect = new Rectangle3(5, 3);
        System.out.println("矩形: " + rect);
        System.out.println("面积: " + rect.area());
        System.out.println("周长: " + rect.perimeter());
        System.out.println("是正方形: " + rect.isSquare());

        Rectangle3 square = new Rectangle3(4, 4);
        System.out.println("正方形: " + square + ", isSquare = " + square.isSquare());

        // ==================== 7. 嵌套 Record ====================
        System.out.println("\n【7. 嵌套 Record】");

        Address address = new Address("北京市", "海淀区", "中关村大街1号", "100080");
        Contact contact = new Contact("李四", "13800138000", "lisi@example.com", address);

        System.out.println("联系人: " + contact.name());
        System.out.println("地址: " + contact.address().city() + contact.address().district());
        System.out.println("完整信息:\n  " + contact);

        // ==================== 8. Record 与模式匹配 ====================
        System.out.println("\n【8. Record 与模式匹配 (Java 21+)】");

        Object[] shapes = {
                new Circle3(5),
                new Rectangle3(4, 6),
                new Circle3(3)
        };

        for (Object shape : shapes) {
            String info = describeShape(shape);
            System.out.println(info);
        }

        // ==================== 9. Record 的限制 ====================
        System.out.println("\n【9. Record 的限制】");
        System.out.println("Record 不能:");
        System.out.println("  ✗ 继承其他类 (隐式继承 Record)");
        System.out.println("  ✗ 被继承 (隐式 final)");
        System.out.println("  ✗ 有可变状态 (字段是 final)");
        System.out.println("  ✗ 声明非静态字段");
        System.out.println();
        System.out.println("Record 可以:");
        System.out.println("  ✓ 实现接口");
        System.out.println("  ✓ 有静态字段和方法");
        System.out.println("  ✓ 有实例方法");
        System.out.println("  ✓ 自定义构造方法");
        System.out.println("  ✓ 重写 equals/hashCode/toString");

        // ==================== 10. 实际应用示例 ====================
        System.out.println("\n【10. 实际应用示例】");

        // API 响应封装
        ApiResponse<User2> successResponse = new ApiResponse<>(
                true,
                new User2("user123", "张三", "zhangsan@example.com"),
                null);

        ApiResponse<User2> errorResponse = new ApiResponse<>(
                false,
                null,
                "用户不存在");

        System.out.println("成功响应: " + successResponse);
        System.out.println("错误响应: " + errorResponse);

        System.out.println("\n✅ Phase 2 - Lesson 12 完成！");
    }

    // 模式匹配方法
    static String describeShape(Object shape) {
        if (shape instanceof Circle3 c) {
            return "圆形，半径: " + c.radius() + "，面积: " + c.area();
        } else if (shape instanceof Rectangle3 r) {
            return "矩形，" + r.width() + "x" + r.height() + "，面积: " + r.area();
        }
        return "未知形状";
    }
}

// ==================== 基本 Record ====================

/**
 * 简单的 3D 点
 * 等价于一个有 x, y, z 字段的不可变类
 */
record Point3D(int x, int y, int z) {
    // 编译器自动生成:
    // - 构造方法 Point3D(int x, int y, int z)
    // - getter: x(), y(), z()
    // - equals(), hashCode(), toString()
}

// ==================== 带验证的 Record ====================

record Person3(String name, int age) {
    // 紧凑构造方法 - 用于验证
    public Person3 {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年龄必须在 0-150 之间");
        }
        // 可以修改参数值
        name = name.trim();
    }
}

// ==================== 带方法的 Record ====================

record Rectangle3(double width, double height) {
    // 自定义方法
    public double area() {
        return width * height;
    }

    public double perimeter() {
        return 2 * (width + height);
    }

    public boolean isSquare() {
        return width == height;
    }

    // 静态工厂方法
    public static Rectangle3 square(double side) {
        return new Rectangle3(side, side);
    }
}

record Circle3(double radius) {
    public double area() {
        return Math.PI * radius * radius;
    }

    public double circumference() {
        return 2 * Math.PI * radius;
    }
}

// ==================== 嵌套 Record ====================

record Address(String city, String district, String street, String zipCode) {
}

record Contact(String name, String phone, String email, Address address) {
}

// ==================== 实现接口的 Record ====================

interface Measurable {
    double measure();
}

record Line(Point3D start, Point3D end) implements Measurable {
    @Override
    public double measure() {
        int dx = end.x() - start.x();
        int dy = end.y() - start.y();
        int dz = end.z() - start.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}

// ==================== 泛型 Record ====================

record Pair<T, U>(T first, U second) {
    public Pair<U, T> swap() {
        return new Pair<>(second, first);
    }
}

record ApiResponse<T>(boolean success, T data, String errorMessage) {
    public boolean isSuccess() {
        return success;
    }

    public boolean hasError() {
        return !success && errorMessage != null;
    }
}

record User2(String id, String name, String email) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. record 是 Java 16+ 的不可变数据类
 * 2. 自动生成构造方法、getter、equals、hashCode、toString
 * 3. 字段隐式 final，Record 隐式 final
 * 4. 紧凑构造方法用于验证和规范化
 * 5. 可以添加自定义方法和静态方法
 * 6. 可以实现接口，但不能继承类
 * 7. 适合 DTO、值对象、不可变数据结构
 * 
 * 🏃 练习:
 * 1. 创建一个 Money record，支持货币类型和金额
 * 2. 使用 Record 实现一个简单的事件系统
 * 3. 创建 Range record，表示数值范围
 */
