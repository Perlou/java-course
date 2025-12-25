package phase07.ex;

public class FactoryDemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 3: 工厂模式");
        System.out.println("=".repeat(60));

    }
}

// 产品接口
interface FactoryProduct {
    void use();
}

// 简单工厂
class FactoryProductA implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用产品A");
    }
}

class FactoryProductB implements FactoryProduct {
    @Override
    public void use() {
        System.out.println("使用产品B");
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

// 工厂方法
