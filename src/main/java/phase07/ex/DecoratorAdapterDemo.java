package phase07.ex;

public class DecoratorAdapterDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 6: 装饰器模式与适配器模式");
        System.out.println("=".repeat(60));

        // ==================== 装饰器模式 ====================

        System.out.println("\n【装饰器模式 (Decorator)】");
        System.out.println("""
                意图: 动态地给对象添加额外的职责

                特点:
                - 比继承更灵活
                - 可以叠加多个装饰器
                - 遵循开闭原则

                结构:
                Coffee coffee = new SimpleCoffee();          // 原始对象
                coffee = new MilkDecorator(coffee);          // 加牛奶
                coffee = new SugarDecorator(coffee);         // 加糖
                // coffee 现在是 加糖(加牛奶(简单咖啡))
                """);

        // 演示装饰器
        System.out.println("\n咖啡装饰器示例:");

    }

}

// 装饰器模式

interface Coffee {
    double getCost();

    String getDescription();
}

class SimpleCoffee implements Coffee {
    @Override
    public double getCost() {
        return 10.0;
    }

    @Override
    public String getDescription() {
        return "简单咖啡";
    }
}

// 装饰器基类
abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public double getCost() {
        return coffee.getCost();
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
}

// 具体装饰器
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 2.0;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + 牛奶";
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 1.0;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + 糖";
    }
}

// 适配器
class OldCharger {

    public void charge110V() {
        System.out.println("  110V 充电中...");

    }
}

interface USB {
    void chargeUSB();
}

interface USBDevice {
    void charge(USB usb);
}

class ChargerAdapter implements USB {
    private final OldCharger oldCharger;

    public ChargerAdapter(OldCharger oldCharger) {
        this.oldCharger = oldCharger;
    }

    @Override
    public void chargeUSB() {
        System.out.println("  适配器: 将 USB 转换为 110V");
        oldCharger.charge110V();
    }
}

// 使用适配器的设备
class MobilePhone implements USBDevice {
    @Override
    public void charge(USB usb) {
        System.out.println("手机通过 USB 充电:");
        usb.chargeUSB();
    }
}

// ==================== 媒体播放器适配器示例 ====================

interface MediaPlayer {
    void play(String type, String fileName);
}

interface AdvancedMediaPlayer {
    void playVlc(String fileName);

    void playMp4(String fileName);
}

class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("  播放 VLC: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("  播放 MP4: " + fileName);
    }
}

class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(String type) {
        if (type.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        } else if (type.equalsIgnoreCase("mp4")) {
            advancedPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String type, String fileName) {
        if (type.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        } else if (type.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        }
    }
}

class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String type, String fileName) {
        if (type.equalsIgnoreCase("mp3")) {
            System.out.println("  播放 MP3: " + fileName);
        } else if (type.equalsIgnoreCase("vlc") || type.equalsIgnoreCase("mp4")) {
            MediaAdapter adapter = new MediaAdapter(type);
            adapter.play(type, fileName);
        } else {
            System.out.println("  不支持的格式: " + type);
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 装饰器模式:
 * - 动态添加功能
 * - 可叠加多个装饰器
 * - Java IO 是典型应用
 * 
 * 适配器模式:
 * - 接口转换
 * - 解决不兼容问题
 * - 对象适配器更灵活
 * 
 * 🏃 练习:
 * 1. 用装饰器实现日志增强
 * 2. 为老 API 设计适配器
 * 3. 分析 Arrays.asList() 的适配器特性
 */
