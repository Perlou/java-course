package phase07;

/**
 * Phase 7 - Lesson 4: 建造者模式与原型模式
 * 
 * 🎯 学习目标:
 * 1. 理解建造者模式的使用场景
 * 2. 掌握链式调用风格
 * 3. 了解原型模式与克隆
 */
public class BuilderPrototypeDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 4: 建造者模式与原型模式");
        System.out.println("=".repeat(60));

        // ==================== 建造者模式 ====================

        System.out.println("\n【建造者模式 (Builder)】");
        System.out.println("""
                意图: 分步骤构建复杂对象

                适用场景:
                - 对象有很多可选参数
                - 对象创建过程复杂
                - 需要不可变对象

                优点:
                - 链式调用，代码清晰
                - 参数可选，避免多个构造函数
                - 可以创建不可变对象
                """);

        // 演示建造者模式
        System.out.println("构建 Computer 对象:");

        Computer computer = new Computer.Builder("Intel i7", "16GB")
                .storage("512GB SSD")
                .gpu("RTX 3080")
                .screen("27 inch")
                .build();

        System.out.println(computer);

        // 只设置必选参数
        Computer basicComputer = new Computer.Builder("Intel i5", "8GB")
                .build();
        System.out.println("基础配置: " + basicComputer);

        // Java 中的建造者示例
        System.out.println("\nJava 中的建造者模式:");
        System.out.println("""
                StringBuilder:
                new StringBuilder()
                    .append("Hello")
                    .append(" ")
                    .append("World")
                    .toString();

                Stream API:
                Stream.of(1, 2, 3)
                    .filter(x -> x > 1)
                    .map(x -> x * 2)
                    .collect(Collectors.toList());

                HttpClient (Java 11+):
                HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
                """);

        // ==================== 原型模式 ====================

        System.out.println("=".repeat(60));
        System.out.println("\n【原型模式 (Prototype)】");
        System.out.println("""
                意图: 通过克隆现有对象来创建新对象

                适用场景:
                - 对象创建成本高
                - 需要避免构建复杂的创建过程
                - 系统需要保存对象状态快照

                实现方式:
                - 实现 Cloneable 接口
                - 重写 clone() 方法
                """);

        // 演示原型模式
        System.out.println("克隆 Document 对象:");

        Document original = new Document("设计模式", "这是内容...");
        original.addComment("很好的文档");

        System.out.println("原始对象: " + original);

        // 浅克隆
        Document shallowCopy = original.clone();
        System.out.println("浅克隆: " + shallowCopy);

        // 修改克隆对象
        shallowCopy.setTitle("新标题");
        shallowCopy.addComment("新评论");

        System.out.println("\n修改克隆后:");
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + shallowCopy);
        System.out.println("⚠️ 注意: 浅克隆中的集合是共享的!");

        // 深克隆
        System.out.println("\n深克隆:");
        Document deepOriginal = new Document("深克隆测试", "内容");
        deepOriginal.addComment("评论1");

        Document deepCopy = deepOriginal.deepClone();
        deepCopy.addComment("新评论");

        System.out.println("原始: " + deepOriginal);
        System.out.println("深克隆: " + deepCopy);
        System.out.println("✅ 深克隆的集合是独立的");

        // 浅克隆 vs 深克隆
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【浅克隆 vs 深克隆】");
        System.out.println("""
                浅克隆 (Shallow Clone):
                - 基本类型: 复制值
                - 引用类型: 复制引用 (共享对象)

                深克隆 (Deep Clone):
                - 基本类型: 复制值
                - 引用类型: 递归复制 (独立对象)

                深克隆实现方式:
                1. 手动递归克隆
                2. 序列化/反序列化
                3. 使用第三方库 (如 Apache Commons)
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 建造者模式: 分步构建复杂对象，链式调用");
        System.out.println("💡 原型模式: 通过克隆快速创建对象");
        System.out.println("💡 注意浅克隆和深克隆的区别");
        System.out.println("=".repeat(60));
    }
}

// ==================== 建造者模式实现 ====================

class Computer {
    // 必选参数
    private final String cpu;
    private final String ram;

    // 可选参数
    private final String storage;
    private final String gpu;
    private final String screen;

    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
        this.screen = builder.screen;
    }

    @Override
    public String toString() {
        return String.format("Computer[CPU=%s, RAM=%s, Storage=%s, GPU=%s, Screen=%s]",
                cpu, ram, storage, gpu, screen);
    }

    // 静态内部类 Builder
    public static class Builder {
        // 必选参数
        private final String cpu;
        private final String ram;

        // 可选参数 - 提供默认值
        private String storage = "256GB";
        private String gpu = "集成显卡";
        private String screen = "15 inch";

        // 构造函数接收必选参数
        public Builder(String cpu, String ram) {
            this.cpu = cpu;
            this.ram = ram;
        }

        // 设置可选参数，返回 this 实现链式调用
        public Builder storage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder gpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        public Builder screen(String screen) {
            this.screen = screen;
            return this;
        }

        // 构建最终对象
        public Computer build() {
            return new Computer(this);
        }
    }
}

// ==================== 原型模式实现 ====================

class Document implements Cloneable {
    private String title;
    private String content;
    private java.util.List<String> comments;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
        this.comments = new java.util.ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    // 浅克隆
    @Override
    public Document clone() {
        try {
            return (Document) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // 深克隆
    public Document deepClone() {
        Document copy = new Document(this.title, this.content);
        copy.comments = new java.util.ArrayList<>(this.comments);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("Document[title=%s, comments=%s]", title, comments);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 建造者模式:
 * - 分步骤构建复杂对象
 * - 链式调用 (Fluent API)
 * - 常用于多个可选参数的对象创建
 * 
 * 原型模式:
 * - 通过克隆创建对象
 * - 注意浅克隆与深克隆
 * - 实现 Cloneable 接口
 * 
 * 🏃 练习:
 * 1. 用建造者模式实现 HTTP 请求构建
 * 2. 实现一个支持深克隆的游戏角色类
 * 3. 分析 Lombok @Builder 注解
 */
