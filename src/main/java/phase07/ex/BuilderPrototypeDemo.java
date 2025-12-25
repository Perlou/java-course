package phase07.ex;

public class BuilderPrototypeDemo {
    public static void main(String[] args) {

        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 4: 建造者模式与原型模式");
        System.out.println("=".repeat(60));
    }
}

// 建造者模式
class Computer {
    private final String cpu;
    private final String ram;

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

// 原型模式

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
