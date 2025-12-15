package phase02;

/**
 * Phase 2 - Lesson 10: 内部类
 * 
 * 🎯 学习目标:
 * 1. 理解内部类的概念和分类
 * 2. 掌握成员内部类、静态内部类
 * 3. 了解局部内部类和匿名内部类
 * 4. 学会正确使用内部类
 */
public class InnerClassDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 10: 内部类 ===\n");

        // ==================== 1. 内部类类型 ====================
        System.out.println("【1. 内部类类型】");
        System.out.println("Java 有 4 种内部类:");
        System.out.println("  1. 成员内部类 (Member Inner Class)");
        System.out.println("  2. 静态内部类 (Static Nested Class)");
        System.out.println("  3. 局部内部类 (Local Inner Class)");
        System.out.println("  4. 匿名内部类 (Anonymous Inner Class)");

        // ==================== 2. 成员内部类 ====================
        System.out.println("\n【2. 成员内部类】");
        System.out.println("非静态的内部类，与外部类的实例关联");

        // 创建成员内部类需要先创建外部类实例
        OuterClass outer = new OuterClass("外部数据");
        OuterClass.InnerClass inner = outer.new InnerClass();

        inner.showInfo();

        // ==================== 3. 静态内部类 ====================
        System.out.println("\n【3. 静态内部类】");
        System.out.println("使用 static 修饰的内部类，不依赖外部类实例");

        // 直接创建静态内部类，不需要外部类实例
        Container.Item item1 = new Container.Item("苹果", 10);
        Container.Item item2 = new Container.Item("香蕉", 20);

        System.out.println(item1);
        System.out.println(item2);
        System.out.println("总数量: " + Container.Item.getTotalCount());

        // ==================== 4. 局部内部类 ====================
        System.out.println("\n【4. 局部内部类】");
        System.out.println("定义在方法内部的类，作用域仅限于该方法");

        Calculator3 calc = new Calculator3();
        int result = calc.operate(10, 5, "add");
        System.out.println("10 + 5 = " + result);

        result = calc.operate(10, 5, "multiply");
        System.out.println("10 * 5 = " + result);

        // ==================== 5. 匿名内部类 ====================
        System.out.println("\n【5. 匿名内部类】");
        System.out.println("没有名字的内部类，通常用于创建接口/抽象类的一次性实现");

        // 匿名内部类实现接口
        Greeting greeting = new Greeting() {
            @Override
            public void greet(String name) {
                System.out.println("你好，" + name + "！");
            }
        };
        greeting.greet("张三");

        // 匿名内部类扩展类
        Animal4 anonymousDog = new Animal4("旺财") {
            @Override
            void makeSound() {
                System.out.println(name + " 汪汪!");
            }
        };
        anonymousDog.makeSound();

        // ==================== 6. 匿名内部类 vs Lambda ====================
        System.out.println("\n【6. 匿名内部类 vs Lambda (Java 8+)】");

        // 匿名内部类方式
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名内部类执行");
            }
        };

        // Lambda 方式（更简洁）
        Runnable runnable2 = () -> System.out.println("Lambda 执行");

        runnable1.run();
        runnable2.run();

        System.out.println("\nLambda 适用于函数式接口（只有一个抽象方法）");
        System.out.println("匿名内部类可以实现任何接口/扩展任何类");

        // ==================== 7. 内部类访问外部类 ====================
        System.out.println("\n【7. 内部类访问外部类】");

        OuterClass.InnerClass inner2 = outer.new InnerClass();
        inner2.accessOuter();

        // ==================== 8. 实际应用示例 ====================
        System.out.println("\n【8. 实际应用示例】");

        // 使用内部类实现 Builder 模式
        User user = new User.Builder("张三")
                .age(25)
                .email("zhangsan@example.com")
                .phone("13800138000")
                .build();

        System.out.println(user);

        // 使用静态内部类作为数据结构
        LinkedList list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.print();

        System.out.println("\n✅ Phase 2 - Lesson 10 完成！");
    }
}

// ==================== 成员内部类 ====================

class OuterClass {
    private String data;

    OuterClass(String data) {
        this.data = data;
    }

    // 成员内部类
    class InnerClass {
        void showInfo() {
            // 内部类可以访问外部类的私有成员
            System.out.println("外部类数据: " + data);
            System.out.println("外部类引用: " + OuterClass.this);
        }

        void accessOuter() {
            System.out.println("访问外部类私有成员: " + data);
            outerPrivateMethod();
        }
    }

    private void outerPrivateMethod() {
        System.out.println("调用外部类的私有方法");
    }
}

// ==================== 静态内部类 ====================

class Container {
    private static String containerName = "默认容器";

    // 静态内部类
    static class Item {
        private String name;
        private int quantity;
        private static int totalCount = 0;

        Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            totalCount++;
        }

        static int getTotalCount() {
            return totalCount;
        }

        @Override
        public String toString() {
            // 静态内部类可以访问外部类的静态成员
            return containerName + " - " + name + ": " + quantity + "个";
        }
    }
}

// ==================== 局部内部类 ====================

class Calculator3 {
    int operate(int a, int b, String operation) {
        // 局部内部类
        class Operation {
            int add() {
                return a + b;
            }

            int multiply() {
                return a * b;
            }
        }

        Operation op = new Operation();

        return switch (operation) {
            case "add" -> op.add();
            case "multiply" -> op.multiply();
            default -> 0;
        };
    }
}

// ==================== 匿名内部类使用的接口和类 ====================

interface Greeting {
    void greet(String name);
}

abstract class Animal4 {
    String name;

    Animal4(String name) {
        this.name = name;
    }

    abstract void makeSound();
}

// ==================== Builder 模式示例 ====================

class User {
    private String name;
    private int age;
    private String email;
    private String phone;

    // 私有构造方法，只能通过 Builder 创建
    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
        this.phone = builder.phone;
    }

    // 静态内部类 Builder
    static class Builder {
        private String name;
        private int age = 0;
        private String email = "";
        private String phone = "";

        Builder(String name) {
            this.name = name;
        }

        Builder age(int age) {
            this.age = age;
            return this;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age +
                ", email='" + email + "', phone='" + phone + "'}";
    }
}

// ==================== 链表示例 ====================

class LinkedList {
    private Node head;

    // 静态内部类作为节点
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    void add(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    void print() {
        System.out.print("链表: ");
        Node current = head;
        while (current != null) {
            System.out.print(current.value);
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println();
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 成员内部类: 与外部类实例关联，可访问外部类所有成员
 * 2. 静态内部类: 不依赖外部类实例，只能访问静态成员
 * 3. 局部内部类: 定义在方法中，作用域有限
 * 4. 匿名内部类: 一次性使用，简化接口实现
 * 5. Lambda: 函数式接口的简洁替代
 * 6. 内部类常用于 Builder 模式、迭代器、链表节点等
 * 
 * 🏃 练习:
 * 1. 用静态内部类实现一个简单的二叉树
 * 2. 使用匿名内部类创建线程
 * 3. 实现一个带有 Builder 的 Email 类
 */
